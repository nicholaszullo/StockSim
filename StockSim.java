import homemadejson.output.*;

public class StockSim extends ThreadDriver{
	public static void main(String[] args) {
		
		DatabaseHandler database = new DatabaseHandler("","test.db");
		APIHandler api = new APIHandler();
		JsonObject aapl = api.getTicker("AAPL");
		System.out.println("Last price of aapl: " + aapl.getStringValue("lastPrice",aapl.getNestedMap("AAPL", aapl.getValues())));
	}

}
