package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.getProposals;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importEdits;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.isImported;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.MINOR_CHANGE;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getOccurrenceLocation;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.IMPORT;
import static java.lang.Character.isUpperCase;
import static java.util.Collections.singleton;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.util.NormalizedLevenshtein;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.OccurrenceLocation;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.NamedArgumentList;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Scope;

class ChangeReferenceProposal extends CorrectionProposal {
    
    private ChangeReferenceProposal(ProblemLocation problem, 
            String name, String pkg, TextFileChange change) {
        super("Change reference to '" + name + "'" + pkg, 
                change, 
                new Region(problem.getOffset(), 
                        name.length()), 
                MINOR_CHANGE);
    }
    
    static void addChangeReferenceProposal(
            ProblemLocation problem,
            Collection<ICompletionProposal> proposals, 
            IFile file, 
            String brokenName, 
            Declaration dec,
            Tree.CompilationUnit rootNode) {
        TextFileChange change = 
                new TextFileChange("Change Reference", 
                        file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        String pkg = "";
        int problemOffset = problem.getOffset();
        if (dec.isToplevel() && 
                !isImported(dec, rootNode) && 
                isInPackage(rootNode, dec)) {
            String pn = 
                    dec.getContainer()
                        .getQualifiedNameString();
            pkg = " in '" + pn + "'";
            if (!pn.isEmpty() && 
                    !pn.equals(Module.LANGUAGE_MODULE_NAME)) {
                OccurrenceLocation ol = 
                        getOccurrenceLocation(rootNode, 
                                findNode(rootNode, problemOffset),
                                problemOffset);
                if (ol!=IMPORT) {
                    List<InsertEdit> ies = 
                            importEdits(rootNode, 
                                    singleton(dec), 
                                    null, null, doc);
                    for (InsertEdit ie: ies) {
                        change.addEdit(ie);
                    }
                }
            }
        }
        change.addEdit(new ReplaceEdit(problemOffset, 
                brokenName.length(), dec.getName())); //Note: don't use problem.getLength() because it's wrong from the problem list
        proposals.add(new ChangeReferenceProposal(problem, 
                dec.getName(), pkg, change));
    }

    protected static boolean isInPackage(
            Tree.CompilationUnit cu, Declaration dec) {
        return !dec.getUnit().getPackage()
                .equals(cu.getUnit().getPackage());
    }
    
    static void addChangeReferenceProposals(
            Tree.CompilationUnit rootNode, 
            Node node, ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IFile file) {
        Node id = getIdentifyingNode(node);
        if (id!=null) {
            String brokenName = id.getText();
            if (brokenName!=null && 
                    !brokenName.isEmpty()) {
                Scope scope = node.getScope();
                Collection<DeclarationWithProximity> dwps = 
                        getProposals(node, scope, rootNode, null)
                            .values();
                for (DeclarationWithProximity dwp: dwps) {
                    processProposal(rootNode, problem, 
                            proposals, file,
                            brokenName, 
                            dwp.getDeclaration());
                }
            }
        }
    }

    static void addChangeArgumentReferenceProposals(
            Tree.CompilationUnit rootNode, 
            Node node, 
            ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IFile file) {
        String brokenName = 
                getIdentifyingNode(node)
                    .getText();
        if (brokenName!=null && 
                !brokenName.isEmpty()) {
            if (node instanceof Tree.NamedArgument) {
                Scope scope = node.getScope();
                if (!(scope instanceof NamedArgumentList)) {
                    scope = scope.getScope(); //for declaration-style named args
                }
                NamedArgumentList namedArgumentList = 
                        (NamedArgumentList) scope;
                ParameterList parameterList = 
                        namedArgumentList.getParameterList();
                if (parameterList!=null) {
                    for (Parameter parameter: 
                            parameterList.getParameters()) {
                        Declaration declaration = 
                                parameter.getModel();
                        if (declaration!=null) {
                            processProposal(rootNode, problem, 
                                    proposals, file,
                                    brokenName, 
                                    declaration);
                        }
                    }
                }
            }
        }
    }

    private static void processProposal(
            Tree.CompilationUnit rootNode,
            ProblemLocation problem, 
            Collection<ICompletionProposal> proposals,
            IFile file, 
            String brokenName, 
            Declaration declaration) {
        String name = declaration.getName();
        if (!brokenName.equals(name)) {
            boolean nuc = 
                    isUpperCase(name.codePointAt(0));
            boolean bnuc = 
                    isUpperCase(brokenName.codePointAt(0));
            if (nuc==bnuc) {
                double similarity = 
                        distance.similarity(brokenName, name);
                //TODO: would it be better to just sort by distance,
                //      and then select the 3 closest possibilities?
                if (similarity > 0.6) {
                    addChangeReferenceProposal(problem, 
                            proposals, file, 
                            brokenName, declaration, 
                            rootNode);
                }
            }
        }
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), true);
    }
    
    static final NormalizedLevenshtein distance = new NormalizedLevenshtein();
    
}