# Eclipse plugin for Ceylon

## Installing from the update site

_This is the **simplest way** to install the Ceyon IDE Eclipse plugin._

1.  Follow the instructions found there:
    
    <http://ceylon-lang.org/documentation/1.0/ide/install>

2.  Restart Eclipse.

5.  Go to `Help > Welcome to Ceylon` to get started.

## Building with Tycho/Maven 3

_If you want to have an up-to-date version of the Ceylon IDE based on the lastest code of all Ceylon projects, this is the **prefered method**._

1.  Make sure that `ant` (latest version) and `maven` (version from 3.0.5 to 3.2.1) can be run on the command line.

2.  Setup the command line distribution first (decribed at https://github.com/ceylon/ceylon-dist ), `ant setup` prepares the environment and clones additional repositories.

3.  Make sure that your JAVA_HOME is set to the right JDK 7 installation.

4.  Make sure that the following `ceylon`-owned GitHub repositories have all been cloned locally into the same parent directory:
    - `ceylon-dist`
    - `ceylon-sdk`
    - `ceylon.formatter`
    - `ceylon-ide-common`
    - `ceylon.tool.converter.java2ceylon`
    - `ceylon-ide-eclipse`

5.  Build a full Ceylon distribution locally (see [here](https://github.com/ceylon/ceylon-dist/blob/master/README.md#building-the-distribution) for more details):
    - In the `ceylon-dist` directory run: `ant clean publish-all ide-quick`
    - This should have produced an eclipse update site available at the following path:
    
      `.../ceylon-dist/osgi/build/dist`

6.  Build the Ceylon SDK locally:
    - In the `ceylon-sdk` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:
    
      `.../ceylon-sdk/osgi/dist`

7.  Build the Ceylon Formatter locally (see [here](https://github.com/ceylon/ceylon.formatter) for more details):
    - In the `ceylon.formatter` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:
    
      `.../ceylon.formatter/osgi/dist`

8.  Build the Ceylon IDE Common components locally (see [here](https://github.com/ceylon/ceylon-ide-common) for more details):
    - In the `ceylon-ide-common` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:
    
      `.../ceylon-ide-common/osgi/dist`

9.  Build the Java To Ceylon Converter components locally (see [here](https://github.com/ceylon/ceylon.tool.converter.java2ceylon) for more details):
    - In the `ceylon.tool.converter.java2ceylon` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:
    
      `.../ceylon.tool.converter.java2ceylon/osgi/dist`

10.  From this directory (`ceylon-ide-eclipse`), type:
    
        mvn clean install -fae

    Faster alternative: To skip tests completely you can run:

        mvn clean install -DskipTests
   
11.  The directory `site/target/repository` now contains an update site you can install from. The update process is decribed at http://ceylon-lang.org/documentation/1.1/ide/install/ but use the path to this directory instead of the stable web repository. 

## Building inside Eclipse

_This method implies some **additional complexity**, and is only useful if you want to debug the Ceylon IDE plugin._

**_Prelimiary remark_**: Now, parts of the Ceylon IDE project itself are written in Ceylon. Thus, in order to develop the Ceylon IDE plugin, you must have a previous version of the plugin installed in your
main Eclipse (preferably build with Maven, or downloaded from the update site).

1.  Make sure that `ant` (latest version) can be run on the command line.

2.  Setup the command line distribution first (decribed at https://github.com/ceylon/ceylon-dist ), `ant setup` prepares the environment and clones additional repositories.

3.  Make sure that your JAVA_HOME is set to the right JDK 7 installation.

4.  Make sure that the following `ceylon`-owned GitHub repositories have all been cloned locally into the same parent directory:
    - `ceylon-dist`
    - `ceylon-sdk`
    - `ceylon.formatter`
    - `ceylon-ide-common`
    - `ceylon.tool.converter.java2ceylon`
    - `ceylon-ide-eclipse`

5.  Start preferably with a clean install of Eclipse Kepler, Luna or Mars.

    <http://www.eclipse.org/downloads/>

6.  Install the following feature: _Graphical Editing Framework Zest Visualization Toolkit SDK_ available at the main Eclipse release update site (http://download.eclipse.org/releases/kepler for the Kepler version)

7.  Install a previous version of the Ceylon IDE (preferably build with Maven, or downloaded from the update site).

8.  Make sure you have the following feature: _Eclipse Plug-in Development Environment_.
    This is normally included inside the Eclipse Standard Package.

9.  Use `File > Import... > Existing Projects into Workspace` to import the Java and Ceylon Eclipse projects that are in these directories:
    - `../ceylon-ide-common`
    - `ceylon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.ui.jdt.debug.fragment`
    - `ceylon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.ui`
    - `ceylon-ide-eclipse/plugins/com.redhat.ceylon.test.eclipse.plugin`
    - `ceylon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.android.plugin`

10. Build a full Ceylon distribution locally first (see [here](https://github.com/ceylon/ceylon-dist/blob/master/README.md#building-the-distribution) for more details):
    - In the `ceylon-dist` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:

        `.../ceylon-dist/osgi/build/dist`

11. Build the Ceylon SDK:
    - In the `ceylon-sdk` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:

        `.../ceylon-sdk/osgi/dist`


12. Build the required _ceylon.formatter_ module (see [here](https://github.com/ceylon/ceylon.formatter) for more details):
    - In the `ceylon.formatter` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:

        `.../ceylon.formatter/osgi/dist`


13. Build the required _ceylon-ide-common_ module (see [here](https://github.com/ceylon/ceylon-ide-common) for more details):
    - In the `ceylon-ide-common` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:

        `.../ceylon-ide-common/osgi/dist`


14. Build the required _ceylon.tool.converter.java2ceylon_ module (see [here](https://github.com/ceylon/ceylon.tool.converter.java2ceylon) for more details):
    - In the `ceylon.tool.converter.java2ceylon` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:

        `.../ceylon.tool.converter.java2ceylon/osgi/dist`


15. Add the following folder as a local update site in your Eclipse _Available Software Sites_ list:

    `.../ceylon-ide-eclipse/UpdateSiteForBinaryDependencies/`
        
    From this new update site, install _*only*_ the elements that are under the categories whose name contains : '` - Only Binary Dependencies`'.
    This provides (as OSGI bundles) only the external archives required by the various siblings projects required by the IDE Plugin (jboss modules, antlr-runtime v4, etc ...).


16. Import inside your Eclipse workspace: 
    - the `ceylon-dist-osgi` project found at the following location:
    
        `.../ceyon-dist/osgi`
        
    - the `ceylon-dist-osgi-embedded-repository` project found at the following location:
        
        `.../ceyon-dist/osgi/embeddedRepository`
        
    - all the required _bundle-proxys_ projects found under the following location:
    
        `.../ceyon-ide-eclipse/required-bundle-proxies`
    
    #### _Important Note:_

    Since the Ceylon Distribution modules have circular dependencies on each others, it happens that those circular dependencies are reproduced by the _ceylon-dist-osgi_ and _bundle-proxys_ projects.
    
    In order to be able to build you projects, you will have to allow cycles in the Java build paths by setting the following Eclipse preference:

    `Java -> Compiler -> Build -> Circular Dependencies`to `warning`

17. During the development, you should be aware of these rules:
    - If you _change some of the fixed jars included in the Ceylon distribution_ (such as `org.antlr`, `org.apache.commons.logging`, etc...), then you should :
        - **rebuild/publish** the distribution by running the `ant clean publish ide-quick` command in the `ceylon-dist` directory,
        - **update, inside Eclipse,** the `Ceylon Distribution Binary Dependencies Feature` feature from the `.../ceylon-dist/osgi/build/dist` update site.
    - If you _have modified code inside one of the projects required by the Ceylon IDE plugin_ (distribution project, SDK, formatter, java2ceylon converter, ceylon-ide-common, ...), you should:
        - **rebuild/publish** the modified project by running the `ant clean publish ide-quick` command in the project directory,
    - Each time you _rebuild/publish one of the projects required by the Ceylon IDE plugin_ (distribution project, SDK, formatter, java2ceylon converter, ceylon-ide-common, ...), you should:
        - **refresh inside Eclipse** the `ceylon-dist-osgi` project, as well as the _bundle proxy projects_ related to the rebuilt project. This is necessary so that Eclipse will see the changes, especially when running/debugging the CeylonIDE.

17. If you want to modify / add IDE tests, you should also add the test plugin. For this purpose
    - Add the SWTBot Eclipse features, which are required to compile and run the Ceylon IDE
      interactive tests.
      Install all the features available at the following update site:
        `http://download.eclipse.org/technology/swtbot/releases/latest/`
    - Use `File > Import... > Existing Projects into Workspace`
      to import the project from this root directory:
        `ceylon-ide-eclipse/tests/com.redhat.ceylon.eclipse.ui.test`


18.  Select the `com.redhat.ceylon.eclipse.ui` project and run it using
    `Run > Run As > Eclipse Application`. Now go to the new instance of
    Eclipse.

19.  Go to `Help > Welcome to Ceylon` to get started.

## Updating the Ceylon version

1. Total panic

2. It's a nightmare

3. For proxy bundles, I used:

    for f in *-1.1.0.?ar; do newf=${f/1.1.0/1.1.1}; if test \! -d $newf; then mkdir $newf; fi; cp $f/{.classpath,.project,.gitignore} $newf/; perl -pi -e 's/1\.1\.0/1.1.1/g' $newf/{.classpath,.project,.gitignore}; done

## Pushing a new release onto the development update site

1.  Build with Tycho/Maven 3 (see previous section)

2.  Copy (through sftp) the content of the directory `site/target/repository` onto the server:

        www.ceylon-lang.org

    to the following directory:

        /var/www/downloads.ceylonlang/ide/dev

## License

The content of this repository is released under the EPL v1.0
as provided in the LICENSE file that accompanied this code.

By submitting a "pull request" or otherwise contributing to this repository, you
agree to license your contribution under the license mentioned above.
