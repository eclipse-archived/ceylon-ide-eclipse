package com.redhat.ceylon.eclipse.code.outline;

import java.util.ArrayList;

import org.eclipse.imp.editor.hover.AbstractAnnotationHover;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.TextInvocationContext;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixController;
import com.redhat.ceylon.eclipse.code.quickfix.ProblemLocation;

/**
 * This annotation hover shows the description of the selected java annotation.
 * 
 * XXX: Currently this problem hover only works for Java and spelling problems.
 * see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=62081
 * 
 * @since 3.0
 */
public class ProblemHover extends AbstractAnnotationHover {

//	/**
//	 * Action to configure the problem severity of a compiler option.
//	 * 
//	 * @since 3.4
//	 */
//	private static final class ConfigureProblemSeverityAction extends Action {
//
//		private static final String CONFIGURE_PROBLEM_SEVERITY_DIALOG_ID = "configure_problem_severity_dialog_id"; //$NON-NLS-1$
//
//		// private final IJavaProject fProject;
//		private final String fOptionId;
//		private final boolean fIsJavadocOption;
//		private final IInformationControl fInfoControl;
//
//		public ConfigureProblemSeverityAction(
//				/* IJavaProject project, */String optionId,
//				boolean isJavadocOption, IInformationControl infoControl) {
//			super();
//			// fProject= project;
//			fOptionId = optionId;
//			fIsJavadocOption = isJavadocOption;
//			fInfoControl = infoControl;
//			setImageDescriptor(PluginImages.DESC_ELCL_CONFIGURE_PROBLEM_SEVERITIES);
//			setDisabledImageDescriptor(PluginImages.DESC_DLCL_CONFIGURE_PROBLEM_SEVERITIES);
//			setToolTipText("Configure Problem Severity");
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * 
//		 * @see org.eclipse.jface.action.Action#run()
//		 */
//		public void run() {
//			boolean showPropertyPage;
//
//			// Shell shell=
//			// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//			//
//			// if (! hasProjectSpecificOptions()) {
//			// String message= Messages.format(
//			// JavaHoverMessages.ProblemHover_chooseSettingsTypeDialog_message,
//			// new Object[] { JavaElementLabels.getElementLabel(fProject,
//			// JavaElementLabels.ALL_DEFAULT) });
//			//
//			// String[] buttons= new String[] {
//			// JavaHoverMessages.ProblemHover_chooseSettingsTypeDialog_button_project,
//			// JavaHoverMessages.ProblemHover_chooseSettingsTypeDialog_button_workspace,
//			// JavaHoverMessages.ProblemHover_chooseSettingsTypeDialog_button_cancel
//			// };
//			//
//			// int result= OptionalMessageDialog.open(
//			// CONFIGURE_PROBLEM_SEVERITY_DIALOG_ID, shell,
//			// JavaHoverMessages.ProblemHover_chooseSettingsTypeDialog_title,
//			// null, message, MessageDialog.QUESTION, buttons, 0,
//			// JavaHoverMessages.ProblemHover_chooseSettingsTypeDialog_checkBox_dontShowAgain);
//			//
//			// if (result == OptionalMessageDialog.NOT_SHOWN) {
//			// showPropertyPage= false;
//			// } else if (result == 2 || result == SWT.DEFAULT) {
//			// return;
//			// } else if (result == 0) {
//			// showPropertyPage= true;
//			// } else {
//			// showPropertyPage= false;
//			// }
//			// } else {
//			// showPropertyPage= true;
//			// }
//			//
//			// Map data= new HashMap();
//			// String pageId;
//			// if (fIsJavadocOption) {
//			// if (showPropertyPage) {
//			// pageId= JavadocProblemsPreferencePage.PROP_ID;
//			// data.put(JavadocProblemsPreferencePage.DATA_USE_PROJECT_SPECIFIC_OPTIONS,
//			// Boolean.TRUE);
//			// } else {
//			// pageId= JavadocProblemsPreferencePage.PREF_ID;
//			// }
//			// data.put(JavadocProblemsPreferencePage.DATA_SELECT_OPTION_KEY,
//			// fOptionId);
//			// data.put(JavadocProblemsPreferencePage.DATA_SELECT_OPTION_QUALIFIER,
//			// JavaCore.PLUGIN_ID);
//			// } else {
//			// if (showPropertyPage) {
//			// pageId= ProblemSeveritiesPreferencePage.PROP_ID;
//			// data.put(ProblemSeveritiesPreferencePage.USE_PROJECT_SPECIFIC_OPTIONS,
//			// Boolean.TRUE);
//			// } else {
//			// pageId= ProblemSeveritiesPreferencePage.PREF_ID;
//			// }
//			// data.put(ProblemSeveritiesPreferencePage.DATA_SELECT_OPTION_KEY,
//			// fOptionId);
//			// data.put(ProblemSeveritiesPreferencePage.DATA_SELECT_OPTION_QUALIFIER,
//			// JavaCore.PLUGIN_ID);
//			// }
//			//
//			// fInfoControl.dispose(); //FIXME: should have protocol to hide,
//			// rather than dispose
//			//
//			// if (showPropertyPage) {
//			// PreferencesUtil.createPropertyDialogOn(shell, fProject, pageId,
//			// null, data).open();
//			// } else {
//			// PreferencesUtil.createPreferenceDialogOn(shell, pageId, null,
//			// data).open();
//			// }
//		}
//
//		private boolean hasProjectSpecificOptions() {
//			return false;
//			// return
//			// OptionsConfigurationBlock.hasProjectSpecificOptions(fProject.getProject(),
//			// ProblemSeveritiesConfigurationBlock.getKeys(), null);
//		}
//	}

