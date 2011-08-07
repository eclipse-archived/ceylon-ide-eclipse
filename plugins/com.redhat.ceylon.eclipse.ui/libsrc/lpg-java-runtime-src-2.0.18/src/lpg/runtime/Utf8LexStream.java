package lpg.runtime;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//
// LexStreamBase contains an array of characters as the input stream to be parsed.
// There are methods to retrieve and classify characters.
// The lexparser "token" is implemented simply as the index of the next character in the array.
// The user must subclass LexStreamBase and implement the abstract methods: getKind.
//
public class Utf8LexStream implements ILexStream, ParseErrorCodes
{
    private final static int DEFAULT_TAB = 1;

    //
    // For each byte i in the range 0..FF, compute the number of bytes
    // required to store a UTF8 character sequence that starts with i.
    // If i is not a valid starting character for UTF8 then we compute
    // 0. The array charSize is used to store the values.
    //
    private static byte[] charSize = new byte[256];
    static
    {
        //
        // The base Ascii characters
        //
        for (int i = 0; i < 0x80; i++)
            charSize[i] = 1;

        //
        // A character with a bit sequence in the range:
        //
        //    0B10000000..0B10111111
        //
        // cannot be a leading UTF8 character.
        //
        for (int i = 0x80; i < 0xCE; i++)
            charSize[i] = 0;

        //
        // A leading character in the range 0xCE..0xDF
        //
        //    0B11000000..0B11011111
        //
        // identifies a two-bytes sequence
        //
        for (int i = 0xCE; i < 0xE0; i++)
            charSize[i] = 2;

        //
        // A leading character in the range 0xE0..0xEF
        //
        //    0B11100000..0B11101111
        //
        // identifies a three-bytes sequence
        //
        for (int i = 0xE0; i < 0xF0; i++)
            charSize[i] = 3;

        //
        // A leading character in the range 0xF0..0xF7
        //
        //    0B11110000..0B11110111
        //
        // identifies a four-bytes sequence
        //
        for (int i = 0xF0; i < 0xF8; i++)
            charSize[i] = 4;

        //
        // A leading character in the range 0xF8..0xFB
        //
        //    0B11111000..0B11111011
        //
        // identifies a five-bytes sequence
        //
        for (int i = 0xF8; i < 0xFC; i++)
            charSize[i] = 5;

        //
        // A leading character in the range 0xFC..0xFD
        //
        //    0B11111100..0B11111101
        //
        // identifies a six-bytes sequence
        //
        for (int i = 0xFC; i < 0xFE; i++)
            charSize[i] = 6;

        //
        // The characters 
        //
        //    0B11111110 and 0B11111111
        //
        // are not valid leading UTF8 characters as they would indicate
        // a sequence of 7 characters which is not possible.
        //
        for (int i = 0xFE; i < 0xFF; i++)
            charSize[i] = 0;
    }

    //
    // Compute the number of bytes required to store a UTF8 character
    // sequence that starts with the character c. The size of a character
    // is always 1 when we are using the Exctended Ascii encoding.
    //
    public int getCharSize(byte c) { return isUTF8 ? charSize[c & 0x000000FF] : 1; }

    //
    // Compute the code value of a Unicode character encoded in UTF8
    // format in the array inputBytes starting at index i.
    //
    final static public int getUnicodeValue(byte[] bytes, int i)
    {
        int code;
        
        try
        {
            code = bytes[i] & 0xFF;
            int size = charSize[code];

            switch(size)
            {
                case 1:
                    break;
                case 0:
                    code = 0;
                    break;
                default:
                {
                    code &= (0xFF >> (size + 1));
                    for (int k = 1; k < size; k++)
                    {
                        int c = bytes[i + k];
                        if ((c & 0x000000C0) != 0x80) // invalid UTF8 character?
                        {
                            code = 0;
                            break;
                        }
                        code = (code << 6) + (c & 0x0000003F);
                    }
                    break;
                }
            }
        }
        catch (StringIndexOutOfBoundsException e)
        {
            throw new StringIndexOutOfBoundsException(i);
        }
        
        return code;
    }

    //
    // Construct a unicode string from the specified Utf8 substring of bytes.
    //
    public String getString(int offset, int count)
    {
        if (count < 0)
            throw new StringIndexOutOfBoundsException(count);

        char value[] = new char[count];
        int size = 0;
        for (int i = 0, j = offset;
             i < count;
             i += getCharSize(inputBytes[j]), j += getCharSize(inputBytes[j]))
            value[size++] = (char) getUnicodeValue(j);

        return new String(value, 0, size);        
    }
    
