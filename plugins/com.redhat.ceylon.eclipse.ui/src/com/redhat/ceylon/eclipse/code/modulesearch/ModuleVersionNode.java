package com.redhat.ceylon.eclipse.code.modulesearch;

import java.util.SortedSet;

import com.redhat.ceylon.common.Backends;

public class ModuleVersionNode {

    private final ModuleNode module;
    private final String version;
    private boolean filled;
    private String license;
    private String doc;
    private SortedSet<String> authors;
    private Backends nativeBackend;

    public ModuleVersionNode(ModuleNode module, String version) {
        this.module = module;
        this.version = version;
    }

    public ModuleNode getModule() {
        return module;
    }

    public String getVersion() {
        return version;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public SortedSet<String> getAuthors() {
        return authors;
    }

    public void setAuthors(SortedSet<String> authors) {
        this.authors = authors;
    }
    
    public Backends getNativeBackend() {
        return nativeBackend;
    }
    
    public void setNativeBackend(Backends nativeBackend) {
        this.nativeBackend = nativeBackend;
    }
    
    public String getAuthorsCommaSeparated() {
        StringBuilder authorsBuilder = new StringBuilder();
        if (authors != null) {
            boolean isFirst = true;
            for (String author : authors) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    authorsBuilder.append(", ");
                }
                authorsBuilder.append(author);
            }
        }
        return authorsBuilder.toString();
    }

}