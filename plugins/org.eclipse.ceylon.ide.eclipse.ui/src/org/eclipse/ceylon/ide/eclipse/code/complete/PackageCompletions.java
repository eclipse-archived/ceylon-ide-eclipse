package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.ide.eclipse.code.complete.EclipseCompletionProcessor.NO_COMPLETIONS;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.fullPath;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.isModuleDescriptor;
import static org.eclipse.ceylon.ide.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_ARGUMENTS;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getPackageName;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.importsJ2C;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.getCompletionFont;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.MODULE;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.PACKAGE;
import static org.eclipse.ceylon.ide.eclipse.util.Highlights.MEMBER_STYLER;
import static org.eclipse.ceylon.ide.eclipse.util.Highlights.TYPE_STYLER;
import static org.eclipse.ceylon.ide.eclipse.util.ModuleQueries.getModuleQuery;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isNameMatching;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isOverloadedVersion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.cmr.api.ModuleQuery;
import org.eclipse.ceylon.cmr.api.ModuleSearchResult;
import org.eclipse.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import org.eclipse.ceylon.cmr.api.ModuleVersionDetails;
import org.eclipse.ceylon.common.Versions;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.complete.CompletionProposal.FixedImageRetriever;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.eclipse.util.LinkedMode;
import org.eclipse.ceylon.ide.common.typechecker.LocalAnalysisResult;
import org.eclipse.ceylon.ide.common.util.escaping_;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.ImportList;
import org.eclipse.ceylon.model.typechecker.model.ModelUtil;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.Unit;

public class PackageCompletions {

    public static final class QueriedModulePackageProposal 
            extends PackageProposal {
        
        private final ModuleVersionDetails version;
        private final Unit unit;
        private final ModuleDetails md;
        private String fullPackageName;

        public QueriedModulePackageProposal(int offset,
                String prefix, String memberPackageSubname,
                boolean withBody, String fullPackageName,
                LocalAnalysisResult controller,
                ModuleVersionDetails version, Unit unit,
                ModuleDetails md) {
            super(offset, prefix, memberPackageSubname,
                    withBody, fullPackageName, (CeylonParseController) controller);
            this.fullPackageName = fullPackageName;
            this.version = version;
            this.unit = unit;
            this.md = md;
        }

        @Override
        public void apply(IDocument document) {
            super.apply(document);
            importsJ2C().importUtil().addModuleImport(
                    controller.getLastPhasedUnit().getPackage().getModule(),
                    version.getModule(), version.getVersion());
        }

        @Override
        public String getAdditionalProposalInfo() {
            return getAdditionalProposalInfo(null);
        }

        @Override
        public String getAdditionalProposalInfo(IProgressMonitor monitor) {
            return getDocumentationFor(md, version.getVersion(),
                    fullPackageName,
                    controller.getLastCompilationUnit().getScope(), unit);
        }
    }

