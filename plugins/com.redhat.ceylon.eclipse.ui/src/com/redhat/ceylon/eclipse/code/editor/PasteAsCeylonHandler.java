package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getSelection;

import java.io.IOException;
import java.io.StringWriter;

import org.antlr.runtime.BufferedTokenStream;
import org.antlr.runtime.RecognitionException;
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
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.util.NewlineFixingStringStream;
import com.redhat.ceylon.eclipse.code.refactor.AbstractHandler;
import com.redhat.ceylon.eclipse.code.style.CeylonStyle;
import com.redhat.ceylon.eclipse.core.model.modelJ2C;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.StringBuilderWriter;
import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;
import com.redhat.ceylon.ide.common.model.JavaToCeylonConverterConfig;

import ceylon.formatter.format_;
import ceylon.formatter.options.SparseFormattingOptions;
import ceylon.formatter.options.combinedOptions_;
import ceylon.formatter.options.loadProfile_;
import ceylon.language.Singleton;
import ceylon.tool.converter.java2ceylon.Java8Lexer;
import ceylon.tool.converter.java2ceylon.Java8Parser;
import ceylon.tool.converter.java2ceylon.JavaToCeylonConverter;

public class PasteAsCeylonHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) 
	        throws ExecutionException {
		String ceylonCode = "<error>";
		Clipboard cb = new Clipboard(null);
		String javaCode = (String) 
		        cb.getContents(TextTransfer.getInstance());

		try {
		    CeylonEditor editor = 
		            (CeylonEditor) 
		                getCurrentEditor();
            IProject project = 
                    editor.getParseController()
                        .getProject();
		    CeylonIdeConfig<IProject> ideConfig = 
		            modelJ2C.ceylonModel()
		                .getProject(project)
		                .getIdeConfiguration();
			ceylonCode = 
			        transformJavaToCeylon(javaCode, ideConfig);
		} catch (IOException e) {
			CeylonPlugin.log(IStatus.ERROR, 
			        "Could not transform Java code", e);
		}

		insertTextInEditor(ceylonCode);

		return null;
	}

	private String transformJavaToCeylon(String javaCode, 
	        CeylonIdeConfig<IProject> ideConfig) 
	                throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(javaCode);
		Java8Lexer lexer = new Java8Lexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Java8Parser parser = new Java8Parser(tokens);
		ParserRuleContext tree = parser.compilationUnit();

		StringWriter out = new StringWriter();
		
		JavaToCeylonConverterConfig config = 
		        ideConfig.getConverterConfig();
        JavaToCeylonConverter converter = 
                new JavaToCeylonConverter(out, 
    		        config.getTransformGetters(),
    		        config.getUseVariableInParameters(),
    		        config.getUseVariableInLocals(),
    		        config.getUseValues());
		
		ideConfig.save();
		tree.accept(converter);

		String ceylonCode = out.toString();

		if (ceylonCode.equals("")) {
			return javaCode;
		}

		return ceylonCode;
	}

	private void insertTextInEditor(String ceylonCode) {
	    CeylonEditor editor = 
	            (CeylonEditor) 
	            getCurrentEditor();
	    IDocument doc = 
	            editor.getCeylonSourceViewer()
	            .getDocument();

	    IProject project = editor.getParseController().getProject();

	    StringBuilder builder = new StringBuilder(ceylonCode.length());
	    NewlineFixingStringStream stream = 
	            new NewlineFixingStringStream(ceylonCode + " ");
	    CeylonLexer lexer = new CeylonLexer(stream);
	    org.antlr.runtime.CommonTokenStream tokenStream = 
	            new org.antlr.runtime.CommonTokenStream(lexer);
	    tokenStream.fill();
	    CeylonParser ceylonParser = new CeylonParser(tokenStream);
	    try {
	        format_.format(
	                ceylonParser.compilationUnit(),
	                combinedOptions_.combinedOptions(
	                        loadProfile_.loadProfile(
	                                CeylonStyle.getFormatterProfile(project),
	                                /* inherit = */ false,
	                                /* baseDir = */ project.getLocation().toOSString()),
	                        new Singleton<SparseFormattingOptions>
	                        (SparseFormattingOptions.$TypeDescriptor$, 
	                                CeylonStyle.getEclipseWsOptions(doc))),
	                new StringBuilderWriter(builder),
	                new BufferedTokenStream(lexer),
	                0);
	    }
	    catch (RecognitionException e1) {
	        e1.printStackTrace();
	    }

	    ITextSelection selection = getSelection(editor);
	    if (selection==null) {
	        selection = new TextSelection(0, 0);
	    }

	    DocumentChange change = 
	            new DocumentChange("Paste Java as Ceylon", 
	                    doc);
	    change.setEdit(new ReplaceEdit(
	            selection.getOffset(), 
	            selection.getLength(), 
	            builder.toString()));

	    try {
	        change.perform(new NullProgressMonitor());
	        editor.getSelectionProvider().setSelection(
	                new TextSelection(
	                        selection.getOffset() 
	                        + builder.length(), 
	                        0));
	    } catch (CoreException e) {
	        CeylonPlugin.log(IStatus.ERROR, 
	                "Could not paste transformed code", e);
	    }
	}

	@Override
	protected boolean isEnabled(CeylonEditor editor) {
		return true;
	}

}
