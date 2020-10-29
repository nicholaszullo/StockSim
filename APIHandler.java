import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.net.http.HttpResponse.*;
import java.time.LocalDateTime;

import homemadejson.output.*;


public class APIHandler {

	HttpClient clientSender;

	public APIHandler() {
		clientSender = HttpClient.newHttpClient();
		//checkAccessToken();
	}

	/**
	 * Used to retrieve information about the stock account
	 * 
	 * @return A JSON object of the balance of the account
	 */
	public JsonObject getBalance() {
		checkAccessToken();
		return getBalance(clientSender);
	}

	private JsonObject getBalance(HttpClient client) {
		StringBuilder auth = new StringBuilder();
		auth.append("Bearer ");
		auth.append(getAccessToken());

		StringBuilder url = new StringBuilder();
		url.append("https://api.tdameritrade.com/v1/accounts/");
		url.append(getAccountNumber());

		HttpRequest balance = HttpRequest.newBuilder(URI.create(url.toString())).header("accept", "application/json")
				.header("authorization", auth.toString()).GET().build();
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
	public JsonObject getTicker(String ticker) {
		checkAccessToken();
		return getTicker(ticker, clientSender);
	}

	private JsonObject getTicker(String ticker, HttpClient client) {
		StringBuilder url = new StringBuilder();

		url.append("https://api.tdameritrade.com/v1/marketdata/");
		url.append(ticker);
		url.append("/quotes?apikey=");
		url.append(getApiKey());

		HttpRequest req = HttpRequest.newBuilder(URI.create(url.toString())).header("accept", "application/json").GET()
				.build();
		try {
			HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
			return new JsonObject(resp.body());

		} catch (Exception e) {
			panic("Unable to receive response! " + e.toString());
			return null; // panic will exit but compiler needs a return statement still
		}
	}

	/**
	 * Used to get a new access token. Access tokens will expire 30 minutes after
	 * being obtained.
	 * 
	 * @return the file AccessToken.key will now contain the updated key
	 */
	public void getNewAccessToken() {
		getNewAccessToken(clientSender);
	}

	private void getNewAccessToken(HttpClient client) {
		BufferedWriter access = null;
		try {
			access = new BufferedWriter(new FileWriter(new File("AccessToken.key"), false));
		} catch (IOException e) {
			panic("Access key cannot be created!");
		}

		HttpRequest newkey = null;

		StringBuilder postBody = new StringBuilder("grant_type=refresh_token&refresh_token=");
		postBody.append(getRefreshToken());
		postBody.append("&access_type=&code=&client_id=");
		postBody.append(getApiKey());
		postBody.append("&redirect_uri=");
		newkey = HttpRequest.newBuilder().uri(URI.create("https://api.tdameritrade.com/v1/oauth2/token"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(BodyPublishers.ofString(postBody.toString())).build();

		HttpResponse<String> response = null;
		try {
			response = client.send(newkey, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			panic("Access Token request failed!");
		}
		try {
			JsonObject json = new JsonObject(response.body());
			access.write(String.valueOf(json.getValues().get("access_token")));
			access.newLine();
			access.write(LocalDateTime.now().toString());
			access.close();
		} catch (IOException e) {
			panic("Failed to write response to file!");
		}
	}

	/**
	 * 
	 * @param index     The index to get the mover stocks from
	 * @param direction Either "up" or "down", or null to not specify a direction
	 * @param change    Either "value" or "percentage"
	 * @return 10 mover stocks in a Json format
	 */
	public JsonObject getMovers(String index, String direction, String change) {
		checkAccessToken();
		return getMovers(index, direction, change, clientSender);

	}

	private JsonObject getMovers(String index, String direction, String change, HttpClient client) {
		StringBuilder url = new StringBuilder();

		url.append("https://api.tdameritrade.com/v1/marketdata/$");
		url.append(index + "/movers");
		url.append("?direction=" + direction);
		url.append("&change=" + change);

		StringBuilder auth = new StringBuilder();
		auth.append("Bearer ");
		auth.append(getAccessToken());

		HttpRequest req = HttpRequest.newBuilder(URI.create(url.toString())).header("accept", "application/json")
				.header("authorization", auth.toString()).GET().build();
		HttpResponse<String> response = null;
		try {
			response = client.send(req, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			panic("Unable to get movers!" + e.toString());
		}
		return new JsonObject(response.body());
	}

	private void checkAccessToken() {
		BufferedReader access = null;
		try {
			access = new BufferedReader(new FileReader(new File("AccessToken.key")));
			access.readLine();
		} catch (IOException e) {
			getNewAccessToken();		//If the file does not exist create an access token
		}
		LocalDateTime time = null;
		try {
			time = LocalDateTime.parse(access.readLine());
			access.close();			
		} catch (Exception e) {
			getNewAccessToken();		//If there was not a time in the file make a new one with proper format
		}
		try {
			if (LocalDateTime.now().minusMinutes(30).isAfter(time)){
				getNewAccessToken();
			}
		} catch (Exception e){
			getNewAccessToken();		//If an error in date format, make a new one
		}
	}

	/** Use to cleanly get the access token in api call methods
	 * 
	 * @return The access token with no spaces before or after
	 */
	private String getAccessToken(){
		BufferedReader access = null;
		String accessToken = "";
		try {
			access = new BufferedReader(new FileReader(new File("AccessToken.key")));
		} catch (FileNotFoundException e) {
			panic("Access Token does not exist!");
		}
		try {
			accessToken = access.readLine();
			access.close();
		} catch (IOException e) {
			panic("Error reading Access Token!" + e.toString());
		}
		return accessToken;
	}

	/** Use to cleanly get the refresh token in api call methods
	 * 	Only used to get a new access token so far
	 * @return The refresh token with no spaces before or after
	 */
	private String getRefreshToken() {
		BufferedReader refresh = null;
		try {
			refresh = new BufferedReader(new FileReader(new File("RefreshToken.key")));
		} catch (FileNotFoundException e) {
			panic("Refresh Token does not exist!");
		}
		String refreshToken = "";
		try {
			refreshToken = refresh.readLine();
			refresh.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return refreshToken;
	}

	/** Use to cleanly get the api key in api call methods
	 * 
	 * @return The api key with no spaces before or after
	 */
	private String getApiKey() {
		BufferedReader api = null;
		try {
			api = new BufferedReader(new FileReader(new File("ApiKey.key")));
		} catch (FileNotFoundException e) {
			panic("Api Key does not exist!");
		}
		String apiKey = "";
		try {
			apiKey = api.readLine();
			api.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return apiKey;
	}

	/** Use to cleanly get the account number in api call methods
	 * 
	 * @return The account number with no spaces before or after
	 */
	private String getAccountNumber(){
		BufferedReader accountNumber = null;
		try {
			accountNumber = new BufferedReader(new FileReader(new File("AccountNumber.key")));
		} catch (FileNotFoundException e1) {
			panic("Account number not found!");
		}
		String num = "";
		try {
			num = accountNumber.readLine();
		} catch (IOException e1) {
			panic("Unable to read Account Number!");
		}
		return num;
	}

	/** Prints the debug message and exits
	 * 
	 * @param s the message to display
	 */
	private void panic(String s){
		System.out.println(s);
		System.exit(0);
	}
	
}
