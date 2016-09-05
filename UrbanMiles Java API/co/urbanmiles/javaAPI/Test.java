package co.urbanmiles.javaAPI;

import java.io.IOException;

public class Test {
	
	public static void main(String[] args) throws IOException {
		Application.setAppId("PUT APPLICATION ID HERE");
		Application.setAppKey("PUT APPLICATION KEY HERE");
		
		test();
	}
	
	/**
	 * Test #1: Login, make a random request, logout
	 * @throws IOException
	 */
	public static void test() throws IOException {
		
		Client c = Client.authenticate("EMAIL", "PASSWORD", "DEVICE ID", "DEVICE TYPE");
		System.out.println(c.auth_token);
		
		RequestResult result = c.makeRequest("test2", Request.mapOf("test", "testing"));
		System.out.println(result);
		System.out.println(result.getString("test"));
		
		RequestResult result2 = c.deauthorize();
		System.out.println(result2);
	}
}
