package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.IMPORT;
import static com.redhat.ceylon.eclipse.util.ModuleQueries.getModuleQuery;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.model.cmr.JDKUtils;
import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.imports.ModuleImportUtil;
import com.redhat.ceylon.eclipse.util.Highlights;

class AddModuleImportProposal implements ICompletionProposal, 
        ICompletionProposalExtension6 {
    
    private final IProject project;
    private final Unit unit; 
    private final String name; 
    private final String version;
    
    AddModuleImportProposal(IProject project, Unit unit, ModuleDetails details) {
        this.project = project;
        this.unit = unit;
        this.name = details.getName();
        this.version = details.getLastVersion().getVersion();
    }
    
    AddModuleImportProposal(IProject project, Unit unit, String name, String version) {
        this.project = project;
        this.unit = unit;
        this.name = name;
        this.version = version;
    }
    
    @Override
    public void apply(IDocument document) {
        ModuleImportUtil.addModuleImport(project, 
                unit.getPackage().getModule(), 
                name, version);
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return "Add 'import " + name + " \"" + version + "\"' to module descriptor";
    }

    @Override
    public Image getImage() {
        return IMPORT;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), true);
    }
    
    static void addModuleImportProposals(final Collection<ICompletionProposal> proposals, 
            final IProject project, final TypeChecker typeChecker, Node node) {
        final Unit unit = node.getUnit();
        if (unit.getPackage().getModule().isDefault()) {
            return;
        }
        if (node instanceof Tree.Import) {
            node = ((Tree.Import) node).getImportPath();
        }
        List<Tree.Identifier> ids = ((Tree.ImportPath) node).getIdentifiers();
        final String pkg = formatPath(ids);
        if (JDKUtils.isJDKAnyPackage(pkg)) {
            for (String mod: new TreeSet<String>(JDKUtils.getJDKModuleNames())) {
                if (JDKUtils.isJDKPackage(mod, pkg)) {
                    proposals.add(new AddModuleImportProposal(project, unit, mod, 
                            JDKUtils.jdk.version));
                    return;
                }
            }
        }
        
        class Runnable implements IRunnableWithProgress {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Querying module repositories...", 
                        IProgressMonitor.UNKNOWN);
                ModuleQuery query = getModuleQuery("", project);
                query.setMemberName(pkg);
                query.setMemberSearchPackageOnly(true);
                query.setMemberSearchExact(true);
                query.setCount(10l);
                query.setBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
                ModuleSearchResult msr = 
                        typeChecker
                            .getContext()
                            .getRepositoryManager()
                            .searchModules(query);
                for (ModuleDetails md: msr.getResults()) {
                    proposals.add(new AddModuleImportProposal(project, unit, md));
                }
                monitor.done();
            }
        }
        try {
            getWorkbench()
                    .getActiveWorkbenchWindow()
                    .run(true, true, new Runnable());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
