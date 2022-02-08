package com.github.richardflee.voyager.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.github.richardflee.voyager.log_objects.LogExtract;

/**
 * Model for displaying Voyager log data in log table tab; the table is a non-editable text table.
 * 
 */

public class ExtractsTableModel extends AbstractTableModel implements ExtractsTableListener {
	private static final long serialVersionUID = 1L;

	private List<LogExtract> tableRows;

	// header column names
	private final static String headers[] = { "Time Stamp", "Log Message", "Type" };

	public ExtractsTableModel() {
		tableRows = new ArrayList<>();
	}
	

	/**
	 * Returns true is log table is not empty, false otherwise
	 * 
	 * @return true is log table is not empty, false otherwise
	 */
	public boolean isPopulated() {
		return tableRows.size() > 0;
	}

	// updates table model data with new LogExtracts list
	@Override
	public void updateTable(List<LogExtract> currentTableRows) {
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
	
	private void addItem(int idx, LogExtract tableRow) {
		tableRows.add(idx, tableRow);
		fireTableRowsInserted(idx, idx);
	}
	
	@Override
	public int getRowCount() {
		return  tableRows.size();
	}

	@Override
	public int getColumnCount() {
		return headers.length;
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
			data = (String) objectRow.getMessageLine();
			break;
		case 2:
			data = (String) objectRow.getMessageType();
			break;
		}
		return data;
	}

//	@Override
//	public void setValueAt(Object value, int rowIndex, int columnIndex) {
//		// placeholder => data model not editable
//	}
	
	@Override
	public String getColumnName(int column) {
		return headers[column];
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

