package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer.getRefinementTextFor;

import java.util.Collection;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedTypedReference;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer;
import com.redhat.ceylon.eclipse.imp.proposals.RequiredTypeVisitor;
import com.redhat.ceylon.eclipse.imp.refactoring.AbstractHandler;
import com.redhat.ceylon.eclipse.imp.wizard.NewUnitWizard;

class CreateSubtypeProposal implements ICompletionProposal {

    private CeylonEditor editor;
    private ProducedType type;
    
    public CreateSubtypeProposal(CeylonEditor editor, ProducedType type) {
        this.editor = editor;
        this.type = type;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return CeylonLabelProvider.MOVE;
    }

    @Override
    public String getDisplayString() {
    	return "Create subtype of '" + 
    	        type.getProducedTypeName() + 
    	        "' in new unit";
    }

    @Override
    public IContextInformation getContextInformation() {
    	return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
    	return null;
    }

    @Override
    public void apply(IDocument doc) {
        createSubtype(editor);
    }

    public void createSubtype(CeylonEditor editor) {
        TypeDeclaration td = type.getDeclaration();
        NewUnitWizard.open(subtypeDeclaration(type, false), 
                Util.getFile(editor.getEditorInput()), 
        		"My" + td.getName(), "Create Subtype", 
        		"Create a new Ceylon compilation unit containing the new class.");
    }

    public static String subtypeDeclaration(ProducedType type, boolean object) {
        TypeDeclaration td = type.getDeclaration();
        StringBuilder def = new StringBuilder();
        if (object) {
            def.append("object $className");
        }
        else {
            def.append("class $className");
            boolean first = true;
            for (ProducedType ta: type.getTypeArgumentList()) {
                if (ta.getDeclaration() instanceof TypeParameter) {
                    if (first) {
                        def.append("<");
                        first=false;
                    }
                    else {
                        def.append(", ");
                    }
                    def.append(ta.getDeclaration().getName());
                }
            }
            if (!first) def.append(">");
            if (td instanceof Class) {
            	Class c = (Class) td;
            	if (c.getParameterList()==null ||
            	        c.getParameterList().getParameters().isEmpty()) {
            		def.append("()");
            	}
            	else {
            		def.append("(");
            		for (Parameter p: c.getParameterList().getParameters()) {
            			ProducedTypedReference ptr = type.getTypedParameter(p);
            			def.append(ptr.getType().getProducedTypeName())
            			    .append(" ").append(p.getName()).append(", ");
            		}
            		def.setLength(def.length()-2);
            		def.append(")");
            	}
            }
            else {
            	def.append("()");
            }
        }
        if (td instanceof Class) {
        	Class c = (Class) td;
        	def.append(" extends ").append(td.getName());
        	if (!td .getTypeParameters().isEmpty()) {
        		def.append("<");
        		for (ProducedType ta: type.getTypeArgumentList()) {
        			def.append(ta.getProducedTypeName()).append(", ");
        		}
        		def.setLength(def.length()-2);
        		def.append(">");
        	}
        	if (c.getParameterList().getParameters().isEmpty()) {
        		def.append("()");
        	}
        	else {
        		def.append("(");
        		for (Parameter p: c.getParameterList().getParameters()) {
        			def.append(p.getName()).append(", ");
        		}
        		def.setLength(def.length()-2);
        		def.append(")");
        	}
        }
        else {
        	def.append(" satisfies ").append(td.getName());
        	if (!td.getTypeParameters().isEmpty()) {
        		def.append("<");
        		for (ProducedType ta: type.getTypeArgumentList()) {
        			def.append(ta.getProducedTypeName()).append(", ");
        		}
        		def.setLength(def.length()-2);
        		def.append(">");
        	}
        }
        def.append(" {\n");
        for (DeclarationWithProximity dwp: td.getMatchingMemberDeclarations("", 0).values()) {
        	Declaration d = dwp.getDeclaration();
        	if (d.isFormal() /*&& td.isInheritedFromSupertype(d)*/) {
        		ProducedReference pr = CeylonContentProposer.getRefinedProducedReference(type, d);
        		def.append("    ").append(getRefinementTextFor(d, pr, ""))
        		        .append("\n");
        	}
        }
        def.append("}");
        String result = def.toString();
        return result;
    }

    public static ProducedType getType(CeylonEditor editor) {
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu==null) return null;
        return getType(editor.getParseController().getRootNode(), 
                AbstractHandler.getSelectedNode(editor));
    }

    public static ProducedType getType(Tree.CompilationUnit cu, Node node) {
        if (node instanceof Tree.BaseType) {
        	return ((Tree.BaseType) node).getTypeModel();
        }
        else if (node instanceof Tree.BaseTypeExpression) {
            return ((Tree.BaseTypeExpression) node).getTypeModel();
        }
        else if (node instanceof Tree.ExtendedTypeExpression) {
            return ((Tree.ExtendedTypeExpression) node).getTypeModel();
        }
        else if (node instanceof Tree.ClassOrInterface) {
            return ((Tree.ClassOrInterface) node).getDeclarationModel().getType();
        }
        else {
            RequiredTypeVisitor rtv = new RequiredTypeVisitor(node);
            rtv.visit(cu);
            return rtv.getType();
        }
    }

    public static void add(Collection<ICompletionProposal> proposals, UniversalEditor editor) {
        if (editor instanceof CeylonEditor) {
            ProducedType type = getType((CeylonEditor)editor);
            if (type!=null) {
                proposals.add(new CreateSubtypeProposal((CeylonEditor) editor, type));
            }
        }
    }

}