package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ARCHIVE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.PACKAGE;
import static com.redhat.ceylon.eclipse.code.propose.CompletionUtil.fullPath;
import static com.redhat.ceylon.eclipse.code.propose.CompletionUtil.isModuleDescriptor;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackageName;
import static com.redhat.ceylon.eclipse.util.Escaping.escapePackageName;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.ImportList;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class PackageCompletions {

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
                        	final String completed = pkg + (withBody?" { ... }":"");
                            result.add(new CompletionProposal(offset, prefix, PACKAGE, 
                            		completed, completed.substring(len), false) {
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
                                public String getAdditionalProposalInfo() {
                                    return getDocumentationFor(cpc, p);
                                }
                            });
                        }
                    }
                //}
            }
        }
    }
    
    static void addPackageDescriptorCompletion(CeylonParseController cpc, int offset, 
            String prefix, List<ICompletionProposal> result) {
        if (!"package".startsWith(prefix)) return; 
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String packageName = getPackageName(file);
        if (packageName!=null) {
            String packageDesc = "package " + packageName;
            String packageText = "package " + packageName + ";";

            result.add(new CompletionProposal(offset, prefix, PACKAGE, packageDesc, packageText, false));
        }
    }    

    static void addCurrentPackageNameCompletion(CeylonParseController cpc, int offset, 
            String prefix, List<ICompletionProposal> result) {
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String moduleName = getPackageName(file);
        if (moduleName!=null) {
            result.add(new CompletionProposal(offset, prefix, 
                    isModuleDescriptor(cpc) ? ARCHIVE : PACKAGE, 
                            moduleName, moduleName, false));
        }
    }
    
}
