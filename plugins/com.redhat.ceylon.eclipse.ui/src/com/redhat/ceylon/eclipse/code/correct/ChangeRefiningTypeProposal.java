package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendParameterText;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclaration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.util.Nodes;

public class ChangeRefiningTypeProposal {

    static void addChangeRefiningTypeProposal(IFile file,
            Tree.CompilationUnit cu, Collection<ICompletionProposal> proposals,
            Node node) {
        Tree.Declaration decNode = findDeclaration(cu, node);
        if (decNode instanceof Tree.TypedDeclaration) {
            TypedDeclaration dec =
                    ((Tree.TypedDeclaration) decNode).getDeclarationModel();
            Declaration rd = dec.getRefinedDeclaration();
            if (rd instanceof TypedDeclaration) {
                TypeDeclaration decContainer =
                        (TypeDeclaration) dec.getContainer();
                TypeDeclaration rdContainer =
                        (TypeDeclaration) rd.getContainer();
                ProducedType supertype =
                        decContainer.getType().getSupertype(rdContainer);
                ProducedReference pr =
                        rd.getProducedReference(supertype, 
                                Collections.<ProducedType>emptyList());
                ProducedType t = pr.getType();
                String type = t.getProducedTypeName(decNode.getUnit());
                TextFileChange change = 
                        new TextFileChange("Change Type", file);
                int offset = node.getStartIndex();
                int length = node.getStopIndex()-offset+1;
                change.setEdit(new ReplaceEdit(offset, length, type));
                proposals.add(new CorrectionProposal("Change type to '" + type + "'", 
                        change, new Region(offset, length)));
            }
        }
    }

    static void addChangeRefiningParametersProposal(IFile file,
            CompilationUnit cu, Collection<ICompletionProposal> proposals,
            Node node) {
        Tree.Declaration decNode = 
                (Tree.Declaration) Nodes.findStatement(cu, node);
        Tree.ParameterList list;
        if (decNode instanceof Tree.AnyMethod) {
            list = ((Tree.AnyMethod) decNode).getParameterLists().get(0);
        }
        else if (decNode instanceof Tree.AnyClass) {
            list = ((Tree.AnyClass) decNode).getParameterList();
        }
        else {
            return;
        }
        Declaration dec = decNode.getDeclarationModel();
        Declaration rd = dec.getRefinedDeclaration();
        if (rd instanceof Functional && dec instanceof Functional) {
            List<ParameterList> rdPls = 
                    ((Functional) rd).getParameterLists();
            List<ParameterList> decPls = 
                    ((Functional) dec).getParameterLists();
            if (rdPls.isEmpty() || decPls.isEmpty()) {
                return;
            }
            List<Parameter> rdpl =
                    rdPls.get(0).getParameters();
            List<Parameter> dpl =
                    decPls.get(0).getParameters();
            TypeDeclaration decContainer =
                    (TypeDeclaration) dec.getContainer();
            TypeDeclaration rdContainer =
                    (TypeDeclaration) rd.getContainer();
            ProducedType supertype =
                    decContainer.getType().getSupertype(rdContainer);
            ProducedReference pr =
                    rd.getProducedReference(supertype, 
                            Collections.<ProducedType>emptyList());
            List<Tree.Parameter> params = list.getParameters();
            TextFileChange change =
                    new TextFileChange("Fix Refining Parameter List", file);
            change.setEdit(new MultiTextEdit());
            Unit unit = decNode.getUnit();
            for (int i=0; i<params.size(); i++) {
                Tree.Parameter p = params.get(i);
                if (rdpl.size()<=i) {
                    Integer start = i==0 ? 
                            list.getStartIndex()+1 : 
                            params.get(i-1).getStopIndex()+1;
                    Integer stop = params.get(params.size()-1).getStopIndex()+1;
                    change.addEdit(new DeleteEdit(start, stop-start));
                    break;
                }
                else {
                    Parameter rdp = rdpl.get(i);
                    ProducedType pt = 
                            pr.getTypedParameter(rdp).getFullType();
                    ProducedType dt = 
                            dpl.get(i).getModel()
                                .getTypedReference().getFullType();
                    if (!dt.isExactly(pt)) {
                        change.addEdit(new ReplaceEdit(p.getStartIndex(), 
                                p.getStopIndex()-p.getStartIndex()+1, 
                                //TODO: better handling for callable parameters
                                pt.getProducedTypeName(unit) + " " + rdp.getName()));
                    }
                }
            }
            if (rdpl.size()>params.size()) {
                StringBuilder buf = new StringBuilder();
                for (int i=params.size(); i<rdpl.size(); i++) {
                    Parameter p = rdpl.get(i);
                    if (i>0) {
                        buf.append(", ");
                    }
                    appendParameterText(buf, pr, p, unit);
                }
                Integer offset = params.isEmpty() ? 
                        list.getStartIndex()+1 : 
                        params.get(params.size()-1).getStopIndex()+1;
                change.addEdit(new InsertEdit(offset, buf.toString()));
            }
            if (change.getEdit().hasChildren()) {
                proposals.add(new CorrectionProposal("Fix refining parameter list", 
                        change, new Region(list.getStartIndex()+1, 0)));
            }
        }
    }

}
