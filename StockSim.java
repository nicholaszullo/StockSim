import homemadejson.output.*;

public class StockSim {
	public static void main(String[] args) {
		
		DatabaseHandler database = new DatabaseHandler("","test.db");
		APIHandler api = new APIHandler();
		ThreadDriver td = new ThreadDriver();
		Buyer buyer = new Buyer(td);
		Seller seller = new Seller(td);
		buyer.start();
		seller.start();
	}

}
