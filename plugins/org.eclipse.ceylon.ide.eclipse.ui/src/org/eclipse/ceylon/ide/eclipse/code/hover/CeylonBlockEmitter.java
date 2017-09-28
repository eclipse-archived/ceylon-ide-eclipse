package org.eclipse.ceylon.ide.eclipse.code.hover;

import java.util.List;

import com.github.rjeschke.txtmark.BlockEmitter;
import org.eclipse.ceylon.ide.eclipse.code.html.HTML;

public final class CeylonBlockEmitter implements BlockEmitter {
    
    @Override
    public void emitBlock(StringBuilder out, List<String> lines, String meta) {
        if (!lines.isEmpty()) {
            out.append("<pre>");
            /*if (meta == null || meta.length() == 0) {
                out.append("<pre>");
            } else {
                out.append("<pre class=\"brush: ").append(meta).append("\">");
            }*/
            StringBuilder code = new StringBuilder();
            for (String s: lines) {
                code.append(s).append('\n');
            }
            String highlighted;
            if (meta == null || meta.length() == 0 || "ceylon".equals(meta)) {
                highlighted = HTML.highlightLine(code.toString());
            }
            else {
                highlighted = code.toString();
            }
            out.append(highlighted);
            out.append("</pre>\n");
        }
    }

}