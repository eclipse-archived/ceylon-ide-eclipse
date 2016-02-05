package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeDefaultDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeFormalDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeSharedDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeVariableDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddConstructorProposal.addConstructorProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddInitializerProposal.addInitializerProposals;
import static com.redhat.ceylon.eclipse.code.correct.AddModuleImportProposal.addModuleImportProposals;
import static com.redhat.ceylon.eclipse.code.correct.AddParameterListProposal.addParameterListProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddParameterProposal.addParameterProposals;
import static com.redhat.ceylon.eclipse.code.correct.AddPunctuationProposal.addEmptyParameterListProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddPunctuationProposal.addImportWildcardProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddSatisfiesProposal.addSatisfiesProposals;
import static com.redhat.ceylon.eclipse.code.correct.AddSpreadToVariadicParameterProposal.addSpreadToSequenceParameterProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddThrowsAnnotationProposal.addThrowsAnnotationProposal;
import static com.redhat.ceylon.eclipse.code.correct.AppendMemberReferenceProposal.addAppendMemberReferenceProposals;
import static com.redhat.ceylon.eclipse.code.correct.AssertExistsDeclarationProposal.addAssertExistsDeclarationProposals;
import static com.redhat.ceylon.eclipse.code.correct.AssignToAssertExistsProposal.addAssignToAssertExistsProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToAssertIsProposal.addAssignToAssertIsProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToAssertNonemptyProposal.addAssignToAssertNonemptyProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToFieldProposal.addAssignToFieldProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToForProposal.addAssignToForProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToIfExistsProposal.addAssignToIfExistsProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToIfIsProposal.addAssignToIfIsProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToIfNonemptyProposal.addAssignToIfNonemptyProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToLocalProposal.addAssignToLocalProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToTryProposal.addAssignToTryProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeDeclarationProposal.addChangeDeclarationProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeInitialCaseOfIdentifierInDeclaration.addChangeIdentifierCaseProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeRefiningTypeProposal.addChangeRefiningParametersProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeRefiningTypeProposal.addChangeRefiningTypeProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeToIfProposal.addChangeToIfProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeTypeProposal.addChangeTypeArgProposals;
import static com.redhat.ceylon.eclipse.code.correct.ChangeTypeProposal.addChangeTypeProposals;
import static com.redhat.ceylon.eclipse.code.correct.ConvertFunctionToGetterProposal.addConvertFunctionToGetterProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertGetterToFunctionProposal.addConvertGetterToFunctionProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertIfElseToThenElse.addConvertToThenElseProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertStringProposal.addConvertFromVerbatimProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertStringProposal.addConvertToVerbatimProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertSwitchToIfProposal.addConvertIfToSwitchProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertSwitchToIfProposal.addConvertSwitchToIfProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertThenElseToIfElse.addConvertToIfElseProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToBlockProposal.addConvertToBlockProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToClassProposal.addConvertToClassProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToConcatenationProposal.addConvertToConcatenationProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToDefaultConstructorProposal.addConvertToDefaultConstructorProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToGetterProposal.addConvertToGetterProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToInterpolationProposal.addConvertToInterpolationProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToNamedArgumentsProposal.addConvertToNamedArgumentsProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToPositionalArgumentsProposal.addConvertToPositionalArgumentsProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToSpecifierProposal.addConvertToSpecifierProposal;
import static com.redhat.ceylon.eclipse.code.correct.CreateParameterProposal.addCreateParameterProposals;
import static com.redhat.ceylon.eclipse.code.correct.CreateProposal.addCreateProposals;
import static com.redhat.ceylon.eclipse.code.correct.CreateTypeParameterProposal.addCreateTypeParameterProposal;
import static com.redhat.ceylon.eclipse.code.correct.DestructureProposal.addDestructureProposal;
import static com.redhat.ceylon.eclipse.code.correct.ExpandTypeProposal.addExpandTypeProposal;
import static com.redhat.ceylon.eclipse.code.correct.FillInArgumentNameProposal.addFillInArgumentNameProposal;
import static com.redhat.ceylon.eclipse.code.correct.FixAliasProposal.addFixAliasProposal;
import static com.redhat.ceylon.eclipse.code.correct.FixMultilineStringIndentationProposal.addFixMultilineStringIndentation;
import static com.redhat.ceylon.eclipse.code.correct.InvertIfElseProposal.addInvertIfElseProposal;
import static com.redhat.ceylon.eclipse.code.correct.JoinDeclarationProposal.addJoinDeclarationProposal;
import static com.redhat.ceylon.eclipse.code.correct.JoinIfStatementsProposal.addJoinIfStatementsProposal;
import static com.redhat.ceylon.eclipse.code.correct.MoveDirProposal.addMoveDirProposal;
import static com.redhat.ceylon.eclipse.code.correct.OperatorProposals.addInvertOperatorProposal;
import static com.redhat.ceylon.eclipse.code.correct.OperatorProposals.addParenthesesProposals;
import static com.redhat.ceylon.eclipse.code.correct.OperatorProposals.addReverseOperatorProposal;
import static com.redhat.ceylon.eclipse.code.correct.OperatorProposals.addSwapBinaryOperandsProposal;
import static com.redhat.ceylon.eclipse.code.correct.PrintProposal.addPrintProposal;
import static com.redhat.ceylon.eclipse.code.correct.RefineEqualsHashProposal.addRefineEqualsHashProposal;
import static com.redhat.ceylon.eclipse.code.correct.RefineFormalMembersProposal.addRefineFormalMembersProposal;
import static com.redhat.ceylon.eclipse.code.correct.RemoveAliasProposal.addRemoveAliasProposal;
import static com.redhat.ceylon.eclipse.code.correct.RenameAliasProposal.addRenameAliasProposal;
import static com.redhat.ceylon.eclipse.code.correct.RenameDescriptorProposal.addRenameDescriptorProposal;
import static com.redhat.ceylon.eclipse.code.correct.RenameVersionProposal.addRenameVersionProposals;
import static com.redhat.ceylon.eclipse.code.correct.ShadowReferenceProposal.addShadowReferenceProposal;
import static com.redhat.ceylon.eclipse.code.correct.ShadowReferenceProposal.addShadowSwitchReferenceProposal;
import static com.redhat.ceylon.eclipse.code.correct.SpecifyTypeProposal.addSpecifyTypeProposal;
import static com.redhat.ceylon.eclipse.code.correct.SpecifyTypeProposal.addTypingProposals;
import static com.redhat.ceylon.eclipse.code.correct.SplitDeclarationProposal.addSplitDeclarationProposals;
import static com.redhat.ceylon.eclipse.code.correct.SplitIfStatementProposal.addSplitIfStatementProposal;
import static com.redhat.ceylon.eclipse.code.correct.UseAliasProposal.addUseAliasProposal;
import static com.redhat.ceylon.eclipse.code.correct.VerboseRefinementProposal.addVerboseRefinementProposal;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.MODULE_DEPENDENCY_PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.MarkerCreator.ERROR_CODE_KEY;
import static com.redhat.ceylon.eclipse.util.AnnotationUtils.getAnnotationsForLine;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Highlights.STRING_STYLER;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static com.redhat.ceylon.eclipse.util.Nodes.findArgument;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;
import static com.redhat.ceylon.eclipse.util.Nodes.findImport;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.findOperator;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNodeInUnit;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.*;


