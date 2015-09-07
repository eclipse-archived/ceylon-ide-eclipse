package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;

import java.io.IOException;
import java.io.StringWriter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.eclipse.code.refactor.AbstractHandler;
import com.redhat.ceylon.eclipse.core.model.modelJ2C;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;

import ceylon.tool.converter.java2ceylon.Java8Lexer;
import ceylon.tool.converter.java2ceylon.Java8Parser;
import ceylon.tool.converter.java2ceylon.JavaToCeylonConverter;

public class PasteAsCeylonHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		String ceylonCode = "<error>";
		Clipboard cb = new Clipboard(null);
		String javaCode = (String) cb.getContents(TextTransfer.getInstance());

		try {
		    IProject project = ((CeylonEditor) getCurrentEditor()).getParseController().getProject();
		    CeylonIdeConfig<IProject> ideConfig = modelJ2C.ceylonModel().getProject(project).getIdeConfiguration();
			ceylonCode = transformJavaToCeylon(javaCode, ideConfig);
		} catch (IOException e) {
			CeylonPlugin.log(IStatus.ERROR, "Could not transform Java code", e);
		}

		insertTextInEditor(ceylonCode);

		return null;
	}

	private String transformJavaToCeylon(String javaCode, CeylonIdeConfig<IProject> ideConfig) throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(javaCode);
		Java8Lexer lexer = new Java8Lexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Java8Parser parser = new Java8Parser(tokens);
		ParserRuleContext tree = parser.compilationUnit();

		StringWriter out = new StringWriter();
		
		JavaToCeylonConverter converter = new JavaToCeylonConverter(out, 
		        ideConfig.getConverterConfig().getTransformGetters(),
		        ideConfig.getConverterConfig().getUseVariableInParameters(),
		        ideConfig.getConverterConfig().getUseVariableInLocals(),
		        ideConfig.getConverterConfig().getUseValues());
		
		ideConfig.save();
		tree.accept(converter);

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
			CeylonPlugin.log(IStatus.ERROR, "Could not paste transformed code", e);
		}

		FormatAction.format(editor.getParseController(), doc, EditorUtil.getSelection(editor), false,
				editor.getSelectionProvider());
	}

	@Override
	protected boolean isEnabled(CeylonEditor editor) {
		return true;
	}

}
