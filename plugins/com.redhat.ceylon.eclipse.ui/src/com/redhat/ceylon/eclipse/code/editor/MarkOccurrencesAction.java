package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;

/**
 * Action class that implements the "Mark Occurrences" mode. This action contains a number of
 * nested listener classes that monitor which editor is active, document changes, and selection
 * changes, and computes a set of "occurrence" annotations in the editor, using the language-specific
 * "mark occurrences" service.
 */
public class MarkOccurrencesAction implements IWorkbenchWindowActionDelegate, 
		CaretListener, TreeLifecycleListener {
    /**
     * The ID for the kind of annotations created for "mark occurrences"
     */
    public static final String OCCURRENCE_ANNOTATION= PLUGIN_ID + ".occurrenceAnnotation";

    /**
     * True if "mark occurrences" is currently on/enabled
     */
    private boolean fMarkingEnabled = true;

    private CeylonEditor activeEditor;

    /**
     * The IParseController for the currently-active editor, if any. Could be null
     * if the current editor is not an IMP editor.
     */
    private CeylonParseController fParseController;

    /**
     * The document provider for the currently-active editor. Could be null if
     * the current editor is not an IMP editor.
     */
    private IDocumentProvider fDocumentProvider;

    /**
     * The document for the currently-active editor, if any. Could be null if
     * the current editor is not an IMP editor.
     */
    private IDocument fDocument;

    /**
     * The language-specific "mark occurrences" service implementation, if any.
     */
    private CeylonOccurrenceMarker fOccurrenceMarker;

    private Annotation[] fOccurrenceAnnotations;

    /**
     * Listens to part-related events from the workbench to monitor when text editors are
     * activated/closed, and keep the necessary listeners pointed at the active editor.
     */
    private final class EditorPartListener implements IPartListener {
        public void partActivated(IWorkbenchPart part) {
            if (part instanceof CeylonEditor) {
            	setUpActiveEditor((CeylonEditor) part);
            	if (fDocumentProvider!=null) {
            		retrieveOccurrenceAnnotations();
            		if (!fMarkingEnabled) {
            			unregisterListeners();
            			removeExistingOccurrenceAnnotations();
            		}
            	}
            }
        }

        public void partClosed(IWorkbenchPart part) {
            if (part == activeEditor) {
                unregisterListeners();
                activeEditor= null;
                fDocumentProvider= null;
                fDocument= null;
                fParseController= null;
                fOccurrenceMarker= null;
                fOccurrenceAnnotations= null;
            }
        }

        public void partBroughtToTop(IWorkbenchPart part) { }
        public void partDeactivated(IWorkbenchPart part) { }
        public void partOpened(IWorkbenchPart part) { }
    }

    public void caretMoved(CaretEvent event) {
    	int offset = event.caretOffset;
    	int length = 0;
    	IRegion selection = activeEditor.getSelection();
		if (selection.getLength()>0) {
    		offset = selection.getOffset();
    		length = selection.getLength();
    	}
    	recomputeAnnotationsForSelection(offset, length, fDocument);
    }

    public void run(IAction action) {
        fMarkingEnabled = action.isChecked();
        if (fMarkingEnabled) {
            setUpActiveEditor((CeylonEditor) PlatformUI.getWorkbench()
            		.getActiveWorkbenchWindow().getActivePage().getActiveEditor());
        } 
        else {
            unregisterListeners();
            removeExistingOccurrenceAnnotations();
        }
    }

    private void registerListeners() {
        // getDocumentFromEditor() can return null, but register listeners
        // should only be called when there is an active editor that can
        // be presumed to have a document provider that has document
        IDocument document = getDocumentFromEditor();
        if (document!=null) {
            activeEditor.getCeylonSourceViewer().getTextWidget()
                    .addCaretListener(this);
        }
        activeEditor.addModelListener(this);
    }

    private void unregisterListeners() {
        if (activeEditor!=null) {
        	activeEditor.getCeylonSourceViewer().getTextWidget()
                    .removeCaretListener(this);
        	activeEditor.removeModelListener(this);
        }
    }

    private IDocument getDocumentFromEditor() {
        IDocumentProvider provider = getDocumentProvider();
        if (provider != null)
            return provider.getDocument(getEditorInput());
        else
            return null;
    }

    private void recomputeAnnotationsForSelection(int offset, int length, IDocument document) {
        IAnnotationModel annotationModel= fDocumentProvider.getAnnotationModel(getEditorInput());
        Tree.CompilationUnit root= getCompilationUnit();
        if (root == null) {
            // Get this when "selecting" an error message that is shown in the editor view
            // but is not part of the source file; just returning should leave previous
            // markings, if any, as they were (which is probably fine)
            // Also get this when the current AST is null, e.g., as in the event of
            // a parse error
            return;
        }
        Node selectedNode= findNode(root, offset, offset+length-1);
        if (fOccurrenceMarker == null) {
            // It might be possible to set the active editor at this point under
            // some circumstances, but attempting to do so under other circumstances
            // can lead to stack overflow, so just return.
            return;
        }
        try {
            List<Object> occurrences= fOccurrenceMarker.getOccurrencesOf(fParseController, selectedNode);
            if (occurrences != null) {
                Position[] positions= convertRefNodesToPositions(occurrences);
                placeAnnotations(convertPositionsToAnnotationMap(positions, document), annotationModel);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<Annotation, Position> convertPositionsToAnnotationMap(Position[] positions, IDocument document) {
        Map<Annotation, Position> annotationMap= new HashMap<Annotation, Position>(positions.length);
        for(int i= 0; i < positions.length; i++) {
            Position position= positions[i];
            try { // Create & add annotation
                String message= document.get(position.offset, position.length);
                annotationMap.put(new Annotation(OCCURRENCE_ANNOTATION, false, message), position);
            } 
            catch (BadLocationException ex) {
                continue; // skip apparently bogus position
            }
        }
        return annotationMap;
    }

    private void placeAnnotations(Map<Annotation,Position> annotationMap, IAnnotationModel annotationModel) {
        synchronized (getLockObject(annotationModel)) {
            if (annotationModel instanceof IAnnotationModelExtension) {
                ((IAnnotationModelExtension) annotationModel).replaceAnnotations(fOccurrenceAnnotations, annotationMap);
            } 
            else {
                removeExistingOccurrenceAnnotations();
                Iterator<Map.Entry<Annotation,Position>> iter= annotationMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<Annotation,Position> mapEntry= iter.next();
                    annotationModel.addAnnotation((Annotation) mapEntry.getKey(), (Position) mapEntry.getValue());
                }
            }
            fOccurrenceAnnotations= (Annotation[]) annotationMap.keySet().toArray(new Annotation[annotationMap.keySet().size()]);
        }
    }

	private void retrieveOccurrenceAnnotations() {
		IAnnotationModel annotationModel= fDocumentProvider.getAnnotationModel(getEditorInput());
		// Need to initialize the set of pre-existing annotations in order
		// for them to be removed properly when new occurrences are marked
		if (annotationModel != null) {
		    @SuppressWarnings("unchecked")
		    Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
		    List<Annotation> annotationList = new ArrayList<Annotation>();
		    while (annotationIterator.hasNext()) {
		        // SMS 23 Jul 2008:  added test for annotation type
		        Annotation ann = (Annotation) annotationIterator.next();
		        if (ann.getType().indexOf(OCCURRENCE_ANNOTATION) > -1) {
		            annotationList.add(ann);
		        }
		    }
		    fOccurrenceAnnotations = annotationList.toArray(new Annotation[annotationList.size()]);
		}
	}

    void removeExistingOccurrenceAnnotations() {
        // RMF 6/27/2008 - If we've come up in an empty workspace, there won't be an active editor
        if (activeEditor == null)
            return;
        // RMF 6/27/2008 - Apparently partActivated() gets called before the editor is initialized
        // (on MacOS?), and then we can't properly initialize this MarkOccurrencesAction instance.
        // When that happens, fDocumentProvider will be null. Initialization needs a fix for that,
        // rather than this simple-minded null guard.
        if (fDocumentProvider == null)
            return;
        IAnnotationModel annotationModel= fDocumentProvider.getAnnotationModel(getEditorInput());
        if (annotationModel == null || fOccurrenceAnnotations == null)
            return;

        synchronized (getLockObject(annotationModel)) {
            if (annotationModel instanceof IAnnotationModelExtension) {
                ((IAnnotationModelExtension) annotationModel).replaceAnnotations(fOccurrenceAnnotations, null);
            } else {
                for(int i= 0, length= fOccurrenceAnnotations.length; i < length; i++)
                    annotationModel.removeAnnotation(fOccurrenceAnnotations[i]);
            }
            fOccurrenceAnnotations= null;
        }
    }

    private Position[] convertRefNodesToPositions(List<Object> refs) {
        Position[] positions= new Position[refs.size()];
        int i= 0;
        for(Iterator<Object> iter= refs.iterator(); iter.hasNext(); i++) {
            Object node= iter.next();
            positions[i]= new Position(getStartOffset(node), getLength(node)+1);
        }
        return positions;
    }

    private Tree.CompilationUnit getCompilationUnit() {
        // Do NOT compute fCompilationUnit conditionally based
        // on the AST being null; that causes problems when switching
        // between editor windows because the old value of the AST
        // will be retained even after the new window comes up, until
        // the text in the new window is parsed.  For now just
        // get the current AST (but in the future do something more
        // sophisticated to avoid needless recomputation but only
        // when it is truly needless).
        return fParseController.getRootNode();
    }

    private IEditorInput getEditorInput() {
        return activeEditor.getEditorInput();
    }

    private IDocumentProvider getDocumentProvider() {
        fDocumentProvider= activeEditor.getDocumentProvider();
        return fDocumentProvider;
    }

    private void setUpActiveEditor(CeylonEditor textEditor) {
        unregisterListeners();
        if (textEditor == null)
            return;
        activeEditor = textEditor;
        fDocument= getDocumentFromEditor();
        fParseController = activeEditor.getParseController();

        if (fParseController == null) {
            return;
        }

        fOccurrenceMarker = new CeylonOccurrenceMarker();
        registerListeners();

        ISelection selection = activeEditor.getSelectionProvider().getSelection();
        if (selection instanceof ITextSelection) {
            ITextSelection textSelection = (ITextSelection) selection;
            recomputeAnnotationsForSelection(textSelection.getOffset(), textSelection.getLength(), fDocument);
        }
    }

    private Object getLockObject(IAnnotationModel annotationModel) {
        if (annotationModel instanceof ISynchronizable)
            return ((ISynchronizable) annotationModel).getLockObject();
        else
            return annotationModel;
    }

    public void selectionChanged(IAction action, ISelection selection) { }

    public void dispose() {
        unregisterListeners();
    }

    public void init(IWorkbenchWindow window) {
        window.getActivePage().addPartListener(new EditorPartListener());
    }
    
    @Override
    public void update(CeylonParseController parseController,
    		IProgressMonitor monitor) {
    	try {
			getWorkbench().getProgressService().runInUI(activeEditor.getSite().getWorkbenchWindow(), 
					new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
			    	IRegion selection = activeEditor.getSelection();
			    	int offset = selection.getOffset();
			    	int length = selection.getLength();
			    	recomputeAnnotationsForSelection(offset, length, fDocument);
				}
			}, null);
		} 
    	catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	public Stage getStage() {
		return Stage.TYPE_ANALYSIS;
	}

}