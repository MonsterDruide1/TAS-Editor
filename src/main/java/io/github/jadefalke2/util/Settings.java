package io.github.jadefalke2.util;

import io.github.jadefalke2.TAS;
import io.github.jadefalke2.stickRelatedClasses.SmoothTransitionDialog;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {

	private File directory;
	private boolean darkTheme;
	private int lastStickPositionCount;
	private JoystickPanelPosition joystickPanelPosition;
	private SmoothTransitionDialog.SmoothTransitionType smoothTransitionType;
	private RedoKeybind redoKeybind;


	private final Preferences backingPrefs;
	private final TAS parent;

	public Settings(Preferences prefs, TAS parent) {
		this.backingPrefs = prefs;
		this.parent = parent;

		File yuzuDir = new File(System.getProperty("user.home")+"/AppData/Roaming/yuzu/tas");

		setDirectory(new File(prefs.get("directory", System.getProperty("user.home")+(yuzuDir.exists() ? "/AppData/Roaming/yuzu/tas" : ""))));
		setDarkTheme(prefs.get("darkTheme", "false").equals("true"));
		setLastStickPositionCount(Integer.parseInt(prefs.get("lastStickPositionCount", "3")));
		setJoystickPanelPosition(JoystickPanelPosition.valueOf(prefs.get("joystickPanelPosition", "RIGHT")));
		setSmoothTransitionType(SmoothTransitionDialog.SmoothTransitionType.valueOf(prefs.get("smoothTransitionType", "ANGULAR_CLOSEST")));
		setRedoKeybind(RedoKeybind.valueOf(prefs.get("redoKeybind", "CTRL_SHIFT_Z")));
	}

	public void storeSettings() throws BackingStoreException {
		backingPrefs.clear();

		backingPrefs.put("directory", directory + "");
		backingPrefs.put("darkTheme", darkTheme + "");
		backingPrefs.put("lastStickPositionCount", lastStickPositionCount + "");
		backingPrefs.put("joystickPanelPosition", joystickPanelPosition + "");
		backingPrefs.put("smoothTransitionType", smoothTransitionType + "");
		backingPrefs.put("redoKeybind", redoKeybind + "");

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

	public void setSmoothTransitionType(SmoothTransitionDialog.SmoothTransitionType smoothTransitionType){
		this.smoothTransitionType = smoothTransitionType;
	}

	public SmoothTransitionDialog.SmoothTransitionType getSmoothTransitionType(){
		return smoothTransitionType;
	}

	public void setRedoKeybind(RedoKeybind redoKeybind) {
		this.redoKeybind = redoKeybind;
		if(parent.getMainEditorWindow() != null && parent.getMainEditorWindow().getMainJMenuBar() != null)
			parent.getMainEditorWindow().getMainJMenuBar().updateRedoAccelerator(redoKeybind);
	}
	public RedoKeybind getRedoKeybind() {
		return redoKeybind;
	}


	public enum JoystickPanelPosition {
		LEFT, RIGHT
	}

	public enum RedoKeybind {
		CTRL_SHIFT_Z, CTRL_Y
	}

}
