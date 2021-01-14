import java.util.ArrayList;

public class Seller extends Thread {
	private ThreadDriver shared;

	public Seller(ThreadDriver td) {
		shared = td;
	}

	public void run() {
		ArrayList<String> tickers = shared.database.selectData("sqlite_master", "name", "");
		for (String s : tickers) {
			if (s.equals("Positions") || s.equals("cash"))
				continue;
			new Thread(){
				public void run(){
					sellAlgorithm(s);
				}
			}.start();
		}
	}

	private void sellAlgorithm(String ticker) {
		double last15 = 0;
		double last25 = 0;
		while (true) {
			last15 = movingAverage(ticker, 15);
			last25 = movingAverage(ticker, 25);
			if (Math.abs(last15 - last25) < .01 && last15 != 0) {
				synchronized (shared) {
					double currPrice = Double.parseDouble(
							shared.database.selectData(ticker, "price", "ORDER BY date DESC LIMIT 1").get(0));
					if (shared.ownTicker(ticker)){ 
						int id = Integer.parseInt(shared.database.selectData("Positions", "id", "WHERE ticker=\""+ ticker +"\" ORDER BY date ASC").get(0));
						if (Math.abs( Double.parseDouble(shared.database.selectData("Positions", "price", "WHERE id="+id).get(0)) - currPrice) > .03){ 
							shared.log.out(shared.log.INFO, "selling " + ticker + " at " + currPrice);
							shared.addCash(currPrice * Integer.parseInt(shared.database.selectData("Positions", "shares", "WHERE id="+id).get(0)));
							shared.database.deleteData("Positions", "WHERE id="+id);
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
