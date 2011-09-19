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
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.compiler.typechecker.model.Import;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportList;

public class CleanImportsHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        UniversalEditor editor = (UniversalEditor) getCurrentEditor();
        Tree.CompilationUnit cu = (Tree.CompilationUnit) editor
                .getParseController().getCurrentAst();
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
                List<Import> list = new ArrayList<Import>();
                for (Import i: ti.getImportList().getImports()) {
                    if (!duiv.getResult().contains(i.getDeclaration())) {
                        list.add(i);
                    }
                }
                if (!list.isEmpty()) {
                    builder.append("import ")
                            .append(packageName(ti))
                            .append(" { ");
                    for (Import i: list) {
                        if ( !i.getAlias().equals(i.getDeclaration().getName()) ) {
                            builder.append(i.getAlias()).append("=");
                        }
                        builder.append(i.getDeclaration().getName())
                                .append(", ");
                    }
                    /*if (ti.getImportMemberOrTypeList().getImportWildcard()!=null) {
                        builder.append(" ... ");
                    }*/
                    builder.setLength(builder.length()-2);
                    builder.append(" }\n");
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
        return i.getImportList().getImportedPackage()
                .getQualifiedNameString();
    }
}
