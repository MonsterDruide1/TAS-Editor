package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.StickImagePanel;
import io.github.jadefalke2.StickPosition;

public class StickAction implements Action {

	private InputLine inputLine;
	private StickImagePanel.StickType stickType;
	private StickPosition oldPosition;
	private StickPosition newPosition;

	public StickAction(InputLine inputLine, StickImagePanel.StickType stickType, StickPosition oldPosition, StickPosition newPosition) {
		this.inputLine = inputLine;
		this.stickType = stickType;
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}

	@Override
	public void execute() {
		if (stickType == StickImagePanel.StickType.L_STICK) {
			inputLine.setStickL(newPosition);
		} else {
			inputLine.setStickR(newPosition);
		}
	}

	@Override
	public void revert() {
		if (stickType == StickImagePanel.StickType.L_STICK) {
			inputLine.setStickL(oldPosition);
		} else {
			inputLine.setStickR(oldPosition);
		}
	}
}