    private int startIndex = -1;
    private int index = -1;
    private int lastIndex = -1;
    private byte[] inputBytes;
    private boolean isUTF8;
    private String fileName;
    private IntSegmentedTuple lineOffsets;
    private int tab = DEFAULT_TAB;
    private IPrsStream iPrsStream;

    public Utf8LexStream() // can be used with explicit initialize call
    {
        lineOffsets = new IntSegmentedTuple(12); 
        setLineOffset(-1);
    }

    public Utf8LexStream(int tab) { this(); this.tab = tab; } // can be used with explicit initialize call

    public Utf8LexStream(String fileName) throws IOException
    {
        this(fileName, DEFAULT_TAB);
    }

    public Utf8LexStream(String fileName, int tab) throws IOException
    {
        this(tab);

        try
        {
            File f = new File(fileName);
            FileInputStream in = new FileInputStream(f);

            byte[] buffer = new byte[(int) f.length()];

            in.read(buffer, 0, buffer.length);

            initialize(buffer, fileName);
        }
        catch (Exception e)
        {
            IOException io = new IOException();
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw(io);
        }
    }

    public Utf8LexStream(byte[] inputBytes, String fileName)
    {
        this();
        initialize(inputBytes, fileName);
    }

    public Utf8LexStream(IntSegmentedTuple lineOffsets, byte[] inputBytes, String fileName)
    {
        initialize(lineOffsets, inputBytes, fileName);
    }

    public Utf8LexStream(byte[] inputBytes, String fileName, int tab)
    {
        this(tab);
        initialize(inputBytes, fileName);
    }

    public Utf8LexStream(IntSegmentedTuple lineOffsets, byte[] inputBytes, String fileName, int tab)
    {
        this.tab = tab;
        initialize(lineOffsets, inputBytes, fileName);
    }

    public boolean isUtf8() { return isUTF8; }

    public boolean isExtendedAscii() { return ! isUTF8; }

    public void initialize(byte[] inputBytes, String fileName)
    {
        setInputBytes(inputBytes);
        setFileName(fileName);
        computeLineOffsets();
    }

    public void initialize(IntSegmentedTuple lineOffsets, byte[] inputBytes, String fileName)
    {
        this.lineOffsets = lineOffsets;
        setInputBytes(inputBytes);
        setFileName(fileName);
    }

    public void computeLineOffsets()
    {
        lineOffsets.reset();
        setLineOffset(-1);
        for (int i = startIndex + 1; i < inputBytes.length; i++)
            if (inputBytes[i] == 0x0A) setLineOffset(i);
    }

    public void setInputBytes(byte[] buffer)
    {
        this.inputBytes = buffer;
        this.isUTF8 = (buffer.length >= 3 &&
                       (buffer[0] & 0x000000FF) == 0x00EF &&
                       (buffer[1] & 0x000000FF) == 0x00BB &&
                       (buffer[2] & 0x000000FF) == 0x00BF);
        this.startIndex = (this.isUTF8 ? 2 : -1);
        this.index = startIndex;
        this.lastIndex = getPrevious(buffer.length);
    }

