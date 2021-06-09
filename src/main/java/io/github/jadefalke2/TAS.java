package io.github.jadefalke2;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import io.github.jadefalke2.components.*;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.util.CorruptedScriptException;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class TAS {
	public static TAS INITIAL_MAIN_TAS_FOR_DEBUGGING;

	private MainEditorWindow mainEditorWindow;

	private Settings preferences;

	private Stack<Action> undoStack;
	private Stack<Action> redoStack;

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
		initPreferences();

		//initialising stacks
		undoStack = new Stack<>();
		redoStack = new Stack<>();

		//initialising windows -> set to be invisible by default
		//will be set visible once they are supposed to
		mainEditorWindow = new MainEditorWindow(new FunctionEditorWindow(this), Script.getEmptyScript(10), this);

		mainEditorWindow.setVisible(true);
	}


	private void initPreferences(){

		Logger.log("initialising settings");
		preferences = new Settings(Preferences.userRoot().node(getClass().getName()), this);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				preferences.storeSettings();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}));
	}

	// set look and feels

	public void setLookAndFeel(boolean darkTheme){
		Logger.log("Changing theme: " + (darkTheme ? "Dark theme" : "Light theme"));

		if (darkTheme) {
			setDarculaLookAndFeel();
		} else {
			setDefaultLookAndFeel();
		}
	}

	public void setDefaultLookAndFeel() {
		//sets the look and feel to light mode
		setLookAndFeel(new FlatLightLaf());
	}

	public void setDarculaLookAndFeel() {
		//sets the look and feel to dark mode
		setLookAndFeel(new FlatDarkLaf());
	}

	public void setLookAndFeel(FlatLaf lookAndFeel){
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			for(Window window : JFrame.getWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
			setDefaultsAfterThemeChange();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private void setDefaultsAfterThemeChange () {
		if (mainEditorWindow != null) {
			mainEditorWindow.getPianoRoll().setShowGrid(true);
		}
	}


	// Actions


	public void executeAction(Action action) {
		//adds a mew action to the stack to make it possible to undo
		action.execute();
		undoStack.push(action);
		redoStack.clear();
		mainEditorWindow.onUndoRedo(!undoStack.isEmpty(), !redoStack.isEmpty());

		Logger.log("executing action: " + action);
	}

	public void undo() {
		//undoes the last action
		if (undoStack.isEmpty())
			return;
		Action action = undoStack.pop();
		action.revert();
		redoStack.push(action);
		mainEditorWindow.onUndoRedo(!undoStack.isEmpty(), !redoStack.isEmpty());

		Logger.log("undoing action: " + action);
	}

	public void redo() {
		//redoes the last action
		if (redoStack.isEmpty())
			return;
		Action action = redoStack.pop();
		action.execute();
		undoStack.push(action);
		mainEditorWindow.onUndoRedo(!undoStack.isEmpty(), !redoStack.isEmpty());

		Logger.log("redoing action: " + action);
	}

	public void cut(){
		copy();
		delete();
	}

	public void copy(){
		InputLine[] rows = mainEditorWindow.getPianoRoll().getSelectedInputRows();
		int[] rowsIndex = mainEditorWindow.getPianoRoll().getSelectedRows();

		String[] rowStrings = new String[rows.length];
		for(int i=0;i<rows.length;i++){
			rowStrings[i] = rows[i].getFull(rowsIndex[i]);
		}
		String fullString = String.join("\n", rowStrings);

		StringSelection selection = new StringSelection(fullString);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}

	public void delete(){
		mainEditorWindow.getPianoRoll().deleteSelectedRows();
	}

	public void paste() throws IOException, UnsupportedFlavorException {
		String clipContent = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor);
		InputLine[] rows = Arrays.stream(clipContent.split("[\r\n]+")).map(line -> {
			try {
				return new InputLine(line);
			} catch(CorruptedScriptException e){
				System.out.println("invalid clipboard content: " + line); //TODO proper error handling here
				return null;
			}
		}).toArray(InputLine[]::new);
		mainEditorWindow.getPianoRoll().replaceSelectedRows(rows);
	}

	public void openSettings(){
		Logger.log("opening settings");
		new SettingsDialog(mainEditorWindow, preferences).setVisible(true);
	}

	public void newFile(){
		Logger.log("opening a new, empty script");
		openScript(Script.getEmptyScript(10));
	}

	public void openScript(File file) throws IOException {
		Logger.log("loading script from " + file.getAbsolutePath());
		//TODO ask for closing current project?
		mainEditorWindow.setScript(file); //do it like this to set the currentScriptFile of MainEditorWindow
		//TODO move this here, as well as all important file operations
		undoStack.clear();
		redoStack.clear();
	}
	public void openScript(Script script) {
		//TODO ask for closing current project?
		mainEditorWindow.setScript(script);
		undoStack.clear();
		redoStack.clear();
	}

	// getter

	public Settings getPreferences() {
		return preferences;
	}

	public void recreateMainPanelWindowLayout() {
		if(mainEditorWindow == null) return; //just skip it if the mainEditorWindow has not been created yet, as the setting will be applied on creation as well
		mainEditorWindow.recreateLayoutPanel();
	}
}
