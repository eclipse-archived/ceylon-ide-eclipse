shared object pluginIds {
    shared String main = "com.redhat.ceylon.eclipse.ui";
    shared String dist = "com.redhat.ceylon.dist";
    shared String embeddedRepo = "com.redhat.ceylon.dist.repo";
}

shared object markerIds {

    "A marker ID that identifies problems"
    shared String ceylonProblem = pluginIds.main + ".ceylonProblem";

    "A marker ID that identifies module dependency problems"
    shared String moduleDependencyProblem = pluginIds.main + ".ceylonModuleDependencyProblem";

    "A marker ID that identifies character encoding problems"
    shared String charsetProblem = pluginIds.main + ".ceylonCharsetProblem";

    "A marker ID that identifies synchronization problems between
     the `.ceylon/config` file and the corresponding eclipse project settings"
    shared String ceylonConfigNotInSyncProblem = pluginIds.main + ".ceylonConfigProblem";

    "A marker ID that identifies Invalid Overrides XML file problems"
    shared String ceylonInvalidOverridesProblem = pluginIds.main + ".invalidOverridesProblem";

    "A marker ID that identifies tasks"
    shared String task = pluginIds.main + ".ceylonTask";
}

