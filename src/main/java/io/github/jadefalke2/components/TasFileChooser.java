package io.github.jadefalke2.components;

import io.github.jadefalke2.script.Format;
import io.github.jadefalke2.util.Settings;
import io.github.jadefalke2.util.Util;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.Dimension;
import java.io.File;

public class TasFileChooser extends JFileChooser {

	private static final FileNameExtensionFilter sTAS = new FileNameExtensionFilter("SwitchTAS files (.stas)", "stas");
	private static final FileNameExtensionFilter nxTAS = new FileNameExtensionFilter("nxTAS files (.txt)", "txt");

	private final File defaultDir;
	/**
	 * Constructor
	 */
	public TasFileChooser(File defaultDir){
		super(FileSystemView.getFileSystemView());
		this.defaultDir = defaultDir;
		setPreferredSize(new Dimension(1000,600));
	}

	/**
	 * returns the chosen file
	 * @param openFile whether it should display the opening or saving dialog
	 * @return the chosen file
	 */
	public File getFile (boolean openFile){
		setDialogTitle(openFile ? "Choose existing TAS file" : "Choose place to save");
		setCurrentDirectory(defaultDir);
		addChoosableFileFilter(sTAS);
		addChoosableFileFilter(nxTAS);
		setFileFilter(Settings.INSTANCE.defaultScriptFormat.get() == Format.STAS ? sTAS : nxTAS);
		int option = openFile ? showOpenDialog(null) : showSaveDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {
			return getSelectedFile();
		}

		return null;
	}

	public Format getFormat() {
		if(getFileFilter() == sTAS) return Format.STAS;
		else if(getFileFilter() == nxTAS) return Format.nxTAS;
		else throw new IllegalStateException("Unexpected value: " + getFileFilter());
	}

	@Override
	public void approveSelection(){
		File file = getSelectedFile();

		if (getFileFilter() == sTAS) {
			if(!file.getAbsolutePath().endsWith(".stas")) {
				file = new File(file.getAbsolutePath() + ".stas");
				setSelectedFile(file);
			}
		} else if(getFileFilter() == nxTAS) {
			if(!file.getAbsolutePath().endsWith(".txt")) {
				file = new File(file.getAbsolutePath() + ".txt");
				setSelectedFile(file);
			}
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
