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
				ObjectInputStream data = new ObjectInputStream(new FileInputStream("session.txt"));
				cash = data.readDouble();
				positions = (HashMap<String,Position>) data.readObject();
				data.close();
			} catch (IOException | ClassNotFoundException e) {
				cash = Double.valueOf(0);
				positions = new HashMap<String,Position>();
			} 
		}  
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					ObjectOutputStream data = new ObjectOutputStream(new FileOutputStream("session.txt",false));
					data.writeDouble(cash);
					data.writeObject(positions);
					data.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}); 
	}

	public void changeCash(double val){
		cash = Double.valueOf(val);
	}
	public Double getCash(){
		return cash;
	}
}
