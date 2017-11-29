/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.compiler.java.codegen.CodegenUtil.getJavaNameOfDeclaration;
import static org.eclipse.ceylon.ide.eclipse.util.DocLinks.nameRegion;
import static org.eclipse.ceylon.ide.eclipse.util.JavaSearch.createSearchPattern;
import static org.eclipse.ceylon.ide.eclipse.util.JavaSearch.getProjectAndReferencingProjects;
import static org.eclipse.ceylon.ide.eclipse.util.JavaSearch.runSearch;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedExplicitDeclaration;
import static java.util.Collections.emptyList;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.CLASS_AND_INTERFACE;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.REFERENCES;
import static org.eclipse.jdt.core.search.SearchPattern.R_EXACT_MATCH;
import static org.eclipse.jdt.core.search.SearchPattern.createPattern;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createErrorStatus;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Identifier;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.common.util.escaping_;
import org.eclipse.ceylon.model.typechecker.model.ClassOrInterface;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.FunctionOrValue;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypeParameter;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;
import org.eclipse.ceylon.model.typechecker.model.Value;

public class RenameRefactoring extends AbstractRefactoring {
    
    private static class FindRenamedReferencesVisitor 
            extends FindReferencesVisitor {
        
        private FindRenamedReferencesVisitor(Declaration declaration) {
            super(declaration);
        }
        
        @Override
        protected boolean isReference(Declaration ref) {
            return super.isReference(ref) ||
                    //include refinements of the selected 
                    //declaration that we're renaming
                    ref!=null && 
                    ref.refines((Declaration) getDeclaration());
        }
        
        @Override
        protected boolean isReference(Declaration ref, String id) {
            return isReference(ref) && 
                    id!=null &&
                    //ignore references that use an alias
                    //since we don't need to rename them
                    getDeclaration().getNameAsString()
                        .equals(id); 
        }
        
        @Override
        public void visit(Tree.SpecifierStatement that) {
            if (that.getRefinement()) {
                Tree.Term lhs = that.getBaseMemberExpression();
                if (lhs instanceof Tree.ParameterizedExpression) {
                    Tree.ParameterizedExpression pe = 
                            (Tree.ParameterizedExpression) lhs;
                    for (Tree.ParameterList pl: pe.getParameterLists()) {
                        if (pl!=null) {
                            pl.visit(this);
                        }
                    }
                    Tree.TypeParameterList tpl = 
                            pe.getTypeParameterList();
                    if (tpl!=null) {
                        tpl.visit(this);
                    }
                }
                //the LHS will be treated as a refinement by
                //FindRefinementsVisitor so ignore it here
                super.visit(that.getSpecifierExpression());
            }
            else {
                super.visit(that);
            }
        }
    }

    private static class FindDocLinkReferencesVisitor 
            extends Visitor {
        
        private Declaration declaration;
        private int count;
        
        public int getCount() {
            return count;
        }
        
        FindDocLinkReferencesVisitor(Declaration declaration) {
            this.declaration = declaration;
        }
        
        @Override
        public void visit(Tree.DocLink that) {
            Declaration base = that.getBase();
            if (base!=null) {
                if (base.equals(declaration)) {
                    count++;
                }
                else {
                    List<Declaration> qualified = 
                            that.getQualified();
                    if (qualified!=null) {
                        if (qualified.contains(declaration)) {
                            count++;
                        }
                    }
                }
            }
        }
    }

    private final class FindDocLinkVisitor extends Visitor {
        
        private List<Region> links = new ArrayList<Region>();
        
        List<Region> getLinks() {
            return links;
        }

        private void visitIt(Region region, Declaration dec) {
            if (dec!=null && dec.equals(declaration)) {
                links.add(region);
            }
        }

        @Override
        public void visit(Tree.DocLink that) {
            Declaration base = that.getBase();
            if (base!=null) {
                visitIt(nameRegion(that, 0), base);
                List<Declaration> qualified = 
                        that.getQualified();
                if (qualified!=null) {
                    for (int i=0; 
                            i<qualified.size(); 
                            i++) {
                        visitIt(nameRegion(that, i+1),
                                qualified.get(i));
                    }
                }
            }
        }
    }

    private final class FindSimilarNameVisitor extends Visitor {
        
        private ArrayList<Identifier> identifiers
            = new ArrayList<Identifier>();
        
        public ArrayList<Identifier> getIdentifiers() {
            return identifiers;
        }
        
