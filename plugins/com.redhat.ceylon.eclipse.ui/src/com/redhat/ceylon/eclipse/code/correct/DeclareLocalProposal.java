package com.redhat.ceylon.eclipse.code.correct;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.ide.common.correct.QuickFixData;
import com.redhat.ceylon.ide.common.correct.declareLocalQuickFix_;

final class DeclareLocalProposal extends CorrectionProposal {
    
    private final QuickFixData data;
    private final Term term;
    private final CompilationUnit rootNode;
    private final CeylonEditor editor;
    private final Tree.BaseMemberExpression bme;
    
    DeclareLocalProposal(QuickFixData data,
            Change change,
            String desc,
            Tree.Term term,
            Tree.BaseMemberExpression bme, 
            Tree.CompilationUnit rootNode, 
            CeylonEditor editor) {
        super(desc, change, null);
        this.data = data;
        this.term = term;
        this.rootNode = rootNode;
        this.editor = editor;
        this.bme = bme;
    }

    public void apply(IDocument document) {
        super.apply(document);
        declareLocalQuickFix_.get_().enableLinkedMode(data, term);
//        Type type = term.getTypeModel();
//        if (type!=null && editor!=null) {
//            LinkedModeModel linkedModeModel = new LinkedModeModel();
//            ProposalPosition typePosition = 
//                    getTypeProposals(document, bme.getStartIndex(), 5, 
//                            type, rootNode, "value");
//
//            try {
//                LinkedMode.addLinkedPosition(linkedModeModel, typePosition);
//                LinkedMode.installLinkedMode(editor, document, linkedModeModel, 
//                        this, new DeleteBlockingExitPolicy(document), NO_STOP, -1);
//            } 
//            catch (BadLocationException ble) {
//                ble.printStackTrace();
//            }
//        }
    }
    
//  @Deprecated
//  DeclareLocalProposal(Change change,
//          Tree.Term term,
//          Tree.BaseMemberExpression bme, 
//          Tree.CompilationUnit rootNode, 
//          CeylonEditor editor) {
//      super("Declare local value '" + bme.getIdentifier().getText() + "'", 
//              change, null);
//      this.term = term;
//      this.rootNode = rootNode;
//      this.editor = editor;
//      this.bme = bme;
//  }

//
//    static void addDeclareLocalProposal(Tree.CompilationUnit rootNode, 
//            Node node, Collection<ICompletionProposal> proposals, 
//            IFile file, CeylonEditor editor) {
//        Tree.Statement st = Nodes.findStatement(rootNode, node);
//        if (st instanceof Tree.SpecifierStatement) {
//            Tree.SpecifierStatement sst = (Tree.SpecifierStatement) st;
//            Tree.SpecifierExpression se = sst.getSpecifierExpression();
//            Tree.Term bme = sst.getBaseMemberExpression();
//            if (bme==node &&
//                    bme instanceof Tree.BaseMemberExpression) {
//                Tree.Expression e = se.getExpression();
//                if (e!=null && e.getTerm()!=null) {
//                    final Tree.Term term = e.getTerm();
//                    TextFileChange change = 
//                            new TextFileChange("Declare Local Value", file);
//                    change.setEdit(new InsertEdit(node.getStartIndex(), "value "));
//                    proposals.add(new DeclareLocalProposal(change,
//                            term, (Tree.BaseMemberExpression) node,
//                            rootNode, editor));
//                }
//            }
//        }
//    }
}