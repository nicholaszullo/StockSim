import java.util.ArrayList;

public class ThreadDriver {
	public DatabaseHandler database;	//Should only be used by threads to read data\
	public Logger log;		//Used to output messages to a logger
	public ThreadDriver(DatabaseHandler database, Logger log) {
		this.database = database;
		this.log = log;
		database.createTable("cash", new String[] {"cash REAL"});
		database.createTable("Positions", new String[] {"id INTEGER", "ticker TEXT", "shares INTEGER","price REAL", "date TEXT"});
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.out(log.INFO, "end with \ncash " + getCash());
				for (String id : database.selectData("Positions", "id", "")){
					log.out(log.INFO, database.selectData("Positions", "ticker", "WHERE id="+id)+ " ");
					log.out(log.INFO, "shares: " + database.selectData("Positions", "shares", "WHERE id="+id) + " ");
					log.out(log.INFO, "price: " + database.selectData("Positions", "price", "WHERE id="+id) + " ");
					log.out(log.INFO, "date: " + database.selectData("Positions", "date", "WHERE id="+id) + "\n");
				}
			}
		}); 
		try {
			log.out(log.INFO, "start with \ncash " + getCash());
			for (String id : database.selectData("Positions", "id", "")){
				log.out(log.INFO, database.selectData("Positions", "ticker", "WHERE id="+id)+ " ");
					log.out(log.INFO, "shares: " + database.selectData("Positions", "shares", "WHERE id="+id) + " ");
					log.out(log.INFO, "price: " + database.selectData("Positions", "price", "WHERE id="+id) + " ");
					log.out(log.INFO, "date: " + database.selectData("Positions", "date", "WHERE id="+id) + "\n");
			}
		} catch (Exception e){
			changeCash(10000.0);
			log.out(log.INFO, "start with \ncash " + getCash());
		}
	}

	public void changeCash(double val){
		database.deleteData("cash", "");
		database.insertRow("cash", new String[] { String.valueOf(val) });
	}
	public void addCash(double val){
		changeCash(val + Double.parseDouble(getCash()));
	}
	public void subCash(double val){
		changeCash(Double.parseDouble(getCash()) - val);
	}
	public String getCash(){
		return database.selectData("cash", "cash", "").get(0);
	}
	public boolean ownTicker(String ticker){
		return !(database.selectData("Positions", "ticker", "WHERE ticker=\""+ ticker +"\"").size() == 0);
	}
	public int nextID(){
		if (database.selectData("Positions", "id", "ORDER BY date DESC LIMIT 1") == null)
			return 1;
		if (database.selectData("Positions", "id", "ORDER BY date DESC LIMIT 1").size() == 0)
			return 1;
		return Integer.parseInt(database.selectData("Positions", "id", "ORDER BY date DESC LIMIT 1").get(0))+1;
	}
	public int numberShares(String ticker){
		ArrayList<String> shares = database.selectData("Positions", "shares", "WHERE ticker=\""+ ticker +"\"");
		int num = 0;
		for (String s : shares){
			num += Integer.parseInt(s);
		}
		return num;
	}
}
