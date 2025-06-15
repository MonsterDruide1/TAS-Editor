package io.github.jadefalke2.util;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

public class CustomPianoRollCellRenderer extends DefaultTableCellRenderer {

	public CustomPianoRollCellRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(
		JTable table, Object value, boolean isSelected,
		boolean hasFocus, int row, int column) {
		// wrap in HTML to enable HTML rendering, which disables ellipsis when text is too long
		return super.getTableCellRendererComponent(
			table, "<html>"+value+"</html>", isSelected, hasFocus, row, column);
	}
}
