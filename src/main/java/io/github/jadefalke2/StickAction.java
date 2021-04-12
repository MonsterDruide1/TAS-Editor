package io.github.jadefalke2;

public class StickAction implements Action {

	InputLine inputLine;
	StickImagePanel.StickType stickType;
	StickPosition oldPosition;
	StickPosition newPosition;

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
