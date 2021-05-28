package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.Util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
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
	 * @param openFile whether it should display the opening or saving dialog
	 * @return the chosen file
	 */
	public File getFile (boolean openFile){

		setDialogTitle(openFile ? "Choose existing TAS file" : "Choose place to save");
		setCurrentDirectory(new File(System.getProperty("user.home")));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt", "text");
		setFileFilter(filter);

		int option = openFile ? showOpenDialog(null) : showSaveDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {
			return getSelectedFile();
		}

		return null;

	}

	public File saveFileAs (Script scriptToSave) throws IOException {

		File file = getFile(false);

		if (file != null) {
			file.createNewFile();

			if (file.getName().endsWith(".txt")) {
				writeToFile(scriptToSave, file);
			} else {
				File newFile = new File(file.getAbsolutePath() + Util.fileExtension);
				writeToFile(scriptToSave, newFile);
			}

		}

		return file;
	}

	@Override
	public void approveSelection(){
		File file = getSelectedFile();
		if(file.exists() && getDialogType() == SAVE_DIALOG){
			int result = JOptionPane.showConfirmDialog(this, "This file already exists, overwrite it?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
			switch (result){
				case JOptionPane.YES_OPTION:
					super.approveSelection();
					return;
				case JOptionPane.NO_OPTION:
				case JOptionPane.CLOSED_OPTION:
					return;
				case JOptionPane.CANCEL_OPTION:
					cancelSelection();
					return;
			}
		}
		super.approveSelection();
	}

	public static void writeToFile(Script scriptToSave, File file) throws IOException {
		Util.writeFile(scriptToSave.getFull(), file);
	}

}
