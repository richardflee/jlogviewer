package com.github.richardflee.voyager.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.github.richardflee.voyager.log_objects.LogMatcher;

/**
 * Model for Matchers table data. Follows a 'standard' pattern for Swing table models, extending AbstractTableModel, 
 * view of list of matchers table data.
 * 
 *  <p>Column 0 is boolean with check boxes to select / de-select individual matcher text lines</p>
 */
public class MatchersTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	// dataset
	private List<LogMatcher> tableRows;

	// header column names
	private final static String headers[] = { "Use", "Matching Text", "Preset Message", "Message Type" };

	public MatchersTableModel() {
		tableRows = new ArrayList<>();
	}

	// updates table model data with new matchers list
	public void updateTable(List<LogMatcher> currentTableRows) {
		// clears table and fires RowsDeleted notification
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
	
	private void addItem(int idx, LogMatcher tableRow) {
		tableRows.add(idx, tableRow);
		fireTableRowsInserted(idx, idx);
	}

	@Override
	public int getRowCount() {
		return tableRows.size();
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	/**
	 * Returns data value for matchers row = rowIndex; columnIndex points to object field
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		LogMatcher objectRow = tableRows.get(rowIndex);
		Object data = null;
		switch (columnIndex) {
		case 0:		// isSelected
			data = (Boolean) objectRow.isSelected();
			break;
		case 1:		// match text
			data = (String) objectRow.getMatchText();
			break;
		case 2:		// optional preset message text
			data = (String) objectRow.getPresetText();
			break;
		case 3:		// message type, IFO, CRITICAL etc
			data = (String) objectRow.getMessageType();
		}
		return data;
	}

	// user can select or de-select column 0 boolean check box
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			// extract active row, toggle selected flag and update aperture id column
			var objectRow = tableRows.get(rowIndex);
			var isSelected = (Boolean) getValueAt(rowIndex, 0);
			objectRow.setSelected(!isSelected);
		}
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	}

	/**
	 * Sets column 0 checkbox as editable cell
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return (column == 0) && (row > 0);
	}

	/**
	 * sets column class to bolean for column 0, String for other columns 
	 */
	@Override
	public Class<?> getColumnClass(int column) {
		return (column == 0) ? Boolean.class : String.class;
	}

}
