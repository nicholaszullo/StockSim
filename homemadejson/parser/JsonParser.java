package homemadejson.parser;

import homemadejson.support.InputDataBuffer;
import homemadejson.support.TokenBuffer;
import homemadejson.support.ParserException;

/**
 * Main parser. Creates elements out of tokens. Elements will be interpreted by the navigator. 
 * @author Shane Riley
 * @author Nick Zullo
 */
public class JsonParser {

//    Two token buffers, an index, and a tokenizer

    private TokenBuffer tokenBuffer;
    private TokenBuffer elementBuffer;
    private int elementIndex = 0;
    private JsonTokenizer jsonTokenizer;

//    Constructor
    public JsonParser(TokenBuffer tokenBuffer, TokenBuffer elementBuffer) {
        this.tokenBuffer = tokenBuffer;
        this.jsonTokenizer = new JsonTokenizer(this.tokenBuffer);
        this.elementBuffer = elementBuffer;
    }

//    reinit/parse
    public void parse(InputDataBuffer dataBuffer) {
        this.elementIndex = 0;
        this.jsonTokenizer.reinit(dataBuffer, this.tokenBuffer);
        jsonTokenizer.parseToken();
        if (jsonTokenizer.tokenType() == TokenTypes.JSON_SQUARE_BRACKET_LEFT){
            parseArray(jsonTokenizer);
        } else {
            parseObject(this.jsonTokenizer);
        }
        this.elementIndex++;
        this.elementBuffer.count = this.elementIndex;
    }


//    Object parse
    private void parseObject(JsonTokenizer tokenizer) {
        assertHasMoreTokens(tokenizer);         //If this method was called there must be more tokens
        tokenizer.parseToken();                 //Must start an object with a {
        assertThisTokenType(tokenizer.tokenType(), TokenTypes.JSON_CURLY_BRACKET_LEFT);
        setElementData(tokenizer, ElementTypes.JSON_OBJECT_START);

        byte tokenType = tokenizer.tokenType();
        while(tokenType != TokenTypes.JSON_CURLY_BRACKET_RIGHT) {       //Interpret tokens one at a time

            tokenizer.nextToken();
            tokenizer.parseToken();
            tokenType = tokenizer.tokenType();
            if (tokenizer.peakColon()){             //If the token after this is a colon, it is a key
                setElementData(tokenizer, ElementTypes.JSON_PROPERTY_NAME);
                continue;
            } 
            switch(tokenType) {         //If not, it is a value
                case TokenTypes.JSON_STRING_TOKEN   : { setElementData(tokenizer, ElementTypes.JSON_PROPERTY_VALUE_STRING); } break;
                case TokenTypes.JSON_NUMBER_TOKEN   : { setElementData(tokenizer, ElementTypes.JSON_PROPERTY_VALUE_NUMBER); } break;
                case TokenTypes.JSON_SCIENTIFIC_TOKEN : {setElementData(tokenizer, ElementTypes.JSON_PROPERTY_VALUE_SCIENTIFIC); } break;
                case TokenTypes.JSON_BOOL_TOKEN   : { setElementData(tokenizer, ElementTypes.JSON_PROPERTY_VALUE_BOOLEAN); } break;
                case TokenTypes.JSON_NULL_TOKEN   : { setElementData(tokenizer, ElementTypes.JSON_PROPERTY_VALUE_NULL); } break;
                case TokenTypes.JSON_CURLY_BRACKET_LEFT  : { parseObject(tokenizer); } break;       //Call this method again to recursively parse the new object then return to this object after
                case TokenTypes.JSON_SQUARE_BRACKET_LEFT : { parseArray(tokenizer); } break;
            }
        }
        setElementData(tokenizer, ElementTypes.JSON_OBJECT_END);
    }

//    Array parse
    private void parseArray(JsonTokenizer tokenizer) {
        setElementData(tokenizer, ElementTypes.JSON_ARRAY_START);

        tokenizer.nextToken();
        tokenizer.parseToken();

        while(tokenizer.tokenType() != TokenTypes.JSON_SQUARE_BRACKET_RIGHT) {
//            Still in array
            byte tokenType = tokenizer.tokenType();
            switch(tokenType) {
                case TokenTypes.JSON_STRING_TOKEN   : { setElementData(tokenizer, ElementTypes.JSON_ARRAY_VALUE_STRING); } break;
                case TokenTypes.JSON_NUMBER_TOKEN   : { setElementData(tokenizer, ElementTypes.JSON_ARRAY_VALUE_NUMBER); } break;
                case TokenTypes.JSON_SCIENTIFIC_TOKEN : { setElementData(tokenizer, ElementTypes.JSON_PROPERTY_VALUE_SCIENTIFIC); } break;
                case TokenTypes.JSON_BOOL_TOKEN   : { setElementData(tokenizer, ElementTypes.JSON_ARRAY_VALUE_BOOLEAN); } break;
                case TokenTypes.JSON_NULL_TOKEN   : { setElementData(tokenizer, ElementTypes.JSON_ARRAY_VALUE_NULL); } break;
                case TokenTypes.JSON_CURLY_BRACKET_LEFT : { parseObject(tokenizer);} break;
            }
            tokenizer.nextToken();
            tokenizer.parseToken();
            tokenType = tokenizer.tokenType();
            if(tokenType == TokenTypes.JSON_COMMA) {
                tokenizer.nextToken();
                tokenizer.parseToken();
                tokenType = tokenizer.tokenType();
            }
        }
        setElementData(tokenizer, ElementTypes.JSON_ARRAY_END);
    }

//    set element
    private void setElementData(JsonTokenizer tokenizer, byte elementType) {
        this.elementBuffer.position[this.elementIndex] = tokenizer.tokenPosition();
        this.elementBuffer.length[this.elementIndex] = tokenizer.tokenLength();
        this.elementBuffer.type[this.elementIndex] = elementType;
        this.elementIndex++;
    }

//    token type assertion
    private final void assertThisTokenType(byte tokenType, byte expectedTokenType) {
        if(tokenType != expectedTokenType) {
            throw new ParserException("Token type mismatch: Expected " + expectedTokenType + " but found " + tokenType);
        }
    }

//    assert more tokens
    private void assertHasMoreTokens(JsonTokenizer tokenizer) {
        if(!tokenizer.hasMoreTokens()) {
            throw new ParserException("Expected more tokens available in the tokenizer");
        }
    }

}
