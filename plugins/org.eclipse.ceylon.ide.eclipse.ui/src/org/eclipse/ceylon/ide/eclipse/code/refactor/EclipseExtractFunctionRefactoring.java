/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getProjectPhasedUnits;
import static org.eclipse.ceylon.ide.eclipse.util.InteropUtils.toJavaString;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.java.runtime.model.TypeDescriptor;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnits;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;
import org.eclipse.ceylon.ide.common.refactoring.DefaultRegion;
import org.eclipse.ceylon.ide.common.refactoring.ExtractFunctionRefactoring;
import org.eclipse.ceylon.ide.common.refactoring.createExtractFunctionRefactoring_;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;

import ceylon.interop.java.CeylonIterable;
import ceylon.language.empty_;

public class EclipseExtractFunctionRefactoring extends AbstractRefactoring implements ExtractLinkedModeEnabled {

    private String newName;
    private boolean explicitType;
    private TypedDeclaration resultDeclaration;
    private Type returnType;
    private Tree.Declaration target;
    private ExtractFunctionRefactoring refactoring;
    private CeylonEditor ce;
    
    public EclipseExtractFunctionRefactoring(IEditorPart editor) {
        super(editor);
        if (rootNode!=null) {
            if (editor instanceof CeylonEditor) {
                CeylonEditor ce = (CeylonEditor) editor;
                if (ce.getSelectionProvider()!=null) {
                    this.ce = ce;
                    selection = ce.getSelection();
                }
            }
            
            if (resultDeclaration!=null) {
                newName = resultDeclaration.getName();
            }
            else {
                newName = Nodes.nameProposals(node)[0];
                if ("it".equals(newName)) {
                    newName = "do";
                }
            }
        }
        init();
    }

    public EclipseExtractFunctionRefactoring(CeylonEditor editor,
            Tree.Declaration target) {
        this(editor);
        this.target = target;
        init();
    }
    
    private void init() {
        if (newName == null) {
            newName = "do";
        }
        CeylonParseController parseController = 
                ce.getParseController();
        PhasedUnits projectPhasedUnits = 
                getProjectPhasedUnits(project);
        refactoring = createExtractFunctionRefactoring_
                .createExtractFunctionRefactoring(
                        Java2CeylonProxies.correctJ2C()
                            .newDocument(document),
                        selection.getOffset(),
                        selection.getOffset() 
                            + selection.getLength(),
                        parseController.getTypecheckedRootNode(),
                        parseController.getTokens(), 
                        target, 
                        projectPhasedUnits==null ?
                            (ceylon.language.Iterable) 
                                empty_.get_() :
                            new CeylonIterable<PhasedUnit>(
                                TypeDescriptor.klass(PhasedUnit.class),
                                projectPhasedUnits.getPhasedUnits()),
                        parseController
                            .getLastPhasedUnit()
                            .getUnitFile(),
                        new ceylon.language.String(newName));
    }

    @Override
    public boolean getEnabled() {
        if (refactoring != null) {
            return refactoring.getEnabled();
        }
        return sourceFile!=null &&
                getEditable() &&
                !sourceFile.getName()
                    .equals("module.ceylon") &&
                !sourceFile.getName()
                    .equals("package.ceylon");
    }
    
    public String getName() {
        return "Extract Function";
    }
    
    public boolean forceWizardMode() {
        return refactoring.getForceWizardMode();
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        
        String str = toJavaString(refactoring.checkInitialConditions());
        
        if (str != null) {
            return createWarningStatus(str);
        }
        
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        Declaration existing = 
                node.getScope()
                    .getMemberOrParameter(node.getUnit(), 
                            newName, null, false);
        if (null!=existing) {
            return createWarningStatus(
                    "An existing declaration named '" +
                    newName + 
                    "' already exists in the same scope");
        }
        return new RefactoringStatus();
    }

    @Override
    public IRegion getTypeRegion() {
        return refactoring == null ? null : toRegion(refactoring.getTypeRegion());
    }

    @Override
    public IRegion getDecRegion() {
        return refactoring == null ? null : toRegion(refactoring.getDecRegion());
    }

    @Override
    public IRegion getRefRegion() {
        return refactoring == null ? null : toRegion(refactoring.getRefRegion());
    }

    private Region toRegion(DefaultRegion reg) {
        if (reg == null) {
            return null;
        }
        return new Region((int) reg.getStart(), (int) reg.getLength());
    }
    
	private boolean canBeInferred;
    private IRegion selection;

    public Change createChange(IProgressMonitor pm) 
            throws CoreException,
                   OperationCanceledException {

        return Java2CeylonProxies.platformJ2C().getNativeChange(
              refactoring.build());
    }
    
    public void setNewName(String text) {
        newName = text;
        refactoring.setNewName(newName);
    }
    
    public String getNewName() {
        return newName;
    }
    
    public void setExplicitType() {
        this.explicitType = !explicitType;
    }
    
    public Boolean getExplicitType() {
        return explicitType;
    }

    Type getType() {
        return returnType;
    }
    
	public String[] getNameProposals() {
		return Nodes.nameProposals(node);
	}
    
    public boolean canBeInferred() {
        return canBeInferred;
    }

    @Override
    protected boolean isAffectingOtherFiles() {
        return false;
    }

    public List<IRegion> getDupeRegions() {
        if (refactoring != null) {
            List<IRegion> regions = new ArrayList<>();
            ceylon.language.List<? extends DefaultRegion> dupeRegions 
                = refactoring.getDupeRegions();
            for (int i=0; i<dupeRegions.getSize(); i++) {
                regions.add(toRegion(dupeRegions.getFromFirst(i)));
            }
            return regions;
        }
        
        return Collections.emptyList();
    }
    
    @Override
    void refactorInFile(TextChange textChange, CompositeChange compositChange,
            CompilationUnit rootNode, List<CommonToken> tokens) {
        // not used
    }
    
}
