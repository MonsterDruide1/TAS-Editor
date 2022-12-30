package io.github.jadefalke2;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import io.github.jadefalke2.components.MainEditorWindow;
import io.github.jadefalke2.connectivity.practice.ServerConnector;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TAS {

	private MainEditorWindow mainEditorWindow;

	private ServerConnector practiceServer;

	public static void main(String[] args) {
		new TAS();
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

		practiceServer = new ServerConnector();

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
		practiceServer.setRunning(false);
		mainEditorWindow.dispose();
	}

	public void newWindow() {
		Logger.log("opening new window");
		new TAS(); // TODO not the right way, as for example settings won't sync properly
	}
	public void runScriptPracticeMod() {
		try {
			practiceServer.runScript(mainEditorWindow.getActiveScriptTab().getScript(), new ServerConnector.GoConfig(
				Settings.INSTANCE.practiceStageName.get(),
				Settings.INSTANCE.practiceScenarioNo.get(),
				Settings.INSTANCE.practiceEntranceName.get()
			));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean closeAllScripts(){
		return mainEditorWindow.closeAllScripts();
	}

	public ServerConnector getPracticeServer() {
		return practiceServer;
	}
}
