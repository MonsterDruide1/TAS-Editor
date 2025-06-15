package io.github.jadefalke2.components;

import io.github.jadefalke2.script.Format;
import io.github.jadefalke2.util.ObservableProperty;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class MainJMenuBar extends JMenuBar {

	private static final int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	private JMenuItem newScript, newWindow, openScript, save, saveAs, saveCopy, exit;
	private JMenuItem undo, redo, cut, copy, paste, replace, deleteLines, selectLines, addLine, addLines, settings;
	private JCheckBoxMenuItem darkTheme;
	private JMenuItem discord, about;
	private final MainEditorWindow mainEditorWindow;

	public MainJMenuBar(MainEditorWindow mainEditorWindow){
		this.mainEditorWindow = mainEditorWindow;
		JMenu fileMenu = createFileMenu(mainEditorWindow);
		add(fileMenu);

		JMenu editMenu = createEditMenu(mainEditorWindow);
		add(editMenu);

		JMenu viewMenu = createViewMenu();
		add(viewMenu);

		JMenu helpMenu = createHelpMenu();
		add(helpMenu);

		updateUndoMenu(false, false);
	}

	private JMenu createFileMenu(MainEditorWindow mainEditorWindow){
		JMenu fileJMenu = new JMenu("File");

		newScript = fileJMenu.add("New");
		newScript.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcut));
		newScript.addActionListener(e -> mainEditorWindow.newFile());

		newWindow = fileJMenu.add("New Window");
		newWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcut | InputEvent.SHIFT_DOWN_MASK));
		newWindow.addActionListener(e -> mainEditorWindow.newWindow());

		openScript = fileJMenu.add("Open...");
		openScript.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcut));
		openScript.addActionListener(e -> {
			TasFileChooser chooser = new TasFileChooser(Settings.INSTANCE.directory.get());
			File selectedFile = chooser.getFile(true);
			Format format = chooser.getFormat();
			if(selectedFile == null) return;

			try {
				mainEditorWindow.openScript(selectedFile, format);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		save = fileJMenu.add("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcut));
		save.addActionListener(e -> {
			try {
				mainEditorWindow.saveFile();
			} catch(IOException ioe) {
				JOptionPane.showMessageDialog(null, "Failed to save file!\nError: "+ioe.getMessage(), "Saving failed", JOptionPane.ERROR_MESSAGE);
			}
		});

		saveAs = fileJMenu.add("Save As...");
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcut | InputEvent.SHIFT_DOWN_MASK));
		saveAs.addActionListener(e -> {
			try {
				mainEditorWindow.saveFileAs();
			} catch(IOException ioe) {
				JOptionPane.showMessageDialog(null, "Failed to save file!\nError: "+ioe.getMessage(), "Saving failed", JOptionPane.ERROR_MESSAGE);
			}
		});

		saveCopy = fileJMenu.add("Save Copy to...");
		saveCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcut | InputEvent.ALT_DOWN_MASK));
		saveCopy.addActionListener(e -> {
			try {
				mainEditorWindow.saveFileCopy();
			} catch(IOException ioe) {
				JOptionPane.showMessageDialog(null, "Failed to save file!\nError: "+ioe.getMessage(), "Saving failed", JOptionPane.ERROR_MESSAGE);
			}
		});
		saveCopy.setToolTipText("Save current state into other file, but keep the current file opened so future saves will still go into the first location");

		fileJMenu.addSeparator();

		exit = fileJMenu.add("Exit");
		exit.addActionListener(e -> mainEditorWindow.dispatchEvent(new WindowEvent(mainEditorWindow, WindowEvent.WINDOW_CLOSING)));

		return fileJMenu;
	}

	private JMenu createEditMenu(MainEditorWindow mainEditorWindow){
		JMenu editJMenu = new JMenu("Edit");

		undo = editJMenu.add("Undo");
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcut));
		undo.addActionListener(e -> getActiveScriptTab().undo());

		redo = editJMenu.add("Redo");
		updateRedoAccelerator(Settings.INSTANCE.redoKeybind.get());
		redo.addActionListener(e -> getActiveScriptTab().redo());
		Settings.INSTANCE.redoKeybind.attachListener(this::updateRedoAccelerator);

		editJMenu.addSeparator();

		cut = editJMenu.add("Cut");
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcut));
		cut.addActionListener(e -> getActiveScriptTab().getPianoRoll().cut());

		copy = editJMenu.add("Copy");
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcut));
		copy.addActionListener(e -> getActiveScriptTab().getPianoRoll().copy());

		paste = editJMenu.add("Paste");
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, shortcut));
		paste.addActionListener(e -> {
			try {
				getActiveScriptTab().getPianoRoll().paste();
			} catch (IOException | UnsupportedFlavorException ioException) {
				ioException.printStackTrace();
			}
		});

		replace = editJMenu.add("Replace");
		replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, shortcut));
		replace.addActionListener(e -> {
			try {
				getActiveScriptTab().getPianoRoll().replace();
			} catch (IOException | UnsupportedFlavorException ioException) {
				ioException.printStackTrace();
			}
		});

		deleteLines = editJMenu.add("Delete");
		deleteLines.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		deleteLines.addActionListener(e -> getActiveScriptTab().getPianoRoll().deleteSelectedRows());

		deleteLines = editJMenu.add("Clear lines");
		deleteLines.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
		deleteLines.addActionListener(e -> getActiveScriptTab().getPianoRoll().clearSelectedRows());

		selectLines = editJMenu.add("Select lines");
		selectLines.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, shortcut));
		selectLines.addActionListener(e -> mainEditorWindow.selectLines());

		addLine = editJMenu.add("Add line");
		addLine.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, shortcut | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		addLine.addActionListener(e -> mainEditorWindow.addSingleEmptyRow());

		addLines = editJMenu.add("Add multiple lines");
		addLines.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, shortcut | InputEvent.SHIFT_DOWN_MASK));
		addLines.addActionListener(e -> mainEditorWindow.addMultipleEmptyRows());

		editJMenu.addSeparator();

		settings = editJMenu.add("Settings");
		settings.addActionListener(e -> mainEditorWindow.openSettings());

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
			case CTRL_SHIFT_Z: redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcut | InputEvent.SHIFT_DOWN_MASK)); break;
			case CTRL_Y: redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, shortcut)); break;
			default: System.err.println("setting undefined redokeybind! "+keybind);
		}
	}

	public void enableScriptRelatedInputs(boolean closed) {
		save.setEnabled(closed);
		saveAs.setEnabled(closed);
		saveCopy.setEnabled(closed);
		undo.setEnabled(closed);
		redo.setEnabled(closed);
		cut.setEnabled(closed);
		copy.setEnabled(closed);
		paste.setEnabled(closed);
		replace.setEnabled(closed);
		deleteLines.setEnabled(closed);
		selectLines.setEnabled(closed);
		addLine.setEnabled(closed);
	}
}
