package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.NO_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.fullPath;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isModuleDescriptor;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackageName;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.MODULE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.PACKAGE;
import static com.redhat.ceylon.eclipse.util.Escaping.escapePackageName;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.editors.text.EditorsUI;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ImportList;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Util;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.LinkedMode;

public class PackageCompletions {

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

    static final class PackageProposal extends CompletionProposal {
        private final boolean withBody;
        private final int len;
        private final Package p;
        private final String completed;
        private final CeylonParseController cpc;

        PackageProposal(int offset, String prefix, boolean withBody, 
                int len, Package p, String completed, 
                CeylonParseController cpc) {
            super(offset, prefix, PACKAGE, completed, 
                    completed.substring(len));
            this.withBody = withBody;
            this.len = len;
            this.p = p;
            this.completed = completed;
            this.cpc = cpc;
        }

        @Override
        public Point getSelection(IDocument document) {
            if (withBody) {
                return new Point(offset+completed.length()-prefix.length()-len-5, 3);
            }
            else {
                return new Point(offset+completed.length()-prefix.length()-len, 0);
            }
        }
        
        @Override
        public void apply(IDocument document) {
            super.apply(document);
            if (withBody && 
                    EditorsUI.getPreferenceStore()
                             .getBoolean(LINKED_MODE)) {
                final LinkedModeModel linkedModeModel = new LinkedModeModel();
                final Point selection = getSelection(document);
                List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
                for (final Declaration d: p.getMembers()) {
                    if (Util.isResolvable(d) && d.isShared()) {
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
                ProposalPosition linkedPosition = 
                        new ProposalPosition(document, selection.x, selection.y, 0, 
                                proposals.toArray(NO_COMPLETIONS));
                try {
                    LinkedMode.addLinkedPosition(linkedModeModel, linkedPosition);
                    LinkedMode.installLinkedMode((CeylonEditor) EditorUtil.getCurrentEditor(), 
                            document, linkedModeModel, this, new LinkedMode.NullExitPolicy(),
                            -1, 0);
                }
                catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        }

        @Override
        public String getAdditionalProposalInfo() {
            return getDocumentationFor(cpc, p);
        }
        
        @Override
        protected boolean qualifiedNameIsPath() {
            return true;
        }
    }

    static void addPackageCompletions(CeylonParseController cpc, 
            int offset, String prefix, Tree.ImportPath path, Node node, 
            List<ICompletionProposal> result, boolean withBody) {
        String fullPath = fullPath(offset, prefix, path);
        addPackageCompletions(offset, prefix, node, result, fullPath.length(), 
                fullPath+prefix, cpc, withBody);
    }

    private static void addPackageCompletions(final int offset, final String prefix,
            Node node, List<ICompletionProposal> result, final int len, String pfp,
            final CeylonParseController cpc, final boolean withBody) {
        //TODO: someday it would be nice to propose from all packages 
        //      and auto-add the module dependency!
        /*TypeChecker tc = CeylonBuilder.getProjectTypeChecker(cpc.getProject().getRawProject());
        if (tc!=null) {
        for (Module m: tc.getContext().getModules().getListOfModules()) {*/
        //Set<Package> packages = new HashSet<Package>();
        Unit unit = node.getUnit();
        if (unit!=null) { //a null unit can occur if we have not finished parsing the file
            Module module = unit.getPackage().getModule();
            for (final Package p: module.getAllPackages()) {
                //if (!packages.contains(p)) {
                    //packages.add(p);
                //if ( p.getModule().equals(module) || p.isShared() ) {
                    final String pkg = escapePackageName(p);
                    if (!pkg.isEmpty() && pkg.startsWith(pfp)) {
                        boolean already = false;
                        if (!pfp.equals(pkg)) {
                            //don't add already imported packages, unless
                            //it is an exact match to the typed path
                            for (ImportList il: node.getUnit().getImportLists()) {
                                if (il.getImportedScope()==p) {
                                    already = true;
                                    break;
                                }
                            }
                        }
                        if (!already) {
                            result.add(new PackageProposal(offset, prefix, withBody, 
                                    len, p, pkg + (withBody ? " { ... }" : ""), cpc));
                        }
                    }
                //}
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
