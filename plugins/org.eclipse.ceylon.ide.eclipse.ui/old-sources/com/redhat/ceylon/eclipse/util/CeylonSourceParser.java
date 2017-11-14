/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import org.eclipse.ceylon.compiler.typechecker.util.NewlineFixingStringStream;
import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.parser.CeylonParser;
import org.eclipse.ceylon.compiler.typechecker.parser.LexError;
import org.eclipse.ceylon.compiler.typechecker.parser.ParseError;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;

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
