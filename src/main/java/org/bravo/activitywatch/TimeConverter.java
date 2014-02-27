package org.bravo.activitywatch;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeConverter {

	private static final String TIMER_FORMAT_TIME = "HH:mm:ss";
	private static SimpleDateFormat sdf = new SimpleDateFormat(TIMER_FORMAT_TIME);
	private static final DecimalFormat df = new DecimalFormat( "#,###,##0.00" );
	private static SettingsManager settings = SettingsManager.getInstance();
	
	public static String convertToTime(Long millis) {
		GregorianCalendar elapsedTime = new GregorianCalendar();
		elapsedTime.set(Calendar.HOUR_OF_DAY, 0);
		elapsedTime.set(Calendar.MINUTE, 0);
		elapsedTime.set(Calendar.SECOND, 0);
		long t = elapsedTime.getTimeInMillis() + millis;
		elapsedTime.setTimeInMillis(t);
		return sdf.format(elapsedTime.getTime());
	}

	public static String convertToDecimal(Long millis) {
		return df.format(Double.valueOf(millis) / 3600000);
	}

	public static String autoconvert(Long millis) {
		switch (settings.getTimeFormat()) {
			case DECIMAL:
				return convertToDecimal(millis);
			case TIME:
				return convertToTime(millis);
			default:
				return convertToTime(millis);
		}
	}

}