        @Override
        public void visit(Tree.TypedDeclaration that) {
            super.visit(that);
            Tree.Identifier id = 
                    that.getIdentifier();
            if (id!=null) {
                Type type = 
                        that.getType()
                            .getTypeModel();
                if (type!=null) {
                    TypeDeclaration td = 
                            type.getDeclaration();
                    if ((td instanceof ClassOrInterface ||
                         td instanceof TypeParameter) && 
                            td.equals(declaration)) {
                        String text = id.getText();
                        String name = declaration.getName();
                        if (text.equalsIgnoreCase(name) ||
                                text.endsWith(name)) {
                            identifiers.add(id);
                        }
                    }
                }
            }
        }
    }

    private String newName;
    private final Declaration declaration;
    private boolean renameFile;
    private boolean renameValuesAndFunctions;
    
    @Override
    int getSaveMode() {
        return isAffectingOtherFiles() ? 
                RefactoringSaveHelper.SAVE_REFACTORING : 
                RefactoringSaveHelper.SAVE_NOTHING;
    }
    
    public Node getNode() {
        return node;
    }

    public RenameRefactoring(IEditorPart editor) {
        super(editor);
        boolean identifiesDeclaration = 
                node instanceof Tree.DocLink || 
                getIdentifyingNode(node) 
                     instanceof Tree.Identifier;
        if (rootNode!=null && identifiesDeclaration) {
            Referenceable refDec = 
                    getReferencedExplicitDeclaration(node, 
                            rootNode);
            if (refDec instanceof Declaration) {
                Declaration dec = (Declaration) refDec;
                declaration = dec.getRefinedDeclaration();
                newName = declaration.getName();
                String filename = 
                        declaration.getUnit()
                            .getFilename();
                renameFile = 
                        (declaration.getName() + ".ceylon")
                            .equals(filename);
            }
            else {
                declaration = null;
            }
        }
        else {
            declaration = null;
        }
    }
    
    @Override
    public boolean getEnabled() {
        return declaration instanceof Declaration &&
                declaration.getName()!=null &&
                project != null &&
                (inSameProject(declaration) || inSameUnit());
    }

    private boolean inSameUnit() {
        return getEditable() && 
                declaration.getUnit()
                    .equals(rootNode.getUnit());
    }

    public int getCount() {
        return declaration==null ? 0 : 
            countDeclarationOccurrences();
    }
    
    @Override
    int countReferences(Tree.CompilationUnit cu) {
        FindRenamedReferencesVisitor frv = 
                new FindRenamedReferencesVisitor(declaration);
        Declaration dec = 
                (Declaration) frv.getDeclaration();
        FindRefinementsVisitor fdv = 
                new FindRefinementsVisitor(dec);
        FindDocLinkReferencesVisitor fdlrv = 
                new FindDocLinkReferencesVisitor(dec);
        cu.visit(frv);
        cu.visit(fdv);
        cu.visit(fdlrv);
        return frv.getNodes().size() + 
                fdv.getDeclarationNodes().size() + 
                fdlrv.getCount();
    }

    public String getName() {
        return "Rename";
    }

    public RefactoringStatus checkInitialConditions(
            IProgressMonitor pm)
                    throws CoreException, 
                           OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(
            IProgressMonitor pm)
                    throws CoreException, 
                           OperationCanceledException {
        if (!newName.matches("^[a-zA-Z_]\\w*$")) {
            return createErrorStatus(
                    "Not a legal Ceylon identifier");
        }
        else if (escaping_.get_().isKeyword(newName)) {
            return createErrorStatus(
                    "'" + newName + "' is a Ceylon keyword");
        }
        else {
            int ch = newName.codePointAt(0);
            if (declaration instanceof TypedDeclaration) {
                if (!Character.isLowerCase(ch) && ch!='_') {
                    return createErrorStatus(
                            "Not an initial lowercase identifier");
                }
            }
            else if (declaration instanceof TypeDeclaration) {
                if (!Character.isUpperCase(ch)) {
                    return createErrorStatus(
                            "Not an initial uppercase identifier");
                }
            }
        }
        Declaration existing = 
                declaration.getContainer()
                    .getMemberOrParameter(
                            declaration.getUnit(), 
                            newName, null, false);
        if (null!=existing && 
                !existing.equals(declaration)) {
            return createWarningStatus(
                    "An existing declaration named '" +
                    newName + 
                    "' already exists in the same scope");
        }
        return new RefactoringStatus();
    }

