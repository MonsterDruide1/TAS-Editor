package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;

public class ScriptTab extends JPanel {

	private final TAS parent;
	private final PianoRoll pianoRoll;
	private final SideJoystickPanel sideJoystickPanel;

	private final JPanel editor;

	public ScriptTab(TAS parent) {
		this.parent = parent;
		pianoRoll = new PianoRoll(parent, parent.getScript());
		sideJoystickPanel = new SideJoystickPanel(parent, pianoRoll);

		editor = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Components
		JScrollPane scrollPane = new JScrollPane(pianoRoll);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		editor.add(scrollPane, c);

		refreshLayout();
	}

	public void refreshLayout() {
		removeAll();
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;

		c.gridx = 1; //leave 0 and 2 open for the JoystickPanel
		c.weightx = 1;
		add(editor, c);

		if(parent.getPreferences().getJoystickPanelPosition() == Settings.JoystickPanelPosition.LEFT)
			c.gridx = 0;
		else //RIGHT
			c.gridx = 2;

		c.weightx = 0;
		add(sideJoystickPanel, c);

		revalidate(); //force layout update
	}

	public void setScript(Script script){
		pianoRoll.setScript(script);
		sideJoystickPanel.setScript(script);
	}

	public PianoRoll getPianoRoll (){
		return pianoRoll;
	}

	public void updateSelectedRows() {
		if (!(pianoRoll.getSelectedRows().length == 0))
			sideJoystickPanel.setEditingRows(pianoRoll.getSelectedRows(), parent.getScript().getLines());
	}
}
