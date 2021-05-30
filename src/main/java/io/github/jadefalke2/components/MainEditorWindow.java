package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.util.Util;
import io.github.jadefalke2.util.CorruptedScriptException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainEditorWindow extends JFrame {

	// frame that can be opened from this one
	private final FunctionEditorWindow functionEditorWindow;
	private final MainJMenuBar mainJMenuBar;

	//JPanel
	private final JPanel editor;

	private final PianoRoll pianoRoll;
	private final SideJoystickPanel joystickPanel;

	//script
	private Script script;
	private File currentScriptFile;

	private final TAS parent;


	/**
	 * Constructor
	 * @param functionEditorWindow the function editor window that can be opened from within this window
	 */
	public MainEditorWindow (FunctionEditorWindow functionEditorWindow, Script script, TAS parent){

		this.functionEditorWindow = functionEditorWindow;
		this.script = script;
		this.parent = parent;
		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //let the WindowListener handle everything

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//TODO ONLY IF IN EDITOR + CHANGES DONE
				if(askForFileSave())
					dispose();
			}

			/**
			 * Ask and save the file before exiting the program
			 * @return whether it should actually close
			 */
			private boolean askForFileSave() {
				int result = JOptionPane.showConfirmDialog(editor, "Save Project changes?", "Save before exiting", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result == 0){
					//opens a new dialog that asks about saving, then exit
					saveFile();
				}
				return result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION; //cancel option and dispose option
			}
		});

		editor = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		pianoRoll = new PianoRoll(script, parent);

		joystickPanel = new SideJoystickPanel(parent, pianoRoll, script);

		//Components
		JScrollPane scrollPane = new JScrollPane(pianoRoll);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		mainJMenuBar = new MainJMenuBar(this, parent);

		JButton functionEditorButton = new JButton("Function editor");
		functionEditorButton.addActionListener(e -> functionEditorWindow.startUp());

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		editor.add(scrollPane, c);

		c.gridy = 1;
		c.weighty = 0;
		editor.add(functionEditorButton, c);

		JPanel combiningPanel = new JPanel(new GridBagLayout());

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		combiningPanel.add(editor, c);

		c.gridx = 1;
		c.weightx = 0;
		combiningPanel.add(joystickPanel, c);

		add(combiningPanel);

		setJMenuBar(mainJMenuBar);

		pack(); //TODO is still too small, the joystick is too little to use
		setExtendedState(MAXIMIZED_BOTH);
	}

	/**
	 * Returns the string that is being read from the given file.
	 * @param file the file to open
	 */
	public void setScript(File file) throws FileNotFoundException {

		// sets the current script file to be the one that the method is called with

		try {
			setScript(new Script(Util.fileToString(file)));
		} catch (CorruptedScriptException e) {
			e.printStackTrace();
		}

		currentScriptFile = file;
	}

	public void setScript(Script script){
		this.script = script;
		pianoRoll.setScript(script);
		joystickPanel.setScript(script);
		currentScriptFile = null;
	}

	/**
	 * writes the current script into the current file
	 */
	public void saveFile() {
		if(currentScriptFile == null){
			saveFileAs();
			return;
		}

		try {
			TxtFileChooser.writeToFile(script, currentScriptFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveFileAs() {
		try {
			File savedFile = new TxtFileChooser().saveFileAs(script);
			if(savedFile != null)
				currentScriptFile = savedFile;
		} catch(IOException err){
			err.printStackTrace();
		}
	}


	// getter


	public SideJoystickPanel getSidejoystickPanel() {
		return joystickPanel;
	}

	public PianoRoll getPianoRoll (){
		return pianoRoll;
	}

	public Script getScript (){
		return script;
	}

	public void updateUndoMenu(boolean enableUndo, boolean enableRedo) {
		mainJMenuBar.updateUndoMenu(enableUndo, enableRedo);
	}
}
