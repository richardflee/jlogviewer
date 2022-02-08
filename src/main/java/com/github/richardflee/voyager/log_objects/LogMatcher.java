package com.github.richardflee.voyager.log_objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.richardflee.voyager.enums.MatchersTypeEnum;

/**
 * Encapsulates a single matcher record imported from VoyagerLogViewer.csv file
 */
public class LogMatcher {
	
	// csv delimiter and delimiter marking start of match text in Voyage log record line 
	public static final String CSV_DELIMITER = ",";
	public static final String LOG_DELIMITER = "] -";
	
	public static final String COMMENT_MATCH_TEXT = "User Comment";
	
	private boolean selected = true;
	private String matchText = "";
	private String presetText = "";
	private String messageType = "";
	
	/**
	 * @param selected flag indicating whether to test if Voyager log lines contain matchText string
	 * @param matchText tests if log line contains this text
	 * @param presetText option for user to specify extracted text
	 * @param messageType 'INFO', 'EMERGENCY' .., text listed in VoyagerLogViewer.csv file
	 */
	public LogMatcher(boolean selected, String matchText, String presetText, String messageType) {
		this.selected = selected;
		this.matchText = matchText;
		this.presetText = presetText;
		this.messageType = messageType;		
	}
	
	/**
	 * @param line comma delimited line encoding LogMatcher parameters
	 */
	public LogMatcher(String line) {
		var tokens = Arrays.asList(line.split(CSV_DELIMITER));
		this.selected = !(new String("0").equals(tokens.get(0).trim()));
		this.matchText = tokens.get(1).trim();
		this.presetText = tokens.get(2).trim();
		this.messageType = tokens.get(3).trim();	
	}
	
	public boolean isWarningMatcher() {
		var en = MatchersTypeEnum.getEnum(this.getMessageType());
		return en.isWarning();
	}
	
	public boolean isMetricMatcher() {
		var en = MatchersTypeEnum.getEnum(this.getMessageType());
		return en.isMetric();
	}
	
	public static boolean isCommentLine(String line) {
		boolean b = line.contains(COMMENT_MATCH_TEXT);
		return b;
	}
	
	
	/**
	 * Text to copy to extract log. Default text extracted from Voyager log, option for user preset to override
	 * with custom text
	 * 
	 * @param logLine current line from VoyagerLogViewer.csv file 
	 * @return user preset text or last text string in   Voyager log line following '] -' delimiter
	 */
	public String getMessageLine(String logLine) {
		var isPreset = presetText.trim().length() > 0;
		var tokens  = logLine.split(LOG_DELIMITER);
		var logText = tokens[tokens.length - 1].trim();
		return (isPreset) ? presetText : logText;
	}
	
	/**
	 * Returns true if matchText detected in current Voyager log line
	 * <p>String matching is case-insensitive</p>
	 * 
	 * @param logLine full Voyager log line
	 * @return true if Voyager log line contains matching text
	 */
	public boolean matches(String logLine) {
		// case insensitive sub string match
		return logLine.toLowerCase().contains(this.matchText.toLowerCase());
	}
	
	public boolean isCommentMatcher() {
		return this.messageType.toLowerCase().equals(MatchersTypeEnum.COMMENT.toString().toLowerCase());
	}
	
	public boolean isExtractMatcher() {
		return !isCommentMatcher();
	}
	

	public Boolean isSelected() {
		return selected;
	}


	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	

	public String getMatchText() {
		return matchText.trim();
	}


	public String getPresetText() {
		return presetText.trim();
	}


	public String getMessageType() {
		return messageType.trim();
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(matchText, messageType, presetText, selected);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogMatcher other = (LogMatcher) obj;
		return Objects.equals(matchText, other.matchText) && Objects.equals(messageType, other.messageType)
				&& Objects.equals(presetText, other.presetText) && selected == other.selected;
	}

	@Override
	public String toString() {
		var terms = new ArrayList<String>();
		var flag = this.isSelected() ? "1" : "0";
		terms.add(flag); 						// 0 => off, otherwise on
		terms.add(this.getMatchText());			// text to match against line in VoyagerLog file
		terms.add(this.getPresetText());		// optional text to show in extracts table
		terms.add(this.getMessageType());		// type INFO, CRITICAL etc; sets message colour
		
		// combine elements in comma-delimited string
		var line = terms.stream().collect(Collectors.joining(CSV_DELIMITER));
		return line;
	}
	

	public static void main(String[] args) {
		
		var matchLine = "Astronomical Night Start";
		String presetLine = "";
		String messageStyle = "WARNING";
		var matcher = new LogMatcher(true, matchLine, presetLine, messageStyle);
		System.out.println(matcher.toString());
		
		var s1= "2021/12/11 18:17:03 723 - Astronomical Night Start";
		var s2 = "2021/12/11 18:17:03 691  Wait Astronomical Night: Data from connected Setup";
		
		System.out.println(String.format("match line detected in s1: %b",  matcher.matches(s1)));
		System.out.println(String.format("match line detected in s2: %b",  matcher.matches(s2)));
		
		var s3= "2021/12/11 17:00:00 001 - COMMENT - [User comment] - here i am";
		matchLine = "User Comment";
		matcher = new LogMatcher(true, matchLine, presetLine, messageStyle);
		System.out.println(String.format("\nmatch line '%s' detected in '%s': %b", 
						matchLine, 
						s3, 
						matcher.matches(s3)));
		
		s1 = "2021/12/11 12:00:01 000 - COMMENT - [User Comment] - I could not possibly comment ..";
		System.out.println(String.format("Comment line %s <= %b", s1, LogMatcher.isCommentLine(s1)));
		System.out.println(String.format("Comment line %s <= %b", s2, LogMatcher.isCommentLine(s2)));
	
	}
	
}

