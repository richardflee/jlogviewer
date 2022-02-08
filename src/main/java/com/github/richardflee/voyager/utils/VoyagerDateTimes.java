package com.github.richardflee.voyager.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.github.richardflee.voyager.log_objects.LogExtract;

/**
 * Voyager time stamp formats 
 */
public class VoyagerDateTimes {
	// start end session time
	public static final LocalTime NOON = LocalTime.of(12, 0, 0, 0);
	
	// datetime patterns
	public static final String FILE_PREFIXDATE_PATTERN = "yyyy_MM_dd";
	public static final DateTimeFormatter FILE_PREFIXDATE_FORMATTER =
			DateTimeFormatter.ofPattern(FILE_PREFIXDATE_PATTERN);
	
	public static final String LOGLINE_TIMESTAMP_PATTERN = "yyyy/MM/dd HH:mm:ss";
	public static final DateTimeFormatter LOGLINE_TIMESTAMP_FORMATTER =
			DateTimeFormatter.ofPattern(LOGLINE_TIMESTAMP_PATTERN);
	
	public static final String EXTRACTS_TIMESTAMP_PATTERN = "HH:mm:ss.SSS";
	public static final DateTimeFormatter EXTRACTS_TIMESTAMP_FORMATTER = 
	DateTimeFormatter.ofPattern(EXTRACTS_TIMESTAMP_PATTERN);
	
	
	/**
	 * Converts LocalDateTime to Date 
	 */
	public static Date localDateTimeToDate(LocalDateTime localDateTime) {
		var instant = Timestamp.valueOf(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).toInstant();
		return Date.from(instant);
	}
	
	/**
	 * Converts LogExtracts time stamp format 'xx:xx:xx.xxx =>' to LocalTime value
	 */
	public static LocalTime extractsTimeStampToLocalDate(String timeStamp) {
		var s = timeStamp.replace(LogExtract.POINTER, "");
		return LocalTime.parse(s, VoyagerDateTimes.EXTRACTS_TIMESTAMP_FORMATTER);
	}
	
	public static void main(String[] args) {
		LocalDateTime localDateTime = LocalDateTime.parse("2019-11-15T13:15:30");
		System.out.println(localDateTime.toString());
		System.out.println(VoyagerDateTimes.localDateTimeToDate(localDateTime));
		
		System.out.println();
		var timeStamp = "12:34:56.789 =>";
		System.out.println(timeStamp);
		System.out.println(VoyagerDateTimes.extractsTimeStampToLocalDate(timeStamp).toString());
		
	}
}
