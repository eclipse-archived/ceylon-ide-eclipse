package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isOverloadedVersion;
import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.NO_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.fullPath;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isModuleDescriptor;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackageName;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.MODULE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.PACKAGE;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPreferences;
import static com.redhat.ceylon.eclipse.util.Escaping.escapePackageName;
import static com.redhat.ceylon.eclipse.util.ModuleQueries.getModuleQuery;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
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
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ImportList;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Util;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.imports.ModuleImportUtil;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.LinkedMode;

public class PackageCompletions {

    private static final class QueriedModulePackageProposal 
            extends PackageProposal {
        
        private final ModuleVersionDetails version;
        private final Unit unit;
        private final ModuleDetails md;
        private String fullPackageName;

        private QueriedModulePackageProposal(int offset,
                String prefix, String memberPackageSubname,
                boolean withBody, String fullPackageName,
                CeylonParseController controller,
                ModuleVersionDetails version, Unit unit,
                ModuleDetails md) {
            super(offset, prefix, memberPackageSubname,
                    withBody, fullPackageName, controller);
            this.fullPackageName = fullPackageName;
            this.version = version;
            this.unit = unit;
            this.md = md;
        }

        @Override
        public void apply(IDocument document) {
            super.apply(document);
            ModuleImportUtil.addModuleImport(controller.getProject(), 
                    controller.getPhasedUnit().getPackage().getModule(), 
                    version.getModule(), version.getVersion());
        }

        @Override
        public String getAdditionalProposalInfo() {
            return getDocumentationFor(md, version.getVersion(), 
                    fullPackageName,
                    controller.getRootNode().getScope(), unit);
        }
    }

    private static final class ImportedModulePackageProposal extends
            PackageProposal {
        private final Package candidate;

        private ImportedModulePackageProposal(int offset, String prefix,
                String memberPackageSubname, boolean withBody,
                String fullPackageName, CeylonParseController controller,
                Package candidate) {
            super(offset, prefix, memberPackageSubname, withBody,
                    fullPackageName, controller);
            this.candidate = candidate;
        }

        @Override
        public void apply(IDocument document) {
            super.apply(document);
            if (withBody && 
                    getPreferences().getBoolean(LINKED_MODE_ARGUMENTS)) {
                final LinkedModeModel linkedModeModel = new LinkedModeModel();
                final Point selection = getSelection(document);
                List<ICompletionProposal> proposals = 
                        new ArrayList<ICompletionProposal>();
                for (final Declaration d: candidate.getMembers()) {
                    if (Util.isResolvable(d) && d.isShared() && 
                            !isOverloadedVersion(d)) {
                        proposals.add(new ICompletionProposal() {
                            @Override
                            public Point getSelection(IDocument document) {
                                return null;
                            }
                            @Override
                            public Image getImage() {
                                return getImageForDeclaration(d);
                            }
                            
                            @Override
                            public String getDisplayString() {
                                return d.getName();
                            }
                            
                            @Override
                            public IContextInformation getContextInformation() {
                                return null;
                            }
                            
                            @Override
                            public String getAdditionalProposalInfo() {
                                return null;
                            }
                            
                            @Override
                            public void apply(IDocument document) {
                                try {
                                    document.replace(selection.x, selection.y, 
                                            d.getName());
                                }
                                catch (BadLocationException e) {
                                    e.printStackTrace();
                                }
                                linkedModeModel.exit(ILinkedModeListener.UPDATE_CARET);
                            }
                        });
                    }
                }
                
                if (!proposals.isEmpty()) {

                    ProposalPosition linkedPosition = 
                            new ProposalPosition(document, 
                                    selection.x, selection.y, 0, 
                                    proposals.toArray(NO_COMPLETIONS));
                    try {
                        LinkedMode.addLinkedPosition(linkedModeModel, linkedPosition);
                        CeylonEditor editor = (CeylonEditor) 
                                EditorUtil.getCurrentEditor();
                        LinkedMode.installLinkedMode(editor, 
                                document, linkedModeModel, this, 
                                new LinkedMode.NullExitPolicy(),
                                -1, 0);
                    }
                    catch (BadLocationException ble) {
                        ble.printStackTrace();
                    }
                    
                }
            }
        }

        @Override
        public String getAdditionalProposalInfo() {
            return getDocumentationFor(controller, candidate);
        }
    }

    static final class PackageDescriptorProposal extends CompletionProposal {
        
        PackageDescriptorProposal(int offset, String prefix, String packageName) {
            super(offset, prefix, PACKAGE, 
                    "package " + packageName, 
                    "package " + packageName + ";");
        }
        
