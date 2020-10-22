package homemadejson.parser;
import homemadejson.support.TokenBuffer;
import homemadejson.support.InputDataBuffer;

/**
 * Primary Tokenizer Class
 * Shane Riley
 */

public class JsonTokenizer {
//    Buffers
    InputDataBuffer dataBuffer;
    TokenBuffer tokenBuffer;

//    Iterators for each
    private int dataPosition = 0;
    private int tokenIndex = 0;

//    Length of current token
//    private int tokenLength = 0;

//    Constructors/reinit
    public JsonTokenizer() {}

    public JsonTokenizer(TokenBuffer tokenBuffer) {
        this.tokenBuffer = tokenBuffer;
    }

    public JsonTokenizer(InputDataBuffer dataBuffer, TokenBuffer tokenBuffer) {
        this.dataBuffer = dataBuffer;
        this.tokenBuffer = tokenBuffer;
    }

    public void reinit(InputDataBuffer dataBuffer, TokenBuffer tokenBuffer) {
        this.dataBuffer = dataBuffer;
        this.tokenBuffer = tokenBuffer;
        this.dataPosition = 0;
        this.tokenIndex = 0;
    }

//    Check for tokens
    public boolean hasMoreTokens() {
        System.out.println(this.dataPosition + " " + this.tokenBuffer.length[this.tokenIndex] + " " + this.dataBuffer.length);
        return (this.dataPosition + this.tokenBuffer.length[this.tokenIndex]) < this.dataBuffer.length ;
    }

//    Main token parser
    public void parseToken() {
        skipWhiteSpace();
        System.out.println("current " + this.dataBuffer.data[this.dataPosition]);
        this.tokenBuffer.position[this.tokenIndex] = this.dataPosition;  // set location of new token
        char nextChar = this.dataBuffer.data[this.dataPosition];

        switch(nextChar) {
            case '{' : { this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_CURLY_BRACKET_LEFT; break; }
            case '}' : { this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_CURLY_BRACKET_RIGHT; break; }
            case '[' : { this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_SQUARE_BRACKET_LEFT; break; }
            case ']' : { this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_SQUARE_BRACKET_RIGHT; break; }
            case ',' : { this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_COMMA; break; }
            case ':' : { this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_COLON; break; }

            case '"' : { parseString(); break; }
            case 'f' : { if(parseFalse()) { this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_BOOL_TOKEN; } break; }
            case 't' : { if(parseTrue()) { this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_BOOL_TOKEN; } break; }
            case 'n' : { if(parseNull()) { this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_NULL_TOKEN; } break; }

            case '0' : ;
            case '1' : ;
            case '2' : ;
            case '3' : ;
            case '4' : ;
            case '5' : ;
            case '6' : ;
            case '7' : ;
            case '8' : ;
            case '9' : { parseNumber(); this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_NUMBER_TOKEN; break; }
        }
    }

//    Next token
    public void nextToken() {
//        Move the position forward by the correct amount
        switch(this.tokenBuffer.type[this.tokenIndex]) {
            case TokenTypes.JSON_STRING_TOKEN           : { this.dataPosition += this.tokenBuffer.length[this.tokenIndex] + 2; break; }  // because quotes
            case TokenTypes.JSON_CURLY_BRACKET_LEFT     : ;
            case TokenTypes.JSON_CURLY_BRACKET_RIGHT    : ;
            case TokenTypes.JSON_SQUARE_BRACKET_LEFT    : ;
            case TokenTypes.JSON_SQUARE_BRACKET_RIGHT   : ;
            case TokenTypes.JSON_COLON                  : ;
            case TokenTypes.JSON_COMMA                  : { this.dataPosition++; break; }
            default                                     : { this.dataPosition += this.tokenBuffer.length[this.tokenIndex]; }
        }
        this.tokenIndex++;
    }
    public boolean peakColon(){
        int tempData = dataPosition;
        switch(this.tokenBuffer.type[this.tokenIndex]) {
            case TokenTypes.JSON_STRING_TOKEN           : { tempData += this.tokenBuffer.length[this.tokenIndex] + 2; break; }  // because quotes
            case TokenTypes.JSON_CURLY_BRACKET_LEFT     : ;
            case TokenTypes.JSON_CURLY_BRACKET_RIGHT    : ;
            case TokenTypes.JSON_SQUARE_BRACKET_LEFT    : ;
            case TokenTypes.JSON_SQUARE_BRACKET_RIGHT   : ;
            case TokenTypes.JSON_COLON                  : ;
            case TokenTypes.JSON_COMMA                  : { tempData++; break; }
            default                                     : { tempData += this.tokenBuffer.length[this.tokenIndex]; }
        }
        boolean isWhiteSpace = true;
        while (isWhiteSpace) {
            if (tempData >= dataBuffer.length) return false;
            switch (this.dataBuffer.data[tempData]) {
                case ' '    : ;
                case '\r'   : ;
                case '\n'   : ;
                case '\t'   : { tempData++; } break;

                default     : {isWhiteSpace = false;}
            }
        }
        
        System.out.println(dataBuffer.data[tempData]);
        return dataBuffer.data[tempData] == ':';

    }
//    Null parser
    private boolean parseNull() {
        if (    this.dataBuffer.data[this.dataPosition + 1] == 'u' &&
                this.dataBuffer.data[this.dataPosition + 2] == 'l' &&
                this.dataBuffer.data[this.dataPosition + 3] == 'l' ) {
            this.tokenBuffer.length[this.tokenIndex] = 4;
            return true;
        } else {
            return false;
        }
    }

//    True parser
    private boolean parseTrue() {
        if (    this.dataBuffer.data[this.dataPosition + 1] == 'r' &&
                this.dataBuffer.data[this.dataPosition + 2] == 'u' &&
                this.dataBuffer.data[this.dataPosition + 3] == 'e' ) {
            this.tokenBuffer.length[this.tokenIndex] = 4;
            return true;
        } else {
            return false;
        }
    }

//    False parser
    private boolean parseFalse() {
        if (    this.dataBuffer.data[this.dataPosition + 1] == 'a' &&
                this.dataBuffer.data[this.dataPosition + 2] == 'l' &&
                this.dataBuffer.data[this.dataPosition + 3] == 's' &&
                this.dataBuffer.data[this.dataPosition + 4] == 'e' ) {
            this.tokenBuffer.length[this.tokenIndex] = 5;
            return true;
        } else {
            return false;
        }
    }

//    Number parser
    private void parseNumber() {
        boolean endOfNumber = false;
        this.tokenBuffer.length[this.tokenIndex] = 1;

        while (!endOfNumber) {
            switch (this.dataBuffer.data[this.dataPosition + this.tokenBuffer.length[this.tokenIndex]]) {
                case '0': ;
                case '1': ;
                case '2': ;
                case '3': ;
                case '4': ;
                case '5': ;
                case '6': ;
                case '7': ;
                case '8': ;
                case '9': ;
                case '.': {
                    this.tokenBuffer.length[this.tokenIndex]++;
                    break;
                }

                default: {
                    endOfNumber = true;
                }  // number ends
            }
        }
    }

//    String parser
    private void parseString() {
        int tempPos = this.dataPosition;
        boolean endOfString = false;

        while (!endOfString) {
            tempPos++;
            if (this.dataBuffer.data[tempPos] == '"') {
                endOfString = true;
            }
        }

        this.tokenBuffer.type[this.tokenIndex] = TokenTypes.JSON_STRING_TOKEN;
        this.tokenBuffer.position[this.tokenIndex] = this.dataPosition + 1;
        this.tokenBuffer.length[this.tokenIndex] = (tempPos - this.dataPosition - 1) ;
    }

//    Whitespace skipper
    private void skipWhiteSpace() {
        boolean isWhiteSpace = true;
        while (isWhiteSpace) {
            switch (this.dataBuffer.data[this.dataPosition]) {
                case ' '    : ;
                case '\r'   : ;
                case '\n'   : ;
                case '\t'   : { this.dataPosition++; } break;

                default     : {isWhiteSpace = false;}
            }
        }
    }

//    Get methods
    public int tokenPosition() {
        return this.tokenBuffer.position[this.tokenIndex];
    }

    public int tokenLength() {
        return this.tokenBuffer.length[this.tokenIndex];
    }

    public byte tokenType() {
        return this.tokenBuffer.type[this.tokenIndex];
    }

}
