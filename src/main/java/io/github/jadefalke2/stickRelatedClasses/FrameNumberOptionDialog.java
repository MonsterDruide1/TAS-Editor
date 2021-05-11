package io.github.jadefalke2.stickRelatedClasses;

import javax.swing.*;

public class FrameNumberOptionDialog {

	public static int getFrameNumber (){

		// creates the spinner
		SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 500, 1);
		JSpinner spinner = new JSpinner(model);

		// opens the dialog and saves the option into an int
		int option = JOptionPane.showOptionDialog(null, spinner, "Enter number of frames", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, 1);

		// if the option is ok it returns the value
		if (option == JOptionPane.OK_OPTION) {
			return (int)spinner.getValue();
		}

		return 0;
	}
}
