package io.github.jadefalke2.Components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class MainEditorWindow extends JFrame {

	private final FunctionEditorWindow functionEditorWindow;

	private JPanel editor;

	private PianoRoll pianoRoll;
	private MainJMenuBar mainJMenuBar;

	private Script script;
	private File currentScriptFile;

	public MainEditorWindow (FunctionEditorWindow functionEditorWindow){

		this.functionEditorWindow = functionEditorWindow;

		setVisible(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				//TODO ONLY IF IN EDITOR + CHANGES DONE

				if (JOptionPane.showConfirmDialog(editor, "Save Project changes?", "Save before exiting", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("")) == 0){
					saveFile();
					System.exit(0);
				}

			}
		});
	}

	public void prepareEditor(File fileToOpen) {
		setVisible(true);
		script = new Script(preparePianoRoll(fileToOpen));
		startEditor();
	}

	public void prepareEditor(Script script) {
		setVisible(true);
		this.script = new Script(script.getFull());
		startEditor();
	}



	private String preparePianoRoll(File file) {
		currentScriptFile = file;

		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				stringBuilder.append(sCurrentLine).append("\n");
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		return stringBuilder.toString();
	}



	public void startEditor() {

		editor = new JPanel();
		add(editor);

		pianoRoll = new PianoRoll(script);
		JScrollPane scrollPane = new JScrollPane(pianoRoll);

		mainJMenuBar = new MainJMenuBar(this);
		setJMenuBar(mainJMenuBar);


		editor.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));


		setSize(600, 700);

		editor.setSize(550, 550);
		editor.add(scrollPane);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

		JButton functionEditorButton = new JButton("Function editor");
		functionEditorButton.addActionListener(e -> {
			functionEditorWindow.startUp();
		});

		editor.add(functionEditorButton);

		pack();
	}

	public void saveFile() {

		BufferedWriter writer = null;
		try {

			StringBuilder wholeScript = new StringBuilder();

			for (InputLine currentLine : script.getInputLines()) {
				if (!currentLine.isEmpty()) {
					wholeScript.append(currentLine.getFull() + "\n");
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

	public PianoRoll getPianoRoll (){
		return pianoRoll;
	}

	public Script getScript (){
		return script;
	}

}
