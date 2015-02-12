package com.redhat.ceylon.eclipse.util;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.redhat.ceylon.compiler.typechecker.util.NewlineFixingStringStream;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;

public abstract class CeylonSourceParser<ResultPhasedUnit extends PhasedUnit> {
    public final ResultPhasedUnit parseFileToPhasedUnit(final ModuleManager moduleManager, final TypeChecker typeChecker,
            final VirtualFile file, final VirtualFile srcDir, final Package pkg) {
        ANTLRStringStream input;
        try {
            input = NewlineFixingStringStream.fromStream(file.getInputStream(), getCharset());
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        CeylonLexer lexer = new CeylonLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        CeylonParser parser = new CeylonParser(tokenStream);
        Tree.CompilationUnit cu;
        try {
            cu = parser.compilationUnit();
        }
        catch (RecognitionException e) {
            throw new RuntimeException(e);
        }
        
        List<LexError> lexerErrors = lexer.getErrors();
        for (LexError le : lexerErrors) {
            cu.addLexError(le);
        }
        lexerErrors.clear();
        
        List<ParseError> parserErrors = parser.getErrors();
        for (ParseError pe : parserErrors) {
            cu.addParseError(pe);
        }
        parserErrors.clear();
        
        return createPhasedUnit(cu, pkg, tokenStream);
    }

    abstract protected ResultPhasedUnit createPhasedUnit(CompilationUnit cu, Package pkg, CommonTokenStream tokenStream);
    abstract protected String getCharset();
}