    public CompositeChange createChange(IProgressMonitor pm) 
            throws CoreException, 
                   OperationCanceledException {
        CompositeChange change = 
                (CompositeChange) 
                    super.createChange(pm);
        
        if (project!=null && renameFile) {
            renameSourceFile(change);
        }
        
        refactorJavaReferences(pm, change);
        
        return change;
    }

    private void renameSourceFile(CompositeChange change) {
        String unitPath = 
                declaration.getUnit()
                    .getFullPath();
        IPath oldPath = 
                project.getFullPath()
                    .append(unitPath);
        String newFileName = getNewName() + ".ceylon";
        IPath newPath = 
                oldPath.removeFirstSegments(1)
                    .removeLastSegments(1)
                    .append(newFileName);
        if (!project.getFile(newPath).exists()) {
            change.add(new RenameResourceChange(
                    oldPath, newFileName));
        }
    }

    protected void refactorJavaReferences(IProgressMonitor pm,
            final CompositeChange cc) {
        final Map<IResource,TextChange> changes = 
                new HashMap<IResource, TextChange>();
        SearchEngine searchEngine = new SearchEngine();
        IProject[] projects = 
                getProjectAndReferencingProjects(project);
        final String pattern;
        try {
            pattern = getJavaNameOfDeclaration(declaration);
        }
        catch (Exception e) {
            return;
        }
        boolean anonymous = pattern.endsWith(".get_");
        if (!anonymous) {
            SearchPattern searchPattern = 
                    createSearchPattern(declaration, 
                            REFERENCES);
            if (searchPattern==null) return;
            SearchRequestor requestor = 
                    new SearchRequestor() {
                @Override
                public void acceptSearchMatch(SearchMatch match) {
                    String filename = 
                            match.getResource().getName();
                    boolean isJavaFile = 
                            JavaCore.isJavaLikeFileName(
                                    filename);
                    if (isJavaFile) {
                        TextChange change = 
                                canonicalChange(cc, changes, match);
                        if (change!=null) {
                            int loc = pattern.lastIndexOf('.') + 1;
                            String oldName = 
                                    pattern.substring(loc);
                            if (declaration instanceof Value) {
                                change.addEdit(new ReplaceEdit(
                                        match.getOffset() + 3, 
                                        oldName.length() - 3, 
                                        escaping_.get_().toInitialUppercase(newName)));
                            }
                            else {
                                change.addEdit(new ReplaceEdit(
                                        match.getOffset(), 
                                        oldName.length(), 
                                        oldName.startsWith("$") ? 
                                                '$' + newName : 
                                                newName));
                            }
                        }
                    }
                }
            };
            runSearch(pm, searchEngine, searchPattern, 
                    projects, requestor);
        }
        if (anonymous ||
                declaration instanceof FunctionOrValue && 
                declaration.isToplevel()) {
            int loc = pattern.lastIndexOf('.');
            SearchPattern searchPattern = 
                    createPattern(pattern.substring(0, loc), 
                            CLASS_AND_INTERFACE, 
                            REFERENCES, 
                            R_EXACT_MATCH);
            SearchRequestor requestor = 
                    new SearchRequestor() {
                @Override
                public void acceptSearchMatch(SearchMatch match) {
                    TextChange change = 
                            canonicalChange(cc, changes, match);
                    if (change!=null) {
                        int end = 
                                pattern.lastIndexOf("_.");
                        int start = 
                                pattern.substring(0, end)
                                    .lastIndexOf('.') +1 ;
                        String oldName = 
                                pattern.substring(start, end);                    
                        change.addEdit(new ReplaceEdit(
                                match.getOffset(), 
                                oldName.length(), 
                                newName));
                    }
                }
            };
            runSearch(pm, searchEngine, searchPattern, 
                    projects, requestor);
        }
    }

    private TextChange canonicalChange(
            CompositeChange composite,
            Map<IResource, TextChange> changes, 
            SearchMatch match) {
        IResource resource = match.getResource();
        if (resource instanceof IFile) {
            TextChange change = changes.get(resource);
            if (change==null) {
                IFile file = (IFile) resource;
                change = new TextFileChange("Rename", file);
                change.setEdit(new MultiTextEdit());
                changes.put(resource, change);
                composite.add(change);
            }
            return change;
        }
        else {
            return null;
        }
    }
    
