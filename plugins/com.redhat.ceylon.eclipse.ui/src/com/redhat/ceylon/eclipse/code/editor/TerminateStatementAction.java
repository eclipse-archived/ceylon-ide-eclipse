package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIndexAtCharacter;
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
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

final class TerminateStatementAction extends Action {
	private final CeylonEditor editor;
	int line;
	
	private abstract class Processor extends Visitor implements NaturalVisitor {}

	TerminateStatementAction(CeylonEditor editor) {
		super(null);
		this.editor = editor;
	}
	
//	int count(String s, char c) {
//		int count=0;
//		for (int i=0; i<s.length(); i++) {
//			if (s.charAt(i)==c) count++;
//		}
//		return count;
//	}

	@Override
	public void run() {
		ITextSelection ts = (ITextSelection) editor.getSelectionProvider().getSelection();
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
//			IRegion li = editor.getCeylonSourceViewer().getDocument().getLineInformation(line);
//			editor.getCeylonSourceViewer().getTextWidget().setSelection(li.getOffset()+li.getLength());
			if (!editor.getSelectionText().equals(before)) {
	            //if the caret was at the end of the line, 
			    //and a semi was added, it winds up selected
			    //so move the caret after the semi
			    IRegion selection = editor.getSelection();
			    editor.getCeylonSourceViewer().setSelectedRange(selection.getOffset()+1,0);
			}
			
//			change = new DocumentChange("Terminate Statement", doc);
//			change.setEdit(new MultiTextEdit());
//			editor.getParseController().parse(doc, new NullProgressMonitor(), null);
//			terminateWithParen(doc, change);			
//			change.perform(new NullProgressMonitor());
			
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

//	private void terminateWithParen(final IDocument doc, final TextChange change) 
//			throws Exception {
//		CompilationUnit rootNode = parse();
//		IRegion li = getLineInfo(doc);
//		String lineText = doc.get(li.getOffset(), li.getLength());
//		final List<CommonToken> tokens = editor.getParseController().getTokens();
//		final int startOfCodeInLine = getCodeStart(li, lineText, tokens);
//		final int endOfCodeInLine = getCodeEnd(li, lineText, tokens);
//		new Visitor() {
//			@Override 
//			public void visit(Tree.Expression that) {
//				super.visit(that);
//				if (that.getStopIndex()<=endOfCodeInLine &&
//					that.getStartIndex()>=startOfCodeInLine) {
//					if (that.getToken().getType()==CeylonLexer.LPAREN &&
//						that.getEndToken().getType()!=CeylonLexer.RPAREN) {
//						change.addEdit(new InsertEdit(that.getStopIndex()+1, 
//								")"));
//					}
//					/*try {
//						String text = doc.get(that.getStartIndex(), 
//								that.getStopIndex()-that.getStartIndex()+1);
//						StringBuilder terminators = new StringBuilder();
//						for (int i=0; i<count(text, '(')-count(text,')'); i++) {
//							terminators.append(')');
//						}
//						if (terminators.length()!=0) {
//							change.addEdit(new InsertEdit(that.getStopIndex()+1, 
//									terminators.toString()));
//						}
//					}
//					catch (Exception e) {
//						e.printStackTrace();
//					}*/
//				}
//			}
//		}.visit(rootNode);
//	}

	private boolean terminateWithBrace() 
			throws Exception {
		IDocument doc = editor.getCeylonSourceViewer().getDocument();
		final TextChange change = new DocumentChange("Terminate Statement", doc);
		change.setEdit(new MultiTextEdit());
		CeylonParseController parser = parse();
		CompilationUnit rootNode = parser.getRootNode();
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
							change.addEdit(new InsertEdit(that.getStopIndex()+1, 
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
				initiate(that, CeylonLexer.LPAREN, "(");
				//does not really work right:
				terminate(that, CeylonLexer.RPAREN, ")");
			}
			@Override
			public void visit(Tree.ForIterator that) {
				super.visit(that);
				initiate(that, CeylonLexer.LPAREN, "(");
				//does not really work right:
				terminate(that, CeylonLexer.RPAREN, ")");
			}
			@Override 
			public void visit(Tree.ImportMemberOrTypeList that) {
				super.visit(that);
				terminate(that, CeylonLexer.RBRACE, " }");
			}
			@Override 
			public void visit(Tree.ImportModule that) {
				super.visit(that);
				terminate(that, CeylonLexer.SEMICOLON, ";");
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
			public void visit(Tree.Body that) {
				super.visit(that);
				terminate(that, CeylonLexer.RBRACE, " }");
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
			private void initiate(Node that, int tokenType, String ch) {
				if (that.getStartIndex()>=startOfCodeInLine &&
					that.getStartIndex()<=endOfCodeInLine) {
					Token et = that.getMainToken();
					if (et==null || et.getType()!=tokenType || 
							et.getText().startsWith("<missing ")) {
						if (!change.getEdit().hasChildren()) {
							change.addEdit(new InsertEdit(that.getStartIndex(), ch));
						}
					}
				}
			}
			private void terminate(Node that, int tokenType, String ch) {
				if (that.getStartIndex()>=startOfCodeInLine &&
					that.getStartIndex()<=endOfCodeInLine) {
					Token et = that.getMainEndToken();
					if ((et==null || et.getType()!=tokenType) ||
							that.getStopIndex()>endOfCodeInLine) {
						if (!change.getEdit().hasChildren()) {
							change.addEdit(new InsertEdit(min(endOfCodeInLine,that.getStopIndex())+1, ch));
						}
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
		CompilationUnit rootNode = parser.getRootNode();
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
				public void visit(Tree.StatementOrArgument that) {
					super.visit(that);
					if (that instanceof Tree.ExecutableStatement && 
							!(that instanceof Tree.ControlStatement) ||
							that instanceof Tree.AttributeDeclaration ||
							that instanceof Tree.MethodDeclaration ||
							that instanceof Tree.TypeAliasDeclaration ||
							that instanceof Tree.SpecifiedArgument) {
						terminateWithSemicolon(that);
					}
					if (that instanceof Tree.ClassDeclaration ||
                        that instanceof Tree.InterfaceDeclaration) {
					    terminateWithBaces(that);
					}
				}
                private void terminateWithBaces(Node that) {
                    try {
                        if (that.getStartIndex()<=endOfCodeInLine &&
                                that.getStopIndex()>=endOfCodeInLine) {
                            Token et = that.getEndToken();
                            if (et==null || et.getType()!=CeylonLexer.SEMICOLON ||
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
				        if (that.getStartIndex()<=endOfCodeInLine &&
				                that.getStopIndex()>=endOfCodeInLine) {
				            Token et = that.getEndToken();
				            if (et==null || et.getType()!=CeylonLexer.SEMICOLON ||
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
		int ti = getTokenIndexAtCharacter(tokens, offset);
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