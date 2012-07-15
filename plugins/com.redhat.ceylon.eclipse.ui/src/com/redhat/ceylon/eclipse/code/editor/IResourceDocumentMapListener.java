package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

/**
 * Implemented by clients that need notification when the association between
 * editor documents and resources changes. At this moment, mostly for the benefit
 * of the indexing mechanism.
 */
public interface IResourceDocumentMapListener {
    void registerDocument(IDocument doc, IResource res, IEditorPart editor);
    void updateResourceDocumentMap(IDocument doc, IResource res, IEditorPart editor);
    void unregisterDocument(IDocument doc);
}
