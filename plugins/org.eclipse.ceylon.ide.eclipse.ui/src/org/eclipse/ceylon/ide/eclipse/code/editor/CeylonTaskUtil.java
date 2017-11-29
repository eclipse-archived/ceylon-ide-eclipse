/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.ide.eclipse.code.editor.RefinementAnnotationCreator.TODO_ANNOTATION_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;

public class CeylonTaskUtil {

    public static void addTaskAnnotation(CommonToken token, IAnnotationModel model) {
        List<Task> tasks = getTasks(token);
        if (tasks != null) {
            for (Task task : tasks) {
                Annotation annotation = new Annotation(TODO_ANNOTATION_TYPE, false, task.text);
                Position position = new Position(task.startIndex, task.text.length());
                model.addAnnotation(annotation, position);
            }
        }
    }

    public static void addTaskMarkers(CommonToken token, IFile file) {
        List<Task> tasks = getTasks(token);
        if (tasks != null) {
            for (Task task : tasks) {
                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                attributes.put(IMarker.PRIORITY, task.priority);
                attributes.put(IMarker.MESSAGE, task.text);
                attributes.put(IMarker.LINE_NUMBER, task.line);
                attributes.put(IMarker.CHAR_START, task.startIndex);
                attributes.put(IMarker.CHAR_END, task.startIndex + task.text.length());
                attributes.put(IMarker.SOURCE_ID, CeylonBuilder.SOURCE);
                attributes.put(IMarker.USER_EDITABLE, false);

                try {
                    file.createMarker(CeylonBuilder.TASK_MARKER_ID).setAttributes(attributes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<Task> getTasks(CommonToken token) {
        List<Task> tasks = null;
        if (token.getType() == CeylonLexer.LINE_COMMENT || 
            token.getType() == CeylonLexer.MULTI_COMMENT) {
            int line = token.getLine();
            int startIndex = token.getStartIndex();
            String[] parts = token.getText().split("(?=TODO|FIXME|XXX|\n|\\*/|/\\*)");
            for (String part : parts) {
                int priority = -1;
                if (part.startsWith("TODO")) {
                    priority = IMarker.PRIORITY_NORMAL;
                }
                else if (part.startsWith("XXX")) {
                    priority = IMarker.PRIORITY_NORMAL;
                }
                else if (part.startsWith("FIXME")) {
                    priority = IMarker.PRIORITY_HIGH;
                }
                else if (part.startsWith("\n")) {
                    line++;
                }

                if (priority != -1) {
                    Task task = new Task(part, priority, line, startIndex);
                    if (tasks == null) {
                        tasks = new ArrayList<Task>();
                    }
                    tasks.add(task);
                }

                startIndex += part.length();
            }
        }
        return tasks;
    }

    public static class Task {

        private final String text;
        private final int priority;
        private final int line;
        private final int startIndex;

        public Task(String text, int priority, int line, int startIndex) {
            this.text = text;
            this.priority = priority;
            this.line = line;
            this.startIndex = startIndex;
        }

        public String getText() {
            return text;
        }

        public int getPriority() {
            return priority;
        }

        public int getLine() {
            return line;
        }

        public int getStartIndex() {
            return startIndex;
        }

    }

}
