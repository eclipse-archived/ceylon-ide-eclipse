/*************************************************************************************
 * Copyright (c) 2011 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package com.redhat.ceylon.eclipse.ui.test;

import static org.junit.Assert.fail;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.junit.Test;

/**
 * 
 * @author max
 * 
 */
public class CeylonEditorTest {

	@Test
	public void testOpenEditor() {

		File fileToOpen = new File("externalfile.xml");

		IFileStore fileStore = EFS.getLocalFileSystem().getStore(
				fileToOpen.toURI());
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		
		try {
			IDE.openEditorOnFileStore(page, fileStore);
		} catch (PartInitException e) {
			fail("editor failed to open file");
		}
	}
}
