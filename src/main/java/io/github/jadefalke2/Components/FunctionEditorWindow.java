package io.github.jadefalke2.Components;

import io.github.jadefalke2.Script;
import sun.awt.image.GifImageDecoder;

import javax.swing.*;
import java.awt.*;

public class FunctionEditorWindow extends JFrame {

	private PianoRoll pianoRoll;
	private JPanel mainPanel;

	public FunctionEditorWindow (){
		mainPanel = new JPanel(new GridBagLayout());
		pianoRoll = new PianoRoll(Script.getEmptyScript(5));
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
