package com.github.richardflee.voyager.fileio;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.github.richardflee.voyager.utils.VoyagerDateTimes;

/**
 * This class manages os paths to Voyager data files.
 */
public class VoyagerPaths {

	// Voyager filename templates
	private static final String MATCHERS_CSV_FILENAME = "VoyagerLogViewer.csv";
	private static final String VOYAGER_FILE_STUB = "_Voyager.log";
	private static final String EXTRACTS_FILE_STUB = "_Voyager.extracts.log";
	private static final String COMMENTS_FILE_STUB = "_Voyager.comments.log";
	private static final String METRICS_FILE_STUB = "_Voyager.metrics.csv";

	public static final File LOGS_FOLDER = new File(System.getProperty("user.dir"), "log");
	private static final File EXTRACTS_FOLDER = new File(LOGS_FOLDER.toString(), "extracts");

	// fields
	private VoyagerFileAttributes startFileAttr = null;
	private VoyagerFileAttributes endFileAttr = null;
	private VoyagerFileAttributes extractsFileAttr = null;
	private VoyagerFileAttributes commentsFileAttr = null;
	private VoyagerFileAttributes metricsFileAttr = null;
	private List<Path> logPaths = null;

	// class fields
	private static String logFilesNames = "";
	private static LocalDate startDate = LocalDate.now();

	// file dialog filter settings
	private static Map<String, String> map = null;

