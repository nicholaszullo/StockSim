package homemadejson.output;

import homemadejson.parser.JsonParser;
import homemadejson.support.InputDataBuffer;
import homemadejson.support.TokenBuffer;

/**
 * Output object for StockSim Project
 * Shane Riley
 */

public class JsonObject {

//    Public attributes to be grabbed in JsonObjectBuilder and then accessed from this object
//    lastPrice, symbol, description, totalVolume, peRatio, divAmount, divYield, etc.

    public String symbol;
    public String description;
    public double lastPrice;
    public double totalVolume;
    public double peRatio;
    public double divAmount;
    public double divYield;

//    Constructor (for use with json string)
    public JsonObject(String jsonString) {

        InputDataBuffer buffer = new InputDataBuffer(jsonString.length());
        TokenBuffer jsonTokens = new TokenBuffer(8192);
        TokenBuffer jsonElements = new TokenBuffer(8192);

//        Map string to char array
        for (int i=0; i < jsonString.length(); i++) {
            buffer.data[i] = jsonString.charAt(i);
        }

        JsonParser jsonParser = new JsonParser(jsonTokens, jsonElements);
        jsonParser.parse(buffer);
        JsonObject jsonObject = JsonObjectBuilder.parseJsonObject(buffer, jsonElements);

    }

}
