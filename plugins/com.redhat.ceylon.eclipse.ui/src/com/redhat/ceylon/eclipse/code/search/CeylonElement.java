package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageKeyForNode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getNodeDecorationAttributes;
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
    private int startOffset;
    private int endOffset;
    
    public CeylonElement(Tree.StatementOrArgument node, 
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
