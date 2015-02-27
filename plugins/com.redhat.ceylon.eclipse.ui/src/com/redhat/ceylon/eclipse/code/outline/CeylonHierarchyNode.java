package com.redhat.ceylon.eclipse.code.outline;

import java.util.ArrayList;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.util.ModelProxy;

class CeylonHierarchyNode 
        implements Comparable<CeylonHierarchyNode> {
    
    private final ModelProxy proxy;
    
    private boolean nonUnique;
    private boolean multiple;
    
    //private final CeylonHierarchyNode parent;
    private final List<CeylonHierarchyNode> children = 
            new ArrayList<CeylonHierarchyNode>();
    
    public CeylonHierarchyNode(Declaration declaration) {
        proxy = new ModelProxy(declaration);
    }
    
    public Declaration getDeclaration() {
        return proxy.getDeclaration();
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
        if (!children.contains(child)) children.add(child);
    }
    
    public List<CeylonHierarchyNode> getChildren() {
        return children;
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
    
}
