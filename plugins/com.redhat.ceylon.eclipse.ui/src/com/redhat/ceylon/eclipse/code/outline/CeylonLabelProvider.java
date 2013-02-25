package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.ANNOTATIONS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.IDENTIFIERS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.KEYWORDS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.STRINGS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.TYPES;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.color;
import static org.eclipse.jface.viewers.StyledString.COUNTER_STYLER;
import static org.eclipse.jface.viewers.StyledString.QUALIFIER_STYLER;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
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
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeAlias;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportPath;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PackageDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Util;
import com.redhat.ceylon.eclipse.code.search.CeylonElement;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

/**
 * Styled Label Provider which can be used to provide labels for Ceylon elements.
 * 
 * Extends StyledCellLabelProvider to provide custom styling by doing its own painting 
 * - here the {@link #update(ViewerCell)} method is the entry point
 * Implements DelegatingStyledCellLabelProvider.IStyledLabelProvider too, but this 
 * probably is not required.
 * 
 * @author max
 *
 */
public class CeylonLabelProvider extends StyledCellLabelProvider 
        implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, 
                   ILabelProvider, CeylonResources {
    
    private static CeylonLabelDecorator DECORATOR = new CeylonLabelDecorator();
    
    private Set<ILabelProviderListener> fListeners = new HashSet<ILabelProviderListener>();
    
    public static ImageRegistry imageRegistry = CeylonPlugin.getInstance()
            .getImageRegistry();
    
    public static Image FILE_IMAGE = imageRegistry.get(CEYLON_FILE);
    private static Image FILE_WITH_WARNING_IMAGE = imageRegistry.get(CEYLON_FILE_WARNING);
    private static Image FILE_WITH_ERROR_IMAGE = imageRegistry.get(CEYLON_FILE_ERROR);
    
    public static Image ALIAS = imageRegistry.get(CEYLON_ALIAS);
    public static Image CLASS = imageRegistry.get(CEYLON_CLASS);
    public static Image INTERFACE = imageRegistry.get(CEYLON_INTERFACE);
    public static Image LOCAL_CLASS = imageRegistry.get(CEYLON_LOCAL_CLASS);
    private static Image LOCAL_INTERFACE = imageRegistry.get(CEYLON_LOCAL_INTERFACE);
    public static Image METHOD = imageRegistry.get(CEYLON_METHOD);
    public static Image ATTRIBUTE = imageRegistry.get(CEYLON_ATTRIBUTE);
    public static Image LOCAL_METHOD = imageRegistry.get(CEYLON_LOCAL_METHOD);
    public static Image LOCAL_ATTRIBUTE = imageRegistry.get(CEYLON_LOCAL_ATTRIBUTE);
    public static Image PARAMETER = imageRegistry.get(CEYLON_PARAMETER);
    public static Image PACKAGE = imageRegistry.get(CEYLON_PACKAGE);
    public static Image ARCHIVE = imageRegistry.get(CEYLON_ARCHIVE);
    public static Image IMPORT = imageRegistry.get(CEYLON_IMPORT);
    private static Image IMPORT_LIST = imageRegistry.get(CEYLON_IMPORT_LIST);
    public static Image PROJECT = imageRegistry.get(CEYLON_PROJECT);
    public static Image CORRECTION = imageRegistry.get(CEYLON_CORRECTION);
    public static Image CHANGE = imageRegistry.get(CEYLON_CHANGE);
    public static Image COMPOSITE_CHANGE = imageRegistry.get(CEYLON_COMPOSITE_CHANGE);
    public static Image RENAME = imageRegistry.get(CEYLON_RENAME);
    public static Image MOVE = imageRegistry.get(CEYLON_MOVE);
    public static Image ADD = imageRegistry.get(CEYLON_ADD);
    
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
    
    public static final Styler VERSION_STYLER = new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground=color(colorRegistry, STRINGS);
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
        if (element instanceof Module) {
            return ARCHIVE;
        }
        if (element instanceof Unit) {
            return FILE_IMAGE;
        }
        if (element instanceof CeylonOutlineNode) {
            return getImageFor((CeylonOutlineNode) element);
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
    
    private static Image getImageFor(CeylonOutlineNode n) {
        return getImageFor((Node) n.getTreeNode());
    }
    
    private static Image getImageFor(Node n) {
        if (n instanceof PackageNode) {
            return PACKAGE;
        }
        else if (n instanceof PackageDescriptor) {
            return PACKAGE;
        }
        else if (n instanceof ModuleDescriptor) {
            return ARCHIVE;
        }
        else if (n instanceof Tree.CompilationUnit) {
            return FILE_IMAGE;
        }
        else if (n instanceof Tree.ImportList) {
            return IMPORT_LIST;
        }
        else if (n instanceof Tree.Import || 
        		n instanceof Tree.ImportModule) {
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
        else if (n instanceof Tree.TypeAliasDeclaration) {
            return ALIAS;
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
        else if (d instanceof TypeAlias) {
            return ALIAS;
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
        if (element instanceof CeylonOutlineNode) {
            return getStyledLabelFor((Node) ((CeylonOutlineNode) element).getTreeNode());
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
            IFile file = ce.getFile();
			String path = file==null ? 
					ce.getVirtualFile().getPath() : 
					file.getFullPath().toString();
			return getStyledLabelFor(ce.getNode())
                    .append(pkg, QUALIFIER_STYLER)
                    .append(" - " + path, COUNTER_STYLER)
                    .append(":" + ce.getLocation(), COUNTER_STYLER);
        }
        else if (element instanceof Package) {
            return new StyledString(getLabel((Package) element), QUALIFIER_STYLER);
        }
        else if (element instanceof Module) {
        	return new StyledString(getLabel((Module) element));
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
        if (n instanceof Tree.TypeAliasDeclaration) {
            Tree.TypeAliasDeclaration ac = (Tree.TypeAliasDeclaration) n;
            StyledString label = new StyledString("alias ", KW_STYLER);
            label.append(name(ac.getIdentifier()), TYPE_ID_STYLER);
            parameters(ac.getTypeParameterList(), label);
            return label;
        }
        else if (n instanceof Tree.ObjectDefinition) {
            Tree.ObjectDefinition ai = (Tree.ObjectDefinition) n;
            return new StyledString("object ", KW_STYLER)
                    .append(name(ai.getIdentifier()), ID_STYLER);
        }
        else if (n instanceof Tree.AttributeSetterDefinition) {
            Tree.AttributeSetterDefinition ai = (Tree.AttributeSetterDefinition) n;
            return new StyledString("assign ", KW_STYLER)
            .append(name(ai.getIdentifier()), ID_STYLER);
        }
        else if (n instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration td = (Tree.TypedDeclaration) n;
            String type;
            Styler styler;
            if (td.getType() instanceof Tree.VoidModifier) {
                type = "void";
                styler = KW_STYLER;
            }
            else {
                type = type(td.getType());
                styler = TYPE_STYLER;
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
        else if (n instanceof Tree.ModuleDescriptor) {
            Tree.ModuleDescriptor i = (Tree.ModuleDescriptor) n;
            ImportPath p = i.getImportPath();
			if (isNonempty(p)) {
                return new StyledString(toPath(p), QUALIFIER_STYLER);
            }
        }
        else if (n instanceof Tree.PackageDescriptor) {
            Tree.PackageDescriptor i = (Tree.PackageDescriptor) n;
            ImportPath p = i.getImportPath();
			if (isNonempty(p)) {
                return new StyledString(toPath(p), QUALIFIER_STYLER);
            }
        }
        else if (n instanceof Tree.ImportList) {
            return new StyledString("imports");
        }
        else if (n instanceof Tree.Import) {
            Tree.Import i = (Tree.Import) n;
            ImportPath p = i.getImportPath();
			if (isNonempty(p)) {
                return new StyledString(toPath(p), QUALIFIER_STYLER);
            }
        }
        else if (n instanceof Tree.ImportModule) {
            Tree.ImportModule i = (Tree.ImportModule) n;
            Tree.ImportPath p = i.getImportPath();
			if (isNonempty(p)) {
                return new StyledString(toPath(p), QUALIFIER_STYLER);
            }
			Tree.QuotedLiteral ql = i.getQuotedLiteral();
			if (ql!=null) {
				return new StyledString(ql.getText().replace("'", ""), 
						QUALIFIER_STYLER);
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

	private static boolean isNonempty(Tree.ImportPath p) {
		return p!=null && !p.getIdentifiers().isEmpty();
	}

    private static String toPath(Tree.ImportPath p) {
        return formatPath(p.getIdentifiers());
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
        	if (tm==null) {
        		return "<Unknown>";
        	}
        	else {
        		boolean sequenced = type instanceof Tree.SequencedType;
        		if (sequenced) {
        			tm = type.getUnit().getIteratedType(tm);
                	if (tm==null) {
                		return "<Unknown>";
                	}
        		}
        		String tn = tm.getProducedTypeName();
        		if (sequenced) {
        			tn+="*";
        		}
				return tn;
        	}
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
    
    public static String getLabel(Package packageModel) {
        String name = packageModel.getQualifiedNameString();
        if (name.isEmpty()) name="default package";
        return name;
    }
    
    public static String getLabel(Module moduleModel) {
        String name = moduleModel.getNameAsString();
        if (name.isEmpty()) name="default module";
        return name;
    }
    
    public static String getPackageLabel(Node decl) {
        return decl.getUnit()==null ? "unknown package" : 
            getLabel(decl.getUnit().getPackage());
    }
    
    public static String getModuleLabel(Node decl) {
        return decl.getUnit()==null ? "unknown module" : 
            getLabel(decl.getUnit().getPackage().getModule());
    }
    
    public static String getModuleLabel(Declaration decl) {
        return decl.getUnit()==null ? "unknown module" : 
            getLabel(decl.getUnit().getPackage().getModule());
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
    
    /**
     * Returns the maximum problem marker severity for the given resource, and, if
     * depth is IResource.DEPTH_INFINITE, its children. The return value will be
     * one of IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO
     * or 0, indicating that no problem markers exist on the given resource.
     * @param depth TODO
     */
    public static int getMaxProblemMarkerSeverity(IResource res, int depth) {
        if (res == null || !res.isAccessible())
            return 0;
    
        boolean hasWarnings= false; // if resource has errors, will return error image immediately
        IMarker[] markers= null;
    
        try {
            markers= res.findMarkers(IMarker.PROBLEM, true, depth);
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
        if (markers == null)
            return 0; // don't know - say no errors/warnings/infos
    
        for(int i= 0; i < markers.length; i++) {
            IMarker m= markers[i];
            int priority= m.getAttribute(IMarker.SEVERITY, -1);
    
            if (priority == IMarker.SEVERITY_WARNING) {
        	hasWarnings= true;
            } else if (priority == IMarker.SEVERITY_ERROR) {
        	return IMarker.SEVERITY_ERROR;
            }
        }
        return hasWarnings ? IMarker.SEVERITY_WARNING : 0;
    }

}
