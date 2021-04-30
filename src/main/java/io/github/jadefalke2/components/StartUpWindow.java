package io.github.jadefalke2.components;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StartUpWindow extends JFrame {

	private MainEditorWindow mainEditorWindow;
	private JPanel startUpPanel;


	public StartUpWindow(MainEditorWindow mainEditorWindow) {

		this.mainEditorWindow = mainEditorWindow;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("SMO TAS editor");
		setLocation(500, 500);
		pack();
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
		mainEditorWindow.prepareEditor(fileChooser.getFile());
		dispose();
	}

	public void openNewFileCreator() {

		JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());

		fileChooser.setDialogTitle("Choose where you want your TAS file to go");
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt", "text");
		fileChooser.setFileFilter(filter);
		fileChooser.setSelectedFile(new File("script1.txt"));

		int option = fileChooser.showSaveDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {

			File fileToOpen = fileChooser.getSelectedFile();
			String fileName = fileChooser.getSelectedFile().getPath();
			File file = new File(fileName);
			try {
				file.createNewFile();
				FileWriter fileWriter = new FileWriter(fileName);
				// optimize the below later
				fileWriter.write("1 NONE 0;0 0;0\n");
				fileWriter.write("2 NONE 0;0 0;0\n");
				fileWriter.write("3 NONE 0;0 0;0\n");
				fileWriter.write("4 NONE 0;0 0;0\n");

				fileWriter.close();

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			mainEditorWindow.prepareEditor(fileToOpen);
		}

	}

}
