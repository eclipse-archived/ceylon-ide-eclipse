package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.getInstance;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CONFIG_ANN;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CONFIG_ANN_DIS;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension2;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;


/**
 * Abstract super class for annotation hovers.
 *
 * @since 3.0
 */
public abstract class AbstractAnnotationHover 
        implements ITextHover, ITextHoverExtension, ITextHoverExtension2 {

    /**
     * Creates the "enriched" control.
     */
    private static final class PresenterControlCreator extends AbstractReusableInformationControlCreator {
        @Override
        public IInformationControl doCreateInformationControl(Shell parent) {
            return new AnnotationInformationControl(parent, new ToolBarManager(SWT.FLAT));
        }
    }

    private static final class HoverControlCreator extends AbstractReusableInformationControlCreator {
        private final IInformationControlCreator fPresenterControlCreator;

        public HoverControlCreator(IInformationControlCreator presenterControlCreator) {
            fPresenterControlCreator= presenterControlCreator;
        }

        @Override
        public IInformationControl doCreateInformationControl(Shell parent) {
            return new AnnotationInformationControl(parent, "F2 for focus") {
                @Override
                public IInformationControlCreator getInformationPresenterControlCreator() {
                    return fPresenterControlCreator;
                }
            };
        }

        @Override
        public boolean canReuse(IInformationControl control) {
            if (!super.canReuse(control))
                return false;

            if (control instanceof IInformationControlExtension4)
                ((IInformationControlExtension4) control).setStatusText("F2 for focus");

            return true;
        }
    }

    /**
     * Action to configure the annotation preferences.
     *
     * @since 3.4
     */
    static final class ConfigureAnnotationsAction extends Action {

        private final Annotation fAnnotation;
        private final IInformationControl fInfoControl;

        public ConfigureAnnotationsAction(Annotation annotation, IInformationControl infoControl) {
            super();
            fAnnotation= annotation;
            fInfoControl= infoControl;
            ImageRegistry imageRegistry = getInstance().getImageRegistry();
            setImageDescriptor(imageRegistry.getDescriptor(CONFIG_ANN));
            setDisabledImageDescriptor(imageRegistry.getDescriptor(CONFIG_ANN_DIS));
            setToolTipText("Configure Annotation Preferences");
        }

        @Override
        public void run() {
            Shell shell= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

            Object data= null;
            AnnotationPreference preference= getAnnotationPreference(fAnnotation);
            if (preference != null)
                data= preference.getPreferenceLabel();

            fInfoControl.dispose(); //FIXME: should have protocol to hide, rather than dispose
            PreferencesUtil.createPreferenceDialogOn(shell, 
                    "org.eclipse.ui.editors.preferencePages.Annotations", 
                    null, data).open();
        }
    }

    //private final IPreferenceStore fStore= CeylonPlugin.getInstance().getPreferenceStore();
    private final DefaultMarkerAnnotationAccess fAnnotationAccess= new DefaultMarkerAnnotationAccess();
    private final boolean fAllAnnotations;

    private IInformationControlCreator fHoverControlCreator;
    private IInformationControlCreator fPresenterControlCreator;
    
    public AbstractAnnotationHover(boolean allAnnotations) {
        fAllAnnotations = allAnnotations;
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
            parent = ((IAnnotationModelExtension2) model).getAnnotationIterator(hoverRegion.getOffset(), 
                    hoverRegion.getLength()+1, true, true);
        }
        else {
            parent= model.getAnnotationIterator();
        }
        Iterator<Annotation> e= new AnnotationIterator(parent, fAllAnnotations);

        int layer= -1;
        Annotation annotation= null;
        Position position= null;
        while (e.hasNext()) {
            Annotation a = (Annotation) e.next();
            Position p = model.getPosition(a);
            int l= fAnnotationAccess.getLayer(a);

            if (l > layer && p != null && p.overlapsWith(hoverRegion.getOffset(), 
                    hoverRegion.getLength())) {
                String msg= a.getText();
                if (msg != null && msg.trim().length() > 0) {
                    layer= l;
                    annotation= a;
                    position= p;
                }
            }
        }
        if (layer > -1)
            return createAnnotationInfo(annotation, position, textViewer);

        return null;
    }
    
    protected AnnotationInfo createAnnotationInfo(Annotation annotation, 
            Position position, ITextViewer textViewer) {
        return new AnnotationInfo(annotation, position, textViewer);
    }
    
    @Override
    public IInformationControlCreator getHoverControlCreator() {
        if (fHoverControlCreator == null)
            fHoverControlCreator= new HoverControlCreator(getInformationPresenterControlCreator());
        return fHoverControlCreator;
    }
    
    private IInformationControlCreator getInformationPresenterControlCreator() {
        if (fPresenterControlCreator == null)
            fPresenterControlCreator= new PresenterControlCreator();
        return fPresenterControlCreator;
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
        if (annotation.isMarkedDeleted())
            return null;
        return EditorsUI.getAnnotationPreferenceLookup().getAnnotationPreference(annotation);
    }
}

