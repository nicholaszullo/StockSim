import java.io.*;
import java.util.HashMap;

public class ThreadDriver {
	public volatile Double cash = null; // Shared variables
	public volatile HashMap<String, Position> positions = null; // Shared variables
	public DatabaseHandler database;	//Should only be used by threads to read data
	@SuppressWarnings("unchecked")
	public ThreadDriver(DatabaseHandler database) {
		this.database = database;
		if (cash == null) {
			try {
				ObjectInputStream data = new ObjectInputStream(new FileInputStream("session.dat"));
				cash = data.readDouble();
				positions = (HashMap<String,Position>) data.readObject();
				data.close();
			} catch (IOException | ClassNotFoundException e) {
				cash = Double.valueOf(10000);
				positions = new HashMap<String,Position>();
			} 
		}  
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				synchronized(this){		//Needed so if a buyer or seller is using the shared data wait for them to finish? is that what this accomplishes?
					try {
						ObjectOutputStream data = new ObjectOutputStream(new FileOutputStream("session.dat",false));
						System.out.println("end with\ncash " + cash);
						for (String s : positions.keySet()){
							System.out.println(positions.get(s));
						}
						data.writeDouble(cash);
						data.writeObject(positions);
						data.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}); 
		System.out.println("start with \ncash " + cash);
		for (String s : positions.keySet()){
			System.out.println(positions.get(s));
		}
	}

	public void changeCash(double val){
		cash = Double.valueOf(val);
	}
	public void addCash(double val){
		cash += val;
	}
	public void subCash(double val){
		cash -= val;
	}
	public Double getCash(){
		return cash;
	}
}
