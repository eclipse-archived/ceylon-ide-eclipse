# Eclipse plugin for Ceylon

## Installing from http://ceylon-lang.org/eclipse/updatesite/

1.  Start with a clean install of Eclipse Indigo (or Juno, especially if you're on a Mac).
    
    <http://www.eclipse.org/downloads/>
    
2.  Use `Help > Install New Software ... > Available Software Sites > Import`
    Select `updatesites.xml`
    
3.  Close the dialog and now choose ceylon-lang.org updatesite and install the 
    plugin.
    
4.  Restart Eclipse.
    
5.  Go to `Help > Welcome to Ceylon` to get started.

## Installing/Building with (pure) Eclipse

1.  Start with a clean install of Eclipse Indigo (or Juno, especially if you're on a Mac).
    
    <http://www.eclipse.org/downloads/>
    
2.  Use `File > Import... > Existing Projects into Workspace` 
    to import the project from this root directory: 
    
        ceyon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.ui
    
3.  Select the `com.redhat.ceylon.eclipse.ui` project and run it using
    `Run > Run As > Eclipse Application`. Now go to the new instance of 
    Eclipse.
    
4.  Go to `Help > Welcome to Ceylon` to get started.

## Building with Tycho/Maven 3

1.  From this directory, type
    
        mvn clean install -fae

    To skip tests you can do:
        mvn clean install -Dmaven.test.skip 
   
2.  The directory `site/target/site` now contains an update site you can 
    install from.

## Pushing a new release onto the development update site

1.  Build with Tycho/Maven 3 (see previous section)
    
2.  Copy (through sftp) the content of the directory `site/target/site` onto the server :

        www.ceylon-lang.org 
    
    to the following directory :
    
        /var/www/downloads.ceylonlang/ide/dev
        
## License

The content of this repository is released under the EPL v1.0
as provided in the LICENSE file that accompanied this code.

By submitting a "pull request" or otherwise contributing to this repository, you
agree to license your contribution under the license mentioned above.
