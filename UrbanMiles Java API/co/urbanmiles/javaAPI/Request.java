package co.urbanmiles.javaAPI;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Request {
	public static LinkedHashMap<String, Object> mapOf(Object... params) {
		if (params.length % 2 != 0)
			throw new IllegalArgumentException("UrbanMiles API Error: Must be in pairs (uneven length)");
		LinkedHashMap<String, Object> result = new LinkedHashMap<>();
		for (int i = 0; i < params.length; i+=2) {
			try {
				result.put((String) params[i], params[i + 1]);
			} catch(ClassCastException e) {
				throw new IllegalArgumentException("UrbanMiles API Error: Keys must be strings");
			}
		}
		return result;
	}
	
	private static String urlencode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError(e); // will definitely never happen
		}
	}
	private static String urldecode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError(e); // will definitely never happen
		}
	}
	
	@SuppressWarnings("unchecked")
	public static RequestResult makeRequest(String method, LinkedHashMap<String, Object> params) throws IOException {
		JSONObject json = null;
		try {
			Object[] jsonResult = GetJson.getJson("https://beta.urbanmiles.co/api/"+method, createQueryString(params));
			if (jsonResult == null)
				throw new RuntimeException("The API request, " + method + ", does not exist");
			json = (JSONObject) jsonResult[0];
			// rawJson = (String) jsonResult[1];
		} catch (ParseException e) {
			throw new AssertionError("There is an error in the UrbanMiles API System, please contact"
					+ " the developers with this error message at https://urbanmiles.co/contact", e);
		}
		boolean success = (boolean) json.get("success");
		String error = !success ? (String) json.get("error") : null;
		String error_msg = !success ? (String) json.get("error_msg") : null;
		
		HashMap<String, Object> args = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : ((Set<Map.Entry<String, Object>>) json.entrySet())) {
			switch (entry.getKey()) { case "success": case "error": case "error_msg": continue; }
			args.put(entry.getKey(), entry.getValue());
		}
		return new RequestResult(success, error, error_msg, args);
	}

	protected static String createQueryString(LinkedHashMap<String, Object> params) {
		StringBuilder sb = new StringBuilder();
		for (HashMap.Entry<String, Object> entry : params.entrySet()) {
			if (sb.length() > 0) {
				sb.append('&');
			}
			sb.append(urlencode(entry.getKey())).append('=').append(urlencode(entry.getValue().toString()));
		}
		return sb.toString();
	}

	
	public static String signRequest(LinkedHashMap<String, Object> params) {
		return signRequest(Application.app_key, params);
	}

	/**
	 * 
	 * @param appKey The app_key recieved when the app was identified by app.identify
	 * @param params Params, must be in same order as the API request param order
	 * @return the signature, include it with the request as the "sig" parameter
	 */
	@SuppressWarnings("unchecked")
	public static <T> String signRequest(String appKey, LinkedHashMap<String, T> params) {
		StringBuilder output = new StringBuilder();
	    for (Map.Entry<String, T> entry : params.entrySet()) {
	    	T value = entry.getValue();
	    	
	    	if (!(value instanceof List)) {
		    	String value_str = urldecode(value.toString());
	    		output.append(urlencode(entry.getKey()) + value_str.getBytes(StandardCharsets.UTF_8).length + value_str);
	    	} else for (T v : ((List<T>) value)) {
		    	String value_str = urldecode(v.toString());
	    		output.append(urlencode(entry.getKey()) + value_str.getBytes(StandardCharsets.UTF_8).length + value_str);
    		}
	    }
	    try {
		    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			sha256_HMAC.init(new SecretKeySpec(appKey.toUpperCase().getBytes(), "HmacSHA256"));
			return bytesToHex(sha256_HMAC.doFinal(output.toString().getBytes()));
	    } catch(NoSuchAlgorithmException | InvalidKeyException e) {
	    	throw new IllegalStateException("This should never happen, if it does the system is missing Hmac-SHA256 which is wierd");
	    }
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
