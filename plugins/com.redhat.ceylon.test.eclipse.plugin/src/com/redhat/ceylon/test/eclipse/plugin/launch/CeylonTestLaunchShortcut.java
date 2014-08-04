package com.redhat.ceylon.test.eclipse.plugin.launch;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_ENTRIES_KEY;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_TYPE;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.CLASS;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.CLASS_LOCAL;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.METHOD;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.METHOD_LOCAL;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.MODULE;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.PACKAGE;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.PROJECT;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getModule;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getShell;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.isCeylonFile;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.isCeylonProject;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.isTestable;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.util.MethodWithContainer;

public class CeylonTestLaunchShortcut implements ILaunchShortcut {

    private final String configTypeId;

    public CeylonTestLaunchShortcut() {
        this(LAUNCH_CONFIG_TYPE);
    }

    public CeylonTestLaunchShortcut(String configTypeId) {
        this.configTypeId = configTypeId;
    }

    @Override
    public void launch(ISelection selection, String mode) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            if (structuredSelection.size() != 1) {
                MessageDialog.openInformation(getShell(), CeylonTestMessages.launchDialogInfoTitle, CeylonTestMessages.launchSelectionSize);
            } else {
                Object selectedElement = structuredSelection.getFirstElement();
                launch(selectedElement, mode);
            }
        }
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        StringBuilder name = new StringBuilder();
        List<CeylonTestLaunchConfigEntry> entries = new ArrayList<CeylonTestLaunchConfigEntry>();

        if (editor instanceof CeylonEditor) {
            processCeylonEditorSelection(name, entries, (CeylonEditor) editor);
        }
        if (entries.isEmpty()) {
            IFile file = EditorUtil.getFile(editor.getEditorInput());
            processFile(name, entries, file);
        }

        launch(name.toString(), entries, mode, configTypeId);
    }

    private void launch(Object element, String mode) {
        StringBuilder name = new StringBuilder();
        List<CeylonTestLaunchConfigEntry> entries = new ArrayList<CeylonTestLaunchConfigEntry>();

        if (element instanceof IJavaProject) {
            processProject(name, entries, ((IJavaProject) element).getProject());
        } else if (element instanceof IPackageFragment) {
            processPackage(name, entries, (IPackageFragment) element);
        } else if (element instanceof IFile) {
            processFile(name, entries, (IFile) element);
        }

        launch(name.toString(), entries, mode, configTypeId);
    }

    public static void launch(String name, List<CeylonTestLaunchConfigEntry> entries, String mode, String configTypeId) {
        if (entries.isEmpty()) {
            MessageDialog.openInformation(getShell(), CeylonTestMessages.launchDialogInfoTitle, CeylonTestMessages.launchNoTestsFound);
            return;
        }

        try {
            ILaunchConfigurationType configType = getLaunchManager().getLaunchConfigurationType(configTypeId);
            ILaunchConfiguration config = findExistingLaunchConfig(configType, entries);
            if (config == null) {
                config = createLaunchConfig(name, entries, configType);
            }

            DebugUITools.launch(config, mode);
        } catch (CoreException e) {
            CeylonTestPlugin.logError("", e);
        }
    }

    private void processProject(StringBuilder name, List<CeylonTestLaunchConfigEntry> entries, IProject project) {
        if (isCeylonProject(project)) {
            name.append(project.getName());
            entries.add(CeylonTestLaunchConfigEntry.build(project, PROJECT, null));
        }
    }

    private void processPackage(StringBuilder name, List<CeylonTestLaunchConfigEntry> entries, IPackageFragment packageFragment) {
        IProject project = packageFragment.getJavaProject().getProject();
        if (isCeylonProject(project)) {
            name.append(packageFragment.getElementName());
            Module module = getModule(project, packageFragment.getElementName());
            if (module != null) {
                entries.add(CeylonTestLaunchConfigEntry.build(project, MODULE, packageFragment.getElementName()));
            } else {
                entries.add(CeylonTestLaunchConfigEntry.build(project, PACKAGE, packageFragment.getElementName()));
            }
        }
    }

    private void processFile(StringBuilder name, List<CeylonTestLaunchConfigEntry> entries, IFile file) {
        if (!isCeylonFile(file)) {
            return;
        }

        IProject project = file.getProject();
        TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(project);
        if (project == null || typeChecker == null) {
            return;
        }

        String fileName = file.getName().substring(0, file.getName().length() - file.getFileExtension().length() - 1);
        name.append(fileName);

        PhasedUnit phasedUnit = typeChecker.getPhasedUnits().getPhasedUnit(ResourceVirtualFile.createResourceVirtualFile(file));
        if (phasedUnit != null) {
            List<Declaration> declarations = phasedUnit.getDeclarations();
            for (Declaration d : declarations) {
                if (d.isToplevel()) {
                    if (d instanceof Class) {
                        Class clazz = (Class) d;
                        if (isTestable(clazz)) {
                            entries.add(CeylonTestLaunchConfigEntry.build(project, clazz.isShared() ? CLASS : CLASS_LOCAL,
                                    clazz.getQualifiedNameString()));
                        }
                    }
                    else if (d instanceof Method) {
                        Method method = (Method) d;
                        if (isTestable(method)) {
                            entries.add(CeylonTestLaunchConfigEntry.build(project, method.isShared() ? METHOD : METHOD_LOCAL,
                                    method.getQualifiedNameString()));
                        }
                    }
                }
            }
        }
    }

    private void processCeylonEditorSelection(StringBuilder name, List<CeylonTestLaunchConfigEntry> entries, CeylonEditor ce) {
        CeylonParseController cpc = ce.getParseController();
        if (cpc == null) {
            return;
        }

        IProject project = cpc.getProject();
        ISelection selection = ce.getSelectionProvider().getSelection();
        Tree.CompilationUnit cu = cpc.getRootNode();
        if (project == null || selection == null || !(selection instanceof ITextSelection) || cu == null) {
            return;
        }

        Node node = Nodes.findNode(cu, (ITextSelection) selection);
        FindContainerVisitor fcv = new FindContainerVisitor(node);
        fcv.visit(cu);
        node = fcv.getDeclaration();

        if (node instanceof Tree.AnyMethod) {
            Method method = ((Tree.AnyMethod) node).getDeclarationModel();
            if (method.getContainer() instanceof Class && isTestable(new MethodWithContainer((Class)method.getContainer(), method))) {
                if (method.isMember()) {
                    name.append(((Declaration) method.getContainer()).getName()).append(".");
                }
                name.append(method.getName());
                entries.add(CeylonTestLaunchConfigEntry.build(project, method.isShared() ? METHOD : METHOD_LOCAL,
                        method.getQualifiedNameString()));
            }
        }
        if (node instanceof Tree.AnyClass) {
            Class clazz = ((Tree.AnyClass) node).getDeclarationModel();
            if (isTestable(clazz)) {
                name.append(clazz.getName());
                entries.add(CeylonTestLaunchConfigEntry.build(project, clazz.isShared() ? CLASS : CLASS_LOCAL,
                        clazz.getQualifiedNameString()));
            }
        }
        if( node instanceof Tree.ObjectDefinition ) {
            Class clazz = ((Tree.ObjectDefinition) node).getAnonymousClass();
            if (isTestable(clazz)) {
                name.append(clazz.getName());
                entries.add(CeylonTestLaunchConfigEntry.build(project, clazz.isShared() ? CLASS : CLASS_LOCAL,
                        clazz.getQualifiedNameString()));
            }
        }
    }

    private static ILaunchConfiguration createLaunchConfig(String name, List<CeylonTestLaunchConfigEntry> entries,
            ILaunchConfigurationType configType) throws CoreException {
        ILaunchConfigurationWorkingCopy configWorkingCopy = configType.newInstance(null, getLaunchManager()
                .generateLaunchConfigurationName(name));
        configWorkingCopy.setAttribute(ATTR_PROJECT_NAME, entries.get(0).getProjectName());
        configWorkingCopy.setAttribute(LAUNCH_CONFIG_ENTRIES_KEY, CeylonTestLaunchConfigEntry.buildLaunchConfigAttributes(entries));
        return configWorkingCopy.doSave();
    }

    @SuppressWarnings("unchecked")
    private static ILaunchConfiguration findExistingLaunchConfig(ILaunchConfigurationType configType,
            List<CeylonTestLaunchConfigEntry> entries) throws CoreException {
        List<String> attributes = CeylonTestLaunchConfigEntry.buildLaunchConfigAttributes(entries);

        List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>();
        for (ILaunchConfiguration candidateConfig : getLaunchManager().getLaunchConfigurations(configType)) {
            List<String> candidateAttributes = candidateConfig.getAttribute(LAUNCH_CONFIG_ENTRIES_KEY, new ArrayList<String>());
            if (candidateAttributes.equals(attributes)) {
                candidateConfigs.add(candidateConfig);
            }
        }

        if (candidateConfigs.size() == 0) {
            return null;
        } else if (candidateConfigs.size() == 1) {
            return candidateConfigs.get(0);
        } else {
            return chooseExistingLaunchConfig(candidateConfigs);
        }
    }

    private static ILaunchConfiguration chooseExistingLaunchConfig(List<ILaunchConfiguration> configList) {
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), DebugUITools.newDebugModelPresentation());
        dialog.setTitle(CeylonTestMessages.launchSelectLaunchConfigTitle);
        dialog.setMessage(CeylonTestMessages.launchSelectLaunchConfigMessage);
        dialog.setElements(configList.toArray());
        dialog.setMultipleSelection(false);
        if (dialog.open() == Window.OK) {
            return (ILaunchConfiguration) dialog.getFirstResult();
        } else {
            return null;
        }
    }

    private static ILaunchManager getLaunchManager() {
        return DebugPlugin.getDefault().getLaunchManager();
    }

}