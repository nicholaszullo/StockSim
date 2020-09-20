import java.util.*;

public class JSON {
	private Map<String,String> converted;

	public JSON(){
		this("");
	}

	/**	currently only works for 1 layer. throws out [AAPL] of quote response and resets keymap  
	 * 
	 * @param s a JSON formatted string
	 */
	public JSON(String s) {
		converted = new HashMap<String,String>();
		for (int i = 0; i < s.length(); i++){
			StringBuilder currKey = new StringBuilder();
			if (s.charAt(i) == '\"' && s.charAt(i+1) != ' '){
				while (s.charAt(++i) != '\"'){				//Key will be a string, write letters until ending quote
					currKey.append(s.charAt(i));
				}
				i = s.indexOf(':', i) + 1;
				if (s.charAt(i) == '{')				//Fix this case?
					continue;
				else {
					for (; s.charAt(i) != '\"' && (s.charAt(i) > '9' || s.charAt(i) < '0') && (s.charAt(i) > 'z' || s.charAt(i) < 'a'); i++);		//Stop on a " , a number, or a letter
				}
				StringBuilder currVal = new StringBuilder();
				if (s.charAt(i) == '\"')					//If on a " move to next char
					i++;
				while (s.charAt(i) != '\"' && s.charAt(i) != ',' && s.charAt(i) != '}'){		//End of value is either a quote for a string or a comma, stop at either
					currVal.append(s.charAt(i));
					i++;
				}
				converted.put(currKey.toString(), currVal.toString());		//Add key value to map
			}
			
		}
		if (converted.containsKey("error"))
			System.out.println("WARNING! REQUEST FAILED");
	}

	/**	Get a value from the JSON
	 * 
	 * @param key the key to retrieve the value with
	 * @return the value if the key is valid or null if key is invalid
	 */
	public String getValue(String key){
		if (converted.containsKey(key))
			return converted.get(key);
		return null;
	}

	/**	Get the keySet of the map 
	 * 
	 * @return the set of all keys in the JSON
	 */
	public Set<String> keySet(){
		return converted.keySet();
	}
	/**	Get the number of entries in the JSON
	 * 
	 * @return the size of the keymap
	 */
	public int size(){
		return converted.size();
	}

	/**	Find if an info header is contained in the JSON
	 * 
	 * @param key the header to find
	 * @return true if the header is contained, false if not
	 */
	public boolean containsKey(String key){
		return converted.containsKey(key);
	}

	/**	Find if a given value is in the JSON
	 * 
	 * @param value the value to find
	 * @return true if the value is contained, false if not
	 */
	public boolean containsValue(String value){
		return converted.containsValue(value);
	}

	/**	Get the string representation of the JSON
	 * 
	 * @return the string representation of the JSON
	 */
	public String toString(){
		return converted.toString();
	}
	


}
