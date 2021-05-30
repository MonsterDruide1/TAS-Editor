package io.github.jadefalke2.actions;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.components.PianoRoll;
import io.github.jadefalke2.util.Button;

import javax.swing.table.TableModel;

public class CellAction implements Action {

	private final PianoRoll pianoRoll;
	private final TableModel tableModel;
	private final Script script;
	private final int row;
	private final int col;

	public CellAction(PianoRoll pianoRoll, Script script, int row, int col) {
		this.pianoRoll = pianoRoll;
		this.tableModel = pianoRoll.getModel();
		this.script = script;
		this.row = row;
		this.col = col;
	}

	@Override
	public void execute() {
		String colName = tableModel.getColumnName(col);
		Button buttonTriggered = Button.valueOf("KEY_" + colName);

		if (pianoRoll.isCellEmpty(row,col)) {

			tableModel.setValueAt(tableModel.getColumnName(col), row, col);
			script.getInputLines().get(row).buttons.add(buttonTriggered);

		} else if (tableModel.getValueAt(row, col).equals(colName)) {

			tableModel.setValueAt("-", row, col);
			script.getInputLines().get(row).buttons.remove(buttonTriggered);

		}

	}

	@Override
	public void revert() {
		// The action toggles the cell, so reverting it is the same as executing it
		execute();
	}

}
