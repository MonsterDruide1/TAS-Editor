package io.github.jadefalke2.util;

import io.github.jadefalke2.TAS;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {

	private File directory;
	private boolean darkTheme;
	private int lastStickPositionCount;
	private JoystickPanelPosition joystickPanelPosition; //FIXME change this without restarting?
	private SmoothTransitionType smoothTransitionType;


	private final Preferences backingPrefs;
	private final TAS parent;

	public Settings(Preferences prefs, TAS parent) {
		this.backingPrefs = prefs;
		this.parent = parent;

		setDirectory(new File(prefs.get("directory", System.getProperty("user.home"))));
		setDarkTheme(prefs.get("darkTheme", "false").equals("true"));
		setLastStickPositionCount(Integer.parseInt(prefs.get("lastStickPositionCount", "3")));
		setJoystickPanelPosition(JoystickPanelPosition.valueOf(prefs.get("joystickPanelPosition", "RIGHT")));
		setSmoothTransitionType(SmoothTransitionType.valueOf(prefs.get("smoothTransitionType", "ANGULAR_CLOSEST")));
	}

	public void storeSettings() throws BackingStoreException {
		backingPrefs.clear();

		backingPrefs.put("directory", directory + "");
		backingPrefs.put("darkTheme", darkTheme + "");
		backingPrefs.put("lastStickPositionCount", lastStickPositionCount + "");
		backingPrefs.put("joystickPanelPosition", joystickPanelPosition + "");
		backingPrefs.put("smoothTransitionType", smoothTransitionType + "");

		backingPrefs.flush();
	}


	public File getDirectory () {
		return directory;
	}

	public void setDirectory (File directory) {
		this.directory = directory;
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
		parent.recreateMainPanelWindowLayout();
	}

	public JoystickPanelPosition getJoystickPanelPosition(){
		return joystickPanelPosition;
	}

	public void setSmoothTransitionType(SmoothTransitionType smoothTransitionType){
		this.smoothTransitionType = smoothTransitionType;
	}

	public SmoothTransitionType getSmoothTransitionType(){
		return smoothTransitionType;
	}

	public enum JoystickPanelPosition {
		LEFT, RIGHT
	}
	public enum SmoothTransitionType {
		ANGULAR_CLOSEST, LINEAR, ANGULAR_CLOCKWISE, ANGULAR_COUNTERCLOCKWISE
	}
}
