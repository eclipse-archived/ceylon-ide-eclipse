/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import ceylon.collection {
    ArrayList
}

import org.eclipse.ceylon.compiler.typechecker.tree {
    Visitor,
    Tree
}
import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor,
    CeylonContentAssistant
}
import org.eclipse.ceylon.ide.eclipse.code.parse {
    CeylonParseController
}
import org.eclipse.ceylon.ide.eclipse.code.preferences {
    CeylonPreferenceInitializer {
        autoActivationChars
    }
}
import org.eclipse.ceylon.ide.eclipse.core.builder {
    CeylonBuilder
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonPlugin {
        preferences
    }
}
import org.eclipse.ceylon.ide.eclipse.util {
    wrapProgressMonitor
}
import org.eclipse.ceylon.ide.common.completion {
    completionManager
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration,
    Functional,
    Unit,
    Reference
}

import java.lang {
    ObjectArray,
    System,
    Types
}
import java.util.concurrent {
    Executors,
    ExecutorService,
    ScheduledExecutorService,
    TimeUnit
}

import org.eclipse.core.runtime {
    IProgressMonitor,
    NullProgressMonitor,
    IStatus,
    Status,
    OperationCanceledException,
    ProgressMonitorWrapper
}
import org.eclipse.core.runtime.jobs {
    ISchedulingRule
}
import org.eclipse.jface.text {
    ITextViewer,
    BadLocationException,
    IDocument
}
import org.eclipse.jface.text.contentassist {
    IContentAssistProcessor,
    ICompletionProposal,
    IContextInformation
}
import org.eclipse.swt {
    SWT
}
import org.eclipse.swt.custom {
    CaretListener,
    CaretEvent,
    VerifyKeyListener
}
import org.eclipse.swt.events {
    FocusListener,
    FocusEvent,
    MouseListener,
    MouseEvent,
    DisposeListener,
    DisposeEvent,
    VerifyEvent
}
import org.eclipse.swt.graphics {
    Cursor
}
import org.eclipse.swt.widgets {
    Display
}
import org.eclipse.ui.plugin {
    AbstractUIPlugin
}


variable ScheduledExecutorService? _timerExecutor = null;
variable ExecutorService? _backgroundExecutor = null;

shared void setupCompletionExecutors() {
    _timerExecutor = Executors.newSingleThreadScheduledExecutor();
    _backgroundExecutor = Executors.newSingleThreadExecutor();
}

shared void shutdownCompletionExecutors() {
    _timerExecutor?.shutdownNow();
    _backgroundExecutor?.shutdown();
    _timerExecutor = null;
    _backgroundExecutor = null;
}

