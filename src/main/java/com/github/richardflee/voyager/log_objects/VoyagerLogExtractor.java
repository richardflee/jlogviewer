package com.github.richardflee.voyager.log_objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.github.richardflee.voyager.fileio.VoyagerFileReadWriter;
import com.github.richardflee.voyager.fileio.VoyagerPaths;

/**
 * Handles file Voyager log operations and updating selected log records
 *
 */
public class VoyagerLogExtractor {

	private VoyagerLogMatchers logMatchers = null;

	// lists comments selected and de-selected log extracts
	private List<LogExtract> allExtracts = null;

	public VoyagerLogExtractor(VoyagerLogMatchers logMatchers) {
		this.logMatchers = logMatchers;
		this.allExtracts = new ArrayList<>();
	}

	/**
	 * Compiles list of all lines extracted from Voyager log file(s) with a valid
	 * format time date stamp between noon on start date and next day noon
	 * 
	 * @param filePaths encapsulates start, end and extract file attributes
	 * @return list of time stamped text lines between noon on start date and next
	 *         day noon
	 */
	public List<LogExtract> getTableExtractsFromFiles(VoyagerPaths filePaths) {
		compileAllExtractsFromFile(filePaths);
		var tableExtracts = getTableExtracts();
		return tableExtracts;
	}


	/**
	 * Saves comment lines and log lines listed in Logs table listing to
	 * Voyager.comments.log and Voyager.extracts.log files
	 * 
	 * @param filePaths encapsulates start, end and extract file attributes
	 */
	public void saveLogExtractsToFiles(VoyagerPaths filePaths) {
		saveCommentExtracts(filePaths);
		saveLogExtracts(filePaths);
	}

	/**
	 * Compiles a list of current log table data
	 * 
	 * @return list of log table comments and selected log extracts
	 */
	public List<LogExtract> getTableExtracts() {
		Predicate<LogExtract> pComment = p -> p.isCommentType();
		Predicate<LogExtract> pSelected = p -> p.getMatcher().isSelected();
		var tableExtracts = this.allExtracts.stream()
				.filter(pComment.or(pSelected))
				.collect(Collectors.toList());
		return tableExtracts;
	}
	
	public List<LogExtract> getSelectedMetricExtracts() {
		Predicate<LogExtract> pMetric = p -> p.isMetricType();
		Predicate<LogExtract> pSelected = p -> p.getMatcher().isSelected();
		var metricExtracts = this.allExtracts.stream()
				.filter(pMetric.and(pSelected))
				.collect(Collectors.toList());
		return metricExtracts;	
	}
	
	/**
	 * Opens an input dialog for user to input a session-based comment. No comment is added if
	 * cancel pressed or empty comment line. 
	 */
	public void getUserComment() {
		var message = JOptionPane.showInputDialog(null, "Enter comment:");
		if ((message != null) && (message.trim().length() > 0)) {
			var pos = getCommentExtracts().size();
			allExtracts.add(pos, new LogExtract(message));
		}
	}
	
	/*
	 * Compiles a list of extract objects from input file lines containing matcher text. Input files:
	 * 
	 * <p>start+end Voyager.log files, where the end file is the next day Voyager log file</p>
	 * <p>Voyager.comments.log + Voyager.extracts.log files, containing user comments and 
	 * filtered Voyager log files respectively</p>
	 *  
	 * @param filePaths paths to start + end or comments + extracts files
	 */
	private void compileAllExtractsFromFile(VoyagerPaths filePaths) {
		var allExtracts = new ArrayList<LogExtract>();

		// reads entire file(s)
		var allLines = VoyagerFileReadWriter.readVoyagerLogFiles(filePaths);
		
		// text matchers array
		var matchers = logMatchers.getMatchers();
		
		// lines containing matcher matchText added to allExtracts array
		for (var line : allLines) {
			Optional<LogMatcher> optMatcher = matchers.stream()
					.filter(p -> p.matches(line))
					.findFirst();
			if (optMatcher.isPresent()) {
				var matcher = optMatcher.get();
				allExtracts.add(new LogExtract(line, matcher));
			}
		}
		this.allExtracts = allExtracts;
	}
	
	/*
	 * Saves log table comment lines to yyyy_mm_dd_Voyager.comments.log file in
	 * extracts sub folder
	 * 
	 * @param filePaths encapsulates file path data
	 */
	private void saveCommentExtracts(VoyagerPaths filePaths) {
		var commentExtracts = getCommentExtracts();
		var lines = commentExtracts.stream().map(p -> p.getLogLine()).collect(Collectors.toList());
		VoyagerFileReadWriter.writeCommentExtractsFile(lines, filePaths);
	}

