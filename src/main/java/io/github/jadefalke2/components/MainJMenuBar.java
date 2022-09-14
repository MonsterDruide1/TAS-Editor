package io.github.jadefalke2.components;

import io.github.jadefalke2.TAS;
import io.github.jadefalke2.util.ObservableProperty;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class MainJMenuBar extends JMenuBar {

	private JMenuItem newScript, newWindow, openScript, save, saveAs, exit;
	private JMenuItem undo, redo, cut, copy, paste, deleteLines, addLine, settings;
	private JCheckBoxMenuItem darkTheme;
	private JMenuItem discord, about;
	private final MainEditorWindow mainEditorWindow;

	public MainJMenuBar(MainEditorWindow mainEditorWindow, TAS parent){
		this.mainEditorWindow = mainEditorWindow;
		JMenu fileMenu = createFileMenu(parent, mainEditorWindow);
		add(fileMenu);

		JMenu editMenu = createEditMenu(parent, mainEditorWindow);
		add(editMenu);

		JMenu viewMenu = createViewMenu();
		add(viewMenu);

		JMenu helpMenu = createHelpMenu();
		add(helpMenu);

		updateUndoMenu(false, false);
	}

	private JMenu createFileMenu(TAS parent, MainEditorWindow mainEditorWindow){
		JMenu fileJMenu = new JMenu("File");

		newScript = fileJMenu.add("New");
		newScript.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		newScript.addActionListener(e -> parent.newFile());

		newWindow = fileJMenu.add("New Window");
		newWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		newWindow.addActionListener(e -> parent.newWindow());

		openScript = fileJMenu.add("Open...");
		openScript.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		openScript.addActionListener(e -> {
			try {
				parent.openScript(new TxtFileChooser(Settings.INSTANCE.directory.get()).getFile(true));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		save = fileJMenu.add("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		save.addActionListener(e -> {
			try {
				parent.saveFile();
			} catch(IOException ioe) {
				JOptionPane.showMessageDialog(null, "Failed to save file!\nError: "+ioe.getMessage(), "Saving failed", JOptionPane.ERROR_MESSAGE);
			}
		});

		saveAs = fileJMenu.add("Save As...");
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		saveAs.addActionListener(e -> {
			try {
				parent.saveFileAs();
			} catch(IOException ioe) {
				JOptionPane.showMessageDialog(null, "Failed to save file!\nError: "+ioe.getMessage(), "Saving failed", JOptionPane.ERROR_MESSAGE);
			}
		});

		fileJMenu.addSeparator();

		exit = fileJMenu.add("Exit");
		exit.addActionListener(e -> mainEditorWindow.dispatchEvent(new WindowEvent(mainEditorWindow, WindowEvent.WINDOW_CLOSING)));

		return fileJMenu;
	}

	private JMenu createEditMenu(TAS parent, MainEditorWindow mainEditorWindow){
		JMenu editJMenu = new JMenu("Edit");

		undo = editJMenu.add("Undo");
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		undo.addActionListener(e -> getActiveScriptTab().undo());

		redo = editJMenu.add("Redo");
		updateRedoAccelerator(Settings.INSTANCE.redoKeybind.get());
		redo.addActionListener(e -> getActiveScriptTab().redo());
		Settings.INSTANCE.redoKeybind.attachListener(this::updateRedoAccelerator);

		editJMenu.addSeparator();

		cut = editJMenu.add("Cut");
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		cut.addActionListener(e -> getActiveScriptTab().getPianoRoll().cut());

		copy = editJMenu.add("Copy");
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		copy.addActionListener(e -> getActiveScriptTab().getPianoRoll().copy());

		paste = editJMenu.add("Paste");
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		paste.addActionListener(e -> {
			try {
				getActiveScriptTab().getPianoRoll().paste();
			} catch (IOException | UnsupportedFlavorException ioException) {
				ioException.printStackTrace();
			}
		});

		deleteLines = editJMenu.add("Delete");
		deleteLines.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		deleteLines.addActionListener(e -> getActiveScriptTab().getPianoRoll().deleteSelectedRows());

		addLine = editJMenu.add("Add line");
		addLine.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		addLine.addActionListener(e -> mainEditorWindow.addEmptyRow());

		editJMenu.addSeparator();

		settings = editJMenu.add("Settings");
		settings.addActionListener(e -> parent.openSettings());

		return editJMenu;
	}

	private JMenu createViewMenu(){
		JMenu viewJMenu = new JMenu("View");

		ObservableProperty<Boolean> darkThemeSetting = Settings.INSTANCE.darkTheme;
		darkTheme = new JCheckBoxMenuItem("Toggle Dark Theme", darkThemeSetting.get());
		viewJMenu.add(darkTheme);
		darkTheme.addItemListener(e -> darkThemeSetting.set(darkTheme.getState()));
		darkThemeSetting.attachListener(darkTheme::setState);

		return viewJMenu;
	}

	private JMenu createHelpMenu(){
		JMenu helpJMenu = new JMenu("Help");

		discord = helpJMenu.add("Join the SMO TASing Discord");
		discord.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URL("https://discord.gg/atKSg9fygq").toURI());
			} catch (IOException | URISyntaxException ex) {
				ex.printStackTrace();
			}
		});

		helpJMenu.addSeparator();

		about = helpJMenu.add("About SMO TAS Editor");
		about.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URL("https://github.com/MonsterDruide1/TAS-Editor").toURI());
			} catch (IOException | URISyntaxException ex) {
				ex.printStackTrace();
			}
		});

		return helpJMenu;
	}

	private ScriptTab getActiveScriptTab() {
		return mainEditorWindow.getActiveScriptTab();
	}

	public void updateUndoMenu(boolean enableUndo, boolean enableRedo) {
		undo.setEnabled(enableUndo);
		redo.setEnabled(enableRedo);
	}

	public void updateRedoAccelerator(Settings.RedoKeybind keybind) {
		switch(keybind) {
			case CTRL_SHIFT_Z: redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)); break;
			case CTRL_Y: redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK)); break;
			default: System.err.println("setting undefined redokeybind! "+keybind);
		}
	}

	public void enableScriptRelatedInputs(boolean closed) {
		save.setEnabled(closed);
		saveAs.setEnabled(closed);
		undo.setEnabled(closed);
		redo.setEnabled(closed);
		cut.setEnabled(closed);
		copy.setEnabled(closed);
		paste.setEnabled(closed);
		deleteLines.setEnabled(closed);
		addLine.setEnabled(closed);
	}
}
