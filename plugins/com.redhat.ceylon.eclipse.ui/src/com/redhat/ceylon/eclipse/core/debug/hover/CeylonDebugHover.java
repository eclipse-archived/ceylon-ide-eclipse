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
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.logicalstructures.JDIPlaceholderVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIClassType;
import org.eclipse.jdt.internal.debug.core.model.JDILocalVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThisVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIVariable;
import org.eclipse.jdt.internal.debug.ui.ExpressionInformationControlCreator;
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
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SafeMemberOp;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
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
        if (object instanceof IVariable) {  
            IVariable var = (IVariable) object;
            return getVariableText(var);
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
    
    private static IJavaVariable findCeylonFieldVariable(IJavaVariable object, String variableName) {
        try {
            return findCeylonField((IJavaValue)object.getValue(), variableName);
        } catch (DebugException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static IJavaVariable findCeylonField(IJavaValue object, String variableName) {
        IVariable[] thisChildren;
        try {
            thisChildren = object.getVariables();
            for (IVariable element : thisChildren) {
                IJavaVariable var = (IJavaVariable) element;
                if (variableName.equals(fixVariableName(var.getName(), 
                                        var instanceof JDILocalVariable,
                                        var.isSynthetic()))) {
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
    private static IJavaVariable findCeylonVariable(IJavaStackFrame frame, String variableName) {
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
                    if (variableName.equals(fixVariableName(var.getName(), 
                                                var instanceof JDILocalVariable,
                                                var.isSynthetic()))) {
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
                    return findCeylonFieldVariable(thisVariable, variableName);
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

    
    private static IJavaVariable findClassFieldOrAttribute(Declaration searchedDeclaration,
                                            ClassOrInterface searchedClassDeclaration,
                                            IJavaObject jdiObject,
                                            ClassOrInterface currentClassDeclaration) {
        if (jdiObject == null ||
                currentClassDeclaration == null) {
            return null;
        }
        
        if (searchedClassDeclaration.equals(currentClassDeclaration)) {
            do {
                Declaration objectDeclaration = DebugUtils.getCeylonDeclaration(jdiObject);
                if (objectDeclaration instanceof ClassOrInterface
                        && ((ClassOrInterface)objectDeclaration).inherits(searchedClassDeclaration)) {
                    searchedDeclaration = objectDeclaration.getMember(searchedDeclaration.getName(), Collections.<ProducedType>emptyList(), false);
                    break;
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

            final IJavaObject currentClassScope = jdiObject;
            if (searchedDeclaration instanceof Value) {
                if (((Value)searchedDeclaration).isTransient()) {
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
                                    return new JDIPlaceholderVariable(searchedDeclaration.getName(), result);
                                }
                            }
                        } catch (DebugException e) {
                            e.printStackTrace();
                        }
                        
                    }
                    return null;
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
                return jdiVariableForNode(debugTarget, frame, node);
            } catch (DebugException e) {
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
                
                IJavaVariable primaryVariable = jdiVariableForNode(debugTarget, frame, primary);
                if (primaryVariable == null) {
                    return null;
                }
                
                IJavaValue primaryValue = (IJavaValue) primaryVariable.getValue();
                if (primaryValue instanceof IJavaObject) {
                    IJavaObject primaryObject = (IJavaObject) primaryValue;
                    Declaration primaryObjectClassDeclaration = DebugUtils.getCeylonDeclaration(primaryObject);
                    if (primaryObjectClassDeclaration instanceof ClassOrInterface) {
                        return unBoxIfVariableBoxed(
                                findClassFieldOrAttribute(
                                        qualifiedMemberDeclaration, 
                                        (ClassOrInterface) container, 
                                        primaryObject, 
                                        (ClassOrInterface) container),
                                        primaryVariable.getName() + memberOperator.getText());
                    }

                }
            }
        }
        return null;
    }
    
    private static IJavaVariable jdiVariableForSimpleIdentifierNode(CeylonJDIDebugTarget debugTarget, JDIStackFrame frame, Node node) throws DebugException {
        Referenceable referenceable = Nodes.getReferencedDeclaration(node);
        if (referenceable instanceof Declaration) {
            Declaration declaration = (Declaration) referenceable;
            Declaration container = JavaSearch.getContainingDeclaration(declaration);
            Declaration frameMethodDeclaration = DebugUtils.getCeylonDeclaration(frame);
            // redescendre en utilisant le field this$x (si on est anonyme) de la classe de la méthode
            // => Faire une fonction qui retourne dans le scope du dessous (classe, OK, ... ou méthode et là on utilise les val$... qui avac un peu de chance sont capturés explicitement par le compilateur Ceylon ???)
            if (container != null
                    && frameMethodDeclaration != null) {
                String variableName = declaration.getName();
                if (container.equals(frameMethodDeclaration)) {
                    return unBoxIfVariableBoxed(findCeylonVariable(frame, variableName));
                } else if (container instanceof MethodOrValue && ! frame.isStatic()) {
                    return unBoxIfVariableBoxed(findCeylonCapturedVariable(frame, variableName));
                } else if (container instanceof ClassOrInterface){
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
                                    && varName.startsWith("val$")
                                    && variableName.equals(fixVariableName(varName.substring(4), 
                                            false, true))) {
                                return var;
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

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHoverExtension2#getInformationPresenterControlCreator()
     */
    public IInformationControlCreator getInformationPresenterControlCreator() {
        return new ExpressionInformationControlCreator();
    }
}
