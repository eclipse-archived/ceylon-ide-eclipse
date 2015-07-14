package com.redhat.ceylon.eclipse.code.converter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.redhat.ceylon.eclipse.code.converter.Java8Parser.*;

public class JavaToCeylonConverter implements Java8Listener {
	String lastFormalParameter = "", forinit = "", forlimit = "", forConditionOperator = "", forCounterDatatype = "",
			lastActualParameter = "", lastTypeParameter = "", variableModifier = "", variableListType = "",
			forByValue = "1", packageName = "", lastInterface = "", firstVariableInList = "";
	boolean enterfor = false;
	boolean enterresult = false;
	boolean isInstanceOf = false;

	String[] keywords = { "assembly", "abstracts", "alias", "assert", "assign", "break", "case", "catch", "class",
			"continue", "dynamic", "else", "exists", "extends", "finally", "for", "function", "given", "if", "import",
			"in", "interface", "is", "module", "nonempty", "object", "of", "out", "outer", "package", "return",
			"satisfies", "super", "switch", "then", "this", "throw", "try", "value", "void", "while" };

	boolean multipleVariables = false;

	Stack<String> operators = new Stack<String>();
	Stack<Boolean> enterArgumentList = new Stack<Boolean>();
	Stack<Object> bracketInstance = new Stack<Object>();

	boolean enterTypeArgumentsList = false;
	boolean enterTypeParametersList = false;
	boolean enterForUpdate = false;
	boolean firstImport = true;
	boolean enterArray = false;
	boolean enterArrayAccessSet = false;
	boolean enterArrayAccess = false;
	boolean enterArrayAccess_lfno_primary = false;
	boolean enterInterfaceDeclaration = false;
	boolean openParenthesis = false;
	boolean enterEnhancedfor = false;
	boolean notEqualNull = false; // to convert !=null to exists
	boolean noVariable = false; // to check if value has to be a variable or not
	boolean isinstanceofForCast = false; // check if cast is after instanceofF
	boolean inExpression = false; // to check if != is in expression
	boolean equalsequalsNull = false; // x == null
	boolean typeConstraints = false;

	BufferedWriter bw;

	public JavaToCeylonConverter(BufferedWriter bw) {
		this.bw = bw;
	}

