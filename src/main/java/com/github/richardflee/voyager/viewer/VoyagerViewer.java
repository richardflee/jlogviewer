package com.github.richardflee.voyager.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.*;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyledDocument;

import com.github.richardflee.voyager.fileio.VoyagerPaths;
import com.github.richardflee.voyager.models.ExtractsTableModel;
import com.github.richardflee.voyager.models.MatchersTableModel;
import com.github.richardflee.voyager.models.MetricsTableModel;

public class VoyagerViewer extends JFrame {
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 1000;
	private static final int FRAME_HEIGHT = 600;

	private static final Integer[] EXTRACTS_COLUMN_WIDTHS = { 12, 88, 0 };
	private static final Integer[] MATCHER_COLUMN_WIDTHS = { 8, 40, 40, 12 };
	
	private static final String[] SELECTED_STATES = {"Select All", "Deselect All"};
	private static final String[] WARNINGS_METRIC_STATES = {"Select WARNINGS", "Select METRICS"};
	

	private ActionHandler handler = null;
	protected StyledDocument doc = null;

	protected MatchersTableModel matchersTableModel = null;
	private JTable matchersTable = null;

	protected ExtractsTableModel extractsTableModel = null;
	private JTable extractsTable = null;

	protected MetricsTableModel metricsTableModel = null;
	private JTable metricsTable = null;
	
	
	public VoyagerViewer(ActionHandler handler, 
			MatchersTableModel matchersTableModel, 
			ExtractsTableModel extractsTableModel, 	
			MetricsTableModel metricsTableModel) {

		initComponents();
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);

		this.handler = handler;

		this.matchersTableModel = matchersTableModel;
		this.matchersTable = new JTable(matchersTableModel);
		this.matchersTableScrollPane.setViewportView(matchersTable);

		this.extractsTableModel = extractsTableModel;
		this.extractsTable = new JTable(extractsTableModel);
		this.extractsTableScrollPane.setViewportView(extractsTable);

		this.metricsTableModel = metricsTableModel;
		this.metricsTable = new JTable(metricsTableModel);
		this.metricsTableScrollPane.setViewportView(metricsTable);

		configureTables();
		setupActionHandlers();

