import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.net.http.HttpResponse.*;

import homemadejson.output.*;

public class APIHandler {

	HttpClient clientSender;

	public APIHandler(){
		clientSender = HttpClient.newHttpClient();

	}
	/**	Used to retrieve information about the stock account
	 * 
	 * @return A JSON object of the balance of the account
	 */
	public JsonObject getBalance(){

		return getBalance(clientSender);
	}	
	private JsonObject getBalance(HttpClient client) {
		StringBuilder auth = new StringBuilder();
		auth.append("Bearer ");
		BufferedReader access = null;
		BufferedReader accountNumber = null;
		try {
			access = new BufferedReader(new FileReader(new File("AccessToken.key")));
		} catch (FileNotFoundException e) {
			panic("Access Token does not exist!");
		}
		try {
			auth.append(access.readLine());
			access.close();
		} catch (IOException e) {
			panic("Error reading Access Token!" + e.toString());
		}
		StringBuilder url = new StringBuilder();
		url.append("https://api.tdameritrade.com/v1/accounts/");
		try {
			accountNumber = new BufferedReader(new FileReader(new File("AccountNumber.key")));
		} catch (FileNotFoundException e1) {
			panic("Account number not found!");
		}
		try {
			url.append(accountNumber.readLine());
		} catch (IOException e1) {
			panic("Unable to read Account Number!");
		}

		HttpRequest balance = HttpRequest.newBuilder(URI.create(url.toString()))
			.header("accept", "application/json")
			.header("authorization", auth.toString())
			.GET()
			.build();
		HttpResponse<String> response = null;
		try {
			response = client.send(balance, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			panic("Unable to send balance request! " + e.toString());
		}
		return new JsonObject(response.body());

	}

	/**
	 * @param ticker The stock to get the price of
	 * @return a JSON representation of the API return
	 */
	public JsonObject getTicker(String ticker){
		return getTicker(ticker, clientSender);
	}
	private JsonObject getTicker(String ticker, HttpClient client) {
		BufferedReader api = null;
		try {
			api = new BufferedReader(new FileReader(new File("ApiKey.key")));
		} catch (FileNotFoundException e) {
			panic("Api Key does not exist!");
		}
		StringBuilder url = new StringBuilder();

		url.append("https://api.tdameritrade.com/v1/marketdata/");
		url.append(ticker);
		url.append("/quotes?apikey=");
		try {
			url.append(api.readLine());
			api.close();
		} catch (IOException e1) {
			panic("Failed to read API Key! " + e1.toString());
		}
		HttpRequest req = HttpRequest.newBuilder(URI.create(url.toString())).header("accept", "application/json").GET()
				.build();
		try {
			HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
			return new JsonObject(resp.body());

		} catch (Exception e) {
			panic("Unable to receive response! " + e.toString());
			return null;	//panic will exit but compiler needs a return statement still
		}
	}

	/**	Used to get a new access token. Access tokens will expire 30 minutes after being obtained.
	 * 
	 * @return the file AccessToken.key will now contain the updated key
	 */
	public void getNewAccessKey(){
		getNewAccessKey(clientSender);
	}
	private void getNewAccessKey(HttpClient client) {
		BufferedWriter access = null;
		BufferedReader refresh = null;
		BufferedReader api = null;
		try {
			access = new BufferedWriter(new FileWriter(new File("AccessToken.key"), false));
		} catch (IOException e) {
			panic("Access key cannot be created!");
		}
		try {
			refresh = new BufferedReader(new FileReader(new File("RefreshToken.key")));
		} catch (FileNotFoundException e) {
			panic("Refresh Token does not exist!");
		}
		try {
			api = new BufferedReader(new FileReader(new File("ApiKey.key")));
		} catch (FileNotFoundException e) {
			panic("Api Key does not exist!");
		}

		HttpRequest newkey = null;
		try {
			StringBuilder postBody = new StringBuilder("grant_type=refresh_token&refresh_token=");
			postBody.append(refresh.readLine());
			postBody.append("&access_type=&code=&client_id=");
			postBody.append(api.readLine());
			postBody.append("&redirect_uri=");
			newkey = HttpRequest.newBuilder().uri(URI.create("https://api.tdameritrade.com/v1/oauth2/token"))
					.header("Content-Type", "application/x-www-form-urlencoded")
					.POST(BodyPublishers.ofString(postBody.toString()))
					.build();
			refresh.close();
		} catch (IOException e) {
			panic("Access Token request failed to build!");
		}
		HttpResponse<String> response = null;
		try {
			response = client.send(newkey, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			panic("Access Token request failed!");
		}
		try {
			JsonObject json = new JsonObject(response.body());
			access.write(String.valueOf(json.getValues().get("access_token")));
			access.close();
		} catch (IOException e) {
			panic("Failed to write response to file!");
		}
	
	}
	private void panic(String s){
		System.out.println(s);
		System.exit(0);
	}
	
}
