package io.github.jadefalke2;

import javax.swing.table.TableModel;

public class CellAction implements Action {

	TableModel table;
	Script script;
	int row;
	int col;

	public CellAction(TableModel table, Script script, int row, int col) {
		this.table = table;
		this.script = script;
		this.row = row;
		this.col = col;
	}

	@Override
	public void execute() {
		if (table.getValueAt(row,col).equals(" ")) {
			table.setValueAt(table.getColumnName(col), row, col);

			script.getInputLines().get(row).buttonsEncoded.add(table.getColumnName(col));

		}else if (table.getValueAt(row,col).equals(table.getColumnName(col))){

			table.setValueAt(" ",row,col);
			script.getInputLines().get(row).buttonsEncoded.remove(table.getColumnName(col));
		}
	}

	@Override
	public void revert() {
		// The action toggles the cell, so reverting it is the same as executing it
		execute();
	}

}
