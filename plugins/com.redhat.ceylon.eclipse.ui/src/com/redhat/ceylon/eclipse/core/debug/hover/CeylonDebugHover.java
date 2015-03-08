package com.redhat.ceylon.eclipse.core.debug.hover;

import static com.redhat.ceylon.eclipse.core.debug.presentation.CeylonContentProviderFilter.unBoxIfVariableBoxed;
import static com.redhat.ceylon.eclipse.core.debug.presentation.CeylonJDIModelPresentation.fixVariableName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.logicalstructures.JDIPlaceholderVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIClassType;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDILocalVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThisVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIVariable;
import org.eclipse.jdt.internal.debug.ui.IJDIPreferencesConstants;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

import com.redhat.ceylon.compiler.java.codegen.Naming;
import com.redhat.ceylon.compiler.java.codegen.Naming.Suffix;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SafeMemberOp;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.hover.DocumentationHover;
import com.redhat.ceylon.eclipse.code.hover.SourceInfoHover;
import com.redhat.ceylon.eclipse.core.debug.DebugUtils;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget.EvaluationListener;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget.EvaluationRunner;
import com.redhat.ceylon.eclipse.core.debug.presentation.CeylonJDIModelPresentation;
import com.redhat.ceylon.eclipse.util.JavaSearch;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.sun.jdi.ClassType;
import com.sun.jdi.Method;


public class CeylonDebugHover extends SourceInfoHover {
    
    public CeylonDebugHover(CeylonEditor editor) {
        super(editor);
    }

    public boolean isEnabled() {
        return DebugUtils.getFrame() != null;
    }
    
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        Object object = getHoverInfo2(textViewer, hoverRegion);
        if (object instanceof Object[]) {
            Object[] inputs = (Object[]) object;
            if (inputs[0] instanceof IVariable) {  
                IVariable var = (IVariable) inputs[0];
                return getVariableText(var);
            }
        }
        return null;
    }

