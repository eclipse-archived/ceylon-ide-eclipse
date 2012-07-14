package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonClassesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.EDITOR_ID;
import static java.lang.Character.isJavaIdentifierPart;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.editor.EditorUtility;
import org.eclipse.imp.editor.IRegionSelectionService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Util;

public class JavaToCeylonHyperlinkDetector extends AbstractHyperlinkDetector {


	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			final IRegion region, boolean canShowMultipleHyperlinks) {
		try {
			final IDocument doc = textViewer.getDocument();
			ICompilationUnit cu = (ICompilationUnit) JavaCore.create(Util.getFile(Util.getCurrentEditor().getEditorInput()));
			if (cu==null) return null;
			IJavaElement[] selection = cu.codeSelect(region.getOffset(), region.getLength());
			for (final IJavaElement je: selection) {
				final IProject p = je.getJavaProject().getProject();
				if (isExplodeModulesEnabled(p)) {
					if (getCeylonClassesOutputFolder(p).getFullPath()
							.isPrefixOf(je.getPath())) {
						return new IHyperlink[] {
								new IHyperlink() {
									@Override
									public void open() {
										for (PhasedUnit pu: getProjectTypeChecker(p).getPhasedUnits().getPhasedUnits()) {
											for (Declaration d: pu.getDeclarations()) {
												//TODO: the following is not quite right because
												//      there can be multiple declarations with
												//      the same (unqualified) name in a unit
												if (d.getName().equals(je.getElementName())) {
													IEditorInput editorInput = EditorUtility.getEditorInput(p.findMember(pu.getUnitFile().getPath()));
													try {
														CeylonEditor editor = (CeylonEditor) Util.getActivePage().openEditor(editorInput, EDITOR_ID);
														int offset = getIdentifyingNode(getReferencedNode(d, pu.getCompilationUnit())).getStartIndex();
														IRegionSelectionService rss = (IRegionSelectionService) editor.getAdapter(IRegionSelectionService.class);
														rss.selectAndReveal(offset, 0);
													} 
													catch (PartInitException e) {
														e.printStackTrace();
													}
												}
											}
										}
									}
									@Override
									public String getTypeLabel() {
										return null;
									}
									@Override
									public String getHyperlinkText() {
										return "Open Ceylon Declaration";
									}
									@Override
									public IRegion getHyperlinkRegion() {
										int offset = region.getOffset();
										int length = region.getLength();
										try {
											while (isJavaIdentifierPart(doc.getChar(offset-1))) {
												offset--;
											}
											while (isJavaIdentifierPart(doc.getChar(offset+length))) {
												length++;
											}
										} 
										catch (BadLocationException e) {
											e.printStackTrace();
										}
										return new Region(offset, length);
									}
								}
						};
					}
				}		
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
