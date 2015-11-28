# Ceylon IDE for Eclipse

This project is an Eclipse-based IDE for the [Ceylon programming language][Ceylon].

For more information about Ceylon IDE for Eclipse, go to:

<http://ceylon-lang.org/documentation/ide>

[Ceylon]: http://ceylon-lang.org/

## Installing the Eclipse Plugin

[sdk]: https://github.com/ceylon/ceylon-sdk
[formatter]: https://github.com/ceylon/ceylon.formatter
[java2ceylon]: https://github.com/ceylon/ceylon.tool.converter.java2ceylon
[common]: https://github.com/ceylon/ceylon-ide-common

### From the update site

This is the simplest way to install the Ceyon IDE Eclipse plugin.

1.  Follow the instructions found there:
    <http://ceylon-lang.org/documentation/1.2/ide/install>
2.  Restart Eclipse.
5.  Go to `Help > Welcome to Ceylon` to get started.

### From a local snapshot site built with Tycho/Maven 3

If you want to have an up-to-date version of the Ceylon IDE based on the latest code of 
all Ceylon projects, this is the recommended method.

1. Make sure that you are using Java 7 or 8 by running `javac -version`.
   
2. Make sure that `ant` (latest version) and `maven` (version from 3.0.5 to 3.2.1) 
   can be run on the command line.
   
