package io.github.jadefalke2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.prefs.Preferences;

public class MainMenuBar extends MenuBar {

	public MainMenuBar (TAS main){
		// TODO: Handle the rest of the listeners and make things enabled or disabled correctly

		Menu fileMenu = add(new Menu("File"));
		MenuItem newMenuItem = fileMenu.add(new MenuItem("New", new MenuShortcut(KeyEvent.VK_N)));
		newMenuItem.addActionListener(e -> {});

		MenuItem newWindowMenuItem = fileMenu.add(new MenuItem("New Window", new MenuShortcut(KeyEvent.VK_N, true)));
		newWindowMenuItem.addActionListener(e -> {});

		MenuItem openMenuItem = fileMenu.add(new MenuItem("Open...", new MenuShortcut(KeyEvent.VK_O)));
		openMenuItem.addActionListener(e -> {});

		MenuItem saveMenuItem = fileMenu.add(new MenuItem("Save", new MenuShortcut(KeyEvent.VK_S)));
		saveMenuItem.addActionListener(e -> main.saveFile());

		MenuItem saveAsMenuItem = fileMenu.add(new MenuItem("Save As...", new MenuShortcut(KeyEvent.VK_S, true)));
		saveAsMenuItem.addActionListener(e -> main.saveFile());

		fileMenu.addSeparator();

		MenuItem exitMenuItem = fileMenu.add(new MenuItem("Exit"));
		exitMenuItem.addActionListener(e -> System.exit(0));

		Menu editMenu = add(new Menu("Edit"));

		MenuItem undoMenuItem = editMenu.add(new MenuItem("Undo", new MenuShortcut(KeyEvent.VK_Z)));
		undoMenuItem.addActionListener(e -> main.undo());

		MenuItem redoMenuItem = editMenu.add(new MenuItem("Redo", new MenuShortcut(KeyEvent.VK_Y)));
		redoMenuItem.addActionListener(e -> main.redo());

		editMenu.addSeparator();

		MenuItem cutMenuItem = editMenu.add(new MenuItem("Cut", new MenuShortcut(KeyEvent.VK_X)));
		cutMenuItem.addActionListener(e -> {});

		MenuItem copyMenuItem = editMenu.add(new MenuItem("Copy", new MenuShortcut(KeyEvent.VK_C)));
		copyMenuItem.addActionListener(e -> {});

		MenuItem pasteMenuItem = editMenu.add(new MenuItem("Paste", new MenuShortcut(KeyEvent.VK_V)));
		pasteMenuItem.addActionListener(e -> {});

		MenuItem deleteMenuItem = editMenu.add(new MenuItem("Delete", new MenuShortcut(KeyEvent.VK_DELETE)));
		deleteMenuItem.addActionListener(e -> {});

		Menu viewMenu = add(new Menu("View"));

		Preferences preferences = TAS.getInstance().getPreferences();

		CheckboxMenuItem darkThemeMenuItem = new CheckboxMenuItem("Toggle Dark Theme", preferences.getBoolean("dark_theme", false));

		viewMenu.add(darkThemeMenuItem);

		darkThemeMenuItem.addItemListener(e -> {
			preferences.putBoolean("dark_theme", darkThemeMenuItem.getState());
			if (darkThemeMenuItem.getState()) {
				main.setDarculaLookAndFeel();
			} else {
				main.setWindowsLookAndFeel();
			}
		});

		Menu helpMenu = add(new Menu("Help"));
		MenuItem discordMenuItem = helpMenu.add(new MenuItem("Join the SMO TASing Discord"));

		discordMenuItem.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URL("https://discord.gg/atKSg9fygq").toURI());
			} catch (IOException | URISyntaxException ex) {
				ex.printStackTrace();
			}
		});

		helpMenu.addSeparator();
		MenuItem aboutMenuItem = helpMenu.add(new MenuItem("About SMO TAS Editor"));
		aboutMenuItem.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URL("https://github.com/Jadefalke2/TAS-editor").toURI());
			} catch (IOException | URISyntaxException ex) {
				ex.printStackTrace();
			}
		});

	}
}
