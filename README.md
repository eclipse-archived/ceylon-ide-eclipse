# Eclipse plugin for Ceylon

## Installing from xam.dk

1. Start with a clean install of Eclipse Indigo.

   <http://www.eclipse.org/downloads/>

2. Use `Help > Install New Software ... > Available Software Sites > Import`
   Select `updatesites.xml`

3. Close the dialog and now choose xam.dk updatesite and install the 
   plugin.

4. Restart and your Eclipse should now syntax highlight Ceylon files.

## Installing/Building with (pure) Eclipse

1.  Start with a clean install of Eclipse Indigo.
    
    <http://www.eclipse.org/downloads/>
    
2.  Use `Help > Install New Software...` to install all components of 
    IMP from the update site at:
    
    <http://download.eclipse.org/technology/imp/updates/0.2/>
    
3.  Use `File > Import... > Existing Projects into Workspace` 
    to import the project from this root directory: 
    
        ceyon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.ui
    
4.  Select the `com.redhat.ceylon.eclipse.ui` project and run it using
    `Run > Run As > Eclipse Application`. Now go to the new instance of 
    Eclipse.

5.  Open `Eclipse > Preferences` and go to 
    `Java > Compiler > Building > Output folder`. Add the pattern
    `*.ceylon` to `Filtered resources`.

6.  Use `File > New > Java Project`, enter a project name, and select 
    `Finish` create a new Java project in the workspace.
    
7.  Select the new project in the Java package explorer, and select 
    `Enable Ceylon Builder` from the context menu.
    
8.  Use `New > File` to create a new file with the extension `.ceylon`
    in the `src` directory of your project.

## Building with Tycho/Maven 3

1.  mvn clean install

2. `site/target/site` now contains an update site you can install from.
   (Need to have IMP update site added too)

