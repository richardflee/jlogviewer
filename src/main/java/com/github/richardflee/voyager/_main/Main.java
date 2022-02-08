package com.github.richardflee.voyager._main;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.github.richardflee.voyager.log_objects.VoyagerLogMatchers;
import com.github.richardflee.voyager.models.ExtractsTableModel;
import com.github.richardflee.voyager.models.MatchersTableModel;
import com.github.richardflee.voyager.models.MetricsTableModel;
import com.github.richardflee.voyager.viewer.ActionHandler;
import com.github.richardflee.voyager.viewer.VoyagerViewer;

/**
 * 
 * Main class for JVoyagerLogViewer program to read, view and save selected
 * abstracts from Voyager session logs
 *
 */
public class Main {

	private final static String LOGGER_TITLE = "Voyager Log Viewer";
	private final static String LOGGER_VERSION = "SNAPSHOT-1.00c";
	private final static String VOYAGER_VERSION = "Voyager-2.3.4a";

		private static void runApp() {
			// references to Swing JTable models
			var matchersModel = new MatchersTableModel();
			var extractsModel = new ExtractsTableModel();
			var metricsModel = new MetricsTableModel();

			// imports log matching lines listed in VoyagerLogViewer.csv and updates matcher table
			var logsMatcher = new VoyagerLogMatchers();
			matchersModel.updateTable(logsMatcher.getMatchers());
			
			// sets up viewer event handler, sets ltm as listener to updateTable events 
			var handler = new ActionHandler(logsMatcher);
			handler.setTableListeners(extractsModel, metricsModel);

			// configures viewer ui
			var view = new VoyagerViewer(handler, matchersModel, extractsModel, metricsModel);		

			// window title text
			var version = String.format("%s - %s :: %s", LOGGER_TITLE, LOGGER_VERSION, VOYAGER_VERSION); 
			view.setTitle(version);

			// show ui
			view.setVisible(true);
		}

	public static void main(String[] args) {
		try {
			// dashing flat laf dark theme
			UIManager.setLookAndFeel(new FlatDarkLaf());
			UIManager.put("TabbedPane.showTabSeparators", true);
			UIManager.put("OptionPane.minimumSize",new Dimension(500,80)); 
		} catch (Exception ex) {
			System.err.println("Failed to initialize LaF");
		}
		
		// runs app in EDT (event dispatching thread)
			EventQueue.invokeLater(() -> {
				runApp();
			});
	}
}
