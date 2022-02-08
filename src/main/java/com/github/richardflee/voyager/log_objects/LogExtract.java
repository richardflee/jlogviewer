package com.github.richardflee.voyager.log_objects;

import java.time.LocalDateTime;
import java.util.Objects;

import com.github.richardflee.voyager.enums.MatchersTypeEnum;
import com.github.richardflee.voyager.fileio.VoyagerPaths;
import com.github.richardflee.voyager.utils.VoyagerDateTimes;

public class LogExtract {

	public static final String POINTER = " =>";
	private static final String COMMENT_STUB = "000 - COMMENT - [User Comment" + LogMatcher.LOG_DELIMITER;

	private String timeStamp = "";
	private String messageLine = "";
	private String messageType = "";
	private String LogLine = "";
	private LogMatcher matcher = null;

	public LogExtract(String line, LogMatcher matcher) {
		this.LogLine = line;
		this.matcher = matcher;
		this.timeStamp = compileTimeStamp(getTimeStamp(line));
		this.messageLine = matcher.getMessageLine(line);
		this.messageType = matcher.getMessageType();
	}

	public LogExtract(String userComment) {
		var ldt = LocalDateTime.of(VoyagerPaths.getStartDate(), VoyagerDateTimes.NOON.plusSeconds(1));
		this.timeStamp = compileTimeStamp(ldt);
		this.messageLine = userComment;
		this.messageType = MatchersTypeEnum.COMMENT.toString();
		this.LogLine = compileCommentLogLine(userComment, ldt);
	}

	public boolean isCommentType() {
		return MatchersTypeEnum.getEnum(this.messageType) == MatchersTypeEnum.COMMENT;
	}

	public boolean isExtractType() {
		return MatchersTypeEnum.getEnum(this.messageType) != MatchersTypeEnum.COMMENT;
	}
	
	public boolean isFocusMetric() {
		return MatchersTypeEnum.getEnum(this.messageType) == MatchersTypeEnum.METRIC_F;
	}
	
	public boolean isGuidingMetric() {
		return MatchersTypeEnum.getEnum(this.messageType) == MatchersTypeEnum.METRIC_G;
	}

	public boolean isPointingMetric() {
		return MatchersTypeEnum.getEnum(this.messageType) == MatchersTypeEnum.METRIC_P;
	}
	
	public boolean isMetricType() {
		return isFocusMetric() || isGuidingMetric() || isPointingMetric();
	}

	private String compileTimeStamp(LocalDateTime ldt) {
		return ldt.format(VoyagerDateTimes.EXTRACTS_TIMESTAMP_FORMATTER) + POINTER;
	}

	// 
	private String compileCommentLogLine(String userComment, LocalDateTime ldt) {
		var ts = ldt.format(VoyagerDateTimes.LOGLINE_TIMESTAMP_FORMATTER);
		return String.format("%s %s %s", ts, COMMENT_STUB, userComment);
	}

	// converts log time stamp format 2021/12/11 12:00:30 353 to datetime object
	private LocalDateTime getTimeStamp(String line) {
		String[] token = line.split(" ");
		String strDate = token[0].trim();
		String strTime = token[1].trim();
		double nano = Double.valueOf(token[2].trim()) * 1e6;

		LocalDateTime ldt = LocalDateTime.parse(strDate + " " + strTime, VoyagerDateTimes.LOGLINE_TIMESTAMP_FORMATTER);
		return ldt.plusNanos((long) nano);
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public String getMessageLine() {
		return messageLine;
	}

	public String getMessageType() {
		return messageType;
	}

	public String getLogLine() {
		return LogLine;
	}

	public LogMatcher getMatcher() {
		return matcher;
	}

//	@Override
//	public String toString() {
//		return "LogExtract [timeStamp=" + timeStamp + ", messageLine=" + messageLine + ", messageType=" + messageType
//				+ ", logLine=" + logLine + "]";
//	}

	@Override
	public String toString() {
		var ts = this.timeStamp.replace(POINTER, "");
		return String.format("%s %s %s", ts, LogExtract.COMMENT_STUB, this.messageLine);
	}

	@Override
	public int hashCode() {
		return Objects.hash(LogLine, messageLine, messageType, timeStamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogExtract other = (LogExtract) obj;
		return Objects.equals(LogLine, other.LogLine) && Objects.equals(messageLine, other.messageLine)
				&& Objects.equals(messageType, other.messageType) && Objects.equals(timeStamp, other.timeStamp);
	}

	public static void main(String[] args) {
		
//		var comments = new ArrayList<LogExtract>();
//		
//		String[] arr = {"msg11", "msg22", "msg33"};
//		for (int i = 0; i < arr.length; i++) {
//			comments.add(new LogExtract(arr[i]));
//		}
//		
//		var line = comments.stream().map(p -> p.messageLine).collect(Collectors.joining("\n"));
//		
//		comments.clear();
//		System.out.println("\nclear comments:");
//		comments.stream().forEach(p ->System.out.println(p.getExtractedLogLine()));
//		List<String> commentLines = new ArrayList<String>(Arrays.asList(line.split("\n")));
//		
//		
//		commentLines.remove(1);
//		commentLines.add("lastmsg");
//		
//		
//		for (var commentLine : commentLines) {
//			comments.add(new LogExtract(commentLine));
//		}
//		
//		System.out.println("\nnew comments:");
//		comments.stream().forEach(p ->System.out.println(p.getExtractedLogLine()));
		
	}

}
