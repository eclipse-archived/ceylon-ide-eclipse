package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.internal.ui.DefaultLabelProvider;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.internal.debug.core.logicalstructures.JDIAllInstancesValue;
import org.eclipse.jdt.internal.debug.core.model.JDIClassType;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDINullValue;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDIReferenceListValue;
import org.eclipse.jdt.internal.debug.ui.DebugUIMessages;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;
import org.eclipse.osgi.util.NLS;

import com.redhat.ceylon.compiler.java.runtime.metamodel.Metamodel;
import com.redhat.ceylon.eclipse.core.debug.CeylonJDIDebugTarget.EvaluationListener;
import com.redhat.ceylon.eclipse.core.debug.CeylonJDIDebugTarget.EvaluationRunner;

class CeylonJDIModelPresentation extends JDIModelPresentation {
    private static final String ceylonStringTypeName = ceylon.language.String.class.getName();
    private static final String ceylonStringValueFieldName = "value";

    @Override
    public String getValueText(IJavaValue value) throws DebugException {
        if (!CeylonPresentationContext.isCeylonContext(value)) {
            return super.getValueText(value);
        }

        String refTypeName= value.getReferenceTypeName();
        String valueString= value.getValueString();
        boolean isString= refTypeName.equals(fgStringName);
        if (isString) {
            return super.getValueText(value);
        }
        
        if (refTypeName.equals(ceylonStringTypeName)) {
            isString = true;
            IJavaFieldVariable javaStringValueField = ((IJavaObject)value).getField(ceylonStringValueFieldName, false);
            if (javaStringValueField != null) {
                IValue javaStringValue = javaStringValueField.getValue();
                if (javaStringValue != null) {
                    valueString = javaStringValue.getValueString();
                }
            }
        }
        
        IJavaType type= value.getJavaType();
        String signature= null;
        if (type != null) {
            signature= type.getSignature();
        }

        if (!isObjectValue(signature)) {
            return super.getValueText(value);
        }
        
        boolean isArray= value instanceof IJavaArray;
        StringBuffer buffer= new StringBuffer();
        if (!isString && (refTypeName.length() > 0)) {
            // Don't show type name for instances and references
            if (!(value instanceof JDIReferenceListValue || value instanceof JDIAllInstancesValue)){
                String qualTypeName= getCeylonReifiedTypeName(value);
                if (qualTypeName == null) {
                    qualTypeName = getQualifiedName(refTypeName);
                }
                if (isArray) {
                    qualTypeName= adjustTypeNameForArrayIndex(qualTypeName, ((IJavaArray)value).getLength());
                }
                buffer.append(qualTypeName);
                buffer.append(' ');
            }
        }
        
        // Put double quotes around Strings
        if (valueString != null && (isString || valueString.length() > 0)) {
            if (isString) {
                buffer.append('"');
            }
            buffer.append(DefaultLabelProvider.escapeSpecialChars(valueString));
            if (isString) {
                buffer.append('"');
                if(value instanceof IJavaObject){
                    buffer.append(" "); //$NON-NLS-1$
                    buffer.append(NLS.bind(DebugUIMessages.JDIModelPresentation_118, new String[]{String.valueOf(((IJavaObject)value).getUniqueId())})); 
                }
            }
            
        }
        return buffer.toString().trim();
    }

    public String getCeylonReifiedTypeName(IValue value) throws DebugException {
        if (value instanceof JDIObjectValue) {
            final IJavaValue javaValue = (IJavaValue) value;
            final JDIDebugTarget debugTarget = ((JDIObjectValue) value).getJavaDebugTarget();
            if (debugTarget instanceof CeylonJDIDebugTarget) {
                IJavaValue reifiedTypeNameValue = ((CeylonJDIDebugTarget) debugTarget).getEvaluationResult(new EvaluationRunner() {
                    @Override
                    public void run(IJavaThread innerThread, IProgressMonitor monitor,
                            EvaluationListener listener) throws DebugException {
                        IJavaType[] types = ((JDIObjectValue) javaValue).getJavaDebugTarget().getJavaTypes(Metamodel.class.getName());
                        if (types != null && types.length > 0) {
                            JDIClassType metamodelType = (JDIClassType) types[0];
                            IJavaValue typeDescriptor = metamodelType.sendMessage("getTypeDescriptor", "(Ljava/lang/Object;)Lcom/redhat/ceylon/compiler/java/runtime/model/TypeDescriptor;", new IJavaValue[] {javaValue}, innerThread);
                            if (typeDescriptor instanceof IJavaObject && ! (typeDescriptor instanceof JDINullValue)) {
                                IJavaValue producedType = metamodelType.sendMessage("getProducedType", "(Lcom/redhat/ceylon/compiler/java/runtime/model/TypeDescriptor;)Lcom/redhat/ceylon/compiler/typechecker/model/ProducedType;", new IJavaValue[] {typeDescriptor}, innerThread);
                                if (producedType instanceof IJavaObject && ! (producedType instanceof JDINullValue)) {
                                    IJavaValue producedTypeName = ((IJavaObject) producedType).sendMessage("getProducedTypeName", "()Ljava/lang/String;", new IJavaValue[] {}, innerThread, "Lcom/redhat/ceylon/compiler/typechecker/model/ProducedType;");
                                    listener.finished(producedTypeName);
                                    return;
                                }
                            }
                        }
                        listener.finished(null);
                    }
                }, 5000);
                if (reifiedTypeNameValue instanceof JDIObjectValue  && !(reifiedTypeNameValue instanceof JDINullValue)) {
                    String reifiedTypeName;
                    reifiedTypeName = reifiedTypeNameValue.getValueString();
                    return reifiedTypeName;
                }
            }
        }
        return getQualifiedName(value.getReferenceTypeName());
    }
}
