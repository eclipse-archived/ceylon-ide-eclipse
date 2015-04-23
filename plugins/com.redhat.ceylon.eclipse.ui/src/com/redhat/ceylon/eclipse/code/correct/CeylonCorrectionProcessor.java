package com.redhat.ceylon.eclipse.code.correct;

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

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeAbstractDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeActualDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeContainerAbstractProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeDefaultDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeDefaultProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeFormalDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeRefinedSharedProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeSharedDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeSharedProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeSharedProposalForSupertypes;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeVariableDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal.addMakeVariableProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddConstructorProposal.addConstructorProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddEmptyParameterListProposal.addEmptyParameterListProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddInitializerProposal.addInitializerProposals;
import static com.redhat.ceylon.eclipse.code.correct.AddModuleImportProposal.addModuleImportProposals;
import static com.redhat.ceylon.eclipse.code.correct.AddParameterListProposal.addParameterListProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddParameterProposal.addParameterProposals;
import static com.redhat.ceylon.eclipse.code.correct.AddSatisfiesProposal.addSatisfiesProposals;
import static com.redhat.ceylon.eclipse.code.correct.AddSpreadToVariadicParameterProposal.addEllipsisToSequenceParameterProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddThrowsAnnotationProposal.addThrowsAnnotationProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssertExistsDeclarationProposal.addAssertExistsDeclarationProposals;
import static com.redhat.ceylon.eclipse.code.correct.AssignToForProposal.addAssignToForProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToIfExistsProposal.addAssignToIfExistsProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToIfIsProposal.addAssignToIfIsProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToIfNonemptyProposal.addAssignToIfNonemptyProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToLocalProposal.addAssignToLocalProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToTryProposal.addAssignToTryProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeDeclarationProposal.addChangeDeclarationProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeInitialCaseOfIdentifierInDeclaration.addChangeIdentifierCaseProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeReferenceProposal.addChangeArgumentReferenceProposals;
import static com.redhat.ceylon.eclipse.code.correct.ChangeReferenceProposal.addChangeReferenceProposals;
import static com.redhat.ceylon.eclipse.code.correct.ChangeRefiningTypeProposal.addChangeRefiningParametersProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeRefiningTypeProposal.addChangeRefiningTypeProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeToIfProposal.addChangeToIfProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeTypeProposal.addChangeTypeArgProposals;
import static com.redhat.ceylon.eclipse.code.correct.ChangeTypeProposal.addChangeTypeProposals;
import static com.redhat.ceylon.eclipse.code.correct.ConvertGetterToMethodProposal.addConvertGetterToMethodProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertIfElseToThenElse.addConvertToThenElseProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertMethodToGetterProposal.addConvertMethodToGetterProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertStringProposal.addConvertFromVerbatimProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertStringProposal.addConvertToVerbatimProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertThenElseToIfElse.addConvertToIfElseProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToBlockProposal.addConvertToBlockProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToClassProposal.addConvertToClassProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToConcatenationProposal.addConvertToConcatenationProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToGetterProposal.addConvertToGetterProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToInterpolationProposal.addConvertToInterpolationProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToNamedArgumentsProposal.addConvertToNamedArgumentsProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToPositionalArgumentsProposal.addConvertToPositionalArgumentsProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToSpecifierProposal.addConvertToSpecifierProposal;
import static com.redhat.ceylon.eclipse.code.correct.CreateEnumProposal.addCreateEnumProposal;
import static com.redhat.ceylon.eclipse.code.correct.CreateParameterProposal.addCreateParameterProposals;
import static com.redhat.ceylon.eclipse.code.correct.CreateProposal.addCreateProposals;
import static com.redhat.ceylon.eclipse.code.correct.CreateTypeParameterProposal.addCreateTypeParameterProposal;
import static com.redhat.ceylon.eclipse.code.correct.DeclareLocalProposal.addDeclareLocalProposal;
import static com.redhat.ceylon.eclipse.code.correct.DestructureProposal.addDestructureProposal;
import static com.redhat.ceylon.eclipse.code.correct.ExpandTypeProposal.addExpandTypeProposal;
import static com.redhat.ceylon.eclipse.code.correct.ExportModuleImportProposal.addExportModuleImportProposal;
import static com.redhat.ceylon.eclipse.code.correct.ExportModuleImportProposal.addExportModuleImportProposalForSupertypes;
import static com.redhat.ceylon.eclipse.code.correct.FillInArgumentNameProposal.addFillInArgumentNameProposal;
import static com.redhat.ceylon.eclipse.code.correct.FixAliasProposal.addFixAliasProposal;
import static com.redhat.ceylon.eclipse.code.correct.FixMultilineStringIndentationProposal.addFixMultilineStringIndentation;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.addImportProposals;
import static com.redhat.ceylon.eclipse.code.correct.InvertIfElseProposal.addReverseIfElseProposal;
import static com.redhat.ceylon.eclipse.code.correct.JoinDeclarationProposal.addJoinDeclarationProposal;
import static com.redhat.ceylon.eclipse.code.correct.JoinIfStatementsProposal.addJoinIfStatementsProposal;
import static com.redhat.ceylon.eclipse.code.correct.MoveDirProposal.addMoveDirProposal;
import static com.redhat.ceylon.eclipse.code.correct.PrintProposal.addPrintProposal;
import static com.redhat.ceylon.eclipse.code.correct.RefineFormalMembersProposal.addRefineFormalMembersProposal;
import static com.redhat.ceylon.eclipse.code.correct.RemoveAliasProposal.addRemoveAliasProposal;
import static com.redhat.ceylon.eclipse.code.correct.RemoveAnnotionProposal.addMakeContainerNonfinalProposal;
import static com.redhat.ceylon.eclipse.code.correct.RemoveAnnotionProposal.addRemoveAnnotationDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.RemoveAnnotionProposal.addRemoveAnnotationProposal;
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
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CONFIG_WARNING;
import static com.redhat.ceylon.eclipse.util.AnnotationUtils.getAnnotationsForLine;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Highlights.STRING_STYLER;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.Nodes.findArgument;
import static com.redhat.ceylon.eclipse.util.Nodes.findBinaryOperator;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.findImport;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeEndOffset;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNodeInUnit;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.preferences.CeylonWarningsPropertiesPage;
import com.redhat.ceylon.eclipse.core.builder.MarkerCreator;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Indents;
import com.redhat.ceylon.eclipse.util.MarkerUtils;
import com.redhat.ceylon.eclipse.util.Nodes;

