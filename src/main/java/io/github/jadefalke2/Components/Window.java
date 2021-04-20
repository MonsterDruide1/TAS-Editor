package io.github.jadefalke2.Components;

import javax.swing.*;

public class Window extends JFrame {

	public boolean mainEditor = false;

	public Window() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("SMO TAS editor");
		setLocation(500, 500);
		pack();
		setVisible(true);
	}

}
