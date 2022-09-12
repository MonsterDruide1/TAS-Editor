package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.CorruptedScriptException;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MainEditorWindow extends JFrame {

	private final TAS parent;
	// frame that can be opened from this one
	private final MainJMenuBar mainJMenuBar;

	//layout
	private ScriptTab scriptTab;


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
				if(parent.closeScript()) {
					parent.exit();
				}
			}
		});

		mainJMenuBar = new MainJMenuBar(this, parent);
		setJMenuBar(mainJMenuBar);

		recreateLayoutPanel();

		pack(); //TODO is still too small, the joystick is too little to use
		setExtendedState(MAXIMIZED_BOTH);
	}

	public void recreateLayoutPanel(){

		if(scriptTab != null) {
			scriptTab.refreshLayout();
		}
		revalidate(); //force layout update
	}

	public void setScript(Script script){
		if(scriptTab != null) {
			if(!scriptTab.tryCloseScript())
				return;
			remove(scriptTab);
		}
		scriptTab = new ScriptTab(parent, script);
		add(scriptTab);
		recreateLayoutPanel();
	}
	public boolean closeScript() {
		return scriptTab.closeScript();
	}

	// getter

	public MainJMenuBar getMainJMenuBar() {
		return mainJMenuBar;
	}

	public void onUndoRedo(boolean enableUndo, boolean enableRedo) {
		scriptTab.updateSelectedRows();
		enableUndoRedo(enableUndo, enableRedo);
	}
	public void enableUndoRedo(boolean enableUndo, boolean enableRedo) {
		mainJMenuBar.updateUndoMenu(enableUndo, enableRedo);
	}

	public void executeAction(Action action) {
		scriptTab.executeAction(action);
	}
	public void undo() {
		scriptTab.undo();
	}
	public void redo() {
		scriptTab.redo();
	}
	public void previewAction(Action action) {
		scriptTab.previewAction(action);
	}
	public void saveFile() throws IOException {
		scriptTab.saveFile();
	}
	public void saveFileAs() throws IOException {
		scriptTab.saveFileAs();
	}
	public void copy() {
		scriptTab.getPianoRoll().copy();
	}
	public void paste() throws IOException, UnsupportedFlavorException {
		scriptTab.getPianoRoll().paste();
	}
	public void delete() {
		scriptTab.getPianoRoll().deleteSelectedRows();
	}
	public void addEmptyRow() {
		scriptTab.getPianoRoll().addEmptyRow();
	}
}
