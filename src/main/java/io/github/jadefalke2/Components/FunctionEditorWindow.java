package io.github.jadefalke2.Components;

import io.github.jadefalke2.Script;

import javax.swing.*;

public class FunctionEditorWindow extends JFrame {

	private PianoRoll pianoRoll;
	private JPanel mainPanel;

	public FunctionEditorWindow (){
		mainPanel = new JPanel();
		pianoRoll = new PianoRoll(Script.getEmptyScript(5));
	}

	public void startUp (){
		setVisible(true);
		setSize(200,100);
		setLocation(200,200);


		mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

		JScrollPane scrollPane = new JScrollPane(pianoRoll);
		mainPanel.add(scrollPane);

		add(mainPanel);
	}
}
