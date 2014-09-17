# Eclipse plugin for Ceylon

## Installing from the update site

_This is the **simplest way** to install the Ceyon IDE Eclipse plugin._ 

1.  Follow the instructions found there :
    
    <http://ceylon-lang.org/documentation/1.0/ide/install>
    
2.  Restart Eclipse.
    
5.  Go to `Help > Welcome to Ceylon` to get started.

## Building with Tycho/Maven 3

_If you want to have an up-to-date version of the Ceylon IDE based on the lastest code of all Ceylon projects, this is the **prefered method**._ 

1.  First make sure that your Eclipse can be run by simply typing the `eclipse` command (either by adding the `eclipse` command full path to the PATH environment variable, or by creating a symbolic link to the `eclipse` executable file in a directory already visible in the PATH).

1b. Make sure that your ant (latest version) and maven (Version in Range from 3.0.5 to 3.2.1) can also be run on the command line.

2.  Make sure that your JAVA_HOME is set to the right JDK 7 installation

3.  Make sure that the following GitHub repositories have all been cloned locally into the same parent directory :
	- ceylon-dist
	- ceylon-sdk
	- ceylon.formatter
	- ceylon-ide-eclipse	
	
	
4.  Build a full Ceylon distribution locally (see [here](https://github.com/ceylon/ceylon-dist/blob/master/README.md#building-the-distribution) for more details) :
    - In the `ceylon-dist` directory run : `ant clean publish-all ide-quick`
    - This should have produced an eclipse update site available at the following path :
      `.../ceylon-dist/osgi/build/dist`

5.  Build the Ceylon SDK locally :
    - In the `ceylon-sdk` directory run : `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path :
      `.../ceylon-sdk/osgi/dist`

5.  Build the Ceylon Formatter locally (see [here](https://github.com/ceylon/ceylon.formatter) for more details) :
    - In the `ceylon.formatter` directory run : `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path :
      `.../ceylon.formatter/osgi/dist`

6.  From this directory (`ceylon-ide-eclipse`), type :
    
        `mvn clean install -fae`

    To skip tests completely you can do:

        `mvn clean install -DskipTests` 
   
7.  The directory `site/target/repository` now contains an update site you can 
    install from.

## Building with (pure) Eclipse

_This method implies some **additional complexity**, and is only useful if you want to debug the Ceylon IDE plugin._ 

**_Prelimiary remark_** : Now, parts of the Ceylon IDE project itself are written in Ceylon. Thus, in order to develop the Ceylon IDE plugin, you must have a previous version of the plugin installed in your
main Eclipse (either downloaded from the update site, or built with Maven) 

1.  Start with a clean install of Eclipse Kepler or Luna.
    
    <http://www.eclipse.org/downloads/>
    
2.  Install the following feature : _Graphical Editing Framework Zest Visualization Toolkit SDK_ available at the main Eclipse release update site (http://download.eclipse.org/releases/kepler for the Kepler version)
	
3.  Make sure you have the following feature : _Eclipse Plug-in Development Environment_.
    This is normally included inside the Eclipse Standard Package.

4.  Use `File > Import... > Existing Projects into Workspace` 
    to import the Java projects that are in these directories : 

    ```
    ceyon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.ui
    ceyon-ide-eclipse/plugins/com.redhat.ceylon.test.eclipse.plugin
    ceyon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.android.plugin
    ```
    And also the Ceylon project that is the following directory : 

    ```
    ceyon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.ui/ceylon
    ```

5.  The `com.redhat.ceylon.eclipse.ui` plugin depends on several OSGI bundles, which must be available inside Eclipse to be able to build it.	
    Quite recent versions of these dependencies should be available on the [IDE development update site](http://ceylon-lang.org/eclipse/development/)
	in the _Ceylon IDE Runtime Bundles_ category. Though installing all the bundles of this category will provide the dependencies required to build and run the Ceylon IDE, _*this is not recommended*_.  Indeed this method is only useful if you don't have the Ceylon distribution projects installed locally, or if you don't want to take in account changes made locally to the required bundles.

	So, if you need to build the IDE with the very last versions of the dependencies (ceylon compiler, typechecker, etc), you will
	need to :
	- build a full Ceylon distribution locally first (see [here](https://github.com/ceylon/ceylon-dist/blob/master/README.md#building-the-distribution) for more details) :
		- First make sure that your Eclipse can be run by simply typing the `eclipse` command (either by adding the `eclipse` command full path to the PATH environment variable, or by creating a symbolic link to the `eclipse` executable file in a directory already visible in the PATH).
		- In the `ceylon-dist` directory run : `ant clean publish-all ide-quick`
		- This should have produced an eclipse update site available at the following path :
        	`.../ceylon-dist/osgi/build/dist`
		- Add this folder as a local update site in your Eclipse _Available Software Sites_ list.
		- From this new update site, install _*only*_ the `Ceylon Distribution Binary Dependencies Feature` available under the `Ceylon Distribution - Only Binary Dependencies` category.
		This provides (as OSGI bundles) only the external archives required by the various siblings projects of the local ceylon dist (jboss modules, etc ...).

	- build the Ceylon SDK :
		- In the `ceylon-sdk` directory run : `ant clean publish ide-quick`

	- build the _ceylon.formatter_ module that is also required now (see [here](https://github.com/ceylon/ceylon.formatter) for more details):
		- In the `ceylon.formatter` directory run : `ant clean publish ide-quick`

	- make sure that the following GitHub repositories have all been cloned locally into the same parent directory :
	```
	ceylon-dist
	ceylon-sdk
	ceylon.formatter
	ceylon-ide-eclipse
	```

	- Import inside your Eclipse workspace the `ceylon-dist-osgi` project found at the following location :
	    ```
	    .../ceyon-dist/osgi
	    ```

	- Import inside your Eclipse workspace all the required _bundle-proxys_ projects found under the following location :
	    ```
	    .../ceyon-ide-eclipse/required-bundle-proxies
	    ```

		#### _Important Note :_
		Since the Ceylon Distribution modules have circular dependencies on each others, it happens that those circular dependencies are reproduced by the _ceylon-dist-osgi_ and _bundle-proxys_ projects. In order to be able to build you projects, you will have to allow cycles in the Java build paths by setting the following Eclipse preference `Java -> Compiler -> Build -> Circular Dependencies`to `warning` 
		
	Each time you will rebuild one of the projects required by the Ceylon IDE plugin (distribution, SDK, formatter, ...), you only need to _Refresh_ the `ceylon-dist-osgi` project, as well as the bundle proxy projects related to the rebuilt required projects, in order to be able to see the changes in the Ceylon IDE projects.

Notes:

If you update the dependencies of libs in the Ceylon distrib repo, you must update your `Ceylon Distribution Binary Dependencies Feature`
plugin from the `.../ceylon-dist/osgi/build/dist` update site.

If you update the sdk, ceylon.formatter or Ceylon distrib, you need to redo the `ant clean publish ide` in each project and
refresh them in Eclipse and possible clean their proxy bundle projects and the `ceylon-dist-osgi` project.
	
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