class CeylonCompletionProcessor(CeylonEditor editor)
        satisfies IContentAssistProcessor & EclipseCompletionProcessor {
    
    variable ParameterContextValidator? validator = null;
    variable Boolean secondLevel = false;
    variable Boolean returnedParamInfo = false;
    variable Integer lastOffsetAcrossSessions = -1;
    variable Integer lastOffset = -1;
    variable Boolean isAutoActivated = false;
    
    value contentAssistant => editor.ceylonSourceViewer.contentAssistant;
   
    Integer typecheckingTimeoutMilli => if (isAutoActivated) 
        then contentAssistant.autoActivationDelay 
        else 4000;
    
    Integer typecheckingTimeoutSec => 
            typecheckingTimeoutMilli / 1000;
     
    value noCompletions = ObjectArray<ICompletionProposal>(0);

    ScheduledExecutorService timerExecutor {
        assert(exists te = _timerExecutor);
        return te;
    }

    ExecutorService backgroundExecutor {
        assert(exists be = _backgroundExecutor);
        return be;
    }
    
    completionProposalAutoActivationCharacters =
            Types.nativeString(preferences.getString(autoActivationChars)).toCharArray();
    
    contextInformationAutoActivationCharacters
            = Types.nativeString(",(;{").toCharArray();

    
    object completionSchedulingRule satisfies ISchedulingRule {
        isConflicting(ISchedulingRule rule) => rule == completionSchedulingRule;
        
        contains(ISchedulingRule rule) => rule == completionSchedulingRule;
    }

    class BackgroundCompletion(ITextViewer viewer, Integer offset, IProgressMonitor monitor, Integer start) 
            satisfies Destroyable {
        
        value sourceViewer = editor.ceylonSourceViewer;
        CeylonContentAssistant contentAssistant = sourceViewer.contentAssistant;
        value textWidget = sourceViewer.textWidget;

        shared variable ICompletionProposal?[] _contentProposals = [];
        shared variable String? incompleteResultsMessage = null;
        shared variable Boolean canceledByTextEditorEvent = false;
        shared variable Boolean shouldStillShowCompletion = false;
        shared variable IStatus? status = null;
        
        value keyEvents = ArrayList<VerifyEvent>();

        void stopJobOnEvent(Boolean stillShowResult = false) {
            canceledByTextEditorEvent = true;
            monitor.canceled = true;
            shouldStillShowCompletion = stillShowResult;
        }
        
        object listener satisfies 
        FocusListener &
                MouseListener &
                DisposeListener &
                CaretListener & 
                VerifyKeyListener {
            
            focusLost(FocusEvent focusEvent) => stopJobOnEvent();
            mouseDoubleClick(MouseEvent mouseEvent) => stopJobOnEvent();
            mouseDown(MouseEvent mouseEvent) => stopJobOnEvent();
            shared actual void widgetDisposed(DisposeEvent disposeEvent) {
                stopJobOnEvent();
            }
            caretMoved(CaretEvent caretEvent) => stopJobOnEvent();
            shared actual void verifyKey(VerifyEvent verifyEvent) {
                stopJobOnEvent(verifyEvent.keyCode == SWT.esc.integer);
                if (isAutoActivated) {
                    keyEvents.add(verifyEvent);
                }
            }
            
            focusGained(FocusEvent? focusEvent) =>  noop();
            mouseUp(MouseEvent? mouseEvent) => noop();
        }
        
        value installs = {
            textWidget.addFocusListener,
            textWidget.addMouseListener,
            textWidget.addDisposeListener,
            textWidget.addCaretListener,
            textWidget.addVerifyKeyListener
        };
        
        value removals = {
            textWidget.removeFocusListener,
            textWidget.removeMouseListener,
            textWidget.removeDisposeListener,
            textWidget.removeCaretListener,
            textWidget.removeVerifyKeyListener
        };
        
        for (install in installs) {
            try {
                install(listener);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        Tree.CompilationUnit? forceTypecheckingWithTimeout(
            IProgressMonitor completionMonitor, 
            CeylonParseController controller) {
            
            object typecheckingMonitor 
                    extends ProgressMonitorWrapper(completionMonitor) {
                variable value typecheckingCanceled = false;
                shared actual Boolean canceled {
                    if (typecheckingCanceled) {
                        // print("        `` System.currentTimeMillis() - start ``ms => Typechecking canceled message received");
                        return true;
                    }
                    return wrappedProgressMonitor.canceled;                    
                }
                assign canceled {
                    typecheckingCanceled = true;
                }
            }
            
            // print("        `` System.currentTimeMillis() - start ``ms => Setup typechecking timeout to `` typecheckingTimeoutMilli ``ms");
//            value timeout = timerExecutor.schedule(JavaRunnable((){
//                try {
//                    // print("        `` System.currentTimeMillis() - start ``ms => Typechecking timeout (`` typecheckingTimeoutMilli ``ms) reached");
//                    typecheckingMonitor.canceled = true;
//                } catch(Throwable t) {}
//            }), typecheckingTimeoutMilli, TimeUnit.milliseconds);
            try {
                return controller.parseAndTypecheck(
                    viewer.document, 
                    typecheckingTimeoutSec, 
                    typecheckingMonitor, 
                    null)?.compilationUnit;
            } finally {
//                timeout.cancel(true);
            }
        }
        
        value completionJobFuture = backgroundExecutor.submit(() {
            try (progress = wrapProgressMonitor(monitor)
                .Progress(-1, "Preparing completions...")) {
                
                value controller = editor.parseController;
                value completionMonitor = progress.newChild(-1);
                if (exists lastPhasedUnit = controller.lastPhasedUnit) {
                    Tree.CompilationUnit? typecheckedRootNode;
                    if (exists inBuild = 
                        controller.ceylonProject?.sourceModelLock?.writeLocked,
                    inBuild) {
                        if (isAutoActivated) {
                            typecheckedRootNode = null;
                        } else {
                            typecheckedRootNode = lastPhasedUnit.compilationUnit;
                            incompleteResultsMessage = "The results might be incomplete or incorrect while a build is running";
                        }
                    } else {
                        // print("    `` System.currentTimeMillis() - start ``ms => Start typechecking");
                        value afterForcedTypechecking = 
                                forceTypecheckingWithTimeout(completionMonitor.wrapped, controller);
                        // print("    `` System.currentTimeMillis() - start ``ms => Finished typechecking");
                        if (exists afterForcedTypechecking) {
                            typecheckedRootNode = afterForcedTypechecking;
                        } else {
                            if (isAutoActivated) {
                                typecheckedRootNode = null;
                            } else {
                                typecheckedRootNode = lastPhasedUnit.compilationUnit;
                                incompleteResultsMessage = "The results might be incomplete or incorrect because the analysis timed out";
                            }
                        }
                    }
                    
                    if (exists typecheckedRootNode) {
                        value ctx = EclipseCompletionContext(controller);
                        
                        value timeout =
                                if (isAutoActivated)
                        then timerExecutor.schedule((){
                            try {
                                completionMonitor.wrapped.canceled = true;
                            } catch(Throwable t) {}
                        }, 1000, TimeUnit.milliseconds)
                        else null;
                        try {
                            // print("`` System.currentTimeMillis() - start ``ms => Start constructing completions");
                            completionManager.getContentProposals {
                                typecheckedRootNode = typecheckedRootNode;
                                ctx = ctx;
                                offset = offset;
                                line = CompletionUtil.getLine(offset, viewer);
                                secondLevel = secondLevel;
                                monitor = completionMonitor;
                                returnedParamInfo = returnedParamInfo;
                            };
                            // print("`` System.currentTimeMillis() - start ``ms => Finished constructing completions");
                        } finally {
                            if (exists timeout) {
                                timeout.cancel(true);
                            }
                        }
                        
                        _contentProposals = ctx.proposals.proposals.sequence();
                    }
                } else {
                    if (! CeylonBuilder.allClasspathContainersInitialized()) {
                        incompleteResultsMessage = "Ceylon model initialization is not finished";
                    } else {
                        incompleteResultsMessage = "The file hasn't been analyzed yet";
                    }
                }
                
                if (_contentProposals.size==1 &&
                    _contentProposals.first is ParameterInfo) {
                    returnedParamInfo = true;
                }
                
                if (monitor.canceled) {
                    status = Status.cancelStatus;
                } else {
                    status = Status.okStatus;
                }
            } catch(OperationCanceledException e) {
                status = Status.cancelStatus;
            } catch(Throwable t) {
                status = Status(Status.warning, CeylonPlugin.pluginId, "An exception occured during the Ceylon completion", t);
            }
        });

        shared actual void destroy(Throwable? error) {
            for (remove in removals) {
                try {
                    remove(listener);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            for (e in keyEvents) {
                Display.current.asyncExec(() {
                    contentAssistant.autoAssistListener?.verifyKey(e);
                });
            }
        }
        
        shared Boolean shouldBeWaitedFor => 
                ! completionJobFuture.done &&
                ! canceledByTextEditorEvent;
    }
    
    
    shared actual ObjectArray<ICompletionProposal>?
    computeCompletionProposals(ITextViewer viewer, Integer offset) {
        
        if (offset != lastOffsetAcrossSessions) {
            returnedParamInfo = false;
            secondLevel = false;
        }
        
        function wrongPlace() {
            try {
                if (lastOffset >= 0,
                    offset > 0,
                    offset != lastOffset,
                    !isIdentifierCharacter(viewer, offset)) {
                    return true;
                }
            } catch (BadLocationException ble) {
                ble.printStackTrace();
                return true;
            }
            return false;
        }
        
        if (wrongPlace()) {
            return noCompletions;
        }
        
        value sourceViewer = editor.ceylonSourceViewer;
        value contentAssistant = sourceViewer.contentAssistant;

        if (offset == lastOffset) {
            if(contentAssistant.areResultIncomplete()) {
                isAutoActivated = false;
                contentAssistant.setStatusMessage(CeylonContentAssistant.secondLevelStatusMessage);
            } else {
                secondLevel = !secondLevel;
            }
        }
        lastOffset = offset;
        lastOffsetAcrossSessions = offset;

        value display = sourceViewer.textWidget.display;

        sourceViewer.textWidget.setCursor(Cursor(display, SWT.cursorWait));

        value bars = editor.editorSite?.actionBars;
        value statusLine = bars?.statusLineManager;
        value pm = statusLine?.progressMonitor;
        if (exists pm) {
            pm.beginTask("Preparing completions ...", IProgressMonitor.unknown);
            pm.worked(1);
        }
        value start = System.currentTimeMillis();
        try(completionJob = BackgroundCompletion(viewer, offset, NullProgressMonitor(), start)) {
            // print("`` System.currentTimeMillis() - start ``ms => Start gathering constructed completions");
            while (completionJob.shouldBeWaitedFor) {
                // print("    `` System.currentTimeMillis() - start ``ms => readAndDispatch during gathering of constructed completions");
                if (!display.readAndDispatch()) {
                    // print("    `` System.currentTimeMillis() - start ``ms => sleep during gathering of constructed completions");
                    display.sleep();
                }
            }
            // print("`` System.currentTimeMillis() - start ``ms => Finished gathering constructed completions");
            
            if (completionJob.canceledByTextEditorEvent) {
                if(completionJob.shouldStillShowCompletion) {
                    contentAssistant.setStatusMessage("Results truncated for rapid completion. "
                        + CeylonContentAssistant.retrieveCompleteResultsStatusMessage);
                    return ObjectArray.with(completionJob._contentProposals);
                } else {
                    contentAssistant.setShowEmptyList(false);
                    return noCompletions;
                }
            }
            
            assert(exists status = completionJob.status);
            if (status == Status.cancelStatus) {
                contentAssistant.setStatusMessage("Results truncated for rapid completion. "
                    + CeylonContentAssistant.retrieveCompleteResultsStatusMessage);
                return ObjectArray.with(completionJob._contentProposals);
            }
            
            if (status.severity == Status.error) {
                contentAssistant.setStatusMessage("The results might be incomplete because an error occured");
                (CeylonPlugin.instance of AbstractUIPlugin).log.log(status);
                return ObjectArray.with(completionJob._contentProposals);
            }
            
            if (exists statusMessage = completionJob.incompleteResultsMessage) {
                contentAssistant.setStatusMessage(statusMessage);
            }
            
            if (status == Status.okStatus) {
                return ObjectArray.with(completionJob._contentProposals);
            } else {
                return noCompletions;
            }
        } finally {
            sourceViewer.textWidget.setCursor(null);
            if (exists pm) {
                pm.done();
            }
        }
    }
    
    shared actual ObjectArray<IContextInformation>
    computeContextInformation(ITextViewer viewer, Integer offset) {
        
        CeylonParseController controller = editor.parseController;
        if (exists phasedUnit = controller.parseAndTypecheck(
                viewer.document, 10, NullProgressMonitor(), null)) {
            
            return ObjectArray<IContextInformation>.with(
                computeParameterContextInformation {
                    offset = offset;
                    rootNode = controller.lastCompilationUnit;
                    viewer = viewer;
                });
        } else {
            return ObjectArray<IContextInformation>(0);
        }
    }
    
    contextInformationValidator
            => validator
                    else (validator = ParameterContextValidator(editor));
    
    errorMessage => "No completions available";
    
    shared actual void sessionStarted(Boolean isAutoActivated) {
        secondLevel = false;
        lastOffset = -1;
        this.isAutoActivated = isAutoActivated;
        contentAssistant.setShowEmptyList(true);
    }
    
    Boolean isIdentifierCharacter(ITextViewer viewer, Integer offset) {
        IDocument doc = viewer.document;
        Character ch = doc.get(offset - 1, 1).first else ' ';
        return ch.letter || ch.digit || ch=='_' || ch=='.';
    }
    
    // see InvocationCompletionProposal.computeParameterContextInformation()
    List<IContextInformation> computeParameterContextInformation(
        Integer offset, Tree.CompilationUnit rootNode, ITextViewer viewer) {
        ArrayList<IContextInformation> infos = ArrayList<IContextInformation>();
        rootNode.visit(object extends Visitor() {
                shared actual void visit(Tree.InvocationExpression that) {
                    if (exists al
                                = that.positionalArgumentList
                                        else that.namedArgumentList) {
                        //TODO: should reuse logic for adjusting tokens
                        //      from CeylonContentProposer!!
                        if (exists start = al.startIndex?.intValue(),
                            exists stop = al.endIndex?.intValue(),
                            offset > start) {
                            String string;
                            try {
                                string =
                                    if (offset > stop)
                                    then viewer.document
                                            .get(stop, offset - stop)
                                            .trimmed
                                    else "";
                            } catch (e) {
                                return;
                            }
                            if (string.empty) {
                                Unit unit = rootNode.unit;
                                Tree.Term primary = that.primary;
                                Declaration? declaration;
                                Reference? target;
                                if (is Tree.MemberOrTypeExpression primary) {
                                    declaration = primary.declaration;
                                    target = primary.target;
                                } else {
                                    declaration = null;
                                    target = null;
                                }
                                if (is Functional declaration) {
                                    value pls = declaration.parameterLists;
                                    if (!pls.empty) {
                                        //Note: This line suppresses the little menu 
                                        //      that gives me a choice of context infos.
                                        //      Delete it to get a choice of all surrounding
                                        //      argument lists.
                                        infos.clear();
                                        infos.add(ParameterContextInformation(// TODO migrate this?
                                                declaration, target, unit,
                                                pls.get(0), start, true,
                                                al is Tree.NamedArgumentList));
                                    }
                                } else if (exists type = primary.typeModel,
                                    unit.isCallableType(type)) {
                                    value argTypes = unit.getCallableArgumentTypes(type);
                                    if (!argTypes.empty) {
                                        infos.clear();
                                        infos.add(ParametersCompletionProposal.ParameterContextInformation(
                                                argTypes, start, unit));
                                    }
                                }
                            }
                        }
                    }
                    super.visit(that);
                }
            }
        );
        return infos;
    }
}
