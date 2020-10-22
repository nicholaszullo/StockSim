package homemadejson.output;

import java.util.ArrayList;
import java.util.HashMap;

import homemadejson.parser.ElementTypes;
import homemadejson.parser.JsonNavigator;
import homemadejson.support.InputDataBuffer;
import homemadejson.support.ParserException;
import homemadejson.support.TokenBuffer;

/**
 * Build output objects for StockSim
 * Uses buffer of elements from parser to build JsonObject tailored to the needs of the StockSim project
 * Shane Riley
 */

public class JsonObjectBuilder {

    public JsonObjectBuilder(InputDataBuffer buffer, TokenBuffer elementBuffer, JsonObject jsonObject){
        parseJsonObject(buffer, elementBuffer, jsonObject.getValues());

    }

    public void parseJsonObject(InputDataBuffer buffer, TokenBuffer elementBuffer, HashMap<String, Object> map) {
        JsonNavigator jsonNavigator = new JsonNavigator(buffer, elementBuffer);
        parseJsonObject(jsonNavigator, map);
    }

    public void parseJsonObject(JsonNavigator jsonNavigator, HashMap<String,Object> curr) {
        while (jsonNavigator.type() != ElementTypes.JSON_OBJECT_END){ 
            System.out.println(jsonNavigator.type());
            if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_NAME){       //If at a key, assign the value
                String key = jsonNavigator.asString();
                System.out.println("THERES ONLY 1 OBJECT!!" + " " + key);
                System.out.println(jsonNavigator.type() + "  " +jsonNavigator.asString());
                System.out.println(jsonNavigator.type());
                if (jsonNavigator.type() == ElementTypes.JSON_OBJECT_START){
                    HashMap<String,Object> nested = new HashMap<String,Object>();
                    curr.put(key, nested);
                    jsonNavigator.next();               //Move to next element for new call
                    parseJsonObject(jsonNavigator, nested);
                } else if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_START) {
                    jsonNavigator.next();
                    if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_VALUE_BOOLEAN){
                        ArrayList<Boolean> nested = new ArrayList<Boolean>();
                        curr.put(key, nested);
                        parseJsonBooleanArray(jsonNavigator, nested);
                    } else if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_VALUE_STRING){
                        ArrayList<String> nested = new ArrayList<String>();
                        curr.put(key, nested);
                        parseJsonStringArray(jsonNavigator, nested);
                    } else if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_VALUE_NUMBER){
                        ArrayList<Double> nested = new ArrayList<Double>();
                        curr.put(key, nested);
                        parseJsonNumberArray(jsonNavigator, nested);
                    } else if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_VALUE_NULL){
                        curr.put(key, null);
                    } 
                } else if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_VALUE_STRING){
                    curr.put(key, jsonNavigator.asString());
                    System.out.println(curr.get(key));
                } else if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_VALUE_BOOLEAN){
                    curr.put(key, jsonNavigator.asBoolean());
                } else if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_VALUE_NUMBER){
                    if (jsonNavigator.isInt()){
                        curr.put(key, jsonNavigator.asInt());
                    } else {
                        curr.put(key, jsonNavigator.asDouble());
                    }
                } else if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_VALUE_NULL){
                    curr.put(key, null);
                    jsonNavigator.next();
                }  else {            //Key without a value
                    throw new ParserException("Key without a value!");
                }
            } else if (jsonNavigator.type() == ElementTypes.JSON_OBJECT_END) {
                return ;
            } else if (jsonNavigator.type() == ElementTypes.JSON_OBJECT_START){
                jsonNavigator.next();
            } else {
                throw new ParserException("Malformed Parse!");
            }

        }
    
    }

    private void parseJsonNumberArray(JsonNavigator jsonNavigator, ArrayList<Double> nested) {
        while (jsonNavigator.type() != ElementTypes.JSON_ARRAY_END){
            if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_VALUE_NUMBER){
                if (jsonNavigator.isInt()){
                    nested.add(jsonNavigator.asInt().doubleValue());
                } else {
                    nested.add(jsonNavigator.asDouble());
                }
            }
        }
    }

    private void parseJsonStringArray(JsonNavigator jsonNavigator, ArrayList<String> nested) {
        while (jsonNavigator.type() != ElementTypes.JSON_ARRAY_END){
            if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_VALUE_STRING){
                nested.add(jsonNavigator.asString());
            }
        }
    }

    private void parseJsonBooleanArray(JsonNavigator jsonNavigator, ArrayList<Boolean> nested) {
        while (jsonNavigator.type() != ElementTypes.JSON_ARRAY_END){
            if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_VALUE_BOOLEAN){
                nested.add(jsonNavigator.asBoolean());
            }
        }
    }

}
