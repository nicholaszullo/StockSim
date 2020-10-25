package homemadejson.output;

import java.util.*;

import homemadejson.parser.JsonParser;
import homemadejson.support.InputDataBuffer;
import homemadejson.support.TokenBuffer;

/**
 * Representation of a JSON string stored in a hashmap. 
 * @author Shane Riley
 * @author Nick Zullo
 */
public class JsonObject {

    private HashMap<String, Object> values;
    private ArrayList<HashMap<String, Object>> objects;
    public JsonObject(){
        this("");
    }
//    Constructor (for use with json string)
    public JsonObject(String jsonString) {

        values = new HashMap<String, Object>();
        objects = new ArrayList<HashMap<String,Object>>();
        objects.add(values);

        InputDataBuffer buffer = new InputDataBuffer(jsonString.length());
        TokenBuffer jsonTokens = new TokenBuffer(jsonString.length());
        TokenBuffer jsonElements = new TokenBuffer(jsonString.length());

//        Map string to char array
        for (int i=0; i < jsonString.length(); i++) {
            buffer.data[i] = jsonString.charAt(i);
        }
        buffer.length = jsonString.length()-1;      //Set length of buffer
        JsonParser jsonParser = new JsonParser(jsonTokens, jsonElements);       //Fills the tokenizer
        jsonParser.parse(buffer);       //Fills the elements
        JsonObjectBuilder builder = new JsonObjectBuilder(buffer, jsonElements, this);      //Fills the hashmap
        
    }

    /************************************************************************
     *  Helper methods for accessing the JSON data without casting by user  *
     ************************************************************************/

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

    /** Get the main hashmap of this JsonObject, first element in objects array
     * 
     * @return the HashMap<String, Object> of this Json
     */
    public HashMap<String, Object> getValues() {
        return values;
    }

    /** The array of hashmaps, typically only contains 1 
     * 
     * @return ArrayList<HashMap<String,Object>>
     */
    public ArrayList<HashMap<String, Object>> getObjects() {
        return objects;
    }

    /** Use when you know the value will be a String, such as the name of a stock.
     * 
     * @param key The key to use in the Map
     * @param map The map holding the key:value
     * @return A string representation of the value stored at key, null if key does not exist in map
     */
    public String getStringValue(String key, HashMap<String, Object> map){
        if (!map.containsKey(key)){
            return null;
        }
        return String.valueOf(map.get(key));
    }
    public String getStringValue(String key){
        return getStringValue(key, values);
    }
    /** Use when you know the value will be a String either "True" or "False"
     *  Returns a boolean object to allow for null case
     * @param key The key to use in the map
     * @param map The map holding the key:value
     * @return True if true is stored, false if false is stored, 
     *          or null if key does not exist in map or the string was not a boolean
     */
    public Boolean getBooleanValue(String key, HashMap<String, Object> map){
        if (!map.containsKey(key)){
            return null;
        }
        if (getStringValue(key, map).equalsIgnoreCase("True")){
            return true;
        } else if (getStringValue(key, map).equalsIgnoreCase("False")){
            return false;
        }
        return null;
    }

    /** Use when you know the value will be a Integer.
     * 
     * @param key The key to use in the Map
     * @param map The map holding the key:value
     * @return The Integer stored at key, null if key does not exist in map
     */
    public Integer getIntValue(String key, HashMap<String, Object> map){
        if (!map.containsKey(key)){
            return null;
        }
        return Integer.parseInt(getStringValue(key, map));
    }

    /** Use when you know the value will be a Double, such as stock price
     * 
     * @param key The key to use in the Map
     * @param map The map holding the key:value
     * @return The Double stored at key, null if key does not exist in map
     */
    public Double getDoubleValue(String key, HashMap<String, Object> map){
        if (!map.containsKey(key)){
            return null;
        }
        return Double.parseDouble(getStringValue(key, map));
    }

    /** Use when you know the value will be an ArrayList<String>
     * 
     * @param key The key to use in the Map
     * @param map The map holding the key:value
     * @return The arraylist stored at key, or null if the key does not exist in map or if the array is empty
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> getStringArray(String key, HashMap<String,Object> map){
        if (!map.containsKey(key)){
            return null;
        }
        ArrayList<String> temp = (ArrayList<String>)map.get(key);
        if (temp.size() > 0 && temp.get(0) instanceof String){
            return temp;
        }
        return null;        //Note that an empty array is used instead of null in some JSON representations. 
    }    
    
    /** Use when you know the value will be an ArrayList<Boolean>
     * 
     * @param key The key to use in the Map
     * @param map The map holding the key:value
     * @return The arraylist stored at key, or null if the key does not exist in map or if the array is empty
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Boolean> getBooleanArray(String key, HashMap<String,Object> map){
        if (!map.containsKey(key)){
            return null;
        }
        ArrayList<Boolean> temp = (ArrayList<Boolean>)map.get(key);
        if (temp.size() > 0 && temp.get(0) instanceof Boolean){
            return temp;
        }
        return null;        //Note that an empty array is used instead of null in some JSON representations. 
    }

    /** Use when you know the value will be an ArrayList<Double>
     * 
     * @param key The key to use in the Map
     * @param map The map holding the key:value
     * @return The arraylist stored at key, or null if the key does not exist in map or if the array is empty
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Double> getDoubleArray(String key, HashMap<String,Object> map){
        if (!map.containsKey(key)){
            return null;
        }
        ArrayList<Double> temp = (ArrayList<Double>)map.get(key);
        if (temp.size() > 0 && temp.get(0) instanceof Double){
            return temp;
        }
        return null;        //Note that an empty array is used instead of null in some JSON representations. 
    }

    /** Use when you know the value will be an ArrayList<Integer>
     * 
     * @param key The key to use in the Map
     * @param map The map holding the key:value
     * @return The arraylist stored at key, or null if the key does not exist in map or if the array is empty
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Integer> getIntegerArray(String key, HashMap<String,Object> map){
        if (!map.containsKey(key)){
            return null;
        }
        ArrayList<Integer> temp = (ArrayList<Integer>)map.get(key);
        if (temp.size() > 0 && temp.get(0) instanceof Integer){
            return temp;
        }
        return null;        //Note that an empty array is used instead of null in some JSON representations. 
    }
    
}
