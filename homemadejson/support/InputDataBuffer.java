package homemadejson.support;

/**
 * A buffer for input data from json
 * Shane Riley
 */

public class InputDataBuffer {
//    data and length are all that is required
    public char[] data;
    public int length;

//    Constructors
    public InputDataBuffer() {}
    public InputDataBuffer(char[] inputData) { data = inputData; length = data.length;}
    public InputDataBuffer(int capacity) { data = new char[capacity]; }
}
