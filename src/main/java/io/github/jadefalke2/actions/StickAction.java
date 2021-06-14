package io.github.jadefalke2.actions;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.stickRelatedClasses.JoystickPanel;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;

import java.util.Arrays;

public class StickAction implements Action {

	private final Script script;
	private final int[] rows;
	private final JoystickPanel.StickType stickType;
	private final StickPosition oldPosition;
	private final StickPosition newPosition;

	public StickAction(Script script, int[] rows, JoystickPanel.StickType stickType, StickPosition oldPosition, StickPosition newPosition) {
		this.script = script;
		this.rows = rows;
		this.stickType = stickType;
		this.oldPosition = oldPosition; //FIXME probably has bugs when editing multiple rows at once
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

		for (int row : rows) {
			script.setStickPos(row, stickType, position);
		}

	}

	@Override
	public String toString() {
		return "Stick Action, at frames: " + Arrays.toString(rows) + "; " + stickType + "; new Stickpostion:" + newPosition;
	}
}
