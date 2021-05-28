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

	public StickAction(InputLine[] inputLines, JoystickPanel.StickType stickType, StickPosition oldPosition, StickPosition newPosition, TableModel table) {
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
			if (stickType == JoystickPanel.StickType.L_STICK) {
				i.setStickL(position);
				table.setValueAt(i.getStickL().toCartString(), i.getFrame(), 1);
			} else {
				i.setStickR(position);
				table.setValueAt(i.getStickR().toCartString(), i.getFrame(), 2);
			}
		}

	}
}
