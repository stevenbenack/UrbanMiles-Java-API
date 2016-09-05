package co.urbanmiles.javaAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetJson {
	protected static JSONParser jsonParser = new JSONParser();
	
	protected static String readUrl(String url, String queryString) throws IOException {
		HttpsURLConnection conn = null;
		try {
			conn = (HttpsURLConnection) new URL(url+"?"+queryString).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("GET"); // Doesn't matter if either GET or POST
			conn.setRequestProperty("charset", "UTF-8");
			conn.setRequestProperty("User-Agent", "UrbanMiles Java SDK v0.0.1");
			conn.setUseCaches(false);
		} catch(ProtocolException e) {
			throw new AssertionError(e); // should never happen
		} catch(IOException e) {
			throw e;
		}
		
		try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = in.read(chars)) != -1)
				buffer.append(chars, 0, read);
	
			return buffer.toString();
		} catch (IOException e) {
			throw e;
		}
	}

	public static Object[] getJson(String url, String queryString) throws IOException, ParseException {
		String json = readUrl(url, queryString);
		if (json.trim().isEmpty() || json == null)
			return null;
		System.out.println("json: " + json);
		JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
		return new Object[]{jsonObject, json};
	}
}