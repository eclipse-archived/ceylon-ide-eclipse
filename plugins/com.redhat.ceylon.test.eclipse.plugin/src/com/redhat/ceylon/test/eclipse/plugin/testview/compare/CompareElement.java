package com.redhat.ceylon.test.eclipse.plugin.testview.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

public class CompareElement implements ITypedElement, IEncodedStreamContentAccessor {

    private final String content;

    public CompareElement(String content) {
        this.content = content;
    }

    @Override
    public String getName() {
        return "<no name>";
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public String getType() {
        return "txt";
    }

    @Override
    public String getCharset() throws CoreException {
        return "UTF-8";
    }

    @Override
    public InputStream getContents() {
        try {
            return new ByteArrayInputStream(content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return new ByteArrayInputStream(content.getBytes());
        }
    }
    
}