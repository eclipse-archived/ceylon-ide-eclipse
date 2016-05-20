import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import com.redhat.ceylon.ide.common.correct {
    QuickFixKind,
    addConstructor,
    addParameterList
}
import com.redhat.ceylon.ide.common.platform {
    CommonTextChange=TextChange
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.eclipse.platform {
    EclipseTextChange
}
import com.redhat.ceylon.eclipse.util {
    eclipseIcons,
    Highlights {
        styleProposal
    }
}
import org.eclipse.jface.text {
    IDocument,
    Region
}
import org.eclipse.swt.graphics {
    Image
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.jface.viewers {
    StyledString {
        qualifierStyler
    }
}
import com.redhat.ceylon.eclipse.code.correct { 
    CorrectionUtil {
        shortcut
    }
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}

object proposalsFactory {
    
    alias Builder => ICompletionProposal(String, TextChange, Region?, Image?, Boolean);
    
    shared ICompletionProposal createProposal(
        String description,
        CommonTextChange|Callable<Anything, []> change,
        DefaultRegion? selection,
        Boolean qualifiedNameIsPath,
        Icons? icon,
        QuickFixKind kind) {
        
        value image = eclipseIcons.fromIcons(icon) else CeylonResources.minorChange;
        
        if (is EclipseTextChange change) {
            value region = toRegion(selection);
            
            Builder builder = switch (kind)
            case (addConstructor) createProposalWithShortcut("addConstructor")
            case (addParameterList) createProposalWithShortcut("addParameterList")
            else createGenericChangeProposal;
            
            return builder(description, change.nativeChange, region,
                image, qualifiedNameIsPath);
        } else if (is Callable<Anything, []> callback = change) {
            return object extends CorrectionProposal(description, null, null) {
                shared actual void apply(IDocument? iDocument) {
                    callback();
                }
            };
        }

        throw Exception("Change not supported: " + className(change));
    }
    
    ICompletionProposal createGenericChangeProposal(String description,
        TextChange change, Region? region, Image? image, Boolean qualifiedNameIsPath)
            => CorrectionProposal(description, change, region, image, qualifiedNameIsPath);

    ICompletionProposal createProposalWithShortcut
    (String name)
    (String description,TextChange chg, Region? region, Image? img, Boolean qualifiedNameIsPath) {
        
        return object extends CorrectionProposal(description, chg, region, img, qualifiedNameIsPath) {
             styledDisplayString
                    => let (hint = shortcut("com.redhat.ceylon.eclipse.ui.action." + name))
                       styleProposal(displayString, false).append(hint, qualifierStyler);
        };
    }
    
    Region? toRegion(DefaultRegion? reg)
            => if (exists reg)
               then Region(reg.start, reg.length)
               else null;
}