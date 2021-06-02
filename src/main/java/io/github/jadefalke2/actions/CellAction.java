package io.github.jadefalke2.actions;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.Button;

import javax.swing.table.TableModel;

public class CellAction implements Action {

	private final TableModel table;
	private final Script script;
	private final int row;
	private final int col;
	private String colName;

	private boolean remove;

	public CellAction(TableModel table, Script script, int row, int col) {
		this.table = table;
		this.script = script;
		this.row = row;
		this.col = col;
	}

	@Override
	public void execute() {
		colName = table.getColumnName(col);
		Button buttonTriggered = Button.valueOf("KEY_" + colName);

		if (table.getValueAt(row, col).equals("")) {
			remove = false;
			table.setValueAt(table.getColumnName(col), row, col);
			script.getInputLines().get(row).buttons.add(buttonTriggered);

		} else if (table.getValueAt(row, col).equals(colName)) {

			remove = true;
			table.setValueAt("", row, col);
			script.getInputLines().get(row).buttons.remove(buttonTriggered);

		}

	}

	@Override
	public void revert() {
		// The action toggles the cell, so reverting it is the same as executing it
		execute();
	}

	@Override
	public String toString() {
		return "Cell Action, at frame: " + row + "; " + (remove ? "removing " : "inputing ") + colName;
	}

}
