package homemadejson.parser;

import homemadejson.support.InputDataBuffer;
import homemadejson.support.TokenBuffer;

/**
 * Class for navigating parsed output from the JsonParser
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
            case ElementTypes.JSON_ARRAY_VALUE_STRING   : { return new String(this.buffer.data, this.elementBuffer.position[this.elementIndex], this.elementBuffer.length[this.elementIndex]); }
        }
        return null;
    }

    public boolean asBoolean() {
        return 't' == this.buffer.data[this.elementBuffer.position[this.elementIndex]];
    }

    public int asInt() {
        int value = 0;
        int tempPos = this.elementBuffer.position[this.elementIndex];
        for (int i = 0, n = this.elementBuffer.length[this.elementIndex]; i < n; i++) {
            value *= 10;
            value += this.buffer.data[tempPos] - 48;
            tempPos++;
        }
        return value;
    }

    public double asDouble() {
        int tempPos = this.elementBuffer.position[this.elementIndex];
        int i = 0;
        int length = this.elementBuffer.length[this.elementIndex];

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
        return value / divisor;
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
