package io.github.jadefalke2;

import com.formdev.flatlaf.FlatDarkLaf;
import io.github.jadefalke2.Components.*;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.util.CircularStack;
import io.github.jadefalke2.util.Stack;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class TAS {

	private static TAS instance;

	private StartUpWindow startUpWindow;
	private MainEditorWindow mainEditorWindow;

	private Preferences preferences;

	private Stack<Action> undoStack;
	private Stack<Action> redoStack;

	public static void main(String[] args) {
		new TAS();
	}

	public TAS() {
		instance = this;
		startProgram();
	}

	/**
	 * starts the program by opening a new window with the two options of either creating a new script or loading in a preexisting one. After this it will start the editor.
	 */

	public void startProgram() {


		//initialising preferences
		preferences = Preferences.userRoot().node(getClass().getName());

		//initialising stacks
		undoStack = new CircularStack<>(1024);
		redoStack = new CircularStack<>(1024);

		//initialising windows -> set to be invisible by default
		//will be set visible once they are supposed to
		mainEditorWindow = new MainEditorWindow(new FunctionEditorWindow());
		//startUpWindow = new StartUpWindow(mainEditorWindow);

		mainEditorWindow.prepareEditor(Script.getEmptyScript(10));

		//set correct UI theme
		if (preferences.getBoolean("dark_theme", false)) {
			setDarculaLookAndFeel();
		} else {
			setDefaultLookAndFeel();
		}

	}


	// set look and feels

	public void setDefaultLookAndFeel() {
		//sets the look and feel to the OS' default
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			for(Window window : JFrame.getWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void setDarculaLookAndFeel() {
		//sets the look and feel to dark mode
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
			for(Window window : JFrame.getWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
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

	public static TAS getInstance() {
		return instance;
	}

	public Preferences getPreferences() {
		return preferences;
	}

}
