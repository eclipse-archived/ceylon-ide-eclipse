package com.redhat.ceylon.eclipse.code.propose;

import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

public class ParameterContextInformation implements IContextInformation {
	
	private Declaration declaration;
	private ProducedReference producedReference;
	private ParameterList parameterList;
	int offset;
		
	public ParameterContextInformation(Declaration declaration,
			ProducedReference producedReference,
			ParameterList parameterList, int offset) {
		this.declaration = declaration;
		this.producedReference = producedReference;
		this.parameterList = parameterList;
		this.offset = offset;
	}

	@Override
	public String getContextDisplayString() {
		return declaration.getName();
	}
	
	@Override
	public Image getImage() {
		return CeylonLabelProvider.getImage(declaration);
	}
	
	@Override
	public String getInformationDisplayString() {
		return getParametersInfo(parameterList, producedReference);
	}
	
	public static String getParametersInfo(ParameterList parameterList, 
			ProducedReference producedReference) {
		if (parameterList.getParameters().isEmpty()) {
			return "no parameters";
		}
		StringBuilder sb = new StringBuilder();
		for (Parameter p: parameterList.getParameters()) {
			if (p.getModel().isDynamicallyTyped()) {
				sb.append("dynamic ");
			}
			else {
				sb.append(producedReference.getTypedParameter(p).getFullType()
						.getProducedTypeName(p.getDeclaration().getUnit()));
			}
			sb.append(" ")
			  .append(p.getName())
			  .append(", ");
		}
		sb.setLength(sb.length()-2);
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof ParameterContextInformation) {
			return ((ParameterContextInformation) that).declaration.equals(declaration);
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
