import java.util.ArrayList;

public class Seller extends Thread {
	// private final int SELLERS = 1; // Change this based on number of selling
	// methods
	private ThreadDriver shared;

	public Seller(ThreadDriver td) {
		shared = td;
	}

	public void run() {
		ArrayList<String> tickers = shared.database.selectData("sqlite_master", "name", "");
		for (String s : tickers) {
			new Thread(){
				public void run(){
					sellAlgorithm(s);
				}
			}.start();
			;
		}
	}

	private void sellAlgorithm(String ticker) {
		while (true) {
			double last15 = movingAverage(ticker, 15);
			double last25 = movingAverage(ticker, 25);
			if (last15 - last25 < .1) {
			//	System.out.println("crossed at seller " + ticker);
				synchronized (shared) {
					double currPrice = Double.parseDouble(
							shared.database.selectData(ticker, "price", "ORDER BY date DESC LIMIT 1").get(0));
					if (shared.positions.containsKey(ticker)) {
						System.out.println("selling " + ticker + " at " + currPrice);
						shared.addCash(shared.positions.get(ticker).shares * currPrice);
						shared.positions.remove(ticker);
			//			System.out.println("new cash " + shared.getCash());
					}
				}
			}
			try {
				Thread.sleep(700);
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
