package com.github.richardflee.voyager.viewer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.richardflee.voyager.enums.MatchersTypeEnum;
import com.github.richardflee.voyager.models.ExtractsTableModel;

public class ExtractsColumnRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private ExtractsTableModel model = null;

	public ExtractsColumnRenderer(ExtractsTableModel model) {
		this.model = model;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		var cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	
		var s = (String) model.getValueAt(row, 2);
		var en = MatchersTypeEnum.getEnum(s);
		var color = en.getColor();
		cellComponent.setForeground(color);
		
		return cellComponent;
	}
}

