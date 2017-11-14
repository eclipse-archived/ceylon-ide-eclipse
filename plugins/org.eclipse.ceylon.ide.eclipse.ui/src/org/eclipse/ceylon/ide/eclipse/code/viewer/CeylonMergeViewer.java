/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.viewer;

/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.PartEventAction;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorExtension3;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonSourceViewerConfiguration;


public class CeylonMergeViewer extends TextMergeViewer {
    
    private Map<SourceViewer, CeylonSourceViewerConfiguration> sourceViewerConfigurations;
    private Map<SourceViewer, CeylonEditorAdapter> editors;
    private ArrayList <SourceViewer> sourceViewers;
    
    private IWorkbenchPartSite site;
    
    public CeylonMergeViewer(Composite parent, int styles, CompareConfiguration mp) {
        super(parent, styles | SWT.LEFT_TO_RIGHT, mp);
    }
    
    @Override
    protected void handleDispose(DisposeEvent event) {
        sourceViewers= null;
        if (editors != null) {
            for (CeylonEditorAdapter cea: editors.values()) {
                cea.dispose();
            }
            editors= null;
        }
        site= null;
        super.handleDispose(event);
    }
    
    @Override
    public String getTitle() {
        return "Ceylon Source Comparison";
    }
    
    @Override
    protected IDocumentPartitioner getDocumentPartitioner() {
        return null;
    }
    
    @Override
    protected String getDocumentPartitioning() {
        return IDocument.DEFAULT_CONTENT_TYPE;
    }
    
    @Override
    protected void configureTextViewer(TextViewer viewer) {
        if (viewer instanceof SourceViewer) {
            SourceViewer sourceViewer= (SourceViewer) viewer;
            if (sourceViewers == null) {
                sourceViewers= new ArrayList<SourceViewer>();
            }
            if (!sourceViewers.contains(sourceViewer))
                sourceViewers.add(sourceViewer);
                IEditorInput editorInput= getEditorInput(sourceViewer);
                sourceViewer.unconfigure();
                if (editorInput == null) {
                    sourceViewer.configure(getSourceViewerConfiguration(sourceViewer, null));
                    sourceViewer.invalidateTextPresentation();
                }
                else {
                    getSourceViewerConfiguration(sourceViewer, editorInput);
                    sourceViewer.invalidateTextPresentation();
                }
            }
    }
    
    @Override
    protected void setEditable(ISourceViewer sourceViewer, boolean state) {
        super.setEditable(sourceViewer, state);
        if (editors != null) {
            CeylonEditorAdapter cea = editors.get(sourceViewer);
            if (cea!=null) {
                cea.setEditable(state);
            }
        }
    }
    
    @Override
    protected boolean isEditorBacked(ITextViewer textViewer) {
        return getSite() != null;
    }
    
    @Override
    protected IEditorInput getEditorInput(ISourceViewer sourceViewer) {
        IEditorInput editorInput= super.getEditorInput(sourceViewer);
        if (editorInput == null)
            return null;
        if (getSite() == null)
            return null;
        if (!(editorInput instanceof IStorageEditorInput))
            return null;
        return editorInput;
    }
    
    private IWorkbenchPartSite getSite() {
        if (site == null) {
            IWorkbenchPart workbenchPart = getCompareConfiguration()
                    .getContainer().getWorkbenchPart();
            site= workbenchPart != null ? workbenchPart.getSite() : null;
        }
        return site;
    }
    
    private CeylonSourceViewerConfiguration getSourceViewerConfiguration(SourceViewer sourceViewer, 
            IEditorInput editorInput) {
        if (sourceViewerConfigurations == null) {
            sourceViewerConfigurations= new HashMap<SourceViewer, CeylonSourceViewerConfiguration>(3);
        }
        CeylonSourceViewerConfiguration configuration = new CeylonSourceViewerConfiguration(null);
        if (editorInput != null) {
            // when input available, use editor
            CeylonEditorAdapter cea = editors.get(sourceViewer);
            try {
                cea.init((IEditorSite) cea.getSite(), editorInput);
                cea.createActions();
            } 
            catch (PartInitException e) {
                e.printStackTrace();
            }
        }
        sourceViewerConfigurations.put(sourceViewer, configuration);
        return sourceViewerConfigurations.get(sourceViewer);
    }
       
