package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.ide.eclipse.code.editor.EditorActionIds.RESTORE_PREVIOUS;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.texteditor.ITextEditor;

class RestorePreviousSelectionAction extends Action {
    private CeylonEditor editor;
    private List<IRegion> previous = new ArrayList<IRegion>();
    private boolean restoring;

    public RestorePreviousSelectionAction() {
        this(null);
    }
    
    @Override
    public boolean isEnabled() {
        return super.isEnabled() && editor!=null;
    }

    public RestorePreviousSelectionAction(CeylonEditor editor) {
        super("Select Enclosing");
        setActionDefinitionId(RESTORE_PREVIOUS);
        setEditor(editor);
    }

    private void setEditor(ITextEditor editor) {
        if (editor instanceof CeylonEditor) {
            this.editor = (CeylonEditor) editor;
            this.editor.getSelectionProvider()
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    if (!restoring) {
                        IRegion r = RestorePreviousSelectionAction.this.editor.getSelection();
                        if (r.getLength()==0) {
                            previous.clear();
                        }
                        previous.add(r);//new Region(r.getOffset(), r.getLength()));
                        if (previous.size()>20) {
                            previous.remove(0);
                        }
                        setEnabled(previous.size()>1);
                    }
                }
            });
         } 
        else {
            this.editor= null;
        }
       setEnabled(false);
    }

    @Override
    public void run() {
        if (previous.size()>0) {
            previous.remove(previous.size()-1);
        }
        if (previous.size()>0) {
            IRegion r = previous.get(previous.size()-1);
            restoring=true;
            editor.selectAndReveal(r.getOffset(), r.getLength());
            restoring=false;
            setEnabled(previous.size()>1);
        }
    }
}