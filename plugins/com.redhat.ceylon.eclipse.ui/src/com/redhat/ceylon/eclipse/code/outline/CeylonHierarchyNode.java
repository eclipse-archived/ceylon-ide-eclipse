package com.redhat.ceylon.eclipse.code.outline;

import java.util.Arrays;

import com.redhat.ceylon.eclipse.util.ModelProxy;
import com.redhat.ceylon.model.typechecker.model.Declaration;

class CeylonHierarchyNode 
        implements Comparable<CeylonHierarchyNode> {
    
    private final ModelProxy proxy;
    
    private boolean nonUnique;
    private boolean multiple;
    
    private CeylonHierarchyNode parent;
    
    private CeylonHierarchyNode[] children = 
            new CeylonHierarchyNode[0];

    private boolean focus;
    
    public CeylonHierarchyNode(Declaration declaration) {
        proxy = new ModelProxy(declaration);
    }
    
    public Declaration getDeclaration() {
        return proxy.get();
    }

    boolean isNonUnique() {
        return nonUnique;
    }
    
    void setNonUnique(boolean nonUnique) {
        this.nonUnique = nonUnique;
    }
    
    void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }
    
    public boolean isMultiple() {
        return multiple;
    }
    
    public String getName() {
        return proxy.getName();
    }
    
    void addChild(CeylonHierarchyNode child) {
        if (child==this) {
            return; 
        }
        for (CeylonHierarchyNode c: children) {
            if (child.equals(c)) return;
        }
        int length = children.length;
        CeylonHierarchyNode[] newChildren = 
                new CeylonHierarchyNode[length+1]; 
        System.arraycopy(children, 0, newChildren, 0, length);
        newChildren[length] = child;
        Arrays.sort(newChildren);
        children = newChildren;
        child.parent = this;
    }
    
    public CeylonHierarchyNode[] getChildren() {
        return children;
    }
    
    public CeylonHierarchyNode getParent() {
        return parent;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this==obj) {
            return true;
        }
        else if (obj instanceof CeylonHierarchyNode) {
            return ((CeylonHierarchyNode) obj).proxy.equals(proxy);
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return proxy.hashCode();
    }
    
    @Override
    public String toString() {
        return proxy.toString();
    }

    @Override
    public int compareTo(CeylonHierarchyNode node) {
        if (node.proxy.getQualifiedName()
                .equals(proxy.getQualifiedName())) {
            return 0;
        }
        else {
            int ct = getName().compareTo(node.getName());
            if (ct!=0) {
                return ct;
            }
            else {
                return proxy.getQualifiedName()
                        .compareTo(node.proxy.getQualifiedName());
            }
        }
    }

    public boolean isFocus() {
        return focus;
    }
    
    void setFocus(boolean focus) {
        this.focus = focus;
    }
    
}
