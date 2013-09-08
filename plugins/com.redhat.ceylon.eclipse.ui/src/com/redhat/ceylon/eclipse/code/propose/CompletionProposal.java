package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ANN_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ID_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.KW_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.PACKAGE_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.TYPE_ID_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.VERSION_STYLER;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import java.util.StringTokenizer;

import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;


public class CompletionProposal implements ICompletionProposal, 
        /*ICompletionProposalExtension,*/ ICompletionProposalExtension4, 
        ICompletionProposalExtension6 {
    
    private final String text;
    private final Image image;
    private final boolean selectParams;
    private final String prefix;
    private final String description;
    protected int offset;
    private IEditingSupport editingSupport;
    
    CompletionProposal(int offset, String prefix, Image image,
            String desc, String text, boolean selectParams) {
        this.text=text;
        this.image = image;
        this.selectParams = selectParams;
        this.offset = offset;
        this.prefix = prefix;
        this.description = desc;
    }
    
    @Override
    public Image getImage() {
        return image;
    }
    @Override
    public Point getSelection(IDocument document) {
        /*if (text.endsWith("= ")) {
                return new Point(offset-prefix.length()+text.length(), 0);
            }
        else*/ 
        if (selectParams) {
            int locOfTypeArgs = text.indexOf('<');
            int loc = locOfTypeArgs;
            if (loc<0) loc = text.indexOf('(');
            if (loc<0) loc = text.indexOf('=')+1;
            int start;
            int length;
            if (loc<=0 || locOfTypeArgs<0 &&
                    (text.contains("()") || text.contains("{}"))) {
                start = text.endsWith("{}") ? text.length()-1 : text.length();
                length = 0;
            }
            else {
                int endOfTypeArgs = text.indexOf('>');
                int end = text.indexOf(',');
                if (end<0) end = text.indexOf(')');
                if (end<0) end = text.indexOf(';');
                if (end<0) end = text.length()-1;
                if (endOfTypeArgs>0) end = end < endOfTypeArgs ? end : endOfTypeArgs;
                start = loc+1;
                length = end-loc-1;
            }
            return new Point(offset-prefix.length() + start, length);
        }
        else {
            int loc = text.indexOf("nothing;");
            int length;
            int start;
            if (loc<0) {
                start = offset + text.length()-prefix.length();
                if (text.endsWith("{}")) start--;
                length = 0;
            }
            else {
                start = offset + loc-prefix.length();
                length = 7;
            }
            return new Point(start, length);
        }
    }
    
    public void apply(IDocument document) {
        try {
            document.replace(offset-prefix.length(), prefix.length(), text);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    public String getDisplayString() {
        return description;
    }

    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public boolean isAutoInsertable() {
    	return true;
    }

	@Override
	public StyledString getStyledDisplayString() {
		StyledString result = new StyledString();
		String string = getDisplayString();
		if (this instanceof RefinementCompletionProposal) {
			if (string.startsWith("shared actual")) {
				result.append(string.substring(0,13), ANN_STYLER);
				string=string.substring(13);
			}
		}
		style(result, string);
		return result;
	}

	public static void style(StyledString result, String string) {
		StringTokenizer tokens = new StringTokenizer(string, " ()<>", true);
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			if (isUpperCase(token.charAt(0))) {
				result.append(token, TYPE_ID_STYLER);
			}
			else if (isLowerCase(token.charAt(0))) {
				if (CeylonTokenColorer.keywords.contains(token)) {
					result.append(token, KW_STYLER);
				}
				else if (token.contains(".")) {
				    result.append(token, PACKAGE_STYLER);
				}
				else {
					result.append(token, ID_STYLER);
				}
			}
			else if (token.charAt(0)=='\"') {
				result.append(token, VERSION_STYLER);
			}
			else {
				result.append(token);
			}
		}
	}
	
	public void enterLinkedMode(IDocument document) {
	    try {
	        final LinkedModeModel linkedModeModel = new LinkedModeModel();
	        int loc = offset-prefix.length();
	        int first = text.indexOf('(');
	        int next = text.substring(first).indexOf(',');
	        if (next<0) next = text.substring(first).indexOf(')');
	        int i=0;
	        while (next>0) {
		        LinkedPositionGroup linkedPositionGroup = new LinkedPositionGroup();
				LinkedPosition linkedPosition = new ProposalPosition(document, 
		        		loc+first+1, next-1, i++, 
		        		new ICompletionProposal[0]);
		        linkedPositionGroup.addPosition(linkedPosition);
		        first = first+next+1;
		        next = text.substring(first).indexOf(',');
		        if (next<0) next = text.substring(first).indexOf(')');
	            linkedModeModel.addGroup(linkedPositionGroup);
	        }
            linkedModeModel.forceInstall();
            final CeylonEditor editor = (CeylonEditor) Util.getCurrentEditor();
            linkedModeModel.addLinkingListener(new ILinkedModeListener() {
                @Override
                public void left(LinkedModeModel model, int flags) {
                    editor.setInLinkedMode(false);
                    editor.unpauseBackgroundParsing();
                    if ((flags&ILinkedModeListener.UPDATE_CARET)!=0) {
//                        editor.doSave(new NullProgressMonitor());
                    	
                    }
                    linkedModeModel.exit(ILinkedModeListener.NONE);
                    ISourceViewer viewer= editor.getCeylonSourceViewer();
                    if (viewer instanceof IEditingSupportRegistry) {
                        IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
                        registry.unregister(editingSupport);
                    }
                    
                    editor.getSite().getPage().activate(editor);
                }
                @Override
                public void suspend(LinkedModeModel model) {
                    editor.setInLinkedMode(false);
                    editor.unpauseBackgroundParsing();
                }
                @Override
                public void resume(LinkedModeModel model, int flags) {
                    editor.setInLinkedMode(true);
                    editor.pauseBackgroundParsing();
                }
            });
            editor.setInLinkedMode(true);
            editor.pauseBackgroundParsing();
            CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
			EditorLinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
            ui.setExitPosition(viewer, loc+first+next+1, 0, i);
            ui.setExitPolicy(new DeleteBlockingExitPolicy(document) {
            	@Override
            	public ExitFlags doExit(LinkedModeModel model,
            			VerifyEvent event, int offset, int length) {
//            		if (event.character==',') {
//            			event.character = '\t';
//						event.keyCode= SWT.TAB;
//            		}
//            		else if (event.character == ')') {
//            			return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
//            		}
            		return super.doExit(model, event, offset, length);
            	}
            });
            ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
            ui.setDoContextInfo(true);
//            ui.enableColoredLabels(true);
            ui.enter();
            
            if (viewer instanceof IEditingSupportRegistry) {
                IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
                editingSupport = new IEditingSupport() {
                    public boolean ownsFocusShell() {
                        Shell editorShell= editor.getSite().getShell();
                        Shell activeShell= editorShell.getDisplay().getActiveShell();
                        if (editorShell == activeShell)
                            return true;
                        return false;
                    }
                    public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
                        return false; //leave on external modification outside positions
                    }
                };
				registry.register(editingSupport);
            }

	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/*class PropContextInfo implements IContextInformation, IContextInformationExtension {
		private final String fContextDisplayString;
		private final String fInformationDisplayString;
		private final Image fImage;

		public PropContextInfo(String contextDisplayString, String informationDisplayString) {
			this(null, contextDisplayString, informationDisplayString);
		}

		public PropContextInfo(Image image, String contextDisplayString, String informationDisplayString) {

			Assert.isNotNull(informationDisplayString);

			fImage= image;
			fContextDisplayString= contextDisplayString;
			fInformationDisplayString= informationDisplayString;
		}

		public boolean equals(Object object) {
			if (object instanceof IContextInformation) {
				IContextInformation contextInformation= (IContextInformation) object;
				boolean equals= fInformationDisplayString.equalsIgnoreCase(contextInformation.getInformationDisplayString());
				if (fContextDisplayString != null)
					equals= equals && fContextDisplayString.equalsIgnoreCase(contextInformation.getContextDisplayString());
				return equals;
			}
			return false;
		}

		public int hashCode() {
		 	int low= fContextDisplayString != null ? fContextDisplayString.hashCode() : 0;
		 	return (fInformationDisplayString.hashCode() << 16) | low;
		}

		public String getInformationDisplayString() {
			return fInformationDisplayString;
		}

		public Image getImage() {
			return fImage;
		}

		public String getContextDisplayString() {
			if (fContextDisplayString != null)
				return fContextDisplayString;
			return fInformationDisplayString;
		}

		@Override
		public int getContextInformationPosition() {
			return offset;
		}
	}*/
	
	@Override
	public IContextInformation getContextInformation() {
		String str = text.substring(text.indexOf('(')+1, text.indexOf(')'));
		return new ContextInformation(text, str);
	}

	/*@Override
	public void apply(IDocument document, char trigger, int offset) {
		apply(document);
	}

	@Override
	public boolean isValidFor(IDocument document, int offset) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public char[] getTriggerCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getContextInformationPosition() {
		// TODO Auto-generated method stub
		return offset;
	}*/

}