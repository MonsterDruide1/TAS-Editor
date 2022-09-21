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

public class SelectLinesDialog extends JDialog {
	private static final Pattern pattern = Pattern.compile("(\\d+)(?:-(\\d+))?(?:,\\s*|$)");

	private final int scriptLength;

	private final JTextField input;
	private boolean accepted = false;
	public SelectLinesDialog(Window owner, int scriptLength) {
		super(owner, "Select Lines", ModalityType.APPLICATION_MODAL);
		this.scriptLength = scriptLength;

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
		input.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
			okButton.setEnabled(isValidInput());
		});
		input.addActionListener(accept);
		input.setToolTipText("Examples: \"34\", \"120, 35\", \"5-20\" or \"120-150, 155\"");
		mainPanel.add(input, c);

		c.gridy++;
		okButton.addActionListener(accept);
		mainPanel.add(okButton, c);

		add(mainPanel);
		setLocationRelativeTo(null);
		pack();
	}

	public boolean isAccepted() {
		return accepted;
	}

	private boolean isValidInput() {
		String labelText = input.getText();
		Matcher matcher = pattern.matcher(labelText);
		StringBuilder collected = new StringBuilder();
		try {
			while(matcher.find()) {
				collected.append(matcher.group());

				int start = Integer.parseInt(matcher.group(1));
				if(start >= scriptLength)
					return false;

				if(matcher.group(2) != null) {
					int end = Integer.parseInt(matcher.group(2));
					if(end >= scriptLength)
						return false;
					if(start > end)
						return false;
				}
			}
		} catch(NumberFormatException e) {
			return false;
		}
		return collected.toString().equals(labelText);
	}

	public int[] getSelectedLines() {
		if(!isValidInput()) return null;

		Matcher matcher = pattern.matcher(input.getText());
		ArrayList<Integer> list = new ArrayList<>();
		while(matcher.find()) {
			if(matcher.group(2) != null) {
				int start = Integer.parseInt(matcher.group(1));
				int end = Integer.parseInt(matcher.group(2));
				list.addAll(IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList()));
			} else {
				list.add(Integer.parseInt(matcher.group(1)));
			}
		}

		return list.stream().mapToInt(i -> i).toArray();
	}
}
