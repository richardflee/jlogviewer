package com.github.richardflee.voyager.viewer;

import com.github.richardflee.voyager.fileio.VoyagerPaths;
import com.github.richardflee.voyager.log_objects.LogMetric;
import com.github.richardflee.voyager.log_objects.VoyagerLogExtractor;
import com.github.richardflee.voyager.log_objects.VoyagerLogMatchers;
import com.github.richardflee.voyager.models.ExtractsTableListener;
import com.github.richardflee.voyager.models.MatchersTableModel;
import com.github.richardflee.voyager.models.MetricsTableListener;

/**
 * This class handles button click and other events fired in viewer ui
 */

public class ActionHandler {

	// Voyager log file paths
	private VoyagerPaths voyagerFilePaths = null;
	
	// encapsulates log matching lines listed in VoyagerLogViewer.csv
	private VoyagerLogMatchers logsMatcher = null;
	
	// encapsulates lines imported from Voyager log files 
	private VoyagerLogExtractor extractor = null;
	
	// reference to listener to update log table events
	private ExtractsTableListener extractsTableListener = null;
	private MetricsTableListener metricsTableListener = null;
	

	// create file and log objects
	public ActionHandler(VoyagerLogMatchers logsMatcher) {
		this.voyagerFilePaths = new VoyagerPaths();
		this.logsMatcher = logsMatcher;
		this.extractor = new VoyagerLogExtractor(logsMatcher);
	}

	public void setTableListeners(ExtractsTableListener extractsTableListener, MetricsTableListener metricsTableListener) {
		this.extractsTableListener = extractsTableListener;
		this.metricsTableListener = metricsTableListener;
	}

	/**
	 * Populates log table with time-stamped log lines from user-selected Voyager log file , taken 
	 * over 24 hrs from start date noon to next day noon. 
	 * Data is filtered against matcher lines, 
	 */
	public void doImportFromVoyagerLogFile(String dialogFile) {
		// populates log table if valid log filename selected
		if (this.voyagerFilePaths.updateLogPaths(dialogFile)) {
			var tableExtracts = this.extractor.getTableExtractsFromFiles(this.voyagerFilePaths);
			this.extractsTableListener.updateTable(tableExtracts);
		}
	}
	
	/**
	 * Populates log table with data from user selected extracts file
	 */
	public void doImportFromExtractsFiles(String dialogFile) {
		// populates log table if valid extracts filename selected
		if (this.voyagerFilePaths.updateExtractsPaths(dialogFile)) {
			var tableExtracts = this.extractor.getTableExtractsFromFiles(this.voyagerFilePaths);
			this.extractsTableListener.updateTable(tableExtracts);
		}
	}
	
	
	/**
	 * Adds a single user comment 
	 */
	public void doAddCommentLine() {
		extractor.getUserComment();
		doHandleTabChange(0); 
	}
	
	/**
	 * Saves current log table data to extracts file
	 */
	public void doSaveLogExtractsToFile() {
		extractor.saveLogExtractsToFiles(voyagerFilePaths);
	}
	
	public void doSaveMetricsToFile() {
		var metricExtracts = extractor.getSelectedMetricExtracts();
		var selectedMetrics = LogMetric.getSelectedMetrics(metricExtracts);
		LogMetric.saveSelectedMetrics(selectedMetrics, voyagerFilePaths);
	}
	

	/**
	 * Updates log extracts table when log table tab is selected
	 * 
	 * @param selectedIndex index of selected tab pane; logs extract table is tab 0.
	 */
	public void doHandleTabChange(int selectedIndex) {
		// extracts table tab
		if (selectedIndex == 0) {
			var tableExtracts = extractor.getTableExtracts();
			this.extractsTableListener.updateTable(tableExtracts);
		// metrics table tab
		} else if (selectedIndex == 2) {
			var metricExtracts = extractor.getSelectedMetricExtracts();
			var metrics = LogMetric.getSelectedMetrics(metricExtracts);
			this.metricsTableListener.updateTable(metrics);
		}
	}

	/**
	 * Checks all matchers table use column check boxes  
	 */
	public void doSelectAllMatchers(MatchersTableModel model) {
		var matchers = this.logsMatcher.getMatchers();
		matchers.stream().forEach(p -> p.setSelected(true));
		model.updateTable(matchers);
	}

	/**
	 * Clears all matchers table use column check boxes  
	 */
	public void doDeselectAllMatchers(MatchersTableModel model) {
		var matchers = this.logsMatcher.getMatchers();
		matchers.stream().filter(p -> p.isCommentMatcher() == false).forEach(p -> p.setSelected(false));
		model.updateTable(matchers);
	}
	
	
	public void doSelectWarningMatchers(MatchersTableModel model) {
		var matchers = this.logsMatcher.getMatchers();
		matchers.stream()
			.filter(p -> p.isCommentMatcher() == false)
			.forEach(p -> p.setSelected(p.isWarningMatcher()));
		model.updateTable(matchers);
	}
	
	
	public void doSelectMetricMatchers(MatchersTableModel model) {
		var matchers = this.logsMatcher.getMatchers();
		matchers.stream()
			.filter(p -> p.isCommentMatcher() == false)
			.forEach(p -> p.setSelected(p.isMetricMatcher()));
		model.updateTable(matchers);
	}
	
	/**
	 * Saves selected matcher table lines to VoyagerLogViewer.csv
	 */
	public void doSaveMatcherTableToFile() {
		this.logsMatcher.saveMatchersToFile();
	}
	
	public void doPlotMetrics() {
		System.out.println("Metrics plotter!!");
		
		// TTDO plotter
		
		
	}

}

