import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.platform {
    EclipseTextChange
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    Highlights,
    eclipseIcons
}
import com.redhat.ceylon.ide.common.correct {
    QuickFixData,
    exportModuleImportQuickFix
}
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    CommonTextChange=TextChange
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Type,
    Scope,
    Referenceable,
    TypeDeclaration
}

import java.util {
    Collection
}

import org.eclipse.core.resources {
    IProject
}
import org.eclipse.jface.text {
    Region,
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.ltk.core.refactoring {
    CompositeChange
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}

shared class EclipseQuickFixData(ProblemLocation location,
    shared actual Tree.CompilationUnit rootNode,
    shared actual Node node,
    shared IProject project,
    shared Collection<ICompletionProposal> proposals,
    shared CeylonEditor editor,
    shared actual BaseCeylonProject ceylonProject,
    IDocument doc)
        satisfies QuickFixData {
    
    errorCode => location.problemId;
    problemOffset => location.offset;
    problemLength => location.length;
    
    phasedUnit => editor.parseController.lastPhasedUnit;
    document = EclipseDocument(doc);
    
    shared actual void addQuickFix(String desc, CommonTextChange|Callable<Anything, []> change,
        DefaultRegion? selection, Boolean qualifiedNameIsPath, Icons? icon) {
        
        value image = eclipseIcons.fromIcons(icon);
        
        if (is EclipseTextChange change) {
            value region = toRegion(selection);
            proposals.add(CorrectionProposal(desc,
                    change.nativeChange, region, image,
                    qualifiedNameIsPath));
        } else if (is Callable<Anything, []> callback = change) {
            proposals.add(object extends CorrectionProposal(desc, null, null) {
                shared actual void apply(IDocument? iDocument) {
                    callback();
                }
            });
        }
    }
    
    shared actual void addInitializerQuickFix(String desc, CommonTextChange change,
        DefaultRegion selection, Unit unit, Scope scope, Type? type) {
        
        if (is EclipseTextChange change) {
            proposals.add(EclipseInitializerProposal {
                    name = desc;
                    change = change.nativeChange;
                    unit = unit;
                    scope = scope;
                    type = type;
                    selection = Region(selection.start, selection.length);
                    image = CeylonResources.\iMINOR_CHANGE;
                    exitPos = -1;
                });
        }
    }
    
    shared actual void addParameterQuickFix(String desc, CommonTextChange change,
        DefaultRegion selection, Unit unit, Scope scope, Type? type, Integer exitPos) {
        
        if (is EclipseTextChange change) {
            proposals.add(EclipseInitializerProposal {
                    name = desc;
                    change = change.nativeChange;
                    unit = unit;
                    scope = scope;
                    type = type;
                    selection = Region(selection.start, selection.length);
                    image = CeylonResources.\iADD_CORR;
                    exitPos = exitPos;
                });
        }
    }
    
    shared actual void addParameterListQuickFix(String desc, CommonTextChange change,
        DefaultRegion selection) {
        
        if (is EclipseTextChange ch = change) {
            proposals.add(object extends CorrectionProposal(desc,
                    ch.nativeChange,
                    toRegion(selection)) {
                    shared actual StyledString styledDisplayString {
                        String hint = CorrectionUtil.shortcut("com.redhat.ceylon.eclipse.ui.action.addParameterList");
                        return Highlights.styleProposal(displayString, false)
                            .append(hint, StyledString.\iQUALIFIER_STYLER);
                    }
                });
        }
    }
    
    shared actual void addExportModuleImportProposal(Unit u, String description,
        String name, String version) {
        
        proposals.add(object extends ExportModuleImportProposal(description) {
                shared actual void apply(IDocument doc) {
                    exportModuleImportQuickFix.applyChanges(outer, u, name);
                }
            });
    }
    
    shared actual void addModuleImportProposal(Unit u, String description,
        String name, String version) {
        
        proposals.add(EclipseAddModuleImportProposal {
                desc = description;
                data = this;
                unit = u;
                name = name;
                version = version;
            });
    }
    
    shared actual void addAnnotationProposal(Referenceable declaration,
        String text, String description, CommonTextChange change,
        DefaultRegion? selection) {
        
        assert (is EclipseTextChange change);
        
        value region = toRegion(selection);
        
        value proposal = AddRemoveAnnotionProposal(declaration, text, description,
            change.nativeChange, region);
        
        if (!proposals.contains(proposal)) {
            proposals.add(proposal);
        }
    }
    
    shared actual void addSatisfiesProposal(TypeDeclaration typeParam, 
        String description, String missingSatisfiedTypeText,
        CommonTextChange change, DefaultRegion? region) {
        
        if (is EclipseTextChange change) {
            value composite = CompositeChange(change.nativeChange.name);
            composite.add(change.nativeChange);
            
            value reg = toRegion(region);
            
            value proposal = AddSatisfiesProposal(typeParam, description, 
                missingSatisfiedTypeText, composite, reg);
            
            if (!proposals.contains(proposal)) {
                proposals.add(proposal);
            }
        }
    }
    
    shared actual void addChangeTypeProposal(String description, 
        CommonTextChange change, DefaultRegion selection, Unit unit) {
        
        if (is EclipseTextChange change) {
            value region = toRegion(selection);
            proposals.add(ChangeTypeProposal(description, region, unit,
                change.nativeChange));
        }
    }
    
    shared actual void addConvertToClassProposal(String description, 
        Tree.ObjectDefinition declaration) {
        
        proposals.add(EclipseConvertToClassProposal(description, editor, declaration));
    }
    
    Region? toRegion(DefaultRegion? reg)
            => if (exists reg)
               then Region(reg.start, reg.length)
               else null;
}
