import java.time.LocalDateTime;
import java.util.ArrayList;

public class Buyer extends Thread {
	// private final int BUYERS = 1; // Change this based on number of buying
	// methods
	private ThreadDriver shared;

	public Buyer(ThreadDriver td) {
		shared = td;
	}

	public void run() {
		ArrayList<String> tickers = shared.database.selectData("sqlite_master", "name", "");
		for (String s : tickers) {
			new Thread(){
				public void run(){
					buyAlgorithm(s);
				}
			}.start();
			;
		}
	}

	private void buyAlgorithm(String ticker) {
		while (true) {
			double last15 = movingAverage(ticker, 15);
			double last25 = movingAverage(ticker, 25);
			double last50 = movingAverage(ticker, 50);
			if (last15 - last25 < .1 && last15-last50 < .1) {
			//	System.out.println("crossed at buyer " + ticker);
				synchronized (shared) {
					double currPrice = Double.parseDouble(shared.database.selectData(ticker, "price", "ORDER BY date DESC LIMIT 1").get(0));
					if (currPrice * 5 < shared.getCash()){
						System.out.println("buying " + ticker + " at " + currPrice);
						shared.subCash(5 * currPrice);
						if (shared.positions.containsKey(ticker)){
							shared.positions.get(ticker).shares += 5;
						} else {
							Position temp = new Position(ticker, currPrice, 5, LocalDateTime.now());
							shared.positions.put(ticker, temp);
						}
			//			System.out.println("new cashhh " + shared.getCash());
					}
				}
			}
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private double movingAverage(String ticker, int num){
		ArrayList<String> data = shared.database.selectData(ticker, "price", "ORDER BY date DESC");
		double sum = 0;
		for (int i = 0; i < num && i < data.size(); i++){
			sum += Double.parseDouble(data.get(i));
		}
		return sum/num;
	}
}