import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.jface.text.contentassist {
    IContentAssistProcessor,
    ICompletionProposal,
    IContextInformation
}
import com.redhat.ceylon.eclipse.util {
    wrapProgressMonitor
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.code.preferences {
    CeylonPreferenceInitializer
}
import org.eclipse.jface.text {
    ITextViewer,
    BadLocationException,
    IDocument
}
import java.lang {
    ObjectArray
}
import org.eclipse.core.runtime {
    IProgressMonitor,
    NullProgressMonitor
}
import com.redhat.ceylon.eclipse.ui {
    CeylonPlugin
}
import ceylon.interop.java {
    javaString,
    createJavaObjectArray
}
import org.eclipse.ui {
    PlatformUI
}
import org.eclipse.jface.operation {
    IRunnableWithProgress
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Visitor,
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Functional,
    Unit,
    Reference
}
import ceylon.collection {
    ArrayList
}

class CeylonCompletionProcessor(CeylonEditor editor)
        satisfies IContentAssistProcessor 
                & EclipseCompletionProcessor {

    
    variable ParameterContextValidator? validator = null;
    variable Boolean secondLevel = false;
    variable Boolean returnedParamInfo = false;
    variable Integer lastOffsetAcrossSessions = -1;
    variable Integer lastOffset = -1;
    
    value noCompletions = ObjectArray<ICompletionProposal>(0);

    completionProposalAutoActivationCharacters =
            javaString(CeylonPlugin.preferences.getString(
        CeylonPreferenceInitializer.\iAUTO_ACTIVATION_CHARS))
            .toCharArray();
    
    contextInformationAutoActivationCharacters 
            = javaString(",(;{").toCharArray();
    
    shared actual ObjectArray<ICompletionProposal> computeCompletionProposals(
        ITextViewer viewer, Integer offset) {
        if (offset != lastOffsetAcrossSessions) {
            returnedParamInfo = false;
            secondLevel = false;
        }
        try {
            if (lastOffset >= 0, 
                offset > 0, 
                offset != lastOffset, 
                !isIdentifierCharacter(viewer, offset)) {
                return noCompletions;
            }
        } catch (BadLocationException ble) {
            ble.printStackTrace();
            return noCompletions;
        }
        if (offset == lastOffset) {
            secondLevel = !secondLevel;
        }
        lastOffset = offset;
        lastOffsetAcrossSessions = offset;
        
        object runnable satisfies IRunnableWithProgress {
            shared variable ICompletionProposal?[] _contentProposals = [];
            
            shared actual void run(IProgressMonitor monitor) {
                try (progress = wrapProgressMonitor(monitor)
                    .Progress(-1, "Preparing completions...")) {
                    _contentProposals = eclipseCompletionManager.getEclipseContentProposals {
                        editor = editor;
                        controller = editor.parseController;
                        offset = offset;
                        viewer = viewer;
                        secondLevel = secondLevel;
                        returnedParamInfo = returnedParamInfo;
                        monitor = progress.newChild(-1);
                    };
                    if (_contentProposals.size == 1 && 
                        _contentProposals.first 
                    is InvocationCompletionProposal.ParameterInfo) {
                        returnedParamInfo = true;
                    }
                }
            }
        }
        
        try {
            if (secondLevel) {
                runnable.run(NullProgressMonitor());
            } else {
                PlatformUI.workbench
                        .activeWorkbenchWindow.run(
                    true, true, runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createJavaObjectArray(runnable._contentProposals);
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
            }
            else {
                return ObjectArray<IContextInformation>(0);
            }
        }
        
        contextInformationValidator 
                => validator 
        else (validator = ParameterContextValidator(editor));
        
        errorMessage => "No completions available";

        
        shared actual void sessionStarted() {
            secondLevel = false;
            lastOffset = -1;
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
                            }
                            catch (e) {
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
                                }
                                else {
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
                                        infos.add(InvocationCompletionProposal.ParameterContextInformation( // TODO migrate this?
                                            declaration, target, unit,
                                            pls.get(0), start, true, 
                                            al is Tree.NamedArgumentList));
                                    }
                                }
                                else if (exists type = primary.typeModel, 
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