package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getQualifiedDescriptionFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageKeyForNode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getNodeDecorationAttributes;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getStyledLabelForNode;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_OUTLINES;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StyledString;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.ModelProxy;

public class CeylonElement {
    
    private VirtualFile file; //TODO: is this really the best thing to use nowadays?
    private String qualifiedName;
    private int line;
    private String imageKey;
    private String packageLabel;
    private StyledString label;
    private int decorations;
    private int startOffset;
    private int endOffset;
    private ModelProxy proxy;
    
    public CeylonElement(Node node, 
            VirtualFile file, int line) {
        //the file and line number, which get 
        //displayed in the search results page
        this.file = file;
        this.line = line;
        
        //store enough information to be able to
        //locate the model again if we run a search
        //for usages of the container from the search
        //results view
        startOffset = node.getStartIndex();
        endOffset = node.getStopIndex()+1;
        
        //compute and cache everything we need to 
        //display the search result, without holding 
        //onto a hard ref to the container node
        imageKey = getImageKeyForNode(node);
        label = getStyledLabelForNode(node);
        packageLabel = node.getUnit()==null ? 
                "(unknown package)" : 
                CeylonLabelProvider.getLabel(node.getUnit().getPackage());
        //TODO: this winds up caching error decorations,
        //      so it's not really very good
        decorations = getNodeDecorationAttributes(node);
        
        if (node instanceof Tree.Declaration) {
            proxy = new ModelProxy(((Tree.Declaration) node).getDeclarationModel());
        }
        
        if (node instanceof Tree.Declaration) {
            Declaration dec = ((Tree.Declaration) node).getDeclarationModel();
            qualifiedName = dec.getQualifiedNameString();
        }
        else if (node instanceof Tree.PackageDescriptor) {
            qualifiedName = node.getUnit().getPackage().getNameAsString();
        }
        else if (node instanceof Tree.ModuleDescriptor) {
            qualifiedName = node.getUnit().getPackage().getModule().getNameAsString();
        }
        else if (node instanceof Tree.CompilationUnit) {
            qualifiedName = node.getUnit().getPackage().getNameAsString() + 
                    '/' + node.getUnit().getFilename();
        }
        else if (node instanceof Tree.Import) {
            qualifiedName = node.getUnit().getPackage().getNameAsString() + 
                    '/' + node.getUnit().getFilename() + 
                    '#' + ((Tree.Import) node).getImportPath().getModel().getNameAsString();
        }
        else if (node instanceof Tree.ImportModule) {
            qualifiedName = node.getUnit().getPackage().getNameAsString() + 
                    '/' + node.getUnit().getFilename() + 
                    '@' + ((Tree.ImportModule) node).getImportPath().getModel().getNameAsString();
        }
    }
    
    public String getImageKey() {
        return imageKey;
    }
    
    public String getPackageLabel() {
        return packageLabel;
    }
    
    public StyledString getLabel() {
        if (proxy==null) {
            return label;
        }
        else {
            IPreferenceStore prefs = EditorUtil.getPreferences();
            return getQualifiedDescriptionFor(
                    proxy.getDeclaration(null), 
                    prefs.getBoolean(TYPE_PARAMS_IN_OUTLINES),
                    prefs.getBoolean(PARAMS_IN_OUTLINES),
                    prefs.getBoolean(PARAM_TYPES_IN_OUTLINES),
                    prefs.getBoolean(RETURN_TYPES_IN_OUTLINES));
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
        if (file instanceof IFileVirtualFile) {
            return ((IFileVirtualFile) file).getFile();
        }
        else {
            return null;
        }
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
                return qualifiedName.equals(that.qualifiedName);
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
                (qualifiedName==null ? 0 : qualifiedName.hashCode());
    }
    
}
