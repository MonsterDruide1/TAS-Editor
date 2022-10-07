package io.github.jadefalke2.util;

import javax.swing.JOptionPane;

public class CorruptedScriptException extends Exception {

	public CorruptedScriptException (String errorMessage, int frame, Throwable cause) {
		super(errorMessage + (frame == -1 ? "" : " \nat frame " + frame), cause);

		Logger.log(errorMessage);
		String fullErrorMessage = errorMessage + " \nPlease check your script manually " + (frame == -1 ? "" : " \nat frame " + frame);
		JOptionPane.showMessageDialog(null, fullErrorMessage, "An exception occured", JOptionPane.ERROR_MESSAGE);
	}
	public CorruptedScriptException (String errorMessage, int frame) {
		this(errorMessage, frame, null);
	}
}
