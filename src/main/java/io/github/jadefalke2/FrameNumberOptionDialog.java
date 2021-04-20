package io.github.jadefalke2;

import javax.swing.*;

public class FrameNumberOptionDialog extends JOptionPane {

	public static int getFrameNumber (){
		SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 500, 1);
		JSpinner spinner = new JSpinner(model);

		int option = showOptionDialog(null, spinner, "Enter number of frames", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, 1);

		if (option == JOptionPane.OK_OPTION) {
			return (int)spinner.getValue();
		}

		return 0;
	}
}