    public static final class ImportedModulePackageProposal extends
            PackageProposal {

        private final class PackageMemberCompletionProposal
            implements ICompletionProposal,
                       ICompletionProposalExtension2,
                       ICompletionProposalExtension6  {

            private final Point selection;
            private final LinkedModeModel linkedModeModel;
            private final Declaration d;

            private PackageMemberCompletionProposal(
                    Point selection,
                    LinkedModeModel linkedModeModel,
                    Declaration d) {
                this.selection = selection;
                this.linkedModeModel = linkedModeModel;
                this.d = d;
            }

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

            int length(IDocument document) {
                int length = 0;
                try {
                    for (int i=selection.x;
                            i<document.getLength() &&
                            (Character.isJavaIdentifierPart(document.getChar(i)) ||
                            document.getChar(i)=='.');
                            i++) {
                        length++;
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
                return length;
            }

            @Override
            public void apply(IDocument document) {
                try {
                    document.replace(selection.x,
                            length(document),
                            d.getName());
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
                linkedModeModel.exit(ILinkedModeListener.UPDATE_CARET);
            }

            @Override
            public StyledString getStyledDisplayString() {
                StyledString result = new StyledString();
                Highlights.styleIdentifier(result, prefix,
                        getDisplayString(),
                        d instanceof TypeDeclaration ?
                                TYPE_STYLER : MEMBER_STYLER,
                        getCompletionFont());
                return result;
            }

            @Override
            public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
                apply(viewer.getDocument());
            }

            @Override
            public void selected(ITextViewer viewer, boolean smartToggle) {}

            @Override
            public void unselected(ITextViewer viewer) {}

            @Override
            public boolean validate(IDocument document, int offset, DocumentEvent event) {
                int start = selection.x;
                if (offset<start) {
                    return false;
                }
                String prefix;
                try {
                    prefix = document.get(start, offset-start);
                }
                catch (BadLocationException e) {
                    return false;
                }
                return isNameMatching(prefix, d);
            }
        }

        private final Package candidate;

        public ImportedModulePackageProposal(int offset, String prefix,
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
                    CeylonPlugin.getPreferences().getBoolean(LINKED_MODE_ARGUMENTS)) {
                final LinkedModeModel linkedModeModel = new LinkedModeModel();
                final Point selection = getSelection(document);
                List<ICompletionProposal> proposals =
                        new ArrayList<ICompletionProposal>();
                for (final Declaration d: candidate.getMembers()) {
                    if (ModelUtil.isResolvable(d) && d.isShared() &&
                            !isOverloadedVersion(d)) {
                        proposals.add(
                                new PackageMemberCompletionProposal(
                                        selection, linkedModeModel, d));
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
            return getAdditionalProposalInfo(null);
        }

        @Override
        public String getAdditionalProposalInfo(IProgressMonitor monitor) {
            return getDocumentationFor(controller, candidate);
        }
    }

    public static final class PackageDescriptorProposal extends CompletionProposal {
        
        public PackageDescriptorProposal(int offset, String prefix, String desc, String text) {
            super(offset, prefix, new FixedImageRetriever(PACKAGE), desc, text);
        }

        @Deprecated
        PackageDescriptorProposal(int offset, String prefix, String packageName) {
            super(offset, prefix, new FixedImageRetriever(PACKAGE), 
                    "package " + packageName, 
                    "package " + packageName + ";");
        }
        
        @Override
        protected boolean qualifiedNameIsPath() {
            return true;
        }
    }

    public static class PackageProposal extends CompletionProposal {
        protected final boolean withBody;
        protected final CeylonParseController controller;

        PackageProposal(int offset, String prefix, 
                String memberPackageSubname, boolean withBody, 
                String fullPackageName, 
                CeylonParseController controller) {
            super(offset, prefix, new FixedImageRetriever(PACKAGE), 
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

        @Override
        public StyledString getStyledDisplayString() {
            String text = getDisplayString();
            if (withBody) {
                int loc = text.indexOf(" {");
                return new StyledString(
                        text.substring(0, loc),
                        Highlights.PACKAGE_STYLER)
                            .append(text.substring(loc));
            }
            else {
                return new StyledString(text,
                        Highlights.PACKAGE_STYLER);
            }
        }
    }

    @Deprecated
    static void addPackageCompletions(CeylonParseController cpc, 
            int offset, String prefix, Tree.ImportPath path, Node node, 
            List<ICompletionProposal> result, boolean withBody,
            IProgressMonitor monitor) {
        String fullPath = fullPath(offset, prefix, path);
        addPackageCompletions(offset, prefix, fullPath, 
                withBody, node.getUnit(), cpc, result, monitor);
    }

    @Deprecated
    private static void addPackageCompletions(
            int offset, String prefix,
            String fullPath, boolean withBody, 
            Unit unit,
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
                    String packageName = escaping_.get_().escapePackageName(candidate);
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
                            result.add(new ImportedModulePackageProposal(
                                    offset, prefix,
                                    packageName.substring(fullPath.length()), 
                                    withBody, 
                                    packageName, controller, candidate));
                            found = true;
                        }
                    }
                //}
            }
            if (!found && !unit.getPackage().getNameAsString().isEmpty()) {
                IProject project = controller.getProject();
                monitor.subTask("querying module repositories...");
                ModuleQuery query = 
                        getModuleQuery("", module, project);
                query.setMemberName(fullPrefix);
                query.setMemberSearchPackageOnly(true);
                query.setMemberSearchExact(false);
                query.setJvmBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
                query.setJvmBinaryMinor(Versions.JVM_BINARY_MINOR_VERSION);
                query.setJsBinaryMajor(Versions.JS_BINARY_MAJOR_VERSION);
                query.setJsBinaryMinor(Versions.JS_BINARY_MINOR_VERSION);
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
    
    @Deprecated
    static void addPackageDescriptorCompletion(CeylonParseController cpc, 
            int offset, String prefix, List<ICompletionProposal> result) {
        if (!"package".startsWith(prefix)) return; 
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String packageName = getPackageName(file);
        if (packageName!=null) {
            result.add(new PackageDescriptorProposal(offset, prefix, packageName));
        }
    }    

    @Deprecated
    static void addCurrentPackageNameCompletion(CeylonParseController cpc, 
            int offset, String prefix, List<ICompletionProposal> result) {
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String moduleName = getPackageName(file);
        if (moduleName!=null) {
            result.add(new CompletionProposal(offset, prefix, 
                    new FixedImageRetriever(isModuleDescriptor(cpc) ? MODULE : PACKAGE), 
                            moduleName, moduleName));
        }
    }
    
}
