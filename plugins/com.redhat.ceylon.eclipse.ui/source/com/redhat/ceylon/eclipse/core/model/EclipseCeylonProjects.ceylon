import com.redhat.ceylon.ide.common.model {
    CeylonProjects,
    CeylonProject
}
import org.eclipse.core.resources {
    IProject
}

shared abstract class EclipseCeylonProjects() of ceylonModel  extends CeylonProjects<IProject>() {
    shared actual CeylonProject<IProject> newIdeArtifact(IProject ideArtifact)
        => EclipseCeylonProject(ideArtifact);
}

shared object ceylonModel extends EclipseCeylonProjects() {}