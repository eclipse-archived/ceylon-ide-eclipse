package com.redhat.ceylon.eclipse.code.imports;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeAliasedName;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;
import static com.redhat.ceylon.eclipse.util.Escaping.escapePackageName;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.window.Window;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.compiler.typechecker.analyzer.AnalysisError;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage;

public class CleanImportsHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) 
            throws ExecutionException {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        IDocument doc = editor.getCeylonSourceViewer().getDocument();
        cleanImports(editor.getParseController(), doc);
        return null;
    }

    public static void cleanImports(CeylonParseController cpc, IDocument doc) {
        if (!isEnabled(cpc)) return;
        Tree.CompilationUnit rootNode = cpc.getRootNode();
        if (rootNode!=null) {
            String imports = imports(rootNode, doc);
            if (imports!=null && 
                    !(imports.trim().isEmpty() && 
                            rootNode.getImportList().getImports().isEmpty())) {
                Tree.ImportList il = rootNode.getImportList();
                int start;
                int length;
                String extra;
                if (il==null || il.getImports().isEmpty()) {
                    start=0;
                    length=0;
                    extra=getDefaultLineDelimiter(doc);
                }
                else {
                    start = il.getStartIndex();
                    length = il.getStopIndex()-il.getStartIndex()+1;
                    extra="";
                }
                try {
                    if (!doc.get(start, length).equals(imports+extra)) {
                        DocumentChange change = 
                                new DocumentChange("Organize Imports", doc);
                        change.setEdit(new ReplaceEdit(start, length, imports+extra));
                        try {
                            change.perform(new NullProgressMonitor());
                        }
                        catch (CoreException e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String imports(Node node, Tree.ImportList til,
            IDocument doc) {
        List<Declaration> unused = new ArrayList<Declaration>();
        DetectUnusedImportsVisitor duiv = 
                new DetectUnusedImportsVisitor(unused);
        til.visit(duiv);
        node.visit(duiv);
        return reorganizeImports(til, unused, 
                Collections.<Declaration>emptyList(), doc);
    }
    
    private static String imports(final Tree.CompilationUnit cu, 
            IDocument doc) {
        List<Declaration> proposals = new ArrayList<Declaration>();
        List<Declaration> unused = new ArrayList<Declaration>();
        new ImportProposalsVisitor(cu, proposals).visit(cu);
        new DetectUnusedImportsVisitor(unused).visit(cu);
        return reorganizeImports(cu.getImportList(), unused, proposals, doc);
    }
    
    public static String imports(List<Declaration> proposed,
            IDocument doc) {
        return reorganizeImports(null, 
                Collections.<Declaration>emptyList(), 
                proposed, doc);
    }
    
    public static String reorganizeImports(Tree.ImportList til, 
            List<Declaration> unused, List<Declaration> proposed,
            IDocument doc) {
        Map<String,List<Tree.Import>> packages = 
                new TreeMap<String,List<Tree.Import>>();
        if (til!=null) {
            for (Tree.Import i: til.getImports()) {
                String pn = packageName(i);
                if (pn!=null) {
                    List<Tree.Import> is = packages.get(pn);
                    if (is==null) {
                        is = new ArrayList<Tree.Import>();
                        packages.put(pn, is);
                    }
                    is.add(i);
                }
            }
        }
        for (Declaration d: proposed) {
            String pn = d.getUnit().getPackage().getNameAsString();
            if (!packages.containsKey(pn)) {
                packages.put(pn, Collections.<Tree.Import>emptyList());
            }
        }
        
        StringBuilder builder = new StringBuilder();
        String lastToplevel=null;
        String delim = getDefaultLineDelimiter(doc);
        for (Map.Entry<String, List<Tree.Import>> pack: 
                packages.entrySet()) {
            String packageName = pack.getKey();
            List<Tree.Import> imports = pack.getValue();
            boolean hasWildcard = hasWildcard(imports);
            List<Tree.ImportMemberOrType> list = 
                    getUsedImportElements(imports, unused, 
                            hasWildcard, packages);
            if (hasWildcard || !list.isEmpty() || 
                    imports.isEmpty()) { //in this last case there is no existing import, but imports are proposed
                lastToplevel = appendBreakIfNecessary(lastToplevel, 
                        packageName, builder, doc);
                Referenceable packageModel = imports.isEmpty() ?
                        null : //TODO: what to do in this case? look up the Package where?
                        imports.get(0).getImportPath().getModel();
                String escapedPackageName = packageModel instanceof Package ?
                        escapePackageName((Package) packageModel) : 
                        packageName;
                if (builder.length()!=0) {
                    builder.append(delim);
                }
                builder.append("import ")
                        .append(escapedPackageName)
                        .append(" {");
                appendImportElements(packageName, list, unused, 
                        proposed, hasWildcard, builder, doc);
                builder.append(delim).append("}");
            }
        }
        return builder.toString();
    }

    private static boolean hasWildcard(List<Tree.Import> imports) {
        boolean hasWildcard = false;
        for (Tree.Import i: imports) {
            hasWildcard = hasWildcard || 
                    i!=null && i.getImportMemberOrTypeList()
                            .getImportWildcard()!=null;
        }
        return hasWildcard;
    }

    private static String appendBreakIfNecessary(String lastToplevel,
            String currentPackage, StringBuilder builder, IDocument doc) {
        int di = currentPackage.indexOf('.');
        String topLevel = di<0 ? 
                currentPackage:currentPackage.substring(0, di);
        if (lastToplevel!=null && !topLevel.equals(lastToplevel)) {
            builder.append(getDefaultLineDelimiter(doc));
        }
        return topLevel;
    }

    private static void appendImportElements(String packageName,
            List<Tree.ImportMemberOrType> elements, List<Declaration> unused, 
            List<Declaration> proposed, boolean hasWildcard, 
            StringBuilder builder, IDocument doc) {
        String indent = getDefaultIndent();
        for (Tree.ImportMemberOrType i: elements) {
            Declaration d = i.getDeclarationModel();
            if (d!=null && 
                    i.getIdentifier().getErrors().isEmpty() &&
                    i.getErrors().isEmpty()) {
                builder.append(getDefaultLineDelimiter(doc))
                        .append(indent);
                if (!i.getImportModel().getAlias()
                        .equals(d.getName())) {
                    String escapedAlias = escapeAliasedName(d, 
                            i.getImportModel().getAlias());
                    builder.append(escapedAlias).append("=");
                }
                builder.append(escapeName(d));
                appendNestedImportElements(i, unused, builder, doc);
                builder.append(",");
            }
        }
        for (Declaration d: proposed) {
            if (d.getUnit().getPackage().getNameAsString()
                    .equals(packageName)) {
                builder.append(getDefaultLineDelimiter(doc))
                       .append(indent);
                builder.append(escapeName(d)).append(",");
            }
        }
        if (hasWildcard) {
            builder.append(getDefaultLineDelimiter(doc))
                    .append(indent)
                    .append("...");
        }
        else {
            // remove trailing ,
            builder.setLength(builder.length()-1);
        }
    }

    private static void appendNestedImportElements(Tree.ImportMemberOrType imt,
            List<Declaration> unused, StringBuilder builder, IDocument doc) {
        String indent = getDefaultIndent();
        if (imt.getImportMemberOrTypeList()!=null) {
            builder.append(" {");
            boolean found=false;
            for (Tree.ImportMemberOrType nimt: 
                    imt.getImportMemberOrTypeList()
                            .getImportMemberOrTypes()) {
                Declaration d = nimt.getDeclarationModel();
                if (d!=null && 
                        nimt.getIdentifier().getErrors().isEmpty() &&
                        nimt.getErrors().isEmpty()) {
                    if (!unused.contains(d)) {
                        found=true;
                        builder.append(getDefaultLineDelimiter(doc))
                                .append(indent).append(indent);
                        if (!nimt.getImportModel().getAlias()
                                .equals(d.getName())) {
                            String escapedAlias = escapeAliasedName(d, 
                                    nimt.getImportModel().getAlias());
                            builder.append(escapedAlias).append("=");
                        }
                        builder.append(escapeName(d))
                                .append(",");
                    }
                }
            }
            if (imt.getImportMemberOrTypeList()
                    .getImportWildcard()!=null) {
                found=true;
                builder.append(getDefaultLineDelimiter(doc))
                        .append(indent).append(indent)
                        .append("...,");
            }
            
            if (found) {
                // remove trailing ","
                builder.setLength(builder.length()-1);
                builder.append(getDefaultLineDelimiter(doc))
                        .append(indent)
                        .append('}');   
            } else {
                // remove the " {" 
                builder.setLength(builder.length()-2);
            }
        }
    }

    private static boolean hasRealErrors(Node node) {
        for (Message m: node.getErrors()) {
            if (m instanceof AnalysisError) {
                return true;
            }
        }
        return false;
    }
    
    private static List<Tree.ImportMemberOrType> getUsedImportElements(
            List<Tree.Import> imports, List<Declaration> unused, 
            boolean hasWildcard, Map<String, List<Tree.Import>> packages) {
        List<Tree.ImportMemberOrType> list = 
                new ArrayList<Tree.ImportMemberOrType>();
        for (Tree.Import ti: imports) {
            for (Tree.ImportMemberOrType imt: 
                    ti.getImportMemberOrTypeList()
                            .getImportMemberOrTypes()) {
                Declaration dm = imt.getDeclarationModel();
                if (dm!=null && 
                        !hasRealErrors(imt.getIdentifier()) && 
                        !hasRealErrors(imt)) {
                    Tree.ImportMemberOrTypeList nimtl = imt.getImportMemberOrTypeList();
                    if (unused.contains(dm)) {
                        if (nimtl!=null) {
                            for (Tree.ImportMemberOrType nimt: 
                                    nimtl.getImportMemberOrTypes()) {
                                Declaration ndm = nimt.getDeclarationModel();
                                if (ndm!=null && 
                                        !hasRealErrors(nimt.getIdentifier()) && 
                                        !hasRealErrors(nimt)) {
                                    if (!unused.contains(ndm)) {
                                        list.add(imt);
                                        break;
                                    }
                                }
                            }
                            if (nimtl.getImportWildcard()!=null) {
                                list.add(imt);
                            }
                        }
                    } 
                    else {
                        if (!hasWildcard || 
                                imt.getAlias()!=null || 
                                nimtl!=null || 
                                preventAmbiguityDueWildcards(dm, packages)) {
                            list.add(imt);
                        }
                    }
                }
            }
        }
        return list;
    }
    
    private static boolean preventAmbiguityDueWildcards(Declaration d, 
            Map<String, List<Tree.Import>> importsMap) {
        Module module = d.getUnit().getPackage().getModule();
        String containerName = d.getContainer().getQualifiedNameString();

        for (Map.Entry<String, List<Tree.Import>> importEntry: 
                importsMap.entrySet()) {
            String packageName = importEntry.getKey();
            List<Tree.Import> importList = importEntry.getValue();
            if (!packageName.equals(containerName) &&
                    hasWildcard(importList)) {
                Package p2 = module.getPackage(packageName);
                if (p2 != null) {
                    Declaration d2 = p2.getMember(d.getName(), null, false);
                    if (d2!=null && 
                            d2.isToplevel() && 
                            d2.isShared() && 
                            !d2.isAnonymous() && 
                            !isImportedWithAlias(d2, importList)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    private static boolean isImportedWithAlias(Declaration d, 
            List<Tree.Import> importList) {
        for (Tree.Import i: importList) {
            for (Tree.ImportMemberOrType imt: 
                    i.getImportMemberOrTypeList()
                            .getImportMemberOrTypes()) {
                if (d.getName().equals(imt.getIdentifier().getText()) && 
                        imt.getAlias() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String packageName(Tree.Import i) {
        if (i.getImportPath()!=null) {
            return formatPath(i.getImportPath().getIdentifiers());
        }
        else {
            return null;
        }
    }
    
    @Override
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() && 
                editor instanceof CeylonEditor &&
                editor.getEditorInput() instanceof IFileEditorInput) {
            CeylonParseController cpc = 
                    ((CeylonEditor) editor).getParseController();
            return isEnabled(cpc);
        }
        else {
            return false;
        }
    }

    public static boolean isEnabled(CeylonParseController cpc) {
        return cpc!=null && 
                cpc.getStage().ordinal()>=Stage.TYPE_ANALYSIS.ordinal() && 
                cpc.getRootNode()!=null;
    }
    
    public static Declaration select(List<Declaration> proposals) {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        ImportSelectionDialog fid = 
                new ImportSelectionDialog(editor.getSite().getShell(),
                        proposals);
        if (fid.open() == Window.OK) {
            return (Declaration) fid.getFirstResult();
        }
        else {
            return null;
        }
    }

}