    @Override
    protected SourceViewer createSourceViewer(Composite parent, int textOrientation) {
        SourceViewer viewer;
        if (getSite() != null) {
            CeylonEditorAdapter cea= new CeylonEditorAdapter(textOrientation);
            cea.createPartControl(parent);
            ISourceViewer sourceViewer = cea.getCeylonSourceViewer();
            Assert.isTrue(sourceViewer instanceof SourceViewer);
            viewer = (SourceViewer) sourceViewer;
            if (editors == null) {
                editors = new HashMap<SourceViewer, CeylonEditorAdapter>(3);
            }
            editors.put(viewer, cea);
        } 
        else {
            viewer = super.createSourceViewer(parent, textOrientation);
        }

        if (sourceViewers == null) {
            sourceViewers = new ArrayList<SourceViewer>();
        }
        sourceViewers.add(viewer);
        return viewer;
    }

    @Override
    protected void setActionsActivated(SourceViewer sourceViewer, boolean state) {
        if (editors != null) {
            Object editor = editors.get(sourceViewer);
            if (editor instanceof CeylonEditorAdapter) {
                CeylonEditorAdapter cea = (CeylonEditorAdapter) editor;
                //cuea.setActionsActivated(state);
                IAction saveAction = cea.getAction(ITextEditorActionConstants.SAVE);
                if (saveAction instanceof IPageListener) {
                    PartEventAction partEventAction = (PartEventAction) saveAction;
                    IWorkbenchPart compareEditorPart= getCompareConfiguration().getContainer().getWorkbenchPart();
                    if (state) {
                        partEventAction.partActivated(compareEditorPart);
                    }
                    else {
                        partEventAction.partDeactivated(compareEditorPart);
                    }
                }
            }
        }
    }
    
