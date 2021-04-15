package io.github.jadefalke2;

import com.formdev.flatlaf.FlatDarkLaf;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.prefs.Preferences;

public class TAS {

	private static TAS instance;

	private Window window;
	private JPanel startUpPanel;
	private JPanel editor;

	private Preferences preferences;

	private Script script;

	private File currentScriptFile;

	private boolean functionWindowIsOpen;

	private Stack<Action> undoStack;
	private Stack<Action> redoStack;

	private TAS() {
		instance = this;
		startProgram();
	}

	public static TAS getInstance() {
		return instance;
	}

	/**
	 * starts the program by opening a new window with the two options of either creating a new script or loading in a preexisting one. After this it will start the editor.
	 */

	public void startProgram() {

		preferences = Preferences.userRoot().node(getClass().getName());

		startUpPanel = new JPanel();

		window = new Window();
		window.setSize(300, 200);
		window.add(startUpPanel);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!(JOptionPane.showConfirmDialog(window, "Save Project changes?", "Save before exiting", JOptionPane.OK_OPTION, JOptionPane.CLOSED_OPTION, new ImageIcon("")) != 0) && window.mainEditor) {
					saveFile();
				}
				System.exit(0);
			}
		});

		if (preferences.getBoolean("dark_theme", false)) {
			setDarculaLookAndFeel();
		} else {
			setWindowsLookAndFeel();
		}

		JButton createNewScriptButton = new JButton("create new script");
		JButton loadScriptButton = new JButton("load script");


		createNewScriptButton.addActionListener(e -> onNewScriptButtonPress());

		loadScriptButton.addActionListener(e -> onLoadButtonPress());


		startUpPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

		startUpPanel.add(createNewScriptButton);
		startUpPanel.add(loadScriptButton);
	}


	public void onLoadButtonPress() {
		openLoadFileChooser();
	}

	public void onNewScriptButtonPress() {
		openNewFileCreator();
	}


	public void openLoadFileChooser() {

		JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());

		fileChooser.setDialogTitle("Choose existing TAS file");
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt", "text");
		fileChooser.setFileFilter(filter);

		int option = fileChooser.showOpenDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {

			File fileToOpen = fileChooser.getSelectedFile();
			prepareEditor(fileToOpen);
		}
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

			prepareEditor(fileToOpen);
		}

	}


	private void prepareEditor(File fileToOpen) {
		currentScriptFile = fileToOpen;

		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(fileToOpen))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				stringBuilder.append(sCurrentLine).append("\n");
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		String string = stringBuilder.toString();
		script = new Script(string);

		startEditor();
	}



	public void startEditor() {

		window.remove(startUpPanel);

		editor = new JPanel();
		window.add(editor);

		PianoRoll pianoRoll = new PianoRoll(script);
		JScrollPane scrollPane = new JScrollPane(pianoRoll);

		MainJMenuBar jMenuBar = new MainJMenuBar(this);
		window.setJMenuBar(jMenuBar);


		undoStack = new CircularStack<>(1024);
		redoStack = new CircularStack<>(1024);

		editor.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));


		window.setSize(700, 1100);
		editor.setSize(550, 550);

		window.mainEditor = true;


		editor.add(scrollPane);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

		JButton functionEditorButton = new JButton("Function editor");
		functionEditorButton.addActionListener(e -> {
		/*openFunctionEditor()*/
		});
		buttonsPanel.add(functionEditorButton);

		editor.add(buttonsPanel);
	}

	private void openFunctionEditor() {
		JFrame functionEditor;

		if (!functionWindowIsOpen) {

			functionWindowIsOpen = true;

			functionEditor = new JFrame();
			functionEditor.setVisible(true);
			functionEditor.setSize(500,400);
			functionEditor.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			functionEditor.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					functionWindowIsOpen = false;
					e.getWindow().dispose();
				}
			});

			JPanel functionWindowTypeChooser = new JPanel();
			functionEditor.add(functionWindowTypeChooser);

			JButton createNewFunctionButton = new JButton("Create new function");
			createNewFunctionButton.addActionListener(e -> createNewFunction());

			JButton editFunctionButton = new JButton("Edit existing function");
			editFunctionButton.addActionListener(e -> editFunction());

			functionWindowTypeChooser.add(editFunctionButton);
			functionWindowTypeChooser.add(createNewFunctionButton);
		}

	}

	private void createNewFunction() {
		openLoadFileChooser();
	}

	private void editFunction() {
		//edits a function
	}

	public void saveFile() {


		BufferedWriter writer = null;
		try {

			StringBuilder wholeScript = new StringBuilder();

			for (InputLine currentLine : script.getInputLines()) {
				wholeScript.append(currentLine.getFull() + "\n");
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

	private void askForSave (){
		JDialog save = new JDialog();
		save.setVisible(true);

		JLabel savingLabel = new JLabel("Save project changes?");

		JButton yes = new JButton("Yes");
		JButton no = new JButton("No");

		save.add(savingLabel);
		save.add(yes);
		save.add(no);
	}

	public void setWindowsLookAndFeel() {
		try {
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
			SwingUtilities.updateComponentTreeUI(window);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public void setDarculaLookAndFeel() {
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
			SwingUtilities.updateComponentTreeUI(window);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}


	}

	public void executeAction(Action action) {
		action.execute();
		undoStack.push(action);
		redoStack.clear();
	}

	public void undo() {
		if (undoStack.isEmpty())
			return;
		Action action = undoStack.pop();
		action.revert();
		redoStack.push(action);
	}

	public void redo() {
		if (redoStack.isEmpty())
			return;
		Action action = redoStack.pop();
		action.execute();
		undoStack.push(action);
	}


	public Preferences getPreferences() {
		return preferences;
	}

	public static void main(String[] args) {
		new TAS();
	}
}
