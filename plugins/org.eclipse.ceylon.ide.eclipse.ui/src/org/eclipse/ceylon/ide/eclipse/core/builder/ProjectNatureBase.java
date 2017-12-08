/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public abstract class ProjectNatureBase implements IProjectNature {
    private IProject fProject;

    public ProjectNatureBase() {}

    public abstract String getNatureID();

    public abstract String getBuilderID();


    /**
     * Refresh the preferences, to make sure the project settings are up to date.
     * Derived classes must implement.
     */
    protected abstract void refreshPrefs();

    /**
     * Returns the ID of the builder that processes the artifacts that this
     * nature's builder produces. If there is no such dependency, returns null.
     */
    // TODO this should be a property of the builder itself...
    protected String getDownstreamBuilderID() {
        return null; // by default, no such dependency
    }

    /**
     * Returns the ID of the builder that produces artifacts that this nature's
     * builder consumes. If there is no such dependency, returns null.
     */
    // TODO this should be a property of the builder itself...
    protected String getUpstreamBuilderID() {
        return null; // by default, no such dependency
    }

    public void addToProject(IProject project) {
        String natureID= getNatureID();

        refreshPrefs();

        try {
            IProjectDescription description= project.getDescription();
            String[] natures= description.getNatureIds();
            String[] newNatures= new String[natures.length + 1];

            System.arraycopy(natures, 0, newNatures, 1, natures.length);
            newNatures[0]= natureID;

            description.setNatureIds(newNatures);
            project.setDescription(description, null);

            // At this point, this nature's builder should be in the project description,
            // but since the description holds only nature ID's, the Eclipse framework ends
            // up instantiating the nature itself and calling configure() on that instance.
            // It uses the default (no-arg) ctor to do this, so that instance won't have
            // enough info to properly populate the builder arguments, if any. So: we need
            // to find the builder now and set its arguments using getBuilderArguments().
            // N.B.: As an added twist, we have to ask Eclipse for the project description
            // again, rather than using the one we got above, since the latter won't have
            // the builder in it.
            IProjectDescription newDesc= project.getDescription();
            ICommand[] builders= newDesc.getBuildSpec();
            String builderID= getBuilderID();

            for(int i= 0; i < builders.length; i++) {
                if (builders[i].getBuilderName().equals(builderID)) {
                    builders[i].setArguments(getBuilderArguments());
                }
            }
            newDesc.setBuildSpec(builders);
            project.setDescription(newDesc, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public void configure() throws CoreException {
        IProjectDescription desc= getProject().getDescription();
        String builderID= getBuilderID();
        ICommand[] cmds= desc.getBuildSpec();

        // Check: is the builder already in this project?
        for(int i=0; i < cmds.length; i++) {
            if (cmds[i].getBuilderName().equals(builderID))
                return; // relevant command is already in there...
        }

        int beforeWhere= cmds.length;
        String downstreamBuilderID= getDownstreamBuilderID();

        if (downstreamBuilderID != null) {
            // Since this builder produces artifacts that another one will
            // post-process, it needs to run *before* that one.
            // So, find the right spot (in front of that builder) to put this one.
            for(beforeWhere--; beforeWhere >= 0; beforeWhere--) {
                if (cmds[beforeWhere].getBuilderName().equals(downstreamBuilderID))
                    break; // found it
            }
            if (beforeWhere < 0) {
                //getLog().writeErrorMsg("Unable to find downstream builder '" + downstreamBuilderID + "' for builder '" + builderID + "'.");
                beforeWhere= 0; // be safe
            }
        }

        int afterWhere= -1;
        String upstreamBuilderID= getUpstreamBuilderID();

        if (upstreamBuilderID != null) {
            // This builder consumes artifacts that another one will produce,
            // so it needs to run *after* that one.
            // So, find the right spot (after that builder) to put this one.
            for(afterWhere= 0; afterWhere < cmds.length; afterWhere++) {
                if (cmds[afterWhere].getBuilderName().equals(upstreamBuilderID))
                    break; // found it
            }
            if (afterWhere == cmds.length) {
                //getLog().writeErrorMsg("Unable to find upstream builder '" + upstreamBuilderID + "' for builder '" + builderID + "'.");
                afterWhere= cmds.length - 1;
            }
        }

        if (beforeWhere <= afterWhere) {
            //getLog().writeErrorMsg("Error: builder '" + builderID + "' needs to be before downstream builder '" + downstreamBuilderID + "' but after builder " + upstreamBuilderID + ", but " + downstreamBuilderID + " comes after " + upstreamBuilderID + "!");
        }
        if (beforeWhere == cmds.length && afterWhere >= 0)
            beforeWhere= afterWhere + 1;

        ICommand compilerCmd= desc.newCommand();

        compilerCmd.setBuilderName(builderID);
        // RMF 8/9/2006 - Don't bother trying to set the builder arguments here; this instance of
        // the nature will have been constructed with the no-arg ctor (by the Eclipse framework),
        // and won't have enough info to compute the builder arguments. Do it later, in addToProject(),
        // which will hopefully be executed by a nature instance that does have the necessary info.
        //   compilerCmd.setArguments(getBuilderArguments());

        ICommand[] newCmds= new ICommand[cmds.length+1];

        System.arraycopy(cmds, 0, newCmds, 0, beforeWhere);
        newCmds[beforeWhere] = compilerCmd;
        System.arraycopy(cmds, beforeWhere, newCmds, beforeWhere+1, cmds.length-beforeWhere);
        desc.setBuildSpec(newCmds);
        getProject().setDescription(desc, null);
    }

    protected Map<String, String> getBuilderArguments() {
        return new HashMap<String, String>();
    }

    public void deconfigure() throws CoreException {
        IProjectDescription desc= getProject().getDescription();
        String builderID= getBuilderID();
        ICommand[] cmds= desc.getBuildSpec();

        for(int i=0; i < cmds.length; ++i) {
            if (cmds[i].getBuilderName().equals(builderID)) {
                ICommand[] newCmds= new ICommand[cmds.length - 1];

                System.arraycopy(cmds, 0, newCmds, 0, i);
                System.arraycopy(cmds, i + 1, newCmds, i, cmds.length - i - 1);
                desc.setBuildSpec(newCmds);
                getProject().setDescription(desc, null);
                return;
            }
        }
    }

    public IProject getProject() {
        return fProject;
    }

    public void setProject(IProject project) {
        fProject= project;
    }
}
