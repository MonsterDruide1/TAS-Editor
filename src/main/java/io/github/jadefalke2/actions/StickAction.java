package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.stickRelatedClasses.StickImagePanel;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;

import javax.swing.table.DefaultTableModel;

public class StickAction implements Action {

	private final InputLine[] inputLines;
	private final StickImagePanel.StickType stickType;
	private final StickPosition oldPosition;
	private final StickPosition newPosition;
	private final DefaultTableModel table;
	private final int row;

	public StickAction(InputLine[] inputLines, StickImagePanel.StickType stickType, StickPosition oldPosition, StickPosition newPosition, DefaultTableModel table, int row) {
		this.row = row;
		this.table = table;
		this.inputLines = inputLines;
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

		for (InputLine i: inputLines) {
			if (stickType == StickImagePanel.StickType.L_STICK) {
				i.setStickL(position);
				table.setValueAt(i.getStickL().toCartString(), row, 1);
			} else {
				i.setStickR(position);
				table.setValueAt(i.getStickR().toCartString(), row, 2);
			}
		}

	}
}
