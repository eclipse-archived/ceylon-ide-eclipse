package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.appendDeclarationText;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getParameters;

import java.util.List;

import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedTypedReference;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

public class ParameterContextInformation implements IContextInformation {
	
	private Declaration declaration;
	private ProducedReference producedReference;
	private ParameterList parameterList;
	int offset;
    private Unit unit;
    private boolean includeDefaulted;
		
	public ParameterContextInformation(Declaration declaration,
			ProducedReference producedReference, Unit unit,
			ParameterList parameterList, int offset, 
			boolean includeDefaulted) {
		this.declaration = declaration;
		this.producedReference = producedReference;
        this.unit = unit;
		this.parameterList = parameterList;
		this.offset = offset;
		this.includeDefaulted = includeDefaulted;
	}

	@Override
	public String getContextDisplayString() {
		return declaration.getName();
	}
	
	@Override
	public Image getImage() {
		return getImageForDeclaration(declaration);
	}
	
	@Override
	public String getInformationDisplayString() {
		return getParametersInfo(parameterList, producedReference, unit);
	}
	
	public String getParametersInfo(ParameterList parameterList, 
			ProducedReference producedReference, Unit unit) {
		List<Parameter> ps = getParameters(includeDefaulted, 
				parameterList.getParameters());
		if (ps.isEmpty()) {
			return "no parameters";
		}
		StringBuilder sb = new StringBuilder();
		for (Parameter p: ps) {
			if (includeDefaulted || 
					!p.isDefaulted() ||
					(p==ps.get(ps.size()-1) || 
					        unit.isIterableParameterType(p.getType()))) {
				if (producedReference==null) {
					sb.append(p.getName());
				}
				else {
					ProducedTypedReference pr = producedReference.getTypedParameter(p);
					appendDeclarationText(p.getModel(), pr, unit, sb);
				}
				sb.append(", ");
			}
		}
		if (sb.length()>0) {
			sb.setLength(sb.length()-2);
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof ParameterContextInformation) {
			return ((ParameterContextInformation) that).declaration
					.equals(declaration);
		}
		else {
			return false;
		}
		
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
}