    @Override
    protected void createControls(Composite composite) {
        super.createControls(composite);
        IWorkbenchPart workbenchPart = getCompareConfiguration().getContainer().getWorkbenchPart();
        if (workbenchPart != null) {
            IContextService service = (IContextService) workbenchPart.getSite()
                    .getService(IContextService.class);
            if (service != null) {
                service.activateContext(PLUGIN_ID + ".context");
            }
        }
    }
    
    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        if (adapter == ITextEditorExtension3.class) {
            IEditorInput activeInput = (IEditorInput) super.getAdapter(IEditorInput.class);
            if (activeInput!=null) {
                for (CeylonEditorAdapter cea: editors.values()) {
                    if (activeInput.equals(cea.getEditorInput())) {
                        return cea;
                    }
                }
            }
            return null;
        }
        return super.getAdapter(adapter);
    }
    
    private class CeylonEditorAdapter extends CeylonEditor {
        private int fTextOrientation;
        private boolean fEditable;
        private CeylonEditorAdapter(int textOrientation) {
            fTextOrientation = textOrientation;
        }
        private void setEditable(boolean editable) {
            fEditable = editable;
        }
        @Override
        public IWorkbenchPartSite getSite() {
            return CeylonMergeViewer.this.getSite();
        }
        @Override
        public void createActions() {}
        @Override
        public void createPartControl(Composite composite) {
            SourceViewer sourceViewer = createSourceViewer(composite, new CompositeRuler(), 
                    fTextOrientation | SWT.H_SCROLL | SWT.V_SCROLL);
            setSourceViewer(this, sourceViewer);
            createNavigationActions();
            getSelectionProvider().addSelectionChangedListener(getSelectionChangedListener());
        }
        // called by org.eclipse.ui.texteditor.TextEditorAction.canModifyEditor()
        @Override
        public boolean isEditable() {
            return fEditable;
        }
        @Override
        public boolean isEditorInputModifiable() {
            return fEditable;
        }
        @Override
        public boolean isEditorInputReadOnly() {
            return !fEditable;
        }
        @Override
        public void close(boolean save) {
            getDocumentProvider().disconnect(getEditorInput());
        }
    }
    
    // no setter to private field AbstractTextEditor.fSourceViewer
    private void setSourceViewer(ITextEditor editor, SourceViewer viewer) {
        Field field= null;
        try {
            field= AbstractTextEditor.class.getDeclaredField("fSourceViewer");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        field.setAccessible(true);
        try {
            field.set(editor, viewer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
//  @Override
//  protected int findInsertionPosition(char type, ICompareInput input) {
//
//      int pos= super.findInsertionPosition(type, input);
//      if (pos != 0)
//          return pos;
//
//      if (input instanceof IDiffElement) {
//
//          // find the other (not deleted) element
//          JavaNode otherJavaElement= null;
//          ITypedElement otherElement= null;
//          switch (type) {
//          case 'L':
//              otherElement= input.getRight();
//              break;
//          case 'R':
//              otherElement= input.getLeft();
//              break;
//          }
//          if (otherElement instanceof JavaNode)
//              otherJavaElement= (JavaNode) otherElement;
//
//          // find the parent of the deleted elements
//          JavaNode javaContainer= null;
//          IDiffElement diffElement= (IDiffElement) input;
//          IDiffContainer container= diffElement.getParent();
//          if (container instanceof ICompareInput) {
//
//              ICompareInput parent= (ICompareInput) container;
//              ITypedElement element= null;
//
//              switch (type) {
//              case 'L':
//                  element= parent.getLeft();
//                  break;
//              case 'R':
//                  element= parent.getRight();
//                  break;
//              }
//
//              if (element instanceof JavaNode)
//                  javaContainer= (JavaNode) element;
//          }
//
//          if (otherJavaElement != null && javaContainer != null) {
//
//              Object[] children;
//              Position p;
//
//              switch (otherJavaElement.getTypeCode()) {
//
//              case JavaNode.PACKAGE:
//                  return 0;
//
//              case JavaNode.IMPORT_CONTAINER:
//                  // we have to find the place after the package declaration
//                  children= javaContainer.getChildren();
//                  if (children.length > 0) {
//                      JavaNode packageDecl= null;
//                      for (int i= 0; i < children.length; i++) {
//                          JavaNode child= (JavaNode) children[i];
//                          switch (child.getTypeCode()) {
//                          case JavaNode.PACKAGE:
//                              packageDecl= child;
//                              break;
//                          case JavaNode.CLASS:
//                              return child.getRange().getOffset();
//                          }
//                      }
//                      if (packageDecl != null) {
//                          p= packageDecl.getRange();
//                          return p.getOffset() + p.getLength();
//                      }
//                  }
//                  return javaContainer.getRange().getOffset();
//
//              case JavaNode.IMPORT:
//                  // append after last import
//                  p= javaContainer.getRange();
//                  return p.getOffset() + p.getLength();
//
//              case JavaNode.CLASS:
//                  // append after last class
//                  children= javaContainer.getChildren();
//                  if (children.length > 0) {
//                      for (int i= children.length-1; i >= 0; i--) {
//                          JavaNode child= (JavaNode) children[i];
//                          switch (child.getTypeCode()) {
//                          case JavaNode.CLASS:
//                          case JavaNode.IMPORT_CONTAINER:
//                          case JavaNode.PACKAGE:
//                          case JavaNode.FIELD:
//                              p= child.getRange();
//                              return p.getOffset() + p.getLength();
//                          }
//                      }
//                  }
//                  return javaContainer.getAppendPosition().getOffset();
//
//              case JavaNode.METHOD:
//                  // append in next line after last child
//                  children= javaContainer.getChildren();
//                  if (children.length > 0) {
//                      JavaNode child= (JavaNode) children[children.length-1];
//                      p= child.getRange();
//                      return findEndOfLine(javaContainer, p.getOffset() + p.getLength());
//                  }
//                  // otherwise use position from parser
//                  return javaContainer.getAppendPosition().getOffset();
//
//              case JavaNode.FIELD:
//                  // append after last field
//                  children= javaContainer.getChildren();
//                  if (children.length > 0) {
//                      JavaNode method= null;
//                      for (int i= children.length-1; i >= 0; i--) {
//                          JavaNode child= (JavaNode) children[i];
//                          switch (child.getTypeCode()) {
//                          case JavaNode.METHOD:
//                              method= child;
//                              break;
//                          case JavaNode.FIELD:
//                              p= child.getRange();
//                              return p.getOffset() + p.getLength();
//                          }
//                      }
//                      if (method != null)
//                          return method.getRange().getOffset();
//                  }
//                  return javaContainer.getAppendPosition().getOffset();
//              }
//          }
//
//          if (javaContainer != null) {
//              // return end of container
//              Position p= javaContainer.getRange();
//              return p.getOffset() + p.getLength();
//          }
//      }
//
//      // we give up
//      return 0;
//  }
//
//  private int findEndOfLine(JavaNode container, int pos) {
//      int line;
//      IDocument doc= container.getDocument();
//      try {
//          line= doc.getLineOfOffset(pos);
//          pos= doc.getLineOffset(line+1);
//      } catch (BadLocationException ex) {
//          // silently ignored
//      }
//
//      // ensure that position is within container range
//      Position containerRange= container.getRange();
//      int start= containerRange.getOffset();
//      int end= containerRange.getOffset() + containerRange.getLength();
//      if (pos < start)
//          return start;
//      if (pos >= end)
//          return end-1;
//
//      return pos;
//  }
    
}
