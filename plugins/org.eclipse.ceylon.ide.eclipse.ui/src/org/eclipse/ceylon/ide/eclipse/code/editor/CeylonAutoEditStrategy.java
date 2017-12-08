/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

import org.eclipse.ceylon.compiler.typechecker.util.NewlineFixingStringStream;
import org.eclipse.ceylon.compiler.typechecker.parser.CeylonInterpolatingLexer;
import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.parser.CeylonParser;

public class CeylonAutoEditStrategy implements IAutoEditStrategy {

    @Override
    public void customizeDocumentCommand(IDocument document, 
            DocumentCommand command) {
        ANTLRStringStream stream = 
                new NewlineFixingStringStream(document.get());
        CeylonLexer lexer = new CeylonLexer(stream);
        CommonTokenStream ts = 
                new CommonTokenStream(
                        new CeylonInterpolatingLexer(lexer));
        ts.fill();
        try {
            new CeylonParser(ts).compilationUnit();
        } 
        catch (RecognitionException e) {}
        new AutoEdit(document, (List) ts.getTokens(), command)
                .customizeDocumentCommand();
    }

}
