package homemadejson.parser;

import homemadejson.support.InputDataBuffer;
import homemadejson.support.TokenBuffer;

/**
 * Class for navigating parsed output from the JsonParser
 * Parsed output means it stores the length of elements and the type of elements in a data array.
 * Navigator can then move to the next element in the string and interpret that
 * 
 * @author Shane Riley
 * @author Nick Zullo
 */
public class JsonNavigator {

    private InputDataBuffer buffer = null;
    private TokenBuffer elementBuffer = null;
    private int elementIndex = 0;

    public JsonNavigator(InputDataBuffer buffer, TokenBuffer elementBuffer) {
        this.buffer = buffer;
        this.elementBuffer = elementBuffer;
    }

    public boolean hasNext() {
        return this.elementIndex < this.elementBuffer.count - 1;
    }

    public void next() {
        this.elementIndex++;
    }

    public void previous() {
        this.elementIndex--;
    }

    public int position() {
        return this.elementBuffer.position[this.elementIndex];
    }

    public int length() {
        return this.elementBuffer.length[this.elementIndex];
    }

    public byte type() {
        return this.elementBuffer.type[this.elementIndex];
    }

    public String asString() {
        byte stringType = this.elementBuffer.type[this.elementIndex];
        switch(stringType) {
            case ElementTypes.JSON_PROPERTY_NAME        : ;
            case ElementTypes.JSON_PROPERTY_VALUE_STRING: ;
            case ElementTypes.JSON_ARRAY_VALUE_STRING   : { 
                String holder = new String(this.buffer.data, this.elementBuffer.position[this.elementIndex], this.elementBuffer.length[this.elementIndex]); 
                this.next();
                return holder;
            }
        }
        return null;
    }

    public boolean asBoolean() {
        boolean holder = 't' == this.buffer.data[this.elementBuffer.position[this.elementIndex]];
        this.next();
        return holder;
    }

    /** Use when the current element is a number. If it is a double, there will be a decimal point in the string
     * 
     * @return true if the current element is an int, false if it is a double
     */
    public boolean isInt(){
        for (int i = 0; i < this.elementBuffer.length[this.elementIndex]; i++){
            if (this.buffer.data[ this.elementBuffer.position[this.elementIndex] + i] == '.')
                return false;
        }
        return true;
    }

    public double parseSci(){
        boolean negative = false;
        boolean small = false;
        int tempPos = this.elementBuffer.position[this.elementIndex];
        int i = 0;
        double value = 0;
        if (this.buffer.data[tempPos] == '-'){
            tempPos++;
            negative = true;
            i++;
        }
        int decimalIndex = 0;

        while (this.buffer.data[tempPos] != 'E') {
            value *= 10;
            value += (this.buffer.data[tempPos] - 48);
            tempPos++;
            i++;
            if (this.buffer.data[tempPos] == '.') {
                tempPos++;
                i++;
                decimalIndex = i;
            }
        }
        int fractionLength = i - decimalIndex;
        double divisor = Math.pow(10, fractionLength);
        value /= divisor;
        tempPos++;      //Move pass E
        i++;
        if (this.buffer.data[tempPos] == '-'){
            small = true;
            tempPos++;
            i++;
        }
        int exponent = 0;
        while (i < this.elementBuffer.length[this.elementIndex]){
            exponent *= 10;
            exponent += this.buffer.data[tempPos] - '0';
            tempPos++;
            i++;
        }
        if (small){
            exponent *= -1;
        }
        System.out.println("value " + value + " " + exponent);
        value *= Math.pow(10, exponent);
        return negative ? -value : value;

    }

    public Integer asInt() {
        int value = 0;
        int tempPos = this.elementBuffer.position[this.elementIndex];
        int length = this.elementBuffer.length[this.elementIndex];
        boolean negative = false;
        int i = 0;
       if (this.buffer.data[tempPos] == '-'){
            i++;
            tempPos++;
            negative = true;
        }
        for (; i < length; i++) {
            value *= 10;
            value += this.buffer.data[tempPos] - 48;
            tempPos++;
        }
        this.next();
        return negative ? -value : value;
    }

    public double asDouble() {
        int tempPos = this.elementBuffer.position[this.elementIndex];
        int i = 0;
        int length = this.elementBuffer.length[this.elementIndex];
        boolean negative = false;
        if (this.buffer.data[tempPos] == '-'){
            tempPos++;
            length--;
            negative = true;
        }
        double value = 0;
        int decimalIndex = 0;

        for (; i < length; i++) {
            value *= 10;
            value += (this.buffer.data[tempPos] - 48);
            tempPos++;
            if (this.buffer.data[tempPos] == '.') {
                tempPos++;
                i++;
                decimalIndex = i;
                break;
            }
        }

        for (i++; i < length; i++) {
            value *= 10;
            value += (this.buffer.data[tempPos] - 48);
            tempPos++;
        }

        int fractionLength = length - decimalIndex - 1;
        double divisor = Math.pow(10, fractionLength);
        this.next();
        return  negative ? -value / divisor : value/divisor;
    }

    public boolean isEqualUnencoded(String target) {
        if (target.length() != this.elementBuffer.length[this.elementIndex]) return false;

        for (int i = 0, n = target.length(); i < n; i++) {
            if(target.charAt(i) != this.buffer.data[this.elementBuffer.position[this.elementIndex] + i]) return false;
        }
        return true;
    }

    public int countPrimitiveArrayElements() {
        int tempIndex = this.elementIndex + 1;
        while (this.elementBuffer.type[tempIndex] != ElementTypes.JSON_ARRAY_END) {
            tempIndex++;
        }
        return tempIndex - this.elementIndex - 1;
    }


}
