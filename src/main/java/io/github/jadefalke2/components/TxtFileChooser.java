package io.github.jadefalke2.components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.Util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TxtFileChooser extends JFileChooser {

	/**
	 * Constructor
	 */
	public TxtFileChooser (){
		super(FileSystemView.getFileSystemView());
	}

	/**
	 * returns the chosen file
	 * @return the chosen file
	 */
	public File getFile (){

		setDialogTitle("Choose existing TAS file");
		setCurrentDirectory(new File(System.getProperty("user.home")));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt", "text");
		setFileFilter(filter);

		int option = showOpenDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {
			return getSelectedFile();
		}

		return null;

	}

	public File saveFileAs (Script scriptToSave) throws IOException {

		setDialogTitle("Choose place to save");
		setCurrentDirectory(new File(System.getProperty("user.home")));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt", "text");
		setFileFilter(filter);

		int option = showSaveDialog(null);

		String fileName = getSelectedFile().getPath();
		File file = new File(fileName);

		if (option == JFileChooser.APPROVE_OPTION) {
			try {
				file.createNewFile();
				writeToFile(scriptToSave, file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		if (option == JFileChooser.CANCEL_OPTION){
			return null;
		}

		return file;
	}

	public static void writeToFile(Script scriptToSave, File file) throws IOException {
		Util.writeFile(scriptToSave.getFull(), file);
	}

}
