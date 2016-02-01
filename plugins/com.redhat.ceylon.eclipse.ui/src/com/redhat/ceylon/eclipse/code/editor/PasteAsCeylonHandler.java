package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getSelection;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.*;


import java.io.IOException;
import java.io.StringWriter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
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
import ceylon.tool.converter.java2ceylon.ScopeTree;

public class PasteAsCeylonHandler extends AbstractHandler {

	private static final class Transformer 
	        implements IRunnableWithProgress {
        private final IProject project;
        private final String javaCode;
        private String ceylonCode;

        private Transformer(IProject project, String javaCode) {
            this.project = project;
            this.javaCode = javaCode;
        }
        
        public String getCeylonCode() {
            return ceylonCode;
        }

        @Override
        public void run(IProgressMonitor monitor) {
            monitor.beginTask("Converting Java to Ceylon", -1);
            try {
                CeylonIdeConfig ideConfig = 
                        modelJ2C().ceylonModel()
                            .getProject(project)
                            .getIdeConfiguration();
                ceylonCode = 
                        transformJavaToCeylon(javaCode, 
                                ideConfig);
            }
            catch (IOException e) {
                CeylonPlugin.log(IStatus.ERROR, 
                        "Could not transform Java code", e);
            }
            monitor.done();
        }
    }

    @Override
	public Object execute(ExecutionEvent event) 
	        throws ExecutionException {
        
		Clipboard cb = new Clipboard(null);
		final String javaCode = (String) 
		        cb.getContents(TextTransfer.getInstance());

        CeylonEditor editor = 
                (CeylonEditor) 
                    getCurrentEditor();
        final IProject project = 
                editor.getParseController()
                    .getProject();
        String ceylonCode;
        try {
            Transformer transformer = 
                    new Transformer(project, javaCode);
            editor.getEditorSite()
                .getWorkbenchWindow()
                .run(true, true, transformer);
            ceylonCode = transformer.getCeylonCode();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

		ceylonCode = format(ceylonCode, editor);
		insertTextInEditor(ceylonCode, editor);

		return null;
	}

	private static String transformJavaToCeylon(
	        String javaCode, 
	        CeylonIdeConfig ideConfig) 
	                throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(javaCode);
		Java8Lexer lexer = new Java8Lexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Java8Parser parser = new Java8Parser(tokens);
		ParserRuleContext tree = parser.compilationUnit();

		StringWriter out = new StringWriter();
		
		JavaToCeylonConverterConfig config = 
		        ideConfig.getConverterConfig();
		
        ScopeTree scopeTree = new ScopeTree();
        tree.accept(scopeTree);
		
        JavaToCeylonConverter converter = 
                new JavaToCeylonConverter(out, 
    		        config.getTransformGetters(),
    		        config.getUseVariableInParameters(),
    		        config.getUseVariableInLocals(),
    		        config.getUseValues(), scopeTree);
		
		ideConfig.save();
		tree.accept(converter);

		String ceylonCode = out.toString();

		if (ceylonCode.equals("")) {
			return javaCode;
		}
		else {
		    return ceylonCode;
		}
	}

	private void insertTextInEditor(String ceylonCode,
	        CeylonEditor editor) {

	    ITextSelection selection = getSelection(editor);
	    if (selection==null) {
	        selection = new TextSelection(0, 0);
	    }

        IDocument doc = 
                editor.getCeylonSourceViewer()
                    .getDocument();
	    DocumentChange change = 
	            new DocumentChange("Paste Java as Ceylon", 
	                    doc);
	    change.setEdit(new ReplaceEdit(
	            selection.getOffset(), 
	            selection.getLength(), 
	            ceylonCode));

	    try {
	        change.perform(new NullProgressMonitor());
	        editor.getSelectionProvider().setSelection(
	                new TextSelection(
	                        selection.getOffset() 
	                            + ceylonCode.length(), 
	                        0));
	    } catch (CoreException e) {
	        CeylonPlugin.log(IStatus.ERROR, 
	                "Could not paste transformed code", e);
	    }
	}

    private String format(String ceylonCode, 
            CeylonEditor editor) {
        IProject project = 
                editor.getParseController()
                    .getProject();
        IDocument doc = 
                editor.getCeylonSourceViewer()
                    .getDocument();
        
        // get initial indentation
        int indentation = 0;
        ITextSelection selection = getSelection(editor);
        if (selection!=null) {
            try {
                IRegion region =
                		doc.getLineInformation(
                				selection.getStartLine());
                String line =
                		doc.get(region.getOffset(),
                				region.getLength());
                char[] chars = line.toCharArray();
                loop: for (int i=0; i<chars.length; i++) {
                    switch (chars[i]) {
                    case '\t':
                        indentation += utilJ2C().indents().getIndentSpaces();
                        break;
                    case ' ':
                        indentation += 1;
                        break;
                    default:
                        break loop;
                    }
                }
                indentation /= utilJ2C().indents().getIndentSpaces();
            } catch (BadLocationException e) {
                indentation = 0;
            }
        }
        
        StringBuilder builder = 
                new StringBuilder(ceylonCode.length());
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
	                //new BufferedTokenStream(lexer),
	                null, indentation);
        } catch (Exception e) {
            CeylonPlugin.log(IStatus.ERROR, "Error during converted code formatting", e);
            return ceylonCode;
        } catch (AssertionError e) {
            CeylonPlugin.log(IStatus.ERROR, "Error during converted code formatting", e);
            return ceylonCode;
        }
        return builder.toString();
    }

	@Override
	protected boolean isEnabled(CeylonEditor editor) {
		return true;
	}

}
