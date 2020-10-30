import java.util.HashMap;

import homemadejson.output.JsonObject;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Main driver of the simulation. Writes data to the database and starts threads
 * Note: when 120 API calls per minute is exceeded while running it will error
 * with a null pointer because there's no nested map in {"error": "api calls
 * exceed"} Add sleep 30s in this case? Thread.sleep
 */
public class StockSim {
	public static void main(String[] args) {

		DatabaseHandler database = new DatabaseHandler("", "stocks.db");
		String[] tracked = { "AAPL", "MSFT", "INTC", "TSLA", "ZM" };
		for (String s : tracked) {
			addNewTicker(s, database); // Add the table to te database, if it exists database handler knows to do nothing
			database.deleteData(s, "WHERE date LIKE \"%" + LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))+"%\"");		//Remove previous day's data to keep size of db low
		}
		APIHandler api = new APIHandler();
		ThreadDriver td = new ThreadDriver(database);
		for (String s : tracked) {
			new Thread() {
				public void run() {
					updateTicker(s, database, api);
				}
			}.start();
			; 
		}
		Buyer buyer = new Buyer(td);
		Seller seller = new Seller(td);
		buyer.start();
		seller.start();
	}

	public static void addNewTicker(String ticker, DatabaseHandler database) {
		String[] columns = new String[3];
		columns[0] = "date TEXT";
		columns[1] = "price REAL";
		columns[2] = "volume INTEGER";

		database.createTable(ticker, columns);
	}

	// In production, make this while true and add a sleep to prevent exceeding limit
	// Need 2.5 seconds between calls to api for updating 5 stocks under 120 per minute 
	public static void updateTicker(String ticker, DatabaseHandler database, APIHandler api) {
		String[] data = new String[3];
		while (true) {
			data[0] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
			JsonObject holder = api.getTicker(ticker);
			HashMap<String, Object> actualMap = holder.getNestedMap(ticker, holder.getValues());
			data[1] = holder.getStringValue("lastPrice", actualMap);
			data[2] = holder.getStringValue("totalVolume", actualMap);
			database.insertRow(ticker, data);
			try {
				Thread.sleep(3000);	// Minimum time the API takes to update the data is 700
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}

	}
}