	public VoyagerPaths() {
		// sets initial field values
		this.startFileAttr = new VoyagerFileAttributes(null);
		this.endFileAttr = new VoyagerFileAttributes(null);
		this.extractsFileAttr = new VoyagerFileAttributes(null);
		this.commentsFileAttr = new VoyagerFileAttributes(null);
		this.metricsFileAttr = new VoyagerFileAttributes(null);

		this.logPaths = new ArrayList<>();

		// sets dialog filter settings
		map = new HashMap<>();
		map.put("accept", "");
		map.put("description", "");
		
		// if needed, creates new xtracts folder
		if (EXTRACTS_FOLDER.mkdirs()) {
			var message = String.format("Created log extracts folder:\n %s", EXTRACTS_FOLDER.toString());
			JOptionPane.showMessageDialog(null, message, "Extracts Folder", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Creates file paths derived from user selected yyyy_mm_dd_VoyagerXXXXX.log
	 * file. Example filenames:
	 * 
	 * <p>
	 * Dialog file: 2021_12_04_VoyagerAdvanced.log
	 * </p>
	 * <p>
	 * Start session input file: 2021_12_04_Voyager.log
	 * </p>
	 * <p>
	 * End session input file: 2021_12_05_Voyager.log
	 * </p>
	 * <p>
	 * Log extracts output file: 2021_12_04_Voyager.extracts.log
	 * </p>
	 * 
	 * @param dialogFile full path to user-selected log file, example format:
	 *                   2021_12_04_VoyagerAdvanced.log.
	 * @return true if valid Voyager log file selected in file chooser dialg, false
	 *         otherwise
	 */
	public boolean updateLogPaths(String dialogLogFile) {
		// update fails if user cancel file chooser or selects invalid date format log
		// file
		if (validDialogFilename(dialogLogFile) == false) {
			return false;
		}
		// reformats [date]_VoyagerAdvanced etc to [date]_Voyager.log
		var dialogStartPath = getDialogPath(dialogLogFile, VOYAGER_FILE_STUB);

		// path to selected log file & sets session start date
		this.startFileAttr.updatePath(dialogStartPath);
		VoyagerPaths.startDate = getFileDate(dialogStartPath);

		// derived paths to next day and extracts log files, skips comments log file
		this.endFileAttr.updatePath(derivedEndPath());
		this.extractsFileAttr.updatePath(derivedExtractsPath(EXTRACTS_FILE_STUB));
		this.metricsFileAttr.updatePath(derivedExtractsPath(METRICS_FILE_STUB));
		this.commentsFileAttr.updatePath(null);

		// assemble list of Voyager log file paths to import
		this.logPaths.clear();
		logPaths.add(startFileAttr.getPath());
		if (endFileAttr.isExists() == true) {
			logPaths.add(endFileAttr.getPath());
		}

		// static class file name(s)
		VoyagerPaths.logFilesNames = (endFileAttr.getFilename().length() == 0) ? startFileAttr.getFilename()
				: startFileAttr.getFilename() + " + " + endFileAttr.getFilename();
		return true;
	}

	/**
	 * Creates file path to user selected extracts file in extracts subfolder
	 * 
	 * @param dialogExtractsFile text path to selected extracts file
	 * @return true if valid extracts file selected, false otherwise
	 */
	public boolean updateExtractsPaths(String dialogExtractsFile) {
		// update fails if user cancel file chooser or selects invalid date format
		// extracts file
		if (validDialogFilename(dialogExtractsFile) == false) {
			return false;
		}
		// path to selected extracts file & sets session start date
		var dialogPath = getDialogPath(dialogExtractsFile, EXTRACTS_FILE_STUB);
		this.extractsFileAttr.updatePath(dialogPath);
		VoyagerPaths.startDate = getFileDate(dialogPath);
		
		dialogPath = getDialogPath(dialogExtractsFile, COMMENTS_FILE_STUB);
		this.commentsFileAttr.updatePath(dialogPath);
		
		dialogPath = getDialogPath(dialogExtractsFile, METRICS_FILE_STUB);
		this.metricsFileAttr.updatePath(dialogPath);
		
		// single item list with path to selected extracts file
		this.logPaths.clear();
		if (commentsFileAttr.isExists()) { 
			logPaths.add(commentsFileAttr.getPath());
		}
		logPaths.add(extractsFileAttr.getPath());

		// static class file name
		VoyagerPaths.logFilesNames = extractsFileAttr.getFilename();
		return true;
	}

	/**
	 * Extracts date from Voyager log file with filename format
	 * yyyy_mm_dd_Voyager.log
	 * 
	 * @param filePath path to input file with embedded date stamp format yyyy_MM_dd
	 * @return LocalDate for embedded date stamp
	 * @throws DateTimeParseException embedded date pattern yyyy_MM_dd not found
	 */
	public static LocalDate getFileDate(Path filePath) throws DateTimeParseException {
		var strStartDate = filePath.getFileName().toString().substring(0, VoyagerDateTimes.FILE_PREFIXDATE_PATTERN.length());
		return LocalDate.parse(strStartDate, VoyagerDateTimes.FILE_PREFIXDATE_FORMATTER);
	}

	/**
	 * Path to log matchers data file.
	 * 
	 * @return full path to VoyagerLogViewer.csv file
	 */
	public static Path getPathToMatchersFile() {
		return Paths.get(System.getProperty("user.dir"), MATCHERS_CSV_FILENAME);
	}

	/**
	 * Opens a java open file dialog, configured for *.log files in default folder
	 * user.dir\\log
	 * 
	 * @return full path to selected log file, or empty string if cancel pressed
	 */
	public static String openLogFileDialog() {
		JFileChooser jfc = new JFileChooser(LOGS_FOLDER);
		jfc.setDialogTitle("Select Voyager Log file");

		// constrain dialog to display Voyager.log files
		map.put("accept", "Voyager.log");
		map.put("description", "Voyager.log files");

		var dialogFile = runFileDialog(jfc);
		return dialogFile;
	}


	/**
	 * Opens a java open file dialog, configured for *.log files in extracts folder
	 * user.dir\\log\\extracts
	 * 
	 * @return full path to selected extracts log file, or empty string if cancel
	 *         pressed
	 */
	public static String openExtractsFileDialog() {
		JFileChooser jfc = new JFileChooser(EXTRACTS_FOLDER);
		jfc.setDialogTitle("Select Voyager Log Extracts file");
		
		// constrain dialog to display Voyager.extracts.log files
		map.put("accept", "Voyager.extracts.log");
		map.put("description", "Voyager.extracts.log files");

		var dialogFile = runFileDialog(jfc);
		return dialogFile;
	}

	/*
	 * JFileChooser configuration common to log and extracts dialog
	 */
	private static String runFileDialog(JFileChooser jfc) {
		// configures file chooser to accept either Voyager.log or Voyager.extracts.log files
		var logFileFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (file.getName().endsWith(map.get("accept")));
			}

			@Override
			public String getDescription() {
				return map.get("description");
			}
		};		
		jfc.addChoosableFileFilter(logFileFilter);
		jfc.setAcceptAllFileFilterUsed(false);
		
		var dialogFile = "";
		if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			dialogFile = jfc.getSelectedFile().getAbsoluteFile().toString();
		}
		return dialogFile.trim();
	}


