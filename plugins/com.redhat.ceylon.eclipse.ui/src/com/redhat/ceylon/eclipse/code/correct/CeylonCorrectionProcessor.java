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
import static com.redhat.ceylon.eclipse.code.correct.AddModuleImportProposal.addModuleImportProposals;
import static com.redhat.ceylon.eclipse.code.correct.AddParameterProposal.addParameterProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddParenthesesProposal.addAddParenthesesProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddSatisfiesProposal.addSatisfiesProposals;
import static com.redhat.ceylon.eclipse.code.correct.AddSpreadToVariadicParameterProposal.addEllipsisToSequenceParameterProposal;
import static com.redhat.ceylon.eclipse.code.correct.AddThrowsAnnotationProposal.addThrowsAnnotationProposal;
import static com.redhat.ceylon.eclipse.code.correct.AssignToLocalProposal.addAssignToLocalProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeDeclarationProposal.addChangeDeclarationProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeInitialCaseOfIdentifierInDeclaration.addChangeIdentifierCaseProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeReferenceProposal.addRenameProposals;
import static com.redhat.ceylon.eclipse.code.correct.ChangeRefiningTypeProposal.addChangeRefiningParametersProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeRefiningTypeProposal.addChangeRefiningTypeProposal;
import static com.redhat.ceylon.eclipse.code.correct.ChangeTypeProposal.addChangeTypeProposals;
import static com.redhat.ceylon.eclipse.code.correct.ConvertGetterToMethodProposal.addConvertGetterToMethodProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertIfElseToThenElse.addConvertToThenElseProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertMethodToGetterProposal.addConvertMethodToGetterProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertStringProposal.addConvertFromVerbatimProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertStringProposal.addConvertToVerbatimProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertThenElseToIfElse.addConvertToIfElseProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToBlockProposal.addConvertToBlockProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToGetterProposal.addConvertToGetterProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToNamedArgumentsProposal.addConvertToNamedArgumentsProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToPositionalArgumentsProposal.addConvertToPositionalArgumentsProposal;
import static com.redhat.ceylon.eclipse.code.correct.ConvertToSpecifierProposal.addConvertToSpecifierProposal;
import static com.redhat.ceylon.eclipse.code.correct.CreateEnumProposal.addCreateEnumProposal;
import static com.redhat.ceylon.eclipse.code.correct.CreateLocalSubtypeProposal.addCreateLocalSubtypeProposal;
import static com.redhat.ceylon.eclipse.code.correct.CreateObjectProposal.addCreateObjectProposal;
import static com.redhat.ceylon.eclipse.code.correct.CreateParameterProposal.addCreateParameterProposals;
import static com.redhat.ceylon.eclipse.code.correct.CreateProposal.addCreateProposals;
import static com.redhat.ceylon.eclipse.code.correct.CreateTypeParameterProposal.addCreateTypeParameterProposal;
import static com.redhat.ceylon.eclipse.code.correct.ExportModuleImportProposal.addExportModuleImportProposal;
import static com.redhat.ceylon.eclipse.code.correct.ExportModuleImportProposal.addExportModuleImportProposalForSupertypes;
import static com.redhat.ceylon.eclipse.code.correct.FillInArgumentNameProposal.addFillInArgumentNameProposal;
import static com.redhat.ceylon.eclipse.code.correct.FixAliasProposal.addFixAliasProposal;
import static com.redhat.ceylon.eclipse.code.correct.FixMultilineStringIndentationProposal.addFixMultilineStringIndentation;
import static com.redhat.ceylon.eclipse.code.correct.ImplementFormalAndAmbiguouslyInheritedMembersProposal.addImplementFormalAndAmbiguouslyInheritedMembersProposal;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.addImportProposals;
import static com.redhat.ceylon.eclipse.code.correct.InvertIfElseProposal.addReverseIfElseProposal;
import static com.redhat.ceylon.eclipse.code.correct.MoveDirProposal.addMoveDirProposal;
import static com.redhat.ceylon.eclipse.code.correct.RemoveAliasProposal.addRemoveAliasProposal;
import static com.redhat.ceylon.eclipse.code.correct.RemoveAnnotionProposal.addRemoveAnnotationDecProposal;
import static com.redhat.ceylon.eclipse.code.correct.RenameAliasProposal.addRenameAliasProposal;
import static com.redhat.ceylon.eclipse.code.correct.RenameDescriptorProposal.addRenameDescriptorProposal;
import static com.redhat.ceylon.eclipse.code.correct.RenameVersionProposal.addRenameVersionProposal;
import static com.redhat.ceylon.eclipse.code.correct.ShadowReferenceProposal.addShadowReferenceProposal;
import static com.redhat.ceylon.eclipse.code.correct.ShadowReferenceProposal.addShadowSwitchReferenceProposal;
import static com.redhat.ceylon.eclipse.code.correct.SpecifyTypeProposal.addSpecifyTypeProposal;
import static com.redhat.ceylon.eclipse.code.correct.SplitDeclarationProposal.addSplitDeclarationProposal;
import static com.redhat.ceylon.eclipse.code.correct.UseAliasProposal.addUseAliasProposal;
import static com.redhat.ceylon.eclipse.code.correct.VerboseRefinementProposal.addVerboseRefinementProposal;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.AnnotationUtils.getAnnotationsForLine;
import static com.redhat.ceylon.eclipse.util.FindUtils.findArgument;
import static com.redhat.ceylon.eclipse.util.FindUtils.findDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.core.builder.MarkerCreator;
import com.redhat.ceylon.eclipse.util.FindUtils;
import com.redhat.ceylon.eclipse.util.MarkerUtils;

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
        ArrayList<ProblemLocation> problems = new ArrayList<ProblemLocation>();
        // collect problem locations and corrections from marker annotations
        for (Annotation curr: annotations) {
            if (curr instanceof CeylonAnnotation) {
                ProblemLocation problemLocation = 
                        getProblemLocation((CeylonAnnotation) curr, model);
                if (problemLocation != null) {
                    problems.add(problemLocation);
                }
            }
        }
        if (problems.isEmpty() && addQuickFixes) {
             for (Annotation curr: annotations) {
                 if (curr instanceof SimpleMarkerAnnotation) {
                     collectMarkerProposals((SimpleMarkerAnnotation) curr, proposals);
                 }                 
             }
        }

        ProblemLocation[] problemLocations =
            (ProblemLocation[]) problems.toArray(new ProblemLocation[problems.size()]);
        Arrays.sort(problemLocations);
        if (addQuickFixes) {
            collectCorrections(context, problemLocations, proposals);
        }
        if (addQuickAssists) {
            collectAssists(context, problemLocations, proposals);
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
        collectProposals(context, viewer.getAnnotationModel(),
                getAnnotationsForLine(viewer, getLine(context, viewer)), 
                        true, true, proposals);
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
                    if (location.getOffset()!=loc.getOffset()) break;
                    addProposals(context, location, getFile(), 
                            rootNode, proposals);
                }
                if (!proposals.isEmpty()) {
                    viewer.setSelectedRange(loc.getOffset(), loc.getLength());
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
                viewer.setSelectedRange(loc.getOffset(), loc.getLength());
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
            return ((CeylonAnnotation) annotation).getId()>0;
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
            ProblemLocation problem, IFile file, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals) {
        if (file==null) return;
        IProject project = file.getProject();
        TypeChecker tc = getProjectTypeChecker(project);
        Node node = findNode(cu, problem.getOffset(), 
                    problem.getOffset() + problem.getLength());
        switch ( problem.getProblemId() ) {
        case 100:
            if (tc!=null) {
                addRenameProposals(cu, node, problem, proposals, file);
            }
            addCreationProposals(cu, node, problem, proposals, 
                    project, tc, file);
            break;
        case 102:
            if (tc!=null) {
                addImportProposals(cu, node, proposals, file);
            }
            addCreateEnumProposal(cu, node, problem, proposals, 
                    project, tc, file);
            addCreationProposals(cu, node, problem, proposals, 
                    project, tc, file);
            if (tc!=null) {
                addRenameProposals(cu, node, problem, proposals, file);
            }
            break;
        case 101:
            addCreateParameterProposals(cu, node, problem, proposals, 
                    project, tc, file);
            if (tc!=null) {
                addRenameProposals(cu, node, problem, proposals, file);
            }
            break;
        case 200:
            addSpecifyTypeProposal(cu, node, proposals, file);
            break;
        case 300:
        case 350:
            if (context.getSourceViewer()!=null) {
                //TODO: figure out some other way to get the Document!
                IDocument document = context.getSourceViewer().getDocument();
                addImplementFormalAndAmbiguouslyInheritedMembersProposal(cu, node, 
                        proposals, file, document);
            }
        //fallthrough:
        case 310:
            addMakeAbstractDecProposal(proposals, project, node);
            break;
        case 400:
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
            addExportModuleImportProposalForSupertypes(proposals, project, node);
            break;
        case 800:
        case 804:
            addMakeVariableProposal(proposals, project, node);
            break;
        case 803:
            addMakeVariableProposal(proposals, project, node);
            break;
        case 801:
            addMakeVariableDecProposal(proposals, project, cu, node);
            break;
        case 802:
            break;
        case 905:
            addMakeContainerAbstractProposal(proposals, project, node);
            break;
        case 900:
        case 1100:
            addMakeContainerAbstractProposal(proposals, project, node);
            addRemoveAnnotationDecProposal(proposals, "formal", project, node);
            break;
        case 1000:
            addAddParenthesesProposal(problem, file, proposals, node);
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
            addRemoveAnnotationDecProposal(proposals, "default", project, node);
            break;
        case 1400:
        case 1401:
            addMakeFormalDecProposal(proposals, project, node);
            break;
        case 1500:
            addRemoveAnnotationDecProposal(proposals, "variable", project, node);
            break;
        case 1600:
            addRemoveAnnotationDecProposal(proposals, "abstract", project, node);
            break;
        case 2000:
            addCreateParameterProposals(cu, node, problem, proposals, 
                    project, tc, file);
            break;
        case 2100:
        case 2102:
            addChangeTypeProposals(cu, node, problem, proposals, project);
            addSatisfiesProposals(cu, node, proposals, project);
            break;
        case 2101:
            addEllipsisToSequenceParameterProposal(cu, node, proposals, file);            
            break;
        case 3000:
            addAssignToLocalProposal(file, cu, proposals, node, problem.getOffset());
            break;
        case 3100:
            addShadowReferenceProposal(file, cu, proposals, node);
            break;
        case 3101:
        case 3102:
            addShadowSwitchReferenceProposal(file, cu, proposals, node);
            break;
        case 5001:
        case 5002:
            addChangeIdentifierCaseProposal(node, proposals, file);
            break;
        case 6000:
            addFixMultilineStringIndentation(proposals, file, cu, node);
            break;
        case 7000:
            addModuleImportProposals(proposals, project, tc, node);
            break;
        case 8000:
            addRenameDescriptorProposal(cu, context, problem, proposals, file);
            //TODO: figure out some other way to get a Shell!
            if (context.getSourceViewer()!=null) {
                addMoveDirProposal(file, cu, project, proposals, 
                        context.getSourceViewer().getTextWidget().getShell());
            }
            break;
        case 9000:
            addChangeRefiningTypeProposal(file, cu, proposals, node);
            break;
        case 9100:
        case 9200:
            addChangeRefiningParametersProposal(file, cu, proposals, node);
            break;
        }
    }

    private void addProposals(IQuickAssistInvocationContext context, 
            CeylonEditor editor, Collection<ICompletionProposal> proposals) {
        if (editor==null) return;
        
        IDocument doc = context.getSourceViewer().getDocument();
        IProject project = EditorUtil.getProject(editor.getEditorInput());
        IFile file = EditorUtil.getFile(editor.getEditorInput());
        
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu!=null) {
            Node node = findNode(cu, context.getOffset(), 
                    context.getOffset() + context.getLength());
            int currentOffset = editor.getSelection().getOffset();
            
            RenameDeclarationProposal.add(proposals, editor);
            InlineDeclarationProposal.add(proposals, editor);
            ChangeParametersProposal.add(proposals, editor);
            ExtractValueProposal.add(proposals, editor, node);
            ExtractFunctionProposal.add(proposals, editor, node);
            ExtractParameterProposal.add(proposals, editor, node);
            CollectParametersProposal.add(proposals, editor);
            MoveOutProposal.add(proposals, editor, node);
            MakeReceiverProposal.add(proposals, editor, node);
            ConvertToClassProposal.add(proposals, editor);
                    
            addAssignToLocalProposal(file, cu, proposals, node, 
                    currentOffset);
            
            addConvertToNamedArgumentsProposal(proposals, file, cu, 
                    editor, currentOffset);
            addConvertToPositionalArgumentsProposal(proposals, file, cu, 
                    editor, currentOffset);
            
            Tree.Statement statement = FindUtils.findStatement(cu, node);
            Tree.Declaration declaration = findDeclaration(cu, node);
            Tree.NamedArgument argument = findArgument(cu, node);
            
            addVerboseRefinementProposal(proposals, file, statement, cu);
            
            addAnnotationProposals(proposals, project, declaration,
                    doc, currentOffset);
            addTypingProposals(proposals, file, cu, node, declaration);
            
            addDeclarationProposals(editor, proposals, doc, file, cu, 
                    declaration, currentOffset);
            
            addArgumentProposals(proposals, doc, file, argument);
            addRefactorImportProposals(editor, cu, proposals, file, node);
            
            addConvertToIfElseProposal(doc, proposals, file, statement);
            addConvertToThenElseProposal(cu, doc, proposals, file, statement);
            addReverseIfElseProposal(doc, proposals, file, statement, cu);
            
            addConvertGetterToMethodProposal(proposals, editor, file, statement);
            addConvertMethodToGetterProposal(proposals, editor, file, statement);
            
            addThrowsAnnotationProposal(proposals, statement, cu, file, doc);            

            addCreateObjectProposal(doc, cu, proposals, file, node);
            addCreateLocalSubtypeProposal(doc, cu, proposals, file, node);            
            CreateSubtypeInNewUnitProposal.add(proposals, editor);
            MoveToNewUnitProposal.add(proposals, editor);
            MoveToUnitProposal.add(proposals, editor);
            
            RefineFormalMembersProposal.add(proposals, editor);
            
            addConvertToVerbatimProposal(proposals, file, cu, node, doc);
            addConvertFromVerbatimProposal(proposals, file, cu, node, doc);
        }
        
    }

    private void addAnnotationProposals(Collection<ICompletionProposal> proposals, 
            IProject project, Tree.Declaration decNode, IDocument doc, int offset) {
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

    private void addTypingProposals(Collection<ICompletionProposal> proposals,
            IFile file, Tree.CompilationUnit cu, Node node,
            Tree.Declaration decNode) {
        if (decNode instanceof Tree.TypedDeclaration && 
                !(decNode instanceof Tree.ObjectDefinition) &&
                !(decNode instanceof Tree.Variable)) {
            Tree.Type type = ((Tree.TypedDeclaration) decNode).getType();
            if (type instanceof Tree.LocalModifier) {
                addSpecifyTypeProposal(cu, type, proposals, file);
            }
        }
        else if (node instanceof Tree.LocalModifier) {
            addSpecifyTypeProposal(cu, node, proposals, file);
        }
    }

    private void addDeclarationProposals(CeylonEditor editor,
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
                addConvertToBlockProposal(doc, proposals, file, 
                        (Tree.LazySpecifierExpression) se, decNode);
            }
            else {
                addConvertToGetterProposal(doc, proposals, file, attDecNode);
            }
        }
        if (decNode instanceof Tree.MethodDeclaration) {
            Tree.SpecifierOrInitializerExpression se = 
                    ((Tree.MethodDeclaration) decNode).getSpecifierExpression(); 
            if (se instanceof Tree.LazySpecifierExpression) {
                addConvertToBlockProposal(doc, proposals, file, 
                        (Tree.LazySpecifierExpression) se, decNode);
            }
        }
        if (decNode instanceof Tree.AttributeSetterDefinition) {
            Tree.SpecifierOrInitializerExpression se = 
                    ((Tree.AttributeSetterDefinition) decNode).getSpecifierExpression();
            if (se instanceof Tree.LazySpecifierExpression) {
                addConvertToBlockProposal(doc, proposals, file, 
                        (Tree.LazySpecifierExpression) se, decNode);
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
        if (decNode instanceof Tree.AttributeDeclaration) {
            Tree.AttributeDeclaration attDecNode = (Tree.AttributeDeclaration) decNode;
            Tree.SpecifierOrInitializerExpression sie = 
                    attDecNode.getSpecifierOrInitializerExpression();
            if (sie!=null) {
                addSplitDeclarationProposal(doc, cu, proposals, file, attDecNode);
            }
            if (!(sie instanceof Tree.LazySpecifierExpression)) {
                addParameterProposal(doc, cu, proposals, file, attDecNode, sie, editor);
            }
        }
        if (decNode instanceof Tree.MethodDeclaration) {
            Tree.MethodDeclaration methDecNode = (Tree.MethodDeclaration) decNode;
            Tree.SpecifierExpression sie = methDecNode.getSpecifierExpression();
            if (sie!=null) {
                addSplitDeclarationProposal(doc, cu, proposals, file, methDecNode);
            }
            addParameterProposal(doc, cu, proposals, file, methDecNode, sie, editor);
        }
        
    }

    private void addRefactorImportProposals(CeylonEditor editor, 
            Tree.CompilationUnit cu, Collection<ICompletionProposal> proposals, 
            IFile file, Node node) {
        
        class FindImportVisitor extends Visitor {
            private Declaration declaration;
            Tree.ImportMemberOrType result;
            FindImportVisitor(Declaration dec) {
                this.declaration = dec;
            }
            @Override
            public void visit(Tree.Declaration that) {}
            @Override
            public void visit(Tree.ImportMemberOrType that) {
                super.visit(that);
                if (that.getDeclarationModel()!=null &&
                        that.getDeclarationModel().equals(declaration)) {
                    result = that;
                }
            }
        }
        
        if (node instanceof Tree.MemberOrTypeExpression) {
            Declaration declaration = 
                    ((Tree.MemberOrTypeExpression) node).getDeclaration();
            if (declaration!=null) {
                FindImportVisitor visitor = new FindImportVisitor(declaration);
                visitor.visit(cu);
                node = visitor.result;
            }
        }
        else if (node instanceof Tree.SimpleType) {
            Declaration declaration = 
                    ((Tree.SimpleType) node).getDeclarationModel();
            if (declaration!=null) {
                FindImportVisitor visitor = new FindImportVisitor(declaration);
                visitor.visit(cu);
                node = visitor.result;
            }
        }
            
        if (node instanceof Tree.ImportMemberOrType) {
            Tree.ImportMemberOrType imt = (Tree.ImportMemberOrType) node;
            Declaration dec = imt.getDeclarationModel();
            if (dec!=null) {
                if (imt.getAlias()==null) {
                    addUseAliasProposal(imt, proposals, dec, editor);
                }
                else {
                    addRenameAliasProposal(imt, proposals, dec, editor);
                    addRemoveAliasProposal(imt, proposals, dec, file, editor);
                }
            }
        }
        
        for (Tree.ModuleDescriptor md: cu.getModuleDescriptors()) {
            if (md.getVersion()==node || md==node || md.getImportPath()==node) {
                addRenameVersionProposal(md, proposals, editor);
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
                addConvertToBlockProposal(doc, proposals, file, 
                        (Tree.LazySpecifierExpression) se, node);
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
                addConvertToBlockProposal(doc, proposals, file, 
                        (Tree.LazySpecifierExpression) se, node);
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

    private void addCreationProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project, TypeChecker tc, IFile file) {
        if (node instanceof Tree.MemberOrTypeExpression) {
            addCreateProposals(cu, node, proposals, project, file);
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
            String brokenName = bt.getIdentifier().getText();
            addCreateTypeParameterProposal(proposals, project, cu, bt, brokenName);
        }
    }

}


