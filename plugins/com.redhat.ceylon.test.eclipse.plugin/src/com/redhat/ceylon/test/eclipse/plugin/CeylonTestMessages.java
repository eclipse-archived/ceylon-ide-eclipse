package com.redhat.ceylon.test.eclipse.plugin;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public class CeylonTestMessages extends NLS {

    private static final String BUNDLE_NAME = "com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages";
    
    public static String configTabName;
    public static String configTabTestGroupLabel;
    public static String configTabTestColumnLabel;

    public static String launchDialogInfoTitle;
    public static String launchNoTestsFound;
    public static String launchSelectLaunchConfigTitle;
    public static String launchSelectLaunchConfigMessage;
    public static String launchConfigIsNotValid;

    public static String testSelectDialogTitle;
    public static String testSelectDialogMessage;
    public static String testSelectDialogShowComplateDescription;
    
    public static String errorDialogTitle;
    public static String errorNoTests;
    public static String errorCanNotFindProject;
    public static String errorCanNotFindModule;
    public static String errorCanNotFindPackage;
    public static String errorCanNotFindDeclaration;
    public static String errorCanNotFindSelectedTest;
    public static String errorNoSocket;
    public static String errorMissingCeylonTestImport;
    
    public static String statusTestRunRunning;
    public static String statusTestRunFinished;
    public static String statusTestRunInterrupted;
    public static String statusTestPlatformJvm;
    public static String statusTestPlatformJs;
    
    public static String counterRuns;
    public static String counterFailures;
    public static String counterErrors;
    
    public static String stackTraceLabel;
    public static String stackTraceFilterLabel;
    public static String stackTraceCopyLabel;
    
    public static String showNextFailureLabel;
    public static String showPreviousFailureLabel;
    public static String showFailuresOnlyLabel;
    public static String showTestsInHierarchy;
    public static String showTestsElapsedTime;
    public static String relaunchLabel;
    public static String stopLabel;
    public static String scrollLockLabel;
    public static String collapseAllLabel;
    public static String expandAllLabel;
    public static String gotoLabel;
    public static String runLabel;
    public static String debugLabel;
    
    public static String historyLabel;
    public static String historyDlgTitle;
    public static String historyDlgMessage;
    public static String historyColumnName;
    public static String historyColumnPlatform;
    public static String historyColumnStartDate;
    public static String historyColumnTotal;
    public static String historyColumnSuccess;
    public static String historyColumnFailures;
    public static String historyColumnErrors;
    public static String historyColumnSkipped;
    public static String historyPinLabel;
    public static String historyPinTooltip;
    public static String historyUnpinLabel;
    public static String historyDlgCanNotCompareRunningTest;
    public static String historyDlgCanNotRemoveRunningTest;
    
    public static String compareValuesActionLabel;
    public static String compareValuesDlgTitle;
    public static String compareValuesDlgOk;
    public static String compareValuesDlgExpected;
    public static String compareValuesDlgActual;
    
    public static String compareRunsDlgRunName;
    public static String compareRunsDlgPlatform;
    public static String compareRunsDlgStartDate;
    public static String compareRunsDlgElapsedTime;
    public static String compareRunsDlgTotal;
    public static String compareRunsDlgSuccess;
    public static String compareRunsDlgFailures;
    public static String compareRunsDlgErrors;
    public static String compareRunsDlgSkipped;
    public static String compareRunsDlgShowOnly;
    public static String compareRunsDlgFixed;
    public static String compareRunsDlgRegressedError;
    public static String compareRunsDlgRegressedFailure;
    public static String compareRunsDlgChanged;
    public static String compareRunsDlgUnchanged;
    public static String compareRunsDlgAdded;
    public static String compareRunsDlgRemoved;
    
    public static String add;
    public static String remove;
    public static String removeAll;
    public static String moveUp;
    public static String moveDown;
    public static String compare;
    public static String information;
    public static String inProjectPrefix;
    public static String addCeylonTestImport;
    public static String platformJvm;
    public static String platformJs;

    static {
        NLS.initializeMessages(BUNDLE_NAME, CeylonTestMessages.class);
    }
    
    public static String msg(String msg, Object... args) {
        return MessageFormat.format(msg, args);
    }

    private CeylonTestMessages() {
    }

}