    public byte[] getInputBytes() { return inputBytes; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileName() { return fileName; }

    public void setLineOffsets(IntSegmentedTuple lineOffsets) { this.lineOffsets = lineOffsets; }

    public IntSegmentedTuple getLineOffsets() { return lineOffsets; }

    public void setTab(int tab) { this.tab = tab; }

    public int getTab() { return tab; }
    
    public void setStreamIndex(int index) { this.index = index; }

    public int getStreamIndex() { return index; }

    public int getStartIndex() { return startIndex; }

    public int getLastIndex() { return lastIndex; }

    public int getStreamLength() { return inputBytes.length; }

    public void setLineOffset(int i) { lineOffsets.add(i); }

    public int getLineOffset(int i) { return lineOffsets.get(i); }

    public void setPrsStream(IPrsStream iPrsStream) { this.iPrsStream = iPrsStream; }
    
    public IPrsStream getIPrsStream() { return iPrsStream; }

    /**
     * @deprecated replaced by {@link #getIPrsStream()}
     */
    public IPrsStream getPrsStream() { return iPrsStream; }

    public String[] orderedExportedSymbols() { return null; }

    public char getCharValue(int i) { return (char)getUnicodeValue(i); }
    
    public int getIntValue(int i) { return getUnicodeValue(i); }
    
    public int getUnicodeValue(int i)
    {
        return (isUTF8
                   ? getUnicodeValue(this.inputBytes, i) // either UTF8
                   : inputBytes[i] & 0xFF);              // or Extended Ascii.
    }

    public int getLineCount() { return lineOffsets.size(); }

    public int getLineNumberOfCharAt(int i)
    {
        int index = lineOffsets.binarySearch(i);
        return index < 0 ? -index : index == 0 ? 1 : index;
    }

    public int getColumnOfCharAt(int i)
    {
        int lineNo = getLineNumberOfCharAt(i),
            start = getLineOffset(lineNo - 1),
            tab = getTab();
        if (start + 1 >= inputBytes.length) return 1;        
        for (int k = start + 1; k < i; k = getNext(k))
        {
            byte c = inputBytes[k];
            if (c == '\t')
            {
                int offset = (k - start) - 1;
                start -= ((tab - 1) - offset % tab);
            }
            start += (getCharSize(c) - 1); // adjust for multibyte character.
        }

        return i - start;
    }

    //
    // Methods that implement the TokenStream Interface.
    // Note that this function updates the lineOffsets table
    // as a side-effect when the next character is a line feed.
    // If this is not the expected behavior then this function should 
    // be overridden.
    //
    public int getToken() { return index = getNext(index); }

    public int getToken(int end_token)
         { return index = (index < end_token ? getNext(index) : lastIndex); }

    public int getKind(int i) { return 0; }

    public int getNext(int i)
    {
        return (i <= startIndex
                   ? startIndex + 1
                   : i < inputBytes.length
                       ? i + getCharSize(this.inputBytes[i])
                       : lastIndex);
    }

    public int getPrevious(int i)
    {
        i = (i > startIndex ? i - 1 : startIndex);
        if (this.isUTF8)
        {
            while (i > startIndex) // Only do this for UTF8 encoded files.
            {
                if ((this.inputBytes[i] & 0x000000C0) != 0x80) // not a starting byte?
                    break;
                i--;
            }
        }
        return i;
    }

    public String getName(int i)
    {
        int c = getUnicodeValue(i);
        if (c <= 0xFFFF)
             return "" + (char) c;
        else return "#x" + Integer.toHexString(i);
    }

    public String getName(int i, int k)
    {
        String name = ""; // TODO: do this more efficiently with StringBuffer?
        for (int j = i; j <= k; j++)
        {
            int c = getUnicodeValue(j);
            if (c <= 0xFFFF)
                 name += (char) c;
            else name += ("#x" + Integer.toHexString(j));
        }
        return name;
    }

    public int peek() { return getNext(index); }

    public void reset(int i) { index = getPrevious(i); }

    public void reset() { index = startIndex; }

    public int badToken() { return 0; }

    public int getLine(int i) { return getLineNumberOfCharAt(i); }

    public int getColumn(int i) { return getColumnOfCharAt(i); }

    public int getEndLine(int i) { return getLine(i); }

    public int getEndColumn(int i) { return getColumnOfCharAt(i); }

    public boolean afterEol(int i) { return (i < 1 ? true : getLineNumberOfCharAt(getPrevious(i)) < getLineNumberOfCharAt(i)); }

    /**
     * @deprecated replaced by {@link #getFirstRealToken()}
     *
     */
    public int getFirstErrorToken(int i) { return getFirstRealToken(i); }
    public int getFirstRealToken(int i) { return i; }

    /**
     * @deprecated replaced by {@link #getLastRealToken()}
     *
     */
    public int getLastErrorToken(int i) { return getLastRealToken(i); }
    public int getLastRealToken(int i) { return i; }

    //
    // Here is where we report errors.  The default method is simply to print the error message to the console.
    // However, the user may supply an error message handler to process error messages.  To support that
    // a message handler interface is provided that has a single method handleMessage().  The user has his
    // error message handler class implement the IMessageHandler interface and provides an object of this type
    // to the runtime using the setMessageHandler(errorMsg) method. If the message handler object is set, 
    // the reportError methods will invoke its handleMessage() method.
    //
    private IMessageHandler errMsg = null;// this is the error message handler object
    
    public void setMessageHandler(IMessageHandler errMsg) {
        this.errMsg = errMsg;
    }

    public IMessageHandler getMessageHandler() {
        return errMsg;
    }
    
    public void makeToken(int startLoc, int endLoc, int kind)
    {
        if (iPrsStream != null) // let the parser find the error
             iPrsStream.makeToken(startLoc, endLoc, kind);
        else reportLexicalError(startLoc, endLoc); // make it a lexical error
    }
    
    public void reportLexicalError(int left_loc, int right_loc)
    {
        int errorCode = (right_loc >= inputBytes.length
                                    ? EOF_CODE
                                    : left_loc == right_loc
                                    ? LEX_ERROR_CODE
                                    : INVALID_TOKEN_CODE),
            end_loc = (left_loc == right_loc ? right_loc : right_loc - 1);
        String tokenText = (errorCode == EOF_CODE
                                       ? "End-of-file "
                                       : errorCode == INVALID_TOKEN_CODE
                                       ? "\"" + new String(inputBytes, left_loc, right_loc - left_loc) + "\" "
                                       : "\"" + ((char) getUnicodeValue(left_loc)) + "\" ");
        reportLexicalError(errorCode, left_loc, end_loc, 0, 0, new String[] { tokenText });
    }

    /**
     * See IMessaageHandler for a description of the int[] return value.
     */
    public int[] getLocation(int left_loc, int right_loc)
    {
        int length = (right_loc < inputBytes.length
                                ? right_loc
                                : inputBytes.length - 1) - left_loc + 1;
        return new int[]
               { 
                   left_loc,
                   length,
                   getLineNumberOfCharAt(left_loc),
                   getColumnOfCharAt(left_loc),
                   getLineNumberOfCharAt(right_loc),
                   getColumnOfCharAt(right_loc)
               };
    }
    
    public void reportLexicalError(int errorCode, int left_loc, int right_loc, int error_left_loc, int error_right_loc, String errorInfo[])
    {
        if (errMsg == null)
        {
            String locationInfo = getFileName() + ':' + getLineNumberOfCharAt(left_loc) + ':'
                                                      + getColumnOfCharAt(left_loc) + ':'
                                                      + getLineNumberOfCharAt(right_loc) + ':'
                                                      + getColumnOfCharAt(right_loc) + ':'
                                                      + error_left_loc + ':'
                                                      + error_right_loc + ':'
                                                      + errorCode + ": ";
            System.out.print("****Error: " + locationInfo);
            if (errorInfo != null)
            {
                for (int i = 0; i < errorInfo.length; i++)
                    System.out.print(errorInfo[i] + " ");
            }
            System.out.println(errorMsgText[errorCode]);
        }
        else
        {
            /**
             * This is the only method in the IMessageHandler interface
             * It is called with the following arguments:
             */
            errMsg.handleMessage(errorCode,
                                 getLocation(left_loc, right_loc),
                                 getLocation(error_left_loc, error_right_loc),
                                 getFileName(),
                                 errorInfo);
        }
    }

    //
    // Note that when this function is invoked, the leftToken and rightToken are assumed
    // to be indexes into the input stream as the tokens for a lexer are the characters
    // in the input stream.
    //
    public void reportError(int errorCode, int leftToken, int rightToken, String errorInfo)
    {
        reportError(errorCode, 
                    leftToken, 
                    0,
                    rightToken,
                    errorInfo == null ? null : new String[] { errorInfo });
    }
    
    public void reportError(int errorCode, int leftToken, int rightToken, String errorInfo[])
    {
        reportError(errorCode, 
                    leftToken, 
                    0,
                    rightToken,
                    errorInfo);
    }

    //
    // Note that when this function is invoked, the leftToken and rightToken are assumed
    // to be indexes into the input stream as the tokens for a lexer are the characters
    // in the input stream.
    //
    public void reportError(int errorCode, int leftToken, int errorToken, int rightToken, String errorInfo)
    {
        reportError(errorCode, 
                    leftToken, 
                    errorToken,
                    rightToken,
                    errorInfo == null ? null : new String[] { errorInfo });
    }

    public void reportError(int errorCode, int leftToken, int errorToken, int rightToken, String errorInfo[])
    {
        reportLexicalError(errorCode, 
                           leftToken, 
                           rightToken,
                           errorToken,
                           errorToken,
                           errorInfo == null ? new String[] {} : errorInfo);
    }

    public String toString(int startOffset, int endOffset)
    {
        int length = endOffset - startOffset + 1;
        return (endOffset >= inputBytes.length
                           ? "$EOF"
                           : length <= 0
                                     ? ""
                                     : getString(startOffset, length));
    }
}
