package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.getInstance;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CONFIG_ANN;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CONFIG_ANN_DIS;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHoverExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension2;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;


/**
 * Abstract super class for annotation hovers.
 *
 */
public class AnnotationHover 
        extends SourceInfoHover
        implements IAnnotationHover, IAnnotationHoverExtension {

    /**
     * Action to configure the annotation preferences.
     *
     */
    class ConfigureAnnotationsAction extends Action {

        private final AnnotationInformationControl fInfoControl;
        
        public ConfigureAnnotationsAction(AnnotationInformationControl infoControl) {
            fInfoControl = infoControl;
            setText("Configure Annotation Appearance");
            ImageRegistry imageRegistry = getInstance().getImageRegistry();
            setImageDescriptor(imageRegistry.getDescriptor(CONFIG_ANN));
            setDisabledImageDescriptor(imageRegistry.getDescriptor(CONFIG_ANN_DIS));
            setToolTipText("Configure Annotation Appearance");
        }

        @Override
        public void run() {
            Shell shell = getWorkbench().getActiveWorkbenchWindow().getShell();

            Object data = null;
            Map<Annotation, Position> annotationPositions = 
                    fInfoControl.getAnnotationInfo().getAnnotationPositions();
            if (annotationPositions!=null &&
                    !annotationPositions.isEmpty()) {
                Annotation annotation = 
                        annotationPositions.keySet().iterator().next();
                AnnotationPreference preference = 
                        getAnnotationPreference(annotation);
                if (preference != null) {
                    data = preference.getPreferenceLabel();
                }
            }
            
            fInfoControl.dispose(); //FIXME: should have protocol to hide, rather than dispose
            createPreferenceDialogOn(shell, 
                    "org.eclipse.ui.editors.preferencePages.Annotations", 
                    null, data).open();
        }
    }

    //private final IPreferenceStore fStore= EditorUtil.getPreferences();
    private final DefaultMarkerAnnotationAccess fAnnotationAccess = 
            new DefaultMarkerAnnotationAccess();

    private final CeylonEditor editor;
    private final boolean rulerHover;
    
    public AnnotationHover(CeylonEditor editor, boolean rulerHover) {
        super(editor);
        this.editor = editor;
        this.rulerHover = rulerHover;
    }
    
    @Override
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        return null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
        
        IAnnotationModel model;
        if (textViewer instanceof ISourceViewer) {
            model = ((ISourceViewer) textViewer).getAnnotationModel();
        }
        else {
            // Get annotation model from file buffer manager
            //path= getEditorInputPath();
            //model= getAnnotationModel(path);
            model=null;
        }
        if (model == null) return null;

        Iterator<Annotation> parent;
        if (model instanceof IAnnotationModelExtension2) {
            parent = ((IAnnotationModelExtension2) model)
                    .getAnnotationIterator(hoverRegion.getOffset(), 
                            hoverRegion.getLength()+1, true, true);
        }
        else {
            parent = model.getAnnotationIterator();
        }

        Map<Annotation,Position> annotationPositions = 
                new LinkedHashMap<Annotation, Position>();
        Iterator<Annotation> iter = new AnnotationIterator(parent, rulerHover);
        while (iter.hasNext()) {
            Annotation a = (Annotation) iter.next();
            Position p = model.getPosition(a);
            int l = fAnnotationAccess.getLayer(a);
            //TODO: make higher-layer annotations suppress lower-layer ones
            if (p != null && p.overlapsWith(hoverRegion.getOffset(), 
                    hoverRegion.getLength())) {
                String msg = a.getText();
                if (msg != null && msg.trim().length() > 0) {
                    if (l>=0) {
                        annotationPositions.put(a, p);
                    }
                }
            }
        }
        
        if (!annotationPositions.isEmpty()) {
            return createAnnotationInfo(textViewer, annotationPositions);
        }
        else {
            return null;
        }
    }
    
    protected AnnotationInfo createAnnotationInfo(ITextViewer textViewer, 
            Map<Annotation,Position> annotationPositions) {
        return new AnnotationInfo(editor, annotationPositions, textViewer);
    }
    
    @Override
    public IInformationControlCreator getHoverControlCreator() {
        return new AbstractReusableInformationControlCreator() {
            @Override
            public IInformationControl doCreateInformationControl(Shell parent) {
                return new AnnotationInformationControl(parent, "F2 for focus") {
                    /**
                     * Create the "enriched" control
                     */
                    @Override
                    public IInformationControlCreator getInformationPresenterControlCreator() {
                        return new AbstractReusableInformationControlCreator() {
                            @Override
                            public IInformationControl doCreateInformationControl(Shell parent) {
                                ToolBarManager tbm = new ToolBarManager(SWT.FLAT);
                                AnnotationInformationControl control = 
                                        new AnnotationInformationControl(parent, tbm);
                                ConfigureAnnotationsAction configAnnotations = 
                                        new ConfigureAnnotationsAction(control);
                                configAnnotations.setEnabled(true);
                                //TODO: add a listener which sets the annotation type
                                //      onto the ConfigureAnnotationsAction
                                tbm.add(configAnnotations);
                                tbm.update(true);
                                return control;
                            }
                        };
                    }
                };
            }
            @Override
            public boolean canReuse(IInformationControl control) {
                if (!super.canReuse(control)) {
                    return false;
                }
                if (control instanceof IInformationControlExtension4) {
                    ((IInformationControlExtension4) control)
                            .setStatusText("F2 for focus");
                }
                return true;
            }
        };
    }
    
    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return new Region(offset, 0);
    }
    
    /**
     * Returns the annotation preference for the given annotation.
     *
     * @param annotation the annotation
     * @return the annotation preference or <code>null</code> if none
     */
    private static AnnotationPreference getAnnotationPreference(Annotation annotation) {
        if (annotation.isMarkedDeleted()) {
            return null;
        }
        else {
            return EditorsUI.getAnnotationPreferenceLookup()
                    .getAnnotationPreference(annotation);
        }
    }

    @Override
    public boolean canHandleMouseCursor() {
        return false;
    }

    @Override
    public Object getHoverInfo(ISourceViewer sourceViewer,
            ILineRange lineRange, int visibleNumberOfLines) {
        try {
            return getHoverInfo2(sourceViewer, sourceViewer.getDocument()
                    .getLineInformation(lineRange.getStartLine()));
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ILineRange getHoverLineRange(ISourceViewer viewer, int lineNumber) {
        return new LineRange(lineNumber, 1);
    }

    @Override
    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
        //TODO: implement to get hover in overview ruler!!
        return null;
    }
    
}
