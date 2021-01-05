import java.util.HashMap;

import homemadejson.output.JsonObject;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Main driver of the simulation. Writes data to the database and starts threads
 */
public class StockSim {
	public static void main(String[] args) {
		Logger log = makeLogger();
		DatabaseHandler database = new DatabaseHandler("", "stocks.db");
		String[] tracked = { "AAPL", "NIO", "MSFT", "INTC", "TSLA", "ZM" };
		for (String s : tracked) {
			addNewTicker(s, database); // Add the table to the database, if it exists database handler knows to do nothing
			//Delete historical data. At some point will need to keep this for analysis but right now I'm not using it so don't keep it
			database.deleteData(s, "WHERE date NOT LIKE \"%" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))+"%\"");		//Remove data from days not today
		}
		APIHandler api = new APIHandler();
		ThreadDriver td = new ThreadDriver(database, log);
		for (String s : tracked) {
			new Thread() {
				public void run() {
					updateTicker(s, database, api, log);
				}
			}.start(); 
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
	public static void updateTicker(String ticker, DatabaseHandler database, APIHandler api, Logger log) {
		String[] data = new String[3];
		while (true) {
			data[0] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
			JsonObject holder = api.getTicker(ticker);
			if (holder.getStringValue("error") != null) {
				log.out(log.ERROR, "api calls exceeded!");
				try {
					Thread.sleep(20000);
					continue;			//Dont do operations on an error response
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} 
			HashMap<String, Object> actualMap = holder.getNestedMap(ticker, holder.getValues());
			if (actualMap == null){		//API sometimes returns empty? Catch that case
				continue;
			}
			data[1] = holder.getStringValue("lastPrice", actualMap);
			data[2] = holder.getStringValue("totalVolume", actualMap);
			database.insertRow(ticker, data);
			try {
				Thread.sleep(3200);	// Minimum time the API takes to update the data is 700
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}

	}

	/** Used to get around the logger not being final for the multithreading
	 * 
	 * @return a logger instance
	 */
	private static Logger makeLogger(){
		try {
			return new Logger(new PrintStream(new FileOutputStream("LOG.log", true)));
		} catch (Exception e){
			return new Logger();
		} 
	}
}
