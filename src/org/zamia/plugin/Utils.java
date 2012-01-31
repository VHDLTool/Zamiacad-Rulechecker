package org.zamia.plugin;

import java.util.Arrays;
import java.util.Collection;

public class Utils {
		
	public static String concatenate(Object[] collection) {
		return concatenate(Arrays.asList(collection), ",");
	}
	
	public static String concatenate(Object[] collection, String separator) {
		return concatenate(Arrays.asList(collection), separator);
	}
	
	public static String concatenate(Object[] collection, String separator, boolean... keepLastDot) {
		return concatenate(Arrays.asList(collection), separator, keepLastDot);
	}
	
	public static String concatenate(Collection c) {
		return concatenate(c, ",");
	}
	public static String concatenate(Collection c, String separator, boolean... keepLastDot) {
		StringBuffer sb = new StringBuffer();
		for (Object o: c)
			sb.append(o.toString()).append(separator);
				
		if (!c.isEmpty() && keepLastDot.length == 0) // remove extra separator at the end
			sb.setLength(sb.length() - separator.length()); 
				
		return sb.toString();
	}


}
