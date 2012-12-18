package com.redhat.ceylon.test.eclipse.plugin;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public class CeylonTestMessages extends NLS {

    private static final String BUNDLE_NAME = "com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages";
    
    public static String configTabName;
    public static String configTabTestGroupLabel;
    public static String configTabTestColumnLabel;

    public static String launchDialogInfoTitle;
    public static String launchSelectionSize;
    public static String launchNoTestsFound;
    public static String launchSelectLaunchConfigTitle;
    public static String launchSelectLaunchConfigMessage;
    public static String launchConfigIsNotValid;

    public static String testSelectDialogTitle;
    public static String testSelectDialogMessage;
    public static String testSelectDialogShowComplateTree;
    public static String testSelectDialogShowComplateDescription;
    
    public static String errorNoTests;
    public static String errorMultipleProjectsAreNotAllowed;
    public static String errorCanNotFindProject;
    public static String errorCanNotFindModule;
    public static String errorCanNotFindPackage;
    public static String errorCanNotFindClass;
    public static String errorCanNotFindMethod;
    public static String errorClassIsNotTestable;
    public static String errorMethodIsNotTestable;
    public static String errorNoSocket;
    
    public static String counterRuns;
    public static String counterFailures;
    public static String counterErrors;
    
    public static String stackTraceLabel;
    public static String stackTraceFilterLabel;
    public static String stackTraceCopyLabel;
    
    public static String showNextFailureLabel;
    public static String showPreviousFailureLabel;
    public static String showFailuresOnlyLabel;
    public static String showTestsGroupedByPackages;
    public static String relaunchLabel;
    public static String stopLabel;
    
    public static String add;
    public static String remove;
    public static String moveUp;
    public static String moveDown;
    public static String inProjectPrefix;

    static {
        NLS.initializeMessages(BUNDLE_NAME, CeylonTestMessages.class);
    }
    
    public static String msg(String msg, Object... args) {
        return MessageFormat.format(msg, args);
    }

    private CeylonTestMessages() {
    }

}