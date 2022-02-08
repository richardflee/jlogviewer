package com.github.richardflee.voyager.viewer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.richardflee.voyager.enums.MatchersTypeEnum;
import com.github.richardflee.voyager.models.MatchersTableModel;

/**
 * Formats colours in matchesTable message type column based on column 0
 * checked/unchecked state.
 */
public class MatchersColumnRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	private MatchersTableModel model = null;

	public MatchersColumnRenderer(MatchersTableModel model) {
		this.model = model;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		var cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		setHorizontalAlignment(SwingConstants.CENTER);

		// if selected is checked, sets column 3 to message type colour
		// otherwise sets column colour light grey
		var en = MatchersTypeEnum.getEnum((String) value);
		var rowSelected = (Boolean) model.getValueAt(row, 0);
		var color = (rowSelected) ? en.getColor() : Color.LIGHT_GRAY;
		cellComponent.setForeground(color);
		return cellComponent;
	}
}

