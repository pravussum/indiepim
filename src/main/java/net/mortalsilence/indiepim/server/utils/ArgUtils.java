package net.mortalsilence.indiepim.server.utils;

public class ArgUtils {

	public static boolean empty(String str) {
		return str == null || "".equals(str);
	}

	public static Integer parseInt(String str) {
		return empty(str) ? null : new Integer(str);
	}
	
	public static Integer safeParseInt(String str) {
		try {			
			return empty(str) ? null : new Integer(str);
		} catch(NumberFormatException nfe) {
			return null;
		}
	}
}
