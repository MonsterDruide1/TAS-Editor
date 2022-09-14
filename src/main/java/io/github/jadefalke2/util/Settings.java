package io.github.jadefalke2.util;

import io.github.jadefalke2.TAS;
import io.github.jadefalke2.stickRelatedClasses.SmoothTransitionDialog;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {
	private final ObservableProperty<File> directory;
	private final ObservableProperty<Boolean> darkTheme;
	private final ObservableProperty<Integer> lastStickPositionCount;
	private final ObservableProperty<JoystickPanelPosition> joystickPanelPosition;
	private final ObservableProperty<SmoothTransitionDialog.SmoothTransitionType> smoothTransitionType;
	private final ObservableProperty<RedoKeybind> redoKeybind;


	private final Preferences backingPrefs;

	public Settings(Preferences prefs, TAS parent) {
		this.backingPrefs = prefs;

		File yuzuDir = new File(System.getProperty("user.home")+"/AppData/Roaming/yuzu/tas");

		directory = new ObservableProperty<>(new File(prefs.get("directory", System.getProperty("user.home")+(yuzuDir.exists() ? "/AppData/Roaming/yuzu/tas" : ""))));
		darkTheme = new ObservableProperty<>(prefs.get("darkTheme", "false").equals("true"));
		lastStickPositionCount = new ObservableProperty<>(Integer.parseInt(prefs.get("lastStickPositionCount", "3")));
		joystickPanelPosition = new ObservableProperty<>(JoystickPanelPosition.valueOf(prefs.get("joystickPanelPosition", "RIGHT")));
		smoothTransitionType = new ObservableProperty<>(SmoothTransitionDialog.SmoothTransitionType.valueOf(prefs.get("smoothTransitionType", "ANGULAR_CLOSEST")));
		redoKeybind = new ObservableProperty<>(RedoKeybind.valueOf(prefs.get("redoKeybind", "CTRL_SHIFT_Z")));

		darkTheme.attachListener(parent::setLookAndFeel);
		joystickPanelPosition.attachListener(val -> parent.recreateMainPanelWindowLayout());
		redoKeybind.attachListener(newKeybind -> {
			if(parent.getMainEditorWindow() != null && parent.getMainEditorWindow().getMainJMenuBar() != null)
				parent.getMainEditorWindow().getMainJMenuBar().updateRedoAccelerator(newKeybind);
		});
	}

	public void storeSettings() throws BackingStoreException {
		backingPrefs.clear();

		backingPrefs.put("directory", directory.get() + "");
		backingPrefs.put("darkTheme", darkTheme.get() + "");
		backingPrefs.put("lastStickPositionCount", lastStickPositionCount.get() + "");
		backingPrefs.put("joystickPanelPosition", joystickPanelPosition.get() + "");
		backingPrefs.put("smoothTransitionType", smoothTransitionType.get() + "");
		backingPrefs.put("redoKeybind", redoKeybind.get() + "");

		backingPrefs.flush();
	}


	public File getDirectory () {
		return directory.get();
	}

	public void setDirectory (File directory) {
		this.directory.set(directory);
	}

	public boolean isDarkTheme() {
		return darkTheme.get();
	}

	public void setDarkTheme(boolean darkTheme) {
		this.darkTheme.set(darkTheme);
	}

	public int getLastStickPositionCount() {
		return lastStickPositionCount.get();
	}

	public void setLastStickPositionCount(int lastStickPositionCount) {
		this.lastStickPositionCount.set(lastStickPositionCount);
	}

	public void setJoystickPanelPosition(JoystickPanelPosition joystickPanelPosition){
		this.joystickPanelPosition.set(joystickPanelPosition);
	}

	public JoystickPanelPosition getJoystickPanelPosition(){
		return joystickPanelPosition.get();
	}

	public void setSmoothTransitionType(SmoothTransitionDialog.SmoothTransitionType smoothTransitionType){
		this.smoothTransitionType.set(smoothTransitionType);
	}

	public SmoothTransitionDialog.SmoothTransitionType getSmoothTransitionType(){
		return smoothTransitionType.get();
	}

	public void setRedoKeybind(RedoKeybind redoKeybind) {
		this.redoKeybind.set(redoKeybind);
	}
	public RedoKeybind getRedoKeybind() {
		return redoKeybind.get();
	}


	public enum JoystickPanelPosition {
		LEFT, RIGHT
	}

	public enum RedoKeybind {
		CTRL_SHIFT_Z, CTRL_Y
	}

}
