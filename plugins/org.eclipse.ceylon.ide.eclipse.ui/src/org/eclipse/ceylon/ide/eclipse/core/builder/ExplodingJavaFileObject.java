/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.builder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import org.eclipse.ceylon.javax.lang.model.element.Modifier;
import org.eclipse.ceylon.javax.lang.model.element.NestingKind;
import org.eclipse.ceylon.javax.tools.JavaFileObject;

import org.eclipse.ceylon.langtools.tools.javac.file.RelativePath.RelativeFile;

final class ExplodingJavaFileObject implements
        JavaFileObject {
    private final File classFile;
    private final RelativeFile fileName;
    private final JavaFileObject javaFileObject;

    ExplodingJavaFileObject(File classFile, RelativeFile fileName,
            JavaFileObject javaFileObject) {
        this.classFile = classFile;
        this.fileName = fileName;
        this.javaFileObject = javaFileObject;
    }

    @Override
    public OutputStream openOutputStream()
            throws IOException {
        return new OutputStream() {
            final OutputStream jarStream = javaFileObject.openOutputStream();
            final OutputStream classFileStream = new BufferedOutputStream(new FileOutputStream(classFile));
            @Override
            public void write(int b) throws IOException {
                jarStream.write(b);
                classFileStream.write(b);
            }
            @Override
            public void write(byte[] b, int off, int len)
                    throws IOException {
                jarStream.write(b, off, len);
                classFileStream.write(b, off, len);
            }
            @Override
            public void write(byte[] b) throws IOException {
                jarStream.write(b);
                classFileStream.write(b);
            }
            @Override
            public void close() throws IOException {
                classFileStream.close();
                jarStream.close();
            }
            @Override
            public void flush() throws IOException {
                classFileStream.flush();
                jarStream.flush();
            }
        };
    }

    @Override
    public String toString() {
        return fileName.getPath();
    }

    @Override
    public boolean delete() {
        return javaFileObject.delete();
    }

    @Override
    public CharSequence getCharContent(boolean b)
            throws IOException {
        return javaFileObject.getCharContent(b);
    }

    @Override
    public long getLastModified() {
        return javaFileObject.getLastModified();
    }

    @Override
    public String getName() {
        return javaFileObject.getName();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return javaFileObject.openInputStream();
    }

    @Override
    public Reader openReader(boolean b)
            throws IOException {
        return javaFileObject.openReader(b);
    }

    @Override
    public Writer openWriter() throws IOException {
        return javaFileObject.openWriter();
    }

    @Override
    public URI toUri() {
        return javaFileObject.toUri();
    }

    @Override
    public Modifier getAccessLevel() {
        return javaFileObject.getAccessLevel();
    }

    @Override
    public Kind getKind() {
        return javaFileObject.getKind();
    }

    @Override
    public NestingKind getNestingKind() {
        return javaFileObject.getNestingKind();
    }

    @Override
    public boolean isNameCompatible(String simpleName,
            Kind kind) {
        return javaFileObject.isNameCompatible(simpleName, kind);
    }
}