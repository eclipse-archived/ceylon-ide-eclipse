package com.redhat.ceylon.eclipse.imp.treeModelBuilder;

import static org.eclipse.imp.utils.MarkerUtils.getMaxProblemMarkerSeverity;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.services.ILabelProvider;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Util;
import com.redhat.ceylon.eclipse.imp.editorActionContributions.CeylonElement;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.ICeylonResources;


public class CeylonLabelProvider implements ILabelProvider {
  private Set<ILabelProviderListener> fListeners = new HashSet<ILabelProviderListener>();

  public static ImageRegistry imageRegistry = CeylonPlugin.getInstance()
      .getImageRegistry();

  public static Image FILE_IMAGE = imageRegistry
      .get(ICeylonResources.CEYLON_FILE);
  private static Image FILE_WITH_WARNING_IMAGE = imageRegistry
      .get(ICeylonResources.CEYLON_FILE_WARNING);
  private static Image FILE_WITH_ERROR_IMAGE = imageRegistry
      .get(ICeylonResources.CEYLON_FILE_ERROR);

  private static Image CLASS = imageRegistry
      .get(ICeylonResources.CEYLON_CLASS);
  private static Image INTERFACE = imageRegistry
      .get(ICeylonResources.CEYLON_INTERFACE);
  private static Image LOCAL_CLASS = imageRegistry
      .get(ICeylonResources.CEYLON_LOCAL_CLASS);
  private static Image LOCAL_INTERFACE = imageRegistry
      .get(ICeylonResources.CEYLON_LOCAL_INTERFACE);
  private static Image METHOD = imageRegistry
      .get(ICeylonResources.CEYLON_METHOD);
  private static Image ATTRIBUTE = imageRegistry
      .get(ICeylonResources.CEYLON_ATTRIBUTE);
  private static Image LOCAL_METHOD = imageRegistry
      .get(ICeylonResources.CEYLON_LOCAL_METHOD);
  private static Image LOCAL_ATTRIBUTE = imageRegistry
      .get(ICeylonResources.CEYLON_LOCAL_ATTRIBUTE);
  private static Image PACKAGE = imageRegistry
	      .get(ICeylonResources.CEYLON_PACKAGE);
  private static Image IMPORT = imageRegistry
	      .get(ICeylonResources.CEYLON_IMPORT);
  private static Image IMPORT_LIST = imageRegistry
	      .get(ICeylonResources.CEYLON_IMPORT_LIST);


  @Override
  public Image getImage(Object element) {
    if (element instanceof IFile) {
      return getImageForFile((IFile) element);
    }
    else if (element instanceof CeylonElement) {
      return getImageFor(((CeylonElement) element).getNode());
    }
    else {
      return getImageFor((ModelTreeNode) element);
    }
  }

  private Image getImageForFile(IFile file) {
    int sev = getMaxProblemMarkerSeverity(file, IResource.DEPTH_ONE);
    switch (sev) {
    case IMarker.SEVERITY_ERROR:
      return FILE_WITH_ERROR_IMAGE;
    case IMarker.SEVERITY_WARNING:
      return FILE_WITH_WARNING_IMAGE;
    default:
      return FILE_IMAGE;
    }
  }

  private Image getImageFor(ModelTreeNode n) {
    if (n.getCategory()==-1) return null;
    return getImageFor((Node) n.getASTNode());
  }

  public static Image getImageFor(Node n) {
    if (n instanceof PackageNode) {
      return PACKAGE;
    }
    else if (n instanceof Tree.CompilationUnit) {
      return FILE_IMAGE;
    }
    else if (n instanceof Tree.ImportList) {
        return IMPORT_LIST;
      }
    else if (n instanceof Tree.Import) {
        return IMPORT;
      }
    else if (n instanceof Tree.Declaration) {
      Tree.Declaration d = (Tree.Declaration) n;
      boolean shared = Util.hasAnnotation(d.getAnnotationList(), "shared");
      return getImage(n, shared);
    }
    else {
      return null;
    }
  }

  private static Image getImage(Node n, boolean shared) {
    if (n instanceof Tree.AnyClass) {
      if (shared) {
        return CLASS;
      }
      else {
        return LOCAL_CLASS;
      }
    }
    else if (n instanceof Tree.AnyInterface) {
      if (shared) {
        return INTERFACE;
      }
      else { 
        return LOCAL_INTERFACE;
      }
    }
    else if (n instanceof Tree.AnyMethod) {
      if (shared) {
        return METHOD;
      }
      else {
        return LOCAL_METHOD;
      }
    }
    else {
      if (shared) {
        return ATTRIBUTE;
      }
      else {
        return LOCAL_ATTRIBUTE;
      }
    }
  }

