package net.mortalsilence.indiepim.server.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

    public static <T> List<T> object2Collection(T obj) {
        List<T> result = new LinkedList<T>();
        result.add(obj);
        return result;
    }
}
