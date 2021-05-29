package io.github.jadefalke2.components;

import io.github.jadefalke2.TAS;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class MainJMenuBar extends JMenuBar {

	private JMenuItem undo, redo;

	public MainJMenuBar(MainEditorWindow mainEditorWindow, TAS parent){
		JMenu fileMenu = createFileMenu(parent, mainEditorWindow);
		add(fileMenu);

		JMenu editMenu = createEditMenu(parent, mainEditorWindow);
		add(editMenu);

		JMenu viewMenu = createViewMenu(parent.getPreferences());
		add(viewMenu);

		JMenu helpMenu = createHelpMenu();
		add(helpMenu);

		updateUndoMenu(false, false);
	}

	private JMenu createFileMenu(TAS parent, MainEditorWindow mainEditorWindow){
		JMenu fileJMenu = new JMenu("File");

		JMenuItem newJMenuItem = fileJMenu.add("New");
		newJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		newJMenuItem.addActionListener(e -> parent.newFile()); //TODO ask for closing the current project?

		JMenuItem newWindowJMenuItem = fileJMenu.add("New Window");
		newWindowJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		newWindowJMenuItem.addActionListener(e -> new TAS());

		JMenuItem openJMenuItem = fileJMenu.add("Open...");
		openJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		openJMenuItem.addActionListener(e -> {
			try {
				mainEditorWindow.setScript(new TxtFileChooser().getFile(true));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
		});

		JMenuItem saveJMenuItem = fileJMenu.add("Save");
		saveJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		saveJMenuItem.addActionListener(e -> mainEditorWindow.saveFile());

		JMenuItem saveAsJMenuItem = fileJMenu.add("Save As...");
		saveAsJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		saveAsJMenuItem.addActionListener(e -> mainEditorWindow.saveFileAs());

		fileJMenu.addSeparator();

		JMenuItem exitJMenuItem = fileJMenu.add("Exit");
		exitJMenuItem.addActionListener(e -> mainEditorWindow.dispatchEvent(new WindowEvent(mainEditorWindow, WindowEvent.WINDOW_CLOSING)));

		return fileJMenu;
	}

	private JMenu createEditMenu(TAS parent, MainEditorWindow mainEditorWindow){
		JMenu editJMenu = new JMenu("Edit");

		undo = editJMenu.add("Undo");
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		undo.addActionListener(e -> parent.undo());

		redo = editJMenu.add("Redo");
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		redo.addActionListener(e -> parent.redo());

		editJMenu.addSeparator();

		JMenuItem cutJMenuItem = editJMenu.add("Cut");
		cutJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		cutJMenuItem.addActionListener(e -> parent.cut());

		JMenuItem copyJMenuItem = editJMenu.add("Copy");
		copyJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		copyJMenuItem.addActionListener(e -> parent.copy());

		JMenuItem pasteJMenuItem = editJMenu.add("Paste");
		pasteJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		pasteJMenuItem.addActionListener(e -> {
			try {
				parent.paste();
			} catch (IOException | UnsupportedFlavorException ioException) {
				ioException.printStackTrace();
			}
		});

		JMenuItem deleteJMenuItem = editJMenu.add("Delete");
		deleteJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		deleteJMenuItem.addActionListener(e -> parent.delete());

		JMenuItem addNewLineItem = editJMenu.add("Add line");
		addNewLineItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		addNewLineItem.addActionListener(e -> mainEditorWindow.getPianoRoll().addEmptyRow());

		editJMenu.addSeparator();

		JMenuItem settingsItem = editJMenu.add("Settings");
		settingsItem.addActionListener(e -> parent.openSettings());

		return editJMenu;
	}

	private JMenu createViewMenu(Settings preferences){
		JMenu viewJMenu = new JMenu("View");

		JCheckBoxMenuItem darkThemeJMenuItem = new JCheckBoxMenuItem("Toggle Dark Theme", preferences.isDarkTheme());
		viewJMenu.add(darkThemeJMenuItem);
		darkThemeJMenuItem.addItemListener(e -> preferences.setDarkTheme(darkThemeJMenuItem.getState()));

		return viewJMenu;
	}

	private JMenu createHelpMenu(){
		JMenu helpJMenu = new JMenu("Help");

		JMenuItem discordJMenuItem = helpJMenu.add("Join the SMO TASing Discord");
		discordJMenuItem.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URL("https://discord.gg/atKSg9fygq").toURI());
			} catch (IOException | URISyntaxException ex) {
				ex.printStackTrace();
			}
		});

		helpJMenu.addSeparator();

		JMenuItem aboutJMenuItem = helpJMenu.add("About SMO TAS Editor");
		aboutJMenuItem.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URL("https://github.com/Jadefalke2/TAS-editor").toURI());
			} catch (IOException | URISyntaxException ex) {
				ex.printStackTrace();
			}
		});

		return helpJMenu;
	}

	public void updateUndoMenu(boolean enableUndo, boolean enableRedo) {
		undo.setEnabled(enableUndo);
		redo.setEnabled(enableRedo);
	}
}
