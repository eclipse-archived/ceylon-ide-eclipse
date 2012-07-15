package com.redhat.ceylon.eclipse.code.editor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;


public class HTMLTextPresenter implements DefaultInformationControl.IInformationPresenter, 
            DefaultInformationControl.IInformationPresenterExtension {

	private static final String LINE_DELIM= System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

	private int fCounter;
	private boolean fEnforceUpperLineLimit;

	/**
	 * Enables using bold font in order not to clip the text.
	 * <p>
	 * <em>Enabling this is a hack.</em>
	 * 
	 * @since 3.2
	 */
	private boolean fUseBoldFont= false;

	public HTMLTextPresenter(boolean enforceUpperLineLimit) {
		super();
		fEnforceUpperLineLimit= enforceUpperLineLimit;
	}

	public HTMLTextPresenter() {
		this(true);
	}

	protected HTML2TextReader createReader(String hoverInfo, TextPresentation presentation) {
		return new HTML2TextReader(new StringReader(hoverInfo), presentation);
	}

	protected void adaptTextPresentation(TextPresentation presentation, int offset, int insertLength) {

		int yoursStart= offset;
		int yoursEnd=   offset + insertLength -1;
		yoursEnd= Math.max(yoursStart, yoursEnd);

		Iterator e= presentation.getAllStyleRangeIterator();
		while (e.hasNext()) {

			StyleRange range= (StyleRange) e.next();

			int myStart= range.start;
			int myEnd=   range.start + range.length -1;
			myEnd= Math.max(myStart, myEnd);

			if (myEnd < yoursStart)
				continue;

			if (myStart < yoursStart)
				range.length += insertLength;
			else
				range.start += insertLength;
		}
	}

	private void append(StringBuffer buffer, String string, TextPresentation presentation) {

		int length= string.length();
		buffer.append(string);

		if (presentation != null)
			adaptTextPresentation(presentation, fCounter, length);

		fCounter += length;
	}

	private String getIndent(String line) {
		int length= line.length();

		int i= 0;
		while (i < length && Character.isWhitespace(line.charAt(i))) ++i;

		return (i == length ? line : line.substring(0, i)) + " "; //$NON-NLS-1$
	}

	/*
	 * @see IHoverInformationPresenter#updatePresentation(Display display, String, TextPresentation, int, int)
	 */
	public String updatePresentation(Display display, String hoverInfo, TextPresentation presentation, int maxWidth, int maxHeight) {
		return updatePresentation((Drawable)display, hoverInfo, presentation, maxWidth, maxHeight);
	}
	
	/*
	 * @see IHoverInformationPresenterExtension#updatePresentation(Drawable drawable, String, TextPresentation, int, int)
	 * @since 3.2
	 */
	public String updatePresentation(Drawable drawable, String hoverInfo, TextPresentation presentation, int maxWidth, int maxHeight) {

		if (hoverInfo == null)
			return null;

		GC gc= new GC(drawable);
		
		Font font= null;
		if (fUseBoldFont) {
			font= gc.getFont();
			FontData[] fontData= font.getFontData();
			for (int i= 0; i < fontData.length; i++)
				fontData[i].setStyle(SWT.BOLD);
			font= new Font(gc.getDevice(), fontData);
			gc.setFont(font);
		}
		
		try {

			StringBuffer buffer= new StringBuffer();
			int maxNumberOfLines= Math.round((float)maxHeight / gc.getFontMetrics().getHeight());

			fCounter= 0;
			LineBreakingReader reader= new LineBreakingReader(createReader(hoverInfo, presentation), gc, maxWidth);

			boolean lastLineFormatted= false;
			String lastLineIndent= null;

			String line=reader.readLine();
			boolean lineFormatted= reader.isFormattedLine();
			boolean firstLineProcessed= false;

			while (line != null) {

				if (fEnforceUpperLineLimit && maxNumberOfLines <= 0)
					break;

				if (firstLineProcessed) {
					if (!lastLineFormatted)
						append(buffer, LINE_DELIM, null);
					else {
						append(buffer, LINE_DELIM, presentation);
						if (lastLineIndent != null)
							append(buffer, lastLineIndent, presentation);
					}
				}

				append(buffer, line, null);
				firstLineProcessed= true;

				lastLineFormatted= lineFormatted;
				if (!lineFormatted)
					lastLineIndent= null;
				else if (lastLineIndent == null)
					lastLineIndent= getIndent(line);

				line= reader.readLine();
				lineFormatted= reader.isFormattedLine();

				maxNumberOfLines--;
			}

			if (line != null && buffer.length() > 0) {
				append(buffer, LINE_DELIM, lineFormatted ? presentation : null);
//				append(buffer, JavaUIMessages.HTMLTextPresenter_ellipsis, presentation);
				append(buffer, "...", presentation);
			}

			return trim(buffer, presentation);

		} 
		catch (IOException e) {
            e.printStackTrace();
			return null;

		} 
		finally {
			if (font != null)
				font.dispose();	
			gc.dispose();
		}
	}
	
	private String trim(StringBuffer buffer, TextPresentation presentation) {

		int length= buffer.length();

		int end= length -1;
		while (end >= 0 && Character.isWhitespace(buffer.charAt(end)))
			-- end;

		if (end == -1)
			return ""; //$NON-NLS-1$

		if (end < length -1)
			buffer.delete(end + 1, length);
		else
			end= length;

		int start= 0;
		while (start < end && Character.isWhitespace(buffer.charAt(start)))
			++ start;

		buffer.delete(0, start);
		presentation.setResultWindow(new Region(start, buffer.length()));
		return buffer.toString();
	}
}

