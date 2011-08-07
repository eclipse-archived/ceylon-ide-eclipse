ECLIPSE=/Users/max/products/eclipse/indigo
local_site=/Users/max/impmirror
local_update_site=file:/$local_site

echo Mirroring metadata for $1
#java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source $1 -destination $local_update_site -destinationName "IMP Mirror" -verbose 

echo Mirroring artifacts for $1
#java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source $1 -destination $local_update_site -destinationName "IMP Mirror" -verbose 

echo Fixing site to include p2
rm $local_site/artifacts.*
rm $local_site/content.*
java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository $local_update_site -artifactRepository $local_update_site -source $local_site -publishArtifacts