	/*
	 * Saves user selected log extracts to yyyy_mm_dd_Voyager.extracts.log file in
	 * extracts sub folder
	 * 
	 * @param filePaths encapsulates file path data
	 */
	private void saveLogExtracts(VoyagerPaths filePaths) {
		var selectedExtracts = getSelectedExtracts();
		var lines = selectedExtracts.stream().map(p -> p.getLogLine()).collect(Collectors.toList());
		VoyagerFileReadWriter.writeLogExtractsFile(lines, filePaths);
	}

	public List<LogExtract> getAllExtracts() {
		return allExtracts;
	}

	public List<LogExtract> getSelectedExtracts() {
		var selectedExtracts = this.allExtracts.stream().filter(p -> p.isExtractType())
				.filter(p -> p.getMatcher().isSelected()).collect(Collectors.toList());
		return selectedExtracts;
	}
	

	public List<LogExtract> getCommentExtracts() {
		var commentExtracts = this.allExtracts.stream().filter(p -> p.isCommentType()).collect(Collectors.toList());
		return commentExtracts;
	}

	public static void main(String[] args) {

		// test files start, end extracts comments
		var startFile = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\2021_12_11_Voyager.extracts.log";
		// var endFile =
		// "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\2021_12_12_Voyager.extracts.log";
		var extractsFile = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\extracts\\2021_12_11_Voyager.extracts.log";
		// var commentsFile =
		// "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\extracts\\2021_12_11_Voyager.comments.log";

		// instantiate matchers extractor voyagerfile objects
		var logMatchers = new VoyagerLogMatchers();
		var extractor = new VoyagerLogExtractor(logMatchers);
		var filePaths = new VoyagerPaths();

		// assemble all extracts
		List<LogExtract> allExtracts = new ArrayList<>();
		List<LogExtract> commentExtracts = new ArrayList<>();
		List<String> allLines = new ArrayList<>();

		// start: 2021_12_11_Voyager.log
		// reads start and end log files
		filePaths.updateLogPaths(startFile);
		allLines = VoyagerFileReadWriter.readVoyagerLogFiles(filePaths);

		int n = 2;
		System.out.println(String.format("\nStart file lines: %d", allLines.size()));
		allLines.stream().limit(n).forEach(p -> System.out.println(p));
		System.out.println();
		allLines.stream().skip(allLines.size() - n).forEach(p -> System.out.println(p));

		// all extracts
		allExtracts.clear();
		var matchers = logMatchers.getMatchers();
		for (var line : allLines) {
			Optional<LogMatcher> optMatcher = matchers.stream().filter(p -> p.matches(line)).findFirst();
			if (optMatcher.isPresent()) {
				var matcher = optMatcher.get();
				allExtracts.add(new LogExtract(line, matcher));
			}
		}
		System.out.println(String.format("\nStart file extracts: %d", allExtracts.size()));
		allExtracts.stream().limit(n).forEach(p -> System.out.println(p.toString()));
		System.out.println();
		allExtracts.stream().skip(allExtracts.size() - n).forEach(p -> System.out.println(p.toString()));

		extractor.getTableExtractsFromFiles(filePaths);
		System.out.println(String.format("\nextractor allExtracts: %d", extractor.allExtracts.size()));
		extractor.allExtracts.stream().limit(n).forEach(p -> System.out.println(p.toString()));
		System.out.println();
		extractor.allExtracts.stream().skip(extractor.allExtracts.size() - n)
				.forEach(p -> System.out.println(p.toString()));

		// update selected extracts **********************************************************************************
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		var selectedExtracts = extractor.allExtracts.stream().filter(p -> p.getMatcher().isSelected())
				.collect(Collectors.toList());
		System.out.println(String.format("\nNo. of extracts, all matchers selected: %d", selectedExtracts.size()));

		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(false));
		selectedExtracts = extractor.allExtracts.stream().filter(p -> p.getMatcher().isSelected())
				.collect(Collectors.toList());
		System.out.println(String.format("No. of extracts, all matchers de-selected: %d", selectedExtracts.size()));

		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		System.out.println(
				String.format("No. of extracts, all matchers selected: %d", extractor.getSelectedExtracts().size()));

		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(false));
		System.out.println(
				String.format("No. of extracts, all matchers de-selected: %d", extractor.getSelectedExtracts().size()));

		// Extracts 2021_Voyager.extracts.log, 2021_12_11_Voyager.comments.log
		// reads comments and extracts files
		filePaths.updateExtractsPaths(extractsFile);
		allLines = VoyagerFileReadWriter.readVoyagerLogFiles(filePaths);

		System.out.println(String.format("\nExtracts & comments file lines: %d", allLines.size()));
		allLines.stream().limit(n).forEach(p -> System.out.println(p));
		System.out.println();
		allLines.stream().skip(allLines.size() - n).forEach(p -> System.out.println(p));

		// compile allExtracts = comments + log extracts
		extractor.getTableExtractsFromFiles(filePaths);
		System.out.println(String.format("\nextractor allExtracts: %d", extractor.allExtracts.size()));
		extractor.allExtracts.stream().limit(n).forEach(p -> System.out.println(p.toString()));
		System.out.println();
		extractor.allExtracts.stream().skip(extractor.allExtracts.size() - n)
				.forEach(p -> System.out.println(p.toString()));

		// select all log extracts
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		System.out.println(
				String.format("\nNo. of extracts, all matchers selected: %d", extractor.getSelectedExtracts().size()));

		commentExtracts = extractor.allExtracts.stream().filter(p -> p.isCommentType()).collect(Collectors.toList());
		System.out.println(String.format("No. of comment extracts: %d", commentExtracts.size()));
		System.out.println(String.format("No. of comment extracts: %d", extractor.getCommentExtracts().size()));

		// table extracts, toggle all selected / all de-selected
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		System.out.println("\nAll log extracts selected");
		System.out.println(String.format("No. of all extracts: %d", extractor.allExtracts.size()));
		System.out.println(String.format("No. of table extracts: %d", extractor.getTableExtracts().size()));

		// table extracts, toggle all selected / all de-selected
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(false));
		System.out.println("\nAll log extracts de-selected");
		System.out.println(String.format("No. of all extracts: %d", extractor.allExtracts.size()));
		System.out.println(String.format("No. of table extracts: %d", extractor.getTableExtracts().size()));

		// save selected extracts to extracts file
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		selectedExtracts = extractor.getSelectedExtracts();
		var selectedLines = selectedExtracts.stream().map(p -> p.getLogLine()).collect(Collectors.toList());
		System.out.println("\nAll lines selected:");
		selectedLines.stream().forEach(p -> System.out.println(p.toString()));

		commentExtracts = extractor.getCommentExtracts();
		var commentLines = commentExtracts.stream().map(p -> p.getLogLine()).collect(Collectors.toList());
		System.out.println("\nComment lines:");
		commentLines.stream().forEach(p -> System.out.println(p.toString()));

		extractor.saveLogExtracts(filePaths);
		extractor.saveCommentExtracts(filePaths);
		
		// status extracts and comments before adding new comment *********************************************
