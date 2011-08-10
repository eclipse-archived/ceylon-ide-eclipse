package com.redhat.ceylon.eclipse.imp.builder;

import org.eclipse.core.resources.IProject;

import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.runtime.IPluginLog;

import org.eclipse.imp.smapifier.builder.SmapiProjectNature;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonNature extends ProjectNatureBase {

  public static final String k_natureID = CeylonPlugin.kPluginID + ".imp.nature";

  public String getNatureID() {
    return k_natureID;
  }

  public String getBuilderID() {
    return CeylonBuilder.BUILDER_ID;
  }

  public void addToProject(IProject project) {
    super.addToProject(project);
    new SmapiProjectNature("ceylon").addToProject(project);
  };

  protected void refreshPrefs() {
    // TODO implement preferences and hook in here
  }

  public IPluginLog getLog() {
    return CeylonPlugin.getInstance();
  }

  protected String getDownstreamBuilderID() {
    // TODO If needed, specify the builder that will consume artifacts created by this nature's builder, or null if none
    return "org.eclipse.jdt.core.javabuilder";
  }
}
