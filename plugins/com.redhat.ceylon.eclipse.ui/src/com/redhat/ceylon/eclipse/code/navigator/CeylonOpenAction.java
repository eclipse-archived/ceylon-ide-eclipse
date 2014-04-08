package com.redhat.ceylon.eclipse.code.navigator;

import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.ActionMessages;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.loader.ModelLoader.DeclarationType;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtility;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.IJavaModelAware;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.util.Nodes;

public class CeylonOpenAction extends OpenAction {

    public CeylonOpenAction(IWorkbenchSite site) {
        super(site);
    }

    @Override
    public void run(Object[] elements) {
        if (elements == null)
            return;

        MultiStatus status= new MultiStatus(JavaUI.ID_PLUGIN, IStatus.OK, ActionMessages.OpenAction_multistatus_message, null);

        for (int i= 0; i < elements.length; i++) {
            Object element= elements[i];
            try {
                Object javaElement= getElementToOpen(element);
                if (! (javaElement instanceof IPackageFragment)) {
                    boolean activateOnOpen= OpenStrategy.activateOnOpen();
                    IEditorPart part= EditorUtility.openInEditor(javaElement, activateOnOpen);
                    
                    if (part instanceof CeylonEditor && javaElement instanceof IJavaElement) {
                        IJavaModelAware unit = CeylonBuilder.getUnit((IJavaElement)javaElement);
                        if (unit instanceof CeylonBinaryUnit) {
                            CeylonBinaryUnit ceylonUnit = (CeylonBinaryUnit) unit;
                            CeylonEditor ceylonEditor = (CeylonEditor) part;
                            IMember member = null;
                            if (javaElement instanceof IClassFile) {
                                member = ((IClassFile) javaElement).getType();
                            }
                            if (javaElement instanceof IMember) {
                                member = (IMember) javaElement;
                            }
                            if (member != null) {
                                Declaration declaration = toCeylonDeclaration(member, ceylonUnit, ceylonUnit.getModule().getModuleManager().getModelLoader());
                                Node node = getReferencedNode(declaration, ceylonUnit.getCompilationUnit());
                                if (node != null) {
                                    ceylonEditor.selectAndReveal(Nodes.getStartOffset(node), Nodes.getLength(node));
                                }
                            }
                        }
                    } else if (part != null && javaElement instanceof IJavaElement) {
                        JavaUI.revealInEditor(part, (IJavaElement) javaElement);
                    }
                }
            } catch (PartInitException e) {
                String message= Messages.format(ActionMessages.OpenAction_error_problem_opening_editor, new String[] { JavaElementLabels.getTextLabel(element, JavaElementLabels.ALL_DEFAULT), e.getStatus().getMessage() });
                status.add(new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR, message, null));
            } catch (CoreException e) {
                String message= Messages.format(ActionMessages.OpenAction_error_problem_opening_editor, new String[] { JavaElementLabels.getTextLabel(element, JavaElementLabels.ALL_DEFAULT), e.getStatus().getMessage() });
                status.add(new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR, message, null));
                JavaPlugin.log(e);
            }
        }
        if (!status.isOK()) {
            IStatus[] children= status.getChildren();
            ErrorDialog.openError(getShell(), ActionMessages.OpenAction_error_title, ActionMessages.OpenAction_error_message, children.length == 1 ? children[0] : status);
        }
    }

    private Declaration toCeylonDeclaration(IMember member, CeylonBinaryUnit ceylonUnit, JDTModelLoader modelLoader) {        
        if (member instanceof IType) {
            return modelLoader.convertToDeclaration(ceylonUnit.getModule(), ((IType) member).getFullyQualifiedName(), DeclarationType.VALUE);
        }
        
        if (member instanceof IMethod || member instanceof IField) {
            IType parent = member.getDeclaringType();
            Declaration parentDeclaration = modelLoader.convertToDeclaration(ceylonUnit.getModule(), parent.getFullyQualifiedName(), DeclarationType.VALUE);
            if (parentDeclaration != null) {
                return parentDeclaration.getMemberOrParameter(ceylonUnit, member.getElementName(), null, false);
            }
        }
        return null;
    }
}
