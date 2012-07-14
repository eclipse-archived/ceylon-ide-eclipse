package com.redhat.ceylon.eclipse.code.editor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.parser.IModelListener;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

/**
 * A class that does the real work of repairing the text presentation for an associated ISourceViewer.
 * Calls to damage(IRegion) simply accumulate damaged regions into a work queue, which is processed
 * at the subsequent call to update(IParseController, IProgressMonitor).
 * @author Claffra
 * @author rfuhrer@watson.ibm.com
 */
public class PresentationController implements IModelListener {

    private final ISourceViewer fSourceViewer;
    private final CeylonTokenColorer fColorer;
    private final CeylonParseController fParseCtlr;

    private final Stack<IRegion> fWorkItems= new Stack<IRegion>();

    public PresentationController(ISourceViewer sourceViewer, CeylonParseController cpc) {
        fSourceViewer= sourceViewer;
        this.fParseCtlr= cpc;
        fColorer= new CeylonTokenColorer();
    }

    public AnalysisRequired getAnalysisRequired() {
        return AnalysisRequired.LEXICAL_ANALYSIS;
    }

    private void dumpToken(Object token, ISourcePositionLocator locator, PrintStream ps) {
        if (locator != null) {
            try {
                final IDocument document= fSourceViewer.getDocument();
                final int startOffset= locator.getStartOffset(token);
//              ps.print( " (" + prs.getKind(i) + ")");
                ps.print(" \t" + startOffset);
                ps.print(" \t" + locator.getLength(token));
                int line = document.getLineOfOffset(startOffset);
                ps.print(" \t" + line);
                ps.print(" \t" + (startOffset - document.getLineOffset(line)));
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        ps.print(" \t" + token);
        ps.println();
    }

    /**
     * Push the damaged area onto the work queue for repair when we get scheduled to process the queue.
     * @param region the damaged area
     */
    public void damage(IRegion region) {
        if (fColorer == null)
            return;

        IRegion bigRegion= fColorer.calculateDamageExtent(region, fParseCtlr);

        if (bigRegion != null) {
            synchronized (fWorkItems) {
                boolean redundant= false;
                for(IRegion dr : fWorkItems) {
                    if (contains(bigRegion, dr)) {
                        redundant= true;
                    }
                }
                if (!redundant) {
//                  System.out.println("damage(): got irredundant damage region: " + bigRegion.getOffset() + ":" + bigRegion.getLength());
                    fWorkItems.push(bigRegion);
                }
            }
        }
    }

    private boolean contains(IRegion r1, IRegion r2) {
        return r2.getOffset() <= r1.getOffset() && r2.getOffset() + r2.getLength() >= r1.getOffset() + r1.getLength();
    }

    public void update(IParseController controller, IProgressMonitor monitor) {
//        try {
//            throw new Exception();
//        } catch (Exception e) {
//            System.out.println("Entered PresentationController.update()");
//            e.printStackTrace(System.out);
//        }
        if (!monitor.isCanceled() && fSourceViewer != null && fSourceViewer.getDocument() != null) {
//          if (fWorkItems.size() == 0) {
//              ConsoleUtil.findConsoleStream(PresentationController.CONSOLE_NAME).println("PresentationController.update() called, but no damage in the work queue?");
//          }
            synchronized (fWorkItems) {
                if (fWorkItems.size() == 0 && fSourceViewer.getDocument() != null) {
                    // TODO Shouldn't need to re-color the entire source file here.
                    // This is intended to handle the case that the parser finishes *after*
                    // the PresentationRepairer asks for an update().
                    // We could do a more focused update, if we knew what part of the file had changed.
//                  System.out.println("PresentationController.update() called, but no work items; reprocessing entire document");
                    fWorkItems.add(new Region(0, fSourceViewer.getDocument().getLength()));
                }
                // TODO Optimization: when there are multiple work items, control redrawing explicitly.
                // See JavaDoc regarding ITextViewer.changeTextPresentation()'s 2nd argument.
                // Probably not very common (only refactoring or search/replace?), but perhaps worthwhile.
                for(int n= fWorkItems.size() - 1; !monitor.isCanceled() && n >= 0; n--) {
                    Region damage= (Region) fWorkItems.get(n);
//                  System.out.println(">>> Processing damage region: " + damage.getOffset() + ":" + damage.getLength());
                    changeTextPresentationForRegion(controller, monitor, damage);
                }
                // TODO Remove the work items we actually processed, whether the monitor was canceled or not
                if (!monitor.isCanceled()) {
                    fWorkItems.removeAllElements();
                }
            }
        }
    }

    private void changeTextPresentationForRegion(IParseController parseController, IProgressMonitor monitor, IRegion damage) {
        if (parseController == null) {
            return;
        }
        final TextPresentation presentation= new TextPresentation();
        ISourcePositionLocator locator= parseController.getSourcePositionLocator();
        aggregateTextPresentation(parseController, monitor, damage, presentation, locator);
        if (monitor.isCanceled()) {
            System.err.println("Ignored cancelled presentation update");
        } 
        else if (!presentation.isEmpty()) {
            submitTextPresentation(presentation);
        }
    }

    private void aggregateTextPresentation(IParseController parseController, IProgressMonitor monitor, IRegion damage, TextPresentation presentation,
            ISourcePositionLocator locator) {
        int prevOffset= -1;
        int prevEnd= -1;
        Iterator tokenIterator= parseController.getTokenIterator(damage);
        if (tokenIterator == null) {
            return;
        }
        for(Iterator<Object> iter= tokenIterator; iter.hasNext() && !monitor.isCanceled(); ) {
            Object token= iter.next();
            int offset= locator.getStartOffset(token);
            int end= locator.getEndOffset(token);

            if (offset <= prevEnd && end >= prevOffset) {
                continue;
            }
            changeTokenPresentation(parseController, presentation, token, locator);
            prevOffset= offset;
            prevEnd= end;
        }
    }

    private void changeTokenPresentation(IParseController controller, TextPresentation presentation, Object token, ISourcePositionLocator locator) {
        TextAttribute attribute= fColorer.getColoring(controller, token);

        StyleRange styleRange= new StyleRange(locator.getStartOffset(token), locator.getEndOffset(token) - locator.getStartOffset(token) + 1,
                attribute == null ? null : attribute.getForeground(),
                attribute == null ? null : attribute.getBackground(),
                attribute == null ? SWT.NORMAL : attribute.getStyle());

        // Negative (possibly 0) length style ranges will cause an 
        // IllegalArgumentException in changeTextPresentation(..)
        if (styleRange.length <= 0 || styleRange.start + styleRange.length > fSourceViewer.getDocument().getLength()) {
//          System.err.println("Omitting token '" + token + "' w/ empty style range: " + styleRange.start + ":" + styleRange.length);
        } else {
            presentation.addStyleRange(styleRange);
        }
    }

    private void submitTextPresentation(final TextPresentation presentation) {
        if (fSourceViewer == null) {
            return;
        }

        final int docLength= (fSourceViewer.getDocument() != null) ? fSourceViewer.getDocument().getLength() : 0;
        final TextPresentation newPresentation= fixPresentation(presentation, docLength, false /*sort?*/);

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
        	    try {
                    if (fSourceViewer != null) {
            	        // The document might have changed since the presentation was computed, so
            	        // trim the presentation's "result window" to the current document's extent.
            	        // This avoids upsetting SWT, but there's still a question as to whether
            	        // this is really the right thing to do. I.e., this assumes that the
            	        // presentation will get recomputed later on, when the new document change
            	        // gets noticed. But will it?
            	        int newDocLength= (fSourceViewer.getDocument() != null) ? fSourceViewer.getDocument().getLength() : 0;
            	        IRegion presExtent= newPresentation.getExtent();

            	        if (presExtent.getOffset() + presExtent.getLength() > newDocLength) {
//            	            System.out.println("Trimming result window...");
            	            newPresentation.setResultWindow(new Region(presExtent.getOffset(), newDocLength - presExtent.getOffset()));
            	        }
        	    		fSourceViewer.changeTextPresentation(newPresentation, true);
        	    	}
        	    } catch (IllegalArgumentException e) {
                    int curDocLength= (fSourceViewer.getDocument() != null) ? fSourceViewer.getDocument().getLength() : 0;
        	        diagnoseStyleRangeError(presentation, curDocLength, e);
        	    }
            }
        });
    }

    /**
     * Adjusts the StyleRanges in the given presentation as necessary to ones that
     * should be acceptable to ITextViewer.changeTextPresentation().
     * In particular, no range will extend beyond the end of the source text,
     * and their lengths will all be positive.
     * Optionally, will also sort the ranges, and ensure that they don't overlap. 
     */
    private TextPresentation fixPresentation(final TextPresentation presentation, int docLen, boolean sort) {
        if (checkPresentation(presentation, docLen)) {
            return presentation;
        }
        int lastStart = presentation.getLastStyleRange().start;
        int lastLength = presentation.getLastStyleRange().length;
        int end = lastStart + lastLength;

        List<StyleRange> newRanges= new ArrayList<StyleRange>(presentation.getDenumerableRanges());

        // Phase 1: Collect all ranges in a sortable data structure and trim each one
        // to ensure it lies within the document bounds.
        Iterator presIt = presentation.getAllStyleRangeIterator();
        while (presIt.hasNext()) {
            StyleRange nextRange = (StyleRange) presIt.next();
            if (nextRange.start < docLen) {
                if (nextRange.start + nextRange.length > docLen) {
                    nextRange.length= docLen - nextRange.start;
                }
                newRanges.add(nextRange);
            } else {
                // discard range that lies completely outside the document
            }
        }

        // Phase 2: sort the ranges by their start offset
        Collections.sort(newRanges, new Comparator<StyleRange>() {
            public int compare(StyleRange o1, StyleRange o2) {
                return o1.start - o2.start;
            }
        });

        // Phase 3: check for overlap of adjacent ranges and trim as needed
        StyleRange prevRange= newRanges.get(0);
        for(int i=1; i < newRanges.size(); i++) {
            StyleRange currRange= newRanges.get(i);
            if (currRange.start < prevRange.start + prevRange.length) {
                prevRange.length= currRange.start - prevRange.start;
            }
            prevRange= currRange;
        }

        // Phase 4: remove any ranges that are now empty (as a result of trimming in Phase 3)
        for(Iterator<StyleRange> ri= newRanges.iterator(); ri.hasNext(); ) {
            StyleRange r= ri.next();
            if (r.length <= 0) {
                ri.remove();
            }
        }

        // Final phase: construct new TextPresentation
        TextPresentation newPresentation = new TextPresentation();
        for(StyleRange r: newRanges) {
            newPresentation.addStyleRange(r);
        }
        return newPresentation;
    }

    /**
     * A fail-fast checker that returns false to indicate whether any problems
     * necessitate a fixing pass over the StyleRanges.
     */
    private boolean checkPresentation(TextPresentation presentation, int docLen) {
        Iterator<StyleRange> presIt = presentation.getAllStyleRangeIterator();
        int end= -1;

        while (presIt.hasNext()) {
            StyleRange r= presIt.next();
            int rangeStart= r.start;
            int rangeLen= r.length;
            if (rangeStart < end) {
                return false;
            }
            if (rangeLen < 1) {
                return false;
            }
            if (rangeStart + rangeLen > docLen) {
                return false;
            }
            end= Math.max(end, rangeStart+rangeLen);
        }
        return true; // ok
    }

    /**
     * Called when an IllegalArgumentException has occurred, presumably due to the
     * TextPresentation containing an inappropriate style range, or perhaps an invalid
     * combination of ranges (e.g., overlapping).
     * Try to determine the real cause of the problem, and add an appropriate message
     * for the exception in the plugin log.
     */
    private void diagnoseStyleRangeError(final TextPresentation presentation, int charCount, IllegalArgumentException e) {
        // Possible causes (not necessarily complete):
        // - negative length in a style range
        // - overlapping ranges
        // - range extends beyond last character in file
        Iterator<StyleRange> ranges = presentation.getAllStyleRangeIterator();
        List<StyleRange> rangesList = new ArrayList<StyleRange>();
        while (ranges.hasNext()) {
        	rangesList.add((StyleRange) ranges.next());
        }
        StringBuilder explanation = new StringBuilder();
        if (rangesList.size() > 0) {
        	StyleRange firstRange = rangesList.get(0);

        	if (firstRange.length < 0) {
        		explanation.append("Style range with start = " + firstRange.start + " has negative length = " + firstRange.length);
        	}
        	StyleRange prevRange= firstRange;
        	for (int i = 1; i < rangesList.size(); i++) {
        	    StyleRange currRange= rangesList.get(i);
                int currStart = currRange.start;
        	    int currLength = currRange.length;
        	    if (currLength < 0) {
        	        explanation.append("Style range with start = " + currStart + " has negative length = " + currLength);
        	        break;
        	    }

        	    int prevStart = prevRange.start;
        	    int prevLength = prevRange.length;
        	    if (prevStart + prevLength - 1 >= currStart) {
        	        explanation.append("Style range with start = " + prevStart + " and length = " + prevLength +
        	                " overlaps style range with start = " + currStart);
        	        break;
            	}
        	    prevRange= currRange;
        	}

        	int finalStart = presentation.getLastStyleRange().start;
        	int finalLength = presentation.getLastStyleRange().length;
        	int finalEnd = finalStart + finalLength;
        	if (finalEnd >= charCount) {
        	    explanation.append("Final style range with start = " + finalStart + " and length = " + finalLength + 
        	            " extends beyond last character (character count = " + charCount + ")");
        	}
        	if (explanation.length() == 0) {
        		explanation.append("Cause not identified");
        	}
        }

        ErrorHandler.logError("Malformed TextPresentation:" + explanation, e);
    }
}
