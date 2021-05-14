package io.github.jadefalke2.stickRelatedClasses;

import javax.swing.*;
import java.awt.*;

public class FrameNumberOptionDialog {

	public static int getFrameNumber (){

		// creates the spinner
		SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 500, 1);
		JSpinner spinner = new JSpinner(model);

		// opens the dialog and saves the option into an int
		UIManager.put("OptionPane.minimumSize",new Dimension(200,100));
		int option = JOptionPane.showOptionDialog(null, spinner, "Enter number of frames", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, 1);

		// if the option is ok it returns the value
		if (option == JOptionPane.OK_OPTION) {
			return (int)spinner.getValue();
		}

		return 0;
	}

	public static StickPosition[] getSmoothTransitionData () {
		// creates the spinner
		SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 500, 1);
		JSpinner spinner = new JSpinner(model);

		//option
		JComboBox<String> dropdownMenu = new JComboBox<>();
		dropdownMenu.addItem("Angular");
		dropdownMenu.addItem("Linear");


		Joystick startJoystick = new Joystick(32767, 75, new StickPosition[] {new StickPosition(0,0)});
		Joystick endJoystick =   new Joystick(32767, 75, new StickPosition[] {new StickPosition(0,0)});

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 1;
		c.weighty = 0;

		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 0;
		panel.add(spinner, c);

		c.gridx = 1;
		panel.add(dropdownMenu, c);

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;

		c.gridx = 0;
		c.gridy = 1;
		panel.add(startJoystick, c);

		c.gridx = 1;
		panel.add(endJoystick, c);

		JDialog dialog = new JDialog();
		dialog.setSize(500,500);
		dialog.add(panel);
		dialog.setVisible(true);

		// TODO 2 joysticks (start and end)
		//  + Dialog / JOptionPane
		//  + Add them to a pane
		
		return null;
	}
}
