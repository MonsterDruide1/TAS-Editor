package io.github.jadefalke2.Components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.stream.Collectors;

public class MainEditorWindow extends JFrame {

	// frame that can be opened from this one
	private final FunctionEditorWindow functionEditorWindow;

	//JPanel
	private JPanel editor;

	// Layout manager
	private GroupLayout groupLayout;

	//Components
	private JScrollPane scrollPane;
	private PianoRoll pianoRoll;
	private MainJMenuBar mainJMenuBar;

	//script
	private Script script;
	private File currentScriptFile;


	/**
	 * Constructor
	 * @param functionEditorWindow the function editor window that can be opened from within this window
	 */
	public MainEditorWindow (FunctionEditorWindow functionEditorWindow){

		this.functionEditorWindow = functionEditorWindow;
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
				return result != 2; //cancel option
			}
		});


		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				if (pianoRoll == null) {
					return;
				}

				pianoRoll.setPreferredSize(new Dimension((int)(getSize().getWidth() - 40), 500));
				pianoRoll.setPreferredScrollableViewportSize(new Dimension((int)(getSize().getWidth() - 40), 500));
				pianoRoll.setFillsViewportHeight(true);
			}
		});

	}

	/**
	 * Prepares the editor to make it ready to be started
	 * @param fileToOpen the file the editor will be opened with
	 */
	public void prepareEditor(File fileToOpen) {
		setVisible(true);
		setSize(800, 1000);
		script = new Script(preparePianoRoll(fileToOpen));
		startEditor();
	}

	/**
	 * Prepares the editor to make it ready to be started
	 * @param script the script that the editor will be opened with
	 */
	public void prepareEditor(Script script) {
		setVisible(true);
		setSize(800, 1000);
		this.script = new Script(script.getFull());
		startEditor();
	}

	/**
	 * Returns the string that is being read from the given file.
	 * @param file the file to open
	 * @return the corresponding String
	 */
	public String preparePianoRoll(File file) {

		// sets the current script file to be the one that the method is called with

		currentScriptFile = file;
		//script = new Script();


		// reads the file into a string that is returned
		return Script.fileToString(file);
	}

	/**
	 * starts the editor
	 */
	public void startEditor() {

		editor = new JPanel();

		editor.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		editor.setSize(550, 550);

		groupLayout = new GroupLayout(this);

		pianoRoll = new PianoRoll(script);
		scrollPane = new JScrollPane(pianoRoll);
		mainJMenuBar = new MainJMenuBar(this);


		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));


		JButton functionEditorButton = new JButton("Function editor");
		functionEditorButton.addActionListener(e -> {
			functionEditorWindow.startUp();
		});

		buttonsPanel.add(functionEditorButton);
		editor.add(scrollPane);
		editor.add(buttonsPanel);

		add(editor);
		setJMenuBar(mainJMenuBar);

		pack();
	}

	/**
	 * writes the current script into the current file
	 */
	public void saveFile() {
		TxtFileChooser.writeToFile(script, currentScriptFile);
	}


	// getter

	public PianoRoll getPianoRoll (){
		return pianoRoll;
	}

	public Script getScript (){
		return script;
	}

}
