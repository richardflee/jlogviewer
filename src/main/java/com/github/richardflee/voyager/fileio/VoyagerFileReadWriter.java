package com.github.richardflee.voyager.fileio;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

import com.github.richardflee.voyager.utils.VoyagerDateTimes;

public class VoyagerFileReadWriter {

	// multi-line dialog eror text if VoyagerLogViewer.csv not found in working
	// folder
	private static String CSV_FILE_ERROR = """
			Failed to read Voyager csv file :
			 %s

			Download a copy of VoyagerLogViewer.csv from github repo:
			     https://github.com/richardflee/logviewer_for_voyager
			     
			and save in working folder:
			     %s
			""";

	/**
	 * This class method returns a list of text srings imported from
	 * VoyagerLogViewer.csv.
	 * <p> A multi-line dialog informs the user if csv file is not found with file
	 * download instructions </p>
	 * 
	 * @return text list comprising a set of comma-delimited log matching lines
	 */
	public static List<String> readLogMatchersFile() {
		var matchersPath = VoyagerPaths.getPathToMatchersFile();

		// streams VoyageLogViewer.csv to allLines list
		List<String> allLines = null;
		try (Stream<String> lines = Files.lines(matchersPath)) {
			allLines = lines.collect(Collectors.toList());
		} catch (IOException e) {
			var message = String.format(CSV_FILE_ERROR, matchersPath.toString(),
					Paths.get(System.getProperty("user.dir")));
			JOptionPane.showMessageDialog(null, message, "File Read", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
		return allLines;
	}

	/**
	 * Reads up to two Voyager log files, extracting time stamped log records
	 * between noon on the start and end dates.
	 * 
	 * @param logPaths list of Paths to the start date and (optional) end date log
	 *                 files
	 * @return text list of time stamped log records from noon on start day to next
	 *         day noon.
	 */
	public static List<String> readVoyagerLogFiles(VoyagerPaths filePaths) {
		// single or double loop, stream collects in range time stamp log lines to
		// allLines list
		LocalDateTime startDateTime = LocalDateTime.of(VoyagerPaths.getStartDate(), VoyagerDateTimes.NOON);

		var logPaths = filePaths.getLogPaths();
		List<String> allLines = new ArrayList<>();
		for (int i = 0; i < logPaths.size(); i++) {
			var path = logPaths.get(i);
			try (Stream<String> lines = Files.lines(path)) {
				// time stamp filter on stream
				var fileLines = lines.filter(p -> VoyagerFileReadWriter.isValidTimeStamp(p, startDateTime))
						.collect(Collectors.toList());
				allLines.addAll(fileLines);
			} catch (IOException e) {
				var message = String.format("Error reading Voyager log file:\n %s", logPaths.get(i).toString());
				JOptionPane.showMessageDialog(null, message, "File Read", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		return allLines;
	}

	/**
	 * Writes lines list containing matcher records to VoyagerLogViewer.csv file
	 * 
	 * @param lines   list of matcher records mapped to text line
	 * @param csvPath path to VoyagerLogViewer.csv file.
	 */
	public static void writeLogMatchersFile(List<String> lines) {
		var matchersPath = VoyagerPaths.getPathToMatchersFile();
		try (var pw = new PrintWriter(Files.newBufferedWriter(matchersPath))) {
			lines.stream().forEach(pw::println);
		} catch (IOException e) {
			var message = String.format("Error writing Voyager csv file:\n %s",
					matchersPath.toAbsolutePath().toString());
			JOptionPane.showMessageDialog(null, message, "File Write", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		var message = String.format("Saved matchers table date to:\n %s", matchersPath.toAbsolutePath().toString());
		JOptionPane.showMessageDialog(null, message, "File Save", JOptionPane.INFORMATION_MESSAGE);
	}

	
	/**
	 * Class method writes a list of user comments to Voyagers.comments.log file
	 * 
	 * @param lines text list user comments
	 * @param filePaths encapsulates path to Voyager files
	 */
	public static void writeCommentExtractsFile(List<String> lines, VoyagerPaths filePaths) {
		var commentsPath = filePaths.getCommentsFileAttr().getPath();
		if (commentsPath == null) {
			return;
		}
		try (var pw = new PrintWriter(Files.newBufferedWriter(commentsPath), true)) {
			lines.stream().forEach(pw::println);
		} catch (IOException e) {
			var message = String.format("Error writing Voyager comments file:\n %s",
					commentsPath.toAbsolutePath().toString());
			JOptionPane.showMessageDialog(null, message, "File Write", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * Class method writes comma-delimited metrics data to Voyagers.metrics.csv file
	 * 
	 * @param lines comma-delimited text list of session metrics data 
	 * @param filePaths encapsulates path to Voyager files
	 */
	public static void writeSelectedMetricsFile(List<String> lines, VoyagerPaths filePaths) {
		var metricsPath = filePaths.getMetricsFileAttr().getPath();
		
		try (var pw = new PrintWriter(Files.newBufferedWriter(metricsPath), true)) {
			lines.stream().forEach(pw::println);
		} catch (IOException e) {
			var message = String.format("Error writing Voyager metrics file:\n %s",
					metricsPath.toAbsolutePath().toString());
			JOptionPane.showMessageDialog(null, message, "File Write", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// dialog confirms file write
		var message = String.format("Saved metrics data to:\n %s", metricsPath.toAbsolutePath().toString());
		JOptionPane.showMessageDialog(null, message, "File Save", JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	/**
	 * Class method writes a list of Voyager log lines to Voyagers.extracts.log file
	 * 
	 * @param lines text list extracted from Voyager.log file
	 * @param filePaths encapsulates path to Voyager files
	 */
	public static void writeLogExtractsFile(List<String> lines, VoyagerPaths filePaths) {
		var extractsPath = filePaths.getExtractsFileAttr().getPath();
		try (var pw = new PrintWriter(Files.newBufferedWriter(extractsPath), true)) {
			lines.stream().forEach(pw::println);
		} catch (IOException e) {
			var message = String.format("Error writing Voyager extracts file:\n %s",
					extractsPath.toAbsolutePath().toString());
			JOptionPane.showMessageDialog(null, message, "File Write", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}


	/*
	 * Computes date time from Voyager log line. Throws DateTimeException exception
	 * if time stamp not found
	 * 
	 * @param line current Voyager log line
	 * 
	 * @return localdatetime conversion of log line time stamp
	 * 
	 * @throws DateTimeException unchecked exception time stamp format is valid:
	 * yyyy/MM/dd HH:mm:ss
	 * 
	 * @throws ArrayIndexOutOfBoundsException unchecked exception if less than 2
	 * space delimiters found
	 */
	private static LocalDateTime getTimeStamp(String line) throws Exception {
		// split line on space delimiter, throws ArrayIndexOutOfBoundsException
		// if tokens array less than 3 elements
		String[] tokens = line.split(" ");
		String strDate = tokens[0].trim();
		String strTime = tokens[1].trim();
		double nano = Double.valueOf(tokens[2].trim()) * 1e6;

		// attempt to convert tokens array elements to localdatetime,
		// throws DateTimeException if conversion is unsuccessful
		LocalDateTime ldt = LocalDateTime.parse(strDate + " " + strTime,
				DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

		// returns time stamp localdatetime appended with nano-sec
		return ldt.plusNanos((long) nano);
	}

	/*
	 * Tests if current line is time stamped up to 1 day after start date noon
	 * 
	 * @param line current Voyager log file line
	 * 
	 * @return true if current line is time stamped up to 1 day after start date
	 * noon, false otherwise
	 */
	private static boolean isValidTimeStamp(String line, LocalDateTime startDateTime) {
		line = line.trim();

		// attempts to read leading time stamp, returns false if time conversion fails
		LocalDateTime currentTime = null;
		try {
			currentTime = VoyagerFileReadWriter.getTimeStamp(line);
		} catch (Exception e) {
			return false;
		}
		// returns true if current time is between noon on start and next day
		var b1 = (currentTime.isAfter(startDateTime));
		var b2 = (currentTime.isBefore(startDateTime.plusDays(1)));
		return b1 && b2;
	}
		
	public static void main(String[] args) {
		String[] data = { "0", "1" };
		var lines0 = VoyagerFileReadWriter.readLogMatchersFile();
		for (int i = 0; i <= data.length - 1; i++) {
			String idx = data[i];
			var lines1 = new ArrayList<String>();
			for (var line : lines0) {
				lines1.add(String.format("%s%s", idx, line.substring(line.indexOf(","), line.length())));
			}
			lines1.remove(0);

			VoyagerFileReadWriter.writeLogMatchersFile(lines1);
			var lines2 = VoyagerFileReadWriter.readLogMatchersFile();
			boolean b2 = true;
			for (var line : lines2) {
				b2 = b2 && line.startsWith(idx);
				b2 = b2 && line.indexOf(",") == 1;
			}
			System.out.println(b2);
		}
	}

}
