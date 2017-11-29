/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.util {
    EditorUtil {
        getEditorInput,
        adjustEditorInput
    }
}

import org.eclipse.core.resources {
    IMarker
}
import org.eclipse.core.runtime {
    Path
}
import org.eclipse.debug.core {
    DebugPlugin
}
import org.eclipse.debug.core.sourcelookup.containers {
    LocalFileStorage,
    ZipEntryStorage
}
import org.eclipse.debug.ui {
    ISourcePresentation
}
import org.eclipse.jdt.core {
    IType,
    IJavaElement
}
import org.eclipse.jdt.debug.core {
    IJavaBreakpoint
}
import org.eclipse.jdt.internal.debug.ui {
    BreakpointUtils,
    LocalFileStorageEditorInput,
    ZipEntryStorageEditorInput
}
import org.eclipse.ui {
    IEditorInput,
    PartInitException
}
import org.eclipse.ui.ide {
    IDE
}
import org.eclipse.jdt.internal.launching {
    JavaSourceLookupDirector
}

shared interface CeylonAwareSourceLookupDirector satisfies ISourcePresentation {

    shared actual IEditorInput? getEditorInput(Object? element) {
        if (!exists element) {
            return null;
        }
        
        value item
                = switch (element)
        		case (is IMarker) 
        			DebugPlugin.default.breakpointManager.getBreakpoint(element)
        		else case (is IJavaBreakpoint) 
        			(BreakpointUtils.getType(element) 
            			else element.marker?.resource)
        		else element;
        		
       	switch (item)
        case (is LocalFileStorage) {
            return adjustEditorInput(LocalFileStorageEditorInput(item));
        }
        case (is ZipEntryStorage) {
            if (item.name.endsWith(".ceylon")) {
                value archivePath = Path.fromOSString("``item.archive.name``!");
                IEditorInput input = EditorUtil.getEditorInput(archivePath.append(item.zipEntry.name));
                return adjustEditorInput(input);
            }
            else {
                return ZipEntryStorageEditorInput(item);
            }
        }
        else if (is IType item, !(item of IJavaElement).\iexists()) {
            return null;
        }
        else {
        	return adjustEditorInput(EditorUtil.getEditorInput(item));
        }
    }

    shared actual String? getEditorId(variable IEditorInput input, variable Object element) {
        try {
            return IDE.getEditorDescriptor(input.name).id;
        }
        catch (PartInitException e) {
            return null;
        }
    }
}

shared class CeylonSourceLookupDirector()
        extends JavaSourceLookupDirector()
        satisfies CeylonAwareSourceLookupDirector {
    shared actual Object? getSourceElement(Object? o)
            => super.getSourceElement(o);
}

