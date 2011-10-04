package com.redhat.ceylon.eclipse.imp.outline;

import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_ATTRIBUTE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_CLASS;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_CORRECTION;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FILE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FILE_ERROR;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FILE_WARNING;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_IMPORT;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_IMPORT_LIST;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_INTERFACE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_ATTRIBUTE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_CLASS;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_INTERFACE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_METHOD;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_METHOD;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_PACKAGE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_PROJECT;
import static org.eclipse.imp.utils.MarkerUtils.getMaxProblemMarkerSeverity;
import static org.eclipse.jface.viewers.StyledString.COUNTER_STYLER;
import static org.eclipse.jface.viewers.StyledString.QUALIFIER_STYLER;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.services.ILabelProvider;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Util;
import com.redhat.ceylon.eclipse.imp.search.CeylonElement;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * Styled Label Provider which can be used to provide labels for Ceylon elements.
 * 
 * Needs to explicitly implement org.eclipse.imp.services.ILabelProvider to show up in IMP servicees.
 * Extends StyledCellLabelProvider to provide custom styling by doing its own painting - here the {@link #update(ViewerCell)} method is the entry point
 * Implements DelegatingStyledCellLabelProvider.IStyledLabelProvider too, but this probably is not required.
 * @author max
 *
 */
public class CeylonLabelProvider extends StyledCellLabelProvider 
        implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, ILabelProvider {
    
    private Set<ILabelProviderListener> fListeners = new HashSet<ILabelProviderListener>();
    
    public static ImageRegistry imageRegistry = CeylonPlugin.getInstance()
            .getImageRegistry();
    
    public static Image FILE_IMAGE = imageRegistry.get(CEYLON_FILE);
    private static Image FILE_WITH_WARNING_IMAGE = imageRegistry.get(CEYLON_FILE_WARNING);
    private static Image FILE_WITH_ERROR_IMAGE = imageRegistry.get(CEYLON_FILE_ERROR);
    
    public static Image CLASS = imageRegistry.get(CEYLON_CLASS);
    private static Image INTERFACE = imageRegistry.get(CEYLON_INTERFACE);
    private static Image LOCAL_CLASS = imageRegistry.get(CEYLON_LOCAL_CLASS);
    private static Image LOCAL_INTERFACE = imageRegistry.get(CEYLON_LOCAL_INTERFACE);
    public static Image METHOD = imageRegistry.get(CEYLON_METHOD);
    public static Image ATTRIBUTE = imageRegistry.get(CEYLON_ATTRIBUTE);
    private static Image LOCAL_METHOD = imageRegistry.get(CEYLON_LOCAL_METHOD);
    private static Image LOCAL_ATTRIBUTE = imageRegistry.get(CEYLON_LOCAL_ATTRIBUTE);
    public static Image PACKAGE = imageRegistry.get(CEYLON_PACKAGE);
    public static Image IMPORT = imageRegistry.get(CEYLON_IMPORT);
    private static Image IMPORT_LIST = imageRegistry.get(CEYLON_IMPORT_LIST);
    public static Image PROJECT = imageRegistry.get(CEYLON_PROJECT);
    public static  Image CORRECTION = imageRegistry.get(CEYLON_CORRECTION);
    
    
    private final boolean includePackage;
    
    public CeylonLabelProvider() {
        this(true);
    }
    
    public CeylonLabelProvider(boolean includePackage) {
        this.includePackage = includePackage;
    }
    
    @Override
    public Image getImage(Object element) {
        if (element instanceof IFile) {
            return getImageForFile((IFile) element);
        }
        if (element instanceof IProject) {
            return PROJECT;
        }
        if (element instanceof org.eclipse.core.runtime.Path) {
            return getImageForPath((IPath) element);
        }
        if (element instanceof CeylonElement) {
            return getImageFor(((CeylonElement) element).getNode());
        }
        if (element instanceof Package) {
            return PACKAGE;
        }
        if (element instanceof Unit) {
            return FILE_IMAGE;
        }
        if (element instanceof ModelTreeNode) {
            return getImageFor((ModelTreeNode) element);
        }
        if (element instanceof Node) {
            return getImageFor((Node) element);
        }
        return FILE_IMAGE;
    }
    
    private Image getImageForPath(IPath element) {
        return FILE_IMAGE;
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
        if (d==null) return null;
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
    
    @Override
    public StyledString getStyledText(Object element) {
        if (element instanceof ModelTreeNode) {
            return getStyledLabelFor((Node) ((ModelTreeNode) element).getASTNode());
        }
        else if (element instanceof IFile) {
            return new StyledString(getLabelForFile((IFile) element));
        }
        else if (element instanceof IProject) {
            return new StyledString(((IProject) element).getName());
        }
        else if (element instanceof CeylonElement) {
            CeylonElement ce = (CeylonElement) element;
            String pkg;
            if (includePackage()) {
                pkg = " - " + getPackageLabel(ce.getNode());
            }
            else {
                pkg = "";
            }
            return getStyledLabelFor(ce.getNode())
                    .append(pkg, QUALIFIER_STYLER)
                    .append(" - " + ce.getFile().getFullPath().toString(), COUNTER_STYLER)
                    .append(":" + ce.getLocation(), COUNTER_STYLER);
        }
        else if (element instanceof Package) {
            return new StyledString(" - " + getLabel((Package) element));
        }
        else if (element instanceof Unit) {
            return new StyledString(((Unit) element).getFilename());
        }
        else {
            return getStyledLabelFor((Node) element);
        }
    }
    
    public String getText(Object element) {
        return getStyledText(element).toString();
    }
    
    protected boolean includePackage() {
        return includePackage;
    }
    
    
    private String getLabelForFile(IFile file) {
        return file.getName();
    }
    
    static StyledString getStyledLabelFor(Node n) {
        //TODO: it would be much better to render types
        //      from the tree nodes instead of from the
        //      model nodes
        
        if (n instanceof Tree.TypeParameterDeclaration) {
            Tree.TypeParameterDeclaration ac = (Tree.TypeParameterDeclaration) n;
            return new StyledString(name(ac.getIdentifier()));
        }
        if (n instanceof Tree.AnyClass) {
            Tree.AnyClass ac = (Tree.AnyClass) n;
            return new StyledString("class ", QUALIFIER_STYLER)
                    .append(name(ac.getIdentifier()))
                    .append(parameters(ac.getTypeParameterList()) + 
                            parameters(ac.getParameterList()), QUALIFIER_STYLER);
        }
        else if (n instanceof Tree.AnyInterface) {
            Tree.AnyInterface ai = (Tree.AnyInterface) n;
            return new StyledString("interface ", QUALIFIER_STYLER)
                    .append(name(ai.getIdentifier()))
                    .append(parameters(ai.getTypeParameterList()), QUALIFIER_STYLER);
        }
        else if (n instanceof Tree.ObjectDefinition) {
            Tree.ObjectDefinition ai = (Tree.ObjectDefinition) n;
            return new StyledString("object ", QUALIFIER_STYLER)
                    .append(name(ai.getIdentifier()));
        }
        else if (n instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration td = (Tree.TypedDeclaration) n;
            StyledString label = new StyledString(type(td.getType()), QUALIFIER_STYLER)
                    .append(" ").append(name(td.getIdentifier()));
            if (n instanceof Tree.AnyMethod) {
                Tree.AnyMethod am = (Tree.AnyMethod) n;
                label.append(parameters(am.getTypeParameterList()) +
                        (am.getParameterLists().isEmpty() ? "" : 
                            parameters(am.getParameterLists().get(0))), QUALIFIER_STYLER);
            }
            return label;
        }
        else if (n instanceof Tree.CompilationUnit) {
            Tree.CompilationUnit ai = (Tree.CompilationUnit) n;
            return new StyledString(ai.getUnit().getFilename());
        }
        else if (n instanceof Tree.ImportList) {
            return new StyledString("imports");
        }
        else if (n instanceof Tree.Import) {
            Tree.Import ai = (Tree.Import) n;
            String path="";
            if (ai.getImportPath()!=null &&
                    !ai.getImportPath().getIdentifiers().isEmpty()) {
                for (Tree.Identifier id: ai.getImportPath().getIdentifiers()) {
                    path+="." + id.getText();
                }
                return new StyledString(path.substring(1));
            }
        }
        else if (n instanceof PackageNode) {
            PackageNode pn = (PackageNode) n;
            return new StyledString(pn.getPackageName().isEmpty() ? 
                    "default package" : pn.getPackageName());
        }
        
        return new StyledString("<something>");
    }
    
    public static String getLabelFor(Node n) {
        return getStyledLabelFor(n).toString(); 
        
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
    
    public static String getLabel(Package packageModel) {
        String pkg = packageModel.getQualifiedNameString();
        if (pkg.isEmpty()) pkg="default package";
        return pkg;
    }
    
    public static String getPackageLabel(Node decl) {
        return decl.getUnit()==null ? "unknown package" : 
            getLabel(decl.getUnit().getPackage());
    }
    
    public static String getPackageLabel(Declaration decl) {
        return getLabel(decl.getUnit().getPackage());
    }
    
    
    @Override
    public void update(ViewerCell cell) {
        Object element = cell.getElement();
        
        StyledString styledText = getStyledText(element);
        
        cell.setText(styledText.toString());
        cell.setStyleRanges(styledText.getStyleRanges());
        cell.setImage(getImage(element));
        super.update(cell);
    }
    
}
