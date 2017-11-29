/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import org.eclipse.jface.text.IInformationControlCreator;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.hover.SourceInfoHover;
import org.eclipse.ceylon.ide.common.doc.DocGenerator;

public interface HoverJ2C {

    DocGenerator getDocGenerator();

    SourceInfoHover newEclipseDocGeneratorAsSourceInfoHover(
            CeylonEditor editor);

    DocGenerator newEclipseDocGenerator(CeylonEditor editor);

    IInformationControlCreator getInformationPresenterControlCreator(
            DocGenerator docGenerator);

}