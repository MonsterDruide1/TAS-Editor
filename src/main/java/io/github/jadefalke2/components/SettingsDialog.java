package io.github.jadefalke2.components;

import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.lang.reflect.Field;

public class SettingsDialog extends JDialog {

	public SettingsDialog(Settings prefs){
		super(null, "Settings", ModalityType.APPLICATION_MODAL);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 1;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		for(Field field : Settings.class.getFields()){
			mainPanel.add(new JLabel(field.getName()), c);
			c.gridx = 1;
			try {
				switch(field.getType().getTypeName()){
					case "boolean":
						JCheckBox box = new JCheckBox();
						box.setSelected((Boolean) field.get(prefs));
						box.addItemListener((event) -> {
							try {
								field.set(prefs, event.getStateChange() == ItemEvent.SELECTED);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						});
						mainPanel.add(box, c);
						break;
					case "int":
						JSpinner spinner = new JSpinner();
						spinner.setValue(field.get(prefs));
						spinner.addChangeListener((event) -> {
							try {
								field.set(prefs, spinner.getValue());
							} catch(IllegalAccessException e){
								e.printStackTrace();
							}
						});
						mainPanel.add(spinner, c);
						break;
					default:
						throw new UnsupportedOperationException("Unexpected settings field type: " + field.getType().getTypeName());
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			c.gridy += 1;
			System.out.println(c.gridy);
			c.gridx = 0;
		}
		add(mainPanel);
		pack();
	}

}
