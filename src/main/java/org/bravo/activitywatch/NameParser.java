package org.bravo.activitywatch;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameParser {
	
	private static String HOUR_PATTERN = "([1-9][h]|[1-2][0-9][h])";
	private static String MINUTE_PATTERN = "([1-9][m]|[1-5][0-9][m])";
	private static String SECOND_PATTERN = "([1-9][s]|[1-5][0-9][s])";
	
	public static LocalTime parseName(String name) {
		int hour = Integer.valueOf(find(HOUR_PATTERN, name).replace("h", ""));
		int minute = Integer.valueOf(find(MINUTE_PATTERN, name).replace("m", ""));
		int second = Integer.valueOf(find(SECOND_PATTERN, name).replace("s", ""));
		LocalTime t = LocalTime.of(hour, minute, second);
		return t;
	}
	
	private static String find(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
		  return str.substring(
		    matcher.start(), matcher.end());
		}
		return "0";
	}
}
