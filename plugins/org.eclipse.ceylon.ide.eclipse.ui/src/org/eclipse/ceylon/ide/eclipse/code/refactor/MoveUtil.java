/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.code.correct.ImportProposals.importProposals;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getUnits;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.vfsJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getDocument;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.platform.platformJ2C;
import org.eclipse.ceylon.ide.eclipse.util.DocLinks;
import org.eclipse.ceylon.ide.common.util.escaping_;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;

import ceylon.interop.java.CeylonMap;

public class MoveUtil {

    public static int addImportEdits(Tree.Declaration node, 
            TextChange fc, IDocument doc, Tree.CompilationUnit ncu, 
            Set<String> packages, Declaration declaration) {
        Package p = ncu.getUnit().getPackage();
        Map<Declaration, String> imports = 
                getImports(node, p.getNameAsString(), ncu, 
                        packages);
        org.eclipse.ceylon.ide.common.platform.TextChange change
            = new platformJ2C().newChange("", fc);
        return (int) importProposals()
                .applyImportsWithAliases(change, 
                        new CeylonMap<>(null, null, imports), 
                        ncu, change.getDocument(),
                        null, declaration);
    }

    public static Map<Declaration,String> getImports(Node node,
            final String packageName, final Tree.CompilationUnit ncu,
            final Set<String> packages) {
        final Map<Declaration, String> imports = 
                new HashMap<Declaration, String>();
        node.visit(new Visitor() {
            private void add(Declaration d, Tree.Identifier id) {
                if (d!=null && id!=null) {
                    String pn = 
                            d.getUnit()
                             .getPackage()
                             .getNameAsString();
                    if (d.isToplevel() &&
                            !pn.equals(packageName) &&
                            !pn.equals(Module.LANGUAGE_MODULE_NAME) &&
                            (ncu==null 
                                || !importProposals()
                                    .isImported(d, ncu.getUnit()))) {
                        imports.put(d, id.getText());
                        packages.add(pn);
                    }
                }
            }
            @Override
            public void visit(Tree.BaseType that) {
                super.visit(that);
                add(that.getDeclarationModel(), that.getIdentifier());
            }
            @Override
            public void visit(Tree.BaseMemberOrTypeExpression that) {
                super.visit(that);
                add(that.getDeclaration(), that.getIdentifier());
            }
            @Override
            public void visit(Tree.MemberLiteral that) {
                add(that.getDeclaration(), that.getIdentifier());
            }
        });
        return imports;
    }
    
    public static String getImportText(Tree.Declaration node, 
            String targetPackage, String delim) {
        HashSet<String> packages = new HashSet<String>();
        Map<Declaration, String> imports = 
                getImports(node, targetPackage, null, packages);
        return getImportText(packages, imports, delim);
    }

    public static String getImportText(Set<String> packages, 
            Map<Declaration, String> imports, String delim) {
        StringBuilder sb = new StringBuilder();
        for (String pkg: packages) {
            if (pkg.isEmpty()) {
                //can't import from default package
                continue;
            }
            sb.append("import ")
              .append(escapePackageName(pkg))
              .append(" {")
              .append(delim);
            boolean first = true;
            for (Map.Entry<Declaration, String> entry: 
                    imports.entrySet()) {
                Declaration d = entry.getKey();
                String pname = 
                        d.getUnit()
                         .getPackage()
                         .getQualifiedNameString();
                if (pname.equals(pkg)) {
                    if (!first) {
                        sb.append(",").append(delim);
                    }
                    sb.append(utilJ2C().indents()
                            .getDefaultIndent());
                    String name = d.getName();
                    String alias = entry.getValue();
                    if (!name.equals(alias)) {
                        sb.append(alias).append("=");
                    }
                    sb.append(name);
                    first = false;
                }
            }
            sb.append(delim)
              .append("}")
              .append(delim);
        }
        return sb.toString();
    }

    public static void refactorProjectImportsAndDocLinks(
            Tree.Declaration node,
            IFile originalFile, IFile targetFile, 
            CompositeChange change, 
            String originalPackage, String targetPackage) {
        if (!originalPackage.equals(targetPackage)) {
            List<PhasedUnit> units = 
                    getAllUnits(originalFile.getProject());
            for (PhasedUnit pu: units) {
//                if (!node.getUnit().equals(pu.getUnit())) {
                    IFile file = 
                            vfsJ2C()
                                .getIFileVirtualFile(pu.getUnitFile())
                                .getNativeResource();
                    if (!file.equals(originalFile) 
                            && !file.equals(targetFile)) {
                        TextFileChange tfc = 
                                new TextFileChange("Fix Import", 
                                        file);
                        tfc.setEdit(new MultiTextEdit());
                        Tree.CompilationUnit rootNode = 
                                pu.getCompilationUnit();
                        refactorImports(node, 
                                originalPackage, targetPackage, 
                                rootNode, tfc);
                        refactorDocLinks(node, targetPackage, 
                                rootNode, tfc);
                        if (tfc.getEdit().hasChildren()) {
                            change.add(tfc);
                        }
                    }
//                }
            }
        }
    }

