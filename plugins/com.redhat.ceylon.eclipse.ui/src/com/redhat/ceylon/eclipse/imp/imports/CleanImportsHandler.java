package com.redhat.ceylon.eclipse.imp.imports;

import static com.redhat.ceylon.eclipse.imp.editor.Util.getCurrentEditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.compiler.typechecker.model.Import;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportList;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class CleanImportsHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu==null) return null;
        DetectUnusedImportsVisitor duiv = new DetectUnusedImportsVisitor();
        cu.visit(duiv);
        TextFileChange tfc = new TextFileChange("Clean Imports", 
                ((IFileEditorInput) editor.getEditorInput()).getFile());
        tfc.setEdit(new MultiTextEdit());
        List<Tree.Import> importList = new ArrayList<Tree.Import>();
        ImportList til = cu.getImportList();
        if (til!=null && til.getStartIndex()!=null && til.getStopIndex()!=null) {
            importList.addAll(til.getImports());
            Collections.sort(importList, new Comparator<Tree.Import>() {
                @Override
                public int compare(Tree.Import i1, Tree.Import i2) {
                    return packageName(i1).compareTo(packageName(i2));
                }
            });
            StringBuilder builder = new StringBuilder();
            for (Tree.Import ti: importList) {
                List<Tree.ImportMemberOrType> list = new ArrayList<Tree.ImportMemberOrType>();
                for (Tree.ImportMemberOrType i: ti.getImportMemberOrTypeList()
                            .getImportMemberOrTypes()) {
                    if (i.getDeclarationModel()!=null) {
                        if (!duiv.getResult().contains(i.getDeclarationModel())) {
                            list.add(i);
                        }
                        else {
                            if (i.getImportMemberOrTypeList()!=null) {
                                for (Tree.ImportMemberOrType j: i.getImportMemberOrTypeList()
                                        .getImportMemberOrTypes()) {
                                    if (!duiv.getResult().contains(j.getDeclarationModel())) {
                                        list.add(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (!list.isEmpty()) {
                    builder.append("import ")
                            .append(packageName(ti))
                            .append(" { ");
                    for (Tree.ImportMemberOrType i: list) {
                        if (i.getDeclarationModel()!=null) {
                            if ( !i.getImportModel().getAlias().equals(i.getDeclarationModel().getName()) ) {
                                builder.append(i.getImportModel().getAlias()).append("=");
                            }
                            builder.append(i.getDeclarationModel().getName());
                            if (i.getImportMemberOrTypeList()!=null) {
                                builder.append(" { ");
                                boolean found=false;
                                for (Tree.ImportMemberOrType j: i.getImportMemberOrTypeList()
                                        .getImportMemberOrTypes()) {
                                    if (j.getDeclarationModel()!=null) {
                                        if (!duiv.getResult().contains(j.getDeclarationModel())) {
                                            found=true;
                                            if (!j.getImportModel().getAlias().equals(j.getDeclarationModel().getName())) {
                                                builder.append(j.getImportModel().getAlias()).append("=");
                                            }
                                            builder.append(j.getDeclarationModel().getName()).append(", ");
                                        }
                                    }
                                }
                                if (found) builder.setLength(builder.length()-2);
                                builder.append(" }");
                                if (!found) builder.setLength(builder.length()-5);
                            }
                            builder.append(", ");
                        }
                    }
                    /*if (ti.getImportMemberOrTypeList().getImportWildcard()!=null) {
                        builder.append(" ... ");
                    }*/
                    builder.setLength(builder.length()-2);
                    builder.append(" }\n");
                }
                if (ti.getImportMemberOrTypeList().getImportWildcard()!=null) {
                    builder.append("import ")
                        .append(packageName(ti))
                        .append(" { ... }\n");
                }
            }
            if (builder.length()!=0) {
                builder.setLength(builder.length()-1);
            }
            tfc.addEdit(new ReplaceEdit(til.getStartIndex(), 
                    til.getStopIndex()-til.getStartIndex()+1, 
                    builder.toString()));
            /*for (ImportMemberOrType imt: duiv.getResult()) {
                tfc.addEdit( new DeleteEdit(imt.getStartIndex(), 
                        imt.getStopIndex()-imt.getStartIndex()+1) );
            }*/
            tfc.initializeValidationData(null);
            try {
                ResourcesPlugin.getWorkspace().run(new PerformChangeOperation(tfc), 
                        new NullProgressMonitor());
            }
            catch (CoreException ce) {
                throw new ExecutionException("Error cleaning imports", ce);
            }
        }
        return null;
    }
    
    private static String packageName(Tree.Import i) {
        return i.getImportMemberOrTypeList()
                .getImportList().getImportedScope()
                .getQualifiedNameString();
    }
    
    @Override
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() && 
                editor instanceof CeylonEditor &&
                editor.getEditorInput() instanceof IFileEditorInput) {
            CeylonParseController cpc = ((CeylonEditor) editor).getParseController();
            return cpc.getRootNode()==null ? false :
                !cpc.getRootNode().getImportList().getImports().isEmpty();
        }
        return false;
    }
}
