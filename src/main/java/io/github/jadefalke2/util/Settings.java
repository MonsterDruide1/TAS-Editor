package io.github.jadefalke2.util;

import io.github.jadefalke2.TAS;

import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {

	private boolean darkTheme;
	private int lastStickPositionCount;


	private final Preferences backingPrefs;
	private final TAS parent;

	public Settings(Preferences prefs, TAS parent) {
		this.backingPrefs = prefs;
		this.parent = parent;

		setDarkTheme(prefs.get("darkTheme", "false").equals("true"));
		setLastStickPositionCount(Integer.parseInt(prefs.get("lastStickPositionCount", "3")));
	}

	public void storeSettings() throws BackingStoreException {
		backingPrefs.clear();

		backingPrefs.put("darkTheme", darkTheme+"");
		backingPrefs.put("lastStickPositionCount", lastStickPositionCount+"");

		backingPrefs.flush();
	}


	public boolean isDarkTheme() {
		return darkTheme;
	}

	public void setDarkTheme(boolean darkTheme) {
		this.darkTheme = darkTheme;
		parent.updateLookAndFeel();
	}

	public int getLastStickPositionCount() {
		return lastStickPositionCount;
	}

	public void setLastStickPositionCount(int lastStickPositionCount) {
		this.lastStickPositionCount = lastStickPositionCount;
	}
}
