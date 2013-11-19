package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.compiler.java.util.Util.quoteIfJavaKeyword;
import static com.redhat.ceylon.compiler.typechecker.io.impl.Helper.computeRelativePath;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.util.Util;

import com.redhat.ceylon.compiler.java.codegen.CeylonCompilationUnit;
import com.redhat.ceylon.compiler.java.loader.CeylonClassReader;
import com.redhat.ceylon.compiler.java.tools.CeylonLog;
import com.redhat.ceylon.compiler.java.tools.CeylonPhasedUnit;
import com.redhat.ceylon.compiler.java.tools.LanguageCompiler.CompilerDelegate;
import com.redhat.ceylon.compiler.loader.SourceDeclarationVisitor;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.AnalysisError;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.RecognitionError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Position;
import com.sun.tools.javac.util.Position.LineMap;

final class JdtCompilerDelegate implements CompilerDelegate {
	private final JDTModelLoader modelLoader;
	private final IProject project;
	private final TypeChecker typeChecker;
	private final WeakReference<com.sun.tools.javac.util.Context> contextRef;

	JdtCompilerDelegate(JDTModelLoader modelLoader,
			IProject project, TypeChecker typeChecker,
			com.sun.tools.javac.util.Context context) {
		this.modelLoader = modelLoader;
		this.project = project;
		this.typeChecker = typeChecker;
		contextRef = new WeakReference<Context>(context);
	}

	@Override
	public ModuleManager getModuleManager() {
	    return typeChecker.getPhasedUnits().getModuleManager();
	}

	@Override
	public PhasedUnit getExternalSourcePhasedUnit(
	        VirtualFile srcDir, VirtualFile file) {
	    return typeChecker.getPhasedUnits()
	    		.getPhasedUnitFromRelativePath(computeRelativePath(file, srcDir));
	}
	
    @Override
    public void typeCheck(java.util.List<PhasedUnit> listOfUnits) {
        // Do nothing : 
        //   alreadyDone by the IncrementalBuilder before calling the Compiler for binary generation
    }
    
    private void buildListOfCompiledModules(Module module, Set<ProjectSourceFile> listOfModules) {
        Unit moduleUnit = module.getUnit();
        if (moduleUnit instanceof ProjectSourceFile && !listOfModules.contains(moduleUnit)) {
            listOfModules.add((ProjectSourceFile) moduleUnit);
            for (ModuleImport imp : module.getImports()) {
                Module importedModule =imp.getModule();
                buildListOfCompiledModules(importedModule, listOfModules);
            }
        }
    }
    
    @Override
    public void visitModules(PhasedUnits phasedUnits) {
        Set<ProjectSourceFile> compiledModules = new HashSet<>();  
        for (PhasedUnit pu : phasedUnits.getPhasedUnits()) {
            buildListOfCompiledModules(pu.getPackage().getModule(), compiledModules);
        }
        
        for (ProjectSourceFile compiledModule : compiledModules) {
            PhasedUnit pu = compiledModule.getPhasedUnit();
            boolean hasErrors = false;
            for (Message e : pu.getCompilationUnit().getErrors()) {
                if ( (e instanceof RecognitionError) || 
                     (e instanceof AnalysisError)) {
                    hasErrors = true;
                }
            }
            if (! hasErrors) {
                IFile moduleResource = compiledModule.getFileResource();
                File moduleFile = moduleResource.getRawLocation().toFile();
                Context context = contextRef.get();
                assert(context != null);
                JavacFileManager fileManager = (JavacFileManager) context.get(JavaFileManager.class);
                Iterator<? extends JavaFileObject> files = fileManager.getJavaFileObjects(moduleFile).iterator();
                if (files.hasNext()) {
                    JavaFileObject fileObject = files.next();                    
                    char[] chars = new char[0];
                    try {
                        chars = Util.getResourceContentsAsCharArray(moduleResource);
                    } catch (JavaModelException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    LineMap map = Position.makeLineMap(chars, chars.length, false);
                    CeylonPhasedUnit cpu = new CeylonPhasedUnit(pu, fileObject, map);
                    phasedUnits.addPhasedUnit(pu.getUnitFile(), cpu);
                }
            }
        }
    }

    @Override
    public void prepareForTypeChecking(com.sun.tools.javac.util.List<JCCompilationUnit> trees) {
        final Context context = contextRef.get();
        if (context == null) return;
        
        for(JCCompilationUnit treeHolder: trees){
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
                            // we can pass null here because the module is not currently used
                            ClassMirror previousClass = modelLoader.lookupClassMirror(null, fqn);
                            CeylonLog.instance(context).error("ceylon", "Duplicate declaration error: " + 
                                    fqn + " is declared twice: once in " + tree.getSourceFile() + 
                                    " and again in: " + CeylonBuilder.fileName(previousClass));
                        }
                    }
                });
            }
        }

        /*
        Context context = contextRef.get();
        if (context == null) return;

        CeyloncFileManager fileManager = (CeyloncFileManager) context.get(JavaFileManager.class);

        for (String declarationName : modelLoader.getSourceDeclarations()) {
            SourceDeclarationHolder declarationHolder = modelLoader.getSourceDeclaration(declarationName);
            VirtualFile file = declarationHolder.getPhasedUnit().getUnitFile();
            
            if (! (file instanceof IFileVirtualFile)) {
                continue;
            }
            
            File sourceFile = ((IFileVirtualFile) file).getResource().getLocation().toFile();
            JavaFileObject javacFile = fileManager.getJavaFileObjects(sourceFile).iterator().next();

            Tree.Declaration decl = declarationHolder.getAstDeclaration();
            
            String name = quoteIfJavaKeyword(decl.getIdentifier().getText());
            String pkgName = "";
            Scope container = decl.getDeclarationModel().getContainer(); 
            if (container instanceof com.redhat.ceylon.compiler.typechecker.model.Package) {
                pkgName = container.getQualifiedNameString();
            }
            String fqn = pkgName.isEmpty() ? name : pkgName+"."+name;
            
            try{
                CeylonClassReader.instance(context)
                        .enterClass(Names.instance(context).fromString(fqn), 
                                javacFile);
            }
            catch (AssertionError error){
                // this happens when we have already registered a source 
                // file for this decl, so let's print out a helpful message
                // see https://github.com/ceylon/ceylon-compiler/issues/250
                // we can pass null here because the module is not currently used
                ClassMirror previousClass = modelLoader.lookupClassMirror(null, fqn);
                CeylonLog.instance(context).error("ceylon", "Duplicate declaration error: " + 
                        fqn + " is declared twice: once in " + javacFile + 
                        " and again in: " + CeylonBuilder.fileName(previousClass));
            }
        }
        */
        // Do nothing : 
        //   alreadyDone by the IncrementalBuilder before calling the Compiler for binary generation
    }
}