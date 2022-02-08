package com.github.richardflee.voyager.log_objects;

import java.util.ArrayList;
import java.util.List;

import com.github.richardflee.voyager.enums.MatchersTypeEnum;
import com.github.richardflee.voyager.fileio.VoyagerFileReadWriter;

/**
 * Compiles and saves list of Matcher records from VoyagerLogViewer.csv
 */
public class VoyagerLogMatchers {

	private static final int NFIELDS = 4;
	private static final String CSV_HEADER = String.format("%s, %s, %s, %s", "Select", "Match Message",
			"Preset Message", "Type");

	private List<LogMatcher> matchers = null;

	public VoyagerLogMatchers() {
		this.matchers = compileMatchersFromFile();
		// add comment matcher top of matchers list
		matchers.add(0, new LogMatcher(true, LogMatcher.COMMENT_MATCH_TEXT, "", MatchersTypeEnum.COMMENT.toString()));
	}
	
	/**
	 * Saves current user selections in matchers table to VoyagerLogViewer.csv file
	 * 
	 * @param matchers matcher records listed in viewer matcher table
	 */
	public void saveMatchersToFile() {
		// compiles lines array from matchers list
		var lines = new ArrayList<String>();
		for (var matcher : this.matchers) {
			lines.add(matcher.toString());
		}

		// remove added comment line from matchers list, then add header line
		// before overwriting VoyagerLogViewer.csv file
		if (LogMatcher.isCommentLine(lines.get(0)) == true) {
			lines.remove(0);
		}
		lines.add(0, CSV_HEADER);
		VoyagerFileReadWriter.writeLogMatchersFile(lines);
	}

	// imports comma-delim text list from VoyagerLog.csv file and converts to
	// a list of log matcher objects
	private List<LogMatcher> compileMatchersFromFile() {
		
		// import Voyager csv into allLines list
		// remove CSV_HEADER line at top of list  
		var allLines = VoyagerFileReadWriter.readLogMatchersFile();
		
		allLines.remove(0);
		
		// split lines and compile matchers list. First line is header line
		var matchers = new ArrayList<LogMatcher>();
		for (var line : allLines) {
			var tokens = line.split(LogMatcher.CSV_DELIMITER);
			if (tokens.length >= NFIELDS) {
				matchers.add(new LogMatcher(line));
			}
		}
		return matchers;
	}
	
	public List<LogMatcher> getMatchers() {
		return matchers;
	}

	public void setMatchers(List<LogMatcher> matchers) {
		this.matchers = matchers;
	}

	public static void main(String[] args) {

		var logMatchers = new VoyagerLogMatchers();
//		logMatchers.getMatchers().stream()
//				.filter(p -> p.isExtractMatcher())
//				.forEach(p -> p.setSelected(METRICS_LIST.contains(p.getMatchText())));

		logMatchers.getMatchers().stream()
				.filter(p -> p.isExtractMatcher())
				.filter(p -> p.isSelected())
				.forEach(p -> System.out.println(p.toString()));
		
				
//		var srcPath = VoyagerPaths.getPathToMatchersFile();
//		var dstPath = srcPath.getParent().resolve("VoyagerLogViewer.tmp");
//		
//		try {
//		    Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
//		 
//		} catch (IOException ex) {
//		    System.err.format("I/O Error when copying file");
//		}

	}
}