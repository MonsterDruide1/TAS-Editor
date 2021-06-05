package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.Util;
import io.github.jadefalke2.util.CorruptedScriptException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class MainEditorWindow extends JFrame {

	private final TAS parent;
	// frame that can be opened from this one
	private final FunctionEditorWindow functionEditorWindow;
	private final MainJMenuBar mainJMenuBar;

	private final PianoRoll pianoRoll;
	private final SideJoystickPanel sideJoystickPanel;

	//script
	private Script script;


	/**
	 * Constructor
	 * @param functionEditorWindow the function editor window that can be opened from within this window
	 */
	public MainEditorWindow (FunctionEditorWindow functionEditorWindow, Script script, TAS parent){

		this.parent = parent;
		this.functionEditorWindow = functionEditorWindow;
		this.script = script;
		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //let the WindowListener handle everything

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				//TODO ONLY IF IN EDITOR + CHANGES DONE
				if(askForFileSave())
					Logger.log("exiting program");
					dispose();
			}

			/**
			 * Ask and save the file before exiting the program
			 * @return whether it should actually close
			 */
			private boolean askForFileSave() {
				int result = JOptionPane.showConfirmDialog(MainEditorWindow.this, "Save Project changes?", "Save before exiting", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result == 0){
					//opens a new dialog that asks about saving, then exit
					saveFile();
				}
				return result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION; //cancel option and dispose option
			}
		});

		mainJMenuBar = new MainJMenuBar(this, parent);
		setJMenuBar(mainJMenuBar);

		pianoRoll = new PianoRoll(script, parent);
		sideJoystickPanel = new SideJoystickPanel(parent, pianoRoll, script);

		JPanel editor = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Components
		JScrollPane scrollPane = new JScrollPane(pianoRoll);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		editor.add(scrollPane, c);

		JButton functionEditorButton = new JButton("Function editor");
		functionEditorButton.addActionListener(e -> functionEditorWindow.startUp());

		c.gridy = 1;
		c.weighty = 0;
		// editor.add(functionEditorButton, c);




		JPanel combiningPanel = new JPanel(new GridBagLayout());

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;

		c.gridx = 1; //leave 0 and 2 open for the JoystickPanel
		c.weightx = 1;
		combiningPanel.add(editor, c);

		c.gridx = switch(parent.getPreferences().getJoystickPanelPosition()){
			case LEFT -> 0;
			case RIGHT -> 2;
		};
		c.weightx = 0;
		combiningPanel.add(sideJoystickPanel, c);

		add(combiningPanel);

		pack(); //TODO is still too small, the joystick is too little to use
		setExtendedState(MAXIMIZED_BOTH);
	}

	/**
	 * Returns the string that is being read from the given file.
	 * @param file the file to open
	 */
	public void setScript(File file) throws IOException {

		// sets the current script file to be the one that the method is called with

		try {
			setScript(new Script(file));
		} catch (CorruptedScriptException e) {
			e.printStackTrace();
		}
	}

	public void setScript(Script script){
		this.script = script;
		pianoRoll.setScript(script);
		sideJoystickPanel.setScript(script);
	}

	/**
	 * writes the current script into the current file
	 */
	public void saveFile() {
		script.saveFile(parent.getPreferences().getDirectory());
	}

	public void saveFileAs() {
		script.saveFileAs(parent.getPreferences().getDirectory());
	}


	// getter

	public PianoRoll getPianoRoll (){
		return pianoRoll;
	}

	public void onUndoRedo(boolean enableUndo, boolean enableRedo) {
		if (!(pianoRoll.getSelectedRows().length == 0))
			sideJoystickPanel.setEditingRows(pianoRoll.getSelectedRows(), script.getLines());
		mainJMenuBar.updateUndoMenu(enableUndo, enableRedo);
	}
}
