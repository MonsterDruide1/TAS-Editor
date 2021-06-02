package io.github.jadefalke2.components;

import io.github.jadefalke2.stickRelatedClasses.SmoothTransitionDialog;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class SettingsDialog extends JDialog {

	public SettingsDialog(Window owner, Settings prefs){
		super(owner, "Settings", ModalityType.APPLICATION_MODAL);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,15,10,15));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,0,0,0);
		c.weighty = 1;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;

		addCheckboxSetting("Dark Theme", prefs.isDarkTheme(), prefs::setDarkTheme, mainPanel, c);
		c.gridy++;

		addFileSelectionSetting("directory", prefs.getDirectory(), prefs::setDirectory, mainPanel, c);
		c.gridy++;

		addSpinnerSetting("Show last stick positions", prefs.getLastStickPositionCount(), prefs::setLastStickPositionCount, mainPanel, c);
		c.gridy++;

		addRadioButtonSetting("JoystickPanel Position: ", prefs.getJoystickPanelPosition(), prefs::setJoystickPanelPosition, Settings.JoystickPanelPosition.values(), new String[]{"Left", "Right"}, Settings.JoystickPanelPosition::valueOf, mainPanel, c);
		c.gridy++;

		addDropdownSetting("Default SmoothTransition-Type: ", prefs.getSmoothTransitionType(), prefs::setSmoothTransitionType, Settings.SmoothTransitionType.values(), SmoothTransitionDialog.dropdownOptions, mainPanel, c);
		c.gridy++;

		add(mainPanel);
		setLocationRelativeTo(null);
		pack();
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

}
