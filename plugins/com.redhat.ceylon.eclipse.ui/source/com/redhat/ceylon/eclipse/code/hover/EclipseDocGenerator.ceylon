import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.code.html {
    HTML,
    HTMLPrinter
}
import com.redhat.ceylon.eclipse.code.imports {
    eclipseModuleImportUtils
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.util {
    Nodes,
    Highlights
}
import com.redhat.ceylon.ide.common.doc {
    DocGenerator,
    Icons,
    Colors
}
import com.redhat.ceylon.ide.common.imports {
    AbstractModuleImportUtil
}
import com.redhat.ceylon.ide.common.model {
    CeylonUnit
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Unit,
    Referenceable,
    Scope,
    Package,
    Module
}

import java.lang {
    JStringBuilder=StringBuilder
}

import org.eclipse.core.resources {
    IProject
}
import org.eclipse.core.runtime {
    NullProgressMonitor
}
import org.eclipse.jface.text {
    IRegion,
    ITextViewer,
    IInformationControlCreator,
    IDocument
}

object eclipseDocGenerator extends EclipseDocGenerator(null) {
    
}

class EclipseDocGenerator(CeylonEditor? editor) 
        extends SourceInfoHover(editor)
        satisfies DocGenerator<IDocument>{
    
    shared actual String? getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        if (exists editor, exists prov = editor.selectionProvider) {
            value cpc = editor.parseController;
            
            return getExpressionHoverText(editor, hoverRegion)
                else getDocumentation(cpc.typecheckedRootNode,
                    hoverRegion.offset, cpc);
        }
        
        return null;
    }
    
    shared actual CeylonBrowserInput? getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
        if (exists editor, exists prov = editor.selectionProvider) {
            if (exists result = getExpressionHoverText(editor, hoverRegion)) {
                return CeylonBrowserInput(null, null, result);
            } else if (exists cpc = editor.parseController, 
                exists rootNode = cpc.typecheckedRootNode, 
                exists result = getDocumentation(rootNode, hoverRegion.offset, cpc)) {
                Referenceable? model = DocumentationHover.getModel(editor, hoverRegion);
                return CeylonBrowserInput(null, model, result);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }        
    }
    
    hoverControlCreator
            => CeylonInformationControlCreator(editor, "F2 for focus");
    
    String? getExpressionHoverText(CeylonEditor editor, IRegion hoverRegion) {
        CeylonParseController? parseController = editor.parseController;
        if (!exists parseController) {
            return null;
        }
        
        if (exists rootNode = parseController.typecheckedRootNode) {
            value hoffset = hoverRegion.offset;
            value hlength = hoverRegion.length;
            
            if (exists selection = editor.selectionFromThread) {
                value offset = selection.offset;
                value length = selection.length;
                if (offset <= hoffset, offset+length >= hoffset+hlength) {
                    variable value node = Nodes.findNode(rootNode, parseController.tokens, selection);
                    value document = editor.ceylonSourceViewer.document;
                    IProject? project = editor.parseController.project;
                    if (is Tree.Type n = node) {
                        return DocumentationHover.getTypeHoverText(n, 
                            selection.text, document, project);
                    }
                    
                    if (is Tree.Expression n = node) {
                        node = n.term;
                    }
                    
                    if (is Tree.Term n = node) {
                        return getTermTypeText(n, selection.text);
                    }
                }
            }
        }
        
        return null;
    }
    
    shared actual void addIconAndText(StringBuilder builder, Icons|Referenceable icon, String text) {
        value b = JStringBuilder();
        
        if (is Referenceable icon) {
            value img = if (is Declaration|Package|Module icon)
                        then HTML.fileUrl(DocumentationHover.getIcon(icon)).toExternalForm()
                        else null;
            
            value top = if (is Package|Module icon) then 2 else 4;
            HTML.addImageAndLabel(b, icon, img, 16, 16, text, 20, top);
        } else {
            value i = switch(icon)
            case (Icons.imports) HTML.fileUrl("imp_obj.png").toExternalForm()
            case (Icons.annotations) HTML.fileUrl("annotation_obj.gif").toExternalForm()
            case (Icons.returns) HTML.fileUrl("stepreturn_co.png").toExternalForm()
            case (Icons.units) HTML.fileUrl("unit.gif").toExternalForm()
            case (Icons.enumeration) HTML.fileUrl("sub.png").toExternalForm()
            case (Icons.extendedType) HTML.fileUrl("superclass.png").toExternalForm()
            case (Icons.satisfiedTypes) HTML.fileUrl("super.png").toExternalForm()
            case (Icons.parameters) HTML.fileUrl("methpro_obj.png").toExternalForm()
            case (Icons.types) HTML.fileUrl("types.png").toExternalForm()
            case (Icons.exceptions) HTML.fileUrl("ihigh_obj.gif").toExternalForm()
            case (Icons.see) HTML.fileUrl("link_obj.gif").toExternalForm()
            case (Icons.implementation) HTML.fileUrl("implm_co.png").toExternalForm()
            case (Icons.override) HTML.fileUrl("over_co.png").toExternalForm()
            case (Icons.quickAssists) HTML.fileUrl("correction_change.png").toExternalForm()
            else HTML.fileUrl("ihigh_obj.gif").toExternalForm()
            ;
            value top = icon in [Icons.types, Icons.annotations] 
                        then 4 else 2;

            HTML.addImageAndLabel(b, null, i, 16, 16, text, 20, top);
        }
        builder.append(b.string);
    }
    
    shared actual void appendJavadoc(Declaration model, StringBuilder buffer) {
        JStringBuilder b = JStringBuilder();
        DocumentationHover.appendJavadoc(model, b, NullProgressMonitor());
        buffer.append(b.string);
    }
    
    shared actual void appendPageEpilog(StringBuilder builder) {
        JStringBuilder b = JStringBuilder();
        HTMLPrinter.addPageEpilog(b);
        builder.append(b.string);
    }
    
    shared actual void appendPageProlog(StringBuilder builder) {
        JStringBuilder b = JStringBuilder();
        HTMLPrinter.insertPageProlog(b, 0, HTML.styleSheet);
        builder.append(b.string);
    }
    
    shared actual String buildLink(Referenceable|String model, String text, String protocol) {
        value href = if (is String model) then "href='" + protocol + ":" + model + "'"
                     else if (protocol == "dec")
                     then "href='dec:``HTML.declink(model)``'"
                     else HTML.link(model);
        
        return "<tt><a ``href``>``text``</a></tt>";
    }
    
    shared actual String color(Object? what, Colors how) {
        value myColor = switch(how)
        case (Colors.strings) HTMLPrinter.toHex(Highlights.getCurrentThemeColor(Highlights.\iSTRINGS))
        case (Colors.annotations) HTMLPrinter.toHex(Highlights.getCurrentThemeColor(Highlights.\iANNOTATIONS))
        case (Colors.annotationStrings) HTMLPrinter.toHex(Highlights.getCurrentThemeColor(Highlights.\iANNOTATION_STRINGS))
        case (Colors.numbers) HTMLPrinter.toHex(Highlights.getCurrentThemeColor(Highlights.\iNUMBERS))
        case (Colors.identifiers) HTMLPrinter.toHex(Highlights.getCurrentThemeColor(Highlights.\iIDENTIFIERS))
        case (Colors.types) HTMLPrinter.toHex(Highlights.getCurrentThemeColor(Highlights.\iTYPES))
        case (Colors.keywords) HTMLPrinter.toHex(Highlights.getCurrentThemeColor(Highlights.\iKEYWORDS))
        ;
        
        return "<span style='color:``myColor``'>``what?.string else "null"``</span>";
    }
    
    getLiveValue(Declaration dec, Unit unit)
            => DocumentationHover.getLiveValue(dec, unit);
    
    getPhasedUnit(Unit u)
        => if (is CeylonUnit u) then u.phasedUnit else null;
    
    getReferencedNode(Declaration dec) 
            => Nodes.getReferencedNode(dec);
    
    getUnitName(Unit u)
            => if (is CeylonUnit u, 
                    exists ceylonFileName=u.ceylonFileName)
                then ceylonFileName 
                else u.filename;
    
    highlight(String text, LocalAnalysisResult<IDocument> cmp) 
            => HTML.highlightLine(text);
    
    markdown(String text, LocalAnalysisResult<IDocument> cmp, Scope? linkScope, Unit? unit) 
            => DocumentationHover.markdown(text, linkScope, unit);
    
    printer => DocumentationHover.\iPRINTER;
    verbosePrinter => DocumentationHover.\iVERBOSE_PRINTER;
    
    showMembers => true;
    
    shared IInformationControlCreator informationPresenterControlCreator 
            => CeylonEnrichedInformationControlCreator(editor);
    shared actual AbstractModuleImportUtil<out Anything,out Anything,out Anything,out Anything,out Anything,out Anything> moduleImportUtil
            => eclipseModuleImportUtils;
    
    supportsQuickAssists => true;
}
