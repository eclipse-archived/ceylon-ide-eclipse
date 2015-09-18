package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.java.codegen.CodegenUtil.getJavaNameOfDeclaration;
import static com.redhat.ceylon.eclipse.util.DocLinks.nameRegion;
import static com.redhat.ceylon.eclipse.util.Escaping.toInitialLowercase;
import static com.redhat.ceylon.eclipse.util.Escaping.toInitialUppercase;
import static com.redhat.ceylon.eclipse.util.JavaSearch.createSearchPattern;
import static com.redhat.ceylon.eclipse.util.JavaSearch.getProjectAndReferencingProjects;
import static com.redhat.ceylon.eclipse.util.JavaSearch.runSearch;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedExplicitDeclaration;
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
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Value;

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
                inSameProject(declaration);
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
        else if (Escaping.KEYWORDS.contains(newName)) {
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
        List<PhasedUnit> units = getAllUnits();
        pm.beginTask(getName(), units.size());
        CompositeChange composite = 
                new CompositeChange(getName());
        int i=0;
        for (PhasedUnit pu: units) {
            if (searchInFile(pu)) {
                TextFileChange tfc = newTextFileChange((ProjectPhasedUnit) pu);
                renameInFile(tfc, composite, 
                        pu.getCompilationUnit());
                pm.worked(i++);
            }
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            CompilationUnit editorRootNode = 
                    editor.getParseController()
                        .getLastCompilationUnit();
            renameInFile(dc, composite, editorRootNode);
            pm.worked(i++);
        }
        if (project!=null && renameFile) {
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
                composite.add(new RenameResourceChange(
                        oldPath, newFileName));
            }
        }
        
        refactorJavaReferences(pm, composite);

        pm.done();
        return composite;
    }

    private void refactorJavaReferences(IProgressMonitor pm,
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
                                    toInitialUppercase(newName)));
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
    
    private void renameInFile(
            TextChange tfc, CompositeChange cc, 
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

    void renameIdentifier(TextChange tfc, 
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
                    toInitialLowercase(newName)));
        }
    }

    void renameRegion(TextChange tfc, Region region, 
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

    static Node getIdentifier(Node node) {
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
