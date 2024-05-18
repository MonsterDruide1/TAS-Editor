package io.github.jadefalke2.util;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import io.github.jadefalke2.Script;

public class ScriptTableModel extends AbstractTableModel implements ScriptObserver {

	private final Script script;

	public ScriptTableModel(Script script) {
		this.script = script;
		script.attachObserver(this);
	}

	@Override
	public void onDirtyChange(boolean dirty) {
		fireTableChanged(new TableModelEvent(this, 0, getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
	}

	@Override
	public void onLengthChange(int length) {
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return script.getNumLines();
	}

	@Override
	public int getColumnCount() {
		return Button.values().length + 3;  // frame, left stick, right stick
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
			case 0:
				return "Frame";
			case 1:
				return "L-Stick";
			case 2:
				return "R-Stick";
			default:
				return Button.values()[column - 3].toString();
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
			case 0:
				return rowIndex;
			case 1:
				return script.getLine(rowIndex).getStickL().toCartString();
			case 2:
				return script.getLine(rowIndex).getStickR().toCartString();
			default:
				return script.getLine(rowIndex).buttons.contains(Button.values()[columnIndex - 3]) ? Button.values()[columnIndex - 3].toString() : "";
		}
	}
}
