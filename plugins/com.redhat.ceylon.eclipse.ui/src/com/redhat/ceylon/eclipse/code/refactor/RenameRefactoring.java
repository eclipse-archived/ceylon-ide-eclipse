package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.java.codegen.CodegenUtil.getJavaNameOfDeclaration;
import static com.redhat.ceylon.eclipse.util.DocLinks.nameRegion;
import static com.redhat.ceylon.eclipse.util.JavaSearch.createSearchPattern;
import static com.redhat.ceylon.eclipse.util.JavaSearch.getProjectAndReferencingProjects;
import static com.redhat.ceylon.eclipse.util.JavaSearch.runSearch;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedExplicitDeclaration;
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;

public class RenameRefactoring extends AbstractRefactoring {
    
    private static class FindRenamedReferencesVisitor 
            extends FindReferencesVisitor {
        private FindRenamedReferencesVisitor(Declaration declaration) {
            super(declaration);
        }
        @Override
        protected boolean isReference(Declaration ref) {
            return super.isReference(ref) ||
                    ref!=null && ref.refines((Declaration)getDeclaration());
        }
        @Override
        protected boolean isReference(Declaration ref, String id) {
            return isReference(ref) && id!=null &&
                    getDeclaration().getNameAsString().equals(id); //TODO: really lame way to tell if it's an alias!
        }
    }

    private static class FindDocLinkReferencesVisitor extends Visitor {
        private Declaration declaration;
        int count;
        FindDocLinkReferencesVisitor(Declaration declaration) {
            this.declaration = declaration;
        }
        @Override
        public void visit(Tree.DocLink that) {
            if (that.getBase()!=null) {
                if (that.getBase().equals(declaration)) {
                    count++;
                }
                else if (that.getQualified()!=null) {
                    if (that.getQualified().contains(declaration)) {
                        count++;
                    }
                }
            }
        }
    }

    private String newName;
    private final Declaration declaration;
    private boolean renameFile;
    private boolean renameValuesAndFunctions;
    
    public Node getNode() {
        return node;
    }

