package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.DISPLAY_RETURN_TYPES;
import static com.redhat.ceylon.eclipse.util.Highlights.ARROW_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.ID_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.KW_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.PACKAGE_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.TYPE_ID_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.styleJavaType;
import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.eclipse.jface.viewers.StyledString.COUNTER_STYLER;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Highlights;

public class SearchResultsLabelProvider extends CeylonLabelProvider {
    
    @Override
    public Image getImage(Object element) {
        if (element instanceof WithSourceFolder) {
            element = ((WithSourceFolder) element).element;
        }
        String key;
        int decorations;
        if (element instanceof ArchiveMatches) {
            key = RUNTIME_OBJ;
            decorations = 0;
        }
        else if (element instanceof CeylonElement) {
            key = ((CeylonElement) element).getImageKey(); 
            decorations = ((CeylonElement) element).getDecorations(); 
        }
        else if (element instanceof IType ||
                element instanceof IField ||
                element instanceof IMethod) {
            key = getImageKeyForDeclaration((IJavaElement) element); 
            decorations = 0; 
        }
        else {
            key = super.getImageKey(element);
            decorations = super.getDecorationAttributes(element);
        }
        return getDecoratedImage(key, decorations, false);
    }
    
    @Override
    public StyledString getStyledText(Object element) {
        if (element instanceof WithSourceFolder) {
            element = ((WithSourceFolder) element).element;
        }
        if (element instanceof ArchiveMatches) {
            return new StyledString("Source Archive Matches");
        }
        else if (element instanceof CeylonElement) {
            return getStyledLabelForSearchResult((CeylonElement) element);
        }
        else if (element instanceof IType ||
                element instanceof IField||
                element instanceof IMethod) {
            return getStyledLabelForSearchResult((IJavaElement) element);
        }
        else {
            if (element instanceof Module ||
                element instanceof Package ||
                element instanceof IPackageFragment) {
                return new StyledString(super.getStyledText(element).toString());
            }
            else {
                return super.getStyledText(element);
            }
        }
    }

    private StyledString getStyledLabelForSearchResult(CeylonElement ce) {
        StyledString styledString = new StyledString();
        IFile file = ce.getFile();
        String path = file==null ? 
                ce.getVirtualFile().getPath() : 
                    file.getFullPath().toString();
        styledString.append(ce.getLabel())
                    .append(" - " + ce.getPackageLabel(), PACKAGE_STYLER)
                    .append(" - " + path, COUNTER_STYLER);
        return styledString;
    }

    private StyledString getStyledLabelForSearchResult(IJavaElement je) {
        StyledString styledString = new StyledString();
        String name = je.getElementName();
        if (je instanceof IMethod) {
            try {
                String returnType = ((IMethod) je).getReturnType();
                if (returnType.equals("V")) {
                    styledString.append("void", Highlights.KW_STYLER);
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
            styledString.append(' ').append(name, ID_STYLER);
            try {
                styledString.append('(');
                String[] parameterTypes = ((IMethod) je).getParameterTypes();
                String[] parameterNames = ((IMethod) je).getParameterNames();
                boolean first = true;
                for (int i=0; i<parameterTypes.length && i<parameterNames.length; i++) {
                    if (first) {
                        first = false;
                    }
                    else {
                        styledString.append(", ");
                    }
                    styleJavaType(styledString, 
                            getSignatureSimpleName(parameterTypes[i]));
                    styledString.append(' ')
                                .append(parameterNames[i], ID_STYLER);
                }
                styledString.append(')');
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (EditorUtil.getPreferences().getBoolean(DISPLAY_RETURN_TYPES)) {
                try {
                    String returnType = ((IMethod) je).getReturnType();
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
            styledString.append(' ').append(name, ID_STYLER);
            if (EditorUtil.getPreferences().getBoolean(DISPLAY_RETURN_TYPES)) {
                try {
                    String type = ((IField) je).getTypeSignature();
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
                    styledString.append('@').append("interface ", KW_STYLER);
                }
                else if (type.isInterface()) {
                    styledString.append("interface ", KW_STYLER);
                }
                else if (type.isClass()) {
                    styledString.append("class ", KW_STYLER);
                }
                else if (type.isEnum()) {
                    styledString.append("enum ", KW_STYLER);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            styledString.append(name, TYPE_ID_STYLER);
        }
        IJavaElement pkg = ((IJavaElement) je.getOpenable()).getParent();
        styledString.append(" - ", PACKAGE_STYLER)
                    .append(pkg.getElementName(), PACKAGE_STYLER);
        IFile file = (IFile) je.getResource();
        if (file!=null) {
            styledString.append(" - " + file.getFullPath().toString(), COUNTER_STYLER);
        }
        return styledString;
    }

    private static String getImageKeyForDeclaration(IJavaElement e) {
        if (e==null) return null;
        boolean shared = false;
        if (e instanceof IMember) {
            try {
                shared = Flags.isPublic(((IMember) e).getFlags());
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