/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;
import static java.util.Collections.singletonList;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AttributeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MethodDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.MarkerCreator;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.MarkerUtils;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.NamedArgumentList;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class CeylonCorrectionProcessor extends QuickAssistAssistant 
        implements IQuickAssistProcessor {
    
    private static final ProblemLocation[] NO_PROBLEM_LOCATIONS = new ProblemLocation[0];
    private static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];

    private static final class CollectWarningsToSuppressVisitor
            extends Visitor {
        private final StringBuilder sb;
        private final StyledString ss;

        private CollectWarningsToSuppressVisitor
                (StringBuilder sb, StyledString ss) {
            this.sb = sb;
            this.ss = ss;
        }

        @Override
        public void visitAny(Node node) {
            for (Message m: node.getErrors()) {
                if (m instanceof UsageWarning) {
                    UsageWarning warning = (UsageWarning) m;
                    String warningName = 
                            warning.getWarningName();
                    if (!sb.toString().contains(warningName)) {
                        if (sb.length()>0) {
                            sb.append(", ");
                            ss.append(", ");
                        }
                        sb.append('"')
                          .append(warningName)
                          .append('"');
                        ss.append('"', STRING_STYLER)
                          .append(warningName, STRING_STYLER)
                          .append('"', STRING_STYLER);
                    }
                }
            }
            super.visitAny(node);
        }
    }

    CeylonEditor editor; //may only be used for quick assists!!!
    private Tree.CompilationUnit model;
    private IFile file; //may only be used for markers!
    
    public CeylonCorrectionProcessor(CeylonEditor editor) {
        this.editor = editor;
        setQuickAssistProcessor(this);        
    }

    public CeylonCorrectionProcessor(IMarker marker) {
        IFileEditorInput input = MarkerUtils.getInput(marker);
        if (input!=null) {
            file = input.getFile();
            IProject project = file.getProject();
            IJavaProject javaProject = JavaCore.create(project);
            TypeChecker tc = getProjectTypeChecker(project);
            if (tc!=null) {
                try {
                    for (IPackageFragmentRoot pfr:
                            javaProject.getPackageFragmentRoots()) {
                        if (pfr.getPath()
                                .isPrefixOf(file.getFullPath())) {
                            IPath relPath =
                                    file.getFullPath()
                                        .makeRelativeTo(
                                                pfr.getPath());
                            PhasedUnit pu =
                                    tc.getPhasedUnitFromRelativePath(
                                            relPath.toString());
                            model = pu.getCompilationUnit();
                        }
                    }
                } 
                catch (JavaModelException e) {
                    e.printStackTrace();
                }
            }
        }
        setQuickAssistProcessor(this);
    }
    
    private IFile getFile() {
        if (editor!=null) {
            IEditorInput ei = editor.getEditorInput();
            if (ei instanceof FileEditorInput) {
                FileEditorInput input =
                        (FileEditorInput) ei;
                if (input!=null) {
                    return input.getFile();
                }
            }
        }
        return file;
    }
    
    private Tree.CompilationUnit getRootNode() {
        if (editor!=null) {
            Tree.CompilationUnit upToDateRootNode =
                    editor.getParseController()
                        .getTypecheckedRootNode();
            if (upToDateRootNode != null) {
                return upToDateRootNode;
            }
        }

        if (model!=null) {
            return (Tree.CompilationUnit) model;
        }

        return null;
    }
    
    @Override
    public String getErrorMessage() {
        return null;
    }
    
    private void collectProposals(
            IQuickAssistInvocationContext context,
            IAnnotationModel model,
            Collection<Annotation> annotations,
            boolean addQuickFixes, boolean addQuickAssists,
            Collection<ICompletionProposal> proposals) {
        ArrayList<ProblemLocation> problems = 
                new ArrayList<ProblemLocation>();
        // collect problem locations and corrections from marker annotations
        for (Annotation curr: annotations) {
            if (curr instanceof CeylonAnnotation) {
                CeylonAnnotation ca = (CeylonAnnotation) curr;
                ProblemLocation problemLocation = 
                        getProblemLocation(ca, model);
                if (problemLocation != null) {
                    problems.add(problemLocation);
                }
            }
            else if (curr instanceof MarkerAnnotation) {
                MarkerAnnotation ma = (MarkerAnnotation) curr;
                ProblemLocation problemLocation =
                        getProblemLocation(ma, model);
                if (problemLocation != null) {
                    problems.add(problemLocation);
                }
            }
        }

        ProblemLocation[] problemLocations =
                problems.toArray(NO_PROBLEM_LOCATIONS);
        Arrays.sort(problemLocations);
        if (addQuickFixes) {
            collectCorrections(context, problemLocations, proposals);
        }
        if (addQuickAssists) {
            collectAssists(context, problemLocations, proposals);
        }
        if (addQuickFixes) {
            addSuppressWarningsProposals(context, model, annotations, proposals);
        }
    }

    public void addSuppressWarningsProposals(
            IQuickAssistInvocationContext context,
            IAnnotationModel model,
            Collection<Annotation> annotations,
            Collection<ICompletionProposal> proposals) {
        for (Annotation curr: annotations) {
            if (curr instanceof CeylonAnnotation) {
                CeylonAnnotation ca = (CeylonAnnotation) curr;
                if (ca.getSeverity()==IMarker.SEVERITY_WARNING) {
                    ProblemLocation problemLocation = 
                            getProblemLocation(ca, model);
                    if (problemLocation != null) {
                        collectWarningSuppressions(ca, context,
                                problemLocation, proposals);
                        break;
                    }
                }
            }
        }
    }

    private static ProblemLocation getProblemLocation(
            CeylonAnnotation annotation,
            IAnnotationModel model) {
        int problemId = annotation.getId();
        if (problemId != -1) {
            Position pos = model.getPosition(annotation);
            if (pos != null) {
                return new ProblemLocation(
                        pos.getOffset(), pos.getLength(),
                        problemId); // java problems all handled by the quick assist processors
            }
        }
        return null;
    }

    private static ProblemLocation getProblemLocation(
            MarkerAnnotation annotation,
            IAnnotationModel model) {
        Integer problemId = null;
        try {
            problemId = (Integer)
                annotation.getMarker()
                    .getAttribute(MarkerCreator.ERROR_CODE_KEY);
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
        if (problemId != null) {
            Position pos = model.getPosition(annotation);
            if (pos != null) {
                return new ProblemLocation(
                        pos.getOffset(), pos.getLength(),
                        problemId); // java problems all handled by the quick assist processors
            }
        }
        return null;
    }

    private void collectAssists(
            IQuickAssistInvocationContext context,
            ProblemLocation[] locations,
            Collection<ICompletionProposal> proposals) {
        if (proposals.isEmpty()) {
            addProposalsWithProgress(context, editor, proposals);
        }
    }

    @Override
    public ICompletionProposal[] computeQuickAssistProposals(
            IQuickAssistInvocationContext context) {
        ArrayList<ICompletionProposal> proposals =
                new ArrayList<ICompletionProposal>();
        ISourceViewer viewer = context.getSourceViewer();
        List<Annotation> annotations = 
                getAnnotationsForLine(viewer,
                        getLine(context, viewer));
        collectProposals(context, viewer.getAnnotationModel(),
                annotations, true, true, proposals);
        return proposals.toArray(NO_PROPOSALS);
    }

    private void addProposalsWithProgress(
            final IQuickAssistInvocationContext context,
            final ProblemLocation location, final IFile file,
            final Tree.CompilationUnit rootNode,
            final Collection<ICompletionProposal> proposals) {
        class Runnable implements IRunnableWithProgress {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException,
                           InterruptedException {
                monitor.beginTask("Preparing fix proposals...",
                        IProgressMonitor.UNKNOWN);
                addProposals(context, location, file, rootNode, proposals);
                monitor.done();
            }
        }
        Runnable runnable = new Runnable();
        try {
            getWorkbench()
                .getActiveWorkbenchWindow()
                .run(true, true, runnable);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addProposalsWithProgress(
            final IQuickAssistInvocationContext context,
            final CeylonEditor editor,
            final Collection<ICompletionProposal> proposals) {
        class Runnable implements IRunnableWithProgress {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException,
                           InterruptedException {
                monitor.beginTask("Preparing assist proposals...",
                        IProgressMonitor.UNKNOWN);
                addProposals(context, editor, proposals);
                monitor.done();
            }
        }
        Runnable runnable = new Runnable();
        try {
            getWorkbench()
                .getActiveWorkbenchWindow()
                //we have to run this in the UI thread
                .run(false, true, runnable);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getLine(
            IQuickAssistInvocationContext context,
            ISourceViewer viewer) {
        try {
            return viewer.getDocument()
                    .getLineOfOffset(context.getOffset());
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public void collectCorrections(
            IQuickAssistInvocationContext context,
            ProblemLocation location,
            Collection<ICompletionProposal> proposals) {
        Tree.CompilationUnit rootNode = getRootNode();
        if (rootNode!=null) {
            addProposals(context, location, getFile(), 
                    rootNode, proposals);
        }
    }
    
    private void collectCorrections(
            IQuickAssistInvocationContext context,
            ProblemLocation[] locations,
            Collection<ICompletionProposal> proposals) {
        ISourceViewer viewer = context.getSourceViewer();
        Tree.CompilationUnit rootNode = getRootNode();
        if (rootNode == null) {
            return;
        }
        for (int i=locations.length-1; i>=0; i--) {
            ProblemLocation loc = locations[i];
            if (loc.getOffset()<=viewer.getSelectedRange().x) {
                for (int j=i; j>=0; j--) {
                    ProblemLocation location = locations[j];
                    if (location.getOffset()!=loc.getOffset()) {
                        break;
                    }
                    addProposalsWithProgress(context,
                            location, getFile(),
                            rootNode, proposals);
                }
                if (!proposals.isEmpty()) {
                    viewer.setSelectedRange(loc.getOffset(), 
                            loc.getLength());
                    return;
                }
            }
        }
        for (int i=0; i<locations.length; i++) {
            ProblemLocation loc = locations[i];
            for (int j=i; j<locations.length; j++) {
                ProblemLocation location = locations[j];
                if (location.getOffset()!=loc.getOffset()) break;
                addProposalsWithProgress(context,
                        location, getFile(),
                        rootNode, proposals);
            }
            if (!proposals.isEmpty()) {
                viewer.setSelectedRange(loc.getOffset(), 
                        loc.getLength());
                return;
            }
        }
    }

    public static boolean canFix(IMarker marker)  {
        try {
            String mt = marker.getType();
            if (mt.equals(PROBLEM_MARKER_ID) ||
                mt.equals(MODULE_DEPENDENCY_PROBLEM_MARKER_ID)) {
                int code = marker.getAttribute(ERROR_CODE_KEY, 0);
                return code>0;
            }
            else {
                return false;
            }
        }
        catch (CoreException e) {
            return false;
        }
    }
    
    @Override
    public boolean canFix(Annotation annotation) {
        if (annotation instanceof CeylonAnnotation) {
            CeylonAnnotation ceylonAnnotation =
                    (CeylonAnnotation) annotation;
            return ceylonAnnotation.isFixable();
        }
        else if (annotation instanceof MarkerAnnotation) {
            MarkerAnnotation markerAnnotation =
                    (MarkerAnnotation) annotation;
            return canFix(markerAnnotation.getMarker());
        }
        else {
            return false;
        }
    }

    @Override
    public boolean canAssist(IQuickAssistInvocationContext context) {
        //oops, all this is totally useless, because
        //this method never gets called :-/
        /*Tree.CompilationUnit cu = (CompilationUnit) context.getModel()
                .getAST(new NullMessageHandler(), new NullProgressMonitor());
        return CeylonSourcePositionLocator.findNode(cu, null, context.getOffset(), 
                context.getOffset()+context.getLength()) instanceof Tree.Term;*/
        return true;
    }
    
    CeylonEditor getCurrentCeylonEditor() {
        if (editor != null) {
            return editor;
        }
        IEditorPart editorPart = EditorUtil.getCurrentEditor();
        if (editorPart instanceof CeylonEditor) {
            return (CeylonEditor) editorPart;
        }
        return null;
    }
    
    private void addProposals(
            IQuickAssistInvocationContext context,
            ProblemLocation problem, IFile file, 
            Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals) {
        if (file==null) return;
        IProject project = file.getProject();
        TypeChecker tc = getProjectTypeChecker(project);
        int start = problem.getOffset();
        int end = start + problem.getLength();
        Node node = findNode(rootNode, null, start, end);

        correctJ2C().addQuickFixes(problem, rootNode, node, project, proposals, editor, tc, file);

        switch (problem.getProblemId()) {
//        case 100:
//            addDeclareLocalProposal(rootNode, node, proposals, file, editor);
//            //fall through:
//        case 102:
//            if (tc!=null) {
//                importProposals().addImportProposals(rootNode, node, proposals, file);
//            }
//            addCreateEnumProposal(rootNode, node, problem, proposals, project);
//            addCreationProposals(rootNode, node, problem, proposals, project, file);
//            if (tc!=null) {
//                addChangeReferenceProposals(rootNode, node, problem, proposals, file);
//            }
//            break;
//        case 101:
//            addCreateParameterProposals(rootNode, node, problem, proposals, project);
//            if (tc!=null) {
//                addChangeArgumentReferenceProposals(rootNode, node, problem, proposals, file);
//            }
//            break;
        case 200:
            addSpecifyTypeProposal(rootNode, node, proposals, null);
            break;
//        case 300:
//            addRefineFormalMembersProposal(proposals, node, rootNode, false);
//            addMakeAbstractDecProposal(proposals, project, node);
//            break;
//        case 350:
//            addRefineFormalMembersProposal(proposals, node, rootNode, true);
//            addMakeAbstractDecProposal(proposals, project, node);
//            break;
//        case 310:
//            addMakeAbstractDecProposal(proposals, project, node);
//            break;
//        case 320:
//            addRemoveAnnotationProposal(node, "formal", proposals, project);
//            break;
//        case 400:
//        case 402:
//            addMakeSharedProposal(proposals, project, node);
//            break;
//        case 705:
//            addMakeSharedDecProposal(proposals, project, node);
//            break;
//        case 500:
//        case 510:
//            addMakeDefaultProposal(proposals, project, node);
//            break;
//        case 600:
//            addMakeActualDecProposal(proposals, project, node);
//            break;
//        case 701:
//            addMakeSharedDecProposal(proposals, project, node);
//            addRemoveAnnotationDecProposal(proposals, "actual", project, node);
//            break;
//        case 702:
//            addMakeSharedDecProposal(proposals, project, node);
//            addRemoveAnnotationDecProposal(proposals, "formal", project, node);
//            break;
//        case 703:
//            addMakeSharedDecProposal(proposals, project, node);
//            addRemoveAnnotationDecProposal(proposals, "default", project, node);
//            break;
//        case 710:
//        case 711:
//            addMakeSharedProposal(proposals, project, node);
//            break;
//        case 712:
//            addExportModuleImportProposal(proposals, project, node);
//            break;
//        case 713:
//            addMakeSharedProposalForSupertypes(proposals, project, node);
//            break;
//        case 714:
//            addExportModuleImportProposalForSupertypes(proposals, project, node, rootNode);
//            break;
//        case 800:
//        case 804:
//            addMakeVariableProposal(proposals, project, node);
//            break;
//        case 803:
//            addMakeVariableProposal(proposals, project, node);
//            break;
//        case 801:
//            addMakeVariableDecProposal(proposals, project, rootNode, node);
//            break;
//        case 802:
//            break;
//        case 905:
//            addMakeContainerAbstractProposal(proposals, project, node);
//            break;
//        case 1100:
//            addMakeContainerAbstractProposal(proposals, project, node);
//            addRemoveAnnotationDecProposal(proposals, "formal", project, node);
//            break;
//        case 1101:
//            addRemoveAnnotationDecProposal(proposals, "formal", project, node);
//            //TODO: replace body with ;
//            break;
        case 1000:
        case 1001:
            addEmptyParameterListProposal(file, proposals, node);
            addParameterListProposal(file, proposals, node, rootNode, false);
            addConstructorProposal(file, proposals, node, rootNode);
            addChangeDeclarationProposal(problem, file, proposals, node);
            break;
        case 1020:
            addImportWildcardProposal(file, proposals, node);
            break;
        case 1050:
            addFixAliasProposal(proposals, file, problem);
            break;
//        case 1200:
//        case 1201:
//            addRemoveAnnotationDecProposal(proposals, "shared", project, node);
//            break;
//        case 1300:
//        case 1301:
//            addMakeRefinedSharedProposal(proposals, project, node);
//            addRemoveAnnotationDecProposal(proposals, "actual", project, node);
//            break;
//        case 1303:
//        case 1313:
//        case 1320:
//            addRemoveAnnotationDecProposal(proposals, "formal", project, node);
//            addRemoveAnnotationDecProposal(proposals, "default", project, node);
//            break;
//        case 1350:
//            addRemoveAnnotationDecProposal(proposals, "default", project, node);
//            addMakeContainerNonfinalProposal(proposals, project, node);
//            break;
//        case 1400:
//        case 1401:
//            addMakeFormalDecProposal(proposals, project, node);
//            break;
        case 1450:
        	//addMakeFormalDecProposal(proposals, project, node);
        	addParameterProposals(proposals, file, rootNode, node);
        	addInitializerProposals(proposals, file, rootNode, node);
            addParameterListProposal(file, proposals, node, rootNode, false);
        	addConstructorProposal(file, proposals, node, rootNode);
        	break;
//        case 1610:
//            addRemoveAnnotationDecProposal(proposals, "shared", project, node);
//            addRemoveAnnotationDecProposal(proposals, "abstract", project, node);
//            break;
//        case 1500:
//        case 1501:
//            addRemoveAnnotationDecProposal(proposals, "variable", project, node);
//            break;
//        case 1600:
//        case 1601:
//            addRemoveAnnotationDecProposal(proposals, "abstract", project, node);
//            break;
//        case 1700:
//            addRemoveAnnotationDecProposal(proposals, "final", project, node);
//            break;
//        case 1800:
//        case 1801:
//            addRemoveAnnotationDecProposal(proposals, "sealed", project, node);
//            break;
//        case 1900:
//            addRemoveAnnotationDecProposal(proposals, "late", project, node);
//            break;
//        case 1950:
//        case 1951:
//            addRemoveAnnotationDecProposal(proposals, "annotation", project, node);
//            break;
        case 2000:
            addCreateParameterProposals(rootNode, node, problem, proposals, project);
            break;
        case 2100:
            addAppendMemberReferenceProposals(rootNode, node, problem, proposals, file);
            addChangeTypeProposals(rootNode, node, problem, proposals, project);
            addSatisfiesProposals(rootNode, node, proposals, project);
            break;
        case 2102:
            addChangeTypeArgProposals(rootNode, node, problem, proposals, project);
            addSatisfiesProposals(rootNode, node, proposals, project);
            break;
        case 2101:
            addSpreadToSequenceParameterProposal(rootNode, node, proposals, file);
            break;
        case 2500:
            addTypeParameterProposal(file, rootNode, proposals, node);
            break;
        case 3000:
            CeylonEditor currentEditor = getCurrentCeylonEditor();
            addAssignToLocalProposal(currentEditor, rootNode, proposals, node, start);
            addDestructureProposal(currentEditor, rootNode, proposals, node, start);
            addAssignToForProposal(currentEditor, rootNode, proposals, node, start);
            addAssignToIfExistsProposal(currentEditor, rootNode, proposals, node, start);
            addAssignToAssertExistsProposal(currentEditor, rootNode, proposals, node, start);
            addAssignToIfNonemptyProposal(currentEditor, rootNode, proposals, node, start);
            addAssignToAssertNonemptyProposal(currentEditor, rootNode, proposals, node, start);
            addAssignToTryProposal(currentEditor, rootNode, proposals, node, start);
            addAssignToIfIsProposal(currentEditor, rootNode, proposals, node, start);
            addAssignToAssertIsProposal(currentEditor, rootNode, proposals, node, start);
            addPrintProposal(rootNode, proposals, node, start);
            break;
        case 3100:
            addShadowReferenceProposal(file, node, rootNode, proposals);
            break;
        case 3101:
        case 3102:
            addShadowSwitchReferenceProposal(file, node, rootNode, proposals);
            break;
        case 5001:
        case 5002:
            addChangeIdentifierCaseProposal(node, proposals, file);
            break;
        case 6000:
            addFixMultilineStringIndentation(proposals, file, rootNode, node);
            break;
        case 7000:
            addModuleImportProposals(proposals, project, tc, node);
            break;
        case 8000:
            addRenameDescriptorProposal(rootNode, context, problem, proposals, file);
            if (context.getSourceViewer()!=null) {
                addMoveDirProposal(file, rootNode, project, proposals, 
                        context);
            }
            break;
        case 9000:
            addChangeRefiningTypeProposal(file, rootNode, proposals, node);
            break;
        case 9100:
        case 9200:
            addChangeRefiningParametersProposal(file, rootNode, proposals, node);
            break;
        case 10000:
            addElseProposal(file, rootNode, proposals, node);
            addCasesProposal(file, rootNode, proposals, node);
            break;
        case 11000:
            addNamedArgumentsProposal(file, rootNode, proposals, node);
            break;
        case 12000:
        case 12100:
        	changeToVoid(file, rootNode, node, proposals);
        	break;
        case 13000:
        	changeToFunction(file, rootNode, node, proposals);
        	break;
//        case 20000:
//            addMakeNativeProposal(proposals, project, node, rootNode, file);
//            break;
        }
    }

	private void changeToFunction(IFile file, 
			Tree.CompilationUnit rootNode, Node node, 
			Collection<ICompletionProposal> proposals) {
		Tree.Declaration dec = 
				findDeclarationWithBody(rootNode, node);
		if (dec instanceof Tree.AnyMethod) {
			Tree.Return ret = (Tree.Return) node;
			Tree.AnyMethod m = (Tree.AnyMethod) dec;
			Tree.Type type = m.getType();
			if (type instanceof Tree.VoidModifier) {
				TextFileChange tfc = 
						new TextFileChange("Change To Function", 
								file);
				Unit unit = rootNode.getUnit();
				Type rt = 
						ret.getExpression()
							.getTypeModel();
				tfc.setEdit(new ReplaceEdit(
						type.getStartIndex(), 
						type.getDistance(), 
						isTypeUnknown(rt) ? "function" :
							rt.asSourceCodeString(unit)));
				proposals.add(new CorrectionProposal(
						"make function non-'void'", tfc, null));
			}
		}
	}

	private void changeToVoid(IFile file, 
			Tree.CompilationUnit rootNode, Node node, 
			Collection<ICompletionProposal> proposals) {
		Tree.Declaration dec = 
				findDeclarationWithBody(rootNode, node);
		if (dec instanceof Tree.AnyMethod) {
			Tree.AnyMethod m = (Tree.AnyMethod) dec;
			Tree.Type type = m.getType();
			if (!(type instanceof Tree.VoidModifier)) {
				TextFileChange tfc = 
						new TextFileChange("Change To Void", 
								file);
				tfc.setEdit(new ReplaceEdit(
						type.getStartIndex(), 
						type.getDistance(), 
						"void"));
				proposals.add(new CorrectionProposal(
						"make function 'void'", tfc, null));
			}
		}
	}

    private void addNamedArgumentsProposal(IFile file,
            Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        if (node instanceof Tree.NamedArgumentList) {
            TextFileChange tfc = 
                    new TextFileChange("Add Named Arguments", 
                            file);
            IDocument doc = EditorUtil.getDocument(tfc);
            tfc.setEdit(new MultiTextEdit());
            Tree.NamedArgumentList nal =
                    (Tree.NamedArgumentList) node;
            NamedArgumentList args = 
                    nal.getNamedArgumentList();
            int start = nal.getStartIndex();
            int stop = nal.getEndIndex()-1;
            int loc = start+1;
            String sep = " ";
            List<Tree.NamedArgument> nas = 
                    nal.getNamedArguments();
            if (!nas.isEmpty()) {
                Tree.NamedArgument last = 
                        nas.get(nas.size()-1);
                loc = last.getEndIndex();
                try {
                    int firstLine = 
                            doc.getLineOfOffset(start);
                    int lastLine = 
                            doc.getLineOfOffset(stop);
                    if (firstLine!=lastLine) {
                        sep = utilJ2C().indents().getDefaultLineDelimiter(doc) +
                                utilJ2C().indents().getIndent(last, doc);
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            ParameterList params = args.getParameterList();
            String result = null;
            boolean multipleResults = false;
            for (Parameter param: params.getParameters()) {
                if (!param.isDefaulted() &&
                    !args.getArgumentNames()
                        .contains(param.getName())) {
                    multipleResults = result!=null;
                    result = param.getName();
                    tfc.addEdit(new InsertEdit(loc, 
                            sep + param.getName() + 
                            " = nothing;"));
                }
            }
            if (loc==stop) {
                tfc.addEdit(new InsertEdit(stop, " "));
            }
            String name = multipleResults ?
                "Fill in missing named arguments" :
                "Fill in missing named argument '" 
                    + result + "'";
            proposals.add(new CorrectionProposal(name, tfc, 
                    new Region(loc, 0)));
        }
    }

    private void addElseProposal(IFile file, 
            Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        if (node instanceof Tree.SwitchClause) {
            Tree.Statement st = 
                    findStatement(rootNode, node);
            if (st instanceof Tree.SwitchStatement) {
                int offset = st.getEndIndex();
                TextFileChange tfc = 
                        new TextFileChange("Add Else", file);
                IDocument doc = getDocument(tfc);
                String text = 
                        utilJ2C().indents().getDefaultLineDelimiter(doc) +
                        utilJ2C().indents().getIndent(node, doc) +
                        "else {}";
                tfc.setEdit(new InsertEdit(offset, text));
                Region selection =
                        new Region(offset+text.length()-1, 0);
                proposals.add(new CorrectionProposal(
                        "Add 'else' clause", 
                        tfc, selection));
            }
            //TODO: else handle switch *expressions* 
        }
    }

    private void addCasesProposal(IFile file, 
            Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        if (node instanceof Tree.SwitchClause) {
            Tree.SwitchClause sc = (Tree.SwitchClause) node;
            Tree.Statement st = 
                    findStatement(rootNode, node);
            if (st instanceof Tree.SwitchStatement) {
                //TODO: handle switch expressions!
                Tree.SwitchStatement ss = 
                        (Tree.SwitchStatement) st;
                Tree.Expression e = 
                        sc.getSwitched()
                            .getExpression();
                if (e!=null) {
                    Type type = e.getTypeModel();
                    if (type!=null) {
                        Tree.SwitchCaseList scl = 
                                ss.getSwitchCaseList();
                        for (Tree.CaseClause cc: 
                                scl.getCaseClauses()) {
                            Tree.CaseItem item = 
                                    cc.getCaseItem();
                            if (item instanceof Tree.IsCase) {
                                Tree.IsCase ic = 
                                        (Tree.IsCase) item;
                                Tree.Type tn = ic.getType();
                                if (tn!=null) {
                                    Type t = 
                                            tn.getTypeModel();
                                    if (!isTypeUnknown(t)) {
                                        type = type.minus(t);
                                    }
                                }
                            }
                            else if (item instanceof Tree.MatchCase) {
                                Tree.MatchCase ic = 
                                        (Tree.MatchCase) item;
                                Tree.ExpressionList il = 
                                        ic.getExpressionList();
                                for (Tree.Expression ex: 
                                    il.getExpressions()) {
                                    if (ex!=null) {
                                        Type t = ex.getTypeModel();
                                        if (t!=null && 
                                                !isTypeUnknown(t)) {
                                            type = type.minus(t);
                                        }
                                    }
                                }
                            }
                        }
                        TextFileChange tfc = 
                                new TextFileChange(
                                        "Add Cases", file);
                        IDocument doc = getDocument(tfc);
                        String text = "";
                        List<Type> list;
                        List<Type> cts = type.getCaseTypes();
                        if (cts!=null) {
                            list = cts;
                        }
                        else {
                            list = singletonList(type);
                        }
                        for (Type pt: list) {
                            String is = 
                                    pt.getDeclaration()
                                        .isAnonymous() ? 
                                    "" : "is ";
                            Unit unit = rootNode.getUnit();
                        text += utilJ2C().indents().getDefaultLineDelimiter(doc) +
                                utilJ2C().indents().getIndent(node, doc) +
                                    "case (" +
                                    is + 
                                    pt.asString(unit) +
                                    ") {}"; 
                        }
                        int offset = ss.getEndIndex();
                        tfc.setEdit(new InsertEdit(offset, text));
                        proposals.add(new CorrectionProposal(
                                "Add missing 'case' clauses", tfc, 
                                new Region(offset+text.length()-1, 0)));
                    }
                }
            }
        }        
    }

    void addTypeParameterProposal(IFile file, 
            Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        Tree.TypeConstraint tcn = (Tree.TypeConstraint) node;
        TypeParameter tp = tcn.getDeclarationModel();
        Tree.Declaration decNode = 
                (Tree.Declaration) 
                    getReferencedNodeInUnit(
                            tp.getDeclaration(), 
                            rootNode);
        Tree.TypeParameterList tpl;
        if (decNode instanceof Tree.ClassOrInterface) {
            Tree.ClassOrInterface ci = 
                    (Tree.ClassOrInterface) decNode;
            tpl = ci.getTypeParameterList();
        }
        else if (decNode instanceof Tree.AnyMethod) {
            Tree.AnyMethod am = (Tree.AnyMethod) decNode;
            tpl = am.getTypeParameterList();
        }
        else if (decNode instanceof Tree.TypeAliasDeclaration) {
            Tree.TypeAliasDeclaration ad = 
                    (Tree.TypeAliasDeclaration) decNode;
            tpl = ad.getTypeParameterList();
        }
        else {
            return;
        }
        TextFileChange tfc = 
                new TextFileChange("Add Type Parameter", file);
        InsertEdit edit;
        if (tpl==null) {
            Tree.Identifier id = decNode.getIdentifier();
            edit = new InsertEdit(id.getEndIndex(),
                    "<" + tp.getName() + ">");
        }
        else {
            edit = new InsertEdit(tpl.getEndIndex()-1,
                    ", " + tp.getName());
        }
        tfc.setEdit(edit);
        proposals.add(new CorrectionProposal(
                "Add '" + tp.getName() +
                "' to type parameter list of '" + 
                decNode.getDeclarationModel().getName() + "'", 
                tfc, null));
    }

    private void addProposals(
            IQuickAssistInvocationContext context,
            CeylonEditor editor,
            Collection<ICompletionProposal> proposals) {
        if (editor==null) return;
        
        IDocument doc = context.getSourceViewer().getDocument();
        IEditorInput input = editor.getEditorInput();
        IProject project = EditorUtil.getProject(input);
        IFile file = EditorUtil.getFile(input);
        IRegion selection = editor.getSelection();
        CeylonParseController parseController =
                editor.getParseController();
        Tree.CompilationUnit rootNode = 
                parseController.getTypecheckedRootNode();
        if (rootNode!=null) {
            int start = context.getOffset();
            int len = context.getLength();
            int end = start + (len>0?len:0); //len==-1 means missing info
            Node node =
                    findNode(rootNode,
                            parseController.getTokens(),
                            start, end);
            int currentOffset = selection.getOffset();
            
            CeylonEditor currentEditor = getCurrentCeylonEditor();

            addAssignToLocalProposal(currentEditor, rootNode, proposals, node, currentOffset);
            addDestructureProposal(currentEditor, rootNode, proposals, node, currentOffset);
            addAssignToForProposal(currentEditor, rootNode, proposals, node, currentOffset);
            addAssignToIfExistsProposal(currentEditor, rootNode, proposals, node, currentOffset);
            addAssignToAssertExistsProposal(currentEditor, rootNode, proposals, node, currentOffset);
            addAssignToIfNonemptyProposal(currentEditor, rootNode, proposals, node, currentOffset);
            addAssignToAssertNonemptyProposal(currentEditor, rootNode, proposals, node, currentOffset);
            addAssignToTryProposal(currentEditor, rootNode, proposals, node, currentOffset);
            addAssignToIfIsProposal(currentEditor, rootNode, proposals, node, currentOffset);
            addAssignToAssertIsProposal(currentEditor, rootNode, proposals, node, currentOffset);
            addPrintProposal(rootNode, proposals, node, currentOffset);
            
            addConvertToNamedArgumentsProposal(proposals, file, rootNode, 
                    editor, currentOffset);
            addConvertToPositionalArgumentsProposal(proposals, file, rootNode, 
                    editor, currentOffset);
            
            Tree.Statement statement = findStatement(rootNode, node);
            Tree.Declaration declaration = findDeclaration(rootNode, node);
            Tree.NamedArgument argument = findArgument(rootNode, node);
            Tree.ImportMemberOrType imp = findImport(rootNode, node);
            Tree.OperatorExpression oe = findOperator(rootNode, node);
            
            addOperatorProposals(proposals, file, oe);
            addParenthesesProposals(proposals, file, node, rootNode, oe);

            addVerboseRefinementProposal(proposals, file, statement, rootNode);
            
            addAnnotationProposals(proposals, project, declaration,
                    doc, currentOffset);
            addTypingProposals(proposals, file, rootNode, node, declaration, editor);
            
            addAnonymousFunctionProposals(editor, proposals, doc, file, rootNode, 
                    currentOffset);
            
            addDeclarationProposals(editor, proposals, doc, file, rootNode, 
                    declaration, currentOffset);
            
            addAssignToFieldProposal(file, statement, declaration, proposals);

            addChangeToIfProposal(proposals, doc, file, rootNode, statement);
            
            addConvertToDefaultConstructorProposal(proposals, doc, file, rootNode, statement);
            
            addConvertToClassProposal(proposals, declaration, editor);
            addAssertExistsDeclarationProposals(proposals, doc, file, rootNode, declaration);
            addSplitDeclarationProposals(proposals, doc, file, rootNode, declaration, statement);
            addJoinDeclarationProposal(proposals, rootNode, statement, file);
            addParameterProposals(proposals, file, rootNode, declaration);
            
            addArgumentProposals(proposals, doc, file, argument);
            addUseAliasProposal(imp, proposals, editor);
            addRenameAliasProposal(imp, proposals, editor);
            addRemoveAliasProposal(imp, proposals, file, editor);            
            addRenameVersionProposals(node, proposals, rootNode, editor);
            
            addConvertToIfElseProposal(doc, proposals, file, statement);
            addConvertToThenElseProposal(rootNode, doc, proposals, file, statement);
            addInvertIfElseProposal(doc, proposals, file, statement, node, rootNode);
            
            addConvertSwitchToIfProposal(proposals, doc, file, statement);
            addConvertIfToSwitchProposal(proposals, doc, file, statement);
            
            addSplitIfStatementProposal(proposals, doc, file, statement);
            addJoinIfStatementsProposal(proposals, doc, file, statement);
            
            addConvertGetterToFunctionProposal(proposals, editor, statement);
            addConvertFunctionToGetterProposal(proposals, editor, statement);
            
            addThrowsAnnotationProposal(proposals, statement, rootNode, file, doc);            

            addRefineFormalMembersProposal(proposals, node, rootNode, false);
            addRefineEqualsHashProposal(proposals, node, rootNode);
            
            addConvertToVerbatimProposal(proposals, file, rootNode, node, doc);
            addConvertFromVerbatimProposal(proposals, file, rootNode, node, doc);
            addConvertToConcatenationProposal(proposals, file, rootNode, node, doc);
            addConvertToInterpolationProposal(proposals, file, rootNode, node, doc);
            
            addExpandTypeProposal(editor, statement, file, doc, proposals);

            RenameProposal.add(proposals, editor);
            InlineDeclarationProposal.add(proposals, editor);
            ChangeParametersProposal.add(proposals, editor);
            ExtractValueProposal.add(proposals, editor, node);
            ExtractFunctionProposal.add(proposals, editor, node);
            ExtractParameterProposal.add(proposals, editor, node);
            CollectParametersProposal.add(proposals, editor);
            MoveOutProposal.add(proposals, editor, node);
            MakeReceiverProposal.add(proposals, editor, node);
            InvertBooleanProposal.add(proposals, editor);

            MoveToNewUnitProposal.add(proposals, editor);
            MoveToUnitProposal.add(proposals, editor);

        }
        
    }

    private static void addOperatorProposals(
            Collection<ICompletionProposal> proposals,
            IFile file,
            Tree.OperatorExpression oe) {
        if (oe instanceof Tree.BinaryOperatorExpression) {
            Tree.BinaryOperatorExpression boe =
                    (Tree.BinaryOperatorExpression) oe;
            addReverseOperatorProposal(proposals, file, boe);
            addInvertOperatorProposal(proposals, file, boe);
            addSwapBinaryOperandsProposal(proposals, file, boe);
        }
    }

    private void addAnnotationProposals(
            Collection<ICompletionProposal> proposals,
            IProject project, Tree.Declaration decNode,
            IDocument doc, int offset) {
        if (decNode!=null) {
            try {
                Node in = getIdentifyingNode(decNode);
                if (in==null ||
                        doc.getLineOfOffset(in.getStartIndex())!=
                                doc.getLineOfOffset(offset)) {
                    return;
                }
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
            Declaration d = decNode.getDeclarationModel();
            if (d!=null) {
                if (decNode instanceof Tree.AttributeDeclaration) {
                    addMakeVariableDecProposal(proposals, project, decNode);
                }
                if ((d.isClassOrInterfaceMember()||d.isToplevel()) && 
                        !d.isShared()) {
                    addMakeSharedDecProposal(proposals, project, decNode);
                }
                if (d.isClassOrInterfaceMember() &&
                        !d.isDefault() && !d.isFormal()) {
                    if (decNode instanceof Tree.AnyClass) {
                        addMakeDefaultDecProposal(proposals, project, decNode);
                    }
                    else if (decNode instanceof Tree.AnyAttribute) {
                        addMakeDefaultDecProposal(proposals, project, decNode);
                    }
                    else if (decNode instanceof Tree.AnyMethod) {
                        addMakeDefaultDecProposal(proposals, project, decNode);
                    }
                    if (decNode instanceof Tree.ClassDefinition) {
                        addMakeFormalDecProposal(proposals, project, decNode);
                    }
                    else if (decNode instanceof Tree.AttributeDeclaration) {
                        AttributeDeclaration ad = (Tree.AttributeDeclaration) decNode;
                        if (ad.getSpecifierOrInitializerExpression()==null) {
                            addMakeFormalDecProposal(proposals, project, decNode);
                        }
                    }
                    else if (decNode instanceof Tree.MethodDeclaration) {
                        MethodDeclaration md = (Tree.MethodDeclaration) decNode;
                        if (md.getSpecifierExpression()==null) {
                            addMakeFormalDecProposal(proposals, project, decNode);
                        }
                    }
                }
            }
        }
    }
    
    private static void addAnonymousFunctionProposals(
            CeylonEditor editor,
            Collection<ICompletionProposal> proposals,
            IDocument doc, IFile file,
            Tree.CompilationUnit rootNode,
            final int currentOffset) {
        class FindAnonFunctionVisitor extends Visitor {
            Tree.FunctionArgument result;
            public void visit(Tree.FunctionArgument that) {
                if (currentOffset>=that.getStartIndex() &&
                    currentOffset<=that.getEndIndex()) {
                    result = that;
                }
                super.visit(that);
            }
        }
        FindAnonFunctionVisitor v = new FindAnonFunctionVisitor();
        v.visit(rootNode);
        Tree.FunctionArgument fun = v.result;
        if (fun!=null) {
            if (fun.getExpression()!=null) {
                addConvertToBlockProposal(doc, proposals, file, fun);
            }
            if (fun.getBlock()!=null) {
                addConvertToSpecifierProposal(doc, proposals, file, 
                        fun.getBlock(), true);
            }
        }
    }

    private static void addDeclarationProposals(
            CeylonEditor editor,
            Collection<ICompletionProposal> proposals,
            IDocument doc, IFile file,
            Tree.CompilationUnit rootNode,
            Tree.Declaration decNode,
            int currentOffset) {
        
        if (decNode==null) return;
        
        if (decNode.getAnnotationList()!=null) {
            Integer endIndex =
                    decNode.getAnnotationList().getEndIndex();
            if (endIndex!=null && currentOffset<=endIndex) {
                return;
            }
        }
        if (decNode instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration tdn = 
                    (Tree.TypedDeclaration) decNode;
            if (tdn.getType()!=null) {
                Integer endIndex = tdn.getType().getEndIndex();
                if (endIndex!=null && currentOffset<=endIndex) {
                    return;
                }
            }
        }
            
        if (decNode instanceof Tree.AttributeDeclaration) {
            Tree.AttributeDeclaration attDecNode = 
                    (Tree.AttributeDeclaration) decNode;
            Tree.SpecifierOrInitializerExpression se = 
                    attDecNode.getSpecifierOrInitializerExpression(); 
            if (se instanceof Tree.LazySpecifierExpression) {
                addConvertToBlockProposal(doc, proposals, file, decNode);
            }
            else {
                addConvertToGetterProposal(doc, proposals, file, attDecNode);
            }
        }
        if (decNode instanceof Tree.MethodDeclaration) {
            Tree.MethodDeclaration methodDecNode = 
                    (Tree.MethodDeclaration) decNode;
            Tree.SpecifierOrInitializerExpression se = 
                    methodDecNode.getSpecifierExpression(); 
            if (se instanceof Tree.LazySpecifierExpression) {
                addConvertToBlockProposal(doc, proposals, file, decNode);
            }
        }
        if (decNode instanceof Tree.AttributeSetterDefinition) {
            Tree.AttributeSetterDefinition setterDefNode = 
                    (Tree.AttributeSetterDefinition) decNode;
            Tree.SpecifierOrInitializerExpression se = 
                    setterDefNode.getSpecifierExpression();
            if (se instanceof Tree.LazySpecifierExpression) {
                addConvertToBlockProposal(doc, proposals, file, decNode);
            }
            Tree.Block b = setterDefNode.getBlock(); 
            if (b!=null) {
                addConvertToSpecifierProposal(doc, proposals, file, b);
            }
        }
        if (decNode instanceof Tree.AttributeGetterDefinition) {
            Tree.AttributeGetterDefinition getterDefNode = 
                    (Tree.AttributeGetterDefinition) decNode;
            Tree.Block b = getterDefNode.getBlock(); 
            if (b!=null) {
                addConvertToSpecifierProposal(doc, proposals, file, b);
            }
        }
        if (decNode instanceof Tree.MethodDefinition) {
            Tree.MethodDefinition methodDefNode = 
                    (Tree.MethodDefinition) decNode;
            Tree.Block b = methodDefNode.getBlock(); 
            if (b!=null) {
                addConvertToSpecifierProposal(doc, proposals, file, b);
            }
        }
        
    }

	private void addArgumentProposals(
	        Collection<ICompletionProposal> proposals,
            IDocument doc, IFile file,
            Tree.StatementOrArgument node) {
        if (node instanceof Tree.MethodArgument) {
            Tree.MethodArgument ma =
                    (Tree.MethodArgument) node;
            Tree.SpecifierOrInitializerExpression se = 
                    ma.getSpecifierExpression(); 
            if (se instanceof Tree.LazySpecifierExpression) {
                addConvertToBlockProposal(doc, proposals, file, node);
            }
            Tree.Block b = ma.getBlock(); 
            if (b!=null) {
                addConvertToSpecifierProposal(doc, proposals, file, b);
            }
        }
        if (node instanceof Tree.AttributeArgument) {
            Tree.AttributeArgument aa =
                    (Tree.AttributeArgument) node;
            Tree.SpecifierOrInitializerExpression se = 
                    aa.getSpecifierExpression(); 
            if (se instanceof Tree.LazySpecifierExpression) {
                addConvertToBlockProposal(doc, proposals, file, node);
            }
            Tree.Block b = aa.getBlock(); 
            if (b!=null) {
                addConvertToSpecifierProposal(doc, proposals, file, b);
            }
        }
        if (node instanceof Tree.SpecifiedArgument) {
            Tree.SpecifiedArgument sa =
                    (Tree.SpecifiedArgument) node;
            addFillInArgumentNameProposal(proposals, doc, file, sa);
        }
    }

    private void addCreationProposals(
            Tree.CompilationUnit cu, 
            final Node node, 
            ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IProject project,
            IFile file) {
        if (node instanceof Tree.MemberOrTypeExpression) {
            addCreateProposals(cu, node, proposals, project, file);
        }
        else if (node instanceof Tree.SimpleType) {
            class FindExtendedTypeExpressionVisitor extends Visitor {
                Tree.InvocationExpression invocationExpression;
                @Override
                public void visit(Tree.ExtendedType that) {
                    super.visit(that);
                    if (that.getType()==node) {
                        invocationExpression = 
                                that.getInvocationExpression();
                    }
                }
            }
            FindExtendedTypeExpressionVisitor v = 
                    new FindExtendedTypeExpressionVisitor();
            v.visit(cu);
            if (v.invocationExpression!=null) {
                addCreateProposals(cu, 
                        v.invocationExpression.getPrimary(), 
                        proposals, project, file);
            }
        }
        //TODO: should we add this stuff back in??
        /*else if (node instanceof Tree.BaseType) {
            Tree.BaseType bt = (Tree.BaseType) node;
            String brokenName = bt.getIdentifier().getText();
            String idef = "interface " + brokenName + " {}";
            String idesc = "interface '" + brokenName + "'";
            String cdef = "class " + brokenName + "() {}";
            String cdesc = "class '" + brokenName + "()'";
            //addCreateLocalProposals(proposals, project, idef, idesc, INTERFACE, cu, bt);
            addCreateLocalProposals(proposals, project, cdef, cdesc, CLASS, cu, bt, null, null);
            addCreateToplevelProposals(proposals, project, idef, idesc, INTERFACE, cu, bt, null, null);
            addCreateToplevelProposals(proposals, project, cdef, cdesc, CLASS, cu, bt, null, null);
            CreateInNewUnitProposal.addCreateToplevelProposal(proposals, idef, idesc, 
                    INTERFACE, file, brokenName, null, null);
            CreateInNewUnitProposal.addCreateToplevelProposal(proposals, cdef, cdesc, 
                    CLASS, file, brokenName, null, null);
            
        }*/
        if (node instanceof Tree.BaseType) {
            Tree.BaseType bt = (Tree.BaseType) node;
            Tree.Identifier id = bt.getIdentifier();
            if (id!=null) {
                String brokenName = id.getText();
                addCreateTypeParameterProposal(proposals,
                        project, cu, bt, brokenName);
            }
        }
    }

    public void collectWarningSuppressions(
            CeylonAnnotation annotation,
            IQuickAssistInvocationContext context,
            ProblemLocation location, 
            Collection<ICompletionProposal> proposals) {
        if (annotation.getSeverity()==IMarker.SEVERITY_WARNING) {
            Tree.CompilationUnit rootNode = getRootNode();
            if (rootNode == null) {
                return;
            }
            Tree.StatementOrArgument target =
                    findAnnotatable(rootNode,
                            findNode(rootNode, null,
                                    location.getOffset(),
                                    location.getOffset() +
                                    location.getLength()));
            if (target==null) {
                return;
            }

            IEditorInput ei = editor.getEditorInput();
            IFile file = EditorUtil.getFile(ei);
            IDocument doc =
                    context.getSourceViewer()
                        .getDocument();
            TextFileChange change = 
                    new TextFileChange("Suppress Warnings",
                            file);
            final StringBuilder sb = new StringBuilder();
            final StyledString ss = 
                    new StyledString("Suppress warnings of type ");
            target.visit(new CollectWarningsToSuppressVisitor(sb, ss));
            String ws = 
                    utilJ2C().indents().getDefaultLineDelimiter(doc) +
                    utilJ2C().indents().getIndent(target, doc);
            String text = "suppressWarnings(" + sb + ")";
            Integer start = target.getStartIndex();
            Tree.AnnotationList al = annotationList(target);
            if (al == null) {
                text += ws;
            }
            else {
                Tree.AnonymousAnnotation aa =
                        al.getAnonymousAnnotation();
                if (aa!=null) {
                    start = aa.getEndIndex();
                    text = ws + text;
                }
                else {
                    text += ws;
                }
            }
            change.setEdit(new InsertEdit(start, text));
            proposals.add(new CorrectionProposal(ss.toString(), 
                    change, new Region(start+text.length(), 0), 
                    CeylonResources.SUPPRESS_WARNING) {
                @Override
                public StyledString getStyledDisplayString() {
                    return ss;
                }
            });
            proposals.add(new ConfigureWarningsProposal(editor));
        }

    }

    private static Tree.StatementOrArgument findAnnotatable(
            Tree.CompilationUnit rootNode, final Node node) {
        class FindAnnotatableVisitor extends Visitor {
            Tree.StatementOrArgument result;
            private Tree.StatementOrArgument current;
            @Override
            public void visit(Tree.Declaration that) {
                Tree.StatementOrArgument outer = current;
                current = that;
                super.visit(that);
                current = outer;
            }
            @Override
            public void visit(Tree.ModuleDescriptor that) {
                Tree.StatementOrArgument outer = current;
                current = that;
                super.visit(that);
                current = outer;
            }
            @Override
            public void visit(Tree.PackageDescriptor that) {
                Tree.StatementOrArgument outer = current;
                current = that;
                super.visit(that);
                current = outer;
            }
            @Override
            public void visitAny(Node that) {
                if (that == node) {
                    result = current;
                }
                if (result==null) {
                    super.visitAny(that);
                }
            }
        }
        FindAnnotatableVisitor fav =
                new FindAnnotatableVisitor();
        fav.visit(rootNode);
        Tree.StatementOrArgument target = fav.result;
        return target;
    }

    private static Tree.AnnotationList annotationList(Node node) {
        if (node instanceof Tree.Declaration) {
            Tree.Declaration dec =
                    (Tree.Declaration) node;
            return dec.getAnnotationList();
        }
        else if (node instanceof Tree.ModuleDescriptor) {
            Tree.ModuleDescriptor dec =
                    (Tree.ModuleDescriptor) node;
            return dec.getAnnotationList();
        }
        else if (node instanceof Tree.PackageDescriptor) {
            Tree.PackageDescriptor dec =
                    (Tree.PackageDescriptor) node;
            return dec.getAnnotationList();
        }
        else if (node instanceof Tree.ImportModule) {
            Tree.ImportModule dec =
                    (Tree.ImportModule) node;
            return dec.getAnnotationList();
        }
        else {
            return null;
        }
    }

}
