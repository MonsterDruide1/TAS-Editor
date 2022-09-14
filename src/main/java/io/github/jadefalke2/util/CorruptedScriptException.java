package io.github.jadefalke2.util;

import javax.swing.JOptionPane;

public class CorruptedScriptException extends Exception {

	public CorruptedScriptException (String errorMessage, int frame){
		super(errorMessage);

		Logger.log(errorMessage);
		String fullErrorMessage = errorMessage + " \nPlease check your script manually " + (frame == -1 ? "" : " \nat frame " + frame);
		JOptionPane.showMessageDialog(null, fullErrorMessage, "An exception occured", JOptionPane.ERROR_MESSAGE);
	}
}
