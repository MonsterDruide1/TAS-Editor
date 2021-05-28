package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.TAS;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;

public class FrameNumberOptionDialog {

	public static StickPosition[] getSmoothTransitionData (Settings settings) {

		//option
		JComboBox<String> dropdownMenu = new JComboBox<>();
		dropdownMenu.addItem("Angular");
		dropdownMenu.addItem("Linear");


		JoystickPanel startPanel = new JoystickPanel(settings, null, e -> {});
		JoystickPanel endPanel   = new JoystickPanel(settings, null, e -> {});

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		panel.add(startPanel, c);

		c.gridx = 1;
		panel.add(endPanel, c);

		JDialog dialog = new JDialog();
		dialog.setSize(500,500);
		dialog.add(panel);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible(true);


		//return actual stick positions
		return null;
	}
}
