import homemadejson.output.*;

public class Tester {
	public static void main(String[] args) {
		JsonObject tester = new JsonObject("{\"symbol\": \"Symbol\",\"description\": \"Description\",\"bidPrice\": 0,\"bidSize\": 0,\"bidId\": \"string\"}");
		System.out.println("\n\n\n");
		for (String s : tester.getValues().keySet()){
			System.out.println(s + " " + tester.getValues().get(s));
		}
	}
}
