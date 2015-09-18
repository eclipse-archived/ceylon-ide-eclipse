package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.NO_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.fullPath;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationForModule;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackageName;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.MODULE;
import static com.redhat.ceylon.eclipse.util.ModuleQueries.getModuleQuery;
import static com.redhat.ceylon.eclipse.util.Nodes.getImportedName;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.cmr.api.ModuleVersionDetails;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.LinkedMode;
import com.redhat.ceylon.model.cmr.JDKUtils;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class ModuleCompletions {
    
    static final class ModuleDescriptorProposal extends CompletionProposal {
        
        ModuleDescriptorProposal(int offset, String prefix, String moduleName) {
            super(offset, prefix, MODULE, 
                    "module " + moduleName,
                    "module " + moduleName + " \"1.0.0\" {}");
        }

        @Override
        public Point getSelection(IDocument document) {
            return new Point(offset - prefix.length() + text.indexOf('\"')+1, 5);
        }
        
        @Override
        protected boolean qualifiedNameIsPath() {
            return true;
        }
    }

    static final class ModuleProposal extends CompletionProposal {
        private final int len;
        private final String versioned;
        private final ModuleDetails module;
        private final boolean withBody;
        private final ModuleVersionDetails version;
        private final String name;
        private Node node;

        ModuleProposal(int offset, String prefix, int len, 
                String versioned, ModuleDetails module,
                boolean withBody, ModuleVersionDetails version, 
                String name, Node node) {
            super(offset, prefix, MODULE, versioned, 
                    versioned.substring(len));
            this.len = len;
            this.versioned = versioned;
            this.module = module;
            this.withBody = withBody;
            this.version = version;
            this.name = name;
            this.node = node;
        }
        
        @Override
        public String getDisplayString() {
            String str = super.getDisplayString();
            /*if (withBody && 
                    EditorUtil.getPreferences()
                             .getBoolean(LINKED_MODE)) {
                str = str.replaceAll("\".*\"", "\"<...>\"");
            }*/
            return str;
        }
        
        @Override
        public Point getSelection(IDocument document) {
            final int off = offset+versioned.length()-prefix.length()-len;
            if (withBody) {
                final int verlen = version.getVersion().length();
                return new Point(off-verlen-2, verlen);
            }
            else {
                return new Point(off, 0);
            }
        }

        @Override
        public void apply(IDocument document) {
            super.apply(document);
            if (withBody && //module.getVersions().size()>1 && //TODO: put this back in when sure it works
                    EditorUtil.getPreferences().getBoolean(LINKED_MODE_ARGUMENTS)) {
                final LinkedModeModel linkedModeModel = new LinkedModeModel();
                final Point selection = getSelection(document);
                List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
                for (final ModuleVersionDetails d: module.getVersions()) {
                    proposals.add(new ICompletionProposal() {
                        @Override
                        public Point getSelection(IDocument document) {
                            return null;
                        }
                        @Override
                        public Image getImage() {
                            return CeylonResources.VERSION;
                        }

                        @Override
                        public String getDisplayString() {
                            return d.getVersion();
                        }

                        @Override
                        public IContextInformation getContextInformation() {
                            return null;
                        }

                        @Override
                        public String getAdditionalProposalInfo() {
                            return "Repository: " + d.getOrigin();
                        }

                        @Override
                        public void apply(IDocument document) {
                            try {
                                document.replace(selection.x, selection.y, 
                                        d.getVersion());
                            }
                            catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                            linkedModeModel.exit(ILinkedModeListener.UPDATE_CARET);
                        }
                    });
                }
                ProposalPosition linkedPosition = 
                        new ProposalPosition(document, selection.x, selection.y, 0, 
                                proposals.toArray(NO_COMPLETIONS));
                try {
                    LinkedMode.addLinkedPosition(linkedModeModel, linkedPosition);
                    LinkedMode.installLinkedMode((CeylonEditor) EditorUtil.getCurrentEditor(), 
                            document, linkedModeModel, this, new LinkedMode.NullExitPolicy(),
                            1, selection.x+selection.y+2);
                }
                catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        }
        
        @Override
        public String getAdditionalProposalInfo() {
            Scope scope = node.getScope();
            Unit unit = node.getUnit();
            return JDKUtils.isJDKModule(name) ?
                    getDocumentationForModule(name, JDKUtils.jdk.version,
                            "This module forms part of the Java SDK.",
                            scope, unit) :
                    getDocumentationFor(module, version.getVersion(), 
                            scope, unit);
        }
        
        @Override
        protected boolean qualifiedNameIsPath() {
            return true;
        }
    }
    
    static final class JDKModuleProposal extends CompletionProposal {
        private final String name;

        JDKModuleProposal(int offset, String prefix, int len, 
                String versioned, String name) {
            super(offset, prefix, MODULE, versioned, 
                    versioned.substring(len));
            this.name = name;
        }

        @Override
        public String getAdditionalProposalInfo() {
            return getDocumentationForModule(name, JDKUtils.jdk.version, 
                    "This module forms part of the Java SDK.",
                    null, null);
        }

        @Override
        protected boolean qualifiedNameIsPath() {
            return true;
        }
    }

    private static final SortedSet<String> JDK_MODULE_VERSION_SET = new TreeSet<String>();
    {
        JDK_MODULE_VERSION_SET.add(JDKUtils.jdk.version);
    }
    
    static void addModuleCompletions(CeylonParseController cpc, 
            int offset, String prefix, Tree.ImportPath path, Node node, 
            List<ICompletionProposal> result, boolean withBody,
            IProgressMonitor monitor) {
        String fullPath = fullPath(offset, prefix, path);
        addModuleCompletions(offset, prefix, node, result, fullPath.length(), 
                fullPath+prefix, cpc, withBody, monitor);
    }

    private static void addModuleCompletions(int offset, String prefix, Node node, 
            List<ICompletionProposal> result, int len, String pfp,
            CeylonParseController cpc, boolean withBody,
            IProgressMonitor monitor) {
        if (pfp.startsWith("java.")) {
            for (String name: 
                    new TreeSet<String>(JDKUtils.getJDKModuleNames())) {
                if (name.startsWith(pfp) &&
                        !moduleAlreadyImported(cpc, name)) {
                    result.add(new JDKModuleProposal(offset, prefix, len,
                            getModuleString(withBody, name, JDKUtils.jdk.version), 
                            name));
                }
            }
        }
        else {
            TypeChecker typeChecker = cpc.getTypeChecker();
            if (typeChecker!=null) {
                IProject project = cpc.getProject();
                monitor.subTask("querying module repositories...");
                ModuleQuery query = getModuleQuery(pfp, project);
                query.setBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
                final ModuleSearchResult results = 
                        typeChecker.getContext()
                                .getRepositoryManager()
                                .completeModules(query);
                monitor.subTask(null);
//                final ModuleSearchResult results = 
//                        getModuleSearchResults(pfp, typeChecker,project);
                if (results==null) return;
                for (ModuleDetails module: results.getResults()) {
                    final String name = module.getName();
                    if (!name.equals(Module.DEFAULT_MODULE_NAME) && 
                            !moduleAlreadyImported(cpc, name)) {
                        if (EditorUtil.getPreferences().getBoolean(LINKED_MODE_ARGUMENTS)) {
                            result.add(new ModuleProposal(offset, prefix, len, 
                                    getModuleString(withBody, name, 
                                            module.getLastVersion().getVersion()), 
                                            module, withBody, module.getLastVersion(), 
                                            name, node));
                        }
                        else {
                            for (final ModuleVersionDetails version: 
                                module.getVersions().descendingSet()) {
                                result.add(new ModuleProposal(offset, prefix, len, 
                                        getModuleString(withBody, name, version.getVersion()), 
                                        module, withBody, version, name, node));
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean moduleAlreadyImported(CeylonParseController cpc, final String mod) {
        if (mod.equals(Module.LANGUAGE_MODULE_NAME)) {
            return true;
        }
        List<Tree.ModuleDescriptor> md = cpc.getLastCompilationUnit().getModuleDescriptors();
        if (!md.isEmpty()) {
            Tree.ImportModuleList iml = md.get(0).getImportModuleList();
            if (iml!=null) {
                for (Tree.ImportModule im: iml.getImportModules()) {
                    String path = getImportedName(im);
                    if (path!=null && path.equals(mod)) {
                        return true;
                    }
                }
            }
        }
        //Disabled, because once the module is imported, it hangs around!
//        for (ModuleImport mi: node.getUnit().getPackage().getModule().getImports()) {
//            if (mi.getModule().getNameAsString().equals(mod)) {
//                return true;
//            }
//        }
        return false;
    }

    private static String getModuleString(boolean withBody, 
            String name, String version) {
        if (!name.matches("^[a-z_]\\w*(\\.[a-z_]\\w*)*$")) {
            name = '"' + name + '"';
        }
        return withBody ? name + " \"" + version + "\";" : name;
    }

    static void addModuleDescriptorCompletion(CeylonParseController cpc, 
            int offset, String prefix, List<ICompletionProposal> result) {
        if (!"module".startsWith(prefix)) return; 
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String moduleName = getPackageName(file);
        if (moduleName!=null) {
            result.add(new ModuleDescriptorProposal(offset, prefix, moduleName));
        }
    }
    
}
