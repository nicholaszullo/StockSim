public class Buyer extends Thread {
	//private final int BUYERS = 1; // Change this based on number of buying methods
	private ThreadDriver shared;

	public Buyer(ThreadDriver td) {
		shared = td;
	}

	public void run() {
		buyAlgorithm();
	}

	private void buyAlgorithm() {
		synchronized (shared) {		//Try to obtain lock on the shared memoryl, wait until ready
			System.out.println("Let the buying begin! " + shared.getCash());
			shared.changeCash(1000);
			System.out.println("Buyer stop " + shared.getCash());
		}
	}

}