	/*
	 * Checks if cancel pressed and if filename format is valid
	 * 
	 * @return true is valid Voyager log or extracts filename selected
	 */
	private boolean validDialogFilename(String dialogFile) {
		return (!(userCancelled(dialogFile)) && (validDatePrefix(dialogFile)));
	}

	// zero length string => cancel pressed
	private boolean userCancelled(String dialogFile) {
		return (dialogFile.length() == 0);
	}

	/*
	 * Verifies date prefix is int the correct format yyyy_mm_dd
	 */
	private boolean validDatePrefix(String dialogFile) {
		try {
			VoyagerPaths.getFileDate(Paths.get(dialogFile));
		} catch (DateTimeParseException dte) {
			// invalid log or extracts filename
			var filename = Paths.get(dialogFile).getFileName().toString();
			var message = String.format("Log file has invalid Voyager date format:\n %s", filename);
			JOptionPane.showMessageDialog(null, message, "Voyager Log Files", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}

	/*
	 * Appends fileStub to file date: Voyager log file: yyyy_mm_dd_Voyager.log
	 * Extracts file: yyyy_mm_ddVoyager.extracts.log
	 */
	private Path getDialogPath(String dialogFile, String fileStub) {
		Path path = Paths.get(dialogFile);
		var date = VoyagerPaths.getFileDate(path);
		var strDate = date.format(VoyagerDateTimes.FILE_PREFIXDATE_FORMATTER);
		// prepends date yyyy_mm_dd to stub _Voyager.log
		return path.getParent().resolve(strDate + fileStub);
	}

	/*
	 * Returns path to next day Voyager log file
	 */
	private Path derivedEndPath() {
		var path = this.startFileAttr.getPath();
		var date = getStartDate().plusDays(1);
		var strDate = date.format(VoyagerDateTimes.FILE_PREFIXDATE_FORMATTER);
		return path.getParent().resolve(strDate + VoyagerPaths.VOYAGER_FILE_STUB);
	}

	/**
	 * Returns path to log extracts file
	 */
	private Path derivedExtractsPath(String fileStub) {
		var date = getStartDate();
		var strDate = date.format(VoyagerDateTimes.FILE_PREFIXDATE_FORMATTER);
		return EXTRACTS_FOLDER.toPath().resolve(strDate + fileStub);
	}

	/**
	 * Returns session start date
	 */
	public static LocalDate getStartDate() {
		return VoyagerPaths.startDate;
	}

	public VoyagerFileAttributes getStartFileAttr() {
		return startFileAttr;
	}

	public VoyagerFileAttributes getEndFileAttr() {
		return endFileAttr;
	}

	public VoyagerFileAttributes getExtractsFileAttr() {
		return extractsFileAttr;
	}

	public VoyagerFileAttributes getMetricsFileAttr() {
		return metricsFileAttr;
	}
	
	public List<Path> getLogPaths() {
		return logPaths;
	}

	public static String getLogFilesNames() {
		return logFilesNames;
	}


	public VoyagerFileAttributes getCommentsFileAttr() {
		return commentsFileAttr;
	}

	public static void main(String[] args) {

		var matcherPath = VoyagerPaths.getPathToMatchersFile();
		System.out.println(String.format("Path to VoyagerViewer.csv %s:", matcherPath.toAbsolutePath().toString()));
		var vfp = new VoyagerPaths();
		
		// summary file paths test **********************************************************************************
		//start 2021_12_11
		System.out.println("\nTest updateLogPaths 2021_12_11 *****************************************************");
		
		
		var startFile = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\2021_12_11_Voyager.log";
		var extractsFile = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\extracts\\2021_12_11_Voyager.extracts.log";
		
		vfp.updateLogPaths(startFile);
		System.out.println("\nStart date 2021_12_11:");
		System.out.println(String.format("Start path:       %b =>  %s",
						vfp.startFileAttr.isExists(), vfp.getStartFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println(String.format("End path:         %b =>  %s",
				vfp.endFileAttr.isExists(), vfp.getEndFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println(String.format("Metrics path:     %b =>  %s",
				vfp.metricsFileAttr.isExists(), vfp.getMetricsFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println("\n Log file paths 2021_12_11:");
		for (int i = 0; i < vfp.getLogPaths().size(); i++) {
			var s = vfp.getLogPaths().get(i).toString();
			System.out.println(String.format("Log path %d: %s", i, s));
		}

		// extracts 2021_12_11s*******************
		System.out.println("\nTest updateExtractsPaths 2021_12_11 *****************************************************");
		vfp.updateExtractsPaths(extractsFile);		
		System.out.println("\nExtracts 2021_12_11:");
		System.out.println(String.format("Extracts path:    %b =>  %s",
				vfp.extractsFileAttr.isExists(), vfp.getExtractsFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println(String.format("Comments path:    %b =>  %s",
				vfp.commentsFileAttr.isExists(), vfp.getCommentsFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println(String.format("Metrics path:     %b =>  %s",
				vfp.metricsFileAttr.isExists(), vfp.getMetricsFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println("\n extracts file paths 2021_12_11:");
		for (int i = 0; i < vfp.getLogPaths().size(); i++) {
			var s = vfp.getLogPaths().get(i).toString();
			System.out.println(String.format("Log path %d: %s", i, s));
		}
		
		
		//start 2021_12_12
		System.out.println("\nTest updateLogPaths 2021_12_12 *****************************************************");
		
		startFile = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\2021_12_12_Voyager.log";
		extractsFile = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\extracts\\2021_12_12_Voyager.extracts.log";
		
		vfp.updateLogPaths(startFile);
		System.out.println("\nStart date 2021_12_12:");
		System.out.println(String.format("Start path:       %b =>  %s",
						vfp.startFileAttr.isExists(), vfp.getStartFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println(String.format("End path:         %b =>  %s",
				vfp.endFileAttr.isExists(), vfp.getEndFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println(String.format("Metrics path:     %b =>  %s",
				vfp.metricsFileAttr.isExists(), vfp.getMetricsFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println("\n Log file paths 2021_12_12:");
		for (int i = 0; i < vfp.getLogPaths().size(); i++) {
			var s = vfp.getLogPaths().get(i).toString();
			System.out.println(String.format("Log path %d: %s", i, s));
		}
		
		// extracts 2021_12_12 *******************
		System.out.println("\nTest updateExtractsPaths 2021_12_12 *****************************************************");
		vfp.updateExtractsPaths(extractsFile);		
		System.out.println("\nExtracts 2021_12_12:");
		System.out.println(String.format("Extracts path:    %b =>  %s",
				vfp.extractsFileAttr.isExists(), vfp.getExtractsFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println(String.format("Comments path:    %b =>  %s",
				vfp.commentsFileAttr.isExists(), vfp.getCommentsFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println(String.format("Metrics path:     %b =>  %s",
				vfp.metricsFileAttr.isExists(), vfp.getMetricsFileAttr().getPath().toAbsolutePath().toString()));
		
		System.out.println("\n extracts file paths 2021_12_12:");
		for (int i = 0; i < vfp.getLogPaths().size(); i++) {
			var s = vfp.getLogPaths().get(i).toString();
			System.out.println(String.format("Log path %d: %s", i, s));
		}
		
		

		
		
	}

}
