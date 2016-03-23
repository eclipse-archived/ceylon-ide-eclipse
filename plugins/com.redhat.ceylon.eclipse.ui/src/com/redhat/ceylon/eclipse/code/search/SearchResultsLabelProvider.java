package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.util.Highlights.ARROW_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.ID_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.KW_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.PACKAGE_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.TYPE_ID_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.styleJavaType;
import static com.redhat.ceylon.ide.common.util.toCeylonString_.toCeylonString;
import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;
import static org.eclipse.jdt.core.IJavaElement.TYPE;
import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.eclipse.jface.viewers.StyledString.COUNTER_STYLER;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.ide.common.model.BaseIdeModule;
import com.redhat.ceylon.ide.common.model.CeylonBinaryUnit;
import com.redhat.ceylon.ide.common.model.IJavaModelAware;
import com.redhat.ceylon.ide.common.model.IdeUnit;
import com.redhat.ceylon.ide.common.model.JavaClassFile;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;

public class SearchResultsLabelProvider extends CeylonLabelProvider {
    
    @Override
    public Image getImage(Object element) {
        if (element instanceof WithCategory) {
            WithCategory wsf = 
                    (WithCategory) element;
            element = wsf.getItem();
        }
        if (element instanceof WithSourceFolder) {
            WithSourceFolder wsf = 
                    (WithSourceFolder) element;
            element = wsf.element;
        }
        String key;
        int decorations;
        if (element instanceof CeylonSearchMatch.Type) {
//            key = EXPAND_ALL;
//            decorations = 0;
            return null;
        }
        else if (element instanceof ArchiveMatches) {
            key = RUNTIME_OBJ;
            decorations = 0;
        }
        else if (element instanceof CeylonElement) {
            CeylonElement ce = (CeylonElement) element;
            key = ce.getImageKey(); 
            decorations = ce.getDecorations(); 
        }
        else if (element instanceof IType ||
                element instanceof IField ||
                element instanceof IMethod) {
            IJavaElement je = (IJavaElement) element;
            key = getImageKeyForDeclaration(je); 
            decorations = 0; 
        } else if (element instanceof IJavaModelAware && 
                element instanceof IdeUnit) {
            IdeUnit unit = (IdeUnit) element;
            String sourceFileName = 
                    toJavaString(unit.getSourceFileName());
            if (sourceFileName != null  &&
                    sourceFileName.endsWith("java")) {
                key = JAVA_FILE;
                decorations = 0; 
            } else if (sourceFileName == null &&
                    unit.getCeylonModule().isJava()) {
                key = JAVA_CLASS_FILE;
                decorations = 0; 
            } else {
                key = super.getImageKey(element);
                decorations = 
                        super.getDecorationAttributes(element);
            }
        }
        else {
            key = super.getImageKey(element);
            decorations = 
                    super.getDecorationAttributes(element);
        }
        return getDecoratedImage(key, decorations, false);
    }
    
    @Override
    public StyledString getStyledText(Object element) {
        if (element instanceof WithCategory) {
            WithCategory wc = 
                    (WithCategory) element;
            element = wc.getItem();
        }
        if (element instanceof WithSourceFolder) {
            WithSourceFolder wsf = 
                    (WithSourceFolder) element;
            element = wsf.element;
        }
        if (element instanceof CeylonSearchMatch.Type) {
            return new StyledString(element.toString(), 
                    Highlights.ARROW_STYLER);
        }
        if (element instanceof ArchiveMatches) {
            return new StyledString("Source Archive Matches");
        }
        else if (element instanceof CeylonElement) {
            CeylonElement ce = (CeylonElement) element;
            return getStyledLabelForSearchResult(ce);
        }
        else if (element instanceof IType ||
                element instanceof IField||
                element instanceof IMethod) {
            IJavaElement je = (IJavaElement) element;
            return getStyledLabelForSearchResult(je);
        }
        else if (element instanceof Module ||
                element instanceof Package ||
                element instanceof IPackageFragment ||
                element instanceof IPackageFragmentRoot) {
            StyledString text = super.getStyledText(element);
            StyledString styledString = 
                    new StyledString(text.toString());
            if (appendSourceLocation()) {
                String path = null;
                if (element instanceof BaseIdeModule) {
                    BaseIdeModule mod = (BaseIdeModule) element;
                    path = toJavaString(mod.getSourceArchivePath());
                }
                else if (element instanceof JarPackageFragmentRoot) {
                    try {
                        IPackageFragmentRoot pfr = 
                                (IPackageFragmentRoot) 
                                    element;
                        IPath sap = 
                                pfr.getSourceAttachmentPath();
                        if (sap!=null) {
                            path = sap.toPortableString();
                        }
                    }
                    catch (JavaModelException e) {
                        e.printStackTrace();
                    }
                }
                if (path!=null) {
                    styledString.append(" \u2014 " + path, 
                            COUNTER_STYLER);
                }
            }
            return styledString;
        } else if (element instanceof IdeUnit) {
            IdeUnit unit = (IdeUnit) element;
            String displayedFileName = 
                    toJavaString(unit.getSourceFileName());
            if (displayedFileName == null) {
                displayedFileName = unit.getFilename();
            }
            return new StyledString(displayedFileName);
        }
        else {
            return super.getStyledText(element);
        }
    }
    