class LineBreakingReader {

	private BufferedReader fReader;
	private GC fGC;
	private int fMaxWidth;

	private String fLine;
	private int fOffset;

	private BreakIterator fLineBreakIterator;
	private boolean fBreakWords;

	/**
	 * Creates a reader that breaks an input text to fit in a given width.
	 * 
	 * @param reader Reader of the input text
	 * @param gc The graphic context that defines the currently used font sizes
	 * @param maxLineWidth The max width (pixels) where the text has to fit in
	 */
	public LineBreakingReader(Reader reader, GC gc, int maxLineWidth) {
		fReader= new BufferedReader(reader);
		fGC= gc;
		fMaxWidth= maxLineWidth;
		fOffset= 0;
		fLine= null;
		fLineBreakIterator= BreakIterator.getLineInstance();
		fBreakWords= true;
	}

	public boolean isFormattedLine() {
		return fLine != null;
	}

	/**
	 * Reads the next line. The lengths of the line will not exceed the given maximum
	 * width.
	 * 
	 * @return the next line 
	 * @throws IOException 
	 */
	public String readLine() throws IOException {
		if (fLine == null) {
			String line= fReader.readLine();
			if (line == null)
				return null;

			int lineLen= fGC.textExtent(line).x;
			if (lineLen < fMaxWidth) {
				return line;
			}
			fLine= line;
			fLineBreakIterator.setText(line);
			fOffset= 0;
		}
		int breakOffset= findNextBreakOffset(fOffset);
		String res;
		if (breakOffset != BreakIterator.DONE) {
			res= fLine.substring(fOffset, breakOffset);
			fOffset= findWordBegin(breakOffset);
			if (fOffset == fLine.length()) {
				fLine= null;
			}
		} else {
			res= fLine.substring(fOffset);
			fLine= null;
		}
		return res;
	}

	private int findNextBreakOffset(int currOffset) {
		int currWidth= 0;
		int nextOffset= fLineBreakIterator.following(currOffset);
		while (nextOffset != BreakIterator.DONE) {
			String word= fLine.substring(currOffset, nextOffset);
			int wordWidth= fGC.textExtent(word).x;
			int nextWidth= wordWidth + currWidth;
			if (nextWidth > fMaxWidth) {
				if (currWidth > 0)
					return currOffset;

				if (!fBreakWords)
					return nextOffset;

				// need to fit into fMaxWidth
				int length= word.length();
				while (length >= 0) {
					length--;
					word= word.substring(0, length);
					wordWidth= fGC.textExtent(word).x;
					if (wordWidth + currWidth < fMaxWidth)
						return currOffset + length;
				}
				return nextOffset;
			}
			currWidth= nextWidth;
			currOffset= nextOffset;
			nextOffset= fLineBreakIterator.next();
		}
		return nextOffset;
	}

	private int findWordBegin(int idx) {
		while (idx < fLine.length() && Character.isWhitespace(fLine.charAt(idx))) {
			idx++;
		}
		return idx;
	}
}

