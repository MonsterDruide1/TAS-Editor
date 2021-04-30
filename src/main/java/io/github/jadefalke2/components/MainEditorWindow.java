package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.Util;
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

	//JPanel
	private JPanel editor;

	private PianoRoll pianoRoll;

	//script
	private Script script;
	private File currentScriptFile;

	private TAS parent;


	/**
	 * Constructor
	 * @param functionEditorWindow the function editor window that can be opened from within this window
	 */
	public MainEditorWindow (FunctionEditorWindow functionEditorWindow, TAS parent){

		this.functionEditorWindow = functionEditorWindow;
		this.parent = parent;
		setVisible(false);
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
	}

	/**
	 * Prepares the editor to make it ready to be started
	 * @param script the script that the editor will be opened with
	 */
	public void prepareEditor(Script script) {
		try {
			this.script = new Script(script.getFull());
		} catch (CorruptedScriptException e) {
			e.printStackTrace();
		}
		startEditor();
	}

	/**
	 * Returns the string that is being read from the given file.
	 * @param file the file to open
	 * @return the corresponding String
	 */
	public String setScript(File file) throws FileNotFoundException {

		// sets the current script file to be the one that the method is called with

		currentScriptFile = file;

		try {
			script = new Script(Util.fileToString(file));
		} catch (CorruptedScriptException e) {
			e.printStackTrace();
		}

		pianoRoll.setNewScript(script);

		// reads the file into a string that is returned
		return Util.fileToString(file);
	}

	/**
	 * starts the editor
	 */
	public void startEditor() {
		editor = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		pianoRoll = new PianoRoll(script, parent);
		pianoRoll.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		//Components
		JScrollPane scrollPane = new JScrollPane(pianoRoll);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		MainJMenuBar mainJMenuBar = new MainJMenuBar(this);

		JButton functionEditorButton = new JButton("Function editor");
		functionEditorButton.addActionListener(e -> functionEditorWindow.startUp());

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		editor.add(scrollPane, c);

		c.gridy = 1;
		c.weighty = 0;
		editor.add(functionEditorButton, c);

		add(editor);
		setJMenuBar(mainJMenuBar);

		pack();
		setVisible(true);
	}

	/**
	 * writes the current script into the current file
	 */
	public void saveFile() {
		try {
			TxtFileChooser.writeToFile(script, currentScriptFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// getter

	public PianoRoll getPianoRoll (){
		return pianoRoll;
	}

	public Script getScript (){
		return script;
	}

}
