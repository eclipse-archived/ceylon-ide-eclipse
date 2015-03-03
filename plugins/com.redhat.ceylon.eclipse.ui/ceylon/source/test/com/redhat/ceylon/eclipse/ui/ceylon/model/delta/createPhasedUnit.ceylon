import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    CeylonIterable,
    javaString,
    JavaList
}

import com.redhat.ceylon.cmr.ceylon {
    CeylonUtils {
        repoManager
    }
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker {
        languageModuleVersion=\iLANGUAGE_MODULE_VERSION
    }
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    ModuleValidator
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit,
    PhasedUnits,
    Context
}
import com.redhat.ceylon.compiler.typechecker.io {
    VFS,
    VirtualFile
}
import com.redhat.ceylon.compiler.typechecker.model {
    Module
}
import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    ...
}

import java.io {
    InputStream,
    ByteArrayInputStream
}
import java.util {
    JList=List,
    Collections {
        emptyList
    }
}

PhasedUnit? createPhasedUnit(String contents, String path) {
    value repositoryManager = repoManager()
                .offline(true)
//                .cwd(cwd)
//                .systemRepo(systemRepo)
//                .extraUserRepos(getReferencedProjectsOutputRepositories(project))
//                .logger(new EclipseLogger())
//                .isJDKIncluded(true)
                .buildManager();

    value context = Context(repositoryManager, VFS());
    value phasedUnits = PhasedUnits(context);
    
    abstract class TestVirtualFile(path) satisfies VirtualFile {
        suppressWarnings("expressionTypeNothing")
        shared default actual InputStream inputStream => nothing;
        shared actual String name => path.split('/'.equals, true, true).last;
        shared actual String path;
    }
    
    class TestFile(String path, String contents) extends TestVirtualFile(path) {
        shared actual JList<VirtualFile> children => emptyList<VirtualFile>();
        shared actual Boolean folder => false;
        shared actual InputStream inputStream => ByteArrayInputStream(javaString(contents + " ").bytes);
    }
    
    class TestDirectory(String path) extends TestVirtualFile(path) {
        shared actual Boolean folder => true;
        value theChildren = ArrayList<TestVirtualFile>();

        shared TestFile createFile(String filePath, String contents) {
            return createChild(filePath.split('/'.equals).sequence(), contents);
        }
        
        shared actual JList<VirtualFile> children => JavaList<VirtualFile>(theChildren.collect<VirtualFile>((TestVirtualFile element) => element));
        
        TestFile createChild([String*] filePath, String contents) {
            if (nonempty filePath, nonempty rest = filePath.rest) {
                value subFolder =  TestDirectory ("/".join { path, filePath.first });
                theChildren.add(subFolder);
                return subFolder.createChild(rest, contents);
                
            } else {
                value file = TestFile("/".join { path, *filePath }, contents);
                theChildren.add(file);
                return file;
            }
        }
    }

    
    value srcDir = TestDirectory("");
    value file = srcDir.createFile(path, contents);
    
    phasedUnits.parseUnit(srcDir);

    phasedUnits.moduleManager.prepareForTypeChecking();
    phasedUnits.visitModules();
    phasedUnits.moduleManager.modulesVisited();
    
    //By now the language module version should be known (as local)
    //or we should use the default one.
    Module languageModule = context.modules.languageModule;
    String? version = languageModule.version;
    if (version is Null) {
        languageModule.version = languageModuleVersion;
    }
    
    value moduleValidator = ModuleValidator(context, phasedUnits);
    moduleValidator.verifyModuleDependencyTree();
    
    value listOfUnits = CeylonIterable(phasedUnits.phasedUnits);
    
    for (pu in listOfUnits) {
        pu.validateTree();
        pu.scanDeclarations();
    }
    for (pu in listOfUnits) {
        pu.scanTypeDeclarations();
    }
    for (pu in listOfUnits) {
        pu.validateRefinement();
    }
    for (pu in listOfUnits) {
        pu.analyseTypes();
    }
    for (pu in listOfUnits) {
        pu.analyseFlow();
    }
    for (pu in listOfUnits) {
        pu.analyseUsage();
    }
    
    return phasedUnits.getPhasedUnit(file);
}
