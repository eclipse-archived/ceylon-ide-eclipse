package com.redhat.ceylon.eclipse.imp.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;

public class PackageSelectionDialog extends ElementListSelectionDialog {

    public PackageSelectionDialog(Shell parent) {
        super(parent, new CeylonLabelProvider());
    }
    
    @Override
    public int open() {
        List<Package> elements = new ArrayList<Package>();
        for (IProject p: CeylonBuilder.getProjects()) {
            for (Module m: CeylonBuilder.getProjectTypeChecker(p)
                    .getContext().getModules().getListOfModules()) {
                for (Package pkg: m.getAllPackages()) {
                    elements.add(pkg);
                }
            }
        }
        setElements(elements.toArray());
        return super.open();
    }
    
}
