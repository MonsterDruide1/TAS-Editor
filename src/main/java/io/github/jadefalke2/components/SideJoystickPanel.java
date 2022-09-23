package io.github.jadefalke2.components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.actions.LineAction;
import io.github.jadefalke2.actions.StickAction;
import io.github.jadefalke2.stickRelatedClasses.CustomChangeListener;
import io.github.jadefalke2.stickRelatedClasses.JoystickPanel;
import io.github.jadefalke2.stickRelatedClasses.SmoothTransitionDialog;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.Settings;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class SideJoystickPanel extends JPanel {

	private final JLabel frameAmountLabel;
	private final JoystickPanel lstickPanel;
	private final JoystickPanel rstickPanel;

	private InputLine[] inputLines;
	private final Script script;

	public SideJoystickPanel (ScriptTab scriptTab, PianoRoll pianoRoll, Script script) {

		frameAmountLabel = new JLabel("Currently no frames are being edited");
		frameAmountLabel.setHorizontalAlignment(JLabel.CENTER);
		frameAmountLabel.setFont(new Font(frameAmountLabel.getFont().getName(), frameAmountLabel.getFont().getStyle(), 15));
		this.script = script;

		//TODO remove the duplicate listener here
		ActionListener smoothTransitionListenerL = e -> {
			SmoothTransitionDialog dialog = new SmoothTransitionDialog(scriptTab.getWindow(), inputLines[0].getStickL(), inputLines[inputLines.length-1].getStickL(), inputLines.length);
			dialog.setVisible(true);
			if(!dialog.isAccepted())
				return;
			StickPosition[] replacementStickPos = dialog.getSmoothTransitionData();
			pianoRoll.executeAction(new StickAction(script, pianoRoll.getSelectedRows(), JoystickPanel.StickType.L_STICK, replacementStickPos));
		};

		ActionListener smoothTransitionListenerR = e -> {
			SmoothTransitionDialog dialog = new SmoothTransitionDialog(scriptTab.getWindow(), inputLines[0].getStickR(), inputLines[inputLines.length-1].getStickR(), inputLines.length);
			dialog.setVisible(true);
			if(!dialog.isAccepted())
				return;
			StickPosition[] replacementStickPos = dialog.getSmoothTransitionData();
			pianoRoll.executeAction(new StickAction(script, pianoRoll.getSelectedRows(), JoystickPanel.StickType.R_STICK, replacementStickPos));
		};

		CustomChangeListener<StickPosition> joystickPanelListener = e -> scriptTab.executeAction(new StickAction(script, pianoRoll.getSelectedRows(), getStickType(e.getSource()), e.getValue()));

		lstickPanel = new JoystickPanel(smoothTransitionListenerL, "Left-stick");
		rstickPanel = new JoystickPanel(smoothTransitionListenerR, "Right-Stick");
		lstickPanel.setOnChangeListener(joystickPanelListener);
		rstickPanel.setOnChangeListener(joystickPanelListener);

		lstickPanel.setAllEnabled(false);
		rstickPanel.setAllEnabled(false);

		pianoRoll.getSelectionModel().addListSelectionListener(e -> {
			int[] selectedRows = pianoRoll.getSelectedRows();

			if(selectedRows.length == 0) {
				//no frames selected
				frameAmountLabel.setText("Currently no frames are being edited");
				lstickPanel.setAllEnabled(false);
				rstickPanel.setAllEnabled(false);
			} else if(selectedRows.length == 1){
				//one frame is selected
				frameAmountLabel.setText("Currently editing frame " + selectedRows[0]);
				setEditingRows(selectedRows, script.getLines());
			} else {
				//more than 1 frame is selected
				String text = "Currently editing frames ";
				if((selectedRows.length-1) == (selectedRows[selectedRows.length-1]-selectedRows[0])){
					text += selectedRows[0] + " - " + selectedRows[selectedRows.length - 1];
				} else {
					text += Arrays.toString(selectedRows).substring(1); //add all rows to it, but skipping [
					text = text.substring(0, text.length()-1); //removing ] from it
				}

				if(text.length() > 50) {
					text = text.substring(0, 47)+"...";
				}

				frameAmountLabel.setText(text);
				setEditingRows(selectedRows, script.getLines());
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

	public JoystickPanel.StickType getStickType(Object joystickPanel){
		if(joystickPanel == lstickPanel) return JoystickPanel.StickType.L_STICK;
		if(joystickPanel == rstickPanel) return JoystickPanel.StickType.R_STICK;
		throw new IllegalArgumentException("Unknown StickType of panel: " + joystickPanel);
	}

	public void setEditingRows(int[] rows, InputLine[] scriptLines){
		inputLines = script.getLines(rows);

		setEditingRows(rows[0], inputLines[0], scriptLines, lstickPanel, JoystickPanel.StickType.L_STICK);
		setEditingRows(rows[0], inputLines[0], scriptLines, rstickPanel, JoystickPanel.StickType.R_STICK);

		if(rows.length > 1){ //override past stick positions if more than one row is selected
			StickPosition[] stickPositionsL = new StickPosition[rows.length];
			StickPosition[] stickPositionsR = new StickPosition[rows.length];
			for(int i=0;i<rows.length;i++){
				int realIndex = rows.length - 1 - i;
				stickPositionsL[i] = scriptLines[rows[realIndex]].getStickL();
				stickPositionsR[i] = scriptLines[rows[realIndex]].getStickR();
			}
			lstickPanel.setStickPositions(stickPositionsL);
			rstickPanel.setStickPositions(stickPositionsR);
		}

		lstickPanel.setAllEnabled(true);
		rstickPanel.setAllEnabled(true);
	}

	//TODO clean up this mess
	public void setEditingRows(int firstIndex, InputLine firstLine, InputLine[] inputLines, JoystickPanel joystickPanel, JoystickPanel.StickType stickType){
		StickPosition[] stickPositions = new StickPosition[Math.min(firstIndex, Settings.INSTANCE.lastStickPositionCount.get())]; // TODO listener
		// sets the contents of the stickpositions array to be the previous stick positions of the same stick
		for (int i = 0; i < stickPositions.length; i++){
			InputLine currentLine = inputLines[firstIndex - stickPositions.length + i];
			stickPositions[i] = stickType == JoystickPanel.StickType.L_STICK ? currentLine.getStickL() : currentLine.getStickR();
		}
		joystickPanel.setStickPositions(stickPositions);
		joystickPanel.setStickPosition(stickType == JoystickPanel.StickType.L_STICK ? firstLine.getStickL() : firstLine.getStickR());
	}

}
