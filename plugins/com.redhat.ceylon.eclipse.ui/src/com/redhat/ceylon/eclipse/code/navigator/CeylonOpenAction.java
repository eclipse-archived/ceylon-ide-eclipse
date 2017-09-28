package org.eclipse.ceylon.ide.eclipse.code.navigator;

import static org.eclipse.ceylon.ide.eclipse.core.external.ExternalSourceArchiveManager.getExternalSourceArchiveManager;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedNode;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.ActionMessages;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.editor.Navigation;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.eclipse.core.external.CeylonArchiveFileStore;
import org.eclipse.ceylon.ide.common.model.BaseIdeModelLoader;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.ide.common.model.CeylonBinaryUnit;
import org.eclipse.ceylon.ide.common.model.IJavaModelAware;
import org.eclipse.ceylon.model.loader.ModelLoader.DeclarationType;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.ModelUtil;

public class CeylonOpenAction extends OpenAction {

    public CeylonOpenAction(IWorkbenchSite site) {
        super(site);
    }

    @Override
    public void selectionChanged(IStructuredSelection selection) {
        setEnabled(checkEnabled(selection));
    }

    private boolean checkEnabled(IStructuredSelection selection) {
        if (selection.isEmpty())
            return false;
        for (Iterator<?> iter= selection.iterator(); iter.hasNext();) {
            Object element= iter.next();
            if (element instanceof ISourceReference)
                continue;
            if (element instanceof IFile)
                continue;
            if (JavaModelUtil.isOpenableStorage(element))
                continue;
            if (element instanceof CeylonArchiveFileStore)                
                continue;
            return false;
        }
        return true;
    }

    @Override
    public void run(IStructuredSelection selection) {
        if (!checkEnabled(selection))
            return;
        run(selection.toArray());
    }

    @Override
    public void run(Object[] elements) {
        if (elements == null)
            return;

        MultiStatus status= new MultiStatus(JavaUI.ID_PLUGIN, IStatus.OK, ActionMessages.OpenAction_multistatus_message, null);

        for (int i= 0; i < elements.length; i++) {
            Object element= elements[i];
            try {
                Object elementToOpen= getElementToOpen(element);
                if (! (elementToOpen instanceof IPackageFragment)) {
                    boolean activateOnOpen= OpenStrategy.activateOnOpen();
                    if (elementToOpen instanceof CeylonArchiveFileStore) {                        
                        CeylonArchiveFileStore fileStore = (CeylonArchiveFileStore) elementToOpen;

                        IFolder sourceArchiveFolder = getExternalSourceArchiveManager().getSourceArchive(fileStore.getArchivePath());
                        if (sourceArchiveFolder != null && sourceArchiveFolder.exists()) {
                            IResource file = sourceArchiveFolder.findMember(fileStore.getEntryPath());                            
                            if (file instanceof IFile) {
                                if (CeylonBuilder.isCeylon((IFile)file)) {
                                    elementToOpen = fileStore;
                                }
                                if (CeylonBuilder.isJava((IFile)file)){
                                    //Open with the Class file object if available to have the full-featured ClassFileEditor.
                                    if (fileStore instanceof CeylonArchiveFileStore) {
                                        CeylonArchiveFileStore archiveFileStore = (CeylonArchiveFileStore) fileStore;
                                        IPath entryPath = archiveFileStore.getEntryPath();
                                        String archivePath = archiveFileStore.getArchivePath().toOSString();
                                        searchTheClassToDisplay:
                                        for (IProject project : CeylonBuilder.getProjects()) {
                                            for (BaseIdeModule module : CeylonBuilder.getProjectExternalModules(project)) {
                                                if (archivePath.equals(module.getSourceArchivePath())) {
                                                    String className = ModelUtil.formatPath(Arrays.asList(entryPath.removeFileExtension().segments()));
                                                    IType classToDisplay = JavaCore.create(project).findType(className);
                                                    if (classToDisplay != null) {
                                                        elementToOpen = classToDisplay;
                                                        break searchTheClassToDisplay;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    IEditorPart part= Navigation.openInEditor(elementToOpen, activateOnOpen);
                    
                    if (elementToOpen instanceof IJavaElement) {
                        if (part instanceof CeylonEditor) {
                            IJavaModelAware unit = CeylonBuilder.getUnit((IJavaElement)elementToOpen);
                            if (unit instanceof CeylonBinaryUnit) {
                                CeylonBinaryUnit<IProject,ITypeRoot,IJavaElement> ceylonUnit = (CeylonBinaryUnit<IProject,ITypeRoot,IJavaElement>) unit;
                                CeylonEditor ceylonEditor = (CeylonEditor) part;
                                IMember member = null;
                                if (elementToOpen instanceof IClassFile) {
                                    member = ((IClassFile) elementToOpen).getType();
                                }
                                if (elementToOpen instanceof IMember) {
                                    member = (IMember) elementToOpen;
                                }
                                if (member != null) {
                                    Declaration declaration = toCeylonDeclaration(member, ceylonUnit, 
                                            ceylonUnit.getCeylonModule().getModuleManager().getModelLoader());
                                    Node node = getReferencedNode(declaration, ceylonUnit.getCompilationUnit());
                                    if (node != null) {
                                        Node identifyingNode = getIdentifyingNode(node);
                                        ceylonEditor.selectAndReveal(identifyingNode.getStartIndex(), identifyingNode.getDistance());
                                    }
                                }
                            }
                        } else if (part != null) {
                            JavaUI.revealInEditor(part, (IJavaElement) elementToOpen);
                        }
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

    private Declaration toCeylonDeclaration(IMember member, CeylonBinaryUnit ceylonUnit, BaseIdeModelLoader modelLoader) {
        if (member instanceof IType) {
            return modelLoader.convertToDeclaration(ceylonUnit.getCeylonModule(), ((IType) member).getFullyQualifiedName(), DeclarationType.VALUE);
        }
        
        if (member instanceof IMethod || member instanceof IField) {
            IType parent = member.getDeclaringType();
            Declaration parentDeclaration = modelLoader.convertToDeclaration(ceylonUnit.getCeylonModule(), parent.getFullyQualifiedName(), DeclarationType.VALUE);
            if (parentDeclaration != null) {
                return parentDeclaration.getMemberOrParameter(ceylonUnit, member.getElementName(), null, false);
            }
        }
        return null;
    }
}
