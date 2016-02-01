import ceylon.collection {
    MutableList,
    ArrayList
}
import ceylon.interop.java {
    javaString,
    createJavaObjectArray
}

import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree,
    Visitor
}
import com.redhat.ceylon.eclipse.code.correct {
    TypeProposal
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.code.preferences {
    CeylonPreferenceInitializer
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources,
    CeylonPlugin
}
import com.redhat.ceylon.eclipse.util {
    EclipseProgressMonitor,
    eclipseIndents
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager,
    isModuleDescriptor
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.redhat.ceylon.model.typechecker.model {
    Type,
    Declaration,
    Reference,
    Scope,
    Unit,
    Functional,
    Package
}

import java.lang {
    ObjectArray,
    CharArray
}
import java.util {
    JList=List
}
import java.util.regex {
    Pattern
}

import org.eclipse.core.resources {
    IProject
}
import org.eclipse.core.runtime {
    NullProgressMonitor,
    IProgressMonitor
}
import org.eclipse.jface.operation {
    IRunnableWithProgress
}
import org.eclipse.jface.text {
    IDocument,
    ITextViewer,
    BadLocationException
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    IContentAssistProcessor,
    IContextInformation,
    IContextInformationValidator
}
import org.eclipse.swt.graphics {
    Point
}
import org.eclipse.ui {
    PlatformUI
}

EclipseCompletionManager dummyInstance = EclipseCompletionManager(CeylonEditor());

shared class EclipseCompletionManager(CeylonEditor editor) 
        extends IdeCompletionManager<CeylonParseController,ICompletionProposal,IDocument>()
        satisfies IContentAssistProcessor & EclipseCompletionProcessor {
    
    variable ParameterContextValidator? validator = null;
    variable Boolean secondLevel = false;
    variable Boolean returnedParamInfo = false;
    variable Integer lastOffsetAcrossSessions = -1;
    variable Integer lastOffset = -1;
    
    value noCompletions = createJavaObjectArray<ICompletionProposal>({});
    
    shared actual CharArray completionProposalAutoActivationCharacters =
            javaString(CeylonPlugin.preferences.getString(CeylonPreferenceInitializer.\iAUTO_ACTIVATION_CHARS)).toCharArray();
    
    shared actual CharArray contextInformationAutoActivationCharacters = javaString(",(;{").toCharArray();
    
    shared actual ObjectArray<ICompletionProposal> computeCompletionProposals(ITextViewer viewer, Integer offset) {
        if (offset != lastOffsetAcrossSessions) {
            returnedParamInfo = false;
            secondLevel = false;
        }
        try {
            if (lastOffset >= 0, offset > 0, offset != lastOffset, !isIdentifierCharacter(viewer, offset)) {
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
        class Runnable() satisfies IRunnableWithProgress {
            shared variable ICompletionProposal?[] _contentProposals = [];
            
            shared actual void run(IProgressMonitor monitor) {
                monitor.beginTask("Preparing completions...", IProgressMonitor.\iUNKNOWN);
                _contentProposals = getEclipseContentProposals(editor.parseController, offset, viewer, secondLevel, returnedParamInfo, monitor);
                if (_contentProposals.size == 1 && 
                    _contentProposals.first is InvocationCompletionProposal.ParameterInfo) {
                    returnedParamInfo = true;
                }
                monitor.done();
            }
        }
        
        Runnable runnable = Runnable();
        try {
            if (secondLevel) {
                runnable.run(NullProgressMonitor());
            } else {
                PlatformUI.workbench.activeWorkbenchWindow.run(true, true, runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createJavaObjectArray(runnable._contentProposals);
    }
    
    shared actual ObjectArray<IContextInformation> computeContextInformation(ITextViewer viewer, Integer offset) {
        CeylonParseController controller = editor.parseController;
        if (exists phasedUnit = controller.parseAndTypecheck(viewer.document, 10, NullProgressMonitor(), null)) {
            return createJavaObjectArray<IContextInformation>(computeParameterContextInformation(offset, controller.lastCompilationUnit, viewer));
        }
        else {
            return ObjectArray(0);
        }
    }
    
    shared actual IContextInformationValidator contextInformationValidator 
            => validator else (validator = ParameterContextValidator(editor));
    
    shared actual String errorMessage => "No completions available";
    
    shared actual Indents<IDocument> indents => eclipseIndents;
        
    shared actual ICompletionProposal newParametersCompletionProposal(Integer offset,
        String prefix, String desc, String text, JList<Type> argTypes, Node node, Unit unit) 
                => ParametersCompletionProposal(offset,
                    desc.string, text.string,
                    argTypes, node.scope, unit);
        
    shared actual String getDocumentSubstring(IDocument doc, Integer start, Integer length) => doc.get(start, length);
    
    shared actual ICompletionProposal newInvocationCompletion(Integer offset, String prefix,
        String desc, String text, Declaration dec, Reference? pr, Scope scope, CeylonParseController cpc,
        Boolean includeDefaulted, Boolean positionalInvocation, Boolean namedInvocation, 
        Boolean inheritance, Boolean qualified, Declaration? qualifyingDec) 
            => EclipseInvocationCompletionProposal(offset, prefix, desc, text, dec, pr, scope, cpc,
                includeDefaulted, positionalInvocation, namedInvocation, inheritance, qualified, qualifyingDec, this);
    
    shared actual ICompletionProposal newRefinementCompletionProposal(Integer offset, String prefix, Reference? pr,
        String desc, String text, CeylonParseController cmp, Declaration dec,
        Scope scope, Boolean fullType, Boolean explicitReturnType) 
            => RefinementCompletionProposal(offset, prefix, pr, desc, text, cmp, dec,
                scope, fullType, explicitReturnType);
    
    shared actual ICompletionProposal newMemberNameCompletionProposal(Integer offset, String prefix, String name, String unquotedName) 
            => CompletionProposal(offset, prefix, CeylonResources.\iLOCAL_NAME, unquotedName, name);
    
    shared actual ICompletionProposal newKeywordCompletionProposal(Integer offset, String prefix, String keyword, String text) 
            => KeywordCompletionProposal(offset, prefix, keyword, text);
    
    shared actual ICompletionProposal newAnonFunctionProposal(Integer _offset, Type? requiredType,
        Unit unit, String _text, String header, Boolean isVoid, Integer selectionStart, Integer selectionLength) {
        
        value largeCorrectionImage = CeylonLabelProvider.getDecoratedImage(CeylonResources.\iCEYLON_CORRECTION, 0, false);
        return object extends CompletionProposal(_offset, "", largeCorrectionImage, _text, _text) {
            shared actual Point getSelection(IDocument document) => Point(selectionStart, selectionLength);
        };
    }

    shared actual ICompletionProposal newBasicCompletionProposal(Integer offset, String prefix,
        String text, String escapedText, Declaration decl, CeylonParseController cpc)
            => BasicCompletionProposal(offset, prefix, text, escapedText, decl, cpc);
    
    shared actual List<Pattern> proposalFilters {
        value filters = ArrayList<Pattern>();
        value preferences = CeylonPlugin.preferences;
        parseFilters(filters, preferences.getString(CeylonPreferenceInitializer.\iFILTERS));
        if (preferences.getBoolean(CeylonPreferenceInitializer.\iENABLE_COMPLETION_FILTERS)) {
            parseFilters(filters, preferences.getString(CeylonPreferenceInitializer.\iCOMPLETION_FILTERS));
        }
        return filters;
    }
    
    shared actual void sessionStarted() {
        secondLevel = false;
        lastOffset = -1;
    }
    
    shared actual ICompletionProposal newPackageDescriptorProposal(Integer offset, String prefix, String desc, String text) 
            => PackageCompletions.PackageDescriptorProposal(offset, prefix, desc, text);
    
    shared actual ICompletionProposal newCurrentPackageProposal(Integer offset, String prefix, String packageName,
        CeylonParseController controller) 
            => CompletionProposal(offset, prefix, 
                if (isModuleDescriptor(controller.lastCompilationUnit)) 
                then CeylonResources.\iMODULE 
                else CeylonResources.\iPACKAGE,
                packageName, packageName);

    shared actual ICompletionProposal newImportedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CeylonParseController controller,
        Package candidate) 
            => EclipseImportedModulePackageProposal(offset, prefix, 
                memberPackageSubname, withBody, fullPackageName, controller, candidate);
    
    shared actual ICompletionProposal newQueriedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CeylonParseController controller,
        ModuleVersionDetails version, Unit unit, ModuleSearchResult.ModuleDetails md) 
            => PackageCompletions.QueriedModulePackageProposal(offset, prefix,
                memberPackageSubname, withBody, fullPackageName, controller, version, unit, md);
    
    shared actual ICompletionProposal newModuleProposal(Integer offset, String prefix, Integer len, 
        String versioned, ModuleSearchResult.ModuleDetails mod, Boolean withBody,
        ModuleVersionDetails version, String name, Node node, CeylonParseController cpc)
            => ModuleCompletions.ModuleProposal(offset, prefix, len, versioned, mod, withBody, version, name, node);
    
    shared actual ICompletionProposal newModuleDescriptorProposal(Integer offset, String prefix, String desc,
        String text, Integer selectionStart, Integer selectionEnd)
            => ModuleCompletions.ModuleDescriptorProposal(offset, prefix, desc, text, selectionStart, selectionEnd);

    shared actual ICompletionProposal newJDKModuleProposal(Integer offset, String prefix, Integer len, 
        String versioned, String name)
            => ModuleCompletions.JDKModuleProposal(offset, prefix, len, versioned, name);

    shared actual ICompletionProposal newParameterInfo(Integer offset, Declaration dec, 
        Reference producedReference, Scope scope, CeylonParseController cpc, Boolean namedInvocation)
            => InvocationCompletionProposal.ParameterInfo(offset, dec, producedReference, scope, cpc, namedInvocation);
            
    shared actual ICompletionProposal newFunctionCompletionProposal(Integer offset, String prefix,
           String desc, String text, Declaration dec, Unit unit, CeylonParseController controller) 
           => EclipseFunctionCompletionProposal(offset, prefix, desc, text, dec, controller.lastCompilationUnit);

    shared actual ICompletionProposal newControlStructureCompletionProposal(Integer offset, String prefix,
        String desc, String text, Declaration dec, CeylonParseController cpc, Node? node)
             => EclipseControlStructureProposal(offset, prefix, desc, text, dec, cpc, node);

    shared actual ICompletionProposal newTypeProposal(Integer offset, Type? type, String text, String desc, Tree.CompilationUnit rootNode) 
            => TypeProposal(offset, type, text, desc, rootNode);

    Boolean isIdentifierCharacter(ITextViewer viewer, Integer offset) {
        IDocument doc = viewer.document;
        Character ch = doc.get(offset - 1, 1).first else ' ';
        return ch.letter || ch.digit || ch=='_' || ch=='.';
    }
    
    // see InvocationCompletionProposal.computeParameterContextInformation()
    List<IContextInformation> computeParameterContextInformation(Integer offset, Tree.CompilationUnit rootNode, ITextViewer viewer) {
        ArrayList<IContextInformation> infos = ArrayList<IContextInformation>();
        rootNode.visit(object extends Visitor() {
                shared actual void visit(Tree.InvocationExpression that) {
                    if (exists al = that.positionalArgumentList else that.namedArgumentList) {
                        //TODO: should reuse logic for adjusting tokens
                        //      from CeylonContentProposer!!
                        if (exists start = al.startIndex?.intValue(), 
                            exists stop = al.endIndex?.intValue(), 
                            offset > start) {
                            String string;
                            try {
                                string = 
                                        if (offset > stop) 
                                        then viewer.document.get(stop, offset - stop).trimmed 
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

    
    // see CeylonCompletionProcessor.parseFilters()
    void parseFilters(MutableList<Pattern> filters, String filtersString) {
        if (!filtersString.trimmed.empty) {
            value regexes = filtersString.replace("\\(\\w+\\)", "").replace(".", "\\.").replace("*", ".*").split(','.equals);
            for (String regex in regexes) {
                value trimmedRegex = regex.trimmed;
                if (!trimmedRegex.empty) {
                    filters.add(Pattern.compile(trimmedRegex));
                }
            }
        }
    }
    
    ICompletionProposal[] getEclipseContentProposals(CeylonParseController? controller, Integer offset,
        ITextViewer? viewer, Boolean secondLevel, Boolean returnedParamInfo, IProgressMonitor monitor) {
        if (exists controller, exists viewer, 
            exists rn = controller.lastCompilationUnit, exists t = controller.tokens, 
            exists pu = controller.parseAndTypecheck(viewer.document, 10, monitor, null)) {
            
            editor.annotationCreator.updateAnnotations();
            
            value line = CompletionUtil.getLine(offset, viewer);
            return getContentProposals(pu.compilationUnit, controller, offset, line,
                secondLevel, EclipseProgressMonitor(monitor), returnedParamInfo,
                EclipseProgressMonitor(monitor));
        }
        else {
            return [];
        }
    }
}
