package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.JDK_MODULE_VERSION;
import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CHANGE;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findStatement;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.name;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeAbstractProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeActualProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeContainerAbstractProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeDefaultDecProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeDefaultProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeFormalProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeSharedDecProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeSharedProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeSharedPropsalForSupertypes;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeVariableDecProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.addMakeVariableProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddConstraintSatisfiesProposal.addConstraintSatisfiesProposals;
import static com.redhat.ceylon.eclipse.code.quickfix.AddParameterProposal.addParameterProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddParenthesesProposal.addAddParenthesesProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddSpreadToVariadicParameterProposal.addEllipsisToSequenceParameterProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AddThrowsAnnotationProposal.addThrowsAnnotationProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.AssignToLocalProposal.addAssignToLocalProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ChangeDeclarationProposal.addChangeDeclarationProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ChangeInitialCaseOfIdentifierInDeclaration.addChangeIdentifierCaseProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ChangeReferenceProposal.addRenameProposals;
import static com.redhat.ceylon.eclipse.code.quickfix.ChangeTypeProposal.addChangeTypeProposals;
import static com.redhat.ceylon.eclipse.code.quickfix.ConvertGetterToMethodProposal.addConvertGetterToMethodProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ConvertIfElseToThenElse.addConvertToThenElseProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ConvertMethodToGetterProposal.addConvertMethodToGetterProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ConvertThenElseToIfElse.addConvertToIfElseProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ConvertToBlockProposal.addConvertToBlockProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ConvertToGetterProposal.addConvertToGetterProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ConvertToNamedArgumentsProposal.addConvertToNamedArgumentsProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ConvertToSpecifierProposal.addConvertToSpecifierProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateEnumProposal.addCreateEnumProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateInNewUnitProposal.addCreateToplevelProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateLocalSubtypeProposal.addCreateLocalSubtypeProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateObjectProposal.addCreateObjectProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateParameterProposal.addCreateParameterProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateParameterProposal.addCreateParameterProposals;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateProposal.addCreateLocalProposals;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateProposal.addCreateMemberProposals;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateProposal.addCreateToplevelProposals;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateTypeParameterProposal.addCreateTypeParameterProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.FixAliasProposal.addFixAliasProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.FixMultilineStringIndentationProposal.addFixMultilineStringIndentation;
import static com.redhat.ceylon.eclipse.code.quickfix.ImplementFormalAndAmbiguouslyInheritedMembersProposal.addImplementFormalAndAmbiguouslyInheritedMembersProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.InvertIfElseProposal.addReverseIfElseProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.MoveDirProposal.addMoveDirProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.RemoveAliasProposal.addRemoveAliasProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.RemoveAnnotionProposal.addRemoveAnnotationDecProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.RenameAliasProposal.addRenameAliasProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.RenameVersionProposal.addRenameVersionProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ShadowReferenceProposal.addShadowReferenceProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.ShadowReferenceProposal.addShadowSwitchReferenceProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.SpecifyTypeProposal.addSpecifyTypeProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.SplitDeclarationProposal.addSplitDeclarationProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.UseAliasProposal.addUseAliasProposal;
import static com.redhat.ceylon.eclipse.code.quickfix.Util.getModuleQueryType;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Import;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;
import com.redhat.ceylon.eclipse.core.builder.MarkerCreator;
import com.redhat.ceylon.eclipse.util.FindArgumentVisitor;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;

/**
 * Popup quick fixes for problem annotations displayed in editor
 * @author gavin
 */
public class CeylonQuickFixAssistant {

    public boolean canFix(Annotation annotation) {
        int code;
        if (annotation instanceof CeylonAnnotation) {
            code = ((CeylonAnnotation) annotation).getId();
        }
        else if (annotation instanceof MarkerAnnotation) {
            code = ((MarkerAnnotation) annotation).getMarker()
                   .getAttribute(MarkerCreator.ERROR_CODE_KEY, 0);
        }
        else {
            return false;
        }
        return code>0;
    }

