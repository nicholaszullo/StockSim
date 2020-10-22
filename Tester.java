import java.util.HashMap;

import homemadejson.output.*;

public class Tester {
	public static void main(String[] args) {
		String test = "{\"symbol\": \"Symbol\",\"description\": \"Description\",\"bidPrice\": 0,\"bidSize\": 0,\"bidId\": \"string\",\"lastPrice\": 1.2,\"lastSize\": 0,\"lastId\": \"string\",\"openPrice\": 0,\"highPrice\": 0,\"lowPrice\": 0,\"closePrice\": 0, \"sub\"  :{ \"key\"  : \"value\",\"key2\" : 12345,\"key3\" : false}}";
		String medium =" { \"key\"   : \"value\",\"key2\"  : 12345,\"key3\"  : false,\"sub\"  : { \"key\"  : \"value\",\"key2\" : 12345,\"key3\" : false,\"stringArray\" : [ \"one\", \"two\", \"three\", \"four\", \"five\", \"six\", \"seven\", \"eight\", \"nine\", \"ten\"],\"numberArray\" : [ 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345],\"booleanArray\" : [ true, false, true, false, true, false, true, false, true, false]}\"stringArray\" : [ \"one\", \"two\", \"three\", \"four\", \"five\", \"six\", \"seven\", \"eight\", \"nine\", \"ten\"],\"numberArray\" : [ 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345],\"booleanArray\" : [ true, false, true, false, true, false, true, false, true, false],}";
		JsonObject tester = new JsonObject(test);
		System.out.println("\n\n\n");
		for (String s : tester.getValues().keySet()){
			if (s.equals("sub")){
				HashMap<String, Object> nested = tester.getNestedMap("sub", tester.getValues());
				for (String ss : nested.keySet()){
					System.out.println(ss + " " + nested.get(ss));
				}
			}
			System.out.println(s + " " + tester.getValues().get(s));
		}
	}
}
