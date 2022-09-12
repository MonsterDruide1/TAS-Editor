package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.util.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MainEditorWindow extends JFrame {

	private final TAS parent;
	// frame that can be opened from this one
	private final MainJMenuBar mainJMenuBar;

	//layout
	private final TabbedScriptsPane tabbedPane;


	/**
	 * Constructor
	 */
	public MainEditorWindow (TAS parent){

		Logger.log("Initialising window");

		this.parent = parent;
		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //let the WindowListener handle everything

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(parent.closeAllScripts()) {
					parent.exit();
				}
			}
		});

		mainJMenuBar = new MainJMenuBar(this, parent);
		setJMenuBar(mainJMenuBar);

		tabbedPane = new TabbedScriptsPane(parent);
		add(tabbedPane);

		recreateLayoutPanel();

		pack(); //TODO is still too small, the joystick is too little to use
		setExtendedState(MAXIMIZED_BOTH);
	}

	public void recreateLayoutPanel(){
		tabbedPane.refreshLayouts();
		revalidate(); //force layout update
	}

	public void openScript(Script script){
		tabbedPane.openScript(script);
	}
	public boolean closeAllScripts() {
		return tabbedPane.closeAllScripts();
	}

	// getter

	public MainJMenuBar getMainJMenuBar() {
		return mainJMenuBar;
	}

	public void onUndoRedo(boolean enableUndo, boolean enableRedo) {
		getActiveScriptTab().updateSelectedRows();
		enableUndoRedo(enableUndo, enableRedo);
	}
	public void enableUndoRedo(boolean enableUndo, boolean enableRedo) {
		mainJMenuBar.updateUndoMenu(enableUndo, enableRedo);
	}

	public void executeAction(Action action) {
		getActiveScriptTab().executeAction(action);
	}
	public void previewAction(Action action) {
		getActiveScriptTab().previewAction(action);
	}
	public void saveFile() throws IOException {
		getActiveScriptTab().saveFile();
	}
	public void saveFileAs() throws IOException {
		getActiveScriptTab().saveFileAs();
	}
	public void addEmptyRow() {
		getActiveScriptTab().getPianoRoll().addEmptyRow();
	}

	public ScriptTab getActiveScriptTab() {
		return tabbedPane.getActiveScriptTab();
	}
}
