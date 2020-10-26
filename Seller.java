public class Seller extends Thread {
	private final int SELLERS = 1; // Change this based on number of selling methods
	private ThreadDriver shared;

	public Seller(ThreadDriver td) {
		shared = td;

	}

	public void run() {
		sellAlgorithm();
	}

	private void sellAlgorithm() {
		synchronized(shared){	//Try to obtain lock on shared memory, wait until ready
			System.out.println("Let the selling begin! " + shared.getCash());//Should be the value inside change cash from buyer
		}

	}
}
