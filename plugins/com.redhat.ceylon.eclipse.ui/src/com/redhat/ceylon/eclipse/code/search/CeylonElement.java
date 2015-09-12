package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getQualifiedDescriptionFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageKeyForNode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getNodeDecorationAttributes;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getStyledLabelForNode;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPreferences;
import static com.redhat.ceylon.eclipse.util.Nodes.getImportedName;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Font;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.core.vfs.vfsJ2C;
import com.redhat.ceylon.eclipse.util.ModelProxy;
import com.redhat.ceylon.ide.common.vfs.ResourceVirtualFile;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class CeylonElement {
    
    private VirtualFile file; //TODO: is this really the best thing to use nowadays?
    private String qualifiedName;
    private int line;
    private int decline;
    private String imageKey;
    private String packageLabel;
    private StyledString label;
    private int decorations;
    private int startOffset;
    private int endOffset;
    private final ModelProxy proxy;
    
    public CeylonElement(Node node, VirtualFile file, int line) {
        //the file and line number, which get 
        //displayed in the search results page
        this.file = file;
        this.line = line;
        
        //store enough information to be able to
        //locate the model again if we run a search
        //for usages of the container from the search
        //results view
        startOffset = node.getStartIndex();
        endOffset = node.getEndIndex();
        
        //compute and cache everything we need to 
        //display the search result, without holding 
        //onto a hard ref to the container node
        imageKey = getImageKeyForNode(node);
        label = getStyledLabelForNode(node);
        
        Unit unit = node.getUnit();
        if (unit==null) {
            packageLabel = "(unknown package)";
        }
        else {
            Package pack = unit.getPackage();
            String name = pack.getQualifiedNameString();
            packageLabel = name.isEmpty() ? 
                    "(default package)" : name;
        }
        //TODO: this winds up caching error decorations,
        //      so it's not really very good
        decorations = getNodeDecorationAttributes(node);
        
        if (node instanceof Tree.Declaration) {
            Tree.Declaration decl = (Tree.Declaration) node;
            Declaration d = decl.getDeclarationModel();
            proxy = new ModelProxy(d);
        }
        else {
            proxy = null;
        }
        
        if (node instanceof Tree.Declaration) {
            Tree.Declaration decl = (Tree.Declaration) node;
            Declaration dec = decl.getDeclarationModel();
            qualifiedName = dec.getQualifiedNameString();
            decline = decl.getToken().getLine();
        }
        else if (node instanceof Tree.PackageDescriptor) {
            qualifiedName = 
                    unit.getPackage()
                        .getNameAsString();
        }
        else if (node instanceof Tree.ModuleDescriptor) {
            qualifiedName = 
                    unit.getPackage()
                        .getModule()
                        .getNameAsString();
        }
        else if (node instanceof Tree.CompilationUnit) {
            qualifiedName = 
                    unit.getPackage()
                        .getNameAsString() 
                    + '/' + unit.getFilename();
        }
        else if (node instanceof Tree.Import) {
            Tree.Import imp = (Tree.Import) node;
            String name = getImportedName(imp);
            qualifiedName = 
                    unit.getPackage()
                        .getNameAsString() 
                    + '/' + unit.getFilename() 
                    + '#' + name;
        }
        else if (node instanceof Tree.ImportModule) {
            Tree.ImportModule imp = (Tree.ImportModule) node;
            String name = getImportedName(imp);
            qualifiedName = 
                    unit.getPackage()
                        .getNameAsString() 
                    + '/' + unit.getFilename() 
                    + '@' + name;
        }
    }
    
    public String getImageKey() {
        return imageKey;
    }
    
    public String getPackageLabel() {
        return packageLabel;
    }
    
    public StyledString getLabel() {
        return getLabel(null, null);
    }
    
    public StyledString getLabel(String prefix, Font font) {
        if (proxy==null) {
            return label;
        }
        else {
            IPreferenceStore prefs = getPreferences();
            return getQualifiedDescriptionFor(
                    proxy.get(), 
                    prefs.getBoolean(TYPE_PARAMS_IN_OUTLINES),
                    prefs.getBoolean(PARAMS_IN_OUTLINES),
                    prefs.getBoolean(PARAM_TYPES_IN_OUTLINES),
                    prefs.getBoolean(RETURN_TYPES_IN_OUTLINES),
                    prefix, font);
        }
    }
    
    public int getDecorations() {
        return decorations;
    }
    
    public int getLocation() {
        return line;
    }
    
    public VirtualFile getVirtualFile() {
        return file;
    }
    
    public IFile getFile() {
        if (vfsJ2C.instanceOfIFileVirtualFile(file)) {
            return vfsJ2C.getIFileVirtualFile(file).getNativeResource();
        }
        else {
            return null;
        }
    }
    
    //for no good reason that I can see, ResourceVirtualFiles
    //return a project-relative path from getPath()
    @SuppressWarnings("rawtypes")
    public String getPathString() {
        if (file instanceof ResourceVirtualFile) {
            ResourceVirtualFile rvf = 
                    (ResourceVirtualFile) file;
            Object nativeResource = rvf.getNativeResource();
            if (nativeResource instanceof IResource) {
                return ((IResource)nativeResource)
                            .getFullPath()
                            .toPortableString();
            }
        }
        return file.getPath();
    }
    
    public int getStartOffset() {
        return startOffset;
    }
    
    public int getEndOffset() {
        return endOffset;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CeylonElement) {
            CeylonElement that = (CeylonElement) obj;
            if (!file.equals(that.file)) {
                return false;
            }
            if (qualifiedName!=null && that.qualifiedName!=null) {
                return qualifiedName.equals(that.qualifiedName) &&
                        //handle overloaded methods
                        decline == that.decline;
            }
            else if (qualifiedName==null && that.qualifiedName==null) {
                return line==that.line;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return file.getName().hashCode() ^
                (qualifiedName==null ? 
                        0 : qualifiedName.hashCode());
    }
    
}
