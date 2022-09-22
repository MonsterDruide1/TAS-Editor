package io.github.jadefalke2;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import io.github.jadefalke2.components.MainEditorWindow;
import io.github.jadefalke2.components.SettingsDialog;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.Settings;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Window;

public class TAS {

	public static TAS INITIAL_MAIN_TAS_FOR_DEBUGGING;

	private MainEditorWindow mainEditorWindow;

	public static void main(String[] args) {
		INITIAL_MAIN_TAS_FOR_DEBUGGING = new TAS();
	}

	public TAS() {
		startProgram();
	}

	/**
	 * starts the program by opening a new window with the two options of either creating a new script or loading in a preexisting one. After this it will start the editor.
	 */

	public void startProgram() {
		Logger.log("boot up");

		// initialise preferences
		setLookAndFeel(Settings.INSTANCE.darkTheme.get());
		Settings.INSTANCE.darkTheme.attachListener(this::setLookAndFeel);

		mainEditorWindow = new MainEditorWindow(this);
		mainEditorWindow.openScript(Script.getEmptyScript(10));
		mainEditorWindow.setVisible(true);

		UIManager.put("FileChooser.useSystemExtensionHiding", false);
	}

	// set look and feels

	public void setLookAndFeel(boolean darkTheme){
		Logger.log("Changing theme: " + (darkTheme ? "Dark theme" : "Light theme"));

		try {
			UIManager.setLookAndFeel(darkTheme ? new FlatDarkLaf() : new FlatLightLaf());
			for(Window window : JFrame.getWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public void exit() {
		Logger.log("exiting program");
		mainEditorWindow.dispose();
	}

	public void newWindow() {
		Logger.log("opening new window");
		new TAS(); // TODO not the right way, as for example settings won't sync properly
	}

	public boolean closeAllScripts(){
		return mainEditorWindow.closeAllScripts();
	}

	public void openSettings(){
		Logger.log("opening settings");
		new SettingsDialog(mainEditorWindow).setVisible(true);
	}

}
