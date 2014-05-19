package com.redhat.ceylon.eclipse.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

public class CarUtils {
    public static Properties retrieveMappingFile(File carFile) {
        if (carFile != null) {
            try {
                return retrieveMappingFile(new ZipFile(carFile));
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }
        return new Properties();
    }
    
    public static Properties retrieveMappingFile(ZipFile carFile) {
        Properties mapping = new Properties();
        if (carFile != null && carFile.isValidZipFile()) {
            FileHeader fileHeader;
            try {
                fileHeader = carFile.getFileHeader("META-INF/mapping.txt");
                if (fileHeader != null) {
                    ZipInputStream zis = carFile.getInputStream(fileHeader);
                    try {
                        mapping.load(zis);
                    } finally {
                        zis.close();
                    }
                }
            } catch (ZipException | IOException e) {
                e.printStackTrace();
            }
        }
        return mapping;
    }
    
    public static Map<String, String> searchCeylonFilesForJavaImplementations(Properties classesToSources, File sourceArchive) throws ZipException {
        Map<String, String> javaImplFilesToCeylonDeclFiles = new HashMap<String, String>();
        ZipFile zipFile = new ZipFile(sourceArchive);
        for (Object value : classesToSources.values()) {
            String sourceUnitRelativePath = value.toString();
            if (sourceUnitRelativePath.endsWith(".java")) {
                String ceylonSourceUnitRelativePath = null;
                
                // search in the source archive for a Ceylon file corresponding
                //   to the native declarations implemented in this Java file
                
                if (sourceUnitRelativePath.endsWith("_.java")) { // top-level methods or objects implemented in Java
                    ceylonSourceUnitRelativePath = sourceUnitRelativePath.replaceAll("\\_.java$", "\\.ceylon");
                    if (sourceUnitRelativePath.equals("ceylon/language/true_.java") || sourceUnitRelativePath.equals("ceylon/language/false_.java")) {
                        ceylonSourceUnitRelativePath = "ceylon/language/Boolean.ceylon";
                    }
                } else { // top-level classes or interfaces implemented in Java
                    ceylonSourceUnitRelativePath = sourceUnitRelativePath.replaceAll("\\.java$", "\\.ceylon");
                }
                if (zipFile.getFileHeader(ceylonSourceUnitRelativePath) != null) {
                    javaImplFilesToCeylonDeclFiles.put(sourceUnitRelativePath, ceylonSourceUnitRelativePath);
                    continue;
                }
                if (sourceUnitRelativePath.endsWith("._java")) { 
                    // specific case of Ceylon Classes ending with _ => retry
                    ceylonSourceUnitRelativePath = sourceUnitRelativePath.replaceAll("\\.java$", "\\.ceylon");
                    if (zipFile.getFileHeader(ceylonSourceUnitRelativePath) != null) {
                        javaImplFilesToCeylonDeclFiles.put(sourceUnitRelativePath, ceylonSourceUnitRelativePath);
                    }
                }
            }
        }
        return javaImplFilesToCeylonDeclFiles;
    }
}
