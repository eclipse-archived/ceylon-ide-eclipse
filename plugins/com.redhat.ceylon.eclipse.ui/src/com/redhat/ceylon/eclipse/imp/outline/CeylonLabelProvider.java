package com.redhat.ceylon.eclipse.imp.outline;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.hasAnnotation;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonTokenColorer.ANNOTATIONS;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonTokenColorer.KEYWORDS;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonTokenColorer.TYPES;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonTokenColorer.IDENTIFIERS;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonTokenColorer.color;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_ATTRIBUTE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_CLASS;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_ABSTRACT_CLASS;
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
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_PARAMETER;
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
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.services.ILabelProvider;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
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
    
    private static CeylonLabelDecorator DECORATOR = new CeylonLabelDecorator(LanguageRegistry.findLanguage(CeylonPlugin.LANGUAGE_ID));
    
    private Set<ILabelProviderListener> fListeners = new HashSet<ILabelProviderListener>();
    
    public static ImageRegistry imageRegistry = CeylonPlugin.getInstance()
            .getImageRegistry();
    
    public static Image FILE_IMAGE = imageRegistry.get(CEYLON_FILE);
    private static Image FILE_WITH_WARNING_IMAGE = imageRegistry.get(CEYLON_FILE_WARNING);
    private static Image FILE_WITH_ERROR_IMAGE = imageRegistry.get(CEYLON_FILE_ERROR);
    
    public static Image CLASS = imageRegistry.get(CEYLON_CLASS);
    public static Image ABSTRACT_CLASS = imageRegistry.get(CEYLON_ABSTRACT_CLASS);
    private static Image INTERFACE = imageRegistry.get(CEYLON_INTERFACE);
    private static Image LOCAL_CLASS = imageRegistry.get(CEYLON_LOCAL_CLASS);
    private static Image LOCAL_INTERFACE = imageRegistry.get(CEYLON_LOCAL_INTERFACE);
    public static Image METHOD = imageRegistry.get(CEYLON_METHOD);
    public static Image ATTRIBUTE = imageRegistry.get(CEYLON_ATTRIBUTE);
    private static Image LOCAL_METHOD = imageRegistry.get(CEYLON_LOCAL_METHOD);
    private static Image LOCAL_ATTRIBUTE = imageRegistry.get(CEYLON_LOCAL_ATTRIBUTE);
    public static Image PARAMETER = imageRegistry.get(CEYLON_PARAMETER);
    public static Image PACKAGE = imageRegistry.get(CEYLON_PACKAGE);
    public static Image IMPORT = imageRegistry.get(CEYLON_IMPORT);
    private static Image IMPORT_LIST = imageRegistry.get(CEYLON_IMPORT_LIST);
    public static Image PROJECT = imageRegistry.get(CEYLON_PROJECT);
    public static  Image CORRECTION = imageRegistry.get(CEYLON_CORRECTION);
    
    private static ColorRegistry colorRegistry = PlatformUI.getWorkbench()
            .getThemeManager().getCurrentTheme().getColorRegistry();
    
    public static final Styler ID_STYLER = new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground=color(colorRegistry, IDENTIFIERS);
        }
    };
    
    public static final Styler TYPE_ID_STYLER = new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground=color(colorRegistry, TYPES);
        }
    };
    
    public static final Styler TYPE_STYLER = new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground=color(colorRegistry, TYPES);
        }
    };
    
    public static final Styler KW_STYLER = new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground=color(colorRegistry, KEYWORDS);
        }
    };
    
    public static final Styler ANN_STYLER = new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground=color(colorRegistry, ANNOTATIONS);
        }
    };
    
    private final boolean includePackage;
    
    public CeylonLabelProvider() {
        this(true);
    }
    
    public CeylonLabelProvider(boolean includePackage) {
        this.includePackage = includePackage;
    }
    
    @Override
    public Image getImage(Object element) {
        return DECORATOR.decorateImage(getPlainImage(element), element);
    }

    private static Image getPlainImage(Object element) {
        if (element instanceof IFile) {
            return getImageForFile((IFile) element);
        }
        if (element instanceof IProject) {
            return PROJECT;
        }
        if (element instanceof IPath) {
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
    
    private static Image getImageForPath(IPath element) {
        return FILE_IMAGE;
    }
    
    private static Image getImageForFile(IFile file) {
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
    
    private static Image getImageFor(ModelTreeNode n) {
        if (n.getCategory()==-1) return null;
        return getImageFor((Node) n.getASTNode());
    }
    
    private static Image getImageFor(Node n) {
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
                return hasAnnotation(((Tree.AnyClass) n).getAnnotationList(), 
                        "abstract") ? ABSTRACT_CLASS : CLASS;
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
        else if (n instanceof Tree.Parameter) {
            return PARAMETER;
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
        return DECORATOR.decorateImage(getPlainImage(d), d);
    }

    private static Image getPlainImage(Declaration d) {
        boolean shared = d.isShared();
        if (d instanceof Class) {
            if (shared) {
                return ((Class) d).isAbstract() ? 
                        ABSTRACT_CLASS : CLASS;
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
        else if (d instanceof Parameter) {
            return PARAMETER;
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
            return new StyledString(getLabel((Package) element), QUALIFIER_STYLER);
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
            StyledString label = new StyledString("class ", KW_STYLER);
            label.append(name(ac.getIdentifier()), TYPE_ID_STYLER);
            parameters(ac.getTypeParameterList(), label);
            parameters(ac.getParameterList(), label);
            return label;
        }
        else if (n instanceof Tree.AnyInterface) {
            Tree.AnyInterface ai = (Tree.AnyInterface) n;
            StyledString label = new StyledString("interface ", KW_STYLER);
            label.append(name(ai.getIdentifier()), TYPE_ID_STYLER);
            parameters(ai.getTypeParameterList(), label);
            return label;
        }
        else if (n instanceof Tree.ObjectDefinition) {
            Tree.ObjectDefinition ai = (Tree.ObjectDefinition) n;
            return new StyledString("object ", KW_STYLER)
                    .append(name(ai.getIdentifier()), ID_STYLER);
        }
        else if (n instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration td = (Tree.TypedDeclaration) n;
            String type = type(td.getType());
            Styler styler = TYPE_STYLER;
            if (type.equals("Void")) {
                type = "void"; //TODO: fix!
                styler = KW_STYLER;
            }
            StyledString label = new StyledString(type, styler);
            label.append(" ").append(name(td.getIdentifier()), ID_STYLER);
            if (n instanceof Tree.AnyMethod) {
                Tree.AnyMethod am = (Tree.AnyMethod) n;
                parameters(am.getTypeParameterList(), label);
                for (Tree.ParameterList pl: am.getParameterLists()) { 
                    parameters(pl, label);
                }
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
            if (ai.getImportPath()!=null &&
                    !ai.getImportPath().getIdentifiers().isEmpty()) {
                return new StyledString(toPath(ai), QUALIFIER_STYLER);
            }
        }
        else if (n instanceof PackageNode) {
            PackageNode pn = (PackageNode) n;
            if (pn.getPackageName().isEmpty()) {
                return new StyledString("default package");
            }
            else {
                return new StyledString(pn.getPackageName(), QUALIFIER_STYLER);
            }
        }
        
        return new StyledString("<something>");
    }

    private static String toPath(Tree.Import ai) {
        String path="";
        for (Tree.Identifier id: ai.getImportPath().getIdentifiers()) {
            path+="." + id.getText();
        }
        path = path.substring(1);
        return path;
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
    
    private static void parameters(Tree.ParameterList pl, StyledString label) {
        if (pl==null ||
                pl.getParameters().isEmpty()) {
            label.append("()");
        }
        else {
            label.append("(");
            int len = pl.getParameters().size(), i=0;
            for (Tree.Parameter p: pl.getParameters()) {
                if (p!=null) {
                    label.append(type(p.getType()), TYPE_STYLER) 
                            .append(" ")
                            .append(name(p.getIdentifier()), ID_STYLER);
                    if (p instanceof Tree.FunctionalParameterDeclaration) {
                        Tree.FunctionalParameterDeclaration fp = (Tree.FunctionalParameterDeclaration) p;
                        for (Tree.ParameterList ipl: fp.getParameterLists()) {
                            parameters(ipl, label);
                        }
                    }
                }
                if (++i<len) label.append(", ");
            }
            label.append(")");
        }
    }
    
    private static void parameters(Tree.TypeParameterList tpl, StyledString label) {
        if (tpl!=null &&
                !tpl.getTypeParameterDeclarations().isEmpty()) {
            label.append("<");
            int len = tpl.getTypeParameterDeclarations().size(), i=0;
            for (Tree.TypeParameterDeclaration p: tpl.getTypeParameterDeclarations()) {
                label.append(name(p.getIdentifier()), TYPE_STYLER);
                if (++i<len) label.append(", ");
            }
            label.append(">");
        }
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
    
    private static String getLabel(Package packageModel) {
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
