import java.io.Serializable;

public class Position implements Serializable {
	private static final long serialVersionUID = 9221506446260929571L;
	public String ticker;
	public double purchase_price;
	public int shares;
	public String purchase_date;
	public Position(String ticker, double price, int shares, String date ){
		this.ticker = ticker;
		purchase_price = price;
		this.shares = shares;
		purchase_date = date;
	}
	@Override
	public String toString() {
		return shares + " of " + ticker + " at " + purchase_price + " on " + purchase_date;
	}
}