    @Override
    protected void refactorInFile(
            TextChange tfc, CompositeChange cc, 
            Tree.CompilationUnit root,
            List<CommonToken> tokens) {
        tfc.setEdit(new MultiTextEdit());
        if (declaration!=null) {
            for (Node node: getNodesToRename(root)) {
                renameNode(tfc, node, root);
            }
            for (Region region: getStringsToReplace(root)) {
                renameRegion(tfc, region, root);
            }
            if (renameValuesAndFunctions) { 
                for (Tree.Identifier id: 
                        getIdentifiersToRename(root)) {
                    renameIdentifier(tfc, id, root);
                }
            }
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }
    
    public List<Node> getNodesToRename(
            Tree.CompilationUnit root) {
        ArrayList<Node> list = new ArrayList<Node>();
        FindRenamedReferencesVisitor frv = 
                new FindRenamedReferencesVisitor(
                        declaration);
        root.visit(frv);
        list.addAll(frv.getNodes());
        FindRefinementsVisitor fdv = 
                new FindRefinementsVisitor(
                        (Declaration)
                            frv.getDeclaration());
        root.visit(fdv);
        list.addAll(fdv.getDeclarationNodes());
        return list;
    }
    
    public List<Tree.Identifier> getIdentifiersToRename(
            Tree.CompilationUnit root) {
        if (declaration instanceof TypeDeclaration) {
            FindSimilarNameVisitor fsnv = 
                    new FindSimilarNameVisitor();
            fsnv.visit(root);
            return fsnv.getIdentifiers();
        }
        else {
            return emptyList();
        }
    }
    
    public List<Region> getStringsToReplace(
            Tree.CompilationUnit root) {
        FindDocLinkVisitor fdlv = new FindDocLinkVisitor();
        fdlv.visit(root);
        return fdlv.getLinks();
    }

    protected void renameIdentifier(TextChange tfc, 
            Tree.Identifier id, Tree.CompilationUnit root) {
        String name = declaration.getName();
        int loc = id.getText().indexOf(name);
        int start = id.getStartIndex();
        int len = id.getDistance();
        if (loc>0) {
            tfc.addEdit(new ReplaceEdit(
                    start + loc, name.length(), 
                    newName));
        }
        else {
            tfc.addEdit(new ReplaceEdit(
                    start, len,
                    escaping_.get_().toInitialLowercase(newName)));
        }
    }

    protected void renameRegion(TextChange tfc, Region region, 
            Tree.CompilationUnit root) {
        tfc.addEdit(new ReplaceEdit(
                region.getOffset(), 
                region.getLength(), 
                newName));
    }

    protected void renameNode(TextChange tfc, Node node, 
            Tree.CompilationUnit root) {
        Node identifyingNode = getIdentifier(node);
        tfc.addEdit(new ReplaceEdit(
                identifyingNode.getStartIndex(), 
                identifyingNode.getDistance(), 
                newName));
    }

    protected static Node getIdentifier(Node node) {
        if (node instanceof Tree.SpecifierStatement) {
            Tree.SpecifierStatement st = 
                    (Tree.SpecifierStatement) node;
            Tree.Term lhs = st.getBaseMemberExpression();
            while (lhs instanceof Tree.ParameterizedExpression) {
                Tree.ParameterizedExpression pe = 
                        (Tree.ParameterizedExpression) lhs;
                lhs = pe.getPrimary();
            }
            if (lhs instanceof Tree.StaticMemberOrTypeExpression) {
                Tree.StaticMemberOrTypeExpression mte = 
                        (Tree.StaticMemberOrTypeExpression) lhs;
                return mte.getIdentifier();
            }
            else {
                throw new RuntimeException("impossible");
            }
        }
        else {
            return getIdentifyingNode(node);
        }
    }
    
    public boolean isRenameValuesAndFunctions() {
        return renameValuesAndFunctions;
    }
    
    public void setRenameValuesAndFunctions(boolean renameLocals) {
        this.renameValuesAndFunctions = renameLocals;
    }
    
    public void setNewName(String text) {
        newName = text;
    }
    
    public Declaration getDeclaration() {
        return declaration;
    }
    
    @Override
    public boolean isAffectingOtherFiles() {
        if (declaration==null) {
            return false;
        }
        if (declaration.isToplevel() ||
            declaration.isShared()) {
            return true;
        }
        if (declaration.isParameter()) {
            FunctionOrValue fov = 
                    (FunctionOrValue) 
                        declaration;
            Declaration container = 
                    fov.getInitializerParameter()
                        .getDeclaration();
            if (container.isToplevel() || 
                container.isShared()) {
                return true;
            }
        }
        return false;
    }

    public String getNewName() {
        return newName;
    }

    public boolean isRenameFile() {
        return renameFile;
    }

    public void setRenameFile(boolean renameFile) {
        this.renameFile = renameFile;
    }
}
