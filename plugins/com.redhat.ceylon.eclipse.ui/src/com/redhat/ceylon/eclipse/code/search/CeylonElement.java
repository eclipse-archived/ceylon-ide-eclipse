package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelDecorator.getNodeDecorationAttributes;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageKeyForNode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getStyledLabelForNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.StyledString;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;

public class CeylonElement {
    
    private VirtualFile file;
    private int line;
    private String imageKey;
    private String packageLabel;
    private StyledString label;
    private int decorations;
    
    public CeylonElement(Tree.StatementOrArgument node, 
            VirtualFile file, int line) {
        this.file = file;
        this.line = line;
        imageKey = getImageKeyForNode(node);
        packageLabel = CeylonLabelProvider.getPackageLabel(node);
        label = getStyledLabelForNode(node);
        //TODO: this winds up caching error decorations,
        //      so it's not really very good
        decorations = getNodeDecorationAttributes(node);
    }
    
    public String getImageKey() {
        return imageKey;
    }
    
    public String getPackageLabel() {
        return packageLabel;
    }
    
    public StyledString getLabel() {
        return label;
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
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CeylonElement) {
            CeylonElement that = (CeylonElement) obj;
            return getLocation()==that.getLocation() && 
                    file.equals(that.file);
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return getLocation() ^ file.getName().hashCode();
    }
    
}
