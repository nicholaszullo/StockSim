package homemadejson.test;

import homemadejson.output.*;
import homemadejson.parser.JsonParser;
import homemadejson.support.InputDataBuffer;
import homemadejson.support.TokenBuffer;

import java.io.IOException;

public class JsonTest {
    public static void main(String[] args) throws IOException {
        String fileName = "homemadejson/data/medium.json.txt";

        InputDataBuffer buffer = FileUtil.readFile(fileName);

        JsonTestObject jsonObject = runTestParser(buffer);

        System.out.println("Testing regular JSON file");
        System.out.println(jsonObject.stringArray[0]);
        System.out.println(jsonObject.key2);
        System.out.println(jsonObject.key3);


        System.out.println();
        System.out.println("Testing TD API JSON");
        fileName = "homemadejson/data/equity.json.txt";
        buffer = FileUtil.readFile(fileName);
        JsonObject jsonObject2 = runParser(buffer);
        System.out.println(jsonObject2.symbol);
        System.out.println(jsonObject2.description);
        System.out.println(jsonObject2.lastPrice);
        System.out.println(jsonObject2.totalVolume);
        System.out.println(jsonObject2.peRatio);
        System.out.println(jsonObject2.divAmount);
        System.out.println(jsonObject2.divYield);


    }

    private static JsonTestObject runTestParser(InputDataBuffer buffer) {
        TokenBuffer jsonTokens = new TokenBuffer(8192);
        TokenBuffer jsonElements = new TokenBuffer(8192);

        JsonParser jsonParser = new JsonParser(jsonTokens, jsonElements);
        jsonParser.parse(buffer);
        JsonTestObject jsonObject = JsonTestObjectBuilder.parseJsonObject(buffer, jsonElements);

        return jsonObject;
    }

    private static JsonObject runParser(InputDataBuffer buffer) {
        TokenBuffer jsonTokens = new TokenBuffer(8192);
        TokenBuffer jsonElements = new TokenBuffer(8192);

        JsonParser jsonParser = new JsonParser(jsonTokens, jsonElements);
        jsonParser.parse(buffer);
        JsonObject jsonObject = JsonObjectBuilder.parseJsonObject(buffer, jsonElements);

        return jsonObject;
    }
}
