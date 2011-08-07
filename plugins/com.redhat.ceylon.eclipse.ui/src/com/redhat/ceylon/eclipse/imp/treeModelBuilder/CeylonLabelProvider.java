package com.redhat.ceylon.eclipse.imp.treeModelBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.services.ILabelProvider;
import org.eclipse.imp.language.ILanguageService;
import com.redhat.celyon.eclipse.ui.CeylonPlugin;
import com.redhat.celyon.eclipse.ui.ICeylonResources;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Annotation;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnnotationList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Primary;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.StaticMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.treeModelBuilder.CeylonTreeModelBuilder.CeylonModelVisitor;

import org.eclipse.imp.utils.MarkerUtils;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;


public class CeylonLabelProvider implements ILabelProvider {
	private Set<ILabelProviderListener> fListeners = new HashSet<ILabelProviderListener>();

	private static ImageRegistry sImageRegistry = CeylonPlugin.getInstance()
			.getImageRegistry();

	private static Image DEFAULT_IMAGE = sImageRegistry
			.get(ICeylonResources.CEYLON_DEFAULT_IMAGE);
	private static Image FILE_IMAGE = sImageRegistry
			.get(ICeylonResources.CEYLON_FILE);
	private static Image FILE_WITH_WARNING_IMAGE = sImageRegistry
			.get(ICeylonResources.CEYLON_FILE_WARNING);
	private static Image FILE_WITH_ERROR_IMAGE = sImageRegistry
			.get(ICeylonResources.CEYLON_FILE_ERROR);

	public Image getImage(Object element) {
		if (element instanceof IFile) {
			// TODO:  rewrite to provide more appropriate images
			IFile file = (IFile) element;
			int sev = MarkerUtils.getMaxProblemMarkerSeverity(file,
					IResource.DEPTH_ONE);

			switch (sev) {
			case IMarker.SEVERITY_ERROR:
				return FILE_WITH_ERROR_IMAGE;
			case IMarker.SEVERITY_WARNING:
				return FILE_WITH_WARNING_IMAGE;
			default:
				return FILE_IMAGE;
			}
		}
		ModelTreeNode n = (ModelTreeNode) element;
		
		return getImageFor(n);
	}

	private Image getImageFor(ModelTreeNode n) {
		if (n.getCategory()==-1) return null;
		return getImageFor((Node) n.getASTNode());
	}

	public static Image getImageFor(Node n) {
		
		// TODO:  return specific images for specific node
		// types, as images are available and appropriate
		return DEFAULT_IMAGE;
	}

	public String getText(Object element) {
		ModelTreeNode n = (ModelTreeNode) element;
		return getLabelFor(n);
	}

	private String getLabelFor(ModelTreeNode n) {
		return getLabelFor((Node) n.getASTNode());
	}

	public static String getLabelFor(Node n) {
					
		String label = null;
		
		if(n instanceof Annotation) { // Annotations should actually be part of the declaration
 			Annotation a = (Annotation) n;
			Primary primary = a.getPrimary();
			if(primary instanceof StaticMemberOrTypeExpression) {
				StaticMemberOrTypeExpression smote = (StaticMemberOrTypeExpression) primary;
				label = smote.getIdentifier().getText();
			}
			
		}
		if(n instanceof Declaration) {
			Declaration d = ((Declaration)n);
			
			/*if (d.getAnnotationList() != null) {
				label = "";

				List<Annotation> annotationList = d.getAnnotationList()
						.getAnnotations();
				for (Annotation annotation : annotationList) {
					BaseMemberExpression primary = (BaseMemberExpression) annotation
							.getPrimary();
					if(primary.getIdentifier().getText()!=null) {
						label += primary.getIdentifier().getText() + " ";
					}
				}
			} */
			label = (label==null?"":label) + d.getIdentifier().getText();
		}
		
		if(label==null) {
			label = n.getNodeType() + " : " + n.getText();
		} 
		
			return label + " " + n.getNodeType();
	}	

	public void addListener(ILabelProviderListener listener) {
		fListeners.add(listener);
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		fListeners.remove(listener);
	}
}
