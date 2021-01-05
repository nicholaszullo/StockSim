import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Buyer extends Thread {
	private ThreadDriver shared;

	public Buyer(ThreadDriver td) {
		shared = td;
	}

	public void run() {
		ArrayList<String> tickers = shared.database.selectData("sqlite_master", "name", "");
		for (String s : tickers) {
			if (s.equals("Positions") || s.equals("cash"))
				continue;
			new Thread(){
				public void run(){
					buyAlgorithm(s);
				}
			}.start();
		}
	}

	private void buyAlgorithm(String ticker) {
		double last15 = 0;
		double last25 = 0;
		double last50 = 0;
		while (true) {
			last15 = movingAverage(ticker, 15);
			last25 = movingAverage(ticker, 25);
			last50 = movingAverage(ticker, 50);
			if (Math.abs(last15 - last25) < .01 && Math.abs(last15-last50) < .01 && last15 != 0) {
				synchronized (shared) {
					double currPrice = Double.parseDouble(shared.database.selectData(ticker, "price", "ORDER BY date DESC LIMIT 1").get(0));
					if (currPrice * 5 < Double.parseDouble(shared.getCash())){
						if (shared.numberShares(ticker) < 50){
							shared.log.out(shared.log.INFO, "buying " + ticker + " at " + currPrice);
							shared.subCash(5 * currPrice);
							shared.database.insertRow("Positions", new String[] {String.valueOf(shared.nextID()), ticker, "5", String.valueOf(currPrice), LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"))});
						} 
					}
				}
			}
			try {
				Thread.sleep(3000);
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