  public static Image getImage(Declaration d) {
    boolean shared = d.isShared();
    if (d instanceof Class) {
      if (shared) {
        return CLASS;
      }
      else {
        return LOCAL_CLASS;
      }
    }
    else if (d instanceof Interface) {
      if (shared) {
        return INTERFACE;
      }
      else { 
        return LOCAL_INTERFACE;
      }
    }
    else if (d instanceof Method) {
      if (shared) {
        return METHOD;
      }
      else {
        return LOCAL_METHOD;
      }
    }
    else {
      if (shared) {
        return ATTRIBUTE;
      }
      else {
        return LOCAL_ATTRIBUTE;
      }
    }
  }

  public String getText(Object element) {
    if (element instanceof ModelTreeNode) {
      return getLabelFor((Node) ((ModelTreeNode) element).getASTNode());
    }
    else if (element instanceof IFile) {
      return getLabelForFile((IFile) element);
    }
    else if (element instanceof CeylonElement) {
      CeylonElement ce = (CeylonElement) element;
      return getLabelFor(ce.getNode()) +
    		  " in " + ce.getFile().getName() + 
    		  " at line " + ce.getLocation();
    }
    else {
      return getLabelFor((Node) element);
    }
  }

  private String getLabelForFile(IFile file) {
	return file.getName();
  }

  public static String getLabelFor(Node n) {
    
    //TODO: it would be much better to render types
    //      from the tree nodes instead of from the
    //      model nodes
    
    if (n instanceof Tree.TypeParameterDeclaration) {
      Tree.TypeParameterDeclaration ac = (Tree.TypeParameterDeclaration) n;
      return name(ac.getIdentifier());
    }
    if (n instanceof Tree.AnyClass) {
      Tree.AnyClass ac = (Tree.AnyClass) n;
      return "class " + name(ac.getIdentifier()) +
          parameters(ac.getTypeParameterList()) +
          parameters(ac.getParameterList());
    }
    else if (n instanceof Tree.AnyInterface) {
      Tree.AnyInterface ai = (Tree.AnyInterface) n;
      return "interface " + name(ai.getIdentifier()) + 
          parameters(ai.getTypeParameterList());
    }
    else if (n instanceof Tree.ObjectDefinition) {
      Tree.ObjectDefinition ai = (Tree.ObjectDefinition) n;
      return "object " + name(ai.getIdentifier());
    }
    else if (n instanceof Tree.TypedDeclaration) {
      Tree.TypedDeclaration td = (Tree.TypedDeclaration) n;
      String label = type(td.getType()) + 
          " " + name(td.getIdentifier());
      if (n instanceof Tree.AnyMethod) {
        Tree.AnyMethod am = (Tree.AnyMethod) n;
        label += parameters(am.getTypeParameterList()) +
        		(am.getParameterLists().isEmpty() ? "" : 
        			parameters(am.getParameterLists().get(0)));
      }
      return label;
    }
    else if (n instanceof Tree.CompilationUnit) {
      Tree.CompilationUnit ai = (Tree.CompilationUnit) n;
      return ai.getUnit().getFilename();
    }
    else if (n instanceof Tree.ImportList) {
        return "import list";
      }
    else if (n instanceof Tree.Import) {
        Tree.Import ai = (Tree.Import) n;
        String path="";
        for (Tree.Identifier id: ai.getImportPath().getIdentifiers()) {
          path+="." + id.getText();
        }
        return path.substring(1);
      }
    else if (n instanceof PackageNode) {
      PackageNode pn = (PackageNode) n;
      return pn.getPackageName().isEmpty() ? 
               "default package" : 
               pn.getPackageName();
    }
        
    return "<something>";
  }
  
  private static String type(Tree.Type type) {
    if (type==null) {
      return "<Unknown>";
    }
    else {
      ProducedType tm = type.getTypeModel();
      return tm==null ? "<Unknown>" : tm.getProducedTypeName();
    }
  }
  
  private static String name(Tree.Identifier id) {
    if (id==null) {
      return "<unknown>";
    }
    else {
      return id.getText();
    }
  }

  private static String parameters(Tree.ParameterList pl) {
    if (pl==null ||
        pl.getParameters().isEmpty()) {
      return "()";
    }
    String label = "(";
    for (Tree.Parameter p: pl.getParameters()) {
      label += type(p.getType()) + 
          " " + name(p.getIdentifier()) + ", ";
    }
    return label.substring(0, label.length()-2) + ")";
  }

  private static String parameters(Tree.TypeParameterList tpl) {
    if (tpl==null ||
        tpl.getTypeParameterDeclarations().isEmpty()) {
      return "";
    }
    String label = "<";
    for (Tree.TypeParameterDeclaration p: tpl.getTypeParameterDeclarations()) {
      label += name(p.getIdentifier()) + ", ";
    }
    return label.substring(0, label.length()-2) + ">";
  }

  public void addListener(ILabelProviderListener listener) {
    fListeners.add(listener);
  }

  public void dispose() {}

  public boolean isLabelProperty(Object element, String property) {
    return false;
  }

  public void removeListener(ILabelProviderListener listener) {
    fListeners.remove(listener);
  }
}
