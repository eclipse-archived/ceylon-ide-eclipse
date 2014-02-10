package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.JDK_MODULE_VERSION;
import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.fullPath;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationForModule;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ARCHIVE;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackageName;
import static com.redhat.ceylon.eclipse.util.ModuleQueries.getModuleSearchResults;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.cmr.api.ModuleVersionDetails;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class ModuleCompletions {
    private static final SortedSet<String> JDK_MODULE_VERSION_SET = new TreeSet<String>();
    {
        JDK_MODULE_VERSION_SET.add(AbstractModelLoader.JDK_MODULE_VERSION);
    }
    
    static void addModuleCompletions(CeylonParseController cpc, 
            int offset, String prefix, Tree.ImportPath path, Node node, 
            List<ICompletionProposal> result, boolean withBody) {
        String fullPath = fullPath(offset, prefix, path);
        addModuleCompletions(offset, prefix, node, result, fullPath.length(), 
        		fullPath+prefix, cpc, withBody);
    }

    private static void addModuleCompletions(int offset, String prefix, Node node, 
    		List<ICompletionProposal> result, final int len, String pfp,
            final CeylonParseController cpc, final boolean withBody) {
        if (pfp.startsWith("java.")) {
            for (final String name: new TreeSet<String>(JDKUtils.getJDKModuleNames())) {
                if (name.startsWith(pfp) &&
                        !moduleAlreadyImported(cpc, name)) {
                    String versioned = withBody ? getModuleString(name, JDK_MODULE_VERSION) + ";" : name;
                    result.add(new CompletionProposal(offset, prefix, ARCHIVE, 
                                      versioned, versioned.substring(len), false) {
                        @Override
                        public String getAdditionalProposalInfo() {
                            return getDocumentationForModule(name, JDK_MODULE_VERSION, 
                                    "This module forms part of the Java SDK.");
                        }
                    });
                }
            }
        }
        else {
            final TypeChecker tc = cpc.getTypeChecker();
            if (tc!=null) {
                IProject project = cpc.getProject();
                for (final ModuleDetails module: getModuleSearchResults(pfp, tc,project)
                        .getResults()) {
                    final String name = module.getName();
                    if (!name.equals(Module.DEFAULT_MODULE_NAME) && 
                            !moduleAlreadyImported(cpc, name)) {
                        for (final ModuleVersionDetails version: 
                            module.getVersions().descendingSet()) {
                            final String versioned = withBody ? 
                                    getModuleString(name, version.getVersion()) + ";" : 
                                        name;
                            result.add(new CompletionProposal(offset, prefix, ARCHIVE, 
                                    versioned, versioned.substring(len), false) {
                            	@Override
                            	public Point getSelection(
                            			IDocument document) {
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
                                public String getAdditionalProposalInfo() {
                                    return JDKUtils.isJDKModule(name) ?
                                            getDocumentationForModule(name, JDK_MODULE_VERSION,
                                                    "This module forms part of the Java SDK.") :
                                            getDocumentationFor(module, version.getVersion());
                                }
                            });
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
        List<Tree.ModuleDescriptor> md = cpc.getRootNode().getModuleDescriptors();
		if (!md.isEmpty()) {
			Tree.ImportModuleList iml = md.get(0).getImportModuleList();
			if (iml!=null) {
				for (Tree.ImportModule im: iml.getImportModules()) {
					if (im.getImportPath()!=null) {
						if (formatPath(im.getImportPath().getIdentifiers()).equals(mod)) {
							return true;
						}
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

    private static String getModuleString(final String name, final String version) {
        return name + " \"" + version + "\"";
    }


    static void addModuleDescriptorCompletion(CeylonParseController cpc, int offset, 
            String prefix, List<ICompletionProposal> result) {
        if (!"module".startsWith(prefix)) return; 
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String moduleName = getPackageName(file);
        if (moduleName!=null) {
            String moduleDesc = "module " + moduleName;
            String moduleText = "module " + moduleName + " \"1.0.0\" {}";
            final int selectionStart = offset - prefix.length() + moduleName.length() + 9;
            final int selectionLength = 5;

            result.add(new CompletionProposal(offset, prefix, ARCHIVE, moduleDesc, moduleText, false) {
                @Override
                public Point getSelection(IDocument document) {
                    return new Point(selectionStart, selectionLength);
                }});
        }
    }
    
}
