package com.redhat.ceylon.eclipse.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

public class CarUtils {
    public static Properties retrieveMappingFile(File carFile) throws ZipException, IOException {
        Properties mapping = new Properties();
        if (carFile != null) {
            ZipFile zipFile = new ZipFile(carFile);
            FileHeader fileHeader = zipFile.getFileHeader("META-INF/mapping.txt");
            if (fileHeader != null) {
                ZipInputStream zis = zipFile.getInputStream(fileHeader);
                try {
                    mapping.load(zis);
                } finally {
                    zis.close();
                }
            }
        }
        return mapping;
    }
    
    public static Properties retrieveMappingFile(ZipFile carFile) throws ZipException, IOException {
        Properties mapping = new Properties();
        if (carFile != null) {
            FileHeader fileHeader = carFile.getFileHeader("META-INF/mapping.txt");
            if (fileHeader != null) {
                ZipInputStream zis = carFile.getInputStream(fileHeader);
                try {
                    mapping.load(zis);
                } finally {
                    zis.close();
                }
            }
        }
        return mapping;
    }
}
