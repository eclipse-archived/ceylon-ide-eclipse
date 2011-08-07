ECLIPSE=/Users/max/products/eclipse/indigo

local_site=/Users/max/impmirror
local_update_site=file:/$local_site

original_site=http://download.eclipse.org/technology/imp/updates/

echo Mirroring metadata for $original_site
java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source $original_site -destination $local_update_site -destinationName "IMP Mirror" -verbose 

echo Mirroring artifacts for $original_site
java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source $original_site -destination $local_update_site -destinationName "IMP Mirror" -verbose 

echo Fixing site to include p2
rm $local_site/artifacts.*
rm $local_site/content.*
java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository $local_update_site -artifactRepository $local_update_site -source $local_site -publishArtifacts