	protected static class ProblemInfo extends AnnotationInfo {
		private static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];

		public ProblemInfo(Annotation annotation, Position position,
				ITextViewer textViewer) {
			super(annotation, position, textViewer);
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.java.hover.AbstractAnnotationHover
		 * .AnnotationInfo#getCompletionProposals()
		 */
		public ICompletionProposal[] getCompletionProposals() {
			if (annotation instanceof CeylonAnnotation) {
				ICompletionProposal[] result = getAnnotationFixes((CeylonAnnotation) annotation);
				if (result.length > 0)
					return result;
			}

			if (annotation instanceof MarkerAnnotation) {
				return getMarkerAnnotationFixes((MarkerAnnotation) annotation);
			}
			return NO_PROPOSALS;
		}

		private ICompletionProposal[] getAnnotationFixes(CeylonAnnotation annotation) {
			CeylonQuickFixController qac = new CeylonQuickFixController(annotation.getEditor());
			
			final ProblemLocation location = new ProblemLocation(position
					.getOffset(), position.getLength(), annotation);
			
			IQuickAssistInvocationContext quickAssistContext = new IQuickAssistInvocationContext() {
				public ISourceViewer getSourceViewer() {
					if (viewer instanceof ISourceViewer)
						return (ISourceViewer) viewer;
					return null;
				}

				public int getOffset() {
					return location.getOffset();
				}

				public int getLength() {
					return location.getLength();
				}
			};

			ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
			qac.collectCorrections(qac.getContext(quickAssistContext),
					new ProblemLocation[] { location }, proposals);
			// Collections.sort(proposals, new CompletionProposalComparator());

			return (ICompletionProposal[]) proposals
					.toArray(new ICompletionProposal[proposals.size()]);
		}

		private ICompletionProposal[] getMarkerAnnotationFixes(MarkerAnnotation markerAnnotation) {
			if (markerAnnotation.isQuickFixableStateSet() && !markerAnnotation.isQuickFixable())
				return NO_PROPOSALS;

			TextInvocationContext context = new TextInvocationContext(
					((ISourceViewer) this.viewer), position.getOffset(),
					position.getLength());
			CeylonQuickFixController c = new CeylonQuickFixController(markerAnnotation.getMarker());
			return c.computeQuickAssistProposals(context);
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.java.hover.AbstractAnnotationHover
		 * .AnnotationInfo#fillToolBar(org.eclipse.jface.action.ToolBarManager)
		 */
		public void fillToolBar(ToolBarManager manager, IInformationControl infoControl) {
			super.fillToolBar(manager, infoControl);
			// if (!(annotation instanceof IJavaAnnotation))
			// return;

			// IJavaAnnotation javaAnnotation= (IJavaAnnotation) annotation;
			//
			// String optionId=
			// JavaCore.getOptionForConfigurableSeverity(javaAnnotation.getId());
			// if (optionId != null) {
			// IJavaProject javaProject=
			// javaAnnotation.getCompilationUnit().getJavaProject();
			// boolean isJavadocProblem= (javaAnnotation.getId() &
			// IProblem.Javadoc) != 0;
			// ConfigureProblemSeverityAction problemSeverityAction= new
			// ConfigureProblemSeverityAction(javaProject, optionId,
			// isJavadocProblem, infoControl);
			// manager.add(problemSeverityAction);
			// }
		}

	}

	public ProblemHover() {
		super(false);
	}

	protected AnnotationInfo createAnnotationInfo(Annotation annotation,
			Position position, ITextViewer textViewer) {
		return new ProblemInfo(annotation, position, textViewer);
	}
}
