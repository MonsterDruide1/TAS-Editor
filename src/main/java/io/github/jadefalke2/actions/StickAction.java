package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.stickRelatedClasses.JoystickPanel;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;

import java.util.Arrays;

public class StickAction implements Action {

	private final Script script;
	private final int[] rows;
	private final JoystickPanel.StickType stickType;
	private final StickPosition[] newPositions;
	private final StickPosition[] oldPositions;

	public StickAction(Script script, int[] rows, JoystickPanel.StickType stickType, StickPosition[] newPositions) {
		this.script = script;
		this.rows = rows;
		this.stickType = stickType;
		this.newPositions = newPositions;
		oldPositions = Arrays.stream(rows).mapToObj(script::getLine)
			.map(stickType == JoystickPanel.StickType.L_STICK ? InputLine::getStickL : InputLine::getStickR)
			.toArray(StickPosition[]::new);
	}
	public StickAction(Script script, int[] rows, JoystickPanel.StickType stickType, StickPosition newPosition) {
		this(script, rows, stickType, Arrays.stream(rows).mapToObj(i -> newPosition).toArray(StickPosition[]::new));
	}

	@Override
	public void execute() {
		for (int i=0; i<rows.length; i++) {
			script.setStickPos(rows[i], stickType, newPositions[i]);
		}
	}

	@Override
	public void revert() {
		for(int i=0;i<rows.length;i++){
			script.setStickPos(rows[i], stickType, oldPositions[i]);
		}
	}

	@Override
	public String toString() {
		return "Stick Action, at frames: " + Arrays.toString(rows) + "; " + stickType + "; new Stickpostions:" + Arrays.toString(newPositions);
	}
}