	public void close() throws IOException {
		bw.flush();
		bw.close();

	}

	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");
	}

	public void exitVariableInitializer(VariableInitializerContext ctx) {

	}

	public void exitVariableDeclaratorId(VariableDeclaratorIdContext ctx) {

	}

	public void exitLocalVariableDeclarationStatement(LocalVariableDeclarationStatementContext ctx) {
		try {
			if (!enterfor)
				bw.write(";\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitFieldDeclaration(FieldDeclarationContext ctx) {
		try {
			bw.write(";\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitExpressionStatement(ExpressionStatementContext ctx) {
		try {
			bw.write(ctx.getChild(ctx.getChildCount() - 1) + "\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitClassBody(ClassBodyContext ctx) {
		int count = ctx.getChildCount();

		try {
			bw.write(ctx.getChild(count - 1).toString() + "\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitBlock(BlockContext ctx) {
		int count = ctx.getChildCount();
		try {

			if (!(ctx.getParent().getParent().getParent() instanceof DoStatementContext)) {
				bw.write(ctx.getChild(count - 1).toString());
				bw.write("\n");
			} else {
				bw.write("if(");
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterUnannPrimitiveType(UnannPrimitiveTypeContext ctx) {

		String type = ctx.getText();
		String ceylonType = "";
		if (!enterArray) {
			try {
				if (!enterfor && !enterresult && !noVariable) {
					if (!variableModifier.equals("final")) {
						bw.write("variable ");
						variableListType = "variable ";
					}
				}
				variableModifier = "";

				if (type.equals("int") || type.equals("short") || type.equals("long")) {
					ceylonType = "Integer ";
				} else if (type.equals("byte")) {
					ceylonType = "Byte ";
				} else if (type.equals("char")) {
					ceylonType = "Character ";
				} else if (type.equals("float") || type.equals("double")) {
					ceylonType = "Float ";
				} else if (type.equals("boolean")) {
					ceylonType = "Boolean ";
				} else {
					ceylonType = type + " ";
				}

				variableListType += ceylonType;

				bw.write(ceylonType);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public void enterResult(ResultContext ctx) {

		try {
			if (((MethodHeaderContext) ctx.getParent()).typeParameters() == null) {
				enterresult = true;
				if (ctx.getChild(0).toString().equals("void"))
					bw.write(ctx.getChild(0).toString() + " ");
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterNormalClassDeclaration(NormalClassDeclarationContext ctx) {

		String modifier = " ";
		// if (ctx.classModifier() != null)
		// if (ctx.classModifier(0).getText().equals("public"))
		// modifier = "shared ";

		if (ctx.classModifier() != null)
			for (int i = 0; i < ctx.classModifier().size(); i++) {
				String mod = ctx.classModifier(i).getText();
				if (mod.equals("public"))
					modifier = "shared ";
				else if (mod.equals("abstract"))
					modifier = "abstract ";

			}

		try {
			if (ctx.typeParameters() == null)
				bw.write(modifier + "class " + ctx.Identifier() + "() ");
			else {
				enterTypeParametersList = true;
				bw.write(modifier + "class " + ctx.Identifier());
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterMethodModifier(MethodModifierContext ctx) {

		try {
			if (ctx.getText().equals("public"))
				bw.write("shared ");
			else if (ctx.getText().equals("abstract"))
				bw.write("formal ");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void enterMethodDeclarator(MethodDeclaratorContext ctx) {

		try {
			if (((MethodHeaderContext) ctx.getParent()).typeParameters() == null) {
				String methodDeclarator = ctx.Identifier().getText();

				for (String str : keywords) {
					if (str.equals(methodDeclarator)) {
						methodDeclarator = "\\i" + methodDeclarator;
					}
				}

				bw.write(methodDeclarator);
				if (ctx.formalParameterList() == null) {
					bw.write("()");
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterClassBody(ClassBodyContext ctx) {

		try {
			if (typeConstraints) {
				bw.write(" given "
						+ ((NormalClassDeclarationContext) ctx.getParent()).typeParameters().typeParameterList()
								.typeParameter(0).getChild(0).getText()
						+ " satisfies " + ((NormalClassDeclarationContext) ctx.getParent()).typeParameters()
								.typeParameterList().typeParameter(0).typeBound().typeVariable().getText());
			}
			typeConstraints = false;
			bw.write(ctx.getChild(0).toString() + "\n");
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void enterBlock(BlockContext ctx) {

		try {
			bw.write(ctx.getChild(0) + "\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterAssignment(AssignmentContext ctx) {

		if (enterForUpdate) {
			forByValue = ctx.expression().getText();
		} else if (ctx.getChildCount() > 1 && openParenthesis) {
			bracketInstance.push(ctx);
			try {
				bw.write("(");
				openParenthesis = false;
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

	public void visitTerminal(TerminalNode node) {

	}

	public void visitErrorNode(ErrorNode node) {

	}

	public void exitEveryRule(ParserRuleContext ctx) {

	}

	public void enterEveryRule(ParserRuleContext ctx) {

	}

	public void exitWildcardBounds(WildcardBoundsContext ctx) {

	}

	public void exitWildcard(WildcardContext ctx) {

	}

	public void exitWhileStatementNoShortIf(WhileStatementNoShortIfContext ctx) {

	}

	public void exitWhileStatement(WhileStatementContext ctx) {

	}

	public void exitVariableModifier(VariableModifierContext ctx) {

	}

	public void exitVariableInitializerList(VariableInitializerListContext ctx) {

	}

	public void exitVariableDeclaratorList(VariableDeclaratorListContext ctx) {

		multipleVariables = false;
	}

	public void exitVariableDeclarator(VariableDeclaratorContext ctx) {
		inExpression = false;
		if (multipleVariables && ctx != ctx.getParent().getChild(ctx.getParent().getChildCount() - 1)) {
			try {
				bw.write(";\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void exitUnaryExpressionNotPlusMinus(UnaryExpressionNotPlusMinusContext ctx) {

	}

	public void exitUnaryExpression(UnaryExpressionContext ctx) {

		// try {
		// if (ctx.preIncrementExpression() == null
		// && ctx.preDecrementExpression() == null)
		// if (!operators.isEmpty()) {
		// bw.write(" " + operators.lastElement() + " ");
		// operators.pop();
		// }
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// }
	}

	public void exitUnannTypeVariable(UnannTypeVariableContext ctx) {

	}

	public void exitUnannType(UnannTypeContext ctx) {

		if (ctx.getParent().getChild(1) != null && ctx.getParent().getChild(1).getText().equals("...")) {
			try {
				bw.write("* ");
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public void exitUnannReferenceType(UnannReferenceTypeContext ctx) {

	}

	public void exitUnannPrimitiveType(UnannPrimitiveTypeContext ctx) {

	}

	public void exitUnannInterfaceType_lfno_unannClassOrInterfaceType(
			UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) {

	}

	public void exitUnannInterfaceType_lf_unannClassOrInterfaceType(
			UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) {

	}

	public void exitUnannInterfaceType(UnannInterfaceTypeContext ctx) {

	}

	public void exitUnannClassType_lfno_unannClassOrInterfaceType(
			UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {

	}

	public void exitUnannClassType_lf_unannClassOrInterfaceType(
			UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) {

	}

	public void exitUnannClassType(UnannClassTypeContext ctx) {

	}

	public void exitUnannClassOrInterfaceType(UnannClassOrInterfaceTypeContext ctx) {

	}

	public void exitUnannArrayType(UnannArrayTypeContext ctx) {

		enterArray = false;
	}

	public void exitTypeVariable(TypeVariableContext ctx) {

	}

	public void exitTypeParameters(TypeParametersContext ctx) {

		try {
			bw.write("> ");
			if (ctx.getParent() instanceof NormalClassDeclarationContext)
				bw.write("() ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitTypeParameterModifier(TypeParameterModifierContext ctx) {

	}

	public void exitTypeParameterList(TypeParameterListContext ctx) {

	}

	public void exitTypeParameter(TypeParameterContext ctx) {

	}

	public void exitTypeName(TypeNameContext ctx) {

	}

	public void exitTypeImportOnDemandDeclaration(TypeImportOnDemandDeclarationContext ctx) {

	}

	public void exitTypeDeclaration(TypeDeclarationContext ctx) {

	}

	public void exitTypeBound(TypeBoundContext ctx) {

	}

	public void exitTypeArgumentsOrDiamond(TypeArgumentsOrDiamondContext ctx) {
		try {
			// TODO change getChild(4)
			if (!(ctx.getParent().getChild(4) instanceof ArgumentListContext)) {
				bw.write("()");
			} else {
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitTypeArguments(TypeArgumentsContext ctx) {

	}

	public void exitTypeArgumentList(TypeArgumentListContext ctx) {

		try {
			enterTypeArgumentsList = false;
			if (!typeConstraints)
				bw.write("> ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitTypeArgument(TypeArgumentContext ctx) {

		try {
			ParserRuleContext parentContext = ctx.getParent();
			if (ctx != parentContext.getChild(parentContext.getChildCount() - 1)) {
				bw.write(", ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitType(TypeContext ctx) {

	}

	public void exitTryWithResourcesStatement(TryWithResourcesStatementContext ctx) {

	}

	public void exitTryStatement(TryStatementContext ctx) {

	}

	public void exitThrows_(Throws_Context ctx) {

	}

	public void exitThrowStatement(ThrowStatementContext ctx) {
		try {
			bw.write(";\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitSynchronizedStatement(SynchronizedStatementContext ctx) {

	}

	public void exitSwitchStatement(SwitchStatementContext ctx) {

	}

	public void exitSwitchLabels(SwitchLabelsContext ctx) {

	}

	public void exitSwitchLabel(SwitchLabelContext ctx) {

		try {
			if (ctx.getChild(0).toString().equals("case"))
				bw.write(") {\n");
			else
				bw.write("{\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitSwitchBlockStatementGroup(SwitchBlockStatementGroupContext ctx) {

		try {
			bw.write("}\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitSwitchBlock(SwitchBlockContext ctx) {

		try {
			bw.write("\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitSuperinterfaces(SuperinterfacesContext ctx) {

	}

	public void exitSuperclass(SuperclassContext ctx) {
		try {
			bw.write("() ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitStaticInitializer(StaticInitializerContext ctx) {

	}

	public void exitStaticImportOnDemandDeclaration(StaticImportOnDemandDeclarationContext ctx) {

	}

	public void exitStatementWithoutTrailingSubstatement(StatementWithoutTrailingSubstatementContext ctx) {

		if (ctx.block() == null && !(ctx.getParent().getParent() instanceof BlockStatementContext)) {
			try {
				bw.write("}\n");
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public void exitStatementNoShortIf(StatementNoShortIfContext ctx) {

		if (ctx.getParent() instanceof IfThenElseStatementContext) {
			try {
				bw.write(" else ");
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public void exitStatementExpressionList(StatementExpressionListContext ctx) {

	}

	public void exitStatementExpression(StatementExpressionContext ctx) {
		inExpression = false;
	}

	public void exitStatement(StatementContext ctx) {
		try {
			if (ctx.getParent() instanceof BasicForStatementContext
					&& ctx.statementWithoutTrailingSubstatement() == null) {
				bw.write("}\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitSingleTypeImportDeclaration(SingleTypeImportDeclarationContext ctx) {

	}

	public void exitSingleStaticImportDeclaration(SingleStaticImportDeclarationContext ctx) {

	}

	public void exitSingleElementAnnotation(SingleElementAnnotationContext ctx) {
		try {
			bw.write(") ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitSimpleTypeName(SimpleTypeNameContext ctx) {

	}

	public void exitShiftExpression(ShiftExpressionContext ctx) {

	}

	public void exitReturnStatement(ReturnStatementContext ctx) {

		try {
			bw.write(";\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitResult(ResultContext ctx) {

		enterresult = false;
	}

	public void exitResourceSpecification(ResourceSpecificationContext ctx) {
		try {
			bw.write(") ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitResourceList(ResourceListContext ctx) {

	}

	public void exitResource(ResourceContext ctx) {

	}

	public void exitRelationalExpression(RelationalExpressionContext ctx) {

		try {
			if (!bracketInstance.isEmpty() && bracketInstance.lastElement() == ctx) {
				bw.write(")");
				bracketInstance.pop();
			}
			if (ctx.getParent() instanceof RelationalExpressionContext) {
				if (!operators.isEmpty() && !isInstanceOf) {
					bw.write(" " + operators.lastElement() + " ");
					operators.pop();
				}
			}

			if (!(ctx.getParent() instanceof RelationalExpressionContext))
				isInstanceOf = false;
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitReferenceType(ReferenceTypeContext ctx) {

	}

	public void exitReceiverParameter(ReceiverParameterContext ctx) {

	}

	public void exitPrimitiveType(PrimitiveTypeContext ctx) {

	}

	public void exitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(
			PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) {

	}

	public void exitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(
			PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) {

	}

	public void exitPrimaryNoNewArray_lfno_primary(PrimaryNoNewArray_lfno_primaryContext ctx) {

	}

	public void exitPrimaryNoNewArray_lfno_arrayAccess(PrimaryNoNewArray_lfno_arrayAccessContext ctx) {

	}

	public void exitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(
			PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) {

	}

	public void exitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(
			PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) {

	}

	public void exitPrimaryNoNewArray_lf_primary(PrimaryNoNewArray_lf_primaryContext ctx) {

	}

	public void exitPrimaryNoNewArray_lf_arrayAccess(PrimaryNoNewArray_lf_arrayAccessContext ctx) {

	}

	public void exitPrimaryNoNewArray(PrimaryNoNewArrayContext ctx) {

	}

	public void exitPrimary(PrimaryContext ctx) {

	}

	public void exitPreIncrementExpression(PreIncrementExpressionContext ctx) {

	}

	public void exitPreDecrementExpression(PreDecrementExpressionContext ctx) {

	}

	public void exitPostfixExpression(PostfixExpressionContext ctx) {

	}

	public void exitPostIncrementExpression_lf_postfixExpression(
			PostIncrementExpression_lf_postfixExpressionContext ctx) {

	}

	public void exitPostIncrementExpression(PostIncrementExpressionContext ctx) {
		try {
			if (!enterfor)
				bw.write("++");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitPostDecrementExpression_lf_postfixExpression(
			PostDecrementExpression_lf_postfixExpressionContext ctx) {

	}

	public void exitPostDecrementExpression(PostDecrementExpressionContext ctx) {
		try {
			if (!enterfor)
				bw.write("--");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitPackageOrTypeName(PackageOrTypeNameContext ctx) {

	}

	public void exitPackageName(PackageNameContext ctx) {

	}

	public void exitPackageModifier(PackageModifierContext ctx) {

	}

	public void exitPackageDeclaration(PackageDeclarationContext ctx) {

	}

	public void exitNumericType(NumericTypeContext ctx) {

	}

	public void exitNormalInterfaceDeclaration(NormalInterfaceDeclarationContext ctx) {

	}

	public void exitNormalClassDeclaration(NormalClassDeclarationContext ctx) {

	}

	public void exitNormalAnnotation(NormalAnnotationContext ctx) {

	}

	public void exitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {

		try {
			if (!bracketInstance.isEmpty() && bracketInstance.lastElement() == ctx) {
				bw.write(")");
				bracketInstance.pop();
			}

			if (ctx.getParent() instanceof MultiplicativeExpressionContext)
				if (!operators.isEmpty()) {
					if (!operators.isEmpty()) {
						bw.write(" " + operators.lastElement() + " ");
						operators.pop();
					}

				}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitMethodReference_lfno_primary(MethodReference_lfno_primaryContext ctx) {

	}

	public void exitMethodReference_lf_primary(MethodReference_lf_primaryContext ctx) {

	}

	public void exitMethodReference(MethodReferenceContext ctx) {

	}

	public void exitMethodName(MethodNameContext ctx) {

	}

	public void exitMethodModifier(MethodModifierContext ctx) {

	}

	public void exitMethodInvocation_lfno_primary(MethodInvocation_lfno_primaryContext ctx) {
		if (!isInstanceOf) {
			if (enterfor)
				forlimit += ")";
		}
	}

	public void exitMethodInvocation_lf_primary(MethodInvocation_lf_primaryContext ctx) {

	}

	public void exitMethodInvocation(MethodInvocationContext ctx) {

	}

	public void exitMethodHeader(MethodHeaderContext ctx) {

	}

	public void exitMethodDeclarator(MethodDeclaratorContext ctx) {

	}

	public void exitMethodDeclaration(MethodDeclarationContext ctx) {

	}

	public void exitMethodBody(MethodBodyContext ctx) {

	}

	public void exitMarkerAnnotation(MarkerAnnotationContext ctx) {

	}

	public void exitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
		if (ctx.variableDeclaratorList().variableDeclarator(0).variableInitializer() != null)
			if (ctx.variableDeclaratorList().variableDeclarator(0).variableInitializer().expression()
					.assignmentExpression().conditionalExpression().conditionalOrExpression().conditionalAndExpression()
					.inclusiveOrExpression().exclusiveOrExpression().andExpression().equalityExpression()
					.relationalExpression().shiftExpression() != null)
				if (ctx.variableDeclaratorList().variableDeclarator(0).variableInitializer().expression()
						.assignmentExpression().conditionalExpression().conditionalOrExpression()
						.conditionalAndExpression().inclusiveOrExpression().exclusiveOrExpression().andExpression()
						.equalityExpression().relationalExpression().shiftExpression().additiveExpression()
						.multiplicativeExpression().unaryExpression().unaryExpressionNotPlusMinus()
						.castExpression() != null && !isinstanceofForCast) {
					try {
						bw.write(")");
						noVariable = false;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
	}

	public void exitLiteral(LiteralContext ctx) {

	}

	public void exitLeftHandSide(LeftHandSideContext ctx) {

	}

	public void exitLastFormalParameter(LastFormalParameterContext ctx) {

	}

	public void exitLambdaParameters(LambdaParametersContext ctx) {

	}

	public void exitLambdaExpression(LambdaExpressionContext ctx) {

	}

	public void exitLambdaBody(LambdaBodyContext ctx) {

	}

	public void exitLabeledStatementNoShortIf(LabeledStatementNoShortIfContext ctx) {

	}

	public void exitLabeledStatement(LabeledStatementContext ctx) {

	}

	public void exitInterfaceType_lfno_classOrInterfaceType(InterfaceType_lfno_classOrInterfaceTypeContext ctx) {

	}

	public void exitInterfaceType_lf_classOrInterfaceType(InterfaceType_lf_classOrInterfaceTypeContext ctx) {

	}

	public void exitInterfaceTypeList(InterfaceTypeListContext ctx) {

	}

	public void exitInterfaceType(InterfaceTypeContext ctx) {

		if (!ctx.getText().equals(lastInterface)) {
			try {
				bw.write(" & ");
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	public void exitInterfaceModifier(InterfaceModifierContext ctx) {

	}

	public void exitInterfaceMethodModifier(InterfaceMethodModifierContext ctx) {

	}

	public void exitInterfaceMethodDeclaration(InterfaceMethodDeclarationContext ctx) {

	}

	public void exitInterfaceMemberDeclaration(InterfaceMemberDeclarationContext ctx) {

	}

	public void exitInterfaceDeclaration(InterfaceDeclarationContext ctx) {

	}

	public void exitInterfaceBody(InterfaceBodyContext ctx) {

		int count = ctx.getChildCount();

		try {
			bw.write(ctx.getChild(count - 1).toString() + "\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitIntegralType(IntegralTypeContext ctx) {

	}

	public void exitInstanceInitializer(InstanceInitializerContext ctx) {

	}

	public void exitInferredFormalParameterList(InferredFormalParameterListContext ctx) {

	}

	public void exitInclusiveOrExpression(InclusiveOrExpressionContext ctx) {

		try {
			if (!bracketInstance.isEmpty() && bracketInstance.lastElement() == ctx) {
				bw.write(")");
				bracketInstance.pop();
			}
			if (ctx.getParent() instanceof InclusiveOrExpressionContext) {
				if (!operators.isEmpty()) {
					if (!operators.isEmpty()) {
						bw.write(" " + operators.lastElement() + " ");
						operators.pop();
					}
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitImportDeclaration(ImportDeclarationContext ctx) {

	}

	public void exitIfThenStatement(IfThenStatementContext ctx) {
		isinstanceofForCast = false;
	}

	public void exitIfThenElseStatementNoShortIf(IfThenElseStatementNoShortIfContext ctx) {

	}

	public void exitIfThenElseStatement(IfThenElseStatementContext ctx) {

	}

	public void exitFormalParameters(FormalParametersContext ctx) {

	}

	public void exitFormalParameterList(FormalParameterListContext ctx) {

		try {
			bw.write(")");
		} catch (IOException e) {
			e.printStackTrace();
		}
		lastFormalParameter = "";
	}

	public void exitFormalParameter(FormalParameterContext ctx) {

		try {
			if (!ctx.variableDeclaratorId().getText().equals(lastFormalParameter))
				bw.write(", ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitForUpdate(ForUpdateContext ctx) {
		enterForUpdate = false;
		try {
			if (!forByValue.equals("1"))
				bw.write(".by(" + forByValue + ")");

			bw.write(")");
		} catch (IOException e) {

			e.printStackTrace();
		}
		enterfor = false;
	}

	public void exitForStatementNoShortIf(ForStatementNoShortIfContext ctx) {

	}

	public void exitForStatement(ForStatementContext ctx) {

	}

	public void exitForInit(ForInitContext ctx) {

		try {
			forCounterDatatype = ctx.getChild(0).getChild(0).getText();
			bw.write(" in (" + forinit);
		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	public void exitFloatingPointType(FloatingPointTypeContext ctx) {

	}

	public void exitFinally_(Finally_Context ctx) {

	}

	public void exitFieldModifier(FieldModifierContext ctx) {

	}

	public void exitFieldAccess_lfno_primary(FieldAccess_lfno_primaryContext ctx) {

	}

	public void exitFieldAccess_lf_primary(FieldAccess_lf_primaryContext ctx) {

	}

	public void exitFieldAccess(FieldAccessContext ctx) {

	}

	public void exitExtendsInterfaces(ExtendsInterfacesContext ctx) {

	}

	public void exitExpressionName(ExpressionNameContext ctx) {

		// try {
		// if (!(ctx.getParent() instanceof PostfixExpressionContext && ctx
		// .getParent().getChildCount() > 1))
		// if (!operators.isEmpty()) {
		//
		// if (operators.lastElement().equals(")") && !openParenthesis) {
		// bw.write(" " + operators.lastElement());
		// operators.pop();
		// }
		//
		// if (!operators.isEmpty()) {
		// bw.write(" " + operators.lastElement() + " ");
		// operators.pop();
		// }
		//
		// openParenthesis = false;
		// }
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// }

		if (inExpression && (notEqualNull || equalsequalsNull)) {
			try {
				bw.write(" exists");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void exitExpression(ExpressionContext ctx) {

		try {
			ParserRuleContext parentContext = ctx.getParent();
			if (parentContext instanceof IfThenElseStatementContext || parentContext instanceof IfThenStatementContext
					|| parentContext instanceof WhileStatementContext
					|| parentContext instanceof WhileStatementNoShortIfContext
					|| parentContext instanceof SwitchStatementContext
					|| parentContext instanceof EnhancedForStatementContext) {
				bw.write(")");
				enterEnhancedfor = false;
			} else if (!enterArgumentList.isEmpty() && !enterArrayAccess_lfno_primary
					&& !(parentContext instanceof ReturnStatementContext)) {
				ParseTree lastExpression = parentContext.getChild(parentContext.getChildCount() - 1);
				if (lastExpression instanceof ExpressionContext && ctx != lastExpression) {
					bw.write(", ");
				}
			} else if (parentContext instanceof ConditionalExpressionContext && parentContext.getChildCount() > 1) {
				bw.write(" else ");
			} else if (parentContext instanceof DoStatementContext) {
				bw.write(") {break;}\n");
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void exitExplicitConstructorInvocation(ExplicitConstructorInvocationContext ctx) {

	}

	public void exitExclusiveOrExpression(ExclusiveOrExpressionContext ctx) {

		try {
			if (!bracketInstance.isEmpty() && bracketInstance.lastElement() == ctx) {
				bw.write(")");
				bracketInstance.pop();
			}
			if (ctx.getParent() instanceof ExclusiveOrExpressionContext) {
				if (!operators.isEmpty()) {
					if (!operators.isEmpty()) {
						bw.write(" " + operators.lastElement() + " ");
						operators.pop();
					}
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitExceptionTypeList(ExceptionTypeListContext ctx) {

	}

	public void exitExceptionType(ExceptionTypeContext ctx) {

	}

	public void exitEqualityExpression(EqualityExpressionContext ctx) {

		try {
			if (!bracketInstance.isEmpty() && bracketInstance.lastElement() == ctx) {
				bw.write(")");
				bracketInstance.pop();
			}
			if (ctx.getParent() instanceof EqualityExpressionContext) {
				if (!operators.isEmpty()) {
					if (!operators.isEmpty()) {
						bw.write(" " + operators.lastElement() + " ");
						operators.pop();
					}
				}
			} else if (ctx.getChildCount() > 1) {
				notEqualNull = false;
				equalsequalsNull = false;
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitEnumDeclaration(EnumDeclarationContext ctx) {

	}

	public void exitEnumConstantName(EnumConstantNameContext ctx) {

	}

	public void exitEnumConstantModifier(EnumConstantModifierContext ctx) {

	}

	public void exitEnumConstantList(EnumConstantListContext ctx) {

	}

	public void exitEnumConstant(EnumConstantContext ctx) {

	}

	public void exitEnumBodyDeclarations(EnumBodyDeclarationsContext ctx) {

	}

	public void exitEnumBody(EnumBodyContext ctx) {

	}

	public void exitEnhancedForStatementNoShortIf(EnhancedForStatementNoShortIfContext ctx) {

	}

	public void exitEnhancedForStatement(EnhancedForStatementContext ctx) {

	}

	public void exitEmptyStatement(EmptyStatementContext ctx) {

	}

	public void exitElementValuePairList(ElementValuePairListContext ctx) {

	}

	public void exitElementValuePair(ElementValuePairContext ctx) {

	}

	public void exitElementValueList(ElementValueListContext ctx) {

	}

	public void exitElementValueArrayInitializer(ElementValueArrayInitializerContext ctx) {

	}

	public void exitElementValue(ElementValueContext ctx) {

	}

	public void exitDoStatement(DoStatementContext ctx) {
		try {
			bw.write("}\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitDims(DimsContext ctx) {

	}

	public void exitDimExprs(DimExprsContext ctx) {

	}

	public void exitDimExpr(DimExprContext ctx) {

	}

	public void exitDefaultValue(DefaultValueContext ctx) {

	}

	public void exitContinueStatement(ContinueStatementContext ctx) {

	}

	public void exitConstructorModifier(ConstructorModifierContext ctx) {

	}

	public void exitConstructorDeclarator(ConstructorDeclaratorContext ctx) {
		try {
			if (ctx.formalParameterList() == null)
				bw.write(") ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitConstructorDeclaration(ConstructorDeclarationContext ctx) {

	}

	public void exitConstructorBody(ConstructorBodyContext ctx) {
		try {
			bw.write("}\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitConstantModifier(ConstantModifierContext ctx) {

	}

	public void exitConstantExpression(ConstantExpressionContext ctx) {

	}

	public void exitConstantDeclaration(ConstantDeclarationContext ctx) {

	}

	public void exitConditionalOrExpression(ConditionalOrExpressionContext ctx) {

		try {
			if (!bracketInstance.isEmpty() && bracketInstance.lastElement() == ctx) {
				bw.write(")");
				bracketInstance.pop();
			}
			if (ctx.getParent() instanceof ConditionalOrExpressionContext) {
				if (!operators.isEmpty()) {
					if (!operators.isEmpty()) {
						bw.write(" " + operators.lastElement() + " ");
						operators.pop();
					}
				}
			}

			if (ctx.getParent() instanceof ConditionalExpressionContext && ctx.getParent().getChildCount() > 1) {
				bw.write(" then ");
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitConditionalExpression(ConditionalExpressionContext ctx) {

	}

	public void exitConditionalAndExpression(ConditionalAndExpressionContext ctx) {

		try {
			if (!bracketInstance.isEmpty() && bracketInstance.lastElement() == ctx) {
				bw.write(")");
				bracketInstance.pop();
			}
			if (ctx.getParent() instanceof ConditionalAndExpressionContext) {
				if (!operators.isEmpty()) {
					if (!operators.isEmpty()) {
						bw.write(" " + operators.lastElement() + " ");
						operators.pop();
					}
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitCompilationUnit(CompilationUnitContext ctx) {

	}

	public void exitClassType_lfno_classOrInterfaceType(ClassType_lfno_classOrInterfaceTypeContext ctx) {

	}

	public void exitClassType_lf_classOrInterfaceType(ClassType_lf_classOrInterfaceTypeContext ctx) {

	}

	public void exitClassType(ClassTypeContext ctx) {

	}

	public void exitClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) {

	}

	public void exitClassModifier(ClassModifierContext ctx) {

	}

	public void exitClassMemberDeclaration(ClassMemberDeclarationContext ctx) {

	}

	public void exitClassInstanceCreationExpression_lfno_primary(
			ClassInstanceCreationExpression_lfno_primaryContext ctx) {

	}

	public void exitClassInstanceCreationExpression_lf_primary(ClassInstanceCreationExpression_lf_primaryContext ctx) {

	}

	public void exitClassInstanceCreationExpression(ClassInstanceCreationExpressionContext ctx) {

	}

	public void exitClassDeclaration(ClassDeclarationContext ctx) {

	}

	public void exitClassBodyDeclaration(ClassBodyDeclarationContext ctx) {

	}

	public void exitCatches(CatchesContext ctx) {

	}

	public void exitCatchType(CatchTypeContext ctx) {

	}

	public void exitCatchFormalParameter(CatchFormalParameterContext ctx) {

		try {
			bw.write(") ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitCatchClause(CatchClauseContext ctx) {

	}

	public void exitCastExpression(CastExpressionContext ctx) {

	}

	public void exitBreakStatement(BreakStatementContext ctx) {

	}

	public void exitBlockStatements(BlockStatementsContext ctx) {

	}

	public void exitBlockStatement(BlockStatementContext ctx) {

	}

	public void exitBasicForStatementNoShortIf(BasicForStatementNoShortIfContext ctx) {

	}

	public void exitBasicForStatement(BasicForStatementContext ctx) {

	}

	public void exitAssignmentOperator(AssignmentOperatorContext ctx) {

	}

	public void exitAssignmentExpression(AssignmentExpressionContext ctx) {

	}

	public void exitAssignment(AssignmentContext ctx) {

		try {
			if (!bracketInstance.isEmpty() && bracketInstance.lastElement() == ctx) {
				bw.write(")");
				bracketInstance.pop();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitAssertStatement(AssertStatementContext ctx) {

	}

	public void exitArrayType(ArrayTypeContext ctx) {

	}

	public void exitArrayInitializer(ArrayInitializerContext ctx) {

	}

	public void exitArrayCreationExpression(ArrayCreationExpressionContext ctx) {

		enterArray = false;
		try {
			bw.write(")");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitArrayAccess_lfno_primary(ArrayAccess_lfno_primaryContext ctx) {

		enterArrayAccess_lfno_primary = false;
	}

	public void exitArrayAccess_lf_primary(ArrayAccess_lf_primaryContext ctx) {

	}

	public void exitArrayAccess(ArrayAccessContext ctx) {

		enterArrayAccessSet = true;
		enterArrayAccess = false;
	}

	public void exitArgumentList(ArgumentListContext ctx) {
		enterArgumentList.pop();
		try {
			bw.write(")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitAnnotationTypeMemberDeclaration(AnnotationTypeMemberDeclarationContext ctx) {

	}

	public void exitAnnotationTypeElementModifier(AnnotationTypeElementModifierContext ctx) {

	}

	public void exitAnnotationTypeElementDeclaration(AnnotationTypeElementDeclarationContext ctx) {

	}

	public void exitAnnotationTypeDeclaration(AnnotationTypeDeclarationContext ctx) {

	}

	public void exitAnnotationTypeBody(AnnotationTypeBodyContext ctx) {

	}

	public void exitAnnotation(AnnotationContext ctx) {

	}

	public void exitAndExpression(AndExpressionContext ctx) {

		try {
			if (!bracketInstance.isEmpty() && bracketInstance.lastElement() == ctx) {
				bw.write(")");
				bracketInstance.pop();
			}
			if (ctx.getParent() instanceof AndExpressionContext) {
				if (!operators.isEmpty()) {
					if (!operators.isEmpty()) {
						bw.write(" " + operators.lastElement() + " ");
						operators.pop();
					}
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitAmbiguousName(AmbiguousNameContext ctx) {

	}

	public void exitAdditiveExpression(AdditiveExpressionContext ctx) {

		try {
			if (!bracketInstance.isEmpty() && bracketInstance.lastElement() == ctx) {
				bw.write(")");
				bracketInstance.pop();
			}
			if (ctx.getParent() instanceof AdditiveExpressionContext) {
				if (!operators.isEmpty()) {
					if (!operators.isEmpty()) {
						bw.write(" " + operators.lastElement() + " ");
						operators.pop();
					}
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void exitAdditionalBound(AdditionalBoundContext ctx) {

	}

	public void enterWildcardBounds(WildcardBoundsContext ctx) {

	}

	public void enterWildcard(WildcardContext ctx) {

		try {
			if (ctx.wildcardBounds() != null)
				if ((ctx.getChild(0).getText() + ctx.wildcardBounds().getChild(0)).equals("?extends"))
					bw.write("out ");
				else if ((ctx.getChild(0).getText() + ctx.wildcardBounds().getChild(0)).equals("?super"))
					bw.write("in ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterWhileStatementNoShortIf(WhileStatementNoShortIfContext ctx) {

	}

	public void enterWhileStatement(WhileStatementContext ctx) {

		try {
			bw.write("while(");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterVariableModifier(VariableModifierContext ctx) {

		variableModifier = ctx.getText();
	}

	public void enterVariableInitializerList(VariableInitializerListContext ctx) {

	}

	public void enterVariableInitializer(VariableInitializerContext ctx) {
		try {
			if (!enterfor && !(ctx.getParent() instanceof VariableInitializerListContext))
				bw.write(" = ");
			else {

			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterVariableDeclaratorList(VariableDeclaratorListContext ctx) {

		firstVariableInList = ctx.getChild(0).getText();

		if (ctx.getChildCount() > 1) {
			multipleVariables = true;
		}
	}

	public void enterVariableDeclaratorId(VariableDeclaratorIdContext ctx) {

		try {
			String expressionName = "";
			if (Character.isUpperCase(ctx.getText().charAt(0))) {
				expressionName = "\\i" + ctx.getText();
			} else {
				expressionName = ctx.getText();
			}

			for (String str : keywords) {
				if (str.equals(ctx.getText())) {
					expressionName = "\\i" + ctx.getText();
				}
			}

			bw.write(expressionName);
			// bw.write(ctx.getChild(0).getText());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterVariableDeclarator(VariableDeclaratorContext ctx) {

		inExpression = true;

		try {
			if (enterfor) {
				if (ctx.variableInitializer() != null)
					forinit = ctx.variableInitializer().getText();
			}

			if (multipleVariables && !ctx.getText().equals(firstVariableInList)) {
				bw.write(variableListType);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterUnaryExpressionNotPlusMinus(UnaryExpressionNotPlusMinusContext ctx) {

		try {
			if (ctx.getChild(0).getText().equals("!"))
				bw.write("!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterUnaryExpression(UnaryExpressionContext ctx) {
		try {
			if (ctx.getChild(0).getText().equals("-"))
				bw.write("-");
			else if (ctx.getChild(0).getText().equals("+"))
				bw.write("+");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterUnannTypeVariable(UnannTypeVariableContext ctx) {

	}

	public void enterUnannType(UnannTypeContext ctx) {

	}

	public void enterUnannReferenceType(UnannReferenceTypeContext ctx) {

	}

	public void enterUnannInterfaceType_lfno_unannClassOrInterfaceType(
			UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) {

	}

	public void enterUnannInterfaceType_lf_unannClassOrInterfaceType(
			UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) {

	}

	public void enterUnannInterfaceType(UnannInterfaceTypeContext ctx) {

	}

	public void enterUnannClassType_lfno_unannClassOrInterfaceType(
			UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {

		String type = ctx.getChild(0).getText();
		String ceylonType = "";

		if (!(ctx.getParent().getParent() instanceof UnannArrayTypeContext)) {
			try {
				if (!enterEnhancedfor && !enterfor && !enterresult && !noVariable) {
					if (!variableModifier.equals("final")) {
						bw.write("variable ");
						variableListType = "variable ";
					}
				}
				variableModifier = "";

				ceylonType = type + " ";

				variableListType += ceylonType;

				bw.write(ceylonType);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	public void enterUnannClassType_lf_unannClassOrInterfaceType(
			UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) {

	}

	public void enterUnannClassType(UnannClassTypeContext ctx) {

		try {
			bw.write(ctx.getText() + " ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterUnannClassOrInterfaceType(UnannClassOrInterfaceTypeContext ctx) {

	}

	public void enterUnannArrayType(UnannArrayTypeContext ctx) {

		enterArray = true;

		String type = "";
		if (ctx.unannPrimitiveType() != null) {
			type = ctx.unannPrimitiveType().getText();
		} else if (ctx.unannClassOrInterfaceType() != null) {
			type = ctx.unannClassOrInterfaceType().getText();
		}
		String ceylonType = "";
		try {
			if (!variableModifier.equals("final") && !noVariable) {
				bw.write("variable ");
				variableListType = "variable ";
			}
			variableModifier = "";

			if (type.equals("int")) {
				ceylonType = "IntArray ";
			} else if (type.equals("short")) {
				ceylonType = "ShortArray ";
			} else if (type.equals("boolean")) {
				ceylonType = "BooleanArray ";
			} else if (type.equals("byte")) {
				ceylonType = "ByteArray ";
			} else if (type.equals("long")) {
				ceylonType = "LongArray ";
			} else if (type.equals("float")) {
				ceylonType = "FloatArray ";
			} else if (type.equals("double")) {
				ceylonType = "DoubleArray ";
			} else if (type.equals("char")) {
				ceylonType = "CharArray ";
			} else {
				ceylonType = "ObjectArray<" + type + "> ";
			}

			variableListType += ceylonType;

			bw.write(ceylonType);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterTypeVariable(TypeVariableContext ctx) {

	}

	public void enterTypeParameters(TypeParametersContext ctx) {

		try {
			bw.write("<");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterTypeParameterModifier(TypeParameterModifierContext ctx) {

	}

	public void enterTypeParameterList(TypeParameterListContext ctx) {

		lastTypeParameter = ctx.getChild(ctx.getChildCount() - 1).getText();
	}

	public void enterTypeParameter(TypeParameterContext ctx) {

		try {
			if (ctx.typeBound() != null) {
				if (!ctx.getChild(0).getText().equals("?")) {
					typeConstraints = true;
				}
				bw.write(ctx.getChild(0).getText());
			} else if (!typeConstraints) {
				bw.write(ctx.getText());
			}

			if (!ctx.getText().equals(lastTypeParameter))
				bw.write(", ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterTypeName(TypeNameContext ctx) {

	}

	public void enterTypeImportOnDemandDeclaration(TypeImportOnDemandDeclarationContext ctx) {

		try {
			if (!firstImport) {
				bw.write("\n}\n");
			}
			bw.write("import " + ctx.packageOrTypeName().getText() + "{\n...");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterTypeDeclaration(TypeDeclarationContext ctx) {

		if (!firstImport) {
			try {
				bw.write("\n}\n");
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		firstImport = true;
	}

	public void enterTypeBound(TypeBoundContext ctx) {

	}

	public void enterTypeArgumentsOrDiamond(TypeArgumentsOrDiamondContext ctx) {

	}

	public void enterTypeArguments(TypeArgumentsContext ctx) {

	}

	public void enterTypeArgumentList(TypeArgumentListContext ctx) {

		try {
			enterTypeArgumentsList = true;
			if (!typeConstraints)
				bw.write("<");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterTypeArgument(TypeArgumentContext ctx) {

	}

	public void enterType(TypeContext ctx) {

	}

	public void enterTryWithResourcesStatement(TryWithResourcesStatementContext ctx) {

	}

	public void enterTryStatement(TryStatementContext ctx) {

		try {
			bw.write("try ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterThrows_(Throws_Context ctx) {

	}

	public void enterThrowStatement(ThrowStatementContext ctx) {
		try {
			bw.write("throw ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterSynchronizedStatement(SynchronizedStatementContext ctx) {

	}

	public void enterSwitchStatement(SwitchStatementContext ctx) {

		try {
			bw.write("switch(");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterSwitchLabels(SwitchLabelsContext ctx) {

	}

	public void enterSwitchLabel(SwitchLabelContext ctx) {

		try {
			if (ctx.getChild(0).toString().equals("case"))
				bw.write(ctx.getChild(0) + "(");
			else
				bw.write("else ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterSwitchBlockStatementGroup(SwitchBlockStatementGroupContext ctx) {

	}

	public void enterSwitchBlock(SwitchBlockContext ctx) {

		try {
			bw.write("\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterSuperinterfaces(SuperinterfacesContext ctx) {

		try {
			bw.write("satisfies ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterSuperclass(SuperclassContext ctx) {

		try {
			bw.write(ctx.getChild(0).getText() + " " + ctx.classType().getChild(0).getText());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterStaticInitializer(StaticInitializerContext ctx) {

	}

	public void enterStaticImportOnDemandDeclaration(StaticImportOnDemandDeclarationContext ctx) {

	}

	public void enterStatementWithoutTrailingSubstatement(StatementWithoutTrailingSubstatementContext ctx) {

		if (ctx.block() == null && !(ctx.getParent().getParent() instanceof BlockStatementContext)) {
			try {
				bw.write(" {\n");
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public void enterStatementNoShortIf(StatementNoShortIfContext ctx) {

	}

	public void enterStatementExpressionList(StatementExpressionListContext ctx) {

	}

	public void enterStatementExpression(StatementExpressionContext ctx) {
		inExpression = true;
	}

	public void enterStatement(StatementContext ctx) {
		try {
			if (ctx.getParent() instanceof BasicForStatementContext
					&& ctx.statementWithoutTrailingSubstatement() == null) {
				bw.write("{\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterSingleTypeImportDeclaration(SingleTypeImportDeclarationContext ctx) {

		try {
			// bw.write("import "
			// + ctx.typeName().getChild(0).getText() + " {"
			// + ctx.typeName().getChild(2).getText() + "}\n");

			if (firstImport) {
				packageName = ctx.typeName().packageOrTypeName().getText();
				firstImport = false;
				bw.write("import " + packageName + "{\n" + ctx.typeName().getChild(2).getText());
			} else {
				if (packageName.equals(ctx.typeName().packageOrTypeName().getText())) {
					bw.write(",\n" + ctx.typeName().getChild(2).getText());
				} else {
					bw.write("\n}\n");
					packageName = ctx.typeName().packageOrTypeName().getText();
					bw.write("import " + packageName + "{" + ctx.typeName().getChild(2).getText());
				}

			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterSingleStaticImportDeclaration(SingleStaticImportDeclarationContext ctx) {

	}

	public void enterSingleElementAnnotation(SingleElementAnnotationContext ctx) {
		try {
			String text = ctx.typeName().getText();
			text = Character.toLowerCase(text.charAt(0)) + text.substring(1);
			bw.write(text + "(");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterSimpleTypeName(SimpleTypeNameContext ctx) {

	}

	public void enterShiftExpression(ShiftExpressionContext ctx) {

	}

	public void enterReturnStatement(ReturnStatementContext ctx) {

		try {
			bw.write("return ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterResourceSpecification(ResourceSpecificationContext ctx) {
		try {
			bw.write("(");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterResourceList(ResourceListContext ctx) {

	}

	public void enterResource(ResourceContext ctx) {

	}

	public void enterRelationalExpression(RelationalExpressionContext ctx) {

		if (ctx.getChildCount() > 1) {
			if (ctx.getChild(1).getText().equals("instanceof")) {
				try {
					isInstanceOf = true;
					isinstanceofForCast = true;
					if (openParenthesis) {
						bracketInstance.push(ctx);
						bw.write("(");
						openParenthesis = false;
					}

					if (inExpression)
						bw.write(ctx.getChild(0).getText() + " is " + ctx.getChild(2).getText());
					else
						bw.write("is " + ctx.getChild(2).getText() + " " + ctx.getChild(0).getText());
				} catch (IOException e) {

					e.printStackTrace();
				}
			} else if (!enterfor) {
				operators.push(ctx.getChild(1).getText());
				if (openParenthesis) {
					bracketInstance.push(ctx);
					try {
						bw.write("(");
						openParenthesis = false;
					} catch (IOException e) {

						e.printStackTrace();
					}
				}

			} else {
				forConditionOperator = ctx.getChild(1).getText();
				forlimit = ctx.getChild(2).getText();
			}
		}
	}

	public void enterReferenceType(ReferenceTypeContext ctx) {

	}

	public void enterReceiverParameter(ReceiverParameterContext ctx) {

	}

	public void enterPrimitiveType(PrimitiveTypeContext ctx) {

	}

	public void enterPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(
			PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) {

	}

	public void enterPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(
			PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) {

	}

	public void enterPrimaryNoNewArray_lfno_primary(PrimaryNoNewArray_lfno_primaryContext ctx) {

		try {
			if (!isInstanceOf) {
				if (ctx.typeName() != null) {
					bw.write(ctx.typeName().getText() + ctx.getChild(1).getText() + ctx.getChild(2).getText());
				}

				if (ctx.expression() != null) {
					openParenthesis = true;
				}
				if (!(ctx.getParent().getParent() instanceof FieldAccessContext)) {
					if (ctx.getText().equals("this")) {
						bw.write("this");
					}
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void enterPrimaryNoNewArray_lfno_arrayAccess(PrimaryNoNewArray_lfno_arrayAccessContext ctx) {

	}

	public void enterPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(
			PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) {

	}

	public void enterPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(
			PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) {

	}

	public void enterPrimaryNoNewArray_lf_primary(PrimaryNoNewArray_lf_primaryContext ctx) {

	}

	public void enterPrimaryNoNewArray_lf_arrayAccess(PrimaryNoNewArray_lf_arrayAccessContext ctx) {

	}

	public void enterPrimaryNoNewArray(PrimaryNoNewArrayContext ctx) {

	}

	public void enterPrimary(PrimaryContext ctx) {

	}

	public void enterPreIncrementExpression(PreIncrementExpressionContext ctx) {

		try {
			if (!enterfor)
				bw.write(ctx.getChild(0).getText());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterPreDecrementExpression(PreDecrementExpressionContext ctx) {

		try {
			if (!enterfor)
				bw.write("--");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterPostfixExpression(PostfixExpressionContext ctx) {

	}

	public void enterPostIncrementExpression_lf_postfixExpression(
			PostIncrementExpression_lf_postfixExpressionContext ctx) {

		try {
			bw.write("++");
			if (!operators.isEmpty()) {
				bw.write(" " + operators.lastElement() + " ");
				operators.pop();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterPostIncrementExpression(PostIncrementExpressionContext ctx) {

	}

	public void enterPostDecrementExpression_lf_postfixExpression(
			PostDecrementExpression_lf_postfixExpressionContext ctx) {

		try {
			bw.write("--");
			if (!operators.isEmpty()) {
				bw.write(" " + operators.lastElement() + " ");
				operators.pop();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterPostDecrementExpression(PostDecrementExpressionContext ctx) {

	}

	public void enterPackageOrTypeName(PackageOrTypeNameContext ctx) {

	}

	public void enterPackageName(PackageNameContext ctx) {

	}

	public void enterPackageModifier(PackageModifierContext ctx) {

	}

	public void enterPackageDeclaration(PackageDeclarationContext ctx) {

	}

	public void enterNumericType(NumericTypeContext ctx) {

	}

	public void enterNormalInterfaceDeclaration(NormalInterfaceDeclarationContext ctx) {

		enterInterfaceDeclaration = true;

		String modifier = " ";
		if (ctx.interfaceModifier(0) != null)
			if (ctx.interfaceModifier(0).getText().equals("public"))
				modifier = "shared ";

		try {
			bw.write(modifier + "interface " + ctx.Identifier() + " ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterNormalAnnotation(NormalAnnotationContext ctx) {

	}

	public void enterMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
		if (ctx.getChildCount() > 1 && !enterfor) {
			operators.push(ctx.getChild(1).getText());
			if (openParenthesis) {
				bracketInstance.push(ctx);
				try {
					bw.write("(");
					openParenthesis = false;
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public void enterMethodReference_lfno_primary(MethodReference_lfno_primaryContext ctx) {

	}

	public void enterMethodReference_lf_primary(MethodReference_lf_primaryContext ctx) {

	}

	public void enterMethodReference(MethodReferenceContext ctx) {

	}

	public void enterMethodName(MethodNameContext ctx) {

	}

	public void enterMethodInvocation_lfno_primary(MethodInvocation_lfno_primaryContext ctx) {

		try {
			if (!isInstanceOf) {
				int a = ctx.getChildCount();
				String str = "";

				for (int i = 0; i < a; i++) {
					if (ctx.getChild(i).getText().equals("("))
						break;

					str += ctx.getChild(i).getText();
				}

				String str2 = str;

				if (ctx.typeName() != null)
					str2 = str.replace(ctx.typeName().getText() + ".", "");
				for (String str1 : keywords) {
					if (str1.equals(str2)) {
						str = ctx.typeName().getText() + "." + "\\i" + str2;
					}
				}

				if (!enterfor) {
					if (str.equals("System.out.println")) {
						bw.write("print");
						if (ctx.argumentList() == null)
							bw.write("(\"\")");
					} else if (str.equals("System.out.print")) {
						bw.write("process.write");
					} else {
						bw.write(str);
						if (ctx.argumentList() == null)
							bw.write("()");
					}
				} else {
					forlimit = str + "(";
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void enterMethodInvocation_lf_primary(MethodInvocation_lf_primaryContext ctx) {

		try {
			if (!isInstanceOf) {
				int a = ctx.getChildCount();
				String str = "";

				for (int i = 0; i < a; i++) {
					if (ctx.getChild(i).getText().equals("("))
						break;

					str += ctx.getChild(i).getText();

				}

				if (str.equals("System.out.println")) {
					bw.write("print");
					if (ctx.argumentList() == null)
						bw.write("(\"\")");
				} else if (str.equals("System.out.print")) {
					bw.write("process.write");
				} else {
					bw.write(str);
					if (ctx.argumentList() == null)
						bw.write("()");
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterMethodInvocation(MethodInvocationContext ctx) {

		try {
			int a = ctx.getChildCount();
			String str = "";

			for (int i = 0; i < a; i++) {
				if (ctx.getChild(i).getText().equals("("))
					break;

				str += ctx.getChild(i).getText();

			}

			String str2 = str;

			if (ctx.typeName() != null)
				str2 = str.replace(ctx.typeName().getText() + ".", "");
			for (String str1 : keywords) {
				if (str1.equals(str2)) {
					str = ctx.typeName().getText() + "." + "\\i" + str2;
				}
			}

			if (str.equals("System.out.println")) {
				bw.write("print");
				if (ctx.argumentList() == null)
					bw.write("(\"\")");
			} else if (str.equals("System.out.print")) {
				bw.write("process.write");
			} else {
				bw.write(str);
				if (ctx.argumentList() == null)
					bw.write("()");
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterMethodHeader(MethodHeaderContext ctx) {
		try {
			if (ctx.typeParameters() != null) {
				String methodDeclarator = ctx.methodDeclarator().getChild(0).getText();

				for (String str : keywords) {
					if (str.equals(methodDeclarator)) {
						methodDeclarator = "\\i" + ctx.methodDeclarator().getChild(0).getText();
					}
				}

				bw.write(ctx.result().getText() + " " + methodDeclarator);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterMethodDeclaration(MethodDeclarationContext ctx) {

	}

	public void enterMethodBody(MethodBodyContext ctx) {

		if (typeConstraints) {
			try {
				bw.write(" given "
						+ ((MethodDeclarationContext) ctx.getParent()).methodHeader().typeParameters()
								.typeParameterList().typeParameter(0).getChild(0).getText()
						+ " satisfies " + ((MethodDeclarationContext) ctx.getParent()).methodHeader().typeParameters()
								.typeParameterList().typeParameter(0).typeBound().typeVariable().getText());
				typeConstraints = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (ctx.getText().equals(";"))
			try {
				bw.write(";\n");
			} catch (IOException e) {

				e.printStackTrace();
			}
	}

	public void enterMarkerAnnotation(MarkerAnnotationContext ctx) {

		try {
			if (ctx.typeName().getText().equals("Override")) {
				bw.write("actual ");
			} else {
				String typeName = ctx.typeName().getText();
				typeName = Character.toLowerCase(typeName.charAt(0))
						+ (typeName.length() > 1 ? typeName.substring(1) : "");
				bw.write(typeName + " ");
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterLocalVariableDeclarationStatement(LocalVariableDeclarationStatementContext ctx) {

	}

	public void enterLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {

		// TODO do this in a much more elegant way
		if (ctx.variableDeclaratorList().variableDeclarator(0).variableInitializer() != null)
			if (ctx.variableDeclaratorList().variableDeclarator(0).variableInitializer().expression()
					.assignmentExpression().conditionalExpression().conditionalOrExpression().conditionalAndExpression()
					.inclusiveOrExpression().exclusiveOrExpression().andExpression().equalityExpression()
					.relationalExpression().shiftExpression() != null)
				if (ctx.variableDeclaratorList().variableDeclarator(0).variableInitializer().expression()
						.assignmentExpression().conditionalExpression().conditionalOrExpression()
						.conditionalAndExpression().inclusiveOrExpression().exclusiveOrExpression().andExpression()
						.equalityExpression().relationalExpression().shiftExpression().additiveExpression()
						.multiplicativeExpression().unaryExpression().unaryExpressionNotPlusMinus()
						.castExpression() != null && !isinstanceofForCast) {
					try {
						noVariable = true;
						bw.write("assert(is ");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
	}

	public void enterLiteral(LiteralContext ctx) {

		if (!notEqualNull && !equalsequalsNull)
			if (!enterArrayAccess && !enterArrayAccess_lfno_primary)
				try {
					if (enterArrayAccessSet) {
						bw.write(ctx.getText() + ")");
						enterArrayAccessSet = false;
					} else if (!enterfor) {
						bw.write(ctx.getText());
					}
					// else {
					// forinit = ctx.getText();
					// forlimit = forinit;
					// }
				} catch (IOException e) {

					e.printStackTrace();
				}
	}

	public void enterLeftHandSide(LeftHandSideContext ctx) {

		if (enterfor && !enterForUpdate && ctx.arrayAccess() == null)
			try {
				bw.write(ctx.getText());
			} catch (IOException e) {

				e.printStackTrace();
			}
	}

	public void enterLastFormalParameter(LastFormalParameterContext ctx) {

		if (ctx.formalParameter() != null) {
			lastFormalParameter = ctx.formalParameter().variableDeclaratorId().getText();
		} else {
			lastFormalParameter = ctx.variableDeclaratorId().getText();
		}
	}

	public void enterLambdaParameters(LambdaParametersContext ctx) {

	}

	public void enterLambdaExpression(LambdaExpressionContext ctx) {

	}

	public void enterLambdaBody(LambdaBodyContext ctx) {

	}

	public void enterLabeledStatementNoShortIf(LabeledStatementNoShortIfContext ctx) {

	}

	public void enterLabeledStatement(LabeledStatementContext ctx) {

	}

	public void enterInterfaceType_lfno_classOrInterfaceType(InterfaceType_lfno_classOrInterfaceTypeContext ctx) {

	}

	public void enterInterfaceType_lf_classOrInterfaceType(InterfaceType_lf_classOrInterfaceTypeContext ctx) {

	}

	public void enterInterfaceTypeList(InterfaceTypeListContext ctx) {

		lastInterface = ctx.getChild(ctx.getChildCount() - 1).getText();

	}

	public void enterInterfaceType(InterfaceTypeContext ctx) {

		try {
			bw.write(ctx.classType().getChild(0).getText());
			if (ctx.classType().getChildCount() > 1 && ctx.classType().typeArguments() == null) {
				bw.write(ctx.classType().getChild(1).getText() + ctx.classType().getChild(2).getText());
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterInterfaceModifier(InterfaceModifierContext ctx) {

	}

	public void enterInterfaceMethodModifier(InterfaceMethodModifierContext ctx) {

		if (ctx.getText().equals("public"))
			try {
				bw.write("shared ");
			} catch (IOException e) {

				e.printStackTrace();
			}
	}

	public void enterInterfaceMethodDeclaration(InterfaceMethodDeclarationContext ctx) {

		if (ctx.methodBody().getText().equals(";")) {
			try {
				bw.write("formal ");
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public void enterInterfaceMemberDeclaration(InterfaceMemberDeclarationContext ctx) {

	}

	public void enterInterfaceDeclaration(InterfaceDeclarationContext ctx) {

	}

	public void enterInterfaceBody(InterfaceBodyContext ctx) {

		try {
			if (typeConstraints) {
				bw.write(" given "
						+ ((NormalInterfaceDeclarationContext) ctx.getParent()).typeParameters().typeParameterList()
								.typeParameter(0).getChild(0).getText()
						+ " satisfies " + ((NormalInterfaceDeclarationContext) ctx.getParent()).typeParameters()
								.typeParameterList().typeParameter(0).typeBound().getChild(1).getText());
			}
			typeConstraints = false;

			enterInterfaceDeclaration = false;
			bw.write(ctx.getChild(0).toString() + "\n");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterIntegralType(IntegralTypeContext ctx) {

	}

	public void enterInstanceInitializer(InstanceInitializerContext ctx) {

	}

	public void enterInferredFormalParameterList(InferredFormalParameterListContext ctx) {

	}

	public void enterInclusiveOrExpression(InclusiveOrExpressionContext ctx) {

		if (ctx.getChildCount() > 1 && !enterfor) {
			operators.push(ctx.getChild(1).getText());
			if (openParenthesis) {
				bracketInstance.push(ctx);
				try {
					bw.write("(");
					openParenthesis = false;
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public void enterImportDeclaration(ImportDeclarationContext ctx) {

	}

	public void enterIfThenStatement(IfThenStatementContext ctx) {

		try {
			bw.write(ctx.getChild(0).getText() + ctx.getChild(1).getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterIfThenElseStatementNoShortIf(IfThenElseStatementNoShortIfContext ctx) {

	}

	public void enterIfThenElseStatement(IfThenElseStatementContext ctx) {

		try {
			bw.write(ctx.getChild(0).getText() + ctx.getChild(1).getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterFormalParameters(FormalParametersContext ctx) {

	}

	public void enterFormalParameterList(FormalParameterListContext ctx) {
		try {
			bw.write("(");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterFormalParameter(FormalParameterContext ctx) {

	}

	public void enterForUpdate(ForUpdateContext ctx) {

		try {
			enterForUpdate = true;
			if (forConditionOperator.equals("<=") || forConditionOperator.equals(">="))
				bw.write(".." + forlimit + ")");
			else if (isNumeric(forlimit)) {
				if (forCounterDatatype.equals("int"))
					bw.write(".." + (int) (Double.parseDouble(forlimit) - 1) + ")");
				else
					bw.write(".." + (Double.parseDouble(forlimit) - 1) + ")");
			} else
				bw.write(".." + forlimit + " - 1)");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterForStatementNoShortIf(ForStatementNoShortIfContext ctx) {

	}

	public void enterForStatement(ForStatementContext ctx) {

	}

	public void enterForInit(ForInitContext ctx) {

	}

	public void enterFloatingPointType(FloatingPointTypeContext ctx) {

	}

	public void enterFinally_(Finally_Context ctx) {

		try {
			bw.write("finally ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterFieldModifier(FieldModifierContext ctx) {

		variableModifier = ctx.getText();
		try {
			if (!enterfor && !enterresult) {
				if (variableModifier.equals("public"))

					bw.write("shared ");

			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterFieldDeclaration(FieldDeclarationContext ctx) {

	}

	public void enterFieldAccess_lfno_primary(FieldAccess_lfno_primaryContext ctx) {

	}

	public void enterFieldAccess_lf_primary(FieldAccess_lf_primaryContext ctx) {

	}

	public void enterFieldAccess(FieldAccessContext ctx) {

		if (ctx.primary().getText().equals("this"))
			try {
				bw.write("this." + ctx.getChild(2));
			} catch (IOException e) {

				e.printStackTrace();
			}
	}

	public void enterExtendsInterfaces(ExtendsInterfacesContext ctx) {

		try {
			bw.write("satisfies ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterExpressionStatement(ExpressionStatementContext ctx) {

	}

	public void enterExpressionName(ExpressionNameContext ctx) {

		try {
			String expressionName = "";
			if (Character.isUpperCase(ctx.getText().charAt(0))) {
				expressionName = "\\i" + ctx.getText();
			} else {
				expressionName = ctx.getText();
			}

			for (String str : keywords) {
				if (str.equals(ctx.getText())) {
					expressionName = "\\i" + ctx.getText();
				}
			}

			if (!enterArrayAccess && !enterArrayAccess_lfno_primary) {
				if (equalsequalsNull) {
					bw.write("!");
				}

				if (enterArrayAccessSet) {
					bw.write(expressionName + ")");
					enterArrayAccessSet = false;
				} else if (!isInstanceOf)
					if (!enterfor) {
						if (enterEnhancedfor) {
							bw.write(" in ");
						}
						bw.write(expressionName);
					}
				// else {
				// forinit = ctx.getText();
				// forlimit = forinit;
				// }
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterExpression(ExpressionContext ctx) {

	}

	public void enterExplicitConstructorInvocation(ExplicitConstructorInvocationContext ctx) {

	}

	public void enterExclusiveOrExpression(ExclusiveOrExpressionContext ctx) {

		if (ctx.getChildCount() > 1 && !enterfor) {
			operators.push(ctx.getChild(1).getText());
			if (openParenthesis) {
				bracketInstance.push(ctx);
				try {
					bw.write("(");
					openParenthesis = false;
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public void enterExceptionTypeList(ExceptionTypeListContext ctx) {

	}

	public void enterExceptionType(ExceptionTypeContext ctx) {

	}

	public void enterEqualityExpression(EqualityExpressionContext ctx) {
		try {
			if (ctx.getChildCount() > 1) {
				if (!ctx.getChild(2).getText().equals("null")) {
					operators.push(ctx.getChild(1).getText());
				} else {
					if (!inExpression) {
						bw.write("exists ");
					}
					if (ctx.getChild(1).getText().equals("!="))
						notEqualNull = true;
					else if (ctx.getChild(1).getText().equals("=="))
						equalsequalsNull = true;
				}
				if (openParenthesis) {
					bracketInstance.push(ctx);
					bw.write("(");
					openParenthesis = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterEnumDeclaration(EnumDeclarationContext ctx) {

	}

	public void enterEnumConstantName(EnumConstantNameContext ctx) {

	}

	public void enterEnumConstantModifier(EnumConstantModifierContext ctx) {

	}

	public void enterEnumConstantList(EnumConstantListContext ctx) {

	}

	public void enterEnumConstant(EnumConstantContext ctx) {

	}

	public void enterEnumBodyDeclarations(EnumBodyDeclarationsContext ctx) {

	}

	public void enterEnumBody(EnumBodyContext ctx) {

	}

	public void enterEnhancedForStatementNoShortIf(EnhancedForStatementNoShortIfContext ctx) {

	}

	public void enterEnhancedForStatement(EnhancedForStatementContext ctx) {

		try {
			enterEnhancedfor = true;
			bw.write("for(");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterEmptyStatement(EmptyStatementContext ctx) {

	}

	public void enterElementValuePairList(ElementValuePairListContext ctx) {

	}

	public void enterElementValuePair(ElementValuePairContext ctx) {

	}

	public void enterElementValueList(ElementValueListContext ctx) {

	}

	public void enterElementValueArrayInitializer(ElementValueArrayInitializerContext ctx) {

	}

	public void enterElementValue(ElementValueContext ctx) {

	}

	public void enterDoStatement(DoStatementContext ctx) {
		try {
			bw.write("while(true) ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterDims(DimsContext ctx) {

	}

	public void enterDimExprs(DimExprsContext ctx) {

	}

	public void enterDimExpr(DimExprContext ctx) {

	}

	public void enterDefaultValue(DefaultValueContext ctx) {

	}

	public void enterContinueStatement(ContinueStatementContext ctx) {

	}

	public void enterConstructorModifier(ConstructorModifierContext ctx) {
		if (ctx.getText().equals("public"))
			try {
				bw.write("shared ");
			} catch (IOException e) {

				e.printStackTrace();
			}
	}

	public void enterConstructorDeclarator(ConstructorDeclaratorContext ctx) {
		try {
			bw.write("new ");
			if (ctx.formalParameterList() == null)
				bw.write("(");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterConstructorDeclaration(ConstructorDeclarationContext ctx) {

	}

	public void enterConstructorBody(ConstructorBodyContext ctx) {
		try {
			bw.write("{\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterConstantModifier(ConstantModifierContext ctx) {

	}

	public void enterConstantExpression(ConstantExpressionContext ctx) {

	}

	public void enterConstantDeclaration(ConstantDeclarationContext ctx) {

	}

	public void enterConditionalOrExpression(ConditionalOrExpressionContext ctx) {

		if (ctx.getChildCount() > 1) {
			operators.push(ctx.getChild(1).getText());
			if (openParenthesis) {
				bracketInstance.push(ctx);
				try {
					bw.write("(");
					openParenthesis = false;
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public void enterConditionalExpression(ConditionalExpressionContext ctx) {
		// TODO add brackets if they are not present
		if (ctx.getChildCount() > 1 && ctx.getChild(1).getText().equals("?")) {
			try {
				bw.write("if");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void enterConditionalAndExpression(ConditionalAndExpressionContext ctx) {

		if (ctx.getChildCount() > 1) {
			operators.push(ctx.getChild(1).getText());
			if (openParenthesis) {
				bracketInstance.push(ctx);
				try {
					bw.write("(");
					openParenthesis = false;
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public void enterCompilationUnit(CompilationUnitContext ctx) {

	}

	public void enterClassType_lfno_classOrInterfaceType(ClassType_lfno_classOrInterfaceTypeContext ctx) {
		if (!(ctx.getParent().getParent() instanceof ArrayCreationExpressionContext)
				&& !(ctx.getParent().getParent().getParent() instanceof CastExpressionContext))
			try {
				if (enterTypeArgumentsList && !typeConstraints) {
					bw.write(ctx.getChild(0).getText());
				}

				if (!isInstanceOf && !enterTypeArgumentsList && !enterTypeParametersList && !typeConstraints) {
					bw.write(ctx.getText());
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
	}

	public void enterClassType_lf_classOrInterfaceType(ClassType_lf_classOrInterfaceTypeContext ctx) {

	}

	public void enterClassType(ClassTypeContext ctx) {

	}

	public void enterClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) {

	}

	public void enterClassModifier(ClassModifierContext ctx) {

	}

	public void enterClassMemberDeclaration(ClassMemberDeclarationContext ctx) {

	}

	public void enterClassInstanceCreationExpression_lfno_primary(
			ClassInstanceCreationExpression_lfno_primaryContext ctx) {

		try {
			if (ctx.classBody() != null) {
				if (ctx.argumentList() == null) {
					bw.write("object satisfies "); // assuming it is an
													// interface because of no
													// arguments
				} else {
					bw.write("object extends ");
				}
			}

			bw.write(ctx.getChild(1).getText());

			if (ctx.typeArgumentsOrDiamond() != null) {
				enterTypeArgumentsList = true;
			} else if (ctx.argumentList() != null) {
				enterArgumentList.push(true);
			} else if (ctx.classBody() == null) {
				bw.write("()");
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterClassInstanceCreationExpression_lf_primary(ClassInstanceCreationExpression_lf_primaryContext ctx) {

	}

	public void enterClassInstanceCreationExpression(ClassInstanceCreationExpressionContext ctx) {

	}

	public void enterClassDeclaration(ClassDeclarationContext ctx) {

	}

	public void enterClassBodyDeclaration(ClassBodyDeclarationContext ctx) {

	}

	public void enterCatches(CatchesContext ctx) {

	}

	public void enterCatchType(CatchTypeContext ctx) {

	}

	public void enterCatchFormalParameter(CatchFormalParameterContext ctx) {

		try {
			bw.write("(");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterCatchClause(CatchClauseContext ctx) {

		try {
			bw.write("catch ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterCastExpression(CastExpressionContext ctx) {

	}

	public void enterBreakStatement(BreakStatementContext ctx) {

	}

	public void enterBlockStatements(BlockStatementsContext ctx) {

	}

	public void enterBlockStatement(BlockStatementContext ctx) {

	}

	public void enterBasicForStatementNoShortIf(BasicForStatementNoShortIfContext ctx) {

	}

	public void enterBasicForStatement(BasicForStatementContext ctx) {

		try {
			enterfor = true;
			bw.write(ctx.getChild(0).getText() + ctx.getChild(1).getText());
		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	public void enterAssignmentOperator(AssignmentOperatorContext ctx) {

		try {
			if (!enterfor && !enterArrayAccessSet)
				bw.write(ctx.getText());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterAssignmentExpression(AssignmentExpressionContext ctx) {

	}

	public void enterAssertStatement(AssertStatementContext ctx) {

	}

	public void enterArrayType(ArrayTypeContext ctx) {

	}

	public void enterArrayInitializer(ArrayInitializerContext ctx) {

	}

	public void enterArrayCreationExpression(ArrayCreationExpressionContext ctx) {

		String type = "";
		if (ctx.primitiveType() != null)
			type = ctx.primitiveType().getText();
		else if (ctx.classOrInterfaceType() != null) {
			type = ctx.classOrInterfaceType().getText();
		}
		String ceylonType = "";
		try {
			if (type.equals("int")) {
				ceylonType = "IntArray";
			} else if (type.equals("short")) {
				ceylonType = "ShortArray";
			} else if (type.equals("boolean")) {
				ceylonType = "BooleanArray ";
			} else if (type.equals("byte")) {
				ceylonType = "ByteArray";
			} else if (type.equals("long")) {
				ceylonType = "LongArray";
			} else if (type.equals("float")) {
				ceylonType = "FloatArray";
			} else if (type.equals("double")) {
				ceylonType = "DoubleArray";
			} else if (type.equals("char")) {
				ceylonType = "CharArray";
			} else {
				ceylonType = "ObjectArray<" + type + ">";
			}

			bw.write(ceylonType + "(");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterArrayAccess_lfno_primary(ArrayAccess_lfno_primaryContext ctx) {

		try {
			enterArrayAccess_lfno_primary = true;

			if (equalsequalsNull) {
				bw.write("!");
			}

			bw.write(ctx.expressionName().getText() + ".get(" + ctx.expression(0).getText() + ")");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterArrayAccess_lf_primary(ArrayAccess_lf_primaryContext ctx) {

	}

	public void enterArrayAccess(ArrayAccessContext ctx) {

		try {
			enterArrayAccess = true;
			if (ctx.expressionName() != null)
				bw.write(ctx.expressionName().getText() + ".set(" + ctx.expression(0).getText() + ", ");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void enterArgumentList(ArgumentListContext ctx) {

		enterArgumentList.push(true);
		try {
			bw.write("(");
		} catch (IOException e) {

			e.printStackTrace();
		}

		lastActualParameter = ctx.getChild(ctx.getChildCount() - 1).getText();
	}

	public void enterAnnotationTypeMemberDeclaration(AnnotationTypeMemberDeclarationContext ctx) {

	}

	public void enterAnnotationTypeElementModifier(AnnotationTypeElementModifierContext ctx) {

	}

	public void enterAnnotationTypeElementDeclaration(AnnotationTypeElementDeclarationContext ctx) {

	}

	public void enterAnnotationTypeDeclaration(AnnotationTypeDeclarationContext ctx) {

	}

	public void enterAnnotationTypeBody(AnnotationTypeBodyContext ctx) {

	}

	public void enterAnnotation(AnnotationContext ctx) {

	}

	public void enterAndExpression(AndExpressionContext ctx) {

		if (ctx.getChildCount() > 1) {
			operators.push(ctx.getChild(1).getText());
			if (openParenthesis) {
				bracketInstance.push(ctx);
				try {
					bw.write("(");
					openParenthesis = false;
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public void enterAmbiguousName(AmbiguousNameContext ctx) {

	}

	public void enterAdditiveExpression(AdditiveExpressionContext ctx) {

		if (ctx.getChildCount() > 1 && !enterfor) {
			operators.push(ctx.getChild(1).getText());
			if (openParenthesis) {
				bracketInstance.push(ctx);
				try {
					bw.write("(");
					openParenthesis = false;
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public void enterAdditionalBound(AdditionalBoundContext ctx) {

	}
}