    public boolean canAssist(IQuickAssistInvocationContext context) {
        //oops, all this is totally useless, because
        //this method never gets called :-/
        /*Tree.CompilationUnit cu = (CompilationUnit) context.getModel()
                .getAST(new NullMessageHandler(), new NullProgressMonitor());
        return CeylonSourcePositionLocator.findNode(cu, context.getOffset(), 
                context.getOffset()+context.getLength()) instanceof Tree.Term;*/
        return true;
    }

    public String[] getSupportedMarkerTypes() {
        return new String[] { PROBLEM_MARKER_ID };
    }

    public static String getIndent(Node node, IDocument doc) {
        if (node==null||node.getEndToken()==null||
                node.getEndToken().getLine()==0) {
            return "";
        }
        try {
            IRegion region = doc.getLineInformation(node.getEndToken().getLine()-1);
            String line = doc.get(region.getOffset(), region.getLength());
            char[] chars = line.toCharArray();
            for (int i=0; i<chars.length; i++) {
                if (chars[i]!='\t' && chars[i]!=' ') {
                    return line.substring(0,i);
                }
            }
            return line;
        }
        catch (BadLocationException ble) {
            return "";
        }
    }
    
    public void addProposals(IQuickAssistInvocationContext context, 
    		CeylonEditor editor, Collection<ICompletionProposal> proposals) {
        if (editor==null) return;
        
        IDocument doc = context.getSourceViewer().getDocument();
        IProject project = Util.getProject(editor.getEditorInput());
        IFile file = Util.getFile(editor.getEditorInput());
        
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu!=null) {
            Node node = findNode(cu, context.getOffset(), 
                    context.getOffset() + context.getLength());
            int currentOffset = editor.getSelection().getOffset();
            
            RenameDeclarationProposal.add(proposals, file, editor);
            InlineDeclarationProposal.add(proposals, editor);
            ChangeParametersProposal.add(proposals, file, editor);
            ExtractValueProposal.add(proposals, editor, node);
            ExtractFunctionProposal.add(proposals, editor, node);
            ConvertToClassProposal.add(proposals, editor);
                    
            addAssignToLocalProposal(file, cu, proposals, node, 
                    currentOffset);
            
            addConvertToNamedArgumentsProposal(proposals, file, cu, 
            		editor, currentOffset);
            
            Tree.Statement statement = findStatement(cu, node);
            Tree.Declaration declaration = findDeclaration(cu, node);
            Tree.TypedArgument argument = findArgument(cu, node);
            
            addVerboseRefinementProposal(proposals, file, statement);
            
            addAnnotationProposals(proposals, project, declaration,
            		doc, currentOffset);
            addTypingProposals(proposals, file, cu, node, declaration);
            
            addDeclarationProposals(editor, proposals, doc, file, cu, 
            		declaration, currentOffset);
            
            addArgumentProposals(proposals, doc, file, argument);
            addImportProposals(editor, cu, proposals, file, node);
            
            addConvertToIfElseProposal(doc, proposals, file, statement);
            addConvertToThenElseProposal(cu, doc, proposals, file, statement);
            addReverseIfElseProposal(doc, proposals, file, statement);
            
            addConvertGetterToMethodProposal(proposals, editor, file, statement);
            addConvertMethodToGetterProposal(proposals, editor, file, statement);
            
            addThrowsAnnotationProposal(proposals, statement, cu, file, doc);            

            addCreateObjectProposal(doc, cu, proposals, file, node);
            addCreateLocalSubtypeProposal(doc, cu, proposals, file, node);            
            CreateSubtypeInNewUnitProposal.add(proposals, editor);
            MoveDeclarationProposal.add(proposals, editor);
            
            RefineFormalMembersProposal.add(proposals, editor);
            
            addConvertToVerbatimProposal(proposals, file, cu, node, doc);
            addConvertFromVerbatimProposal(proposals, file, cu, node, doc);
        }
        
    }

	private void addVerboseRefinementProposal(
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.Statement statement) {
	    if (statement instanceof Tree.SpecifierStatement) {
	    	Tree.SpecifierStatement ss = (Tree.SpecifierStatement) statement;
	    	if (ss.getRefinement()) {
	    		TextFileChange change = new TextFileChange("Convert to Verbose Refinement", file);
	    		Tree.Expression e = ss.getSpecifierExpression().getExpression();
	    		if (e!=null && !isTypeUnknown(e.getTypeModel())) {
	    			Unit unit = ss.getUnit();
	    			String type = unit.denotableType(e.getTypeModel())
	    					.getProducedTypeName(unit);
	    			change.setEdit(new InsertEdit(statement.getStartIndex(), 
	    					"shared actual " + type + " "));
	    			proposals.add(new ChangeCorrectionProposal("Convert to verbose refinement", change));
	    		}
	    	}
	    }
    }
    
    private void addConvertToVerbatimProposal(Collection<ICompletionProposal> proposals,
    		IFile file, Tree.CompilationUnit cu, Node node, IDocument doc) {
    	if (node instanceof Tree.StringLiteral) {
	        Tree.StringLiteral literal = (Tree.StringLiteral) node;
    		Token token = node.getToken();
    		if (token.getType()==CeylonLexer.ASTRING_LITERAL ||
    			token.getType()==CeylonLexer.STRING_LITERAL) {
    			String text = "\"\"\"" + literal.getText() + "\"\"\"";
    	        int offset = node.getStartIndex();
    	        int length = node.getStopIndex() - node.getStartIndex() + 1; 
    	        String reindented = getConvertedText(text, token.getCharPositionInLine()+3, doc);
        		TextFileChange change = new TextFileChange("Convert to Verbatim String", file);
    			change.setEdit(new ReplaceEdit(offset, length, reindented));
        		proposals.add(new ChangeCorrectionProposal("Convert to verbatim string", 
        				change, CHANGE));
    		}
    	}
    }

    private void addConvertFromVerbatimProposal(Collection<ICompletionProposal> proposals,
    		IFile file, Tree.CompilationUnit cu, Node node, IDocument doc) {
    	if (node instanceof Tree.StringLiteral) {
    		Tree.StringLiteral literal = (Tree.StringLiteral) node;
    		Token token = node.getToken();
    		if (token.getType()==CeylonLexer.AVERBATIM_STRING ||
    			token.getType()==CeylonLexer.VERBATIM_STRING) {
    			String text = "\"" +
    					literal.getText()
    						.replace("\\", "\\\\")
    						.replace("\"", "\\\"")
    						.replace("`", "\\`") +
    					"\"";
    	        int offset = node.getStartIndex();
    	        int length = node.getStopIndex() - node.getStartIndex() + 1; 
    	        String reindented = getConvertedText(text, token.getCharPositionInLine()+1, doc);
        		TextFileChange change = new TextFileChange("Convert to Ordinary String", file);
    			change.setEdit(new ReplaceEdit(offset, length, reindented));
        		proposals.add(new ChangeCorrectionProposal("Convert to ordinary string", 
        				change, CHANGE));
    		}
    	}
    }

    static String getConvertedText(String text, int indentation,
    		IDocument doc) {
        StringBuilder result = new StringBuilder();
        for (String line: text.split("\n|\r\n?")) {
            if (result.length() == 0) {
            	//the first line of the string
                result.append(line);
            }
            else {
                for (int i = 0; i<indentation; i++) {
                    result.append(" ");
                }
                result.append(line);
            }
            result.append(getDefaultLineDelimiter(doc));
        }
        result.setLength(result.length()-1);
        return result.toString();
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
            	if ((d.isClassOrInterfaceMember()||d.isToplevel()) && 
            			!d.isShared()) {
            		addMakeSharedDecProposal(proposals, project, decNode);
            	}
            	if (d.isClassOrInterfaceMember() && 
            			d.isShared() &&
            			!d.isDefault() && !d.isFormal() &&
            			!(d instanceof Interface)) {
            		addMakeDefaultDecProposal(proposals, project, decNode);
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

    private void addImportProposals(CeylonEditor editor, Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file, Node node) {
        
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
    }

	public static Tree.Declaration findDeclaration(Tree.CompilationUnit cu, Node node) {
		FindDeclarationVisitor fcv = new FindDeclarationVisitor(node);
		fcv.visit(cu);
		return fcv.getDeclarationNode();
	}
    
	public static Tree.TypedArgument findArgument(Tree.CompilationUnit cu, Node node) {
		FindArgumentVisitor fcv = new FindArgumentVisitor(node);
		fcv.visit(cu);
		return fcv.getArgumentNode();
	}
    
    public void addProposals(IQuickAssistInvocationContext context, ProblemLocation problem,
            IFile file, Tree.CompilationUnit cu, Collection<ICompletionProposal> proposals) {
        if (file==null) return;
        IProject project = file.getProject();
        TypeChecker tc = getProjectTypeChecker(project);
        Node node = findNode(cu, problem.getOffset(), 
                    problem.getOffset() + problem.getLength());
        switch ( problem.getProblemId() ) {
        case 100:
        case 102:
        	if (tc!=null) {
        		addImportProposals(cu, node, proposals, file);
        	}
        	addCreateEnumProposal(cu, node, problem, proposals, 
        			project, tc, file);
        	addCreateProposals(cu, node, problem, proposals, 
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
        	if (context.getSourceViewer()!=null) { //TODO: figure out some other way to get the Document!
        		addImplementFormalAndAmbiguouslyInheritedMembersProposal(cu, node, 
        		        proposals, file, context.getSourceViewer().getDocument());
        	}
        //fallthrough:
        case 310:
        	addMakeAbstractProposal(proposals, project, node);
        	break;
        case 400:
        	addMakeSharedProposal(proposals, project, node);
        	break;
        case 500:
        case 510:
        	addMakeDefaultProposal(proposals, project, node);
        	break;
        case 600:
        	addMakeActualProposal(proposals, project, node);
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
        case 713:
            addMakeSharedPropsalForSupertypes(proposals, project, node);
            break;
        case 800:
        case 804:
        	addMakeVariableProposal(proposals, project, node);
        	break;
        case 803:
        	addMakeVariableProposal(proposals, project, node);
        	break;
        case 801:
        	addMakeVariableDecProposal(cu, proposals, project, node);
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
        	addMakeFormalProposal(proposals, project, node);
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
        	addConstraintSatisfiesProposals(cu, node, proposals, project);
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
            addModuleImportProposals(cu, proposals, project, tc, node);
            break;
        case 8000:
            addRenameDescriptorProposal(cu, context, problem, proposals, file);
            //TODO: figure out some other way to get a Shell!
            if (context.getSourceViewer()!=null) {
                addMoveDirProposal(file, cu, project, proposals, 
                        context.getSourceViewer().getTextWidget().getShell());
            }
            break;
        }
    }

    private void addRenameDescriptorProposal(Tree.CompilationUnit cu,
            IQuickAssistInvocationContext context, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IFile file) {
        String pn = escapedPackageName(cu.getUnit().getPackage());
        //TODO: DocumentChange doesn't work for Problems View
        TextFileChange change = new TextFileChange("Rename", file);
//        DocumentChange change = new DocumentChange("Rename", context.getSourceViewer().getDocument());
        change.setEdit(new ReplaceEdit(problem.getOffset(), problem.getLength(), pn));
        proposals.add(new ChangeCorrectionProposal("Rename to '" + pn + "'", change, CHANGE));
    }

    private void addModuleImportProposals(Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IProject project,
            TypeChecker tc, Node node) {
        if (cu.getUnit().getPackage().getModule().isDefault()) {
            return;
        }
        if (node instanceof Tree.Import) {
            node = ((Tree.Import) node).getImportPath();
        }
        List<Tree.Identifier> ids = ((Tree.ImportPath) node).getIdentifiers();
        String pkg = formatPath(ids);
        if (JDKUtils.isJDKAnyPackage(pkg)) {
            for (String mod: new TreeSet<String>(JDKUtils.getJDKModuleNames())) {
                if (JDKUtils.isJDKPackage(mod, pkg)) {
                    proposals.add(new AddModuleImportProposal(project, cu.getUnit(), mod, 
                            JDK_MODULE_VERSION));
                    return;
                }
            }
        }
        for (int i=ids.size(); i>0; i--) {
            String pn = formatPath(ids.subList(0, i));
            ModuleQuery query = new ModuleQuery(pn, getModuleQueryType(project));
            query.setBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
            query.setCount(2l);
            ModuleSearchResult msr = tc.getContext().getRepositoryManager().searchModules(query);
            ModuleDetails md = msr.getResult(pn);
            if (md!=null) {
                proposals.add(new AddModuleImportProposal(project, cu.getUnit(), md));
            }
            if (!msr.getResults().isEmpty()) break;
        }
    }
    
    private void addCreateProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project, TypeChecker tc, IFile file) {
    	if (node instanceof Tree.MemberOrTypeExpression) {
    		Tree.MemberOrTypeExpression smte = (Tree.MemberOrTypeExpression) node;
    		String brokenName = getIdentifyingNode(node).getText();
    		if (!brokenName.isEmpty()) {
    			DefinitionGenerator dg = DefinitionGenerator.create(brokenName, smte, cu);
    			if (dg!=null) {
    				if (smte instanceof Tree.QualifiedMemberOrTypeExpression) {
    					addCreateMemberProposals(proposals, project, dg, 
    							(Tree.QualifiedMemberOrTypeExpression) smte);
    				}
    				else {
    					addCreateLocalProposals(proposals, project, dg);
    					ClassOrInterface container = findClassContainer(cu, smte);
    					if (container!=null && 
    							container!=smte.getScope()) { //if the statement appears directly in an initializer, propose a local, not a member 
    						do {
    							addCreateMemberProposals(proposals, project, 
    									dg, container);
    							if (container.getContainer() instanceof Declaration) {
    								container = findClassContainer((Declaration) container.getContainer());
    							}
    							else { 
    								break;
    							}
    						}
    						while (container!=null);
    					}
    					addCreateToplevelProposals(proposals, project, dg);
    					addCreateToplevelProposal(proposals, dg, file);

    					addCreateParameterProposal(proposals, project, dg);
    				}
    			}
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
        	String brokenName = bt.getIdentifier().getText();
        	addCreateTypeParameterProposal(proposals, project, cu, bt, brokenName);
        }
    }


    private ClassOrInterface findClassContainer(Tree.CompilationUnit cu, Node node){
		FindContainerVisitor fcv = new FindContainerVisitor(node);
		fcv.visit(cu);
    	Tree.Declaration declaration = fcv.getDeclaration();
        if(declaration == null || declaration == node)
            return null;
        if(declaration instanceof Tree.ClassOrInterface)
            return (ClassOrInterface) declaration.getDeclarationModel();
        if(declaration instanceof Tree.MethodDefinition)
            return findClassContainer(declaration.getDeclarationModel());
        if(declaration instanceof Tree.ObjectDefinition)
            return findClassContainer(declaration.getDeclarationModel());
        return null;
    }
    
    private ClassOrInterface findClassContainer(Declaration declarationModel) {
        do {
            if(declarationModel == null)
                return null;
            if(declarationModel instanceof ClassOrInterface)
                return (ClassOrInterface) declarationModel;
            if(declarationModel.getContainer() instanceof Declaration)
                declarationModel = (Declaration)declarationModel.getContainer();
            else
                return null;
        }
        while(true);
    }
    
    static Tree.CompilationUnit getRootNode(PhasedUnit unit) {
        IEditorPart ce = Util.getCurrentEditor();
        if (ce instanceof CeylonEditor) {
            CeylonParseController cpc = ((CeylonEditor) ce).getParseController();
            if (cpc!=null) {
            	Tree.CompilationUnit rn = cpc.getRootNode();
                if (rn!=null) {
                    Unit u = rn.getUnit();
                    if (u.equals(unit.getUnit())) {
                        return rn;
                    }
                }
            }
        }       
        return unit.getCompilationUnit();
    }

    static Tree.Body getBody(Tree.Declaration decNode) {
        if (decNode instanceof Tree.ClassDefinition) {
            return ((Tree.ClassDefinition) decNode).getClassBody();
        }
        else if (decNode instanceof Tree.InterfaceDefinition){
            return ((Tree.InterfaceDefinition) decNode).getInterfaceBody();
        }
        else if (decNode instanceof Tree.ObjectDefinition){
            return ((Tree.ObjectDefinition) decNode).getClassBody();
        }
        return null;
    }

    private void addImportProposals(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, IFile file) {
        if (node instanceof Tree.BaseMemberOrTypeExpression ||
                node instanceof Tree.SimpleType) {
            Node id = getIdentifyingNode(node);
            String brokenName = id.getText();
            Module module = cu.getUnit().getPackage().getModule();
            for (Declaration decl: findImportCandidates(module, brokenName, cu)) {
                ICompletionProposal ip = createImportProposal(cu, file, decl);
                if (ip!=null) proposals.add(ip);
            }
        }
    }
    
    private static Set<Declaration> findImportCandidates(Module module, 
            String name, Tree.CompilationUnit cu) {
        Set<Declaration> result = new HashSet<Declaration>();
        for (Package pkg: module.getAllPackages()) {
            if (!pkg.getName().isEmpty()) {
                Declaration member = pkg.getMember(name, null, false);
                if (member!=null) {
                    result.add(member);
                }
            }
        }
        /*if (result.isEmpty()) {
            for (Package pkg: module.getAllPackages()) {
                for (Declaration member: pkg.getMembers()) {
                    if (!isImported(member, cu)) {
                        int dist = getLevenshteinDistance(name, member.getName());
                        //TODO: would it be better to just sort by dist, and
                        //      then select the 3 closest possibilities?
                        if (dist<=name.length()/3+1) {
                            result.add(member);
                        }
                    }
                }
            }
        }*/
        return result;
    }
    
    private static ICompletionProposal createImportProposal(Tree.CompilationUnit cu, 
    		IFile file, Declaration declaration) {
        TextFileChange change = new TextFileChange("Add Import", file);
        IDocument doc = CreateProposal.getDocument(change);
        List<InsertEdit> ies = importEdit(cu, Collections.singleton(declaration), 
        		null, null, doc);
        if (ies.isEmpty()) return null;
		change.setEdit(new MultiTextEdit());
		for (InsertEdit ie: ies) change.addEdit(ie);
        String proposedName = declaration.getName();
		/*String brokenName = id.getText();
        if (!brokenName.equals(proposedName)) {
		    change.addEdit(new ReplaceEdit(id.getStartIndex(), brokenName.length(), 
		            proposedName));
		}*/
        return new ChangeCorrectionProposal("Add import of '" + proposedName + "'" + 
                " in package " + declaration.getUnit().getPackage().getNameAsString(), 
                change, CeylonLabelProvider.IMPORT);
    }

	public static List<InsertEdit> importEdit(Tree.CompilationUnit cu,
			Iterable<Declaration> declarations, Iterable<String> aliases,
			Declaration declarationBeingDeleted, IDocument doc) {
		List<InsertEdit> result = new ArrayList<InsertEdit>();
		Set<Package> packages = new HashSet<Package>();
		for (Declaration declaration: declarations) {
			packages.add(declaration.getUnit().getPackage());
		}
		for (Package p: packages) {
			StringBuilder text = new StringBuilder();
			if (aliases==null) {
			    for (Declaration d: declarations) {
			        if (d.getUnit().getPackage().equals(p)) {
			            text.append(", ").append(name(d));
			        }
			    }
			}
			else {
		        Iterator<String> aliasIter = aliases.iterator();
                for (Declaration d: declarations) {
                    String alias = aliasIter.next();
                    if (d.getUnit().getPackage().equals(p)) {
                        text.append(", ");
                        if (alias!=null && !alias.equals(d.getName())) {
                            text.append(alias).append('=');
                        }
                        text.append(name(d));
                    }
                }
			}
			Tree.Import importNode = findImportNode(cu, p.getNameAsString());
			if (importNode!=null) {
				Tree.ImportMemberOrTypeList imtl = importNode.getImportMemberOrTypeList();
				if (imtl.getImportWildcard()!=null) {
					//Do nothing
				}
				else {
					int insertPosition = getBestImportMemberInsertPosition(importNode);
					if (declarationBeingDeleted!=null &&
						imtl.getImportMemberOrTypes().size()==1 &&
						imtl.getImportMemberOrTypes().get(0).getDeclarationModel()
							.equals(declarationBeingDeleted)) {
						text.delete(0, 2);
					}
					result.add(new InsertEdit(insertPosition, text.toString()));
				}
			} 
			else {
				int insertPosition = getBestImportInsertPosition(cu);
				text.delete(0, 2);
				text.insert(0, "import " + escapedPackageName(p) + " { ")
				    .append(" }"); 
				String delim = getDefaultLineDelimiter(doc);
				if (insertPosition==0) {
					text.append(delim);
				}
				else {
					text.insert(0, delim);
				}
				result.add(new InsertEdit(insertPosition, text.toString()));
			}
		}
		return result;
	}
    
    public static String escapedPackageName(Package p) {
    	List<String> path = p.getName();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<path.size(); i++) {
            String pathPart = path.get(i);
            if (!pathPart.isEmpty()) {
                if (CeylonTokenColorer.keywords.contains(pathPart)) {
                	pathPart = "\\i" + pathPart;
                }
                sb.append(pathPart);
                if (i<path.size()-1) sb.append('.');
            }
        }
        return sb.toString();
    }
	
    public static List<TextEdit> importEditForMove(Tree.CompilationUnit cu,
            Iterable<Declaration> declarations, Iterable<String> aliases,
            String newPackageName, String oldPackageName, IDocument doc) {
    	String delim = getDefaultLineDelimiter(doc);
        List<TextEdit> result = new ArrayList<TextEdit>();
        Set<Declaration> set = new HashSet<Declaration>();
        for (Declaration d: declarations) {
            set.add(d);
        }
        StringBuilder text = new StringBuilder();
        if (aliases==null) {
            for (Declaration d: declarations) {
                text.append(", ").append(d.getName());
            }
        }
        else {
            Iterator<String> aliasIter = aliases.iterator();
            for (Declaration d: declarations) {
                String alias = aliasIter.next();
                text.append(", ");
                if (alias!=null && !alias.equals(d.getName())) {
                    text.append(alias).append('=');
                }
                text.append(d.getName());
            }
        }
        Tree.Import oldImportNode = findImportNode(cu, oldPackageName);
        if (oldImportNode!=null) {
            Tree.ImportMemberOrTypeList imtl = oldImportNode.getImportMemberOrTypeList();
            if (imtl!=null) {
                int remaining = 0;
                for (Tree.ImportMemberOrType imt: imtl.getImportMemberOrTypes()) {
                    if (!set.contains(imt.getDeclarationModel())) {
                        remaining++;
                    }
                }
                if (remaining==0) {
                    result.add(new DeleteEdit(oldImportNode.getStartIndex(), 
                            oldImportNode.getStopIndex()-oldImportNode.getStartIndex()+1));
                }
                else {
                    //TODO: format it better!!!!
                    StringBuilder sb = new StringBuilder("{").append(delim);
                    for (Tree.ImportMemberOrType imt: imtl.getImportMemberOrTypes()) {
                        if (!set.contains(imt.getDeclarationModel())) {
                            sb.append(getDefaultIndent());
                            if (imt.getAlias()!=null) {
                                sb.append(imt.getAlias().getIdentifier().getText())
                                    .append('=');
                            }
                            sb.append(imt.getIdentifier().getText()).append(",")
                                .append(delim);
                        }
                    }
                    sb.setLength(sb.length()-2);
                    sb.append(delim).append("}");
                    result.add(new ReplaceEdit(imtl.getStartIndex(), 
                            imtl.getStopIndex()-imtl.getStartIndex()+1, 
                            sb.toString()));
                }
            }
        }
        if (!cu.getUnit().getPackage().getQualifiedNameString().equals(newPackageName)) {
            Tree.Import importNode = findImportNode(cu, newPackageName);
            if (importNode!=null) {
                Tree.ImportMemberOrTypeList imtl = importNode.getImportMemberOrTypeList();
                if (imtl.getImportWildcard()!=null) {
                    //Do nothing
                }
                else {
                    int insertPosition = getBestImportMemberInsertPosition(importNode);
                    result.add(new InsertEdit(insertPosition, text.toString()));
                }
            } 
            else {
                int insertPosition = getBestImportInsertPosition(cu);
                text.delete(0, 2);
                text.insert(0, "import " + newPackageName + " { ").append(" }"); 
                if (insertPosition==0) {
                    text.append(delim);
                }
                else {
                    text.insert(0, delim);
                }
                result.add(new InsertEdit(insertPosition, text.toString()));
            }
        }
        return result;
    }
    
    private static int getBestImportInsertPosition(Tree.CompilationUnit cu) {
        Integer stopIndex = cu.getImportList().getStopIndex();
        if (stopIndex == null) return 0;
        return stopIndex+1;
    }

    public static Tree.Import findImportNode(Tree.CompilationUnit cu, String packageName) {
        FindImportNodeVisitor visitor = new FindImportNodeVisitor(packageName);
        cu.visit(visitor);
        return visitor.getResult();
    }

    private static int getBestImportMemberInsertPosition(Tree.Import importNode) {
    	Tree.ImportMemberOrTypeList imtl = 
    			importNode.getImportMemberOrTypeList();
        if (imtl.getImportWildcard()!=null) {
            return imtl.getImportWildcard().getStartIndex();
        }
        else {
            List<Tree.ImportMemberOrType> imts = 
            		imtl.getImportMemberOrTypes();
            if (imts.isEmpty()) {
                return imtl.getStartIndex()+1;
            }
            else {
                return imts.get(imts.size()-1).getStopIndex()+1;
            }
        }
    }

	public static int applyImports(TextChange change,
			Set<Declaration> alreadyImported, 
			Tree.CompilationUnit cu, IDocument doc) {
		return applyImports(change, alreadyImported, null, cu, doc);
	}
	
	public static int applyImports(TextChange change,
			Set<Declaration> alreadyImported, 
			Declaration declarationBeingDeleted,
			Tree.CompilationUnit cu, IDocument doc) {
		int il=0;
		for (InsertEdit ie: importEdit(cu, alreadyImported, 
				null, declarationBeingDeleted, doc)) {
			il+=ie.getText().length();
			change.addEdit(ie);
		}
		return il;
	}

	public static void importSignatureTypes(Declaration declaration, 
			Tree.CompilationUnit rootNode, Set<Declaration> tc) {
		if (declaration instanceof TypedDeclaration) {
			importType(tc, ((TypedDeclaration) declaration).getType(), rootNode);
		}
		if (declaration instanceof Functional) {
			for (ParameterList pl: ((Functional) declaration).getParameterLists()) {
				for (Parameter p: pl.getParameters()) {
					importType(tc, p.getType(), rootNode);
				}
			}
		}
	}
	
	public static void importTypes(Set<Declaration> tfc, 
			Collection<ProducedType> types, 
			Tree.CompilationUnit rootNode) {
		if (types==null) return;
		for (ProducedType type: types) {
			importType(tfc, type, rootNode);
		}
	}
	
	public static void importType(Set<Declaration> tfc, 
			ProducedType type, 
			Tree.CompilationUnit rootNode) {
		if (type==null) return;
		if (type.getDeclaration() instanceof UnionType) {
			for (ProducedType t: type.getDeclaration().getCaseTypes()) {
				importType(tfc, t, rootNode);
			}
		}
		else if (type.getDeclaration() instanceof IntersectionType) {
			for (ProducedType t: type.getDeclaration().getSatisfiedTypes()) {
				importType(tfc, t, rootNode);
			}
		}
		else {
			TypeDeclaration td = type.getDeclaration();
			if (td instanceof ClassOrInterface && 
					td.isToplevel()) {
				importDeclaration(tfc, td, rootNode);
				for (ProducedType arg: type.getTypeArgumentList()) {
					importType(tfc, arg, rootNode);
				}
			}
		}
	}

	public static void importDeclaration(Set<Declaration> declarations,
			Declaration declaration, Tree.CompilationUnit rootNode) {
		Package p = declaration.getUnit().getPackage();
		if (!p.getNameAsString().isEmpty() && 
			!p.equals(rootNode.getUnit().getPackage()) &&
//			!((declaration instanceof MethodOrValue) && ((MethodOrValue)declaration).isParameter()) &&
			!p.getNameAsString().equals(Module.LANGUAGE_MODULE_NAME)) {
			if (!isImported(declaration, rootNode)) {
				declarations.add(declaration);
			}
		}
	}

    public static boolean isImported(Declaration declaration,
            Tree.CompilationUnit rootNode) {
        for (Import i: rootNode.getUnit().getImports()) {
        	if (i.getDeclaration().equals(declaration)) {
        		return true;
        	}
        }
        return false;
    }

}