//		commentExtracts = extractor.getCommentExtracts();
//		commentLines = commentExtracts.stream().map(p -> p.getLogLine()).collect(Collectors.toList());
//		System.out.println("\nComment lines:");
//		commentLines.stream().forEach(p -> System.out.println(p.toString()));

		System.out.println("\nAll extracts before adding a new comment extract");
		
		allLines = allExtracts.stream().map(p -> p.getLogLine()).collect(Collectors.toList());
		System.out.println("\nFirst 10:");
		allLines.stream().limit(10).forEach(p -> System.out.println(p.toString()));
		System.out.println("\nlast 10:");
		allLines.stream().skip(allLines.size() - 10).forEach(p -> System.out.println(p.toString()));
		
		// extractor.allExtracts.stream().forEach(p  -> System.out.println(p.toString()));
		
		// metrics ***********************************************************************************************
		var metricsFile = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\2022_02_02_Voyager.log";
		filePaths.updateLogPaths(metricsFile);
		extractor.compileAllExtractsFromFile(filePaths);

		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		var selectedMetricExtracts = extractor.getSelectedMetricExtracts();
		System.out.println(selectedMetricExtracts.size());
		selectedMetricExtracts.stream().limit(10).forEach(p -> System.out.println(p.getLogLine()));
		
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(false));
		selectedMetricExtracts = extractor.getSelectedMetricExtracts();
		System.out.println(selectedMetricExtracts.size());
		selectedMetricExtracts.stream().limit(10).forEach(p -> System.out.println(p.getLogLine()));
		
		
		System.out.println("here");
	
		
		

	}
}
