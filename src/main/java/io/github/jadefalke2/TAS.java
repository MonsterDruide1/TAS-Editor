package io.github.jadefalke2;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import io.github.jadefalke2.components.*;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.util.CircularStack;
import io.github.jadefalke2.util.Stack;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class TAS {

	private MainEditorWindow mainEditorWindow;

	private Preferences preferences;

	private Stack<Action> undoStack;
	private Stack<Action> redoStack;

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

		//initialising preferences
		preferences = Preferences.userRoot().node(getClass().getName());

		//set correct UI theme
		if (preferences.getBoolean("dark_theme", false)) {
			setDarculaLookAndFeel();
		} else {
			setDefaultLookAndFeel();
		}

		//initialising stacks
		undoStack = new CircularStack<>(1024);
		redoStack = new CircularStack<>(1024);

		//initialising windows -> set to be invisible by default
		//will be set visible once they are supposed to

		mainEditorWindow = new MainEditorWindow(new FunctionEditorWindow(this), this);

		mainEditorWindow.prepareEditor(Script.getEmptyScript(10));

		//set correct UI theme
		updateLookAndFeel();

	}


	// set look and feels

	public void updateLookAndFeel(){
		if (preferences.getBoolean("dark_theme", false)) {
			setDarculaLookAndFeel();
		} else {
			setDefaultLookAndFeel();
		}
	}

	public void setDefaultLookAndFeel() {
		//sets the look and feel to light mode
		setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
	}

	public void setDarculaLookAndFeel() {
		//sets the look and feel to dark mode
		setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
	}

	public void setLookAndFeel(String lookAndFeel){
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			for(Window window : JFrame.getWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}

			setDefaultsAfterThemeChange();
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
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
	}

	public void undo() {
		//undoes the last action
		if (undoStack.isEmpty())
			return;
		Action action = undoStack.pop();
		action.revert();
		redoStack.push(action);
	}

	public void redo() {
		//redoes the last action
		if (redoStack.isEmpty())
			return;
		Action action = redoStack.pop();
		action.execute();
		undoStack.push(action);
	}


	// getter

	public Preferences getPreferences() {
		return preferences;
	}

}
