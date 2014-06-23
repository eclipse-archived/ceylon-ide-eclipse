# Eclipse plugin for Ceylon

## Installing from the update site

1.  Follow the instructions found there :
    
    <http://ceylon-lang.org/documentation/1.0/ide/install>
    
2.  Restart Eclipse.
    
5.  Go to `Help > Welcome to Ceylon` to get started.

## Building with (pure) Eclipse

1.  Start with a clean install of Eclipse Juno or Kepler.
    
    <http://www.eclipse.org/downloads/>
    
2.  Install the following feature : _Graphical Editing Framework Zest Visualization Toolkit SDK_ available at the main Eclipse release update site (http://download.eclipse.org/releases/kepler for the Kepler version)
	
3.  Make sure you have the following feature : _Eclipse Plug-in Development Environment_.
    This is normally included inside the Eclipse Standard Package.

4.  Use `File > Import... > Existing Projects into Workspace` 
    to import the projects from this root directories : 

    ```
    ceyon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.ui
    ceyon-ide-eclipse/plugins/com.redhat.ceylon.test.eclipse.plugin
    ceyon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.android.plugin
    ```

5.  The `com.redhat.ceylon.eclipse.ui` plugin depends on several OSGI bundles, which must be available inside Eclipse to be able to build it.	
    Quite recent versions of these dependencies should be available on the [IDE development update site](http://ceylon-lang.org/eclipse/development/)
	in the _Ceylon IDE Runtime Bundles_ category.
	Installing all the bundles of this category will provide all the dependencies required to build and run the Ceylon IDE.
	
	However, if you need to build the IDE with the very last versions of the dependencies (ceylon compiler, typechecker, etc), you will
	need to build :
	- a full Ceylon distribution locally first (see [here](https://github.com/ceylon/ceylon-dist/blob/master/README.md#building-the-distribution) for more details) :
		
		- First make sure that your Eclipse can be run by simply typing the `eclipse` command (either by adding the `eclipse` command full path to the PATH environment variable, or by creating a symbolic link to the `eclipse` executable file in a directory already visible in the PATH).
		
		- In the `ceylon-dist` directory run : `ant clean publish-all ide-quick`
		
		- This should have produced an eclipse update site available at the following path :
        	`.../ceylon-dist/osgi/build/dist`
			
		The Ceylon Distribution feature available under the _Ceylon Distribution Runtime Bundles_ category should be installed in Eclipse.
	
	- the Ceylon SDK :

		- In the `ceylon-sdk` directory run : `ant clean publish ide-quick`
		
		- This should have produced an eclipse update site available at the following path :
        	`.../ceylon-sdk/osgi/dist`
			
		The required OSGI bundles (ceylon.file, ceylon.interop.java, ceylon.collection) available under the _Ceylon Distribution Runtime Bundles_ category should be installed in Eclipse.

	- The _ceylon.formatter_ module is also required now (see [here](https://github.com/lucaswerkmeister/ceylon.formatter) for more details):
		- In the `ceylon.formatter` directory run : `ant clean publish ide-quick`
		
		- This should have produced an eclipse update site available at the following path :
        	`.../ceylon.formatter/osgi/dist`
			
		The OSGI bundles available under the _Ceylon Distribution Runtime Bundles_ category should be installed in Eclipse.

	Each time you will rebuild one of those elemens, you will need to update these required bundles and restart Eclipse.

6. If you want to modify / add IDE tests, you should also add the test plugin. For this purpose
    - Add the SWTBot Eclipse features, which are required to compile and run the Ceylon IDE 
      interactive tests.
      Install all the features available at the following update site :
        
        http://download.eclipse.org/technology/swtbot/releases/latest/
        
    - Use `File > Import... > Existing Projects into Workspace` 
      to import the project from this root directory: 
    
            ceylon-ide-eclipse/tests/com.redhat.ceylon.eclipse.ui.test

7.  Select the `com.redhat.ceylon.eclipse.ui` project and run it using
    `Run > Run As > Eclipse Application`. Now go to the new instance of 
    Eclipse.
    
8.  Go to `Help > Welcome to Ceylon` to get started.

## Building with Tycho/Maven 3

1.  First make sure that your Eclipse can be run by simply typing the `eclipse` command (either by adding the `eclipse` command full path to the PATH environment variable, or by creating a symbolic link to the `eclipse` executable file in a directory already visible in the PATH).

2.  Make sure that your JAVA_HOME is set to the right JDK 7 installation

3.  Make sure that the following GitHub repositories have all been cloned locally into the same parent directory :
	- ceylon-dist
	- ceylon-sdk
	- ceylon.formatter
	- ceylon-ide-eclipse	
	
	
4.  Build a full Ceylon distribution locally (see [here](https://github.com/ceylon/ceylon-dist/blob/master/README.md#building-the-distribution) for more details) :
    - In the `ceylon-dist` directory run : `ant clean publish-all ide-quick`

5.  Build the Ceylon SDK locally :
    - In the `ceylon-sdk` directory run : `ant clean publish ide-quick`

5.  Build the Ceylon Formatter locally :
    - In the `ceylon.formatter` directory run : `ant clean publish ide-quick`

6.  From this directory (`ceylon-ide-eclipse`), type :
    
        `mvn clean install -fae`

    To skip tests completely you can do:

        `mvn clean install -DskipTests` 
   
7.  The directory `site/target/repository` now contains an update site you can 
    install from.

## Pushing a new release onto the development update site

1.  Build with Tycho/Maven 3 (see previous section)
    
2.  Copy (through sftp) the content of the directory `site/target/repository` onto the server :

        www.ceylon-lang.org 
    
    to the following directory :
    
        /var/www/downloads.ceylonlang/ide/dev
        
## License

The content of this repository is released under the EPL v1.0
as provided in the LICENSE file that accompanied this code.

By submitting a "pull request" or otherwise contributing to this repository, you
agree to license your contribution under the license mentioned above.
