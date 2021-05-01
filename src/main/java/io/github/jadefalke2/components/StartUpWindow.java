package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.Util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StartUpWindow extends JFrame {

	private final MainEditorWindow mainEditorWindow;
	private final JPanel startUpPanel;


	public StartUpWindow(MainEditorWindow mainEditorWindow) {

		this.mainEditorWindow = mainEditorWindow;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("SMO TAS editor");
		setLocation(500, 500);
		setResizable(false);


		startUpPanel = new JPanel();

		JButton createNewScriptButton = new JButton("create new script");
		JButton loadScriptButton = new JButton("load script");


		createNewScriptButton.addActionListener(e -> onNewScriptButtonPress());

		loadScriptButton.addActionListener(e -> onLoadButtonPress());


		startUpPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

		startUpPanel.add(createNewScriptButton);
		startUpPanel.add(loadScriptButton);

		add(startUpPanel);
		pack();
		setVisible(true);
	}

	public void onLoadButtonPress() {
		openLoadFileChooser();
	}

	public void onNewScriptButtonPress() {
		openNewFileCreator();
	}

	public void openLoadFileChooser() {
		TxtFileChooser fileChooser = new TxtFileChooser();
		mainEditorWindow.prepareEditor(fileChooser.getFile(true));
		dispose();
	}

	public void openNewFileCreator() {

		File file = new TxtFileChooser().getFile(false);

		if (file != null) {
			try {
				file.createNewFile();

				String emptyScript = Script.getEmptyScript(5).getFull();
				Util.writeFile(emptyScript, file);

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			mainEditorWindow.prepareEditor(file);
		}

	}

}
