import com.redhat.ceylon.ide.common.model {
    CeylonProjects,
    CeylonProject
}
import org.eclipse.core.resources {
    IProject
}

shared object ceylonModel extends CeylonProjects<IProject>() {
    shared actual CeylonProject<IProject> newIdeArtifact(IProject ideArtifact)
        => EclipseCeylonProject(ideArtifact);
}