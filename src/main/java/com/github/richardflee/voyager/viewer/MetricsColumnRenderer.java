package com.github.richardflee.voyager.viewer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.richardflee.voyager.models.MetricsTableModel;

public class MetricsColumnRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	public static final Color LIGHT_BLUE = new Color(51, 204, 255);
	public static final Color LIGHT_RED = new Color(255, 102, 102);

	// current table column number 
	private int col = 0;
	
	public MetricsColumnRenderer(MetricsTableModel model, int col) {
		this.col = col;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		var cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		// field columns text centred
		setHorizontalAlignment(SwingConstants.CENTER);
		
		// sets ra, dec column foreground colours
		var color = Color.LIGHT_GRAY;
		if (col == 6) {
			color = LIGHT_BLUE;
		} else if (col == 7) {
			color = LIGHT_RED;
		}
		cellComponent.setForeground(color);
		return cellComponent;
	}

	

}
