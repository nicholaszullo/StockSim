import java.util.ArrayList;

public class ThreadDriver {
	public DatabaseHandler database;	//Should only be used by threads to read data
	public ThreadDriver(DatabaseHandler database) {
		this.database = database;
		database.createTable("cash", new String[] {"cash REAL"});
		database.createTable("Positions", new String[] {"id INTEGER UNIQUE", "ticker TEXT", "shares INTEGER","price REAL", "date TEXT"});
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("end with \ncash " + getCash());
				for (String id : database.selectData("Positions", "id", "")){
					System.out.print(database.selectData("Positions", "ticker", "WHERE id="+id)+ " ");
					System.out.print("shares: " + database.selectData("Positions", "shares", "WHERE id="+id) + " ");
					System.out.print("price: " + database.selectData("Positions", "price", "WHERE id="+id) + " ");
					System.out.print("date: " + database.selectData("Positions", "date", "WHERE id="+id) + " ");
				}
			}
		}); 
		System.out.println("start with \ncash " + getCash());
		for (String id : database.selectData("Positions", "id", "")){
			System.out.print(database.selectData("Positions", "ticker", "WHERE id="+id)+ " ");
			System.out.print("shares: " + database.selectData("Positions", "shares", "WHERE id="+id) + " ");
			System.out.print("price: " + database.selectData("Positions", "price", "WHERE id="+id) + " ");
			System.out.print("date: " + database.selectData("Positions", "date", "WHERE id="+id) + " ");
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
		return database.selectData("Positions", "ticker", "WHERE ticker="+ticker).size() != 0;
	}
	public int nextID(){
		return Integer.parseInt(database.selectData("Positions", "id", "ORDER BY DESC LIMIT 1").get(0))+1;
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