    boolean appendMatchPackage() {
        return true;
    }
    
    boolean appendSourceLocation() {
        return true;
    }
    
    private StyledString getStyledLabelForSearchResult(
            CeylonElement ce) {
        StyledString styledString = new StyledString();
        styledString.append(ce.getLabel());
        if (appendMatchPackage()) {
            styledString.append(
                    " \u2014 " + ce.getPackageLabel(), 
                    PACKAGE_STYLER);
            if (appendSourceLocation()) {
                styledString.append(
                        " \u2014 " + ce.getPathString(), 
                        COUNTER_STYLER);
            }
        }
        return styledString;
    }

    private StyledString getStyledLabelForSearchResult(
            IJavaElement je) {
        StyledString styledString = new StyledString();
        String name = je.getElementName();
        IPreferenceStore prefs = 
                CeylonPlugin.getPreferences();
        if (je instanceof IMethod) {
            IMethod m = (IMethod) je;
            try {
                String returnType = m.getReturnType();
                if (returnType.equals("V")) {
                    styledString.append("void", KW_STYLER);
                }
                else {
                    styledString.append("method", KW_STYLER);
                    /*styleJavaType(styledString, 
                            getSignatureSimpleName(returnType));*/
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            styledString.append(' ');
            IJavaElement parent = je.getParent();
            if (parent instanceof IType) {
                styledString.append(parent.getElementName(), 
                        TYPE_ID_STYLER)
                            .append('.');
            }
            styledString.append(name, ID_STYLER);
            boolean names = 
                    prefs.getBoolean(PARAMS_IN_OUTLINES);
            boolean types = 
                    prefs.getBoolean(PARAM_TYPES_IN_OUTLINES);
            if (names || types) {
                try {
                    styledString.append('(');
                    String[] parameterTypes = 
                            m.getParameterTypes();
                    String[] parameterNames = 
                            m.getParameterNames();
                    boolean first = true;
                    for (int i=0; 
                            i<parameterTypes.length && 
                            i<parameterNames.length; 
                            i++) {
                        if (first) {
                            first = false;
                        }
                        else {
                            styledString.append(", ");
                        }
                        if (types) {
                            styleJavaType(styledString, 
                                    getSignatureSimpleName(
                                            parameterTypes[i]));
                        }
                        if (types&&names) {
                            styledString.append(' ');
                        }
                        if (names) {
                            styledString.append(parameterNames[i], 
                                    ID_STYLER);
                        }
                    }
                    styledString.append(')');
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (prefs.getBoolean(RETURN_TYPES_IN_OUTLINES)) {
                try {
                    String returnType = m.getReturnType();
                    styledString.append(" ∊ ");
                    styleJavaType(styledString,
                            getSignatureSimpleName(returnType), 
                            ARROW_STYLER);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if (je instanceof IField) {
            styledString.append("field", KW_STYLER);
            /*try {
                String type = ((IField) je).getTypeSignature();
                styleJavaType(styledString, 
                        getSignatureSimpleName(type));
            }
            catch (Exception e) {
                e.printStackTrace();
            }*/
            styledString.append(' ')
                .append(name, ID_STYLER);
            if (prefs.getBoolean(RETURN_TYPES_IN_OUTLINES)) {
                try {
                    IField f = (IField) je;
                    String type = f.getTypeSignature();
                    styledString.append(" ∊ ");
                    styleJavaType(styledString,
                            getSignatureSimpleName(type), 
                            ARROW_STYLER);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if (je instanceof IType) {
            IType type = (IType) je;
            try {
                if (type.isAnnotation()) {
                    styledString.append('@')
                        .append("interface ", 
                                KW_STYLER);
                }
                else if (type.isInterface()) {
                    styledString.append("interface ", 
                            KW_STYLER);
                }
                else if (type.isClass()) {
                    styledString.append("class ", 
                            KW_STYLER);
                }
                else if (type.isEnum()) {
                    styledString.append("enum ", 
                            KW_STYLER);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            styledString.append(name, TYPE_ID_STYLER);
        }
        if (appendMatchPackage()) {
            IPackageFragment pkg = 
                    (IPackageFragment) 
                        je.getAncestor(PACKAGE_FRAGMENT);
            styledString.append(" \u2014 ", PACKAGE_STYLER)
                        .append(pkg.getElementName(), 
                                PACKAGE_STYLER);
            if (appendSourceLocation()) {
                try {
                    IType type = 
                            (IType) je.getAncestor(TYPE);
                    
                    IJavaModelAware unit = 
                            CeylonBuilder.getUnit(type);
                    if (unit instanceof CeylonBinaryUnit) {
                        CeylonBinaryUnit bu = 
                                (CeylonBinaryUnit)unit;
                        String fullPath = 
                                toJavaString(bu.getSourceFullPath());
                        if (fullPath != null) {
                            return styledString.append(
                                    " \u2014 " + fullPath, 
                                    COUNTER_STYLER);
                        }
                    }
                    if (unit instanceof JavaClassFile) {
                        JavaClassFile javaClassFile = 
                                (JavaClassFile) unit;
                        BaseIdeModule module = 
                                javaClassFile.getCeylonModule();
                        if (module.getIsCeylonBinaryArchive()) {
                            String sourceRelativePath = 
                                    sourceRelativePath(
                                            javaClassFile, 
                                            module);
                            String sourceArchivePath = 
                                    toJavaString(module.getSourceArchivePath());
                            if (sourceRelativePath != null && 
                                sourceArchivePath != null) {
                                return styledString.append(
                                         " \u2014 "
                                         + sourceArchivePath 
                                         + "!/"
                                         + sourceRelativePath, 
                                        COUNTER_STYLER);
                            }
                        }
                    }
                    IPackageFragmentRoot root = 
                            (IPackageFragmentRoot) 
                                je.getAncestor(
                                        PACKAGE_FRAGMENT_ROOT);
                    if (type instanceof BinaryType) {
                        BinaryType bt = (BinaryType) type;
                        IBinaryType info = 
                                (IBinaryType) 
                                    bt.getElementInfo();
                        String simpleSourceFileName = 
                                bt.getSourceFileName(info);
                        PackageFragment pkgFrag = 
                                (PackageFragment) 
                                    type.getPackageFragment();
                        IPath sourceArchivePath = 
                                root.getSourceAttachmentPath();
                        IPath sourceFolderPath = 
                                root.getSourceAttachmentRootPath();
                        if (sourceArchivePath != null && 
                            sourceFolderPath != null) {
                            IPath subpath = 
                                    sourceFolderPath.makeAbsolute();
                            for (String pn: pkgFrag.names) {
                                subpath = subpath.append(pn);
                            }
                            subpath.append(simpleSourceFileName);
                            return styledString.append(
                                    " \u2014 " 
                                    + sourceArchivePath.toPortableString()
                                    + "!"
                                    + subpath.toPortableString(), 
                                    COUNTER_STYLER);
                        }
                    }
                    else if (type instanceof SourceType) {
                        String fullPath = 
                                type.getCompilationUnit()
                                    .getCorrespondingResource()
                                    .getFullPath()
                                    .toPortableString();
                        return styledString.append(
                                " \u2014 " + fullPath, 
                                COUNTER_STYLER);
                    }
                    //new SourceMapper(root.getSourceAttachmentPath(), root.getSourceAttachmentRootPath()==null ? null : root.getSourceAttachmentRootPath().toString(), null)
                    //.findSource(t, info);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return styledString;
    }

    private static String sourceRelativePath(
            JavaClassFile javaClassFile, 
            BaseIdeModule module) {
        ceylon.language.String path = 
                toCeylonString(javaClassFile.getRelativePath());
        return toJavaString(module.toSourceUnitRelativePath(path));
    }

    private static String getImageKeyForDeclaration(
            IJavaElement e) {
        if (e==null) return null;
        boolean shared = false;
        if (e instanceof IMember) {
            try {
                IMember m = (IMember) e;
                shared = Flags.isPublic(m.getFlags());
            }
            catch (JavaModelException jme) {
                jme.printStackTrace();
            }
        }
        switch(e.getElementType()) {
        case IJavaElement.METHOD:
            if (shared) {
                return CEYLON_METHOD;
            }
            else {
                return CEYLON_LOCAL_METHOD;
            }
        case IJavaElement.FIELD:
            if (shared) {
                return CEYLON_ATTRIBUTE;
            }
            else {
                return CEYLON_LOCAL_ATTRIBUTE;
            }
        case IJavaElement.TYPE:
            if (shared) {
                return CEYLON_CLASS;
            }
            else {
                return CEYLON_LOCAL_CLASS;
            }
        default:
            return null;
        }
    }

}