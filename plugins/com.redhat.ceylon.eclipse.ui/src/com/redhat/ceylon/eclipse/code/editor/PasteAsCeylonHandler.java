package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.text.edits.ReplaceEdit;

import ceylon.converter.java.Java8Lexer;
import ceylon.converter.java.Java8Parser;
import ceylon.converter.java.JavaToCeylonConverter;
import com.redhat.ceylon.eclipse.code.refactor.AbstractHandler;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class PasteAsCeylonHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		String ceylonCode = "<error>";
		Clipboard cb = new Clipboard(null);
		String javaCode = (String) cb.getContents(TextTransfer.getInstance());

		try {
			ceylonCode = transformJavaToCeylon(javaCode);
		} catch (IOException e) {
			e.printStackTrace();
		}

		insertTextInEditor(ceylonCode);

		return null;
	}

	private String transformJavaToCeylon(String javaCode) throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(javaCode);
		Java8Lexer lexer = new Java8Lexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Java8Parser parser = new Java8Parser(tokens);
		ParserRuleContext tree = parser.compilationUnit();

		StringWriter out = new StringWriter();
		BufferedWriter bw = new BufferedWriter(out);
		JavaToCeylonConverter converter = new JavaToCeylonConverter(bw);

		ParseTreeWalker.DEFAULT.walk(converter, tree);
		converter.close();

		String ceylonCode = out.toString();

		if (ceylonCode.equals("")) {
			return javaCode;
		}

		return ceylonCode;
	}

	private void insertTextInEditor(String ceylonCode) {
		CeylonEditor editor = (CeylonEditor) getCurrentEditor();
		IDocument doc = editor.getCeylonSourceViewer().getDocument();

		int offset = 0;

		offset = EditorUtil.getSelection(editor).getOffset();

		DocumentChange change = new DocumentChange("Paste Java as Ceylon", doc);
		change.setEdit(new ReplaceEdit(offset, 0, ceylonCode));

		try {
			change.perform(new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}

		FormatAction.format(editor.getParseController(), doc, EditorUtil.getSelection(editor), false,
				editor.getSelectionProvider());
	}

	@Override
	protected boolean isEnabled(CeylonEditor editor) {
		return true;
	}

}
