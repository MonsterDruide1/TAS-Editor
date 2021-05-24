package io.github.jadefalke2.components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.stickRelatedClasses.JoystickPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Arrays;

public class SideJoystickPanel extends JPanel {

	private final JLabel frameAmountLabel;
	private final JoystickPanel lstickPanel;
	private final JoystickPanel rstickPanel;

	public SideJoystickPanel (TAS parent, JTable jTable, Script script) {

		frameAmountLabel = new JLabel("Currently no frames are being edited");
		lstickPanel = new JoystickPanel(parent, jTable, script, JoystickPanel.StickType.L_STICK);
		rstickPanel = new JoystickPanel(parent, jTable, script, JoystickPanel.StickType.R_STICK);

		jTable.getSelectionModel().addListSelectionListener(e -> {

			switch (jTable.getSelectedRows().length) {
				case 0:
					//no frames selected
					frameAmountLabel.setText("Currently no frames are being edited");
					lstickPanel.setGreyedOut();
					rstickPanel.setGreyedOut();
					break;

				case 1:
					//one frame is selected
					frameAmountLabel.setText("Currently editing frame " + jTable.getSelectedRows()[0]);
					lstickPanel.setStickPosition(script.getInputLines().get(jTable.getSelectedRows()[0]).getStickL());
					lstickPanel.setEditingRows(jTable.getSelectedRows());
					rstickPanel.setEditingRows(jTable.getSelectedRows());
					break;

				default:
					//more than 1 frame is selected
					frameAmountLabel.setText("Currently editing frames " + jTable.getSelectedRows()[0] + " - " + jTable.getSelectedRows()[jTable.getSelectedRows().length - 1]);
					lstickPanel.setEditingRows(jTable.getSelectedRows());
					rstickPanel.setEditingRows(jTable.getSelectedRows());
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridy = 0;
		add(frameAmountLabel, c);

		c.gridy = 1;
		add(lstickPanel, c);

		c.gridy = 2;
		add(rstickPanel, c);
	}



}