class HTML2TextReader extends SubstitutionTextReader {

	private static final String EMPTY_STRING= ""; //$NON-NLS-1$
	private static final Map fgEntityLookup;
	private static final Set fgTags;

	static {

		fgTags= new HashSet();
		fgTags.add("b"); //$NON-NLS-1$
		fgTags.add("br"); //$NON-NLS-1$
		fgTags.add("br/"); //$NON-NLS-1$
		fgTags.add("div"); //$NON-NLS-1$
		fgTags.add("h1"); //$NON-NLS-1$
		fgTags.add("h2"); //$NON-NLS-1$
		fgTags.add("h3"); //$NON-NLS-1$
		fgTags.add("h4"); //$NON-NLS-1$
		fgTags.add("h5"); //$NON-NLS-1$
		fgTags.add("p"); //$NON-NLS-1$
		fgTags.add("dl"); //$NON-NLS-1$
		fgTags.add("dt"); //$NON-NLS-1$
		fgTags.add("dd"); //$NON-NLS-1$
		fgTags.add("li"); //$NON-NLS-1$
		fgTags.add("ul"); //$NON-NLS-1$
		fgTags.add("pre"); //$NON-NLS-1$
		fgTags.add("head"); //$NON-NLS-1$

		fgEntityLookup= new HashMap(7);
		fgEntityLookup.put("lt", "<"); //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("gt", ">"); //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("nbsp", " "); //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("amp", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("circ", "^"); //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("tilde", "~"); //$NON-NLS-2$ //$NON-NLS-1$
		fgEntityLookup.put("quot", "\"");		 //$NON-NLS-1$ //$NON-NLS-2$
	}

	private int fCounter= 0;
	private TextPresentation fTextPresentation;
	private int fBold= 0;
	private int fStartOffset= -1;
	private boolean fInParagraph= false;
	private boolean fIsPreformattedText= false;
	private boolean fIgnore= false;

	/**
	 * Transforms the HTML text from the reader to formatted text.
	 *
	 * @param reader the reader
	 * @param presentation If not <code>null</code>, formattings will be applied to
	 * the presentation.
	*/
	public HTML2TextReader(Reader reader, TextPresentation presentation) {
		super(new PushbackReader(reader));
		fTextPresentation= presentation;
	}

	public int read() throws IOException {
		int c= super.read();
		if (c != -1)
			++ fCounter;
		return c;
	}

	protected void startBold() {
		if (fBold == 0)
			fStartOffset= fCounter;
		++ fBold;
	}

	protected void startPreformattedText() {
		fIsPreformattedText= true;
		setSkipWhitespace(false);
	}

	protected void stopPreformattedText() {
		fIsPreformattedText= false;
		setSkipWhitespace(true);
	}

