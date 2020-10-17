package homemadejson.output;

import homemadejson.parser.ElementTypes;
import homemadejson.parser.JsonNavigator;
import homemadejson.support.InputDataBuffer;
import homemadejson.support.TokenBuffer;

/**
 * Build output objects
 * Shane Riley
 */

public class JsonObjectBuilder {

    public static JsonObject parseJsonObject(InputDataBuffer buffer, TokenBuffer elementBuffer) {
        JsonNavigator jsonNavigator = new JsonNavigator(buffer, elementBuffer);
        return parseJsonObject(jsonNavigator);
    }

    public static JsonObject parseJsonObject(JsonNavigator jsonNavigator) {
        JsonObject jsonObject = new JsonObject();

        jsonNavigator.next();

        while (jsonNavigator.type() != ElementTypes.JSON_OBJECT_END) {
            jsonNavigator.next();

            if (jsonNavigator.isEqualUnencoded("key")) {
                jsonObject.key = jsonNavigator.asString();
                jsonNavigator.next();
            } else if (jsonNavigator.isEqualUnencoded("key2")) {
                jsonObject.key2 = jsonNavigator.asInt();
                jsonNavigator.next();
            } else if (jsonNavigator.isEqualUnencoded("key3")) {
                jsonObject.key3 = jsonNavigator.asBoolean();
                jsonNavigator.next();
            } else if (jsonNavigator.isEqualUnencoded("stringArray")) {
                jsonNavigator.next();
                String[] strings = new String[jsonNavigator.countPrimitiveArrayElements()];
                for (int i = 0, n = strings.length; i < n; i++) {
                    strings[i] = jsonNavigator.asString();
                    jsonNavigator.next();
                }
                jsonObject.stringArray = strings;
                jsonNavigator.next();
            } else if (jsonNavigator.isEqualUnencoded("numberArray")) {
                jsonNavigator.next();
                int[] ints = new int[jsonNavigator.countPrimitiveArrayElements()];
                for (int i = 0, n = ints.length; i < n; i++) {
                    ints[i] = jsonNavigator.asInt();
                    jsonNavigator.next();
                }
            } else if (jsonNavigator.isEqualUnencoded("booleanArray")) {
                jsonNavigator.next();
                boolean[] booleans = new boolean[jsonNavigator.countPrimitiveArrayElements()];

                for (int i = 0, n = booleans.length; i < n; i++) {
                    booleans[i] = jsonNavigator.asBoolean();
                    jsonNavigator.next();
                }
                jsonObject.booleanArray = booleans;
                jsonNavigator.next();
            } else if (jsonNavigator.isEqualUnencoded("sub")) {
                jsonObject.sub = parseJsonObject(jsonNavigator);
            }
        }

        jsonNavigator.next();
        return jsonObject;
    }
}
