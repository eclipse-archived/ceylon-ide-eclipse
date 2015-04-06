package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.typechecker.tree.AnalysisMessage;
import com.redhat.ceylon.compiler.typechecker.tree.Node;

//This is bullshit. The IDE should have a nice way of converting
//compilation units into objects that can be shown in the problems
//view, and even a nice way of converting errors in nodes in the
//typechecker into errors that can be shown in the problems view.
//Right now I'm using this for the js compiler errors but I expect
//to see other people wanting this and probably implementing their own
//solution again.
public class CeylonCompilationError implements Diagnostic<JavaFileObject> {

    private final AnalysisMessage err;
    private final IProject project;
    private final JavaFileObject jf;
    private final IFile file;

    public CeylonCompilationError(IProject proj, AnalysisMessage error) {
        err = error;
        this.project = proj;
        file = project.getFile(err.getTreeNode().getUnit().getFullPath());
        jf = new JavaFileObject() {
            @Override
            public URI toUri() {
                try {
                    return new URI(file.getFullPath().toString());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return null;
            }
            
            @Override
            public Writer openWriter() throws IOException {
                return null;
            }
            
            @Override
            public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
                return null;
            }
            
            @Override
            public OutputStream openOutputStream() throws IOException {
                return null;
            }
            
            @Override
            public InputStream openInputStream() throws IOException {
                return null;
            }
            
            @Override
            public String getName() {
                return file.getLocation().toOSString();
            }
            
            @Override
            public long getLastModified() {
                return file.getModificationStamp();
            }
            
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors)
                    throws IOException {
                return null;
            }
            
            @Override
            public boolean delete() {
                return false;
            }
            
            @Override
            public boolean isNameCompatible(String simpleName, Kind kind) {
                return false;
            }
            
            @Override
            public NestingKind getNestingKind() {
                return NestingKind.TOP_LEVEL;
            }
            
            @Override
            public Kind getKind() {
                return Kind.SOURCE;
            }
            
            @Override
            public Modifier getAccessLevel() {
                return Modifier.FINAL;
            }
        };
    }

    @Override
    public javax.tools.Diagnostic.Kind getKind() {
        return Diagnostic.Kind.ERROR;
    }

    @Override
    public JavaFileObject getSource() {
        return jf;
    }
    
    @Override
    public long getPosition() {
        return getStartPosition();
    }

    @Override
    public long getStartPosition() {
        int startOffset = 0;
        Node errorNode = 
                getIdentifyingNode(err.getTreeNode());
        if (errorNode == null) {
            errorNode = err.getTreeNode();
        }
        Token token = errorNode.getToken();
        if (token!=null) {
            startOffset = errorNode.getStartIndex();
        }
        return startOffset;
    }

    @Override
    public long getEndPosition() {
        int endOffset = 0;
        Node errorNode = 
                getIdentifyingNode(err.getTreeNode());
        if (errorNode == null) {
            errorNode = err.getTreeNode();
        }
        Token token = errorNode.getToken();
        if (token!=null) {
            endOffset = errorNode.getStopIndex()+1;
        }
        return endOffset;
    }

    @Override
    public long getLineNumber() {
        return err.getLine();
    }

    @Override
    public long getColumnNumber() {
        int startCol = 0;
        Node errorNode = 
                getIdentifyingNode(err.getTreeNode());
        if (errorNode == null) {
            errorNode = err.getTreeNode();
        }
        Token token = errorNode.getToken();
        if (token!=null) {
            startCol = token.getCharPositionInLine();
        }
        return startCol;
    }

    @Override
    public String getCode() {
        return String.valueOf(err.getCode());
    }

    @Override
    public String getMessage(Locale locale) {
        return err.getMessage();
    }
    
}