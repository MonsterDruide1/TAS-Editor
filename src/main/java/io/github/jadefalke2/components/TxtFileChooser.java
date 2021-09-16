package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.Util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;

public class TxtFileChooser extends JFileChooser {

	private final File defaultDir;
	/**
	 * Constructor
	 */
	public TxtFileChooser (File defaultDir){
		super(FileSystemView.getFileSystemView());
		this.defaultDir = defaultDir;
	}

	/**
	 * returns the chosen file
	 * @param openFile whether it should display the opening or saving dialog
	 * @return the chosen file
	 */
	public File getFile (boolean openFile){

		setDialogTitle(openFile ? "Choose existing TAS file" : "Choose place to save");
		setCurrentDirectory(defaultDir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Util.fileExtension + " files", Util.fileExtension, "text");
		setFileFilter(filter);
		int option = openFile ? showOpenDialog(null) : showSaveDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {
			return getSelectedFile();
		}

		return null;
	}

	@Override
	public void approveSelection(){
		File file = getSelectedFile();

		if(!file.getAbsolutePath().endsWith( "." + Util.fileExtension)){
			file = new File(file.getAbsolutePath() + "." + Util.fileExtension);
			setSelectedFile(file);
		}

		if(file.exists() && getDialogType() == SAVE_DIALOG){
			int result = JOptionPane.showConfirmDialog(this, "This file already exists, overwrite it?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
			switch (result) {
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

}
