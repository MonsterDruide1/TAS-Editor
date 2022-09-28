package io.github.jadefalke2.components;

import io.github.jadefalke2.util.SimpleDocumentListener;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AddLinesDialog extends JDialog {
	private final int defaultValue;

	private final JTextField input;
	private boolean accepted = false;
	public AddLinesDialog(Window owner, int defaultValue) {
		super(owner, "Add multiple lines", ModalityType.APPLICATION_MODAL);
		this.defaultValue = defaultValue;

		getRootPane().registerKeyboardAction(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)), KeyStroke.getKeyStroke(
			KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		JButton okButton = new JButton("OK");
		ActionListener accept = e -> {
			if(!isValidInput()) return;
			accepted = true;
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		};

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 15;
		c.insets = new Insets(5, 0, 0, 0);
		c.weighty = 1;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;

		input = new JTextField();
		input.getDocument().addDocumentListener((SimpleDocumentListener) e -> okButton.setEnabled(isValidInput()));
		input.addActionListener(accept);
		input.putClientProperty("JTextField.placeholderText", ""+defaultValue);
		mainPanel.add(input, c);

		c.gridy++;
		okButton.addActionListener(accept);
		mainPanel.add(okButton, c);

		add(mainPanel);
		pack();
		setLocationRelativeTo(owner);
	}

	public boolean isAccepted() {
		return accepted;
	}

	private Integer getValue() {
		String text = input.getText();
		if(text.isEmpty())
			return defaultValue;

		try {
			return Integer.parseInt(text);
		} catch(NumberFormatException e) {
			return null;
		}
	}

	private boolean isValidInput() {
		Integer val = getValue();
		return val != null && val >= 0;
	}

	public int getNumLines() {
		if(!isValidInput()) return -1;

		return getValue();
	}
}
