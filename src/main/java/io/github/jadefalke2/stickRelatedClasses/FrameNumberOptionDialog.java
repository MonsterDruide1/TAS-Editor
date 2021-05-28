package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;

public class FrameNumberOptionDialog {

	public static StickPosition[] getSmoothTransitionData (Settings settings) {
		// creates the spinner
		SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 500, 1);
		JSpinner spinner = new JSpinner(model);

		//option
		JComboBox<String> dropdownMenu = new JComboBox<>();
		dropdownMenu.addItem("Angular");
		dropdownMenu.addItem("Linear");


		Joystick startJoystick = new Joystick(32767, settings);
		Joystick endJoystick =   new Joystick(32767, settings);

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
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible(true);

		// TODO 2 joysticks (start and end)
		//  + Dialog / JOptionPane
		//  + Add them to a pane
		
		return null;
	}
}
