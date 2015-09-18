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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.vfs.vfsJ2C;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil;
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

            List<String> names = new ArrayList<String>();
            List<CeylonTestLaunchConfigEntry> entries = new ArrayList<CeylonTestLaunchConfigEntry>();

            Object[] selectedElements = structuredSelection.toArray();
            for (Object selectedElement : selectedElements) {
                processSelectedElement(selectedElement, mode, names, entries);
            }
            
            launch(getLaunchName(names), entries, mode, configTypeId);
        }
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        List<String> names = new ArrayList<String>();
        List<CeylonTestLaunchConfigEntry> entries = new ArrayList<CeylonTestLaunchConfigEntry>();

        if (editor instanceof CeylonEditor) {
            processCeylonEditorSelection(names, entries, (CeylonEditor) editor);
        }
        if (entries.isEmpty()) {
            IFile file = EditorUtil.getFile(editor.getEditorInput());
            processFile(names, entries, file);
        }

        launch(getLaunchName(names), entries, mode, configTypeId);
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

    private void processSelectedElement(Object selectedElement, String mode, List<String> names, List<CeylonTestLaunchConfigEntry> entries) {
        if( selectedElement instanceof IProject ) {
            processProject(names, entries, (IProject) selectedElement);
        } else if (selectedElement instanceof IJavaProject) {
            processProject(names, entries, ((IJavaProject) selectedElement).getProject());
        } else if (selectedElement instanceof IPackageFragmentRoot) {
            processPackageFragmentRoot(names, entries, (IPackageFragmentRoot) selectedElement);
        } else if (selectedElement instanceof IPackageFragment) {
            processPackage(names, entries, (IPackageFragment) selectedElement);
        } else if (selectedElement instanceof IFile) {
            processFile(names, entries, (IFile) selectedElement);
        }
    }

    private void processProject(List<String> names, List<CeylonTestLaunchConfigEntry> entries, IProject project) {
        if (isCeylonProject(project)) {
            names.add(project.getName());
            entries.add(CeylonTestLaunchConfigEntry.build(project, PROJECT, null));
        }
    }
    
    private void processPackageFragmentRoot(List<String> names, List<CeylonTestLaunchConfigEntry> entries, IPackageFragmentRoot packageFragmentRoot) {
        try {
            IProject project = packageFragmentRoot.getJavaProject().getProject();
            if (isCeylonProject(project)) {
                names.add(packageFragmentRoot.getElementName());
                IJavaElement[] children = packageFragmentRoot.getChildren();
                for (IJavaElement child : children) {
                    if (child instanceof IPackageFragment) {
                        IPackageFragment packageFragment = (IPackageFragment) child;
                        Module module = getModule(project, packageFragment.getElementName());
                        if (CeylonTestUtil.containsCeylonTestImport(module)) {
                            entries.add(CeylonTestLaunchConfigEntry.build(project, MODULE, packageFragment.getElementName()));
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            throw new RuntimeException(e);
        }
    }

    private void processPackage(List<String> names, List<CeylonTestLaunchConfigEntry> entries, IPackageFragment packageFragment) {
        IProject project = packageFragment.getJavaProject().getProject();
        if (isCeylonProject(project)) {
            names.add(packageFragment.getElementName());
            Module module = getModule(project, packageFragment.getElementName());
            if (module != null) {
                entries.add(CeylonTestLaunchConfigEntry.build(project, MODULE, packageFragment.getElementName()));
            } else {
                entries.add(CeylonTestLaunchConfigEntry.build(project, PACKAGE, packageFragment.getElementName()));
            }
        }
    }

    private void processFile(List<String> names, List<CeylonTestLaunchConfigEntry> entries, IFile file) {
        if (!isCeylonFile(file)) {
            return;
        }

        IProject project = file.getProject();
        TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(project);
        if (project == null || typeChecker == null) {
            return;
        }

        String fileName = file.getName().substring(0, file.getName().length() - file.getFileExtension().length() - 1);
        names.add(fileName);

        PhasedUnit phasedUnit = typeChecker.getPhasedUnits().getPhasedUnit(vfsJ2C.createVirtualFile(file));
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
                    else if (d instanceof Function) {
                        Function method = (Function) d;
                        if (isTestable(method)) {
                            entries.add(CeylonTestLaunchConfigEntry.build(project, method.isShared() ? METHOD : METHOD_LOCAL,
                                    method.getQualifiedNameString()));
                        }
                    }
                }
            }
        }
    }

    private void processCeylonEditorSelection(List<String> names, List<CeylonTestLaunchConfigEntry> entries, CeylonEditor ce) {
        CeylonParseController cpc = ce.getParseController();
        if (cpc == null) {
            return;
        }

        IProject project = cpc.getProject();
        ISelection selection = ce.getSelectionProvider().getSelection();
        Tree.CompilationUnit cu = cpc.getLastCompilationUnit();
        if (project == null || selection == null || !(selection instanceof ITextSelection) || cu == null) {
            return;
        }

        Node node = Nodes.findNode(cu, cpc.getTokens(), (ITextSelection) selection);
        FindContainerVisitor fcv = new FindContainerVisitor(node);
        fcv.visit(cu);
        node = fcv.getDeclaration();

        if (node instanceof Tree.AnyMethod) {
            Function method = ((Tree.AnyMethod) node).getDeclarationModel();
            if (method.getContainer() instanceof Class && isTestable(new MethodWithContainer((Class)method.getContainer(), method))) {
                if (method.isMember()) {
                    names.add(((Declaration) method.getContainer()).getName() + "." + method.getName());
                } else {
                    names.add(method.getName());
                }
                entries.add(CeylonTestLaunchConfigEntry.build(project, method.isShared() ? METHOD : METHOD_LOCAL,
                        method.getQualifiedNameString()));
            }
        }
        if (node instanceof Tree.AnyClass) {
            Class clazz = ((Tree.AnyClass) node).getDeclarationModel();
            if (isTestable(clazz)) {
                names.add(clazz.getName());
                entries.add(CeylonTestLaunchConfigEntry.build(project, clazz.isShared() ? CLASS : CLASS_LOCAL,
                        clazz.getQualifiedNameString()));
            }
        }
        if( node instanceof Tree.ObjectDefinition ) {
            Class clazz = ((Tree.ObjectDefinition) node).getAnonymousClass();
            if (isTestable(clazz)) {
                names.add(clazz.getName());
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

    private String getLaunchName(List<String> names) {
        StringBuilder nameBuilder = new StringBuilder();
        for (String name : names) {
            nameBuilder.append(name);
            nameBuilder.append(",");
        }
        if (nameBuilder.length() != 0) {
            nameBuilder.setLength(nameBuilder.length() - 1);
        }
        return nameBuilder.toString();
    }

}
