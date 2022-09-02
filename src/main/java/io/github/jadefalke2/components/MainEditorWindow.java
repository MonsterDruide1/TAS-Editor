package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.CorruptedScriptException;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

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

		scriptTab = new ScriptTab(parent);

		recreateLayoutPanel();

		pack(); //TODO is still too small, the joystick is too little to use
		setExtendedState(MAXIMIZED_BOTH);
	}

	public void recreateLayoutPanel(){
		remove(scriptTab);

		scriptTab.refreshLayout();

		add(scriptTab);
		revalidate(); //force layout update
	}

	public void setScript(Script script){
		scriptTab.setScript(script);
	}

	// getter

	public PianoRoll getPianoRoll (){
		return scriptTab.getPianoRoll();
	}

	public MainJMenuBar getMainJMenuBar() {
		return mainJMenuBar;
	}

	public void onUndoRedo(boolean enableUndo, boolean enableRedo) {
		scriptTab.updateSelectedRows();
		mainJMenuBar.updateUndoMenu(enableUndo, enableRedo);
	}
}
