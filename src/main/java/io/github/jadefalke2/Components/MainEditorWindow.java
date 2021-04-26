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


		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {

				if (pianoRoll == null) {
					return;
				}

				pianoRoll.setPreferredSize(new Dimension((int)(getSize().getWidth() - 40), Math.min((int)(getSize().getHeight() - 40), 1000)));
				pianoRoll.setPreferredScrollableViewportSize(pianoRoll.getPreferredSize());
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

		//sets the current script file to be the one that the method is called with
		currentScriptFile = file;
		System.out.println(currentScriptFile);

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
