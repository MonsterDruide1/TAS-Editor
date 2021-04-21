package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.stickRelatedClasses.StickImagePanel;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;

import javax.swing.table.DefaultTableModel;

public class StickAction implements Action {

	private final InputLine inputLine;
	private final StickImagePanel.StickType stickType;
	private final StickPosition oldPosition;
	private final StickPosition newPosition;
	private final DefaultTableModel table;
	private final int row;

	public StickAction(InputLine inputLine, StickImagePanel.StickType stickType, StickPosition oldPosition, StickPosition newPosition, DefaultTableModel table, int row) {
		this.row = row;
		this.table = table;
		this.inputLine = inputLine;
		this.stickType = stickType;
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}

	@Override
	public void execute() {
		setPosition(newPosition);
	}

	@Override
	public void revert() {
		setPosition(oldPosition);
	}

	private void setPosition(StickPosition position) {

		if (stickType == StickImagePanel.StickType.L_STICK) {
			inputLine.setStickL(position);
			table.setValueAt(inputLine.getStickL().toString(),row, 1);
		} else {
			inputLine.setStickR(position);
			table.setValueAt(inputLine.getStickR().toString(),row,2);
		}

	}
}
