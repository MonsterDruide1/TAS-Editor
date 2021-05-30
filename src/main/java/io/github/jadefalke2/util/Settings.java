package io.github.jadefalke2.util;

import io.github.jadefalke2.TAS;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {

	private boolean darkTheme;
	private int lastStickPositionCount;
	private JoystickPanelPosition joystickPanelPosition; //FIXME change this without restarting?


	private final Preferences backingPrefs;
	private final TAS parent;

	public Settings(Preferences prefs, TAS parent) {
		this.backingPrefs = prefs;
		this.parent = parent;

		setDarkTheme(prefs.get("darkTheme", "false").equals("true"));
		setLastStickPositionCount(Integer.parseInt(prefs.get("lastStickPositionCount", "3")));
		setJoystickPanelPosition(JoystickPanelPosition.valueOf(prefs.get("joystickPanelPosition", "RIGHT")));
	}

	public void storeSettings() throws BackingStoreException {
		backingPrefs.clear();

		backingPrefs.put("darkTheme", darkTheme+"");
		backingPrefs.put("lastStickPositionCount", lastStickPositionCount+"");
		backingPrefs.put("joystickPanelPosition", joystickPanelPosition+"");

		backingPrefs.flush();
	}


	public boolean isDarkTheme() {
		return darkTheme;
	}

	public void setDarkTheme(boolean darkTheme) {
		this.darkTheme = darkTheme;
		parent.setLookAndFeel(darkTheme);
	}

	public int getLastStickPositionCount() {
		return lastStickPositionCount;
	}

	public void setLastStickPositionCount(int lastStickPositionCount) {
		this.lastStickPositionCount = lastStickPositionCount;
	}

	public void setJoystickPanelPosition(JoystickPanelPosition joystickPanelPosition){
		this.joystickPanelPosition = joystickPanelPosition;
	}

	public JoystickPanelPosition getJoystickPanelPosition(){
		return joystickPanelPosition;
	}

	public enum JoystickPanelPosition {
		LEFT, RIGHT;
	}
}