    public static boolean isUnsharedUsedLocally(
            Tree.Declaration node, IFile originalFile, 
            String originalPackage, String targetPackage) {
        Declaration dec = node.getDeclarationModel();
        if (!dec.isShared() 
                && !originalPackage.equals(targetPackage)) {
            List<PhasedUnit> units = 
                    getAllUnits(originalFile.getProject());
            for (PhasedUnit pu: units) {
                Tree.CompilationUnit cu = 
                        pu.getCompilationUnit();
                String pn = 
                        cu.getUnit()
                            .getPackage()
                            .getNameAsString();
                if (pn.equals(originalPackage) 
                        && isUsedInUnit(cu, dec)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void refactorImports(Tree.Declaration node, 
            String originalPackage, String targetPackage, 
            Tree.CompilationUnit cu, TextChange tc) {
        Declaration dec = node.getDeclarationModel();
        String pname = 
                cu.getUnit()
                    .getPackage()
                    .getNameAsString();
        boolean inOriginalPackage = 
                pname.equals(originalPackage);
        boolean inNewPackage = 
                pname.equals(targetPackage);
        boolean foundOriginal = 
                removeImport(originalPackage, dec, cu, tc, 
                        Collections.<String>emptySet());
        if (foundOriginal && !inNewPackage || 
                inOriginalPackage && !inNewPackage && 
                        isUsedInUnit(cu, dec)) {
            if (!targetPackage.isEmpty()) {
                addImport(targetPackage, dec, cu, tc);
            }
        }
    }

    public static void refactorDocLinks(Tree.Declaration node,
            final String targetPackage, Tree.CompilationUnit cu, 
            final TextChange tc) {
        final Declaration dec = node.getDeclarationModel();
        cu.visit(new Visitor() {
            @Override
            public void visit(Tree.DocLink that) {
                super.visit(that);
                if (that.getBase()!=null &&
                        that.getBase().equals(dec)) {
                    boolean inTargetPackage = 
                            that.getUnit().getPackage()
                            .getQualifiedNameString()
                            .equals(targetPackage);
                    if (that.getPkg() == null) {
                        if (!inTargetPackage) {
                            Region region = 
                                    DocLinks.nameRegion(that,0);
                            tc.addEdit(new InsertEdit(
                                    region.getOffset(), 
                                    targetPackage + "::"));
                        }
                    }
                    else {
                        Region region = 
                                DocLinks.packageRegion(that);
                        if (inTargetPackage) {
                            tc.addEdit(new DeleteEdit(
                                    region.getOffset(), 
                                    region.getLength()+2));
                        }
                        else {
                            tc.addEdit(new ReplaceEdit(
                                    region.getOffset(), 
                                    region.getLength(), 
                                    targetPackage));
                        }
                    }
                }
            }
        });
    }

    public static boolean isUsedInUnit(Tree.CompilationUnit rootNode, 
            final Declaration dec) {
        class DetectUsageVisitor extends Visitor {
            boolean detected;
            @Override
            public void visit(Tree.Declaration that) {
                if (!that.getDeclarationModel().equals(dec)) {
                    super.visit(that);
                }
            }
            @Override
            public void visit(Tree.BaseType that) {
                TypeDeclaration d = that.getDeclarationModel();
                if (d!=null && d.equals(dec)) {
                    detected = true;
                }
            }
            @Override
            public void visit(Tree.BaseMemberOrTypeExpression that) {
                Declaration d = that.getDeclaration();
                if (d!=null && d.equals(dec)) {
                    detected = true;
                }
            }
        }
        DetectUsageVisitor duv = new DetectUsageVisitor();
        duv.visit(rootNode);
        boolean used = duv.detected;
        return used;
    }

    public static boolean removeImport(String originalPackage, 
            Declaration dec, Tree.CompilationUnit cu, 
            TextChange tc, Set<String> packages) {
        boolean foundOriginal = false;
        Tree.ImportList il = cu.getImportList();
        for (Tree.Import imp: il.getImports()) {
            Referenceable model = 
                    imp.getImportPath()
                        .getModel();
            if (model!=null) {
                if (model.getNameAsString()
                        .equals(originalPackage)) {
                    Tree.ImportMemberOrTypeList imtl = 
                            imp.getImportMemberOrTypeList();
                    if (imtl!=null) {
                        List<Tree.ImportMemberOrType> imts = 
                                imtl.getImportMemberOrTypes();
                        for (int j=0; j<imts.size(); j++) {
                            Tree.ImportMemberOrType imt = 
                                    imts.get(j);
                            Declaration d = 
                                    imt.getDeclarationModel();
                            if (d!=null && d.equals(dec)) {
                                int offset;
                                int length;
                                if (j>0) {
                                    offset = imts.get(j-1)
                                            .getEndIndex();
                                    length = imt.getEndIndex()-offset;
                                }
                                else if (j<imts.size()-1) {
                                    offset = imt.getStartIndex();
                                    length = imts.get(j+1)
                                            .getStartIndex()-offset;
                                }
                                else {
                                    if (packages.contains(originalPackage)) { 
                                        //we're adding to this import statement,
                                        //so don't delete the whole import
                                        offset = imt.getStartIndex();
                                        length = imt.getDistance();
                                    }
                                    else {
                                        offset = imp.getStartIndex();
                                        length = imp.getDistance();
                                    }
                                }
                                tc.addEdit(new DeleteEdit(offset,length));
                                foundOriginal = true;
                                break;
                                //TODO: return the alias!
                            }
                        }
                    }
                    break;
                }
            }
        }
        return foundOriginal;
    }

    private static void addImport(String targetPackage, 
            Declaration dec, Tree.CompilationUnit cu, 
            TextChange tc) {
        String name = dec.getName();
        String delim = 
                utilJ2C().indents()
                    .getDefaultLineDelimiter(
                            getDocument(tc));
        String indent = 
                utilJ2C().indents()
                    .getDefaultIndent();
        Tree.ImportList il = cu.getImportList();
        for (Tree.Import i: il.getImports()) {
            Referenceable model = 
                    i.getImportPath()
                        .getModel();
            if (model!=null) {
                if (model.getNameAsString()
                        .equals(targetPackage)) {
                    //add to the existing import statement
                    Tree.ImportMemberOrTypeList imtl = 
                            i.getImportMemberOrTypeList();
                    if (imtl!=null) {
                        String addition;
                        int offset;
                        List<Tree.ImportMemberOrType> imts = 
                                imtl.getImportMemberOrTypes();
                        if (imts.isEmpty()) {
                            offset = imtl.getStartIndex()+1;
                            addition = delim + indent + name;
                            int len = imtl.getDistance();
                            if (len==2) {
                                addition += delim;
                            }
                        }
                        else {
                            offset = imts.get(imts.size()-1)
                                    .getEndIndex();
                            addition = "," + delim + indent + name;
                        }
                        //TODO: the alias!
                        tc.addEdit(new InsertEdit(offset, addition));
                    }
                    return;
                }
            }
        }
        
        //else create a whole new import statement
        StringBuilder sb = new StringBuilder();
        sb.append("import ")
          .append(escapePackageName(targetPackage))
          .append(" {")
          .append(delim)
          .append(indent)
          .append(name)
          .append(delim)
          .append("}")
          .append(delim);
        tc.addEdit(new InsertEdit(0, sb.toString()));
    }

    static TextChange createEditorChange(CeylonEditor editor,
            IDocument document) {
        if (editor.isDirty()) {
            return new DocumentChange("Move from Source File", 
                    document);
        }
        else {
            return new TextFileChange("Move from Source File", 
                    getFile(editor.getEditorInput()));
        }
    }

    public static boolean canMoveDeclaration(CeylonEditor editor) {
        Node node = editor.getSelectedNode();
        if (node instanceof Tree.Declaration) {
            Tree.Declaration dn = (Tree.Declaration) node;
            Declaration d = dn.getDeclarationModel();
            return d!=null && d.isToplevel();
        }
        else {
            return false;
        }
    }

    public static String getDeclarationName(CeylonEditor editor) {
        Node node = editor.getSelectedNode();
        if (node instanceof Tree.Declaration) {
            Tree.Declaration dn = (Tree.Declaration) node;
            return dn.getIdentifier().getText();
        }
        else {
            return null;
        }
    }

    static List<PhasedUnit> getAllUnits(IProject project) {
        List<PhasedUnit> units = new ArrayList<PhasedUnit>();
        units.addAll(getUnits(project));
        for (IProject p: project.getReferencingProjects()) {
            units.addAll(getUnits(p));
        }
        return units;
    }

    static IStructuredSelection getSelection() {
        IEditorPart ed = getCurrentEditor();
        if (ed!=null) {
            IEditorInput input = ed.getEditorInput();
            if (input instanceof IFileEditorInput) {
                IFileEditorInput fei = (IFileEditorInput) input;
                IFile file = fei.getFile();
                return new StructuredSelection(file);
            }
        }
        return null;
    }
    
    //TODO: move to escaping!
    public static String escapePackageName(String newName) {
        StringTokenizer tokenizer = 
                new StringTokenizer(newName, ".");
        StringBuilder builder = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            if (builder.length()!=0) {
                builder.append('.');
            }
            builder.append(escaping_.get_()
                    .escape(tokenizer.nextToken()));
        }
        return builder.toString();
    }

}