    public RenameRefactoring(IEditorPart editor) {
        super(editor);
        if (rootNode!=null && 
                (getIdentifyingNode(node) instanceof Tree.Identifier ||
                node instanceof Tree.DocLink)) {
            Referenceable refDec = 
                    getReferencedExplicitDeclaration(node, rootNode);
            if (refDec instanceof Declaration) {
                declaration = ((Declaration) refDec).getRefinedDeclaration();
                newName = declaration.getName();
                String filename = declaration.getUnit().getFilename();
                renameFile = (declaration.getName()+".ceylon").equals(filename);
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
    public boolean isEnabled() {
        return declaration instanceof Declaration &&
                project != null &&
                inSameProject(declaration);
    }

    public int getCount() {
        return declaration==null ? 
                0 : countDeclarationOccurrences();
    }
    
    @Override
    int countReferences(Tree.CompilationUnit cu) {
        FindRenamedReferencesVisitor frv = 
                new FindRenamedReferencesVisitor(declaration);
        Declaration dec = (Declaration) frv.getDeclaration();
        FindRefinementsVisitor fdv = 
                new FindRefinementsVisitor(dec);
        FindDocLinkReferencesVisitor fdlrv = 
                new FindDocLinkReferencesVisitor(dec);
        cu.visit(frv);
        cu.visit(fdv);
        cu.visit(fdlrv);
        return frv.getNodes().size() + 
                fdv.getDeclarationNodes().size() + 
                fdlrv.count;
    }

    public String getName() {
        return "Rename";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        if (!newName.matches("^[a-zA-Z_]\\w*$")) {
            return createErrorStatus("Not a legal Ceylon identifier");
        }
        else if (Escaping.KEYWORDS.contains(newName)) {
            return createErrorStatus("'" + newName + "' is a Ceylon keyword");
        }
        else {
            int ch = newName.codePointAt(0);
            if (declaration instanceof TypedDeclaration) {
                if (!Character.isLowerCase(ch) && ch!='_') {
                    return createErrorStatus("Not an initial lowercase identifier");
                }
            }
            else if (declaration instanceof TypeDeclaration) {
                if (!Character.isUpperCase(ch)) {
                    return createErrorStatus("Not an initial uppercase identifier");
                }
            }
        }
        Declaration existing = declaration.getContainer()
                        .getMemberOrParameter(declaration.getUnit(), 
                                newName, null, false);
        if (null!=existing && !existing.equals(declaration)) {
            return createWarningStatus("An existing declaration named '" +
                newName + "' already exists in the same scope");
        }
        return new RefactoringStatus();
    }

    public CompositeChange createChange(IProgressMonitor pm) 
            throws CoreException, OperationCanceledException {
        List<PhasedUnit> units = getAllUnits();
        pm.beginTask(getName(), units.size());
        CompositeChange cc = new CompositeChange(getName());
        int i=0;
        for (PhasedUnit pu: units) {
            if (searchInFile(pu)) {
                TextFileChange tfc = newTextFileChange(pu);
                renameInFile(tfc, cc, pu.getCompilationUnit());
                pm.worked(i++);
            }
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            renameInFile(dc, cc, editor.getParseController().getRootNode());
            pm.worked(i++);
        }
        if (project!=null && renameFile) {
            IPath oldPath = project.getFullPath()
                    .append(declaration.getUnit().getFullPath());
            String newFileName = getNewName() + ".ceylon";
            IPath newPath = oldPath.removeFirstSegments(1).removeLastSegments(1)
                    .append(newFileName);
            if (!project.getFile(newPath).exists()) {
                cc.add(new RenameResourceChange(oldPath, newFileName));
            }
        }
        
        refactorJavaReferences(pm, cc);

        pm.done();
        return cc;
    }

    private void refactorJavaReferences(IProgressMonitor pm,
            final CompositeChange cc) {
        final Map<IResource,TextChange> changes = 
                new HashMap<IResource, TextChange>();
        SearchEngine searchEngine = new SearchEngine();
        IProject[] projects = getProjectAndReferencingProjects(project);
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
                    createSearchPattern(declaration, REFERENCES);
            if (searchPattern==null) return;
            SearchRequestor requestor = new SearchRequestor() {
                @Override
                public void acceptSearchMatch(SearchMatch match) {
                    TextChange change = canonicalChange(cc, changes, match);
                    if (change!=null) {
                        int loc = pattern.lastIndexOf('.')+1;
                        String oldName = pattern.substring(loc);
                        if (declaration instanceof Value) {
                            String uppercased = 
                                    Escaping.toInitialUppercase(newName);
                            change.addEdit(new ReplaceEdit(match.getOffset()+3, 
                                    oldName.length()-3, 
                                    uppercased));
                        }
                        else {
                            String replacedName = newName;
                            if (oldName.startsWith("$")) {
                                replacedName = '$' + replacedName;
                            }
                            change.addEdit(new ReplaceEdit(match.getOffset(), 
                                    oldName.length(), replacedName));
                        }
                    }
                }
            };
            runSearch(pm, searchEngine, searchPattern, projects, requestor);
        }
        if (anonymous ||
                declaration instanceof FunctionOrValue && 
                declaration.isToplevel()) {
            int loc = pattern.lastIndexOf('.');
            SearchPattern searchPattern = createPattern(pattern.substring(0, loc), 
                    CLASS_AND_INTERFACE, REFERENCES, R_EXACT_MATCH);
            SearchRequestor requestor = new SearchRequestor() {
                @Override
                public void acceptSearchMatch(SearchMatch match) {
                    TextChange change = canonicalChange(cc, changes, match);
                    if (change!=null) {
                        int end = pattern.lastIndexOf("_.");
                        int start = pattern.substring(0, end).lastIndexOf('.')+1;
                        String oldName = pattern.substring(start, end);                    
                        change.addEdit(new ReplaceEdit(match.getOffset(), 
                                oldName.length(), newName));
                    }
                }
            };
            runSearch(pm, searchEngine, searchPattern, projects, requestor);
        }
    }

    private TextChange canonicalChange(final CompositeChange cc,
            final Map<IResource, TextChange> changes, SearchMatch match) {
        IResource resource = match.getResource();
        if (resource instanceof IFile) {
            TextChange change = changes.get(resource);
            if (change==null) {
                change = new TextFileChange("Rename", (IFile) resource);
                change.setEdit(new MultiTextEdit());
                changes.put(resource, change);
                cc.add(change);
            }
            return change;
        }
        else {
            return null;
        }
    }
    
    private void renameInFile(TextChange tfc, CompositeChange cc, 
            Tree.CompilationUnit root) {
        tfc.setEdit(new MultiTextEdit());
        if (declaration!=null) {
            for (Node node: getNodesToRename(root)) {
                renameNode(tfc, node, root);
            }
            for (Region region: getStringsToReplace(root)) {
                renameRegion(tfc, region, root);
            }
            if (renameValuesAndFunctions) { 
                for (Tree.Identifier id: getIdentifiersToRename(root)) {
                    renameIdentifier(tfc, id, root);
                }
            }
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }
    
    public List<Node> getNodesToRename(Tree.CompilationUnit root) {
        ArrayList<Node> list = new ArrayList<Node>();
        FindRenamedReferencesVisitor frv = 
                new FindRenamedReferencesVisitor(declaration);
        root.visit(frv);
        list.addAll(frv.getNodes());
        FindRefinementsVisitor fdv = 
                new FindRefinementsVisitor((Declaration)frv.getDeclaration()) {
            @Override
            public void visit(Tree.SpecifierStatement that) {}
        };
        root.visit(fdv);
        list.addAll(fdv.getDeclarationNodes());
        return list;
    }
    
    public List<Tree.Identifier> getIdentifiersToRename(Tree.CompilationUnit root) {
        final ArrayList<Tree.Identifier> list = 
                new ArrayList<Tree.Identifier>();
        if (declaration instanceof TypeDeclaration) {
            new Visitor() {
                String name = declaration.getName();
                @Override
                public void visit(Tree.TypedDeclaration that) {
                    super.visit(that);
                    Tree.Identifier id = 
                            that.getIdentifier();
                    if (id!=null) {
                        TypeDeclaration d = 
                                that.getType().getTypeModel()
                                    .getDeclaration();
                        if ((d instanceof ClassOrInterface ||
                             d instanceof TypeParameter) && 
                                d.equals(declaration)) {
                            String text = id.getText();
                            if (text.equalsIgnoreCase(name) ||
                                    text.endsWith(name)) {
                                list.add(id);
                            }
                        }
                    }
                }
            }.visit(root);
        }
        return list;
    }
    
    public List<Region> getStringsToReplace(Tree.CompilationUnit root) {
        final List<Region> result = new ArrayList<Region>();
        new Visitor() {
            private void visitIt(Region region, Declaration dec) {
                if (dec!=null && dec.equals(declaration)) {
                    result.add(region);
                }
            }
            @Override
            public void visit(Tree.DocLink that) {
                Declaration base = that.getBase();
                List<Declaration> qualified = that.getQualified();
                if (base!=null) {
                    visitIt(nameRegion(that, 0), base);
                    if (qualified!=null) {
                        for (int i=0; i<qualified.size(); i++) {
                            visitIt(nameRegion(that, i+1),
                                    qualified.get(i));
                        }
                    }
                }
            }
        }.visit(root);
        return result;
    }

    void renameIdentifier(TextChange tfc, Tree.Identifier id, 
            Tree.CompilationUnit root) {
        String name = declaration.getName();
        int loc = id.getText().indexOf(name);
        if (loc>0) {
            tfc.addEdit(new ReplaceEdit(id.getStartIndex() + loc, 
                    name.length(), newName));
        }
        else {
            tfc.addEdit(new ReplaceEdit(id.getStartIndex(), 
                    id.getText().length(), 
                    Escaping.toInitialLowercase(newName)));
        }
    }

    void renameRegion(TextChange tfc, Region region, 
            Tree.CompilationUnit root) {
        tfc.addEdit(new ReplaceEdit(region.getOffset(), 
                region.getLength(), newName));
    }

    protected void renameNode(TextChange tfc, Node node, 
            Tree.CompilationUnit root) {
        Node identifyingNode = getIdentifyingNode(node);
        tfc.addEdit(new ReplaceEdit(identifyingNode.getStartIndex(), 
                identifyingNode.getText().length(), newName));
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
