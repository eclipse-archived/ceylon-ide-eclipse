package com.redhat.ceylon.eclipse.code.select;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

public class SourceFileSelectionDialog extends ElementTreeSelectionDialog {

	private class PackageAndProjectSelectionValidator extends TypedElementSelectionValidator {

		public PackageAndProjectSelectionValidator() {
			super(new Class[]{IFile.class},false);
		}

		@Override
		public boolean isSelectedValid(Object element) {
		    if (element instanceof IFile) {
                IFile file = (IFile) element;
                return file.getFileExtension().equals("ceylon") &&
                        !file.getName().equals("package.ceylon") &&
                        !file.getName().equals("module.ceylon");
            }
		    else {
		        return false;
		    }
		}
	}

	private class SourceFileViewerFilter extends TypedViewerFilter {

		public SourceFileViewerFilter() {
			super(new Class[]{IPackageFragmentRoot.class, IJavaProject.class});
		}

		@Override
		public boolean select(Viewer viewer, Object parent, Object element) {
			if (element instanceof IPackageFragmentRoot) {
				IPackageFragmentRoot fragmentRoot= (IPackageFragmentRoot)element;
				try {
					return (fragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE);
				}
				catch (Exception e) {
					return false;
				}
			}
			if (element instanceof IPackageFragment) {
			    try {
                    for (IResource child: ((IFolder) ((IPackageFragment) element).getResource()).members()) {
                        if (child instanceof IFile) {
                            if (child.getFileExtension().equals("ceylon")) {
                                return true;
                            }
                        }
                    }
                }
			    catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
			}
			if (element instanceof IFile) {
			    return ((IFile) element).getFileExtension().equals("ceylon");
			}
			return super.select(viewer, parent, element);
		}
	}

	private SourceFileSelectionDialog(Shell shell) {
		super(shell,new CeylonLabelProvider(false),new StandardJavaElementContentProvider());
		setValidator(new PackageAndProjectSelectionValidator());
		setComparator(new JavaElementComparator());
		setTitle("Source File Selection");
		setMessage("&Choose a source file:");
		addFilter(new SourceFileViewerFilter());
	}

	public static IFile getSourceFile(Shell shell, IWorkspaceRoot workspaceRoot, IJavaElement initElement) {
		SourceFileSelectionDialog dialog= new SourceFileSelectionDialog(shell);
		dialog.setInput(JavaCore.create(workspaceRoot));
		dialog.setInitialSelection(initElement);

		if (dialog.open() == Window.OK) {
			Object element= dialog.getFirstResult();
			if (element instanceof IFile) {
			    return (IFile) element;
			}
		}
		return null;
	}
}
