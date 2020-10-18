package homemadejson.test;

import homemadejson.output.FileUtil;
import homemadejson.output.JsonObject;
import homemadejson.output.JsonObjectBuilder;
import homemadejson.parser.JsonParser;
import homemadejson.support.InputDataBuffer;
import homemadejson.support.TokenBuffer;

import java.io.IOException;

public class JsonTest {
    public static void main(String[] args) throws IOException {
        String fileName = "homemadejson/data/medium.json.txt";

        InputDataBuffer buffer = FileUtil.readFile(fileName);

        JsonObject jsonObject = runParser(buffer);

        System.out.println(jsonObject.stringArray[0]);
        System.out.println(jsonObject.key2);
        System.out.println(jsonObject.key3);

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
