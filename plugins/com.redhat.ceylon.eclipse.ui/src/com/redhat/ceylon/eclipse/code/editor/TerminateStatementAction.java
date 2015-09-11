package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getSelection;
import static java.lang.Math.min;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Nodes;

final class TerminateStatementAction extends Action {
    private final CeylonEditor editor;
    private int line;
    
    private abstract class Processor extends Visitor {}

    TerminateStatementAction(CeylonEditor editor) {
        super(null);
        this.editor = editor;
    }
    
//    int count(String s, char c) {
//        int count=0;
//        for (int i=0; i<s.length(); i++) {
//            if (s.charAt(i)==c) count++;
//        }
//        return count;
//    }

    @Override
    public void run() {
        ITextSelection ts = getSelection(editor);
        String before = editor.getSelectionText();
        line = ts.getEndLine();
        try {
            terminateWithSemicolon();
            boolean changed;
            int count=0;
            do {
                changed = terminateWithBrace();
                count++;
            } 
            while (changed&&count<5);
//            IRegion li = editor.getCeylonSourceViewer().getDocument().getLineInformation(line);
//            editor.getCeylonSourceViewer().getTextWidget().setSelection(li.getOffset()+li.getLength());
            if (!editor.getSelectionText().equals(before)) {
                //if the caret was at the end of the line, 
                //and a semi was added, it winds up selected
                //so move the caret after the semi
                IRegion selection = editor.getSelection();
                editor.getCeylonSourceViewer().setSelectedRange(selection.getOffset()+1,0);
            }
            
//            change = new DocumentChange("Terminate Statement", doc);
//            change.setEdit(new MultiTextEdit());
//            editor.getParseController().parse(doc, new NullProgressMonitor(), null);
//            terminateWithParen(doc, change);            
//            change.perform(new NullProgressMonitor());
            
            editor.scheduleParsing();
            
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCodeEnd(IRegion li, String lineText, List<CommonToken> tokens) {
        int j=lineText.length()-1;
        for (; j>=0; j--) {
            int offset = li.getOffset()+j;
            if (!skipToken(tokens, offset)) break;
        }
        int endOfCodeInLine = li.getOffset()+j;
        return endOfCodeInLine;
    }

    private int getCodeStart(IRegion li, String lineText,
            List<CommonToken> tokens) {
        int k=0;
        for (; k<lineText.length(); k++) {
            int offset = li.getOffset()+k;
            if (!skipToken(tokens, offset)) break;
        }
        int startOfCodeInLine = li.getOffset()+k;
        return startOfCodeInLine;
    }

//    private void terminateWithParen(final IDocument doc, final TextChange change) 
//            throws Exception {
//        CompilationUnit rootNode = parse();
//        IRegion li = getLineInfo(doc);
//        String lineText = doc.get(li.getOffset(), li.getLength());
//        final List<CommonToken> tokens = editor.getParseController().getTokens();
//        final int startOfCodeInLine = getCodeStart(li, lineText, tokens);
//        final int endOfCodeInLine = getCodeEnd(li, lineText, tokens);
//        new Visitor() {
//            @Override 
//            public void visit(Tree.Expression that) {
//                super.visit(that);
//                if (that.getStopIndex()<=endOfCodeInLine &&
//                    that.getStartIndex()>=startOfCodeInLine) {
//                    if (that.getToken().getType()==CeylonLexer.LPAREN &&
//                        that.getEndToken().getType()!=CeylonLexer.RPAREN) {
//                        change.addEdit(new InsertEdit(that.getStopIndex()+1, 
//                                ")"));
//                    }
//                    /*try {
//                        String text = doc.get(that.getStartIndex(), 
//                                that.getStopIndex()-that.getStartIndex()+1);
//                        StringBuilder terminators = new StringBuilder();
//                        for (int i=0; i<count(text, '(')-count(text,')'); i++) {
//                            terminators.append(')');
//                        }
//                        if (terminators.length()!=0) {
//                            change.addEdit(new InsertEdit(that.getStopIndex()+1, 
//                                    terminators.toString()));
//                        }
//                    }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }*/
//                }
//            }
//        }.visit(rootNode);
//    }

    private boolean terminateWithBrace() 
            throws Exception {
        IDocument doc = editor.getCeylonSourceViewer().getDocument();
        final TextChange change = new DocumentChange("Terminate Statement", doc);
        change.setEdit(new MultiTextEdit());
        CeylonParseController parser = parse();
        Tree.CompilationUnit rootNode = parser.getRootNode();
        IRegion li = getLineInfo(doc);
        final String lineText = doc.get(li.getOffset(), li.getLength());
        final List<CommonToken> tokens = parser.getTokens();
        final int startOfCodeInLine = getCodeStart(li, lineText, tokens);
        final int endOfCodeInLine = getCodeEnd(li, lineText, tokens);
        new Processor() {
            @Override 
            public void visit(Tree.Expression that) {
                super.visit(that);
                if (that.getStopIndex()<=endOfCodeInLine &&
                    that.getStartIndex()>=startOfCodeInLine) {
                    Token et = that.getMainEndToken();
                    Token st = that.getMainToken();
                    if (st!=null && st.getType()==CeylonLexer.LPAREN &&
                        (et==null || et.getType()!=CeylonLexer.RPAREN)) {
                        if (!change.getEdit().hasChildren()) {
                            change.addEdit(new InsertEdit(that.getEndIndex(), 
                                    ")"));
                        }
                    }
                }
            }
            @Override 
            public void visit(Tree.ParameterList that) {
                super.visit(that);
                terminate(that, CeylonLexer.RPAREN, ")");
            }
            public void visit(Tree.IndexExpression that) {
                super.visit(that);
                terminate(that, CeylonLexer.RBRACKET, "]");
            }
            @Override
            public void visit(Tree.TypeParameterList that) {
                super.visit(that);
                terminate(that, CeylonLexer.LARGER_OP, ">");
            }
            @Override
            public void visit(Tree.TypeArgumentList that) {
                super.visit(that);
                terminate(that, CeylonLexer.LARGER_OP, ">");
            }
            @Override 
            public void visit(Tree.PositionalArgumentList that) {
                super.visit(that);
                Token t = that.getToken();
                if (t!=null && t.getType()==CeylonLexer.LPAREN) { //for infix function syntax
                    terminate(that, CeylonLexer.RPAREN, ")");
                }
            }
            @Override 
            public void visit(Tree.NamedArgumentList that) {
                super.visit(that);
                terminate(that, CeylonLexer.RBRACE, " }");
            }
            @Override 
            public void visit(Tree.SequenceEnumeration that) {
                super.visit(that);
                terminate(that, CeylonLexer.RBRACE, " }");
            }
            @Override 
            public void visit(Tree.IterableType that) {
                super.visit(that);
                terminate(that, CeylonLexer.RBRACE, "}");
            }
            @Override 
            public void visit(Tree.Tuple that) {
                super.visit(that);
                terminate(that, CeylonLexer.RBRACKET, "]");
            }
            @Override 
            public void visit(Tree.TupleType that) {
                super.visit(that);
                terminate(that, CeylonLexer.RBRACKET, "]");
            }
            @Override
            public void visit(Tree.ConditionList that) {
                super.visit(that);
                if (!that.getMainToken().getText().startsWith("<missing ")) {
                    terminate(that, CeylonLexer.RPAREN, ")");
                }
            }
            @Override
            public void visit(Tree.ForIterator that) {
                super.visit(that);
                if (!that.getMainToken().getText().startsWith("<missing ")) {
                    terminate(that, CeylonLexer.RPAREN, ")");
                }
            }
            @Override 
            public void visit(Tree.ImportMemberOrTypeList that) {
                super.visit(that);
                terminate(that, CeylonLexer.RBRACE, " }");
            }
            @Override 
            public void visit(Tree.Import that) {
                if (that.getImportMemberOrTypeList()==null||
                        that.getImportMemberOrTypeList()
                                .getMainToken().getText().startsWith("<missing ")) {
                    if (!change.getEdit().hasChildren()) {
                        if (that.getImportPath()!=null &&
                                that.getImportPath().getStopIndex()<=endOfCodeInLine) {
                            change.addEdit(new InsertEdit(that.getImportPath().getEndIndex(), 
                                    " { ... }"));
                        }
                    }
                }
                super.visit(that);
            }
            @Override 
            public void visit(Tree.ImportModule that) {
                super.visit(that);
                if (that.getImportPath()!=null || 
                    that.getQuotedLiteral()!=null) {
                    terminate(that, CeylonLexer.SEMICOLON, ";");
                }
                if (that.getVersion()==null) {
                    if (!change.getEdit().hasChildren()) {
                        if (that.getImportPath()!=null &&
                                that.getImportPath().getStopIndex()<=endOfCodeInLine) {
                            change.addEdit(new InsertEdit(that.getImportPath().getEndIndex(), 
                                    " \"1.0.0\""));
                        }
                    }
                }
            }
            @Override 
            public void visit(Tree.ImportModuleList that) {
                super.visit(that);
                terminate(that, CeylonLexer.RBRACE, " }");
            }
            @Override 
            public void visit(Tree.PackageDescriptor that) {
                super.visit(that);
                terminate(that, CeylonLexer.SEMICOLON, ";");
            }
            @Override 
            public void visit(Tree.Directive that) {
                super.visit(that);
                terminate(that, CeylonLexer.SEMICOLON, ";");
            }
            @Override 
            public void visit(Tree.Body that) {
                super.visit(that);
                terminate(that, CeylonLexer.RBRACE, " }");
            }
            @Override 
            public void visit(Tree.MetaLiteral that) {
                super.visit(that);
                terminate(that, CeylonLexer.BACKTICK, "`");
            }
            @Override 
            public void visit(Tree.StatementOrArgument that) {
                super.visit(that);
                if (/*that instanceof Tree.ExecutableStatement && 
                        !(that instanceof Tree.ControlStatement) ||
                    that instanceof Tree.AttributeDeclaration ||
                    that instanceof Tree.MethodDeclaration ||
                    that instanceof Tree.ClassDeclaration ||
                    that instanceof Tree.InterfaceDeclaration ||*/
                    that instanceof Tree.SpecifiedArgument) {
                    terminate(that, CeylonLexer.SEMICOLON, ";");
                }
            }
            private boolean inLine(Node that) {
                return that.getStartIndex()>=startOfCodeInLine &&
                    that.getStartIndex()<=endOfCodeInLine;
            }
            /*private void initiate(Node that, int tokenType, String ch) {
                if (inLine(that)) {
                    Token mt = that.getMainToken();
                    if (mt==null || mt.getType()!=tokenType || 
                            mt.getText().startsWith("<missing ")) {
                        if (!change.getEdit().hasChildren()) {
                            change.addEdit(new InsertEdit(that.getStartIndex(), ch));
                        }
                    }
                }
            }*/
            private void terminate(Node that, int tokenType, String ch) {
                if (inLine(that)) {
                    Token et = that.getMainEndToken();
                    if ((et==null || et.getType()!=tokenType) ||
                            that.getStopIndex()>endOfCodeInLine) {
                        if (!change.getEdit().hasChildren()) {
                            change.addEdit(new InsertEdit(min(endOfCodeInLine,that.getStopIndex())+1, ch));
                        }
                    }
                }
            }
            @Override
            public void visit(Tree.ClassDeclaration that) {
                super.visit(that);
                if (inLine(that) && 
                        that.getParameterList()==null) {
                    if (!change.getEdit().hasChildren()) {
                        change.addEdit(new InsertEdit(that.getIdentifier().getEndIndex(), "()"));
                    }
                }
            }
            @Override
            public void visit(Tree.ClassDefinition that) {
                super.visit(that);
                if (inLine(that) && 
                        that.getParameterList()==null && 
                        that.getClassBody()!=null) {
                    /*for (Tree.Statement st: that.getClassBody().getStatements()) {
                        if (st instanceof Tree.Constructor) {
                            return;
                        }
                    }*/
                    if (!change.getEdit().hasChildren()) {
                        change.addEdit(new InsertEdit(that.getIdentifier().getEndIndex(), "()"));
                    }
                }
            }
            @Override
            public void visit(Tree.Constructor that) {
                super.visit(that);
                if (inLine(that) && 
                        that.getParameterList()==null && 
                        that.getBlock()!=null) {
                    if (!change.getEdit().hasChildren()) {
                        Tree.Identifier id = that.getIdentifier();
                        CommonToken tok = (CommonToken) (id==null ? that.getMainToken() : id.getToken());
                        change.addEdit(new InsertEdit(tok.getStopIndex()+1, "()"));
                    }
                }
            }
            @Override
            public void visit(Tree.AnyMethod that) {
                super.visit(that);
                if (inLine(that) && 
                        that.getParameterLists().isEmpty()) {
                    if (!change.getEdit().hasChildren()) {
                        change.addEdit(new InsertEdit(that.getIdentifier().getEndIndex(), "()"));
                    }
                }
            }
        }.visit(rootNode);
        if (change.getEdit().hasChildren()) {
            change.perform(new NullProgressMonitor());
            return true;
        }
        return false;
    }

    private boolean terminateWithSemicolon() 
            throws Exception {
        final IDocument doc = editor.getCeylonSourceViewer().getDocument();
        final TextChange change = new DocumentChange("Terminate Statement", doc);
        change.setEdit(new MultiTextEdit());
        CeylonParseController parser = parse();
        Tree.CompilationUnit rootNode = parser.getRootNode();
        IRegion li = getLineInfo(doc);
        String lineText = doc.get(li.getOffset(), li.getLength());
        final List<CommonToken> tokens = parser.getTokens();
        //final int startOfCodeInLine = getCodeStart(li, lineText, tokens);
        final int endOfCodeInLine = getCodeEnd(li, lineText, tokens);
        if (!doc.get(endOfCodeInLine,1).equals(";")) {
            new Processor() {
                @Override 
                public void visit(Tree.Annotation that) {
                    super.visit(that);
                    terminateWithSemicolon(that);
                }
                @Override 
                public void visit(Tree.StaticType that) {
                    super.visit(that);
                    terminateWithSemicolon(that);
                }
                @Override 
                public void visit(Tree.Expression that) {
                    super.visit(that);
                    terminateWithSemicolon(that);
                }
                boolean terminatedInLine(Node node) {
                    return node!=null &&
                            node.getStartIndex()<=endOfCodeInLine;
                }
                @Override 
                public void visit(Tree.IfClause that) {
                    super.visit(that);
                    if (missingBlock(that.getBlock()) &&
                            terminatedInLine(that.getConditionList())) {
                        terminateWithParenAndBaces(that, 
                                that.getConditionList());
                    }
                }
                @Override 
                public void visit(Tree.ElseClause that) {
                    super.visit(that);
                    if (missingBlock(that.getBlock())) {
                        terminateWithBaces(that);
                    }
                }
                @Override 
                public void visit(Tree.ForClause that) {
                    super.visit(that);
                    if (missingBlock(that.getBlock()) && 
                            terminatedInLine(that.getForIterator())) {
                        terminateWithParenAndBaces(that,
                                that.getForIterator());
                    }
                }
                @Override 
                public void visit(Tree.WhileClause that) {
                    super.visit(that);
                    if (missingBlock(that.getBlock()) && 
                            terminatedInLine(that.getConditionList())) {
                        terminateWithParenAndBaces(that, 
                                that.getConditionList());
                    }
                }
                @Override 
                public void visit(Tree.CaseClause that) {
                    super.visit(that);
                    if (missingBlock(that.getBlock()) && 
                            terminatedInLine(that.getCaseItem())) {
                        terminateWithParenAndBaces(that, that.getCaseItem());
                    }
                }
                @Override 
                public void visit(Tree.TryClause that) {
                    super.visit(that);
                    if (missingBlock(that.getBlock())) {
                        terminateWithBaces(that);
                    }
                }
                @Override 
                public void visit(Tree.CatchClause that) {
                    super.visit(that);
                    if (missingBlock(that.getBlock()) && 
                            terminatedInLine(that.getCatchVariable())) {
                        terminateWithParenAndBaces(that,
                                that.getCatchVariable());
                    }
                }
                @Override 
                public void visit(Tree.FinallyClause that) {
                    super.visit(that);
                    if (missingBlock(that.getBlock())) {
                        terminateWithBaces(that);
                    }
                }
                @Override 
                public void visit(Tree.StatementOrArgument that) {
                    if (that instanceof Tree.ExecutableStatement && 
                            !(that instanceof Tree.ControlStatement) ||
                            that instanceof Tree.AttributeDeclaration ||
                            that instanceof Tree.ImportModule ||
                            that instanceof Tree.TypeAliasDeclaration ||
                            that instanceof Tree.SpecifiedArgument) {
                        terminateWithSemicolon(that);
                    }
                    
                    if (that instanceof Tree.MethodDeclaration) {
                        Tree.MethodDeclaration md = (Tree.MethodDeclaration) that;
                        if (md.getSpecifierExpression()==null) {
                            List<Tree.ParameterList> pl = md.getParameterLists();
                            if (md.getIdentifier()!=null && terminatedInLine(md.getIdentifier())) {
                                terminateWithParenAndBaces(that, pl.isEmpty() ? null : pl.get(pl.size()-1));
                            }
                        }
                        else {
                            terminateWithSemicolon(that);
                        }
                    }
                    if (that instanceof Tree.ClassDeclaration) {
                        Tree.ClassDeclaration cd = (Tree.ClassDeclaration) that;
                        if (cd.getClassSpecifier()==null) {
                            terminateWithParenAndBaces(that, cd.getParameterList());
                        }
                        else {
                            terminateWithSemicolon(that);
                        }
                    }
                    if (that instanceof Tree.InterfaceDeclaration) {
                        Tree.InterfaceDeclaration id = (Tree.InterfaceDeclaration) that;
                        if (id.getTypeSpecifier()==null) {
                            terminateWithBaces(that);
                        }
                        else {
                            terminateWithSemicolon(that);
                        }
                    }
                    super.visit(that);
                }
                private void terminateWithParenAndBaces(Node that, Node subnode) {
                    try {
                        if (withinLine(that)) {
                            if (subnode==null || 
                                    subnode.getStartIndex()>endOfCodeInLine) {
                                if (!change.getEdit().hasChildren()) {
                                    change.addEdit(new InsertEdit(endOfCodeInLine+1, "() {}"));
                                }
                            }
                            else {
                                Token et = that.getEndToken();
                                Token set = subnode.getEndToken();
                                if (set==null || 
                                        set.getType()!=CeylonLexer.RPAREN ||
                                        subnode.getStopIndex()>endOfCodeInLine) {
                                    if (!change.getEdit().hasChildren()) {
                                        change.addEdit(new InsertEdit(endOfCodeInLine+1, ") {}"));
                                    }
                                }
                                else if (et==null || 
                                        et.getType()!=CeylonLexer.RBRACE ||
                                        that.getStopIndex()>endOfCodeInLine) {
                                    if (!change.getEdit().hasChildren()) {
                                        change.addEdit(new InsertEdit(endOfCodeInLine+1, " {}"));
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                private void terminateWithBaces(Node that) {
                    try {
                        if (withinLine(that)) {
                            Token et = that.getEndToken();
                            if (et==null || 
                                    et.getType()!=CeylonLexer.SEMICOLON &&
                                    et.getType()!=CeylonLexer.RBRACE ||
                                    that.getStopIndex()>endOfCodeInLine) {
                                if (!change.getEdit().hasChildren()) {
                                    change.addEdit(new InsertEdit(endOfCodeInLine+1, " {}"));
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                private void terminateWithSemicolon(Node that) {
                    try {
                        if (withinLine(that)) {
                            Token et = that.getEndToken();
                            if (et==null || 
                                    et.getType()!=CeylonLexer.SEMICOLON ||
                                    that.getStopIndex()>endOfCodeInLine) {
                                if (!change.getEdit().hasChildren()) {
                                    change.addEdit(new InsertEdit(endOfCodeInLine+1, ";"));
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                boolean withinLine(Node that) {
                    return that.getStartIndex()!=null &&
                        that.getStopIndex()!=null &&
                            that.getStartIndex()<=endOfCodeInLine &&
                            that.getStopIndex()>=endOfCodeInLine;
                }
                protected boolean missingBlock(Tree.Block block) {
                    return block==null || block.getMainToken()==null || 
                            block.getMainToken()
                                .getText().startsWith("<missing");
                }
            }.visit(rootNode);
            if (change.getEdit().hasChildren()) {
                change.perform(new NullProgressMonitor());
                return true;
            }
        }
        return false;
    }

    private IRegion getLineInfo(final IDocument doc)
            throws BadLocationException {
        return doc.getLineInformation(line);
    }

    private boolean skipToken(List<CommonToken> tokens, int offset) {
        int ti = Nodes.getTokenIndexAtCharacter(tokens, offset);
        if (ti<0) ti=-ti;
        int type = tokens.get(ti).getType();
        return type==CeylonLexer.WS ||
                type==CeylonLexer.MULTI_COMMENT ||
                type==CeylonLexer.LINE_COMMENT;
    }

    private CeylonParseController parse() {
        CeylonParseController cpc = new CeylonParseController();
        cpc.initialize(editor.getParseController().getPath(), 
                editor.getParseController().getProject(), null);
        cpc.parse(editor.getCeylonSourceViewer().getDocument(), 
                new NullProgressMonitor(), null);
        return cpc;
    }

}