3. First clone the [Ceylon command line distribution](https://github.com/ceylon/ceylon) 
   locally. For this purpose type the following command in the directory of your choice:
   
       git clone https://github.com/ceylon/ceylon.git
   
   This has created the `ceylon` local repository.
   
4. Next, this Git repository should be cloned locally, along with the repositories of 
   the following required projects: [Ceylon SDK][sdk], [Ceylon Formatter][formatter], 
   [Java To Ceylon Converter][java2ceylon], and [Common code for Ceylon IDEs][common]. 
   To clone all these projects at once, go to the `ceylon/` directory ceated in the 
   previous step, and type:
   
       ant setup-sdk setup-ide
   
   This command will have cloned the following Git repositories:
   
   - `ceylon-sdk`
   - `ceylon.formatter`
   - `ceylon-ide-common`
   - `ceylon.tool.converter.java2ceylon`
   - `ceylon-ide-eclipse`
   
   at the same directory level as the `ceylon` local repository.
   
5. Finally, to build all the above projects, stay in the `ceylon/` directory and type:
   
       ant clean-all dist sdk eclipse
   
6. The directory `../ceylon-ide-eclipse/site/target/repository` now contains an 
   Eclipse update site you can install from. The update process is decribed at 
   <http://ceylon-lang.org/documentation/1.2/ide/install/> but use the full path 
   to this directory instead as the URL of the update site.

## Developing the Ceylon Plugin
    
### Building project per project

If you make modifications on one of Ceylon IDE's dependencies, you can rebuild projects separately:

1.  Build a full Ceylon distribution locally (see [here](https://github.com/ceylon/ceylon/blob/master/dist/README.md#building-the-distribution) for more details):
    - In the `.../ceylon` directory run: `ant clean dist`
    - This should have produced an eclipse update site available at the following path:
    
      `./osgi/build/dist`

2.  Build the Ceylon SDK locally:
    - In the `.../ceylon` directory run: `ant clean-sdk sdk`
    - This should have produced an eclipse update site available at the following path:
    
      `../ceylon-sdk/osgi/dist`

3.  Build the Ceylon Formatter locally (see [here](https://github.com/ceylon/ceylon.formatter) for more details):
    - In the `.../ceylon.formatter` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:
    
      `.../ceylon.formatter/osgi/dist`

4.  Build the Ceylon IDE Common components locally (see [here](https://github.com/ceylon/ceylon-ide-common) for more details):
    - In the `.../ceylon-ide-common` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:
    
      `.../ceylon-ide-common/osgi/dist`

5.  Build the Java To Ceylon Converter components locally (see [here](https://github.com/ceylon/ceylon.tool.converter.java2ceylon) for more details):
    - In the `.../ceylon.tool.converter.java2ceylon` directory run: `ant clean publish ide-quick`
    - This should have produced an eclipse update site available at the following path:
    
      `.../ceylon.tool.converter.java2ceylon/osgi/dist`

6.  From the directory `.../ceylon-ide-eclipse`, type:
    
        mvn clean install -fae

    Faster alternative: To skip tests completely you can run:

        mvn clean install -DskipTests

### Developing the plugin inside Eclipse

_This implies some **additional complexity**, and is only useful if you want to debug the Ceylon IDE plugin._

**_Prelimiary remark_**: Now, parts of the Ceylon IDE project itself are written in Ceylon. Thus, in order to develop the Ceylon IDE plugin, you must have a previous version of the plugin installed in your
main Eclipse (preferably downloaded from the stable update site, or built with Maven).

1.  Perform steps 1 to 4 of section : [Building with Tycho / Maven 3](#building-with-tychomaven-3).

2.  Start preferably with a clean install of Eclipse Kepler, Luna or Mars.

    <http://www.eclipse.org/downloads/>

3.  Install the following feature: _Graphical Editing Framework Zest Visualization Toolkit SDK_ available here : 

    http://download.eclipse.org/tools/gef/updates/releases/

4.  Make sure you have the following feature: _Eclipse Plug-in Development Environment_. This is normally included inside the Eclipse Standard Package.

5.  Install a previous version of the Ceylon IDE, preferably downloaded from the stable update site (see [here](#installing-from-the-update-site)), or built with Maven (see [here](#building-with-tychomaven-3)).

6.  Use `File > Import... > Existing Projects into Workspace` to import the Java and Ceylon Eclipse projects that are in these directories:
    - `.../ceylon-ide-common`
    - `.../ceylon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.ui.jdt.debug.fragment`
    - `.../ceylon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.ui`
    - `.../ceylon-ide-eclipse/plugins/com.redhat.ceylon.test.eclipse.plugin`
    - `.../ceylon-ide-eclipse/plugins/com.redhat.ceylon.eclipse.android.plugin`

7. Add the following folder as a local update site in your Eclipse _Available Software Sites_ list:

    `.../ceylon-ide-eclipse/UpdateSiteForBinaryDependencies/`
        
    From this new update site, install _*only*_ the elements that are under the categories whose name contains : '` - Only Binary Dependencies`'.
    This provides (as OSGI bundles) only the external archives required by the various siblings projects required by the IDE Plugin (jboss modules, antlr-runtime v4, etc ...).


8. Use `File > Import... > Existing Projects into Workspace` to import the Eclipse projects that are in these directories: 
    - the `ceylon-dist-osgi` project found at the following location:
    
        `.../ceyon/osgi`
        
    - the `ceylon-dist-osgi-embedded-repository` project found at the following location:
        
        `.../ceyon/osgi/embeddedRepository`
        
    - all the required _bundle-proxys_ projects found under the following location:
    
        `.../ceyon-ide-eclipse/required-bundle-proxies`
    
    #### _Important Note:_

    Since the Ceylon Distribution modules have circular dependencies on each others, it happens that those circular dependencies are reproduced by the _ceylon-dist-osgi_ and _bundle-proxys_ projects.
    
    In order to be able to build you projects, you will have to allow cycles in the Java build paths by setting the following Eclipse preference:

    `Java -> Compiler -> Build -> Circular Dependencies`to `warning`

9. During the development, you should be aware of these rules:
    - If you _change some of the fixed jars included in the Ceylon distribution_ (such as `org.antlr`, `org.apache.commons.logging`, etc...), then you should :
        - **rebuild/publish** the distribution by running the `ant clean publish ide-quick` command in the `ceylon/dist` directory,
        - **update, inside Eclipse,** the `Ceylon Distribution Binary Dependencies Feature` feature from the `.../ceylon/dist/osgi/build/dist` update site.
    - If you _have modified code inside one of the projects required by the Ceylon IDE plugin_ (distribution project, SDK, formatter, java2ceylon converter, ceylon-ide-common, ...), you should:
        - **rebuild/publish** the modified project by running the `ant clean publish ide-quick` command in the project directory,
    - Each time you _rebuild/publish one of the projects required by the Ceylon IDE plugin_ (distribution project, SDK, formatter, java2ceylon converter, ceylon-ide-common, ...), you should:
        - **refresh inside Eclipse** the `ceylon-dist-osgi` project, as well as the _bundle proxy projects_ related to the rebuilt project. This is necessary so that Eclipse will see the changes, especially when running/debugging the CeylonIDE.

9. If you want to modify / add IDE tests, you should also add the test plugin. For this purpose
    - Add the SWTBot Eclipse features, which are required to compile and run the Ceylon IDE
      interactive tests.
      Install all the features available at the following update site:
        `http://download.eclipse.org/technology/swtbot/releases/latest/`
    - Use `File > Import... > Existing Projects into Workspace`
      to import the project from this root directory:
        `ceylon-ide-eclipse/tests/com.redhat.ceylon.eclipse.ui.test`


10.  Select the `com.redhat.ceylon.eclipse.ui` project and run it using
    `Run > Run As > Eclipse Application`. Now go to the new instance of
    Eclipse.

### Building the next maintenance update of the last released version

By default, when [building the plugin from Maven](#building-with-tychomaven-3) and developping it, all the code is built against the *master* branch of all the dependencies (incuding the Ceylon command line distribution).
However, after a release, we create GitHub maintenance branches for the two following projects:

- `.../ceylon-ide-common`
- `.../ceylon-ide-eclipse`
    
These branches allow pushing only changes that are fully compatible with the release available at the main updatesite: http://ceylon-lang.org/eclipse/updatesite/.

In order to work on these branches and build these 2 projects against the dependencies found in the release update site (instead of building against the local master branch of each project), you should:

- switch to the last release maintenance branch by going into the `.../ceylon/dist` directory and typing:
    
        ant eclipse-switch-to-last-release-updates
        
- implement your maintenance changes inside the the IDE as usual.  
- build the IDE with the following command run from the `.../ceylon/dist` directory:
    
        ant eclipse-rebuild-last-release-updates
    
The generated update site generated in directory `.../ceylon-ide-eclipse/site/target/repository` now contains a maintenance release fully compatible with the last release published in the [official Ceylon Eclipse update site](http://ceylon-lang.org/eclipse/updatesite/)  

In order to come back to the master branches, run the following command from the `.../ceylon/dist` directory:
    
        ant eclipse-switch-back-to-master
    
### Updating the Ceylon version

1. Total panic

2. It's a nightmare

3. For proxy bundles, I used:

    for f in *-1.1.0.?ar; do newf=${f/1.1.0/1.1.1}; if test \! -d $newf; then mkdir $newf; fi; cp $f/{.classpath,.project,.gitignore} $newf/; perl -pi -e 's/1\.1\.0/1.1.1/g' $newf/{.classpath,.project,.gitignore}; done

### Pushing a new release onto the development update site

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
