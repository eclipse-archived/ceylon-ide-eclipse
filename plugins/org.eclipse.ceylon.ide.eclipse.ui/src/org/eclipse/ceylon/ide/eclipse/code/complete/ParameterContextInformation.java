/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.ide.eclipse.code.complete.CodeCompletions.appendParameterContextInfo;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.getParameters;
import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;

import java.util.List;

import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Parameter;
import org.eclipse.ceylon.model.typechecker.model.ParameterList;
import org.eclipse.ceylon.model.typechecker.model.Reference;
import org.eclipse.ceylon.model.typechecker.model.TypedReference;
import org.eclipse.ceylon.model.typechecker.model.Unit;
import org.eclipse.ceylon.model.typechecker.model.Value;

final class ParameterContextInformation 
            implements IContextInformation {
        
        private final Declaration declaration;
        private final Reference producedReference;
        private final ParameterList parameterList;
        private final int argumentListOffset;
        private final Unit unit;
        private final boolean includeDefaulted;
//        private final boolean inLinkedMode;
        private final boolean namedInvocation;
        
        ParameterContextInformation(
                Declaration declaration,
                Reference producedReference, Unit unit,
                ParameterList parameterList, 
                int argumentListOffset, 
                boolean includeDefaulted, 
                boolean namedInvocation) {
//                boolean inLinkedMode
            this.declaration = declaration;
            this.producedReference = producedReference;
            this.unit = unit;
            this.parameterList = parameterList;
            this.argumentListOffset = argumentListOffset;
            this.includeDefaulted = includeDefaulted;
//            this.inLinkedMode = inLinkedMode;
            this.namedInvocation = namedInvocation;
        }
        
        @Override
        public String getContextDisplayString() {
            return "Parameters of '" + declaration.getName() + "'";
        }
        
        @Override
        public Image getImage() {
            return getImageForDeclaration(declaration);
        }
        
        @Override
        public String getInformationDisplayString() {
            List<Parameter> ps = 
                    getParameters(parameterList, 
                            includeDefaulted, 
                            namedInvocation);
            if (ps.isEmpty()) {
                return "no parameters";
            }
            StringBuilder result = new StringBuilder();
            for (Parameter p: ps) {
                boolean isListedValues = 
                        namedInvocation && 
                        p==ps.get(ps.size()-1) &&
                        p.getModel() instanceof Value && 
                        p.getType()!=null &&
                        unit.isIterableParameterType(
                                p.getType());
                if (includeDefaulted || !p.isDefaulted() ||
                        isListedValues) {
                    if (producedReference==null) {
                        result.append(p.getName());
                    }
                    else {
                        TypedReference pr = 
                                producedReference.getTypedParameter(p);
                        appendParameterContextInfo(
                                result, pr, p, unit, 
                                namedInvocation, 
                                isListedValues);
                    }
                    if (!isListedValues) {
                        result.append(namedInvocation ? "; " : ", ");
                    }
                }
            }
            if (!namedInvocation && result.length()>0) {
                result.setLength(result.length()-2);
            }
            return result.toString();
        }
        
        @Override
        public boolean equals(Object that) {
            if (that instanceof ParameterContextInformation) {
                ParameterContextInformation pci = 
                        (ParameterContextInformation) that;
                return pci.declaration.equals(declaration);
            }
            else {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            return declaration.hashCode();
        }
        
        int getArgumentListOffset() {
            return argumentListOffset;
        }
        
    }