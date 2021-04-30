package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;

import javax.swing.*;
import java.awt.*;

public class FunctionEditorWindow extends JFrame {

	private PianoRoll pianoRoll;
	private JPanel mainPanel;

	public FunctionEditorWindow (TAS parent){
		mainPanel = new JPanel(new GridBagLayout());
		pianoRoll = new PianoRoll(Script.getEmptyScript(5), parent);
	}

	public void startUp (){
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		setLocation(200,200);

		JScrollPane scrollPane = new JScrollPane(pianoRoll);
		mainPanel.add(scrollPane, c);

		add(mainPanel);
		pack();
		setVisible(true);
	}
}