		saveExtractsFileButton.setEnabled(false);
		addCommentButton.setEnabled(false);
	}
	

	/*
	 * Sets up relative column widths and renderers 
	 */
	private void configureTables() {
		configureMatchersTable();
		configureExtractsTable();
		configureMetricsTable();		
	}
	
	private void configureMatchersTable() {
		this.matchersTable.setFillsViewportHeight(true);
		setColumnWidths(this.matchersTable, MATCHER_COLUMN_WIDTHS);
		this.matchersTable.getColumnModel().getColumn(3).setCellRenderer(new MatchersTypeColumnRenderer(matchersTableModel));
	}
	
	private void configureExtractsTable() {
		this.extractsTable.setFillsViewportHeight(true);
		setColumnWidths(this.extractsTable, EXTRACTS_COLUMN_WIDTHS);
		this.extractsTable.getColumnModel().getColumn(1).setCellRenderer(new LogTypeColumnRenderer(extractsTableModel));
	}
	
	// centre cell alignment & set ra (cl 6) and dec (col 7) to cyan / red in renderer 
	private void configureMetricsTable() {
		this.metricsTable.setFillsViewportHeight(true);
		this.metricsTable.setShowGrid(true);
		for (int col = 1; col < metricsTable.getColumnCount(); col++) {
			this.metricsTable.getColumnModel().getColumn(col).setCellRenderer(new MetricsColumnRenderer(metricsTableModel, col));			
		}
		
	}
	
	private void setColumnWidths(JTable table, Integer[] columnWidths) {
		
		var totalWidth = Arrays.asList(columnWidths).stream()
				.collect(Collectors.summingInt(Integer::intValue));
		
		var nCols = columnWidths.length;
		for (int i = 0; i < nCols; i++) {
			int scaledWidth = columnWidths[i] * FRAME_WIDTH / totalWidth;
			table.getColumnModel().getColumn(i).setPreferredWidth(scaledWidth);
		}
	}
	

	private void setupActionHandlers() {

		openLogFileButton.addActionListener(e -> {
			var dialogFile = VoyagerPaths.openLogFileDialog();
			this.handler.doImportFromVoyagerLogFile(dialogFile);
			this.updateExtractTableControls();
		});

		openExtractsFileButton.addActionListener(e -> {
			var dialogFile = VoyagerPaths.openExtractsFileDialog();
			handler.doImportFromExtractsFiles(dialogFile);
			this.updateExtractTableControls();
		});

		
		addCommentButton.addActionListener(e -> {
			this.handler.doAddCommentLine();
			this.updateExtractTableControls();
		});

		toggleSelectButton.addActionListener(e -> {
			boolean selectAll = (toggleSelectButton.getText().equalsIgnoreCase(SELECTED_STATES[0]));
			if (selectAll) {
				handler.doSelectAllMatchers(matchersTableModel);
				toggleSelectButton.setText(SELECTED_STATES[1]);
			} else {
				handler.doDeselectAllMatchers(matchersTableModel);
				toggleSelectButton.setText(SELECTED_STATES[0]);
			}
		});
		
		// toggles WARNINGS / METRICS matcher selections
		toggleWarningsButton.addActionListener(e -> {
			boolean selectWarnings = (toggleWarningsButton.getText().equalsIgnoreCase(WARNINGS_METRIC_STATES[0]));
			if (selectWarnings) {
				toggleWarningsButton.setText(WARNINGS_METRIC_STATES[1]);
				handler.doSelectWarningMatchers(matchersTableModel);
			} else {
				toggleWarningsButton.setText(WARNINGS_METRIC_STATES[0]);
				handler.doSelectMetricMatchers(matchersTableModel);
			}
		});
		
		plotMetricsFileButton.addActionListener(e -> this.handler.doPlotMetrics());
		

		// file save
		saveExtractsFileButton.addActionListener(e -> this.handler.doSaveLogExtractsToFile());
		saveTableDataButton.addActionListener(e -> this.handler.doSaveMatcherTableToFile());
		saveMetricsFileButton.addActionListener(e -> this.handler.doSaveMetricsToFile());

		viewerTabbedPane.addChangeListener(e -> {
			this.handler.doHandleTabChange(viewerTabbedPane.getSelectedIndex());
			updateExtractTableControls();
		});
	}
	
	private void updateExtractTableControls() {
		this.logFilesTextField.setText(VoyagerPaths.getLogFilesNames());
		var enabled = extractsTableModel.isPopulated();
		this.saveExtractsFileButton.setEnabled(enabled);
		this.addCommentButton.setEnabled(enabled);
		int nRows = extractsTableModel.getRowCount();
		this.linesTextField.setText(String.format("%d", nRows));
	}


	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		viewerTabbedPane = new JTabbedPane();
		logViewerPanel = new JPanel();
		panel1 = new JPanel();
		openLogFileButton = new JButton();
		openExtractsFileButton = new JButton();
		logFilesTextField = new JTextField();
		linesTextField = new JTextField();
		addCommentButton = new JButton();
		saveExtractsFileButton = new JButton();
		extractsTableScrollPane = new JScrollPane();
		matchersTablePanel = new JPanel();
		panel2 = new JPanel();
		toggleSelectButton = new JButton();
		toggleWarningsButton = new JButton();
		saveTableDataButton = new JButton();
		matchersTableScrollPane = new JScrollPane();
		metricsTablePanel = new JPanel();
		panel6 = new JPanel();
		plotMetricsFileButton = new JButton();
		saveMetricsFileButton = new JButton();
		metricsTableScrollPane = new JScrollPane();

		//======== this ========
		setTitle("DEMO!!");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{

				//======== viewerTabbedPane ========
				{

					//======== logViewerPanel ========
					{

						//======== panel1 ========
						{

							//---- openLogFileButton ----
							openLogFileButton.setText("Open Log File");

							//---- openExtractsFileButton ----
							openExtractsFileButton.setText("Open Extracts");

							//---- logFilesTextField ----
							logFilesTextField.setEditable(false);

							//---- linesTextField ----
							linesTextField.setEditable(false);
							linesTextField.setHorizontalAlignment(SwingConstants.CENTER);

							//---- addCommentButton ----
							addCommentButton.setText("Add Comment");

							//---- saveExtractsFileButton ----
							saveExtractsFileButton.setText("Save Extracts");

							GroupLayout panel1Layout = new GroupLayout(panel1);
							panel1.setLayout(panel1Layout);
							panel1Layout.setHorizontalGroup(
								panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addContainerGap()
										.addComponent(openLogFileButton, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(openExtractsFileButton, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(addCommentButton, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(saveExtractsFileButton, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
										.addComponent(linesTextField, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(logFilesTextField, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE)
										.addContainerGap())
							);
							panel1Layout.setVerticalGroup(
								panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addContainerGap()
										.addGroup(panel1Layout.createParallelGroup()
											.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(openExtractsFileButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
												.addComponent(openLogFileButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
												.addComponent(addCommentButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
												.addComponent(saveExtractsFileButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
											.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(logFilesTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(linesTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
										.addContainerGap())
							);
						}

						GroupLayout logViewerPanelLayout = new GroupLayout(logViewerPanel);
						logViewerPanel.setLayout(logViewerPanelLayout);
						logViewerPanelLayout.setHorizontalGroup(
							logViewerPanelLayout.createParallelGroup()
								.addGroup(logViewerPanelLayout.createSequentialGroup()
									.addContainerGap()
									.addGroup(logViewerPanelLayout.createParallelGroup()
										.addComponent(panel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(extractsTableScrollPane, GroupLayout.Alignment.TRAILING))
									.addContainerGap())
						);
						logViewerPanelLayout.setVerticalGroup(
							logViewerPanelLayout.createParallelGroup()
								.addGroup(logViewerPanelLayout.createSequentialGroup()
									.addComponent(extractsTableScrollPane, GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addContainerGap())
						);
					}
					viewerTabbedPane.addTab("Log Viewer Table", logViewerPanel);

					//======== matchersTablePanel ========
					{

						//======== panel2 ========
						{

							//---- toggleSelectButton ----
							toggleSelectButton.setText("Select All");

							//---- toggleWarningsButton ----
							toggleWarningsButton.setText("Select WARNINGS");

							//---- saveTableDataButton ----
							saveTableDataButton.setText("Save Table Data");

							GroupLayout panel2Layout = new GroupLayout(panel2);
							panel2.setLayout(panel2Layout);
							panel2Layout.setHorizontalGroup(
								panel2Layout.createParallelGroup()
									.addGroup(panel2Layout.createSequentialGroup()
										.addComponent(toggleSelectButton, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(toggleWarningsButton, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(saveTableDataButton, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
										.addContainerGap(454, Short.MAX_VALUE))
							);
							panel2Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {saveTableDataButton, toggleSelectButton, toggleWarningsButton});
							panel2Layout.setVerticalGroup(
								panel2Layout.createParallelGroup()
									.addGroup(panel2Layout.createSequentialGroup()
										.addContainerGap()
										.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(toggleSelectButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
											.addComponent(toggleWarningsButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
											.addComponent(saveTableDataButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
										.addContainerGap())
							);
						}

						GroupLayout matchersTablePanelLayout = new GroupLayout(matchersTablePanel);
						matchersTablePanel.setLayout(matchersTablePanelLayout);
						matchersTablePanelLayout.setHorizontalGroup(
							matchersTablePanelLayout.createParallelGroup()
								.addGroup(matchersTablePanelLayout.createSequentialGroup()
									.addContainerGap()
									.addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addContainerGap())
								.addComponent(matchersTableScrollPane, GroupLayout.DEFAULT_SIZE, 944, Short.MAX_VALUE)
						);
						matchersTablePanelLayout.setVerticalGroup(
							matchersTablePanelLayout.createParallelGroup()
								.addGroup(matchersTablePanelLayout.createSequentialGroup()
									.addComponent(matchersTableScrollPane, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addContainerGap())
						);
					}
					viewerTabbedPane.addTab(" Matchers Table", matchersTablePanel);

					//======== metricsTablePanel ========
					{

						//======== panel6 ========
						{

							//---- plotMetricsFileButton ----
							plotMetricsFileButton.setText("Plot Metrics");

							//---- saveMetricsFileButton ----
							saveMetricsFileButton.setText("Save Metrics File");

							GroupLayout panel6Layout = new GroupLayout(panel6);
							panel6.setLayout(panel6Layout);
							panel6Layout.setHorizontalGroup(
								panel6Layout.createParallelGroup()
									.addGroup(panel6Layout.createSequentialGroup()
										.addContainerGap()
										.addComponent(plotMetricsFileButton, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(saveMetricsFileButton, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
										.addContainerGap(664, Short.MAX_VALUE))
							);
							panel6Layout.setVerticalGroup(
								panel6Layout.createParallelGroup()
									.addGroup(panel6Layout.createSequentialGroup()
										.addContainerGap()
										.addGroup(panel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(plotMetricsFileButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
											.addComponent(saveMetricsFileButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
										.addContainerGap())
							);
						}

						GroupLayout metricsTablePanelLayout = new GroupLayout(metricsTablePanel);
						metricsTablePanel.setLayout(metricsTablePanelLayout);
						metricsTablePanelLayout.setHorizontalGroup(
							metricsTablePanelLayout.createParallelGroup()
								.addGroup(metricsTablePanelLayout.createSequentialGroup()
									.addContainerGap()
									.addComponent(panel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addContainerGap())
								.addComponent(metricsTableScrollPane, GroupLayout.DEFAULT_SIZE, 944, Short.MAX_VALUE)
						);
						metricsTablePanelLayout.setVerticalGroup(
							metricsTablePanelLayout.createParallelGroup()
								.addGroup(metricsTablePanelLayout.createSequentialGroup()
									.addComponent(metricsTableScrollPane, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(panel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addContainerGap())
						);
					}
					viewerTabbedPane.addTab("Session Metrics Table", metricsTablePanel);
				}

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(
					contentPanelLayout.createParallelGroup()
						.addComponent(viewerTabbedPane)
				);
				contentPanelLayout.setVerticalGroup(
					contentPanelLayout.createParallelGroup()
						.addComponent(viewerTabbedPane, GroupLayout.Alignment.TRAILING)
				);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JTabbedPane viewerTabbedPane;
	private JPanel logViewerPanel;
	private JPanel panel1;
	private JButton openLogFileButton;
	private JButton openExtractsFileButton;
	protected JTextField logFilesTextField;
	protected JTextField linesTextField;
	private JButton addCommentButton;
	private JButton saveExtractsFileButton;
	private JScrollPane extractsTableScrollPane;
	private JPanel matchersTablePanel;
	private JPanel panel2;
	private JButton toggleSelectButton;
	private JButton toggleWarningsButton;
	private JButton saveTableDataButton;
	private JScrollPane matchersTableScrollPane;
	private JPanel metricsTablePanel;
	private JPanel panel6;
	private JButton plotMetricsFileButton;
	private JButton saveMetricsFileButton;
	private JScrollPane metricsTableScrollPane;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}

