import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    ICompletionProposalExtension6,
    IContextInformation
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
    Image,
    Point
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
        
        value myImage = eclipseIcons.fromIcons(icon) else CeylonResources.minorChange;
        
        if (is EclipseTextChange change) {
            value region = toRegion(selection);
            
            Builder builder = switch (kind)
            case (addConstructor) createProposalWithShortcut("addConstructor")
            case (addParameterList) createProposalWithShortcut("addParameterList")
            else createGenericChangeProposal;
            
            return builder(description, change.nativeChange, region,
                myImage, qualifiedNameIsPath);
            
        } else if (is Callable<Anything, []> callback = change) {
            
            return object satisfies ICompletionProposal
                                  & ICompletionProposalExtension6 {
                
                apply(IDocument? iDocument) => callback();
                
                shared actual String? additionalProposalInfo => null;
                
                shared actual IContextInformation? contextInformation => null;
                
                displayString => description;
                
                shared actual Point? getSelection(IDocument iDocument)
                        => if (exists selection)
                           then Point(selection.start, selection.end)
                           else null;
                
                image => myImage;
                
                styledDisplayString
                        => styleProposal(displayString, qualifiedNameIsPath);
                
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