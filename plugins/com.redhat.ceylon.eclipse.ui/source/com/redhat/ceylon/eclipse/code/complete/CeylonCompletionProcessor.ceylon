import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaString,
    createJavaObjectArray
}

import com.redhat.ceylon.compiler.typechecker.tree {
    Visitor,
    Tree
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.code.preferences {
    CeylonPreferenceInitializer {
        autoActivationChars
    }
}
import com.redhat.ceylon.eclipse.ui {
    CeylonPlugin {
        preferences
    }
}
import com.redhat.ceylon.eclipse.util {
    wrapProgressMonitor
}
import com.redhat.ceylon.ide.common.completion {
    completionManager
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Functional,
    Unit,
    Reference
}

import java.lang {
    ObjectArray
}

import org.eclipse.core.runtime {
    IProgressMonitor,
    NullProgressMonitor,
    IStatus,
    Status,
    OperationCanceledException
}
import org.eclipse.core.runtime.jobs {
    Job,
    ISchedulingRule
}
import org.eclipse.jface.text {
    ITextViewer,
    BadLocationException,
    IDocument,
    IDocumentListener,
    DocumentEvent
}
import org.eclipse.jface.text.contentassist {
    IContentAssistProcessor,
    ICompletionProposal,
    IContextInformation
}
import org.eclipse.swt {
    SWT
}
import org.eclipse.swt.graphics {
    Cursor
}
import org.eclipse.swt.widgets {
    Display
}

class CeylonCompletionProcessor(CeylonEditor editor)
        satisfies IContentAssistProcessor & EclipseCompletionProcessor {
    
    variable ParameterContextValidator? validator = null;
    variable Boolean secondLevel = false;
    variable Boolean returnedParamInfo = false;
    variable Integer lastOffsetAcrossSessions = -1;
    variable Integer lastOffset = -1;
    variable Boolean isAutoActivated = false;
    
    value noCompletions = ObjectArray<ICompletionProposal>(0);
    
    completionProposalAutoActivationCharacters =
        javaString(preferences.getString(autoActivationChars)).toCharArray();
    
    contextInformationAutoActivationCharacters
            = javaString(",(;{").toCharArray();

    object completionSchedulingRule satisfies ISchedulingRule {
        isConflicting(ISchedulingRule rule) => rule == this;
        
        contains(ISchedulingRule rule) => rule == this;
    }
    
    class CompletionJob(ITextViewer viewer, Integer offset) extends Job("Ceylon Editor Completion") {
        priority = interactive;
        system = true;
        rule = completionSchedulingRule;
        
        shared variable ICompletionProposal?[] _contentProposals = [];
        shared variable String? incompleteResultsMessage = null;
        shared variable Boolean canceledByTextInput = false;
        
        shared actual IStatus run(IProgressMonitor monitor) {
            
            object documentListener satisfies IDocumentListener {
                shared actual void documentAboutToBeChanged(DocumentEvent? documentEvent) {
                    canceledByTextInput = true;
                    outer.cancel();
                }
                documentChanged(DocumentEvent? documentEvent) => noop();
            }
            
            value sourceViewer = editor.ceylonSourceViewer;
            value document = sourceViewer.document;
            document.addDocumentListener(documentListener);
            
            try (progress = wrapProgressMonitor(monitor)
                .Progress(-1, "Preparing completions...")) {
                
                value controller = editor.parseController;
                value completionMonitor = progress.newChild(-1);
                if (exists lastPhasedUnit = controller.lastPhasedUnit) {
                    Tree.CompilationUnit? typecheckedRootNode;
                    if (exists inBuild = 
                        controller.ceylonProject?.sourceModelLock?.writeLocked,
                    inBuild) {
                        typecheckedRootNode = lastPhasedUnit.compilationUnit;
                        incompleteResultsMessage = "The results might be incomplete while a build is running";
                    } else {
                        variable Tree.CompilationUnit? afterForcedTypechecking = null;
                        try {
                            afterForcedTypechecking = controller.parseAndTypecheck(
                                viewer.document, 
                                if (isAutoActivated) then 0 else 4, 
                                completionMonitor.wrapped, 
                                null)?.compilationUnit;
                        } catch(OperationCanceledException e) {
                        }
                        if (exists aft=afterForcedTypechecking) {
                            typecheckedRootNode = aft;
                        } else {
                            typecheckedRootNode = lastPhasedUnit.compilationUnit;
                            incompleteResultsMessage = "The results were truncated for faster completion";
                        }
                    }
                    
                    if (exists typecheckedRootNode) {
                        value ctx = EclipseCompletionContext(controller);
                        
                        completionManager.getContentProposals {
                            typecheckedRootNode = typecheckedRootNode;
                            ctx = ctx;
                            offset = offset;
                            line = CompletionUtil.getLine(offset, viewer);
                            secondLevel = secondLevel;
                            monitor = completionMonitor;
                            returnedParamInfo = returnedParamInfo;
                        };
                        
                        _contentProposals = ctx.proposals.proposals.sequence();
                    } else {
                        incompleteResultsMessage = "The file hasn't been analyzed yet";
                    }
                } 
                
                if (_contentProposals.size==1 &&
                    _contentProposals.first is ParameterInfo) {
                    returnedParamInfo = true;
                }
                
                if (monitor.canceled) {
                    return Status.cancelStatus;
                }
                return Status.okStatus;
            } catch(OperationCanceledException e) {
                return Status.cancelStatus;
            } finally {
                document.removeDocumentListener(documentListener);
            }
        }
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
        
        if (offset == lastOffset) {
            secondLevel = !secondLevel;
        }
        lastOffset = offset;
        lastOffsetAcrossSessions = offset;

        value sourceViewer = editor.ceylonSourceViewer;
        value contentAssistant = sourceViewer.contentAssistant;
        value display = Display.current;
        CompletionJob completionJob = CompletionJob(viewer, offset);

        sourceViewer.textWidget.setCursor(Cursor(display, SWT.cursorWait));
        try {
            completionJob.schedule();
            while (completionJob.state != completionJob.none) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } finally {
            sourceViewer.textWidget.setCursor(null);
        }
        
        if (completionJob.result == Status.cancelStatus) {
            if (completionJob.canceledByTextInput) {
                throw OperationCanceledException();
            }
            contentAssistant.setStatusMessage("The results might be incomplete because search has been interrupted");
            return createJavaObjectArray(completionJob._contentProposals);
        }
        
        if (exists statusMessage = completionJob.incompleteResultsMessage) {
            contentAssistant.setStatusMessage(statusMessage);
        }
        
        if (completionJob.result == Status.okStatus) {
            return createJavaObjectArray(completionJob._contentProposals);
        } else {
            return noCompletions;
        }
    }
    
    shared actual ObjectArray<IContextInformation>
    computeContextInformation(ITextViewer viewer, Integer offset) {
        
        CeylonParseController controller = editor.parseController;
        if (exists phasedUnit = controller.parseAndTypecheck(
                viewer.document, 10, NullProgressMonitor(), null)) {
            
            return createJavaObjectArray<IContextInformation>(
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
