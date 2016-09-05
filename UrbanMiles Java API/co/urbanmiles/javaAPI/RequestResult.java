package co.urbanmiles.javaAPI;

import java.util.HashMap;

public class RequestResult {
	final boolean success;
	final String error_code;
	final String error_msg;
	private HashMap<String, Object> args;
	
	protected RequestResult(boolean success, String error_code, String error_msg, HashMap<String, Object> args) {
		this.success = success;
		this.error_code = error_code;
		this.error_msg = error_msg;
		this.args = args;
	}
	
	public Object get(String key) {
		return args.get(key);
	}
	
	public String getString(String key) {
		return (String) args.get(key);
	}
	
	public boolean getBoolean(String key) {
		return (boolean) args.get(key);
	}
	
	@Override
	public String toString() {
		return "RequestResult[success="+success+", error_code="+error_code+", error_msg="+error_msg+"]";
	}
}
