import ceylon.collection {
    MutableList,
    ArrayList
}

import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
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
    EclipseProgressMonitorChild
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager,
    isModuleDescriptor
}
import com.redhat.ceylon.model.typechecker.model {
    Type,
    Declaration,
    Reference,
    Scope,
    Unit,
    Package
}

import java.util {
    JList=List
}
import java.util.regex {
    Pattern
}

import org.eclipse.jface.text {
    IDocument,
    ITextViewer
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.swt.graphics {
    Point
}

shared object eclipseCompletionManager 
        extends IdeCompletionManager<CeylonParseController,ICompletionProposal,IDocument>() {
    
    newParametersCompletionProposal(
            Integer offset, String prefix, String desc, String text, 
            JList<Type> argTypes, Node node, Unit unit) 
                => ParametersCompletionProposal(offset,
                    desc.string, text.string,
                    argTypes, node.scope, unit);
        
    getDocumentSubstring(IDocument doc, Integer start, Integer length) 
                => doc.get(start, length);
    
    newInvocationCompletion(Integer offset, String prefix,
        String desc, String text, Declaration dec, 
        Reference? pr, Scope scope, CeylonParseController cpc,
        Boolean includeDefaulted, Boolean positionalInvocation, 
        Boolean namedInvocation, Boolean inheritance, 
        Boolean qualified, Declaration? qualifyingDec) 
            => EclipseInvocationCompletionProposal {
                _offset = offset;
                prefix = prefix;
                description = desc;
                text = text;
                dec = dec;
                producedReference = pr;
                scope = scope;
                cpc = cpc;
                includeDefaulted = includeDefaulted;
                positionalInvocation = positionalInvocation;
                namedInvocation = namedInvocation;
                inheritance = inheritance;
                qualified = qualified;
                qualifyingValue = qualifyingDec;
            };
    
    // TODO replace with EclipseRefinementCompletionProposal (and finish rewriting it)
    newRefinementCompletionProposal(Integer offset, 
        String prefix, Reference? pr, String desc, String text, 
        CeylonParseController cmp, Declaration dec, Scope scope, 
        Boolean fullType, Boolean explicitReturnType) 
            => RefinementCompletionProposal(offset, prefix, 
                pr, desc, text, cmp, dec, scope, fullType, 
                explicitReturnType);
    
    newMemberNameCompletionProposal(Integer offset, 
        String prefix, String name, String unquotedName) 
            => CompletionProposal(offset, prefix, 
                CeylonResources.\iLOCAL_NAME, 
                unquotedName, name);
    
    newKeywordCompletionProposal(Integer offset, 
        String prefix, String keyword, String text) 
            => KeywordCompletionProposal(offset, prefix, keyword, text);
    
    shared actual ICompletionProposal newAnonFunctionProposal(
        Integer _offset, Type? requiredType, Unit unit, 
        String _text, String header, Boolean isVoid, 
        Integer selectionStart, Integer selectionLength) {
        
        value largeCorrectionImage 
                = CeylonLabelProvider.getDecoratedImage(
                    CeylonResources.\iCEYLON_CORRECTION, 
                    0, false);
        return object 
                extends CompletionProposal(_offset, "", 
                        largeCorrectionImage, _text, _text) {
            getSelection(IDocument document) 
                    => Point(selectionStart, selectionLength);
        };
    }

    newBasicCompletionProposal(Integer offset, String prefix,
        String text, String escapedText, Declaration decl, 
        CeylonParseController cpc)
            => BasicCompletionProposal(offset, prefix, text, 
                escapedText, decl, cpc);
    
    shared actual List<Pattern> proposalFilters {
        value filters = ArrayList<Pattern>();
        value preferences = CeylonPlugin.preferences;
        parseFilters(filters, preferences.getString(CeylonPreferenceInitializer.\iFILTERS));
        if (preferences.getBoolean(CeylonPreferenceInitializer.\iENABLE_COMPLETION_FILTERS)) {
            parseFilters(filters, preferences.getString(CeylonPreferenceInitializer.\iCOMPLETION_FILTERS));
        }
        return filters;
    }
    
    newPackageDescriptorProposal(Integer offset, String prefix, 
        String desc, String text) 
            => PackageCompletions.PackageDescriptorProposal(
                offset, prefix, desc, text);
    
    newCurrentPackageProposal(Integer offset, String prefix, 
        String packageName, CeylonParseController controller) 
            => CompletionProposal(offset, prefix, 
                if (isModuleDescriptor(controller.lastCompilationUnit)) 
                then CeylonResources.\iMODULE 
                else CeylonResources.\iPACKAGE,
                packageName, packageName);

    newImportedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CeylonParseController controller,
        Package candidate) 
            => EclipseImportedModulePackageProposal {
                offset = offset;
                prefix = prefix;
                memberPackageSubname = memberPackageSubname;
                withBody = withBody;
                fullPackageName = fullPackageName;
                controller = controller;
                candidate = candidate;
            };
    
    newQueriedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CeylonParseController controller,
        ModuleVersionDetails version, Unit unit, ModuleSearchResult.ModuleDetails md) 
            => PackageCompletions.QueriedModulePackageProposal(offset, prefix,
                memberPackageSubname, withBody, fullPackageName, 
                controller, version, unit, md);
    
    newModuleProposal(Integer offset, String prefix, Integer len, 
        String versioned, ModuleSearchResult.ModuleDetails mod, Boolean withBody,
        ModuleVersionDetails version, String name, Node node, CeylonParseController cpc)
            => ModuleCompletions.ModuleProposal(
                offset, prefix, len, versioned, mod, 
                withBody, version, name, node);
    
    newModuleDescriptorProposal(Integer offset, String prefix, String desc,
        String text, Integer selectionStart, Integer selectionEnd)
            => ModuleCompletions.ModuleDescriptorProposal(
                offset, prefix, desc, text, 
                selectionStart, selectionEnd);

    newJDKModuleProposal(Integer offset, String prefix, 
        Integer len, String versioned, String name)
            => ModuleCompletions.JDKModuleProposal(offset, 
                prefix, len, versioned, name);

    newParameterInfo(Integer offset, Declaration dec, 
        Reference producedReference, Scope scope, 
        CeylonParseController cpc, Boolean namedInvocation)
            => InvocationCompletionProposal.ParameterInfo(
                offset, dec, producedReference, scope, cpc, namedInvocation);
            
    newFunctionCompletionProposal(Integer offset, String prefix,
           String desc, String text, Declaration dec, Unit unit, 
           CeylonParseController controller) 
           => EclipseFunctionCompletionProposal {
               offset = offset;
               prefix = prefix;
               desc = desc;
               text = text;
               declaration = dec;
               rootNode = controller.lastCompilationUnit;
           };

    newControlStructureCompletionProposal(Integer offset, String prefix,
        String desc, String text, Declaration dec, 
        CeylonParseController cpc, Node? node)
             => EclipseControlStructureProposal {
                 offset = offset;
                 prefix = prefix;
                 desc = desc;
                 text = text;
                 declaration = dec;
                 cpc = cpc;
                 node = node;
             };

    newTypeProposal(Integer offset, Type? type, String text, 
        String desc, Tree.CompilationUnit rootNode) 
            => TypeProposal(offset, type, text, desc, rootNode);


    
    // see CeylonCompletionProcessor.parseFilters()
    void parseFilters(MutableList<Pattern> filters, String filtersString) {
        if (!filtersString.trimmed.empty) {
            value regexes 
                    = filtersString
                        .replace("\\(\\w+\\)", "")
                        .replace(".", "\\.")
                        .replace("*", ".*")
                        .split(','.equals);
            for (String regex in regexes) {
                value trimmedRegex = regex.trimmed;
                if (!trimmedRegex.empty) {
                    filters.add(Pattern.compile(trimmedRegex));
                }
            }
        }
    }
    
    shared ICompletionProposal[] getEclipseContentProposals(
        CeylonEditor editor,
        CeylonParseController? controller, Integer offset,
        ITextViewer? viewer, Boolean secondLevel, 
        Boolean returnedParamInfo, 
        EclipseProgressMonitorChild monitor) {
        
        if (exists controller, exists viewer, 
            exists rn = controller.lastCompilationUnit, 
            exists t = controller.tokens, 
            exists pu = controller.parseAndTypecheck(
                viewer.document, 10, monitor.wrapped, null)) {
            
            editor.annotationCreator.updateAnnotations();
            return getContentProposals {
                typecheckedRootNode = pu.compilationUnit;
                analysisResult = controller;
                offset = offset;
                line = CompletionUtil.getLine(offset, viewer);
                secondLevel = secondLevel;
                monitor = monitor;
                returnedParamInfo = returnedParamInfo;
                cancellable = monitor;
            };
        }
        else {
            return [];
        }
    }
}
