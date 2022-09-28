package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;

public class InsertEmptyLineAction implements Action {

	private final Script script;
	private final int position;
	private final int amount;

	public InsertEmptyLineAction(Script script, int position, int amount) {
		this.script = script;
		this.position = position;
		this.amount = amount;
	}

	@Override
	public void execute() {
		for (int i = 0; i < amount; i++){
			int actualIndex = position + i;
			script.insertRow(actualIndex, InputLine.getEmpty());
		}
	}

	@Override
	public void revert() {
		for (int i = 0; i < amount; i++){
			script.removeRow(position);
		}
	}

	@Override
	public String toString() {
		return "InsertEmptyLineAction for "+amount+" frames at frame "+position;
	}
}