public class CeylonCorrectionProcessor extends QuickAssistAssistant 
        implements IQuickAssistProcessor {
    
    private CeylonEditor editor; //may only be used for quick assists!!!
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
                    for (IPackageFragmentRoot pfr: javaProject.getPackageFragmentRoots()) {
                        if (pfr.getPath().isPrefixOf(file.getFullPath())) {
                            IPath relPath = file.getFullPath().makeRelativeTo(pfr.getPath());
                            model = tc.getPhasedUnitFromRelativePath(relPath.toString())
                                    .getCompilationUnit();    
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
        if (editor!=null && 
                editor.getEditorInput() instanceof FileEditorInput) {
            FileEditorInput input = (FileEditorInput) editor.getEditorInput();
            if (input!=null) {
                return input.getFile();
            }
        }
        return file;
    }
    
    private Tree.CompilationUnit getRootNode() {
        if (editor!=null) {
            return editor.getParseController().getRootNode();
        }
        else if (model!=null) {
            return (Tree.CompilationUnit) model;
        }
        else {
            return null;
        }
    }
    
    @Override
    public String getErrorMessage() {
        return null;
    }
    
    private void collectProposals(IQuickAssistInvocationContext context,
            IAnnotationModel model, Collection<Annotation> annotations,
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
        }
        if (problems.isEmpty() && addQuickFixes) {
             for (Annotation curr: annotations) {
                 if (curr instanceof SimpleMarkerAnnotation) {
                     SimpleMarkerAnnotation sma = 
                             (SimpleMarkerAnnotation) curr;
                    collectMarkerProposals(sma, proposals);
                 }                 
             }
        }

        ProblemLocation[] problemLocations =
                problems.toArray(new ProblemLocation[problems.size()]);
        Arrays.sort(problemLocations);
        if (addQuickFixes) {
            collectCorrections(context, problemLocations, proposals);
        }
        if (addQuickAssists) {
            collectAssists(context, problemLocations, proposals);
        }
        if (addQuickFixes) {
            addSuppressWarningsProposal(context, model, annotations, proposals);
        }
    }

    public void addSuppressWarningsProposal(
            IQuickAssistInvocationContext context, IAnnotationModel model,
            Collection<Annotation> annotations,
            Collection<ICompletionProposal> proposals) {
        for (Annotation curr: annotations) {
            if (curr instanceof CeylonAnnotation) {
                CeylonAnnotation ca = (CeylonAnnotation) curr;
                if (ca.getSeverity()==IMarker.SEVERITY_WARNING) {
                    ProblemLocation problemLocation = 
                            getProblemLocation(ca, model);
                    if (problemLocation != null) {
                        collectAnnotationCorrections(ca, context, 
                                problemLocation, proposals);
                        break;
                    }
                }
            }
        }
    }

    private static ProblemLocation getProblemLocation(CeylonAnnotation annotation, 
            IAnnotationModel model) {
        int problemId = annotation.getId();
        if (problemId != -1) {
            Position pos = model.getPosition((Annotation) annotation);
            if (pos != null) {
                return new ProblemLocation(pos.getOffset(), pos.getLength(),
                        annotation); // java problems all handled by the quick assist processors
            }
        }
        return null;
    }

    private void collectAssists(IQuickAssistInvocationContext context,
            ProblemLocation[] locations, Collection<ICompletionProposal> proposals) {
        if (proposals.isEmpty()) {
            addProposals(context, editor, proposals);
        }
    }

    private static void collectMarkerProposals(SimpleMarkerAnnotation annotation, 
            Collection<ICompletionProposal> proposals) {
        IMarker marker = annotation.getMarker();
        IMarkerResolution[] res = IDE.getMarkerHelpRegistry().getResolutions(marker);
        if (res.length > 0) {
            for (int i = 0; i < res.length; i++) {
                proposals.add(new CeylonMarkerResolutionProposal(res[i], marker));
            }
        }
    }

    @Override
    public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext context) {
        ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        ISourceViewer viewer = context.getSourceViewer();
        List<Annotation> annotations = 
                getAnnotationsForLine(viewer, getLine(context, viewer));
        collectProposals(context, viewer.getAnnotationModel(),
                annotations, true, true, proposals);
        return proposals.toArray(new ICompletionProposal[proposals.size()]);
    }

    private int getLine(IQuickAssistInvocationContext context, ISourceViewer viewer) {
        try {
            return viewer.getDocument().getLineOfOffset(context.getOffset());
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public void collectCorrections(IQuickAssistInvocationContext context,
            ProblemLocation location, Collection<ICompletionProposal> proposals) {
        Tree.CompilationUnit rootNode = getRootNode();
        if (rootNode!=null) {
            addProposals(context, location, getFile(), 
                    rootNode, proposals);
        }
    }
    
    private void collectCorrections(IQuickAssistInvocationContext context,
            ProblemLocation[] locations, Collection<ICompletionProposal> proposals) {
        ISourceViewer viewer = context.getSourceViewer();
        Tree.CompilationUnit rootNode = getRootNode();
        for (int i=locations.length-1; i>=0; i--) {
            ProblemLocation loc = locations[i];
            if (loc.getOffset()<=viewer.getSelectedRange().x) {
                for (int j=i; j>=0; j--) {
                    ProblemLocation location = locations[j];
                    if (location.getOffset()!=loc.getOffset()) {
                        break;
                    }
                    addProposals(context, location, getFile(), 
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
                addProposals(context, location, getFile(), 
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
            if (marker.getType().equals(PROBLEM_MARKER_ID)) {
                return marker.getAttribute(MarkerCreator.ERROR_CODE_KEY,0)>0;
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
            CeylonAnnotation ca = (CeylonAnnotation) annotation;
            return ca.getId()>0 || 
                    ca.getSeverity() == IMarker.SEVERITY_WARNING;
        }
        else if (annotation instanceof MarkerAnnotation) {
            return canFix(((MarkerAnnotation) annotation).getMarker());
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
        return CeylonSourcePositionLocator.findNode(cu, context.getOffset(), 
                context.getOffset()+context.getLength()) instanceof Tree.Term;*/
        return true;
    }
    
    private void addProposals(IQuickAssistInvocationContext context, 
            ProblemLocation problem, IFile file, 
            Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals) {
        if (file==null) return;
        IProject project = file.getProject();
        TypeChecker tc = getProjectTypeChecker(project);
        int start = problem.getOffset();
        int end = start + problem.getLength();
        Node node = findNode(rootNode, start, end);
        switch (problem.getProblemId()) {
        case 100:
            addDeclareLocalProposal(rootNode, node, proposals, file, editor);
            //fall through:
        case 102:
            if (tc!=null) {
                addImportProposals(rootNode, node, proposals, file);
            }
            addCreateEnumProposal(rootNode, node, problem, proposals, 
                    project, tc, file);
            addCreationProposals(rootNode, node, problem, proposals, 
                    project, tc, file);
            if (tc!=null) {
                addChangeReferenceProposals(rootNode, node, problem, proposals, file);
            }
            break;
        case 101:
            addCreateParameterProposals(rootNode, node, problem, proposals, 
                    project, tc, file);
            if (tc!=null) {
                addChangeArgumentReferenceProposals(rootNode, node, problem, proposals, file);
            }
            break;
        case 200:
            addSpecifyTypeProposal(rootNode, node, proposals, null);
            break;
        case 300:
            addRefineFormalMembersProposal(proposals, node, rootNode, false);
            addMakeAbstractDecProposal(proposals, project, node);
            break;
        case 350:
            addRefineFormalMembersProposal(proposals, node, rootNode, true);
            addMakeAbstractDecProposal(proposals, project, node);
            break;
        case 310:
            addMakeAbstractDecProposal(proposals, project, node);
            break;
        case 320:
            addRemoveAnnotationProposal(node, "formal", proposals, project);
            break;
        case 400:
        case 401:
        case 402:
            addMakeSharedProposal(proposals, project, node);
            break;
        case 500:
        case 510:
            addMakeDefaultProposal(proposals, project, node);
            break;
        case 600:
            addMakeActualDecProposal(proposals, project, node);
            break;
        case 701:
            addMakeSharedDecProposal(proposals, project, node);
            addRemoveAnnotationDecProposal(proposals, "actual", project, node);
            break;
        case 702:
            addMakeSharedDecProposal(proposals, project, node);
            addRemoveAnnotationDecProposal(proposals, "formal", project, node);
            break;
        case 703:
            addMakeSharedDecProposal(proposals, project, node);
            addRemoveAnnotationDecProposal(proposals, "default", project, node);
            break;
        case 710:
        case 711:
            addMakeSharedProposal(proposals, project, node);
            break;
        case 712:
            addExportModuleImportProposal(proposals, project, node);
            break;
        case 713:
            addMakeSharedProposalForSupertypes(proposals, project, node);
            break;
        case 714:
            addExportModuleImportProposalForSupertypes(proposals, project, node, rootNode);
            break;
        case 800:
        case 804:
            addMakeVariableProposal(proposals, project, node);
            break;
        case 803:
            addMakeVariableProposal(proposals, project, node);
            break;
        case 801:
            addMakeVariableDecProposal(proposals, project, rootNode, node);
            break;
        case 802:
            break;
        case 905:
            addMakeContainerAbstractProposal(proposals, project, node);
            break;
        case 1100:
            addMakeContainerAbstractProposal(proposals, project, node);
            addRemoveAnnotationDecProposal(proposals, "formal", project, node);
            break;
        case 1101:
            addRemoveAnnotationDecProposal(proposals, "formal", project, node);
            //TODO: replace body with ;
            break;
        case 1000:
        case 1001:
            addEmptyParameterListProposal(file, proposals, node);
            addParameterListProposal(file, proposals, node, rootNode);
            addConstructorProposal(file, proposals, node, rootNode);
            addChangeDeclarationProposal(problem, file, proposals, node);
            break;
        case 1050:
            addFixAliasProposal(proposals, file, problem);
            break;
        case 1200:
        case 1201:
            addRemoveAnnotationDecProposal(proposals, "shared", project, node);
            break;
        case 1300:
        case 1301:
            addMakeRefinedSharedProposal(proposals, project, node);
            addRemoveAnnotationDecProposal(proposals, "actual", project, node);
            break;
        case 1302:
        case 1312:
        case 1307:
            addRemoveAnnotationDecProposal(proposals, "formal", project, node);
            break;
        case 1303:
        case 1313:
        case 1320:
            addRemoveAnnotationDecProposal(proposals, "formal", project, node);
            addRemoveAnnotationDecProposal(proposals, "default", project, node);
            break;
        case 1350:
            addRemoveAnnotationDecProposal(proposals, "default", project, node);
            addMakeContainerNonfinalProposal(proposals, project, node);
            break;
        case 1400:
        case 1401:
            addMakeFormalDecProposal(proposals, project, node);
            break;
        case 1450:
        	addMakeFormalDecProposal(proposals, project, node);
        	addParameterProposals(proposals, file, rootNode, node, null);
        	addInitializerProposals(proposals, file, rootNode, node);
            addParameterListProposal(file, proposals, node, rootNode);
        	addConstructorProposal(file, proposals, node, rootNode);
        	break;
        case 1610:
            addRemoveAnnotationDecProposal(proposals, "shared", project, node);
            addRemoveAnnotationDecProposal(proposals, "abstract", project, node);
            break;
        case 1500:
        case 1501:
            addRemoveAnnotationDecProposal(proposals, "variable", project, node);
            break;
        case 1600:
        case 1601:
            addRemoveAnnotationDecProposal(proposals, "abstract", project, node);
            break;
        case 1700:
            addRemoveAnnotationDecProposal(proposals, "final", project, node);
            break;
        case 1800:
        case 1801:
            addRemoveAnnotationDecProposal(proposals, "sealed", project, node);
            break;
        case 1900:
            addRemoveAnnotationDecProposal(proposals, "late", project, node);
            break;
        case 1950:
        case 1951:
            addRemoveAnnotationDecProposal(proposals, "annotation", project, node);
            break;
        case 2000:
            addCreateParameterProposals(rootNode, node, problem, proposals, 
                    project, tc, file);
            break;
        case 2100:
            addChangeTypeProposals(rootNode, node, problem, proposals, project);
            addSatisfiesProposals(rootNode, node, proposals, project);
            break;
        case 2102:
            addChangeTypeArgProposals(rootNode, node, problem, proposals, project);
            addSatisfiesProposals(rootNode, node, proposals, project);
            break;
        case 2101:
            addEllipsisToSequenceParameterProposal(rootNode, node, proposals, file);            
            break;
        case 2500:
            addTypeParameterProposal(file, rootNode, proposals, node);
            break;
        case 3000:
            addAssignToLocalProposal(rootNode, proposals, node, start);
            addDestructureProposal(rootNode, proposals, node, start);
            addAssignToForProposal(rootNode, proposals, node, start);
            addAssignToIfExistsProposal(rootNode, proposals, node, start);
            addAssignToIfNonemptyProposal(rootNode, proposals, node, start);
            addAssignToTryProposal(rootNode, proposals, node, start);
            addAssignToIfIsProposal(rootNode, proposals, node, start);
            addPrintProposal(rootNode, proposals, node, start);
            break;
        case 3100:
            addShadowReferenceProposal(file, rootNode, proposals, node);
            break;
        case 3101:
        case 3102:
            addShadowSwitchReferenceProposal(file, rootNode, proposals, node);
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
            //TODO: figure out some other way to get a Shell!
            if (context.getSourceViewer()!=null) {
                addMoveDirProposal(file, rootNode, project, proposals, 
                        context.getSourceViewer().getTextWidget().getShell());
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
        }
    }

    private void addElseProposal(IFile file, 
            Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        if (node instanceof Tree.SwitchClause) {
            Tree.SwitchStatement ss = (Tree.SwitchStatement) 
                    findStatement(rootNode, node);
            TextFileChange tfc = 
                    new TextFileChange("Add Else", file);
            IDocument doc = getDocument(tfc);
            String text = 
                    getDefaultLineDelimiter(doc) + 
                    getIndent(node, doc) + 
                    "else {}";
            int offset = getNodeEndOffset(ss);
            tfc.setEdit(new InsertEdit(offset, 
                    text));
            proposals.add(new CorrectionProposal("Add 'else' clause", 
                    tfc, new Region(offset+text.length()-1, 0)));
        }
        
    }

    private void addCasesProposal(IFile file, 
            Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        if (node instanceof Tree.SwitchClause) {
            Tree.SwitchClause sc = (Tree.SwitchClause) node;
            Tree.SwitchStatement ss = (Tree.SwitchStatement) 
                    findStatement(rootNode, node);
            Tree.Expression e = 
                    sc.getSwitched().getExpression();
            if (e!=null) {
                ProducedType type = e.getTypeModel();
                if (type!=null) {
                    Tree.SwitchCaseList scl = 
                            ss.getSwitchCaseList();
                    for (Tree.CaseClause cc: 
                            scl.getCaseClauses()) {
                        Tree.CaseItem item = cc.getCaseItem();
                        if (item instanceof Tree.IsCase) {
                            Tree.IsCase ic = 
                                    (Tree.IsCase) item;
                            Tree.Type tn = ic.getType();
                            if (tn!=null) {
                                ProducedType t = 
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
                                    ProducedType t = 
                                            ex.getTypeModel();
                                    if (t!=null && 
                                            !isTypeUnknown(t)) {
                                        type = type.minus(t);
                                    }
                                }
                            }
                        }
                    }
                    TextFileChange tfc = 
                            new TextFileChange("Add Cases", 
                                    file);
                    IDocument doc = getDocument(tfc);
                    String text = "";
                    List<ProducedType> list;
                    if (type.getCaseTypes()!=null) {
                        list = type.getCaseTypes();
                    }
                    else {
                        list = singletonList(type);
                    }
                    for (ProducedType pt: list) {
                        String is = 
                                pt.getDeclaration()
                                    .isAnonymous() ? 
                                "" : "is ";
                        Unit unit = rootNode.getUnit();
                        text += getDefaultLineDelimiter(doc) + 
                                getIndent(node, doc) +
                                "case (" +
                                is + 
                                pt.getProducedTypeName(unit) +
                                ") {}"; 
                    }
                    int offset = getNodeEndOffset(ss);
                    tfc.setEdit(new InsertEdit(offset, text));
                    proposals.add(new CorrectionProposal(
                            "Add missing 'case' clauses", tfc, 
                            new Region(offset+text.length()-1, 0)));
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
        Tree.Declaration decNode = (Tree.Declaration) 
                getReferencedNodeInUnit(tp.getDeclaration(), 
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
            edit = new InsertEdit(decNode.getIdentifier().getStopIndex()+1, 
                    "<" + tp.getName() + ">");
        }
        else {
            edit = new InsertEdit(tpl.getStopIndex(), 
                    ", " + tp.getName());
        }
        tfc.setEdit(edit);
        proposals.add(new CorrectionProposal("Add '" + tp.getName() + 
                "' to type parameter list of '" + 
                decNode.getDeclarationModel().getName() + "'", 
                tfc, null));
    }

    private void addProposals(IQuickAssistInvocationContext context, 
            CeylonEditor editor, Collection<ICompletionProposal> proposals) {
        if (editor==null) return;
        
        IDocument doc = context.getSourceViewer().getDocument();
        IProject project = EditorUtil.getProject(editor.getEditorInput());
        IFile file = EditorUtil.getFile(editor.getEditorInput());
        
        Tree.CompilationUnit rootNode = 
                editor.getParseController().getRootNode();
        if (rootNode!=null) {
            int start = context.getOffset();
            int end = start + context.getLength();
            Node node = findNode(rootNode, start, end);
            int currentOffset = editor.getSelection().getOffset();
            
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
                    
            addAssignToLocalProposal(rootNode, proposals, node, currentOffset);
            addDestructureProposal(rootNode, proposals, node, currentOffset);
            addAssignToForProposal(rootNode, proposals, node, currentOffset);
            addAssignToIfExistsProposal(rootNode, proposals, node, currentOffset);
            addAssignToIfNonemptyProposal(rootNode, proposals, node, currentOffset);
            addAssignToTryProposal(rootNode, proposals, node, currentOffset);
            addAssignToIfIsProposal(rootNode, proposals, node, currentOffset);
            addPrintProposal(rootNode, proposals, node, currentOffset);
            
            addConvertToNamedArgumentsProposal(proposals, file, rootNode, 
                    editor, currentOffset);
            addConvertToPositionalArgumentsProposal(proposals, file, rootNode, 
                    editor, currentOffset);
            
            Tree.Statement statement = findStatement(rootNode, node);
            Tree.Declaration declaration = findDeclaration(rootNode, node);
            Tree.NamedArgument argument = findArgument(rootNode, node);
            Tree.ImportMemberOrType imp = findImport(rootNode, node);
            Tree.BinaryOperatorExpression boe = findBinaryOperator(rootNode, node);
            
            addBinaryOperatorProposals(proposals, file, boe);
            
            addVerboseRefinementProposal(proposals, file, statement, rootNode);
            
            addAnnotationProposals(proposals, project, declaration,
                    doc, currentOffset);
            addTypingProposals(proposals, file, rootNode, node, declaration, editor);
            
            addAnonymousFunctionProposals(editor, proposals, doc, file, rootNode, 
                    currentOffset);
            
            addDeclarationProposals(editor, proposals, doc, file, rootNode, 
                    declaration, currentOffset);
            
            addChangeToIfProposal(proposals, doc, file, rootNode, statement);
            
            addConvertToClassProposal(proposals, declaration, editor);
            addAssertExistsDeclarationProposals(proposals, doc, file, rootNode, declaration);
            addSplitDeclarationProposals(proposals, doc, file, rootNode, declaration);
            addJoinDeclarationProposal(proposals, rootNode, statement, file);
            addParameterProposals(proposals, file, rootNode, declaration, editor);
            
            addArgumentProposals(proposals, doc, file, argument);
            addUseAliasProposal(imp, proposals, editor);
            addRenameAliasProposal(imp, proposals, editor);
            addRemoveAliasProposal(imp, proposals, file, editor);            
            addRenameVersionProposals(node, proposals, rootNode, editor);
            
            addConvertToIfElseProposal(doc, proposals, file, statement);
            addConvertToThenElseProposal(rootNode, doc, proposals, file, statement);
            addReverseIfElseProposal(doc, proposals, file, statement, rootNode);
            
            addSplitIfStatementProposal(proposals, doc, file, statement);
            addJoinIfStatementsProposal(proposals, doc, file, statement);
            
            addConvertGetterToMethodProposal(proposals, editor, file, statement);
            addConvertMethodToGetterProposal(proposals, editor, file, statement);
            
            addThrowsAnnotationProposal(proposals, statement, rootNode, file, doc);            
            
            MoveToNewUnitProposal.add(proposals, editor);
            MoveToUnitProposal.add(proposals, editor);
            
            addRefineFormalMembersProposal(proposals, node, rootNode, false);
            
            addConvertToVerbatimProposal(proposals, file, rootNode, node, doc);
            addConvertFromVerbatimProposal(proposals, file, rootNode, node, doc);
            addConvertToConcatenationProposal(proposals, file, rootNode, node, doc);
            addConvertToInterpolationProposal(proposals, file, rootNode, node, doc);
            
            addExpandTypeProposal(editor, statement, file, doc, proposals);
        }
        
    }

    private void addBinaryOperatorProposals(
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.BinaryOperatorExpression boe) {
        if (boe!=null) {
            addParenthesizeBinaryOperatorProposal(proposals, file, boe);
            addSwapBinaryOperandsProposal(proposals, file, boe);
        }
    }

    private void addSwapBinaryOperandsProposal(
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.BinaryOperatorExpression boe) {
        TextChange change = new TextFileChange("Swap Operands", file);
        change.setEdit(new MultiTextEdit());
        Tree.Term lt = boe.getLeftTerm();
        Tree.Term rt = boe.getRightTerm();
        if (lt!=null && rt!=null) {
            IDocument document = getDocument(change);
            int lto = lt.getStartIndex();
            int ltl = lt.getStopIndex()-lto+1;
            int rto = rt.getStartIndex();
            int rtl = rt.getStopIndex()-rto+1;
            try {
                change.addEdit(new ReplaceEdit(lto, ltl, 
                        document.get(rto, rtl)));
                change.addEdit(new ReplaceEdit(rto, rtl, 
                        document.get(lto, ltl)));
                proposals.add(new CorrectionProposal("Swap operands of " + 
                        boe.getMainToken().getText() + 
                        " expression", change, null));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addParenthesizeBinaryOperatorProposal(
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.BinaryOperatorExpression boe) {
        TextChange change = new TextFileChange("Parenthesize Expression", file);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(boe.getStartIndex(), "("));
        change.addEdit(new InsertEdit(boe.getStopIndex()+1, ")"));
        proposals.add(new CorrectionProposal("Parenthesize " + 
                boe.getMainToken().getText() + 
                " expression", change, null));
    }

    private void addAnnotationProposals(Collection<ICompletionProposal> proposals, 
            IProject project, Tree.Declaration decNode, IDocument doc, int offset) {
        if (decNode!=null) {
            try {
                Node in = Nodes.getIdentifyingNode(decNode);
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
                        if (((Tree.AttributeDeclaration) decNode).getSpecifierOrInitializerExpression()==null) {
                            addMakeFormalDecProposal(proposals, project, decNode);
                        }
                    }
                    else if (decNode instanceof Tree.MethodDeclaration) {
                        if (((Tree.MethodDeclaration) decNode).getSpecifierExpression()==null) {
                            addMakeFormalDecProposal(proposals, project, decNode);
                        }
                    }
                }
            }
        }
    }
    
    private static void addAnonymousFunctionProposals(CeylonEditor editor,
            Collection<ICompletionProposal> proposals, IDocument doc,
            IFile file, Tree.CompilationUnit cu,
            final int currentOffset) {
        class FindAnonFunctionVisitor extends Visitor {
            Tree.FunctionArgument result;
            public void visit(Tree.FunctionArgument that) {
                if (currentOffset>=that.getStartIndex() &&
                    currentOffset<=that.getStopIndex()+1) {
                    result = that;
                }
                super.visit(that);
            }
        }
        FindAnonFunctionVisitor v = new FindAnonFunctionVisitor();
        v.visit(cu);
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

    private static void addDeclarationProposals(CeylonEditor editor,
            Collection<ICompletionProposal> proposals, IDocument doc,
            IFile file, Tree.CompilationUnit cu,
            Tree.Declaration decNode, int currentOffset) {
        
        if (decNode==null) return;
        
        if (decNode.getAnnotationList()!=null) {
            Integer stopIndex = decNode.getAnnotationList().getStopIndex();
            if (stopIndex!=null && currentOffset<=stopIndex+1) {
                return;
            }
        }
        if (decNode instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration tdn = (Tree.TypedDeclaration) decNode;
            if (tdn.getType()!=null) {
                Integer stopIndex = tdn.getType().getStopIndex();
                if (stopIndex!=null && currentOffset<=stopIndex+1) {
                    return;
                }
            }
        }
            
        if (decNode instanceof Tree.AttributeDeclaration) {
            Tree.AttributeDeclaration attDecNode = (Tree.AttributeDeclaration) decNode;
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
            Tree.SpecifierOrInitializerExpression se = 
                    ((Tree.MethodDeclaration) decNode).getSpecifierExpression(); 
            if (se instanceof Tree.LazySpecifierExpression) {
                addConvertToBlockProposal(doc, proposals, file, decNode);
            }
        }
        if (decNode instanceof Tree.AttributeSetterDefinition) {
            Tree.SpecifierOrInitializerExpression se = 
                    ((Tree.AttributeSetterDefinition) decNode).getSpecifierExpression();
            if (se instanceof Tree.LazySpecifierExpression) {
                addConvertToBlockProposal(doc, proposals, file, decNode);
            }
            Tree.Block b = ((Tree.AttributeSetterDefinition) decNode).getBlock(); 
            if (b!=null) {
                addConvertToSpecifierProposal(doc, proposals, file, b);
            }
        }
        if (decNode instanceof Tree.AttributeGetterDefinition) {
            Tree.Block b = ((Tree.AttributeGetterDefinition) decNode).getBlock(); 
            if (b!=null) {
                addConvertToSpecifierProposal(doc, proposals, file, b);
            }
        }
        if (decNode instanceof Tree.MethodDefinition) {
            Tree.Block b = ((Tree.MethodDefinition) decNode).getBlock(); 
            if (b!=null) {
                addConvertToSpecifierProposal(doc, proposals, file, b);
            }
        }
        
    }

	private void addArgumentProposals(Collection<ICompletionProposal> proposals, 
            IDocument doc, IFile file, Tree.StatementOrArgument node) {
        if (node instanceof Tree.MethodArgument) {
            Tree.MethodArgument ma = (Tree.MethodArgument) node;
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
            Tree.AttributeArgument aa = (Tree.AttributeArgument) node;
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
            Tree.SpecifiedArgument sa = (Tree.SpecifiedArgument) node;
            addFillInArgumentNameProposal(proposals, doc, file, sa);
        }
    }

    private void addCreationProposals(Tree.CompilationUnit cu, final Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project, TypeChecker tc, IFile file) {
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
                        invocationExpression = that.getInvocationExpression();
                    }
                }
            }
            FindExtendedTypeExpressionVisitor v = new FindExtendedTypeExpressionVisitor();
            v.visit(cu);
            if (v.invocationExpression!=null) {
                addCreateProposals(cu, v.invocationExpression.getPrimary(), 
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
                addCreateTypeParameterProposal(proposals, project, cu, bt, brokenName);
            }
        }
    }

    public void collectAnnotationCorrections(CeylonAnnotation annotation,
            IQuickAssistInvocationContext context,
            ProblemLocation location, Collection<ICompletionProposal> proposals) {
        if (annotation.getSeverity()==IMarker.SEVERITY_WARNING) {
            Tree.CompilationUnit rootNode = getRootNode();
            Tree.Statement st = Nodes.findStatement(rootNode,
                    Nodes.findNode(rootNode, location.getOffset(), 
                            location.getOffset()+location.getLength()));
            if (st==null) return;
            if (!(st instanceof Tree.Declaration)) {
                st = Nodes.findDeclaration(rootNode, st);
            }
            IFile file = EditorUtil.getFile(editor.getEditorInput());
            IDocument doc = context.getSourceViewer().getDocument();
            TextFileChange change = 
                    new TextFileChange("Suppress Warnings", file);
            final StringBuilder sb = new StringBuilder();
            final StyledString ss = 
                    new StyledString("Suppress warnings of type ");
            new Visitor() {
                @Override
                public void visitAny(Node node) {
                    for (Message m: node.getErrors()) {
                        if (m instanceof UsageWarning) {
                            UsageWarning warning = (UsageWarning) m;
                            String warningName = warning.getWarningName();
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
            }.visit(st);
            String ws = 
                    Indents.getDefaultLineDelimiter(doc) +
                    Indents.getIndent(st, doc);
            String text = "suppressWarnings(" + sb + ")";
            Integer start = st.getStartIndex();
            if (st instanceof Tree.Declaration) {
                Tree.AnnotationList al = 
                        ((Tree.Declaration) st).getAnnotationList();
                if (al!=null && al.getAnonymousAnnotation()!=null) {
                    start = al.getAnonymousAnnotation().getStopIndex()+1;
                    text = ws + text;
                }
                else {
                    text += ws;
                }
            }
            else {
                text += ws;
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
            proposals.add(new ICompletionProposal() {
                @Override
                public Point getSelection(IDocument document) {
                    return null;
                }
                
                @Override
                public Image getImage() {
                    return CONFIG_WARNING;
                }
                
                @Override
                public String getDisplayString() {
                    return "Configure compiler warnings";
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
                    PreferencesUtil.createPropertyDialogOn(editor.getSite().getShell(), 
                            editor.getParseController().getProject(), //TODO: is this correct? 
                            CeylonWarningsPropertiesPage.ID, 
                            new String[] { CeylonWarningsPropertiesPage.ID }, 
                            null).open();
                }
            });
        }
        
    }

}
