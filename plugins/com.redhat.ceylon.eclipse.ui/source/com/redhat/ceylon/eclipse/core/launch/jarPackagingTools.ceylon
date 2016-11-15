import ceylon.interop.java {
    JavaList,
    JavaStringList
}

import com.redhat.ceylon.ide.common.model {
    AnyCeylonProject
}
import com.redhat.ceylon.model.typechecker.model {
    Module
}

import java.io {
    File
}
import java.lang {
    ProcessBuilder
}

shared [JarPackagingTool+] jarCreationTools = [
    JarPackagingTool {
        type = "FatJar";
        canStopInMain = true;
        outputFileSuffix = "";
        function doCreateFile(File ceylonBinary, File outputFile, AnyCeylonProject ceylonProject, Module moduleToJar, File workingDirectory) {
            value processBuilder = ProcessBuilder(JavaList(JavaStringList(
                concatenate({
                    ceylonBinary.absolutePath,
                    "fat-jar",
                    "--out=``outputFile``",
                    "--rep=``ceylonProject.ceylonModulesOutputDirectory.absolutePath``",
                    for (repo in ceylonProject.ceylonRepositories) "--rep=``repo``" }, { 
                    "``moduleToJar.nameAsString``/``moduleToJar.version``"
                }))));
            processBuilder.directory(workingDirectory);
            return processBuilder;
        }
    },
    JarPackagingTool {
        type = "Swarm";
        canStopInMain = false;
        function doCreateFile(File ceylonBinary, File outputFile, AnyCeylonProject ceylonProject, Module moduleToJar, File workingDirectory) {
            value processBuilder = ProcessBuilder(JavaList(JavaStringList(
                concatenate({
                    ceylonBinary.absolutePath,
                    "swarm",
                    "--provided-module=javax:javaee-api",
                    "--out=``outputFile.parent``",
                    "--rep=``ceylonProject.ceylonModulesOutputDirectory.absolutePath``",
                    for (repo in ceylonProject.ceylonRepositories) "--rep=``repo``" }, { 
                    "``moduleToJar.nameAsString``/``moduleToJar.version``"
                }))));
            processBuilder.directory(workingDirectory);
            return processBuilder;
        }
    }    
];

shared Map<String, JarPackagingTool> jarCreationToolsMap = map { 
    for (tool in jarCreationTools)
    tool.type -> tool
};

