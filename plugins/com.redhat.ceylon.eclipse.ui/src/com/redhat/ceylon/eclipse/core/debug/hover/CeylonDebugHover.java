package com.redhat.ceylon.eclipse.core.debug.hover;

import static com.redhat.ceylon.eclipse.core.debug.presentation.CeylonJDIModelPresentation.fixVariableName;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
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

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.hover.SourceInfoHover;
import com.redhat.ceylon.eclipse.core.debug.DebugUtils;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget.EvaluationListener;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget.EvaluationRunner;
import com.redhat.ceylon.eclipse.core.debug.presentation.CeylonJDIModelPresentation;
import com.redhat.ceylon.eclipse.util.JavaSearch;
import com.redhat.ceylon.eclipse.util.Nodes;


public class CeylonDebugHover extends SourceInfoHover {
    
    
    
    public CeylonDebugHover(CeylonEditor editor) {
        super(editor);
    }

    /**
     * Returns the stack frame in which to search for variables, or <code>null</code>
     * if none.
     * 
     * @return the stack frame in which to search for variables, or <code>null</code>
     * if none
     */
    protected JDIStackFrame getFrame() {
        IAdaptable adaptable = DebugUITools.getDebugContext();
        if (adaptable != null) {
            IJavaStackFrame stackFrame = (IJavaStackFrame)adaptable.getAdapter(IJavaStackFrame.class);
            if (stackFrame instanceof JDIStackFrame) {
                return (JDIStackFrame) stackFrame;
            }
        }
        return null;
    }
        
    public boolean isEnabled() {
        return getFrame() != null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
     */
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        Object object = getHoverInfo2(textViewer, hoverRegion);
        if (object instanceof IVariable) {  
            IVariable var = (IVariable) object;
            return getVariableText(var);
        }
        return null;
    }
    
