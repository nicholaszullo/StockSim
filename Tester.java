import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpResponse.*;
import java.util.Scanner;

public class Tester {
	public static void main(String[] args) {
		StringBuilder url = new StringBuilder();
		Scanner scan = new Scanner(System.in);
		url.append("https://api.tdameritrade.com/v1/marketdata/");
		System.out.println("Enter a stock ticker: ");
		String stock = scan.nextLine();
		url.append(stock);
		url.append("/quotes?apikey=ItsASecret");
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder(URI.create(url.toString())).header("accept", "application/json").GET().build();
		try {
			HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
		//	System.out.println(resp.body());

			int index = resp.body().indexOf("\"lastPrice\"");
			for (int start = index; index < start+20 && (resp.body().charAt(index) > '9' || resp.body().charAt(index) <= '\"'); index++);
			String found = resp.body().substring(index, index+6);
			System.out.println("Last price of " + stock + ": " + found);
			
		} catch (Exception e) {
			System.out.println("Something broke");
		}
		
		
	}


}