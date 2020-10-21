package homemadejson.output;

import homemadejson.parser.ElementTypes;
import homemadejson.parser.JsonNavigator;
import homemadejson.support.InputDataBuffer;
import homemadejson.support.TokenBuffer;

/**
 * Build output objects for StockSim
 * Uses buffer of elements from parser to build JsonObject tailored to the needs of the StockSim project
 * Shane Riley
 */

public class JsonObjectBuilder {
    public static JsonObject parseJsonObject(InputDataBuffer buffer, TokenBuffer elementBuffer) {
        JsonNavigator jsonNavigator = new JsonNavigator(buffer, elementBuffer);
        return parseJsonObject(jsonNavigator);
    }

    public static JsonObject parseJsonObject(JsonNavigator jsonNavigator) {
        JsonObject jsonObject = new JsonObject();

        while (jsonNavigator.type() != ElementTypes.JSON_OBJECT_END) {
            jsonNavigator.next();

//            Add more entries as needed to pull more from JSON file
            if (jsonNavigator.isEqualUnencoded("symbol")) {
                jsonNavigator.next();
                jsonObject.symbol = jsonNavigator.asString();
            } else if (jsonNavigator.isEqualUnencoded("description")) {
                jsonNavigator.next();
                jsonObject.description = jsonNavigator.asString();
            } else if (jsonNavigator.isEqualUnencoded("lastPrice")) {
                jsonNavigator.next();
                jsonObject.lastPrice = jsonNavigator.asDouble();
            } else if (jsonNavigator.isEqualUnencoded("totalVolume")) {
                jsonNavigator.next();
                jsonObject.totalVolume = jsonNavigator.asDouble();
            } else if (jsonNavigator.isEqualUnencoded("peRatio")) {
                jsonNavigator.next();
                jsonObject.peRatio = jsonNavigator.asDouble();
            } else if (jsonNavigator.isEqualUnencoded("divAmount")) {
                jsonNavigator.next();
                jsonObject.divAmount = jsonNavigator.asDouble();
            } else if (jsonNavigator.isEqualUnencoded("divYield")) {
                jsonNavigator.next();
                jsonObject.divYield = jsonNavigator.asDouble();
            }
        }

        jsonNavigator.next();
        return jsonObject;
    }
}