    /**
     * Returns a local variable in the given frame based on the hover region
     * or <code>null</code> if none.
     * 
     * @return local variable or <code>null</code>
     */
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

    
    private IVariable findCeylonField(IJavaVariable object, String variableName) {
        IVariable[] thisChildren;
        try {
            thisChildren = object.getValue().getVariables();
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
    private IVariable findCeylonVariable(IJavaStackFrame frame, String variableName) {
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
                    return findCeylonField(thisVariable, variableName);
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

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
     */
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
        JDIStackFrame frame = getFrame();
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
                if (node != null) {
                    Referenceable referenceable = Nodes.getReferencedDeclaration(node);
                    if (referenceable instanceof Declaration) {
                        Declaration declaration = (Declaration) referenceable;
                        Declaration container = JavaSearch.getContainingDeclaration(declaration);
                        Declaration frameMethodDeclaration = DebugUtils.getStackFrameCeylonDeclaration(frame);
                        // redescendre en utilisant le field this$x (si on est anonyme) de la classe de la méthode
                        // => Faire une fonction qui retourne dans le scope du dessous (classe, OK, ... ou méthode et là on utilise les val$... qui avac un peu de chance sont capturés explicitement par le compilateur Ceylon ???)
                        if (container != null
                                && frameMethodDeclaration != null) {
                            String variableName = declaration.getName();
                            if (container.equals(frameMethodDeclaration)) {
                                return findCeylonVariable(frame, variableName);
                            } else if (container instanceof MethodOrValue && ! frame.isStatic()) {
                                return findCeylonCapturedVariables(frame, variableName);
                            } else {
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
                }
            } catch (DebugException e) {
                return null;
            }
            
            return resolveCeylonVariable(frame, textViewer, getHoverRegion(textViewer, hoverRegion.getOffset()));
            
            
            /*
            try {
                if (javaElement instanceof IField) {
                    IField field = (IField)javaElement;
                    IJavaVariable variable = null;
                    IJavaDebugTarget debugTarget = (IJavaDebugTarget)frame.getDebugTarget();
                    if (Flags.isStatic(field.getFlags())) {
                        IJavaType[] javaTypes = debugTarget.getJavaTypes(field.getDeclaringType().getFullyQualifiedName());
                        if (javaTypes != null) {
                            for (int j = 0; j < javaTypes.length; j++) {
                                IJavaType type = javaTypes[j];
                                if (type instanceof IJavaReferenceType) {
                                    IJavaReferenceType referenceType = (IJavaReferenceType) type;
                                    variable = referenceType.getField(field.getElementName());
                                }
                                if (variable != null) {
                                    break;
                                }
                            }
                        }
                        if (variable == null) {
                            // the class is not loaded yet, but may be an in-lined primitive constant
                            Object constant = field.getConstant();
                            if (constant != null) {
                                IJavaValue value = null;
                                if (constant instanceof Integer) {
                                    value = debugTarget.newValue(((Integer)constant).intValue());
                                } else if (constant instanceof Byte) {
                                    value = debugTarget.newValue(((Byte)constant).byteValue());
                                } else if (constant instanceof Boolean) {
                                    value = debugTarget.newValue(((Boolean)constant).booleanValue());
                                } else if (constant instanceof Character) {
                                    value = debugTarget.newValue(((Character)constant).charValue());
                                } else if (constant instanceof Double) {
                                    value = debugTarget.newValue(((Double)constant).doubleValue());
                                } else if (constant instanceof Float) {
                                    value = debugTarget.newValue(((Float)constant).floatValue());
                                } else if (constant instanceof Long) {
                                    value = debugTarget.newValue(((Long)constant).longValue());
                                } else if (constant instanceof Short) {
                                    value = debugTarget.newValue(((Short)constant).shortValue());
                                } else if (constant instanceof String) {
                                    value = debugTarget.newValue((String)constant);
                                }
                                if (value != null) {
                                    variable = new JDIPlaceholderVariable(field.getElementName(), value);
                                }
                            }
                            if (variable == null) {
                                return null; // class not loaded yet and not a constant
                            }
                        }
                    } else {
                        if (!frame.isStatic()) {
                            // ensure that we only resolve a field access on 'this':
                            if (!(codeAssist instanceof ITypeRoot))
                                return null;
                            ITypeRoot typeRoot = (ITypeRoot) codeAssist;
                            ASTNode root= SharedASTProvider.getAST(typeRoot, SharedASTProvider.WAIT_NO, null);
                            if (root == null) {
                                ASTParser parser = ASTParser.newParser(AST.JLS4);
                                parser.setSource(typeRoot);
                                parser.setFocalPosition(hoverRegion.getOffset());
                                root = parser.createAST(null);
                            }
                            ASTNode node = NodeFinder.perform(root, hoverRegion.getOffset(), hoverRegion.getLength());
                            if (node == null)
                                return null;
                            StructuralPropertyDescriptor locationInParent = node.getLocationInParent();
                            if (locationInParent == FieldAccess.NAME_PROPERTY) {
                                FieldAccess fieldAccess = (FieldAccess) node.getParent();
                                if (!(fieldAccess.getExpression() instanceof ThisExpression)) {
                                    return null;
                                }
                            } else if (locationInParent == QualifiedName.NAME_PROPERTY) {
                                return null;
                            }
                            
                            String typeSignature = Signature.createTypeSignature(field.getDeclaringType().getFullyQualifiedName(), true);
                            typeSignature = typeSignature.replace('.', '/');
                            variable = frame.getThis().getField(field.getElementName(), typeSignature);
                        }
                    }
                    if (variable != null) {
                        return variable;
                    }
                    break;
                }
                if (javaElement instanceof ILocalVariable) {
                    ILocalVariable var = (ILocalVariable)javaElement;
                    IJavaElement parent = var.getParent();
                    while (!(parent instanceof IMethod) && parent != null) {
                        parent = parent.getParent();
                    }
                    if (parent instanceof IMethod) {
                        IMethod method = (IMethod) parent;
                        boolean equal = false;
                        if (method.isBinary()) {
                            // compare resolved signatures
                            if (method.getSignature().equals(frame.getSignature())) {
                                equal = true;
                            }
                        } else {
                            // compare unresolved signatures
                            if (((frame.isConstructor() && method.isConstructor()) || frame.getMethodName().equals(method.getElementName()))
                                    && frame.getDeclaringTypeName().endsWith(method.getDeclaringType().getElementName())
                                    && frame.getArgumentTypeNames().size() == method.getNumberOfParameters()) {
                                equal = true;
                            }
                        }
                        // find variable if equal or method is a Lambda Method
                        if (equal || method.isLambdaMethod()) {
                            return findLocalVariable(frame, var.getElementName());
                        }
                    }
                    break;
                }
            } catch (CoreException e) {
                JDIDebugPlugin.log(e);
            }
                */
        }
        return null;
    }

    private IVariable findCeylonCapturedVariables(IJavaStackFrame frame,
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
