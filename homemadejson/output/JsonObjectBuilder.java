package homemadejson.output;

import java.util.ArrayList;
import java.util.HashMap;

import homemadejson.parser.ElementTypes;
import homemadejson.parser.JsonNavigator;
import homemadejson.support.InputDataBuffer;
import homemadejson.support.ParserException;
import homemadejson.support.TokenBuffer;

/**
 * Fills the hashmap of the JsonObject using the JsonNavigator. 
 * The hashmap supports nested JSON as well as arrays 
 * @author Shane Riley
 * @author Nick Zullo
 */

public class JsonObjectBuilder {

    public JsonObjectBuilder(InputDataBuffer buffer, TokenBuffer elementBuffer, JsonObject jsonObject){
        parseJsonObject(buffer, elementBuffer, jsonObject.getObjects());

    }

    public void parseJsonObject(InputDataBuffer buffer, TokenBuffer elementBuffer, ArrayList<HashMap<String, Object>> objects) {
        JsonNavigator jsonNavigator = new JsonNavigator(buffer, elementBuffer);
        if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_START){
            parseJsonObjectArray(jsonNavigator, objects);
        } else if (jsonNavigator.type() == ElementTypes.JSON_OBJECT_START){
            parseJsonObject(jsonNavigator, objects.get(0));
        } else {
            throw new ParserException("Invalid Start to elements!");
        }
        
    }

    public void parseJsonObject(JsonNavigator jsonNavigator, HashMap<String,Object> curr) {
        while (jsonNavigator.type() != ElementTypes.JSON_OBJECT_END){ 
            if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_NAME){       //If at a key, assign the value
                String key = jsonNavigator.asString();
                if (jsonNavigator.type() == ElementTypes.JSON_OBJECT_START){    //If new object, create a new hashmap and store it in the current hashmap. Call the new object with the new hashmap
                    HashMap<String,Object> nested = new HashMap<String,Object>();
                    curr.put(key, nested);
                    jsonNavigator.next();               //Move to next element for new call
                    parseJsonObject(jsonNavigator, nested);
                } else if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_START) { //New if else ladder for parsing an array
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
                    } //End array ladder
                } else if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_VALUE_STRING){
                    curr.put(key, jsonNavigator.asString());
                } else if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_VALUE_BOOLEAN){
                    curr.put(key, jsonNavigator.asBoolean());
                } else if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_VALUE_NUMBER){
                    if (jsonNavigator.isInt()){
                        curr.put(key, jsonNavigator.asInt());
                    } else {
                        curr.put(key, jsonNavigator.asDouble());
                    }
                } else if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_VALUE_SCIENTIFIC){
                    curr.put(key, jsonNavigator.parseSci());
                    jsonNavigator.next();
                } else if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_VALUE_NULL){
                    curr.put(key, null);
                    jsonNavigator.next();
                }  else {
                    throw new ParserException("Key without a value!" + jsonNavigator.type());
                }   //End key ladder
            } else if (jsonNavigator.type() == ElementTypes.JSON_OBJECT_END) {  //If not at a key, must either be at the start or the end
                return ;
            } else if (jsonNavigator.type() == ElementTypes.JSON_OBJECT_START){
                jsonNavigator.next();
            } else {    //Else the parser is wrong and debugging needed!
                throw new ParserException("Malformed Parse!" + " " + jsonNavigator.type());
            }   //End main ladder

        }
    
    }

    private void parseJsonObjectArray(JsonNavigator jsonNavigator, ArrayList<HashMap<String,Object>> objects){
        while (jsonNavigator.type() != ElementTypes.JSON_ARRAY_END){
            if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_START){
                jsonNavigator.next();
            } else if (jsonNavigator.type() == ElementTypes.JSON_OBJECT_START){
                HashMap<String,Object> new_object = new HashMap<String, Object>();
                objects.add(new_object);
                parseJsonObject(jsonNavigator, new_object);
            } else if (jsonNavigator.type() == ElementTypes.JSON_OBJECT_END){
                jsonNavigator.next();
            }
        }
        jsonNavigator.next();
    }

    private void parseJsonNumberArray(JsonNavigator jsonNavigator, ArrayList<Double> nested) {
        while (jsonNavigator.type() != ElementTypes.JSON_ARRAY_END){
            if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_VALUE_NUMBER){
                if (jsonNavigator.isInt()){
                    nested.add(jsonNavigator.asInt().doubleValue());
                } else {
                    nested.add(jsonNavigator.asDouble());
                }
            } else if (jsonNavigator.type() == ElementTypes.JSON_PROPERTY_VALUE_SCIENTIFIC){
                nested.add(jsonNavigator.parseSci());
            }
        }
        jsonNavigator.next();
    }

    private void parseJsonStringArray(JsonNavigator jsonNavigator, ArrayList<String> nested) {
        while (jsonNavigator.type() != ElementTypes.JSON_ARRAY_END){
            if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_VALUE_STRING){
                nested.add(jsonNavigator.asString());
            }
        }
        jsonNavigator.next();
    }

    private void parseJsonBooleanArray(JsonNavigator jsonNavigator, ArrayList<Boolean> nested) {
        while (jsonNavigator.type() != ElementTypes.JSON_ARRAY_END){
            if (jsonNavigator.type() == ElementTypes.JSON_ARRAY_VALUE_BOOLEAN){
                nested.add(jsonNavigator.asBoolean());
            }
        }
        jsonNavigator.next();
    }

}