/*
    private IVariable resolveCeylonVariable(IJavaStackFrame frame, ITextViewer textViewer, IRegion hoverRegion) {
        if (frame != null) {
            try {
                IDocument document= textViewer.getDocument();
                if (document != null) {
                    String variableName= document.get(hoverRegion.getOffset(), hoverRegion.getLength());
                    return findCeylonVariable(frame, variableName);
                }
            } catch (BadLocationException x) {
            }
        }
        return null;
    }
*/
    
    private static Pattern enclosingObjectPattern = Pattern.compile("^this\\$([0-9]+)$");
    public static IJavaObject getEnclosingObject(IJavaObject object) {
        TreeMap<Integer, IJavaObject> enclosingScopes = new TreeMap<Integer, IJavaObject>();
        if (object == null) {
            return null;
        }
        try {
            for (IVariable var : object.getVariables()) {
                if (var instanceof IJavaFieldVariable
                        && var.getName().startsWith("this$")) {
                    Matcher matcher = enclosingObjectPattern.matcher(var.getName());
                    if (matcher.matches()) {
                        IValue value = var.getValue();
                        if (value instanceof IJavaObject) {
                            String numberString = matcher.group(1);
                            try {
                                Integer number = Integer.parseInt(numberString);
                                enclosingScopes.put(number, (IJavaObject)value);
                            } catch(NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    
                }
            }
        } catch (DebugException e) {
            e.printStackTrace();
        }
        
        if (! enclosingScopes.isEmpty()) {
            return enclosingScopes.lastEntry().getValue();
        }
        return null;
    }
    
    private static IJavaVariable findCeylonFieldVariable(IJavaVariable object, String variableName, boolean useFixedName) {
        try {
            return findCeylonField((IJavaValue)object.getValue(), variableName, useFixedName);
        } catch (DebugException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static IJavaVariable findCeylonField(IJavaValue object, String variableName) {
        return findCeylonField(object, variableName, true);
    }
    
    private static IJavaVariable findCeylonField(IJavaValue object, String variableName, boolean useFixedName) {
        IVariable[] thisChildren;
        try {
            thisChildren = object.getVariables();
            for (IVariable element : thisChildren) {
                IJavaVariable var = (IJavaVariable) element;
                String searchedName = useFixedName ? fixVariableName(var.getName(), 
                        var instanceof JDILocalVariable,
                        var.isSynthetic()) : var.getName();
                if (variableName.equals(searchedName)) {
                    return var;
                }
            }
        } catch (DebugException e) {
            if (e.getStatus().getCode() != IJavaThread.ERR_THREAD_NOT_SUSPENDED) {
                JDIDebugUIPlugin.log(e);
            }
        }
        return null;
    }

    /**
     * Returns a local variable in the given frame based on the the given name
     * or <code>null</code> if none.
     * 
     * @return local variable or <code>null</code>
     */
    private static IJavaVariable findCeylonVariable(IJavaStackFrame frame, String variableName, boolean useFixedName) {
        if (frame != null && variableName != null) {
            try {
                if (frame.isNative()) {
                    return null;
                }
                IVariable[] variables = frame.getVariables();
                List<IJavaVariable> possibleMatches = new ArrayList<IJavaVariable>();
                IJavaVariable thisVariable = null;
                for (IVariable variable : variables) {
                    IJavaVariable var = (IJavaVariable) variable;
                    String searchedName = useFixedName ? fixVariableName(var.getName(), 
                            var instanceof JDILocalVariable,
                            var.isSynthetic()) : var.getName();
                    if (variableName.equals(searchedName)) {
                        possibleMatches.add(var);
                    }
                    if (var instanceof JDIThisVariable) {
                        // save for later - check for instance and static variables
                        thisVariable = var;
                    }
                }
                for(IJavaVariable variable: possibleMatches){
                    // Local Variable has more preference than Field Variable
                    if(variable instanceof JDILocalVariable){
                        return variable;
                    }
                }
                if(possibleMatches.size() > 0) {
                    return possibleMatches.get(0);
                }

                if (thisVariable != null) {
                    return findCeylonFieldVariable(thisVariable, variableName, useFixedName);
                }
                return null;
            } catch (DebugException x) {
                if (x.getStatus().getCode() != IJavaThread.ERR_THREAD_NOT_SUSPENDED) {
                    JDIDebugUIPlugin.log(x);
                }
            }
        }
        return null;
    }   

    private static IJavaVariable findCeylonVariable(IJavaStackFrame frame, String variableName) {
        return findCeylonVariable(frame, variableName, true);
    }   
    
    /**
     * Returns HTML text for the given variable
     */
    private static String getVariableText(IVariable variable) {
        StringBuffer buffer= new StringBuffer();
        CeylonJDIModelPresentation modelPresentation = getModelPresentation();
        buffer.append("<p><pre>"); //$NON-NLS-1$
        String variableText= modelPresentation.getVariableText((IJavaVariable) variable);
        buffer.append(replaceHTMLChars(variableText));
        buffer.append("</pre></p>"); //$NON-NLS-1$
        modelPresentation.dispose();
        if (buffer.length() > 0) {
            return buffer.toString();
        }
        return null;
    }
    
    /**
     * Replaces reserved HTML characters in the given string with
     * their escaped equivalents. This is to ensure that variable
     * values containing reserved characters are correctly displayed.
     */
    private static String replaceHTMLChars(String variableText) {
        StringBuffer buffer= new StringBuffer(variableText.length());
        char[] characters = variableText.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            char character= characters[i];
            switch (character) {
                case '<':
                    buffer.append("&lt;"); //$NON-NLS-1$
                    break;
                case '>':
                    buffer.append("&gt;"); //$NON-NLS-1$
                    break;
                case '&':
                    buffer.append("&amp;"); //$NON-NLS-1$
                    break;
                case '"':
                    buffer.append("&quot;"); //$NON-NLS-1$
                    break;
                default:
                    buffer.append(character);
            }
        }
        return buffer.toString();
    }

    /**
     * Returns a configured model presentation for use displaying variables.
     */
    private static CeylonJDIModelPresentation getModelPresentation() {
        CeylonJDIModelPresentation presentation = new CeylonJDIModelPresentation();
        
        String[][] booleanPrefs= {
                {IJDIPreferencesConstants.PREF_SHOW_QUALIFIED_NAMES, JDIModelPresentation.DISPLAY_QUALIFIED_NAMES}};
        String viewId= IDebugUIConstants.ID_VARIABLE_VIEW;
        for (int i = 0; i < booleanPrefs.length; i++) {
            boolean preferenceValue = getBooleanPreferenceValue(viewId, booleanPrefs[i][0]);
            presentation.setAttribute(booleanPrefs[i][1], (preferenceValue ? Boolean.TRUE : Boolean.FALSE));
        }
        return presentation;
    }
    
       /**
     * Returns the value of this filters preference (on/off) for the given
     * view.
     * 
     * @param part
     * @return boolean
     */
    public static boolean getBooleanPreferenceValue(String id, String preference) {
        String compositeKey = id + "." + preference; //$NON-NLS-1$
        IPreferenceStore store = JDIDebugUIPlugin.getDefault().getPreferenceStore();
        boolean value = false;
        if (store.contains(compositeKey)) {
            value = store.getBoolean(compositeKey);
        } else {
            value = store.getBoolean(preference);
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
     */
    public IInformationControlCreator getHoverControlCreator() {
        return new ExpressionInformationControlCreator();
    }

    // copied from the compiler project since it is not visible
    private static String getErasedGetterName(Declaration decl) {
        String property = decl.getName();
        // ERASURE
        if (!(decl instanceof Value) || ((Value)decl).isShared()) {
            if ("hash".equals(property)) {
                return "hashCode";
            } else if ("string".equals(property)) {
                return "toString";
            }
        }
        
        @SuppressWarnings("deprecation")
        String getterName = Naming.getGetterName(property);
        if (decl.isMember() && !decl.isShared()) {
            getterName = Naming.suffixName(Suffix.$priv$, getterName);
        }
        return getterName;
    }

    
    public static IJavaVariable findClassFieldOrAttribute(Declaration searchedDeclaration,
                                            ClassOrInterface searchedClassDeclaration,
                                            IJavaObject jdiObject,
                                            ClassOrInterface currentClassDeclaration) {
        if (jdiObject == null ||
                currentClassDeclaration == null) {
            return null;
        }
        
        if (searchedClassDeclaration.equals(currentClassDeclaration)) {
            do {
                Declaration objectDeclaration = DebugUtils.getDeclaration(jdiObject);
                if (objectDeclaration instanceof ClassOrInterface) {
                    if (objectDeclaration.equals(searchedClassDeclaration)) {
                        break;
                    } else if (((ClassOrInterface)objectDeclaration).inherits(searchedClassDeclaration)) {
                        if (!(searchedDeclaration instanceof TypeParameter)) {
                            searchedDeclaration = objectDeclaration.getMember(searchedDeclaration.getName(), Collections.<ProducedType>emptyList(), false);
                        }
                        break;
                    }
                }
                IJavaObject enclosingJdiObject = getEnclosingObject(jdiObject);
                if (enclosingJdiObject == jdiObject) {
                    jdiObject = null;
                } else {
                    jdiObject = enclosingJdiObject;
                }
            } while (jdiObject != null);
            
            if (jdiObject == null) {
                return null;
            }
            
            if (searchedDeclaration == null) {
                return null;
            }

            if (searchedDeclaration instanceof TypeParameter) {
                IVariable[] thisChildren;
                String searchedName = Naming.Prefix.$reified$ + searchedDeclaration.getName();
                JDIDebugTarget debugTarget = DebugUtils.getDebugTarget();
                if (debugTarget != null) {
                    try {
                        thisChildren = jdiObject.getVariables();
                        for (IVariable field : thisChildren) {
                            if (searchedName.equals(field.getName())) {
                                 return (IJavaVariable) field;
                            }
                        }
                    } catch (DebugException e) {
                        if (e.getStatus().getCode() != IJavaThread.ERR_THREAD_NOT_SUSPENDED) {
                            JDIDebugUIPlugin.log(e);
                        }
                    }
                }
            } else {
                
            }

            final IJavaObject currentClassScope = jdiObject;
            if (searchedDeclaration instanceof Value) {
                // values with getters
                CeylonJDIDebugTarget debugTarget = DebugUtils.getDebugTarget();
                if (debugTarget != null) {
                    final String getterName = getErasedGetterName(searchedDeclaration);
                    try {
                        ClassType classJDIType = (ClassType) ((JDIClassType)currentClassScope.getJavaType()).getUnderlyingType();
                        for (Method m : classJDIType.allMethods()) {
                            if (m.name().equals(getterName)) {
                                final String signature = m.signature();
                                IJavaValue result = debugTarget.getEvaluationResult(new EvaluationRunner() {
                                    @Override
                                    public void run(IJavaThread innerThread, IProgressMonitor monitor,
                                            EvaluationListener listener) throws DebugException {
                                        listener.finished(
                                                currentClassScope.sendMessage(
                                                        getterName, 
                                                        signature,
                                                        new IJavaValue[0], innerThread, false));
                                    }
                                }, 5000);
                                if (result != null) {
                                    return new JDIPlaceholderVariable(searchedDeclaration.getName(), result);
                                }
                            }
                        }
                    } catch (DebugException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Simple fields
            return findCeylonField(currentClassScope, searchedDeclaration.getName());
        } else {
            ClassOrInterface containerClassDeclaration = getContainingClassOrInterface(currentClassDeclaration);
            return findClassFieldOrAttribute(searchedDeclaration, searchedClassDeclaration, jdiObject, (ClassOrInterface) containerClassDeclaration);
        }
    }

    public static ClassOrInterface getContainingClassOrInterface(
            Declaration declaration) {
        Declaration containerClassDeclaration = null;
        do {
            declaration = JavaSearch.getContainingDeclaration(declaration);
            if (declaration instanceof ClassOrInterface) {
                containerClassDeclaration = declaration;
                break;
            }
        } while(declaration != null);
        return (ClassOrInterface) containerClassDeclaration;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
     */
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
        JDIStackFrame frame = DebugUtils.getFrame();
        if (frame != null
                && frame.getDebugTarget() instanceof CeylonJDIDebugTarget) {
            try {
                final CeylonJDIDebugTarget debugTarget = (CeylonJDIDebugTarget) frame.getDebugTarget();
                // first check for 'this' - code resolve does not resolve java elements for 'this'
                IDocument document= textViewer.getDocument();
                if (document != null) {
                    try {
                        String variableName= document.get(hoverRegion.getOffset(), hoverRegion.getLength());
                        if (variableName.equals("this")) { //$NON-NLS-1$
                            IJavaVariable variable = frame.findVariable(variableName);
                            if (variable != null) {
                                return variable;
                            }
                        }
                    } catch (BadLocationException e) {
                        return null;
                    }
                }
                Node node = null;
                if (editor != null) {
                    node = getHoverNode(hoverRegion, editor.getParseController());
                }
                IJavaVariable var = 
                        jdiVariableForNode(debugTarget, frame, node);
                if (var!=null) {
                    return new Object[] { var,
                            new DocumentationHover(editor)
                                    .getHoverInfo(textViewer, hoverRegion) };
                }
            }
            catch (DebugException e) {
                return null;
            }
        }
        return null;
    }

    private static IJavaVariable jdiVariableForNode(CeylonJDIDebugTarget debugTarget, JDIStackFrame frame, Node node) throws DebugException {
        if (node instanceof Tree.QualifiedMemberExpression) {
            return jdiVariableForQualifierMemberExpression(debugTarget, frame, (Tree.QualifiedMemberExpression)node);
        } 
        if (node != null) {
            return jdiVariableForSimpleIdentifierNode(debugTarget, frame, node);
        }
        return null;
    }
    
    private static IJavaVariable jdiVariableForQualifierMemberExpression(CeylonJDIDebugTarget debugTarget, JDIStackFrame frame, Tree.QualifiedMemberExpression node) throws DebugException {
        Tree.QualifiedMemberExpression qualifiedMember = (Tree.QualifiedMemberExpression) node;
        Tree.MemberOperator memberOperator = qualifiedMember.getMemberOperator();
        if (memberOperator instanceof MemberOp || 
                memberOperator instanceof SafeMemberOp ) {
            Declaration qualifiedMemberDeclaration = qualifiedMember.getDeclaration();
            Declaration container = JavaSearch.getContainingDeclaration(qualifiedMemberDeclaration);
            if (container instanceof ClassOrInterface) {
                Tree.Primary primary = qualifiedMember.getPrimary();
                
                String prefix = "";
                IJavaValue primaryValue = null;
                if (primary instanceof Tree.Literal) {
                    String literalText = primary.getText();
                    prefix = literalText;
                    if (primary instanceof Tree.NaturalLiteral) {
                        primaryValue = debugTarget.newValue(Long.parseLong(literalText));
                    } else if (primary instanceof Tree.CharLiteral) {
                        primaryValue = debugTarget.newValue(literalText.charAt(0));
                    } else if (primary instanceof Tree.FloatLiteral) {
                        primaryValue = debugTarget.newValue(Float.parseFloat(literalText));
                    } else if (primary instanceof Tree.StringLiteral) {
                        primaryValue = debugTarget.newValue(literalText);
                    }
                } else {
                    IJavaVariable primaryVariable = jdiVariableForNode(debugTarget, frame, primary);
                    if (primaryVariable == null) {
                        return null;
                    }
                    primaryValue = (IJavaValue) primaryVariable.getValue();
                    prefix = primaryVariable.getName();
                }
                
                IJavaObject primaryJdiObject = makeConsistentWithModel(debugTarget, primaryValue, primary.getTypeModel());
                if (primaryJdiObject != null) {
                    return unBoxIfVariableBoxed(
                            findClassFieldOrAttribute(
                                    qualifiedMemberDeclaration, 
                                    (ClassOrInterface) container, 
                                    primaryJdiObject, 
                                    (ClassOrInterface) container),
                                    prefix + memberOperator.getText());
                }
            }
        }
        return null;
    }
    
    private static IJavaObject createCeylonObject(
            CeylonJDIDebugTarget debugTarget,
            String typeName, 
            final String constructorSignature, 
            final IJavaValue primitiveValue) throws DebugException {
        IJavaType[] types = debugTarget.getJavaTypes(typeName);
        if (types.length > 0 
                && types[0] instanceof IJavaClassType) {
            final IJavaClassType type = (IJavaClassType) types[0];
            if (type != null) {
                return (IJavaObject) debugTarget.getEvaluationResult(new EvaluationRunner() {
                    
                    @Override
                    public void run(IJavaThread innerThread, IProgressMonitor monitor,
                            EvaluationListener listener) throws DebugException {
                        listener.finished(type.newInstance(constructorSignature, new IJavaValue[] {primitiveValue}, innerThread));
                    }
                }, 5000);
            }
        }
        return null;
    }
    
    private static IJavaObject makeConsistentWithModel(CeylonJDIDebugTarget debugTarget, IJavaValue primaryJdiValue, ProducedType modelProducedType) throws DebugException {
        IJavaType jdiType = primaryJdiValue.getJavaType();

        IJavaObject primaryJdiObject = null;
        
        String jdiTypeQualifiedName = jdiType.getName();
        switch (jdiTypeQualifiedName) {
        case "java.lang.String":
            if ("ceylon.language::String".equals(modelProducedType.getProducedTypeQualifiedName())) {
                primaryJdiObject = createCeylonObject(debugTarget, "ceylon.language.String", "(Ljava/lang/String;)V", primaryJdiValue);
            }
            break;
        case "long":
            primaryJdiObject = createCeylonObject(debugTarget, "ceylon.language.Integer", "(J)V", primaryJdiValue);
            break;
        case "byte":
            primaryJdiObject = createCeylonObject(debugTarget, "ceylon.language.Byte", "(B)V", primaryJdiValue);
            break;
        case "char":
            primaryJdiObject = createCeylonObject(debugTarget, "ceylon.language.Character", "(C)V", primaryJdiValue);
            break;
        case "double":
            primaryJdiObject = createCeylonObject(debugTarget, "ceylon.language.Float", "(D)V", primaryJdiValue);
            break;
        case "boolean":
            primaryJdiObject = createCeylonObject(debugTarget, "ceylon.language.Boolean", "(Z)V", primaryJdiValue);
            break;
        }

        if (primaryJdiObject == null) {
            if (primaryJdiValue instanceof IJavaObject) {
                primaryJdiObject = (IJavaObject) primaryJdiValue;
            } else {
                return null;
            }
        }
        
        Declaration primaryObjectClassDeclaration = DebugUtils.getModelDeclaration(primaryJdiObject);
        if (! (primaryObjectClassDeclaration instanceof ClassOrInterface)) {
            return null;
        }
        
        return primaryJdiObject;
    }

    public static IJavaVariable jdiVariableForTypeParameter(JDIDebugTarget debugTarget, JDIStackFrame frame, TypeParameter typeParameter) throws DebugException {
        Declaration container = JavaSearch.getContainingDeclaration(typeParameter);
        Declaration frameMethodDeclaration = DebugUtils.getSourceDeclaration(frame);
        if (container != null
                && frameMethodDeclaration != null) {
            String variableName = Naming.Prefix.$reified$ + typeParameter.getName();
            if (container.equals(frameMethodDeclaration)) {
                return findCeylonVariable(frame, variableName, false);
            } else if (container instanceof MethodOrValue && ! frame.isStatic()) {
                return findCeylonCapturedVariable(frame, variableName, false);
            } else if (container instanceof ClassOrInterface){
                if (!frame.isStatic()) {
                    IJavaObject thisObject = frame.getThis();
                    return unBoxIfVariableBoxed(findClassFieldOrAttribute(typeParameter, (ClassOrInterface) container, thisObject, getContainingClassOrInterface(frameMethodDeclaration)));
                    
                }
                // case of attributes or getters of the class or an outer class
            }
        }
        return null;
    }
    
    private static IJavaVariable jdiVariableForSimpleIdentifierNode(CeylonJDIDebugTarget debugTarget, JDIStackFrame frame, Node node) throws DebugException {
        Referenceable referenceable = Nodes.getReferencedDeclaration(node);
        if (referenceable instanceof Declaration) {
            Declaration declaration = (Declaration) referenceable;
            Declaration container = JavaSearch.getContainingDeclaration(declaration);
            Declaration frameMethodDeclaration = DebugUtils.getSourceDeclaration(frame);
            // redescendre en utilisant le field this$x (si on est anonyme) de la classe de la méthode
            // => Faire une fonction qui retourne dans le scope du dessous (classe, OK, ... ou méthode et là on utilise les val$... qui avac un peu de chance sont capturés explicitement par le compilateur Ceylon ???)
            if (container != null
                    && frameMethodDeclaration != null) {
                String variableName = declaration.getName();
                if (container.equals(frameMethodDeclaration)) {
                    return unBoxIfVariableBoxed(findCeylonVariable(frame, variableName));
                } else if (container instanceof MethodOrValue && ! frame.isStatic()) {
                    return unBoxIfVariableBoxed(findCeylonCapturedVariable(frame, variableName));
                } else if (container instanceof ClassOrInterface && ! (declaration instanceof TypeParameter)){
                    if (!frame.isStatic()) {
                        IJavaObject thisObject = frame.getThis();
                        return unBoxIfVariableBoxed(findClassFieldOrAttribute(declaration, (ClassOrInterface) container, thisObject, getContainingClassOrInterface(frameMethodDeclaration)));
                        
                    }
                    // case of attributes or getters of the class or an outer class
                }
            } else {
                if (declaration instanceof Value 
                        && ((Value)declaration).isToplevel()) {
                    IJavaType[] types;
                    types = frame.getJavaDebugTarget().getJavaTypes(declaration.getQualifiedNameString()
                                                      .replace("::", ".") + "_");
                    if (types != null 
                            && types.length > 0
                            && (types[0] instanceof JDIClassType)) {
                        final JDIClassType classType = (JDIClassType) types[0];
                        final String typeSignature = classType.getField("value").getSignature();
                        final String methodSignature = "()" + typeSignature;
                        final IJavaObject[] arguments = new IJavaObject[0];
                        IJavaValue result = debugTarget.getEvaluationResult(new EvaluationRunner() {
                            @Override
                            public void run(IJavaThread innerThread, IProgressMonitor monitor,
                                    EvaluationListener listener) throws DebugException {
                                IJavaValue result = classType.sendMessage("get_", methodSignature, arguments, innerThread);
                                listener.finished(result);
                            }
                        }, 5000);
                        if (result != null) {
                            return new JDIPlaceholderVariable(declaration.getName(), result);
                        }
                    }
                    return null;
                }
            }
        }
        return null;
    }
    
    private static IJavaVariable findCeylonCapturedVariable(IJavaStackFrame frame,
            String variableName) {
        return findCeylonCapturedVariable(frame, variableName, true);
    }

    private static IJavaVariable findCeylonCapturedVariable(IJavaStackFrame frame,
            String variableName, boolean useFixedName) {
        if (frame != null && variableName != null) {
            try {
                if (frame.isNative()) {
                    return null;
                }
                if (frame.isStatic()) {
                    return null;
                }
                IJavaObject thisObject = frame.getThis();
                if (thisObject == null) {
                    return null;
                }
                IVariable[] variables = frame.getThis().getVariables();
                for (IVariable variable : variables) {
                    if (variable instanceof JDIVariable) {
                        JDIVariable var = (JDIVariable) variable;
                        if (var.isSynthetic()) {
                            String varName = var.getName();
                            if (varName != null
                                    && varName.startsWith("val$")) {
                                String searchedName = useFixedName ? fixVariableName(varName.substring(4), 
                                        false, true) : varName.substring(4);
                                if (variableName.equals(searchedName)) {
                                    return var;
                                }
                            }
                        }
                    }
                }
            } catch (DebugException x) {
                if (x.getStatus().getCode() != IJavaThread.ERR_THREAD_NOT_SUSPENDED) {
                    JDIDebugUIPlugin.log(x);
                }
            }
        }
        return null;
    }

}
