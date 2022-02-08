package com.github.richardflee.voyager.log_objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.richardflee.voyager.enums.MatchersTypeEnum;
import com.github.richardflee.voyager.fileio.VoyagerFileReadWriter;
import com.github.richardflee.voyager.fileio.VoyagerPaths;
import com.github.richardflee.voyager.models.MetricsTableModel;

/**
 * Extracts focus, guiding and pointing data from Voyager log files 
 *
 */
public class LogMetric {
	// field terms embedded in Voyager log text	
	private static final String[] FOCUS_FIELDS = { "FILTER", "HFD", "TEMPERATURE", "POS", "TIME"};
	private static final String[] GUIDING_FIELDS = {"RA", "DEC"};
	private static final String[] POINTING_TERMS = {"[DMS]", "00° 00' 00\""};
	
	// log line timestamp
	private String timeStamp = "";
	
	// focus parameters	
	private String focusFilter = "";
	private String focusHfd = "";
	private String focusTemperature = "";
	private String focusPos = "";
	private String focusTime = "";
	
	// rms guiding errors (PHD2)
	private String guidingRa = "";
	private String guidingDec = "";
	
	// residual pointing error after closed loop slew
	private String slewPointing = "";
	
	
	public LogMetric(LogExtract extract) {
		getFieldData(extract);
	}

	/**
	 * Compiles a list of metrics data extracted from Voyager log file
	 * 
	 * @param selectedMetricExtracts user selected list of log extracts encapsulating metrics data   
	 * @return metrics data list
	 */
	public static List<LogMetric> getSelectedMetrics(List<LogExtract> selectedMetricExtracts) {
		List<LogMetric> metrics = new ArrayList<>();
		for (var ex : selectedMetricExtracts) {
			metrics.add(new LogMetric(ex));
		}
		return metrics;
	}
	
	public static void saveSelectedMetrics(List<LogMetric> selectedMetrics, VoyagerPaths filePaths) {
		var header = Arrays.asList(MetricsTableModel.HEADERS).stream().collect(Collectors.joining(","));
		var lines = selectedMetrics.stream().map(p -> p.toString()).collect(Collectors.toList());
		lines.add(0, header);
		VoyagerFileReadWriter.writeSelectedMetricsFile(lines, filePaths);
	}
	
	/**
	 * Extracts and copies metrics data to object fields for current line  message type
	 */
	private void getFieldData(LogExtract extract) {
		// common to all metric types
		this.timeStamp = extract.getTimeStamp();
		var messageLine = extract.getMessageLine();
		
		// switch based on extract.matcher type METRIC_X
		var en = MatchersTypeEnum.getEnum(extract.getMessageType());
		Map<String, String> map = null;
		switch (en) {
		case METRIC_F:
			map = getFocusData(messageLine);
			this.focusFilter = map.get("FILTER");
			this.focusHfd = map.get("HFD");
			this.focusTemperature =  map.get("TEMPERATURE");
			this.focusPos = map.get("POS");
			this.focusTime = map.get("TIME");
			break;
			
		case METRIC_G:
			map = getGuidingData(messageLine);
			this.guidingRa = map.get("RA");
			this.guidingDec = map.get("DEC");
			break;
			
		case METRIC_P:
			this.slewPointing = getPointingdata(messageLine);
			break;
		
		default:
			// no action
		}
	}
	
	// decodes line for FOCUS_FIELDS terms
	private Map<String, String> getFocusData(String line) {
		var map = getData(line, FOCUS_FIELDS);
		// hfd 2 decimal places
		var hfd = map.get("HFD");
		map.put("HFD", String.format("%.2f" , Double.valueOf(hfd)));
		return map;
	}
	
	// decodes line for GUIDING_FIELDS terms
	private Map<String, String> getGuidingData(String line) {
		var map = getData(line, GUIDING_FIELDS);
		var ra = map.get("RA");
		var dec = map.get("DEC");
		// ra,dec 2 decimal places
		map.put("RA", String.format("%.2f" , Double.valueOf(ra)));
		map.put("DEC", String.format("%.2f" , Double.valueOf(dec)));
		return map;
	}
	
	// extracts pointing data relative to '[DMS]' marker
	private String getPointingdata(String line) {
		int endIdx = line.indexOf(POINTING_TERMS[0]);
		int startIdx = endIdx - POINTING_TERMS[1].length();
		return line.substring(startIdx, endIdx);
	}
	
	/*
	 * Extracts map items specified in keys array 
	 */
	private Map<String, String> getData(String line, String[] keys) {
		Map<String, String> map = new HashMap<>(lineSplitter(line));
		map.keySet().retainAll(Arrays.asList(keys));
		return map;
	}
	
