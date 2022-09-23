package io.github.jadefalke2.actions;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.Button;

public class ButtonAction implements Action {

	private final Script script;
	private final Button button;
	private final boolean enabled;
	private final int start, end;
	private boolean[] oldValues;

	public ButtonAction(Script script, Button button, boolean enabled, int start, int end) {
		this.script = script;
		this.button = button;
		this.enabled = enabled;
		this.start = start;
		this.end = end;
	}

	@Override
	public void execute() {
		oldValues = new boolean[end-start+1];
		for(int i=0; i<oldValues.length; i++) {
			oldValues[i] = script.getLine(start+i).buttons.contains(button);
		}

		for(int i=start; i<=end; i++) {
			script.setButton(i, button, enabled);
		}
	}

	@Override
	public void revert() {
		for(int i=start; i<=end; i++) {
			script.setButton(i, button, oldValues[i-start]);
		}
	}

	@Override
	public String toString() {
		return "Button Action, at frames: "+end+"-"+start+"; "+button+"="+enabled;
	}
}
