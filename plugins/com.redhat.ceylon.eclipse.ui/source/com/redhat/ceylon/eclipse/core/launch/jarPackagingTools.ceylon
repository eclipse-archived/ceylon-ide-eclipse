import ceylon.interop.java {
    JavaList,
    JavaStringList
}

import org.eclipse.ceylon.ide.common.model {
    AnyCeylonProject
}
import org.eclipse.ceylon.model.typechecker.model {
    Module
}

import java.io {
    File
}
import java.lang {
    ProcessBuilder
}

String moduleString(Module moduleToJar, String sep="/")
    => moduleToJar.defaultModule 
    then moduleToJar.nameAsString
    else moduleToJar.nameAsString + sep + moduleToJar.version;

shared [JarPackagingTool+] jarCreationTools = [
    JarPackagingTool {
        type = "Fat Jar";
        outputFileName(Module moduleToJar) => moduleString(moduleToJar, "-") + ".jar";
        canStopInMain = true;
        function doCreateFile(File ceylonBinary, File outputFile, AnyCeylonProject ceylonProject, Module moduleToJar, File workingDirectory, String runFunction) {
            value processBuilder = ProcessBuilder(JavaList(JavaStringList(
                [
                    ceylonBinary.absolutePath,
                    "fat-jar",
                    "--out=``outputFile``",
                    "--rep=``ceylonProject.ceylonModulesOutputDirectory.absolutePath``",
                    "--run=``runFunction.empty then "run" else runFunction``",
                    "--force",
                    for (repo in ceylonProject.ceylonRepositories) "--rep=``repo``" 
                ]
                .withTrailing(moduleString(moduleToJar)))));
            processBuilder.directory(workingDirectory);
            return processBuilder;
        }
    },
    JarPackagingTool {
        type = "Swarm";
        outputFileName(Module moduleToJar) => moduleString(moduleToJar, "-") + "-swarm.jar";
        canRunFunction = false;
        function doCreateFile(File ceylonBinary, File outputFile, AnyCeylonProject ceylonProject, Module moduleToJar, File workingDirectory, String runFunction) {
            value processBuilder = ProcessBuilder(JavaList(JavaStringList(
                [
                    ceylonBinary.absolutePath,
                    "swarm",
                    "--provided-module=javax:javaee-api",
                    "--out=``outputFile.parent``",
                    "--rep=``ceylonProject.ceylonModulesOutputDirectory.absolutePath``",
                    for (repo in ceylonProject.ceylonRepositories) "--rep=``repo``" 
                ]
                .withTrailing(moduleString(moduleToJar)))));
            processBuilder.directory(workingDirectory);
            return processBuilder;
        }
    },  
    JarPackagingTool {
        type = "Assembly";
        outputFileName(Module moduleToJar) => moduleString(moduleToJar, "-") + ".cas";
        function doCreateFile(File ceylonBinary, File outputFile, AnyCeylonProject ceylonProject, Module moduleToJar, File workingDirectory, String runFunction) {
            value processBuilder = ProcessBuilder(JavaList(JavaStringList(
                [
                    ceylonBinary.absolutePath,
                    "assemble",
                    "--out=``outputFile``",
                    "--jvm",
                    "--include-runtime",
                    "--include-language",
                    "--rep=``ceylonProject.ceylonModulesOutputDirectory.absolutePath``",
                    "--run=``runFunction.empty then "run" else runFunction``",
                    "--force",
                    for (repo in ceylonProject.ceylonRepositories) "--rep=``repo``" 
                ]
                .withTrailing(moduleString(moduleToJar)))));
            processBuilder.directory(workingDirectory);
            return processBuilder;
        }
    }    
];

shared Map<String, JarPackagingTool> jarCreationToolsMap = map { 
    for (tool in jarCreationTools)
    tool.type -> tool
};

