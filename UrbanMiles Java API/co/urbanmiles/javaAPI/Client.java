package co.urbanmiles.javaAPI;

import static co.urbanmiles.javaAPI.Request.mapOf;

import java.io.IOException;
import java.util.LinkedHashMap;

public class Client {
	final String email;
	final String auth_token;
	final String user_id;
	String firstname;
	String lastname;
	
	public Client(String email, String auth_token, String user_id, String firstname, String lastname) {
		this.auth_token = auth_token;
		this.email = email;
		this.user_id = user_id;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public RequestResult makeRequest(String method) throws IOException {
		return this.makeRequest(method, Request.mapOf());
	}
	
	public RequestResult makeRequest(String method, LinkedHashMap<String, Object> params) throws IOException {
		String sig = Request.signRequest(params);
		params.put("auth_token", this.auth_token);
		params.put("sig", sig);
		return Request.makeRequest(method, params);
	}
	
	public RequestResult deauthorize() throws IOException {
		return this.makeRequest("app.deauth", Request.mapOf());
	}
	
	public static Client authenticate(String user_email, CharSequence password, String device_id, String device_type) throws IOException {
		LinkedHashMap<String, Object> params = mapOf(
				"login", user_email,
				"password", password,
				"device_id", device_id,
				"device_type", device_type,
				"app_id", Application.app_id
			);
		params.put("sig", Request.signRequest(params));
		RequestResult result = Request.makeRequest("app.auth", params);
		if (!result.success)
			throw new RuntimeException(result.error_code + ": " + result.error_msg);
		return new Client(
					user_email,
					result.getString("auth_token"),
					result.getString("user_id"),
					result.getString("firstname"),
					result.getString("lastname")
				);
	}
	
}
