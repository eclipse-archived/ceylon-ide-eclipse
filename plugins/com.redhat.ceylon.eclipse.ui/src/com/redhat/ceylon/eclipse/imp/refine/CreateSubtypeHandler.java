package com.redhat.ceylon.eclipse.imp.refine;

import static com.redhat.ceylon.eclipse.imp.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer.getRefinementTextFor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

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
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer;
import com.redhat.ceylon.eclipse.imp.wizard.NewUnitWizard;

public class CreateSubtypeHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu==null) return null;
        Node node = getSelectedNode(editor);
        ProducedType type;
        if (node instanceof Tree.BaseType) {
        	type = ((Tree.BaseType) node).getTypeModel();
        }
        else if (node instanceof Tree.BaseTypeExpression) {
            type = ((Tree.BaseTypeExpression) node).getTypeModel();
        }
        else if (node instanceof Tree.ExtendedTypeExpression) {
            type = ((Tree.ExtendedTypeExpression) node).getTypeModel();
        }
        else if (node instanceof Tree.ClassOrInterface) {
        	type = ((Tree.ClassOrInterface) node).getDeclarationModel().getType();
        }
        else {
        	return null;
        }
        
        StringBuilder def = new StringBuilder();
        TypeDeclaration td = type.getDeclaration();
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
        	if (c.getParameterList().getParameters().isEmpty()) {
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
        NewUnitWizard.open(def.toString(), Util.getFile(editor.getEditorInput()), 
        		"My" + td.getName(), "Create Subtype", 
        		"Create a new Ceylon compilation unit containing the new class.");
        
		return null;
	}

    //TODO: copy/pasted from AbstractFindAction
    private static Node getSelectedNode(CeylonEditor editor) {
        CeylonParseController cpc = editor.getParseController();
        return cpc.getRootNode()==null ? null : 
            findNode(cpc.getRootNode(), 
                (ITextSelection) editor.getSelectionProvider().getSelection());
    }

    //TODO: copy/pasted from RefineFormalMembersHandler
    @Override
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() && 
                editor instanceof CeylonEditor &&
                editor.getEditorInput() instanceof IFileEditorInput) {
            Node node = getSelectedNode((CeylonEditor) editor);
            return node instanceof Tree.BaseType || 
                    node instanceof Tree.BaseTypeExpression ||
                    node instanceof Tree.ExtendedTypeExpression ||
            		node instanceof Tree.ClassOrInterface;
        }
        else {
            return false;
        }
    }
}
