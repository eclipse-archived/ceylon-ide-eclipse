package com.redhat.ceylon.eclipse.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClassFile;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.compiler.typechecker.io.ClosableVirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModule;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModuleManager;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.eclipse.util.CarUtils;

/*
 * Created inside the JDTModelLoader.getCompiledUnit() function if the unit is a ceylon one
 */
public class CeylonBinaryUnit extends CeylonUnit implements IJavaModelAware {
    
    IClassFile classFileElement;
    
    public CeylonBinaryUnit(IClassFile typeRoot) {
        super();
        this.classFileElement = typeRoot;
    }
    
    /*
     * Might be null if no source is linked to this ModelLoader-originating unit
     * 
     * (non-Javadoc)
     * @see com.redhat.ceylon.eclipse.core.model.CeylonUnit#getPhasedUnit()
     */
    
    @Override
    public ExternalPhasedUnit getPhasedUnit() {
        return (ExternalPhasedUnit) super.getPhasedUnit();
    }
    
    public IClassFile getJavaElement() {
        return classFileElement;
    }

    @Override
    protected void setPhasedUnitIfNecessary() {
        if (phasedUnitRef == null) {
            // Look into the mapping.txt of the module archive, and get the name of the source unit
            // Then get the PhasedUnits related to this module, and search for the relative path in it.
            // Then set it into the WeakRef with createPhasedUnit
            ExternalPhasedUnit phasedUnit = null;
            
            String[] splittedPath = getFullPath().split("!/");
            if (splittedPath.length == 2) {
                String carPath = splittedPath[0];
                try {
                    Properties mapping = CarUtils.retrieveMappingFile(new File(carPath));
                    String sourceFileRelativePath = mapping.getProperty(splittedPath[1]);
                    if (sourceFileRelativePath != null) {
                        JDTModule module = (JDTModule) getPackage().getModule();
                        JDTModuleManager moduleManager = module.getModuleManager();
                        ClosableVirtualFile sourceArchive = null;
                        VirtualFile archiveEntry = null; 
                        try {
                            sourceArchive = moduleManager.getContext().getVfs().getFromZipFile(new File(carPath.substring(0, carPath.length()-ArtifactContext.CAR.length()) + ArtifactContext.SRC));
                            archiveEntry = sourceArchive; 
                            for (String part : sourceFileRelativePath.split("/")) {
                                boolean found = false;
                                for (VirtualFile vf : archiveEntry.getChildren()) {
                                    if (part.equals(vf.getName().replace("/", ""))) {
                                        archiveEntry = vf;
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    archiveEntry = null;
                                    break;
                                }
                            }
                            IProject project = this.getJavaElement().getJavaProject().getProject();
                            CeylonLexer lexer = new CeylonLexer(new ANTLRInputStream(archiveEntry.getInputStream(), project.getDefaultCharset()));
                            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                            CeylonParser parser = new CeylonParser(tokenStream);
                            Tree.CompilationUnit cu = parser.compilationUnit();
                            List<CommonToken> tokens = new ArrayList<CommonToken>(tokenStream.getTokens().size()); 
                            tokens.addAll(tokenStream.getTokens());
                            phasedUnit = new ExternalPhasedUnit(archiveEntry, sourceArchive, cu, 
                                    getPackage(), moduleManager, CeylonBuilder.getProjectTypeChecker(project), tokens);
                        } catch (Exception e) {
                            StringBuilder error = new StringBuilder("Unable to read source artifact from ");
                            error.append(sourceArchive);
                            error.append( "\ndue to connection error: ").append(e.getMessage());
                        } finally {
                            if (sourceArchive != null) {
                                sourceArchive.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            createPhasedUnitRef(phasedUnit);
        }
    }

    @Override
    public IProject getProjectResource() {
        return getJavaElement().getJavaProject().getProject();
    }
    
}
