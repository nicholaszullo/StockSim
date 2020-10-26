import java.io.*;
import java.util.HashMap;

public class ThreadDriver {
	public volatile Double cash = null; // Shared variables
	public volatile HashMap<String, Position> positions = null; // Shared variables
	@SuppressWarnings("unchecked")
	public ThreadDriver() {
		if (cash == null) {
			try {
				ObjectInputStream data = new ObjectInputStream(new FileInputStream("data.txt"));
				cash = data.readDouble();
				positions = (HashMap<String,Position>) data.readObject();
				data.close();
			} catch (IOException | ClassNotFoundException e) {
				cash = Double.valueOf(0);
				positions = new HashMap<String,Position>();
			} 
		}  
		System.out.println(positions);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					ObjectOutputStream data = new ObjectOutputStream(new FileOutputStream("data.txt",false));
					data.writeDouble(cash);
					data.writeObject(positions);
					data.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
