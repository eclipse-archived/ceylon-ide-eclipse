package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.compiler.java.util.Util.quoteIfJavaKeyword;

import java.util.List;

import com.redhat.ceylon.compiler.java.codegen.CeylonCompilationUnit;
import com.redhat.ceylon.compiler.java.loader.CeylonClassReader;
import com.redhat.ceylon.compiler.java.tools.CeylonLog;
import com.redhat.ceylon.compiler.loader.SourceDeclarationVisitor;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader.SourceFileObjectManager;
import com.sun.tools.javac.util.Names;

final class JdtSourceFileObjectManager implements
		SourceFileObjectManager {
	private final com.sun.tools.javac.util.Context context;
	private final JDTModelLoader modelLoader;

	JdtSourceFileObjectManager(
			com.sun.tools.javac.util.Context context,
			JDTModelLoader modelLoader) {
		this.context = context;
		this.modelLoader = modelLoader;
	}

	@Override
	public void setupSourceFileObjects(List<?> treeHolders) {
		for(Object treeHolder: treeHolders){
			if (treeHolder instanceof CeylonCompilationUnit) {
				final CeylonCompilationUnit tree = (CeylonCompilationUnit) treeHolder;
				CompilationUnit ceylonTree = tree.ceylonTree;
				final String pkgName = tree.getPackageName() != null ? 
						tree.getPackageName().toString() : "";
				ceylonTree.visit(new SourceDeclarationVisitor() {
					@Override
					public void loadFromSource(Tree.Declaration decl) {
						String name = quoteIfJavaKeyword(decl.getIdentifier().getText());
						String fqn = pkgName.isEmpty() ? name : pkgName+"."+name;
						try{
							CeylonClassReader.instance(context)
							        .enterClass(Names.instance(context).fromString(fqn), 
									        tree.getSourceFile());
						}
						catch (AssertionError error){
							// this happens when we have already registered a source 
							// file for this decl, so let's print out a helpful message
							// see https://github.com/ceylon/ceylon-compiler/issues/250
							ClassMirror previousClass = modelLoader.lookupClassMirror(fqn);
							CeylonLog.instance(context).error("ceylon", "Duplicate declaration error: " + 
									fqn + " is declared twice: once in " + tree.getSourceFile() + 
									" and again in: " + CeylonBuilder.fileName(previousClass));
						}
					}
				});
			}
		}
	}
}