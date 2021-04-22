package io.github.jadefalke2;

import com.formdev.flatlaf.FlatDarkLaf;
import io.github.jadefalke2.Components.*;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.util.CircularStack;
import io.github.jadefalke2.util.Stack;

import javax.swing.*;
import java.util.prefs.Preferences;

public class TAS {

	private static TAS instance;

	private StartUpWindow startUpWindow;
	private MainEditorWindow mainEditorWindow;
	private FunctionEditorWindow functionEditorWindow;

	private Preferences preferences;

	private Stack<Action> undoStack;
	private Stack<Action> redoStack;

	public static void main(String[] args) {
		new TAS();
	}

	private TAS() {
		instance = this;
		startProgram();
	}

	public static TAS getInstance() {
		return instance;
	}

	/**
	 * starts the program by opening a new window with the two options of either creating a new script or loading in a preexisting one. After this it will start the editor.
	 */

	public void startProgram() {

		//initialising stacks
		undoStack = new CircularStack<>(1024);
		redoStack = new CircularStack<>(1024);

		//initialising windows -> set to be invisible by default
		//will be set visible once they are supposed to
		mainEditorWindow = new MainEditorWindow();
		functionEditorWindow = new FunctionEditorWindow();
		startUpWindow = new StartUpWindow(mainEditorWindow);

		//initialising preferences
		preferences = Preferences.userRoot().node(getClass().getName());

		//set correct UI theme
		if (preferences.getBoolean("dark_theme", false)) {
			setDarculaLookAndFeel();
		} else {
			setDefaultLookAndFeel();
		}

	}


	public void setDefaultLookAndFeel() {
		//sets the look and feel to the OS' default
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(startUpWindow);
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void setDarculaLookAndFeel() {
		//sets the look and feel to dark mode
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
			SwingUtilities.updateComponentTreeUI(startUpWindow);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}


	}


	// Actions


	public void executeAction(Action action) {
		action.execute();
		undoStack.push(action);
		redoStack.clear();
	}

	public void undo() {
		if (undoStack.isEmpty())
			return;
		Action action = undoStack.pop();
		action.revert();
		redoStack.push(action);
	}

	public void redo() {
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
