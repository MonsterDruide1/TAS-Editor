package io.github.jadefalke2.util;

import io.github.jadefalke2.TAS;
import io.github.jadefalke2.stickRelatedClasses.SmoothTransitionDialog;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {

	public static Settings INSTANCE = new Settings(Preferences.userRoot().node(TAS.class.getName()));
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				INSTANCE.storeSettings();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}));
	}

	public final ObservableProperty<File> directory;
	public final ObservableProperty<Boolean> darkTheme;
	public final ObservableProperty<Integer> lastStickPositionCount;
	public final ObservableProperty<JoystickPanelPosition> joystickPanelPosition;
	public final ObservableProperty<SmoothTransitionDialog.SmoothTransitionType> smoothTransitionType;
	public final ObservableProperty<RedoKeybind> redoKeybind;


	private final Preferences backingPrefs;

	private Settings(Preferences prefs) {
		this.backingPrefs = prefs;

		File yuzuDir = new File(System.getProperty("user.home")+"/AppData/Roaming/yuzu/tas");

		directory = new ObservableProperty<>(new File(prefs.get("directory", System.getProperty("user.home")+(yuzuDir.exists() ? "/AppData/Roaming/yuzu/tas" : ""))));
		darkTheme = new ObservableProperty<>(prefs.get("darkTheme", "false").equals("true"));
		lastStickPositionCount = new ObservableProperty<>(Integer.parseInt(prefs.get("lastStickPositionCount", "3")));
		joystickPanelPosition = new ObservableProperty<>(JoystickPanelPosition.valueOf(prefs.get("joystickPanelPosition", "RIGHT")));
		smoothTransitionType = new ObservableProperty<>(SmoothTransitionDialog.SmoothTransitionType.valueOf(prefs.get("smoothTransitionType", "ANGULAR_CLOSEST")));
		redoKeybind = new ObservableProperty<>(RedoKeybind.valueOf(prefs.get("redoKeybind", "CTRL_SHIFT_Z")));
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

	public enum JoystickPanelPosition {
		LEFT, RIGHT
	}

	public enum RedoKeybind {
		CTRL_SHIFT_Z, CTRL_Y
	}

}
