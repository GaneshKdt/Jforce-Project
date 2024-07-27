package com.nmims.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

/**
 * Generic Request Response Utility class
 * 
 * @author Swarup Singh Rajpurohit
 *
 */
public class RequestResponseUtils {

	/**
	 * Extracts payload string from request object and returns in readable format
	 * (for logging)
	 * 
	 * @param request
	 * @return request payload as string
	 */
	public static String getRequestPayloadAsString(HttpServletRequest request) {

		StringBuilder builder = new StringBuilder();

		Map<String, String[]> parameterMap = request.getParameterMap();
		Iterator<Entry<String, String[]>> iterator = parameterMap.entrySet().iterator();

		while (iterator.hasNext()) {

			Map.Entry<String, String[]> entry = iterator.next();
			String key = entry.getKey();
			String values = Arrays.toString(entry.getValue());

			builder.append(key).append(" : ").append(values);

			if (iterator.hasNext())
				builder.append(" | ");
		}
		return builder.toString();
	}

}
