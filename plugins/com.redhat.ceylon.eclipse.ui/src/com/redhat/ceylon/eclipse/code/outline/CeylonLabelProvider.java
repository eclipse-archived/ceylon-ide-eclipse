package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.compiler.typechecker.tree.Util.hasAnnotation;
import static com.redhat.ceylon.eclipse.code.editor.AdditionalAnnotationCreator.getRefinedDeclaration;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.DISPLAY_RETURN_TYPES;
import static com.redhat.ceylon.eclipse.util.Highlights.ARROW_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.ID_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.KW_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.PACKAGE_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.TYPE_ID_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.TYPE_STYLER;
import static org.eclipse.core.resources.IMarker.SEVERITY_ERROR;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;
import static org.eclipse.jface.viewers.IDecoration.BOTTOM_LEFT;
import static org.eclipse.jface.viewers.IDecoration.BOTTOM_RIGHT;
import static org.eclipse.jface.viewers.IDecoration.TOP_LEFT;
import static org.eclipse.jface.viewers.IDecoration.TOP_RIGHT;
import static org.eclipse.jface.viewers.StyledString.QUALIFIER_STYLER;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Constructor;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeAlias;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PackageDescriptor;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.ErrorCollectionVisitor;

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
    
    private Set<ILabelProviderListener> fListeners = new HashSet<ILabelProviderListener>();
    
    public static final Point BIG_SIZE = new Point(22,16);
    public static final Point SMALL_SIZE = new Point(16,16);
    
    private final boolean smallSize;

    public final static int WARNING = 1 << 2;
    public final static int ERROR = 1 << 3;
    private final static int REFINES = 1 << 4;
    private final static int IMPLEMENTS = 1 << 5;
    private final static int FORMAL = 1 << 6;
    private final static int ABSTRACT = 1 << 7;
    private final static int VARIABLE = 1 << 8;
    private final static int ANNOTATION = 1 << 9;
    private final static int ENUM = 1 << 10;
    private final static int ALIAS = 1 << 11;
    private final static int DEPRECATED = 1 << 12;
    private final static int CEYLON_NATURE = 1 << 12;
    //private final static int FINAL = 1 << 13;

    static final DecorationDescriptor[] DECORATIONS = new DecorationDescriptor[] {
        new DecorationDescriptor(WARNING, WARNING_IMAGE, BOTTOM_LEFT),
        new DecorationDescriptor(ERROR, ERROR_IMAGE, BOTTOM_LEFT),
        new DecorationDescriptor(REFINES, REFINES_IMAGE, BOTTOM_RIGHT),
        new DecorationDescriptor(IMPLEMENTS, IMPLEMENTS_IMAGE, BOTTOM_RIGHT),
        new DecorationDescriptor(FORMAL, FINAL_IMAGE, TOP_RIGHT),
        new DecorationDescriptor(ABSTRACT, ABSTRACT_IMAGE, TOP_RIGHT),
        new DecorationDescriptor(VARIABLE, VARIABLE_IMAGE, TOP_LEFT),
        new DecorationDescriptor(ANNOTATION, ANNOTATION_IMAGE, TOP_LEFT),
        new DecorationDescriptor(ENUM, ENUM_IMAGE, TOP_LEFT),
        new DecorationDescriptor(ALIAS, ALIAS_IMAGE, TOP_LEFT),
        new DecorationDescriptor(DEPRECATED, DEPRECATED_IMAGE, IDecoration.UNDERLAY),
        //new DecorationDescriptor(FINAL, CeylonPlugin.getInstance().image("..."), TOP_RIGHT)
    };

    public CeylonLabelProvider() {
        this(false);
    }

    public CeylonLabelProvider(boolean smallSize) {
        this.smallSize = smallSize;
    }
    
    protected static Image getDecoratedImage(Object element, 
            String key, boolean smallSize) {
        if (key==null) return null;
        return getDecoratedImage(key, 
                getDecorationAttributes(element), 
                smallSize);
    }

    public static Image getDecoratedImage(String key, 
            int decorationAttributes, boolean smallSize) {
        ImageDescriptor descriptor = imageRegistry.getDescriptor(key);
        if (descriptor==null) {
            return null;
        }
        String decoratedKey = key+'#'+decorationAttributes + 
                (smallSize ? "#small" : "");
        Image image = imageRegistry.get(decoratedKey);
        if (image==null) {
            imageRegistry.put(decoratedKey, 
                    new DecoratedImageDescriptor(descriptor, 
                            decorationAttributes, 
                            smallSize ? SMALL_SIZE : BIG_SIZE));
            image = imageRegistry.get(decoratedKey);
        }
        return image;
    }

    @Override
    public Image getImage(Object element) {
        return getDecoratedImage(element, getImageKey(element), smallSize);
    }
    
    protected String getImageKey(Object element) {
        if (element instanceof IFile) {
            return getImageKeyForFile((IFile) element);
        }
        else if (element instanceof IPath) {
            String name = ((IPath) element).lastSegment();
            if (name.equals("module.ceylon")) {
                return CEYLON_MODULE_DESC;
            }
            else if (name.equals("package.ceylon")) {
                return CEYLON_PACKAGE_DESC;
            }
            return CEYLON_FILE;
        }
        if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            if (folder.getAdapter(IPackageFragmentRoot.class)!=null) {
                return CEYLON_SOURCE_FOLDER;
            }
            else if (folder.getAdapter(IPackageFragment.class)!=null) {
                return CEYLON_PACKAGE;
            }
            return CEYLON_FOLDER;
        }
        else if (element instanceof IPackageFragmentRoot) {
            if (((IPackageFragmentRoot) element).getPath().getFileExtension()!=null) {
                return CEYLON_MODULE;
            }
            else {
                return CEYLON_SOURCE_FOLDER;
            }
        }
        if (element instanceof IProject ||
            element instanceof IJavaProject) {
            return CEYLON_PROJECT;
        }
        if (element instanceof IPackageFragment) {
            return isModule((IPackageFragment) element) ? 
                    CEYLON_MODULE : CEYLON_PACKAGE;
        }
        if (element instanceof Package ||
            element instanceof IPackageFragment) {
            return CEYLON_PACKAGE;
        }
        if (element instanceof IImportDeclaration) {
            return CEYLON_IMPORT;
        }
        if (element instanceof Module) {
            return CEYLON_MODULE;
        }
        if (element instanceof Unit) {
            return CEYLON_FILE;
        }
        if (element instanceof CeylonOutlineNode) {
            return ((CeylonOutlineNode) element).getImageKey();
        }
        if (element instanceof Node) {
            return getImageKeyForNode((Node) element);
        }
        return null;
    }

    public boolean isModule(IPackageFragment element) {
        IFolder folder = (IFolder) element.getResource();
        if (folder!=null &&
                folder.getFile("module.ceylon").exists()) {
            return true;
        }
        else {
            return false;
        }
    }

    public static String getImageKeyForFile(IFile element) {
        String name = element.getName();
        if (name.equals("module.ceylon")) {
            return CEYLON_MODULE_DESC;
        }
        else if (name.equals("package.ceylon")) {
            return CEYLON_PACKAGE_DESC;
        }
        else if (element.getFileExtension().equalsIgnoreCase("ceylon")) {
            return CEYLON_FILE;
        }
        else if (element.getFileExtension().equalsIgnoreCase("java")) {
            return JAVA_FILE;
        }
        else {
            return GENERIC_FILE;
        }
    }
    
    public static String getImageKeyForNode(Node n) {
        if (n instanceof PackageNode) {
            return CEYLON_PACKAGE;
        }
        else if (n instanceof PackageDescriptor) {
            return CEYLON_PACKAGE;
        }
        else if (n instanceof ModuleDescriptor) {
            return CEYLON_MODULE;
        }
        else if (n instanceof Tree.CompilationUnit) {
            return CEYLON_FILE;
        }
        else if (n instanceof Tree.ImportList) {
            return CEYLON_IMPORT_LIST;
        }
        else if (n instanceof Tree.Import || 
                n instanceof Tree.ImportModule) {
            return CEYLON_IMPORT;
        }
        else if (n instanceof Tree.Declaration) {
            return getImageKeyForDeclarationNode((Tree.Declaration) n);
        }
        else if (n instanceof Tree.SpecifierStatement) {
            Tree.Term bme = 
                    ((Tree.SpecifierStatement) n).getBaseMemberExpression();
            if (bme instanceof Tree.BaseMemberExpression) { 
                return CEYLON_LOCAL_ATTRIBUTE;
            }
            else if (bme instanceof Tree.ParameterizedExpression) {
                return CEYLON_LOCAL_METHOD;
            }
            else {
                throw new RuntimeException("unexpected node type");
            }
        }
        else {
            return null;
        }
    }
    
    private static String getImageKeyForDeclarationNode(Tree.Declaration n) {
        boolean shared = hasAnnotation(n.getAnnotationList(), 
                "shared", n.getUnit());
        if (n instanceof Tree.AnyClass) {
            if (shared) {
                return CEYLON_CLASS;
            }
            else {
                return CEYLON_LOCAL_CLASS;
            }
        }
        else if (n instanceof Tree.AnyInterface) {
            if (shared) {
                return CEYLON_INTERFACE;
            }
            else { 
                return CEYLON_LOCAL_INTERFACE;
            }
        }
        else if (n instanceof Tree.Constructor) {
            if (shared) {
                return CEYLON_CONSTRUCTOR;
            }
            else {
                return CEYLON_CONSTRUCTOR; //TODO!!
            }
        }
        else if (n instanceof Tree.AnyMethod) {
            if (shared) {
                return CEYLON_METHOD;
            }
            else {
                return CEYLON_LOCAL_METHOD;
            }
        }
        else if (n instanceof Tree.TypeAliasDeclaration) {
            return CEYLON_ALIAS;
        }
        else {
            if (shared) {
                return CEYLON_ATTRIBUTE;
            }
            else {
                return CEYLON_LOCAL_ATTRIBUTE;
            }
        }
    }
    
    public static Image getImageForDeclaration(Declaration element) {
        return getDecoratedImage(element, 
                getImageKeyForDeclaration(element), false);
    }
    
    public static Image getImageForFile(IFile file) {
        return getDecoratedImage(file, getImageKeyForFile(file), false);
    }
    
    private static String getImageKeyForDeclaration(Declaration d) {
        if (d==null) return null;
        boolean shared = d.isShared();
        if (d instanceof Class) {
            if (shared) {
                return CEYLON_CLASS;
            }
            else {
                return CEYLON_LOCAL_CLASS;
            }
        }
        else if (d instanceof Interface) {
            if (shared) {
                return CEYLON_INTERFACE;
            }
            else { 
                return CEYLON_LOCAL_INTERFACE;
            }
        }
        if (d instanceof Constructor) {
            if (shared) {
                return CEYLON_CONSTRUCTOR;
            }
            else {
                return CEYLON_CONSTRUCTOR; //TODO!
            }
        }
        else if (d instanceof TypeParameter) {
            return CEYLON_TYPE_PARAMETER;
        }
        else if (d.isParameter()) {
            if (d instanceof Method) {
                return CEYLON_PARAMETER_METHOD;
            }
            else {
                return CEYLON_PARAMETER;
            }
        }
        else if (d instanceof Method) {
            if (shared) {
                return CEYLON_METHOD;
            }
            else {
                return CEYLON_LOCAL_METHOD;
            }
        }
        else if (d instanceof TypeAlias ||
                d instanceof NothingType) {
            return CEYLON_ALIAS;
        }
        else {
            if (shared) {
                return CEYLON_ATTRIBUTE;
            }
            else {
                return CEYLON_LOCAL_ATTRIBUTE;
            }
        }
    }
    
    @Override
    public StyledString getStyledText(Object element) {
        if (element instanceof CeylonOutlineNode) {
            return ((CeylonOutlineNode) element).getLabel();
            //TODO: add the arrow if the node is dirty vs git!
            //return new StyledString("> ", ARROW_STYLER).append(label);
        }
        else if (element instanceof IFile) {
            return new StyledString(((IFile) element).getName());
        }
        else if (element instanceof IPath) {
            return new StyledString(((IPath) element).lastSegment());
        }
        else if (element instanceof IFolder) {
            return new StyledString(((IFolder) element).getName());
        }
        else if (element instanceof IProject) {
            return new StyledString(((IProject) element).getName());
        }
        else if (element instanceof IJavaProject) {
            return new StyledString(((IJavaProject) element).getElementName());
        }
        else if (element instanceof IPackageFragment) {
            String packageName = ((IPackageFragment) element).getElementName();
            if (packageName.isEmpty()) packageName = "(default package)";
            return new StyledString(packageName, PACKAGE_STYLER);
        }
        else if (element instanceof IPackageFragmentRoot) {
            boolean isCar = ((IPackageFragmentRoot) element).getPath().getFileExtension()!=null;
            String name = ((IJavaElement) element).getElementName();
            int loc = name.lastIndexOf('.');
            if (loc>=0) name = name.substring(0, loc);
            loc = name.indexOf('-');
            if (loc>=0) name = name.substring(0, loc);
            if (isCar) {
                return new StyledString(name, PACKAGE_STYLER);
            }
            else {
                return new StyledString(name);
            }
        }
        else if (element instanceof IImportDeclaration) {
            return new StyledString("import ", KW_STYLER)
                .append(((IImportDeclaration) element).getElementName(), PACKAGE_STYLER);
        }
        else if (element instanceof Package) {
            return new StyledString(getLabel((Package) element), 
            		PACKAGE_STYLER);
        }
        else if (element instanceof Module) {
            return new StyledString(getLabel((Module) element), 
            		PACKAGE_STYLER);
        }
        else if (element instanceof Unit) {
            return new StyledString(((Unit) element).getFilename());
        }
        else if (element instanceof Node) {
            return getStyledLabelForNode((Node) element);
        }
        else {
            return new StyledString("");
        }
    }

    @Override
    public String getText(Object element) {
        return getStyledText(element).toString();
    }
    
    private static void appendPostfixType(Tree.TypedDeclaration td,
            StyledString label) {
        if (EditorUtil.getPreferences().getBoolean(DISPLAY_RETURN_TYPES)) {
            Tree.Type type = td.getType();
            if (type!=null && 
                    !(type instanceof Tree.DynamicModifier) &&
                    !(type instanceof Tree.VoidModifier)) {
                ProducedType tm = type.getTypeModel();
                if (!isTypeUnknown(tm)) {
                    label.append(" ∊ ");
                    appendTypeName(label, tm, ARROW_STYLER);
                }
            }
        }
    }

    public static StyledString getStyledLabelForNode(Node node) {
        //TODO: it would be much better to render types
        //      from the tree nodes instead of from the
        //      model nodes
        
        if (node instanceof Tree.TypeParameterDeclaration) {
            Tree.TypeParameterDeclaration ac = (Tree.TypeParameterDeclaration) node;
            return new StyledString(name(ac.getIdentifier()));
        }
        if (node instanceof Tree.AnyClass) {
            Tree.AnyClass ac = (Tree.AnyClass) node;
            StyledString label = new StyledString("class ", KW_STYLER);
            label.append(name(ac.getIdentifier()), TYPE_ID_STYLER);
            parameters(ac.getTypeParameterList(), label);
            parameters(ac.getParameterList(), label);
            return label;
        }
        else if (node instanceof Tree.AnyInterface) {
            Tree.AnyInterface ai = (Tree.AnyInterface) node;
            StyledString label = new StyledString("interface ", KW_STYLER);
            label.append(name(ai.getIdentifier()), TYPE_ID_STYLER);
            parameters(ai.getTypeParameterList(), label);
            return label;
        }
        if (node instanceof Tree.Constructor) {
            Tree.Constructor ac = (Tree.Constructor) node;
            StyledString label = new StyledString("new ", KW_STYLER);
            label.append(name(ac.getIdentifier()), TYPE_ID_STYLER);
            parameters(ac.getParameterList(), label);
            return label;
        }
        if (node instanceof Tree.TypeAliasDeclaration) {
            Tree.TypeAliasDeclaration ac = (Tree.TypeAliasDeclaration) node;
            StyledString label = new StyledString("alias ", KW_STYLER);
            label.append(name(ac.getIdentifier()), TYPE_ID_STYLER);
            parameters(ac.getTypeParameterList(), label);
            return label;
        }
        else if (node instanceof Tree.ObjectDefinition) {
            Tree.ObjectDefinition ai = (Tree.ObjectDefinition) node;
            return new StyledString("object ", KW_STYLER)
                    .append(name(ai.getIdentifier()), ID_STYLER);
        }
        else if (node instanceof Tree.AttributeSetterDefinition) {
            Tree.AttributeSetterDefinition ai = (Tree.AttributeSetterDefinition) node;
            return new StyledString("assign ", KW_STYLER)
                    .append(name(ai.getIdentifier()), ID_STYLER);
        }
        else if (node instanceof Tree.AnyMethod) {
            Tree.TypedDeclaration td = (Tree.TypedDeclaration) node;
            String kind;
            if (td.getType() instanceof Tree.DynamicModifier) {
                kind = "dynamic";
            }
            else if (td.getType() instanceof Tree.VoidModifier) {
                kind = "void";
            }
            else {
                kind = "function";
            }
            StyledString label = new StyledString(kind, KW_STYLER)
                    .append(" ")
                    .append(name(td.getIdentifier()), ID_STYLER);
            Tree.AnyMethod am = (Tree.AnyMethod) node;
            parameters(am.getTypeParameterList(), label);
            for (Tree.ParameterList pl: am.getParameterLists()) { 
                parameters(pl, label);
            }
            appendPostfixType(td, label);
            return label;
        }
        else if (node instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration td = (Tree.TypedDeclaration) node;
            String kind;
            if (td.getType() instanceof Tree.DynamicModifier) {
                kind = "dynamic";
            }
            else {
                kind = "value";
            }
            StyledString label = new StyledString(kind, KW_STYLER)
                    .append(" ")
                    .append(name(td.getIdentifier()), ID_STYLER);
            appendPostfixType(td, label);
			return label;
        }
//        else if (n instanceof Tree.TypedDeclaration) {
//            Tree.TypedDeclaration td = (Tree.TypedDeclaration) n;
//            Tree.Type tt = td.getType();
//            StyledString label = new StyledString();
//            label.append(type(tt, td))
//                .append(" ")
//                .append(name(td.getIdentifier()), ID_STYLER);
//            if (n instanceof Tree.AnyMethod) {
//                Tree.AnyMethod am = (Tree.AnyMethod) n;
//                parameters(am.getTypeParameterList(), label);
//                for (Tree.ParameterList pl: am.getParameterLists()) { 
//                    parameters(pl, label);
//                }
//            }
//            return label;
//        }
        else if (node instanceof Tree.CompilationUnit) {
            Tree.CompilationUnit ai = (Tree.CompilationUnit) node;
            if (ai.getUnit()==null) {
                return new StyledString("unknown");
            }
            return new StyledString(ai.getUnit().getFilename());
        }
        else if (node instanceof Tree.ModuleDescriptor) {
            Tree.ModuleDescriptor i = (Tree.ModuleDescriptor) node;
            Tree.ImportPath p = i.getImportPath();
            if (isNonempty(p)) {
                return new StyledString("module ", KW_STYLER)
                        .append(toPath(p), PACKAGE_STYLER);
            }
        }
        else if (node instanceof Tree.PackageDescriptor) {
            Tree.PackageDescriptor i = (Tree.PackageDescriptor) node;
            Tree.ImportPath p = i.getImportPath();
            if (isNonempty(p)) {
                return new StyledString("package ", KW_STYLER)
                        .append(toPath(p), PACKAGE_STYLER);
            }
        }
        else if (node instanceof Tree.ImportList) {
            return new StyledString("imports");
        }
        else if (node instanceof Tree.ImportPath) {
            Tree.ImportPath p = (Tree.ImportPath) node;
            if (isNonempty(p)) {
                return new StyledString(toPath(p), PACKAGE_STYLER);
            }
        }
        else if (node instanceof Tree.Import) {
            Tree.Import i = (Tree.Import) node;
            Tree.ImportPath p = i.getImportPath();
            if (isNonempty(p)) {
                return new StyledString("import ", KW_STYLER)
                        .append(toPath(p), PACKAGE_STYLER);
            }
        }
        else if (node instanceof Tree.ImportModule) {
            Tree.ImportModule i = (Tree.ImportModule) node;
            Tree.ImportPath p = i.getImportPath();
            if (isNonempty(p)) {
                return new StyledString("import ", KW_STYLER)
                        .append(toPath(p), PACKAGE_STYLER);
            }
            Tree.QuotedLiteral ql = i.getQuotedLiteral();
            if (ql!=null) {
                return new StyledString(ql.getText().replace("'", ""), 
                        QUALIFIER_STYLER);
            }
        }
        else if (node instanceof PackageNode) {
            PackageNode pn = (PackageNode) node;
            String name = pn.getPackageName();
            if (name.isEmpty()) name = "(default package)";
            return new StyledString(name); //PACKAGE_STYLER??
        }
        else if (node instanceof Tree.SpecifierStatement) {
            Tree.Term bme = ((Tree.SpecifierStatement) node).getBaseMemberExpression();
            Tree.Identifier id;
            String kw;
            List<Tree.ParameterList> pls;
            if (bme instanceof Tree.BaseMemberExpression) {
                id = ((Tree.BaseMemberExpression) bme).getIdentifier();
                pls = null;
                kw = "value";
            }
            else if (bme instanceof Tree.ParameterizedExpression) {
                Tree.Primary primary = ((Tree.ParameterizedExpression) bme).getPrimary();
                id = ((Tree.BaseMemberExpression) primary).getIdentifier();
                kw = "function";
                pls = ((Tree.ParameterizedExpression) bme).getParameterLists();
            }
            else {
                 throw new RuntimeException("unexpected node type");
            }
            StyledString label = new StyledString();
            label.append(kw, KW_STYLER)
                .append(" ")
                .append(name(id), ID_STYLER);
            if (pls!=null) {
                for (Tree.ParameterList pl: pls) { 
                    parameters(pl, label);
                }
            }
            return label;
        }
        
        return new StyledString("");
    }

    private static boolean isNonempty(Tree.ImportPath p) {
        return p!=null && !p.getIdentifiers().isEmpty();
    }

    private static String toPath(Tree.ImportPath p) {
        return formatPath(p.getIdentifiers());
    }
    
    private static StyledString type(Tree.Type type, Tree.TypedDeclaration node) {
        StyledString result = new StyledString();
        if (type!=null) {
            if (type instanceof Tree.VoidModifier) {
                return result.append("void", KW_STYLER);
            }
            if (type instanceof Tree.DynamicModifier) {
                return result.append("dynamic", KW_STYLER);
            }
            ProducedType tm = type.getTypeModel();
            if (tm!=null && !isTypeUnknown(tm)) {
                if (type instanceof Tree.SequencedType) {
                    Tree.SequencedType st = (Tree.SequencedType) type;
                    ProducedType itm = type.getUnit().getIteratedType(tm);
                    if (itm!=null) {
                        appendTypeName(result, itm);
                        result.append(st.getAtLeastOne()?"+":"*");
                        return result;
                    }
                }
                appendTypeName(result, tm);
                return result;
            }
        }
        return result.append(node instanceof Tree.AnyMethod ? "function" : "value", 
                KW_STYLER);
    }
    
    private static String name(Tree.Identifier id) {
        if (id==null || id.getText().startsWith("<missing")) {
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
                    if (p instanceof Tree.ParameterDeclaration) {
                        Tree.TypedDeclaration td = 
                                ((Tree.ParameterDeclaration) p).getTypedDeclaration();
                        label.append(type(td.getType(), td))
                            .append(" ")
                            .append(name(td.getIdentifier()), ID_STYLER);
                        if (p instanceof Tree.FunctionalParameterDeclaration) {
                            for (Tree.ParameterList ipl: 
                                ((Tree.MethodDeclaration) td).getParameterLists()) {
                                parameters(ipl, label);
                            }
                        }
                    }
                    else if (p instanceof Tree.InitializerParameter) {
                        Tree.Identifier id = ((Tree.InitializerParameter) p).getIdentifier();
                        label.append(name(id), ID_STYLER);
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
                if (p.getTypeVariance()!=null) {
                    label.append(p.getTypeVariance().getText(), KW_STYLER).append(" "); 
                }
                label.append(name(p.getIdentifier()), TYPE_STYLER);
                if (++i<len) label.append(", ");
            }
            label.append(">");
        }
    }
    
    @Override
    public void addListener(ILabelProviderListener listener) {
        fListeners.add(listener);
    }
    
    @Override
    public void dispose() {}
    
    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
    
    @Override
    public void removeListener(ILabelProviderListener listener) {
        fListeners.remove(listener);
    }
    
    public static String getLabel(Package packageModel) {
        String name = packageModel.getQualifiedNameString();
        if (name.isEmpty()) {
            return "(default package)";
        }
        return name;
    }
    
    public static String getLabel(Module moduleModel) {
        String name = moduleModel.getNameAsString();
        if (name.isEmpty() || 
                name.equals(Module.DEFAULT_MODULE_NAME)) {
            return "(default module)";
        }
        return name;
    }
    
    public static String getPackageLabel(Node decl) {
        return decl.getUnit()==null ? "(unknown package)" : 
            getLabel(decl.getUnit().getPackage());
    }
    
    public static String getModuleLabel(Node decl) {
        return decl.getUnit()==null ? "(unknown module)" : 
            getLabel(decl.getUnit().getPackage().getModule());
    }
    
    public static String getModuleLabel(Declaration decl) {
        return decl.getUnit()==null ? "(unknown module)" : 
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

    public static void appendTypeName(StyledString result, ProducedType type) {
        appendTypeName(result, type, TYPE_STYLER);
    }
    
    public static void appendTypeName(StyledString result, ProducedType type, 
            Styler styler) {
        try {
            String typeName = type.getProducedTypeName();
            StringTokenizer tokens = 
                    new StringTokenizer(typeName,"|&?[]{}*+=-<>(), ",true);
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (Character.isLetter(token.charAt(0))) {
                    result.append(token, styler);
                }
                else {
                    result.append(token);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getDecorationAttributes(Object entity) {
        try {
            if (entity instanceof IProject) {
                int decorationAttributes = CEYLON_NATURE;
                switch (getMaxProblemMarkerSeverity((IProject) entity, 
                        IResource.DEPTH_INFINITE)) {
                        case IMarker.SEVERITY_ERROR:
                            decorationAttributes &= ERROR;
                            break;
                        case IMarker.SEVERITY_WARNING:
                            decorationAttributes &= WARNING;
                            break;
                }
                return decorationAttributes;
            }
            if (entity instanceof IPackageFragment) {
                IFolder folder = null;
                try {
                    folder = (IFolder) ((IPackageFragment) entity).getCorrespondingResource();
                } catch (JavaModelException e) {
                }
                if (folder != null) {
                    final JDTModule moduleOfRootPackage = CeylonBuilder.getModule(folder);
                    int sev = getMaxProblemMarkerSeverity(folder, 
                            IResource.DEPTH_INFINITE,
                            new IMarkerFilter() {
                        @Override
                        public boolean select(IMarker marker) {
                            if (marker.getResource() instanceof IFile) {
                                Package currentPackage = CeylonBuilder.getPackage((IFile) marker.getResource());
                                if (moduleOfRootPackage != null && currentPackage != null) {
                                    return moduleOfRootPackage.equals(currentPackage.getModule());
                                }
                            }
                            return false;
                        }
                    });
                    switch (sev) {
                    case SEVERITY_ERROR:
                        return ERROR;
                    case SEVERITY_WARNING:
                        return WARNING;
                    default: 
                        return 0;
                    }
                }
            }
            if (entity instanceof IResource) {
                int sev = getMaxProblemMarkerSeverity((IResource) entity, IResource.DEPTH_ONE);
                switch (sev) {
                case SEVERITY_ERROR:
                    return ERROR;
                case SEVERITY_WARNING:
                    return WARNING;
                default: 
                    return 0;
                }
            }
            if (entity instanceof CeylonOutlineNode) {
                return ((CeylonOutlineNode) entity).getDecorations();
            }
            if (entity instanceof Declaration) {
                return getDecorationAttributes((Declaration) entity);
            }
            if (entity instanceof Node) {
                return getNodeDecorationAttributes((Node) entity);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getNodeDecorationAttributes(Node node) {
        int result = 0;
        if (node instanceof Tree.Declaration || node instanceof Tree.Import) {
            ErrorCollectionVisitor ev = new ErrorCollectionVisitor(node, true);
            node.visit(ev);
            boolean warnings=false;
            boolean errors=false;
            for (Message m: ev.getErrors()) {
                if (m instanceof UsageWarning) {
                    warnings = true;
                }
                else {
                    errors = true;
                }
            }
            if (errors) {
                result |= ERROR;
            }
            else if (warnings) {
                result |= WARNING;
            }
            if (node instanceof Tree.Declaration) {
                Tree.Declaration dec = (Tree.Declaration) node;
                result |= getDecorationAttributes(dec.getDeclarationModel());
            }
        }
        else if (node instanceof Tree.SpecifierStatement) {
            Tree.Term bme = ((Tree.SpecifierStatement) node).getBaseMemberExpression();
            Declaration d;
            if (bme instanceof Tree.BaseMemberExpression) {
                d = ((Tree.BaseMemberExpression) bme).getDeclaration();
            }
            else if (bme instanceof Tree.ParameterizedExpression) {
                Tree.Primary primary = ((Tree.ParameterizedExpression) bme).getPrimary();
                d = ((Tree.BaseMemberExpression) primary).getDeclaration();
            }
            else {
                 throw new RuntimeException("unexpected node type");
            }
            if (d!=null) {
                Declaration r = getRefinedDeclaration(d);
                if (r!=null) {
                    result |= r.isFormal() ? IMPLEMENTS : REFINES;
                }
            }
        }
        return result;
    }

    private static int getDecorationAttributes(Declaration model) {
            if (model == null) {
                return 0;
            }
            
            int result = 0;
            if (model.isDeprecated()) {
                result |= CeylonLabelProvider.DEPRECATED;
            }
            if (model.isFormal()) {
                result |= CeylonLabelProvider.FORMAL;
            }
            if (model.isAnnotation()) {
                result |= CeylonLabelProvider.ANNOTATION;
            }
            if ((model instanceof Value) && ((Value) model).isVariable()) {
                result |= CeylonLabelProvider.VARIABLE;
            }
            if (model instanceof Class && ((Class) model).isAbstract()) {
                result |= CeylonLabelProvider.ABSTRACT;
            }
    //        if (model instanceof Class && ((Class) model).isFinal()) {
            //            result |= FINAL;
            //        }
            if (model instanceof TypeDeclaration) {
                TypeDeclaration td = (TypeDeclaration) model;
                if(td.getCaseTypeDeclarations()!=null) {
                    result |= CeylonLabelProvider.ENUM;
                }
                if (td.isAlias()) {
                    result |= CeylonLabelProvider.ALIAS;
                }
            }
            if (model.isActual()) {
                Declaration refined = getRefinedDeclaration(model);
                if (refined!=null) {
                    result |= refined.isFormal() ? CeylonLabelProvider.IMPLEMENTS : CeylonLabelProvider.REFINES;
                }
            }
            return result;
        }

    static interface IMarkerFilter {
        boolean select(IMarker marker);
    }
    
    static IMarkerFilter acceptAllMarkers = new IMarkerFilter() {
        @Override
        public boolean select(IMarker marker) {
            return true;
        }
    };
    
    /**
     * Returns the maximum problem marker severity for the given resource, and, if
     * depth is IResource.DEPTH_INFINITE, its children. The return value will be
     * one of IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO
     * or 0, indicating that no problem markers exist on the given resource.
     * @param depth TODO
     */
    static int getMaxProblemMarkerSeverity(IResource res, int depth, IMarkerFilter markerFilter) {
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
    
        for (int i= 0; i < markers.length; i++) {
            IMarker m= markers[i];
            if (markerFilter.select(m)) {
                int priority= m.getAttribute(IMarker.SEVERITY, -1);
                if (priority == IMarker.SEVERITY_WARNING) {
                    hasWarnings= true;
                } 
                else if (priority == IMarker.SEVERITY_ERROR) {
                    return IMarker.SEVERITY_ERROR;
                }
            }
        }
        return hasWarnings ? IMarker.SEVERITY_WARNING : 0;
    }

    /**
     * Returns the maximum problem marker severity for the given resource, and, if
     * depth is IResource.DEPTH_INFINITE, its children. The return value will be
     * one of IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO
     * or 0, indicating that no problem markers exist on the given resource.
     * @param depth TODO
     */
    static int getMaxProblemMarkerSeverity(IResource res, int depth) {
        return getMaxProblemMarkerSeverity(res, depth, acceptAllMarkers);
    }

    private static String getRefinementIconKey(Declaration dec) {
        if (dec.isParameter()) {
            return CEYLON_ARGUMENT;
        }
        else {
            return dec.isFormal() ? 
                CEYLON_FORMAL_REFINEMENT : 
                CEYLON_DEFAULT_REFINEMENT;
        }
    }

    public static Image getRefinementIcon(Declaration dec) {
        return getDecoratedImage(null, getRefinementIconKey(dec), false);
    }
    
}
