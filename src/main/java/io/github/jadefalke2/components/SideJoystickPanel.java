package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.StickAction;
import io.github.jadefalke2.stickRelatedClasses.CustomChangeListener;
import io.github.jadefalke2.stickRelatedClasses.ChangeObject;
import io.github.jadefalke2.stickRelatedClasses.JoystickPanel;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;

import javax.swing.*;
import java.awt.*;

public class SideJoystickPanel extends JPanel {

	private final JLabel frameAmountLabel;
	private final JoystickPanel lstickPanel;
	private final JoystickPanel rstickPanel;

	public SideJoystickPanel (TAS parent, PianoRoll pianoRoll, Script script) {

		frameAmountLabel = new JLabel("Currently no frames are being edited");

		CustomChangeListener joystickPanelListener = e -> parent.executeAction(new StickAction(((JoystickPanel)e.getSource()).getInputLines(), ((JoystickPanel)e.getSource()).getStickType(), e.getOldValue(), e.getNewValue(), pianoRoll.getModel()));
		lstickPanel = new JoystickPanel(parent.getPreferences(), JoystickPanel.StickType.L_STICK, joystickPanelListener);
		rstickPanel = new JoystickPanel(parent.getPreferences(), JoystickPanel.StickType.R_STICK, joystickPanelListener);

		pianoRoll.getSelectionModel().addListSelectionListener(e -> {
			int[] selectedRows = pianoRoll.getSelectedRows();

			switch (selectedRows.length) {
				case 0 -> {
					//no frames selected
					frameAmountLabel.setText("Currently no frames are being edited");
					lstickPanel.setAllEnabled(false);
					rstickPanel.setAllEnabled(false);
				}
				case 1 -> {
					//one frame is selected
					frameAmountLabel.setText("Currently editing frame " + selectedRows[0]);
					lstickPanel.setEditingRows(selectedRows, script);
					rstickPanel.setEditingRows(selectedRows, script);
				}
				default -> {
					//more than 1 frame is selected
					frameAmountLabel.setText("Currently editing frames " + selectedRows[0] + " - " + selectedRows[selectedRows.length - 1]);
					lstickPanel.setEditingRows(selectedRows, script);
					rstickPanel.setEditingRows(selectedRows, script);
				}
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0;
		c.gridy = 0;
		add(frameAmountLabel, c);

		c.weighty = 1;
		c.gridy = 1;
		add(lstickPanel, c);

		c.gridy = 2;
		add(rstickPanel, c);
	}



}