	protected void stopBold() {
		-- fBold;
		if (fBold == 0) {
			if (fTextPresentation != null) {
				fTextPresentation.addStyleRange(new StyleRange(fStartOffset, fCounter - fStartOffset, null, null, SWT.BOLD));
			}
			fStartOffset= -1;
		}
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.SubstitutionTextReader#computeSubstitution(int)
	 */
	protected String computeSubstitution(int c) throws IOException {

		if (c == '<')
			return  processHTMLTag();
		else if (fIgnore)
			return EMPTY_STRING;
		else if (c == '&')
			return processEntity();
		else if (fIsPreformattedText)
			return processPreformattedText(c);

		return null;
	}

	private String html2Text(String html) {

		if (html == null || html.length() == 0)
			return EMPTY_STRING;

		html= html.toLowerCase();
		
		String tag= html;
		if ('/' == tag.charAt(0))
			tag= tag.substring(1);

		if (!fgTags.contains(tag))
			return EMPTY_STRING;


		if ("pre".equals(html)) { //$NON-NLS-1$
			startPreformattedText();
			return EMPTY_STRING;
		}

		if ("/pre".equals(html)) { //$NON-NLS-1$
			stopPreformattedText();
			return EMPTY_STRING;
		}

		if (fIsPreformattedText)
			return EMPTY_STRING;

		if ("b".equals(html)) { //$NON-NLS-1$
			startBold();
			return EMPTY_STRING;
		}

		if ((html.length() > 1 && html.charAt(0) == 'h' && Character.isDigit(html.charAt(1))) || "dt".equals(html)) { //$NON-NLS-1$
			startBold();
			return EMPTY_STRING;
		}

		if ("dl".equals(html)) //$NON-NLS-1$
			return LINE_DELIM;

		if ("dd".equals(html)) //$NON-NLS-1$
			return "\t"; //$NON-NLS-1$

		if ("li".equals(html)) //$NON-NLS-1$
			// FIXME: this hard-coded prefix does not work for RTL languages, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=91682
//			return LINE_DELIM + JavaUIMessages.HTML2TextReader_listItemPrefix;
			return LINE_DELIM + "\t-";
		

		if ("/b".equals(html)) { //$NON-NLS-1$
			stopBold();
			return EMPTY_STRING;
		}

		if ("p".equals(html))  { //$NON-NLS-1$
			fInParagraph= true;
			return LINE_DELIM;
		}

		if ("br".equals(html) || "br/".equals(html) || "div".equals(html)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return LINE_DELIM;

		if ("/p".equals(html))  { //$NON-NLS-1$
			boolean inParagraph= fInParagraph;
			fInParagraph= false;
			return inParagraph ? EMPTY_STRING : LINE_DELIM;
		}

		if ((html.startsWith("/h") && html.length() > 2 && Character.isDigit(html.charAt(2))) || "/dt".equals(html)) { //$NON-NLS-1$ //$NON-NLS-2$
			stopBold();
			return LINE_DELIM;
		}

		if ("/dd".equals(html)) //$NON-NLS-1$
			return LINE_DELIM;
		
		if ("head".equals(html)) { //$NON-NLS-1$
			fIgnore= true;
			return EMPTY_STRING;
		}
		
		if ("/head".equals(html)) { //$NON-NLS-1$
			fIgnore= false;
			return EMPTY_STRING;
		}

		return EMPTY_STRING;
	}

	/*
	 * A '<' has been read. Process a html tag
	 */
	private String processHTMLTag() throws IOException {

		StringBuffer buf= new StringBuffer();
		int ch;
		do {

			ch= nextChar();

			while (ch != -1 && ch != '>') {
				buf.append(Character.toLowerCase((char) ch));
				ch= nextChar();
				if (ch == '"'){
					buf.append(Character.toLowerCase((char) ch));
					ch= nextChar();
					while (ch != -1 && ch != '"'){
						buf.append(Character.toLowerCase((char) ch));
						ch= nextChar();
					}
				}
				if (ch == '<'){
					unread(ch);
					return '<' + buf.toString();
				}
			}

			if (ch == -1)
				return null;

			int tagLen= buf.length();
			// needs special treatment for comments
			if ((tagLen >= 3 && "!--".equals(buf.substring(0, 3))) //$NON-NLS-1$
				&& !(tagLen >= 5 && "--".equals(buf.substring(tagLen - 2)))) { //$NON-NLS-1$
				// unfinished comment
				buf.append(ch);
			} else {
				break;
			}
		} while (true);

		return html2Text(buf.toString());
	}

	private String processPreformattedText(int c) {
		if  (c == '\r' || c == '\n')
			fCounter++;
		return null;
	}


	private void unread(int ch) throws IOException {
		((PushbackReader) getReader()).unread(ch);
	}

	protected String entity2Text(String symbol) {
		if (symbol.length() > 1 && symbol.charAt(0) == '#') {
			int ch;
			try {
				if (symbol.charAt(1) == 'x') {
					ch= Integer.parseInt(symbol.substring(2), 16);
				} else {
					ch= Integer.parseInt(symbol.substring(1), 10);
				}
				return EMPTY_STRING + (char)ch;
			} catch (NumberFormatException e) {
			}
		} else {
			String str= (String) fgEntityLookup.get(symbol);
			if (str != null) {
				return str;
			}
		}
		return "&" + symbol; // not found //$NON-NLS-1$
	}

	/*
	 * A '&' has been read. Process a entity
	 */
	private String processEntity() throws IOException {
		StringBuffer buf= new StringBuffer();
		int ch= nextChar();
		while (Character.isLetterOrDigit((char)ch) || ch == '#') {
			buf.append((char) ch);
			ch= nextChar();
		}

		if (ch == ';')
			return entity2Text(buf.toString());

		buf.insert(0, '&');
		if (ch != -1)
			buf.append((char) ch);
		return buf.toString();
	}
}

abstract class SingleCharReader extends Reader {
	
	/**
	 * @see Reader#read()
	 */
	public abstract int read() throws IOException;

	/**
	 * @see Reader#read(char[],int,int)
	 */
	public int read(char cbuf[], int off, int len) throws IOException {
		int end= off + len;
		for (int i= off; i < end; i++) {
			int ch= read();
			if (ch == -1) {
				if (i == off) {
					return -1;
				} else {
					return i - off;
				}
			}
			cbuf[i]= (char)ch;
		}
		return len;
	}		
	
	/**
	 * @see Reader#ready()
	 */		
    public boolean ready() throws IOException {
		return true;
	}
	
	/**
	 * Gets the content as a String
	 */
	public String getString() throws IOException {
		StringBuffer buf= new StringBuffer();
		int ch;
		while ((ch= read()) != -1) {
			buf.append((char)ch);
		}
		return buf.toString();
	}
}

/**
 * Reads the text contents from a reader and computes for each character
 * a potential substitution. The substitution may eat more characters than
 * only the one passed into the computation routine.
 */
abstract class SubstitutionTextReader extends SingleCharReader {

	protected static final String LINE_DELIM= System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

	private Reader fReader;
	protected boolean fWasWhiteSpace;
	private int fCharAfterWhiteSpace;

	/**
	 * Tells whether white space characters are skipped.
	 */
	private boolean fSkipWhiteSpace= true;

	private boolean fReadFromBuffer;
	private StringBuffer fBuffer;
	private int fIndex;


	protected SubstitutionTextReader(Reader reader) {
		fReader= reader;
		fBuffer= new StringBuffer();
		fIndex= 0;
		fReadFromBuffer= false;
		fCharAfterWhiteSpace= -1;
		fWasWhiteSpace= true;
	}

	/**
	 * Implement to compute the substitution for the given character and
	 * if necessary subsequent characters. Use <code>nextChar</code>
	 * to read subsequent characters.
	 */
	protected abstract String computeSubstitution(int c) throws IOException;

	/**
	 * Returns the internal reader.
	 */
	protected Reader getReader() {
		return fReader;
	}

	/**
	 * Returns the next character.
	 */
	protected int nextChar() throws IOException {
		fReadFromBuffer= (fBuffer.length() > 0);
		if (fReadFromBuffer) {
			char ch= fBuffer.charAt(fIndex++);
			if (fIndex >= fBuffer.length()) {
				fBuffer.setLength(0);
				fIndex= 0;
			}
			return ch;
		} else {
			int ch= fCharAfterWhiteSpace;
			if (ch == -1) {
				ch= fReader.read();
			}
			if (fSkipWhiteSpace && Character.isWhitespace((char)ch)) {
				do {
					ch= fReader.read();
				} while (Character.isWhitespace((char)ch));
				if (ch != -1) {
					fCharAfterWhiteSpace= ch;
					return ' ';
				}
			} else {
				fCharAfterWhiteSpace= -1;
			}
			return ch;
		}
	}

	/**
	 * @see Reader#read()
	 */
	public int read() throws IOException {
		int c;
		do {

			c= nextChar();
			while (!fReadFromBuffer) {
				String s= computeSubstitution(c);
				if (s == null)
					break;
				if (s.length() > 0)
					fBuffer.insert(0, s);
				c= nextChar();
			}

		} while (fSkipWhiteSpace && fWasWhiteSpace && (c == ' '));
		fWasWhiteSpace= (c == ' ' || c == '\r' || c == '\n');
		return c;
	}

	/**
	 * @see Reader#ready()
	 */
    public boolean ready() throws IOException {
		return fReader.ready();
	}

	/**
	 * @see Reader#close()
	 */
	public void close() throws IOException {
		fReader.close();
	}

	/**
	 * @see Reader#reset()
	 */
	public void reset() throws IOException {
		fReader.reset();
		fWasWhiteSpace= true;
		fCharAfterWhiteSpace= -1;
		fBuffer.setLength(0);
		fIndex= 0;
	}

	protected final void setSkipWhitespace(boolean state) {
		fSkipWhiteSpace= state;
	}

	protected final boolean isSkippingWhitespace() {
		return fSkipWhiteSpace;
	}
}
