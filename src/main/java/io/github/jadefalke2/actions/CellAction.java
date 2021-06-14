package io.github.jadefalke2.actions;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.Button;

import javax.swing.table.TableModel;

public class CellAction implements Action {

	private final Script script;
	private final int row;
	private final Button button;

	private boolean remove;

	public CellAction(Script script, int row, int col) {
		this.script = script;
		this.row = row;
		this.button = Button.values()[col-3]; //TODO find a better way than -3
	}

	@Override
	public void execute() {
		script.toggleButton(row, button);

		remove = !script.getLine(row).buttons.contains(button);
	}

	@Override
	public void revert() {
		// The action toggles the cell, so reverting it is the same as executing it
		execute();
	}

	@Override
	public String toString() {
		return "Cell Action at frame: " + row + "; " + (remove ? "removing " : "inputing ") + button;
	}

}
