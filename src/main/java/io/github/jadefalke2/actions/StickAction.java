package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.stickRelatedClasses.JoystickPanel;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class StickAction implements Action {

	private final InputLine[] inputLines;
	private final JoystickPanel.StickType stickType;
	private final StickPosition oldPosition;
	private final StickPosition newPosition;
	private final TableModel table;
	private final int[] rows;

	public StickAction(InputLine[] inputLines, JoystickPanel.StickType stickType, StickPosition oldPosition, StickPosition newPosition, TableModel table, int[] rows) {
		this.table = table;
		this.inputLines = inputLines;
		this.stickType = stickType;
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
		this.rows = rows;
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

		for (int i=0;i<inputLines.length;i++) {
			if (stickType == JoystickPanel.StickType.L_STICK) {
				inputLines[i].setStickL(position);
				table.setValueAt(inputLines[i].getStickL().toCartString(), rows[i], 1);
			} else {
				inputLines[i].setStickR(position);
				table.setValueAt(inputLines[i].getStickR().toCartString(), rows[i], 2);
			}
		}

	}
}
