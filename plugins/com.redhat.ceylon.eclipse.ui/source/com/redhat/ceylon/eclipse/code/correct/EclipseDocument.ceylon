import org.eclipse.jface.text {
    IDocument
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}
import com.redhat.ceylon.eclipse.util {
    eclipseIndents
}

shared class EclipseDocument(shared IDocument document) 
        satisfies CommonDocument {
    
    getLineContent(Integer line)
            => let (info = document.getLineInformation(line))
            document.get(info.offset, info.length);

    getLineStartOffset(Integer line)
            => document.getLineInformation(line).offset;
    
    getLineEndOffset(Integer line)
            => document.getLineInformation(line).offset
             + document.getLineInformation(line).length;
    
    getLineOfOffset(Integer offset)
            => document.getLineOfOffset(offset);
    
    getText(Integer offset, Integer length)
            => document.get(offset, length);
    
    defaultLineDelimiter
            => eclipseIndents.getDefaultLineDelimiter(document);
    
    indentSpaces => eclipseIndents.indentSpaces;
    
    indentWithSpaces => eclipseIndents.indentWithSpaces;
    
}