        @Override
        protected boolean qualifiedNameIsPath() {
            return true;
        }
    }

    static class PackageProposal extends CompletionProposal {
        protected final boolean withBody;
        protected final CeylonParseController controller;

        PackageProposal(int offset, String prefix, 
                String memberPackageSubname, boolean withBody, 
                String fullPackageName, 
                CeylonParseController controller) {
            super(offset, prefix, PACKAGE, 
                    fullPackageName + (withBody ? " { ... }" : ""), 
                    memberPackageSubname + (withBody ? " { ... }" : ""));
            this.withBody = withBody;
            this.controller = controller;
        }

        @Override
        public Point getSelection(IDocument document) {
            if (withBody) {
                return new Point(offset+text.indexOf("...")-prefix.length(), 3);
            }
            else {
                return super.getSelection(document);
            }
        }
        
        @Override
        protected boolean qualifiedNameIsPath() {
            return true;
        }
    }

    static void addPackageCompletions(CeylonParseController cpc, 
            int offset, String prefix, Tree.ImportPath path, Node node, 
            List<ICompletionProposal> result, boolean withBody,
            IProgressMonitor monitor) {
        String fullPath = fullPath(offset, prefix, path);
        addPackageCompletions(offset, prefix, fullPath, 
                withBody, node.getUnit(), cpc, result, monitor);
    }

    private static void addPackageCompletions(int offset, String prefix,
            String fullPath, boolean withBody, final Unit unit,
            CeylonParseController controller, 
            List<ICompletionProposal> result, 
            IProgressMonitor monitor) {
        if (unit!=null) { //a null unit can occur if we have not finished parsing the file
            boolean found = false;
            Module module = unit.getPackage().getModule();
            final String fullPrefix = fullPath + prefix;
            for (final Package candidate: module.getAllVisiblePackages()) {
                //if (!packages.contains(p)) {
                    //packages.add(p);
                //if ( p.getModule().equals(module) || p.isShared() ) {
                    String packageName = escapePackageName(candidate);
                    if (!packageName.isEmpty() && 
                            packageName.startsWith(fullPrefix)) {
                        boolean already = false;
                        if (!fullPrefix.equals(packageName)) {
                            //don't add already imported packages, unless
                            //it is an exact match to the typed path
                            for (ImportList il: unit.getImportLists()) {
                                if (il.getImportedScope()==candidate) {
                                    already = true;
                                    break;
                                }
                            }
                        }
                        //TODO: completion filtering
                        if (!already) {
                            result.add(new ImportedModulePackageProposal(offset, prefix,
                                    packageName.substring(fullPath.length()), withBody, 
                                    packageName, controller, candidate));
                            found = true;
                        }
                    }
                //}
            }
            if (!found && !unit.getPackage().getNameAsString().isEmpty()) {
                monitor.subTask("querying module repositories...");
                ModuleQuery query = 
                        getModuleQuery("", controller.getProject());
                query.setMemberName(fullPrefix);
                query.setMemberSearchPackageOnly(true);
                query.setMemberSearchExact(false);
                query.setBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
                ModuleSearchResult msr = 
                        controller.getTypeChecker()
                            .getContext()
                            .getRepositoryManager()
                            .searchModules(query);
                for (final ModuleDetails md: msr.getResults()) {
                    final ModuleVersionDetails version = 
                            md.getLastVersion();
                    for (String packageName: version.getMembers()) {
                        //TODO: completion filtering
                        if (packageName.startsWith(fullPrefix)) {
                            result.add(new QueriedModulePackageProposal(offset, prefix, 
                                    packageName.substring(fullPath.length()), withBody,
                                    packageName, controller, version, unit, md));
                        }
                    }
                }
            }
        }
    }
    
    static void addPackageDescriptorCompletion(CeylonParseController cpc, 
            int offset, String prefix, List<ICompletionProposal> result) {
        if (!"package".startsWith(prefix)) return; 
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String packageName = getPackageName(file);
        if (packageName!=null) {
            result.add(new PackageDescriptorProposal(offset, prefix, packageName));
        }
    }    

    static void addCurrentPackageNameCompletion(CeylonParseController cpc, 
            int offset, String prefix, List<ICompletionProposal> result) {
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String moduleName = getPackageName(file);
        if (moduleName!=null) {
            result.add(new CompletionProposal(offset, prefix, 
                    isModuleDescriptor(cpc) ? MODULE : PACKAGE, 
                            moduleName, moduleName));
        }
    }
    
}
