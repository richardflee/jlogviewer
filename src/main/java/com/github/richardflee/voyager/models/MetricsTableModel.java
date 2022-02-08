package com.github.richardflee.voyager.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.github.richardflee.voyager.log_objects.LogMetric;

/**
 * Model for displaying metrics data extracted from Voyager log files. The table is a non-editable text table.
 */
public class MetricsTableModel extends AbstractTableModel implements MetricsTableListener {
	private static final long serialVersionUID = 1L;

	// data set
	private List<LogMetric> tableRows;

	// header column names
	public final static String HEADERS[] = { 
			"Time Stamp",
			"Filter", "HFD", "Temp", "Pos", "Time",
			"Ra", "Dec",
			"Pointing"
	};
	
	public MetricsTableModel() {
		tableRows = new ArrayList<>();
	}
	
	/**
	 * Returns true if metrics table contains data, false if table is empty
	 */
	public boolean isPopulated() {
		return tableRows.size() > 0;
	}

	// updates table model data with new LogMetrics list
	@Override
	public void updateTable(List<LogMetric> currentTableRows) {
		// repeat delete top row (index 0) until table is empty
		// and fire RowsDeleted notification
		int LastRow = tableRows.size();
		while (tableRows.size() > 0) {
			tableRows.remove(0);
		}
		fireTableRowsDeleted(0, LastRow);
		
		// refill table rows, fire RowsInserted notification after each addition
		if (currentTableRows != null) {
			int idx = 0;
			for (var tableRow : currentTableRows) {
				addItem(idx, tableRow);
				idx++;
			}
		}
	}
	
	private void addItem(int idx, LogMetric tableRow) {
		tableRows.add(idx, tableRow);
		fireTableRowsInserted(idx, idx);
	}
	
	@Override
	public int getRowCount() {
		return  tableRows.size();
	}

	@Override
	public int getColumnCount() {
		return HEADERS.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		var objectRow = tableRows.get(rowIndex);
		Object data = null;
		switch (columnIndex) {
		case 0:
			data = (String) objectRow.getTimeStamp();
			break;
		case 1:
			data = (String) objectRow.getFocusFilter();
			break;
		case 2:
			data = (String) objectRow.getFocusHfd();
			break;
		case 3:
			data = (String) objectRow.getFocusTemperature();
			break;
		case 4:
			data = (String) objectRow.getFocusPos();
			break;
		case 5:
			data = (String) objectRow.getFocusTime();
			break;
		case 6:
			data = (String) objectRow.getGuidingRa();
			break;
		case 7:
			data = (String) objectRow.getGuidingDec();
			break;
		case 8:
			data = (String) objectRow.getSlewPointing();
			break;			
		}
		return data;
	}

	@Override
	public String getColumnName(int column) {
		return HEADERS[column];
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		return String.class;
	}
}