	/*
	 * Splits line on spaces then splits segments on '=' into key-val map:
	 *  XXX k1=v1 XXX k2=v2 .. => map(k1, v1), map(k2, v2) ..
	 */
	private Map<String, String> lineSplitter(String line) {
		// replaces open-close bracket with a space
		line = line.replace("(", " ").replace(")", " ");
		
		// returns map with key-value pairs split on '='
		return Arrays.stream(line.split(" "))					// split line on space delimiter
				.filter(p -> p.contains("="))					// split groups containing '=' 
				.map(p -> p.split("="))							// map & collect split groups to key/value pairs
				.collect(Collectors.toMap(p -> p[0].toUpperCase(), p -> p[1]));   // convert keys to upper case
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}


	public String getFocusFilter() {
		return focusFilter;
	}


	public String getFocusHfd() {
		return focusHfd;
	}


	public String getFocusTemperature() {
		return focusTemperature;
	}


	public String getFocusPos() {
		return focusPos;
	}


	public String getFocusTime() {
		return focusTime;
	}


	public String getGuidingRa() {
		return guidingRa;
	}


	public String getGuidingDec() {
		return guidingDec;
	}


	public String getSlewPointing() {
		return slewPointing;
	}

	// compiles a comma-delimted string to save to csv file format
	@Override
	public String toString() {
		var s = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
				timeStamp,
				focusFilter , focusHfd, focusTemperature, focusPos, focusTime, 
				guidingRa, guidingDec,
				slewPointing);
		return s;
	}


	public static void main(String[] args) {
		
		var metricsFile = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\2022_02_02_Voyager.log";
		var vp = new VoyagerPaths();
		
		var logMatchers = new VoyagerLogMatchers();
		var extractor = new VoyagerLogExtractor(logMatchers);
		
		vp.updateLogPaths(metricsFile);
		
		extractor.getTableExtractsFromFiles(vp);

		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		var selectedMetricExtracts = extractor.getSelectedMetricExtracts();
		
		//System.out.println(selectedMetricExtracts.size());
//		selectedMetricExtracts.stream().limit(10).forEach(p -> System.out.println(p.getLogLine()));
		
	
		var s1 = "2022/02/03 00:44:11 272 - INFO  - [Focus  ] - [FINISH_Code ] - Focus Done - Pos=33734 HFD=6.428524 Star(X,Y)=1671.419 - 1250.466 Temperature=6.4 Focus Time=01:57 Filter=R [3] ColoreRGBFiltro=Color [A=255, R=255, G=0, B=0]";
		var s2 = "2022/02/03 00:27:07 334 - INFO  - [Sequence - Safety Run  ] - [EsposizioneOK  ] - GUIDING Stats - RMS Error (RA=0.664 - DEC=0.656)";
		var s3 = "2022/02/03 01:26:08 818 - INFO  - [PrecisePointing        ] - [CHECK_POINTING_ERROR_Code] - For your info the Best Performance obtained from your Mount in this pointing is 00° 00' 02\"[DMS]";
		//var s4 = "2022/02/03 00:26:20 648 - INFO      - [Sequence - Safety Run        ] - [EsposizioneOK                                ] - GUIDING Stats - RMS Error (RA=0.776 - DEC=0.476)";
		
		var focusMatcher = logMatchers.getMatchers().stream()
				.filter(p -> p.getMessageType().equalsIgnoreCase("METRIC_F"))
				.findFirst().get();
		
		var guidingMatcher = logMatchers.getMatchers().stream()
				.filter(p -> p.getMessageType().equalsIgnoreCase("METRIC_G"))
				.findFirst().get();
		
		var pointingMatcher = logMatchers.getMatchers().stream()
				.filter(p -> p.getMessageType().equalsIgnoreCase("METRIC_P"))
				.findFirst().get();
		
		var extract = new LogExtract(s1, focusMatcher);
		
		extract = new LogExtract(s2, guidingMatcher);
		var metric = new LogMetric(extract);
		System.out.println(metric.toString());
		
		extract = new LogExtract(s3, pointingMatcher);
	//	metric = new LogMetric(extract);
		//System.out.println(metric.toString());
		
//		List<Metric> x = new ArrayList<>();
//		for (var ex : selectedMetricExtracts) {
//			x.add(new Metric(ex));
//		}
		
		List<LogMetric> x = LogMetric.getSelectedMetrics(selectedMetricExtracts);
		
		System.out.println(x.size());
		x.stream().limit(10).forEach(p -> System.out.println(p.toString()));
		
		var y = Arrays.asList(MetricsTableModel.HEADERS).stream().collect(Collectors.joining(","));
		System.out.println(y);
				
	}

}
