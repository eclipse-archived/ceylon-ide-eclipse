import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.eclipse.code.complete {
    RefinementCompletionProposal
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
    eclipseIcons
}
import com.redhat.ceylon.ide.common.correct {
    QuickFixData,
    refineFormalMembersQuickFix,
    QuickFixKind
}
import com.redhat.ceylon.ide.common.doc {
    Icons
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
    Declaration
}

import java.util {
    Collection
}

import org.eclipse.core.resources {
    IProject,
    ResourcesPlugin {
        workspace
    }
}
import org.eclipse.core.runtime {
    NullProgressMonitor
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
    PerformChangeOperation
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
    editorSelection => DefaultRegion(editor.selection.offset, editor.selection.length);
    
    shared actual void addQuickFix(String desc, CommonTextChange|Callable<Anything, []> change,
        DefaultRegion? selection, Boolean qualifiedNameIsPath, Icons? icon,
        QuickFixKind kind) {
        
        if (is EclipseTextChange change) {
            proposals.add(
                proposalsFactory.createProposal(desc, change, selection,
                    qualifiedNameIsPath, icon, kind)
            );
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
    
    shared actual void addCreateParameterProposal(String description, 
        Declaration dec, Type? type, DefaultRegion selection, Icons icon,
        CommonTextChange change, Integer exitPos) {
        
        if (is EclipseTextChange change) {
            value image = eclipseIcons.fromIcons(icon) else CeylonResources.addCorr;
            proposals.add(
                CreateParameterProposal(description, dec, type,
                    toRegion(selection), image, change.nativeChange, exitPos)
            );
        }
    }
    
    shared actual void addCreateQuickFix(String description, Scope scope, 
        Unit unit, Type? returnType, Icons icon, CommonTextChange change,
        Integer exitPos, DefaultRegion selection) {
        
        if (is EclipseTextChange change) {
            value image = eclipseIcons.fromIcons(icon) else CeylonResources.addCorr;
            proposals.add(
                CreateProposal(description, scope, unit, returnType, image,
                    change.nativeChange, exitPos, toRegion(selection))
            );
        }
    }
    
    shared actual void addDeclareLocalProposal(String description, CommonTextChange change,
        Tree.Term term, Tree.BaseMemberExpression bme) {
        
        if (is EclipseTextChange change) {
            proposals.add(DeclareLocalProposal(this, change.nativeChange, description,
                term, bme, rootNode, editor));
        }
    }
    
    shared actual void addRefineFormalMembersProposal(String description) {
        value proposal = object extends RefineFormalMembersProposal(outer, description) {
            shared actual void apply(IDocument document) {
                value change = refineFormalMembersQuickFix.refineFormalMembers {
                    data = outer;
                    editorOffset = outer.editor.selection.offset;
                };
                if (is EclipseTextChange change) {
                    change.nativeChange.initializeValidationData(null);
                    workspace.run(
                        PerformChangeOperation(change.nativeChange),
                        NullProgressMonitor()
                    );
                }
            }
        };
        proposals.add(proposal);
    }
    
    shared actual void addRefineEqualsHashProposal(String description, CommonTextChange change) {
        if (is EclipseTextChange _chg = change) {
            value proposal = object extends CorrectionProposal(description, _chg.nativeChange, 
                null, RefinementCompletionProposal.\iDEFAULT_REFINEMENT) {
                
                styledDisplayString =>
                        let(hint=CorrectionUtil.shortcut("com.redhat.ceylon.eclipse.ui.action.refineEqualsHash"))
                        super.styledDisplayString.append(hint, StyledString.\iQUALIFIER_STYLER);
            };
            proposals.add(proposal); 
        }
    }
    
    shared actual void addAssignToLocalProposal(String description) {
        proposals.add(EclipseAssignToLocalProposal(this, description));
    }
}
