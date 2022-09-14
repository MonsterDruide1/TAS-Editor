package io.github.jadefalke2.components;

import io.github.jadefalke2.stickRelatedClasses.SmoothTransitionDialog;
import io.github.jadefalke2.util.Settings;
import io.github.jadefalke2.util.SimpleDocumentListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class SettingsDialog extends JDialog {

	public SettingsDialog(Window owner){
		super(owner, "Settings", ModalityType.APPLICATION_MODAL);

		Settings prefs = Settings.INSTANCE;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,15,10,15));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 15;
		c.insets = new Insets(5,0,0,0);
		c.weighty = 1;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;

		addCheckboxSetting("Dark Theme", prefs.darkTheme.get(), prefs.darkTheme::set, mainPanel, c);
		c.gridy++;

		addFileSelectionSetting("Directory", prefs.directory.get(), prefs.directory::set, mainPanel, c);
		c.gridy++;

		addSpinnerSetting("Show last stick positions", prefs.lastStickPositionCount.get(), prefs.lastStickPositionCount::set, mainPanel, c);
		c.gridy++;

		addDropdownSetting("Default SmoothTransition-Type: ", prefs.smoothTransitionType.get(), prefs.smoothTransitionType::set, SmoothTransitionDialog.SmoothTransitionType.values(), Arrays.stream(SmoothTransitionDialog.SmoothTransitionType.values()).map(SmoothTransitionDialog.SmoothTransitionType::getName).toArray(String[]::new), mainPanel, c);
		c.gridy++;

		addRadioButtonSetting("JoystickPanel Position: ", prefs.joystickPanelPosition.get(), prefs.joystickPanelPosition::set, Settings.JoystickPanelPosition.values(), new String[]{"Left", "Right"}, Settings.JoystickPanelPosition::valueOf, mainPanel, c);
		c.gridy++;

		addRadioButtonSetting("Redo-Keybind: ", prefs.redoKeybind.get(), prefs.redoKeybind::set, Settings.RedoKeybind.values(), new String[]{"CTRL+SHIFT+Z", "CTRL+Y"}, Settings.RedoKeybind::valueOf, mainPanel, c);
		c.gridy++;

		add(mainPanel);
		setLocationRelativeTo(null);
		pack();
	}

	private void addSeperator (JPanel mainPanel, GridBagConstraints c) {
		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 2;
		mainPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);
		c.insets = new Insets(5,0,0,0);
		c.gridwidth = 1;
	}

	private void addFileSelectionSetting (String name, File defaultState,Consumer<File> setter, JPanel mainPanel, GridBagConstraints c) {
		mainPanel.add(new JLabel(name), c);
		c.gridx = 1;
		JButton openFileChooserButton = new JButton("open file chooser");

		openFileChooserButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(defaultState);

			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);

			int option = fileChooser.showDialog(openFileChooserButton, "select");

			if (option == JFileChooser.APPROVE_OPTION) {
				setter.accept(fileChooser.getSelectedFile());
			}
		});

		mainPanel.add(openFileChooserButton, c);
		c.gridx = 0;
	}

	private void addCheckboxSetting(String name, boolean defaultState, Consumer<Boolean> setter, JPanel mainPanel, GridBagConstraints c){
		mainPanel.add(new JLabel(name), c);
		c.gridx = 1;
		JCheckBox box = new JCheckBox();
		box.setSelected(defaultState);
		box.addItemListener((event) -> setter.accept(event.getStateChange() == ItemEvent.SELECTED));
		mainPanel.add(box, c);
		c.gridx = 0;
	}

	private void addSpinnerSetting(String name, int defaultState, Consumer<Integer> setter, JPanel mainPanel, GridBagConstraints c){
		mainPanel.add(new JLabel(name), c);
		c.gridx = 1;
		JSpinner spinner = new JSpinner();
		SpinnerNumberModel model = new SpinnerNumberModel();
		model.setMinimum(0);
		spinner.setModel(model);
		spinner.setValue(defaultState);
		spinner.addChangeListener((event) -> setter.accept((Integer)spinner.getValue()));
		mainPanel.add(spinner, c);
		c.gridx = 0;
	}

	private <T extends Enum<T>> void addRadioButtonSetting(String name, T defaultState, Consumer<T> setter, T[] values, String[] descriptions, Function<String, T> creator, JPanel mainPanel, GridBagConstraints c){
		if(values.length != descriptions.length)
			throw new IllegalArgumentException("Length of values differs from descriptions");

		mainPanel.add(new JLabel(name), c);
		c.gridx = 1;
		JPanel buttonPanel = new JPanel();
		ButtonGroup group = new ButtonGroup();
		for(int i=0;i<values.length;i++){
			JRadioButton button = new JRadioButton(descriptions[i]);
			button.setActionCommand(values[i].toString());
			button.addActionListener(e -> setter.accept(creator.apply(((JRadioButton)e.getSource()).getActionCommand())));

			group.add(button);
			buttonPanel.add(button);
			if(values[i] == defaultState)
				button.setSelected(true);
		}
		mainPanel.add(buttonPanel, c);
		c.gridx = 0;
	}

	private <T extends Enum<T>> void addDropdownSetting(String name, T defaultState, Consumer<T> setter, T[] values, String[] descriptions, JPanel mainPanel, GridBagConstraints c){
		if(values.length != descriptions.length)
			throw new IllegalArgumentException("Length of values differs from descriptions");

		mainPanel.add(new JLabel(name), c);
		c.gridx = 1;
		JComboBox<String> comboBox = new JComboBox<>();
		for(int i=0;i< values.length;i++){
			comboBox.addItem(descriptions[i]);
		}
		comboBox.setSelectedIndex(Arrays.asList(values).indexOf(defaultState));
		comboBox.addActionListener(e -> setter.accept(values[comboBox.getSelectedIndex()]));
		mainPanel.add(comboBox, c);
		c.gridx = 0;
	}

	private void addTitle(String title, JPanel mainPanel, GridBagConstraints c) {
		mainPanel.add(new JLabel(title), c);
	}

	private void addTextFieldSetting(String name, String defaultState, Consumer<String> setter, JPanel mainPanel, GridBagConstraints c){
		mainPanel.add(new JLabel(name), c);
		c.gridx = 1;
		JTextField field = new JTextField();
		field.setText(defaultState);
		field.getDocument().addDocumentListener((SimpleDocumentListener) e -> setter.accept(field.getText()));
		mainPanel.add(field, c);
		c.gridx = 0;
	}

}
