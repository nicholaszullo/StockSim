package homemadejson.support;

/**
 * A buffer for tokens, to be used as a guide to navigating the DataBuffer
 * Shane Riley
 */

public class TokenBuffer {
//    we need a position, a length, and a type
    public int[] position;  // starting positions
    public int[] length;  // length of each token
    public byte[] type;  // type of token (using TokenTypes)

    public int count = 0;  // size of above arrays, or number of tokens

//    Constructors
    public TokenBuffer() {}
    public TokenBuffer(int capacity) {
//        Initialize
        position = new int[capacity];
        length = new int[capacity];
        type = new byte[capacity];
    }
}
