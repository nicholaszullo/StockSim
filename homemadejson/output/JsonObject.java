package homemadejson.output;

import java.util.*;

import homemadejson.parser.JsonParser;
import homemadejson.support.InputDataBuffer;
import homemadejson.support.TokenBuffer;

/**
 * Output object for StockSim Project
 * Shane Riley
 */

public class JsonObject {

    private HashMap<String, Object> values;
    private JsonObject jsonObject;
    public JsonObject(){
        this("");
    }
//    Constructor (for use with json string)
    public JsonObject(String jsonString) {

        values = new HashMap<String, Object>();

        InputDataBuffer buffer = new InputDataBuffer(jsonString.length());
        TokenBuffer jsonTokens = new TokenBuffer(8192);
        TokenBuffer jsonElements = new TokenBuffer(8192);

//        Map string to char array
        for (int i=0; i < jsonString.length(); i++) {
            buffer.data[i] = jsonString.charAt(i);
        }
        buffer.length = jsonString.length()-1;
        JsonParser jsonParser = new JsonParser(jsonTokens, jsonElements);
        jsonParser.parse(buffer);
        JsonObjectBuilder builder = new JsonObjectBuilder(buffer, jsonElements, this);
        
    }

    /** Use on a HashMap that contains another HashMap as a value to receive the nested HashMap 
     * 
     * @param key the key the HashMap is mapped to
     * @param map the HashMap to use the key with
     * @return a HashMap<String,Object>, or null if no hashmap existed at key in map
     */
    @SuppressWarnings("unchecked")
    public HashMap<String,Object> getNestedMap(String key, HashMap<String,Object> map){
        HashMap<String, Object> nested = (HashMap<String,Object>)map.get(key);
        if (nested instanceof HashMap<?,?>){
            return nested;
        }
        return null;
    }

    public HashMap<String, Object> getValues() {
        return values;
    }

}
