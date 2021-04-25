package io.github.jadefalke2.Components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.stream.Collectors;

public class MainEditorWindow extends JFrame {

	// frame that can be opened from this one
	private final FunctionEditorWindow functionEditorWindow;

	//JPanel
	private JPanel editor;

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
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//TODO ONLY IF IN EDITOR + CHANGES DONE
				askForFileSave();
				dispose();
				System.exit(0);
			}

			private void askForFileSave() {
				if (JOptionPane.showConfirmDialog(editor, "Save Project changes?", "Save before exiting", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("")) == 0){
					//opens a new dialog that asks about saving, the exits
					saveFile();
				}
			}
		});
	}

	/**
	 * Prepares the editor to make it ready to be started
	 * @param fileToOpen the file the editor will be opened with
	 */
	public void prepareEditor(File fileToOpen) {
		setVisible(true);
		script = new Script(preparePianoRoll(fileToOpen));
		startEditor();
	}

	/**
	 * Prepares the editor to make it ready to be started
	 * @param script the script that the editor will be opened with
	 */
	public void prepareEditor(Script script) {
		setVisible(true);
		this.script = new Script(script.getFull());
		startEditor();
	}

	/**
	 * Returns the string that is being read from the given file.
	 * @param file the file to open
	 * @return the corresponding String
	 */
	private String preparePianoRoll(File file) {

		//sets the current script file to be the one that the method is called with
		currentScriptFile = file;

		//reads the file into a string that is returned
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			return br.lines().map(sCurrentLine -> sCurrentLine + "\n").collect(Collectors.joining());
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// in case file is not being found -> also throws an exception
		return "";
	}

	/**
	 * starts the editor
	 */
	public void startEditor() {

		setSize(600, 700);

		editor = new JPanel();

		pianoRoll = new PianoRoll(script);
		scrollPane = new JScrollPane(pianoRoll);

		mainJMenuBar = new MainJMenuBar(this);
		setJMenuBar(mainJMenuBar);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

		editor.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		editor.setSize(550, 550);

		JButton functionEditorButton = new JButton("Function editor");
		functionEditorButton.addActionListener(e -> {
			functionEditorWindow.startUp();
		});

		editor.add(scrollPane);
		editor.add(functionEditorButton);

		add(editor);

		pack();
	}

	/**
	 * writes the current script into the current file
	 */
	public void saveFile() {

		BufferedWriter writer = null;

		try {

			StringBuilder wholeScript = new StringBuilder();

			for (InputLine currentLine : script.getInputLines()) {
				if (!currentLine.isEmpty()) {
					wholeScript.append(currentLine.getFull()).append("\n");
				}
			}

			FileWriter fw;

			fw = new FileWriter(currentScriptFile);


			writer = new BufferedWriter(fw);


			writer.write(wholeScript.toString());

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (Exception ex) {
				System.out.println("Error in closing the BufferedWriter" + ex);
			}
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
