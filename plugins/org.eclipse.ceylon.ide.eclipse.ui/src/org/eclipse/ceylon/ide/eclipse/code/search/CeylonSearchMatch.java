/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;

import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;

import org.eclipse.search.ui.text.Match;

import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.common.search.FindContainerVisitor;

public class CeylonSearchMatch extends Match {
    
    public static enum Type {
        DECLARATION {
            @Override
            public String toString() {
                return "Declarations";
            }
        },
        ASSIGNMENT {
            @Override
            public String toString() {
                return "Assignments";
            }
        }, 
        TYPE_USAGE {
            @Override
            public String toString() {
                return "Uses in type expressions";
            }
        },
        EXPRESSION_USAGE {
            @Override
            public String toString() {
                return "Uses in value expressions";
            }
        },
        IMPORT {
            @Override
            public String toString() {
                return "Imports";
            }
        }, 
        DOC_LINK  {
            @Override
            public String toString() {
                return "References in documentation annotations";
            }
        },
        JAVA {
            @Override
            public String toString() {
                return "References in Java code";
            }
        }
    }
    
    public static CeylonSearchMatch create(
            Node match, 
            //the containing declaration or named arg
            Tree.CompilationUnit rootNode,
            //the file in which the match occurs
            VirtualFile file) {
        FindContainerVisitor fcv = 
                new FindContainerVisitor(match) {
            @Override
            public boolean accept(
                    Tree.StatementOrArgument node) {
                if (node instanceof Tree.Declaration) {
                    Tree.Declaration dec = 
                            (Tree.Declaration) node;
                    Declaration d = 
                            dec.getDeclarationModel();
                    return d.isToplevel() || 
                           d.isClassOrInterfaceMember();
                }
                else {
                    return true;
                }
            }
        };
        rootNode.visit(fcv);
        Tree.StatementOrArgument result = 
                fcv.getStatementOrArgument();
        Node node = result==null ? rootNode : result;
        return new CeylonSearchMatch(match, node, file);
    }
    
    private CeylonSearchMatch(Node match, 
            //the containing declaration or named arg
            Node node,
            //the file in which the match occurs
            VirtualFile file) {
        super(new CeylonElement(node, file, 
                match.getToken().getLine(),
                getType(match)),
                //the exact location of the match:
                getIdentifyingNode(match).getStartIndex(), 
                getIdentifyingNode(match).getDistance());
    }

    private static Type getType(Node node) {
        Type type;        
        if (node instanceof Tree.Import || 
            node instanceof Tree.ImportMemberOrType ||
            node instanceof Tree.ImportModule) {
            type = Type.IMPORT;
        }
        else if (node instanceof Tree.DocLink) {
            type = Type.DOC_LINK;
        }
        else if (node instanceof Tree.Type) {
            type = Type.TYPE_USAGE;
        }
        else if (node instanceof Tree.StaticMemberOrTypeExpression) {
            if (((Tree.StaticMemberOrTypeExpression) node).getAssigned()) {
                type = Type.ASSIGNMENT;
            }
            else {
                type = Type.EXPRESSION_USAGE;
            }
        }
        else if (node instanceof Tree.SpecifierStatement ||
                 node instanceof Tree.SpecifiedArgument) {
            type = Type.ASSIGNMENT;
        }
        else if (node instanceof Tree.TypeConstraint ||
                node instanceof Tree.InitializerParameter ||
                node instanceof Tree.Declaration ||
                node instanceof Tree.TypedArgument) {
            type = Type.DECLARATION;
        }
        else {
            type = Type.EXPRESSION_USAGE;
        }
        return type;
    }
    
    @Override
    public CeylonElement getElement() {
        return (CeylonElement) super.getElement();
    }
    
    public boolean isInImport() {
        return getElement().getType()==Type.IMPORT;
    }
    
}
