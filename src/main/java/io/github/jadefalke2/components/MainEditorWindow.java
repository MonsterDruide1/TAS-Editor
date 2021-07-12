package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.CorruptedScriptException;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class MainEditorWindow extends JFrame {

	private final TAS parent;
	// frame that can be opened from this one
	private final MainJMenuBar mainJMenuBar;

	private final PianoRoll pianoRoll;
	private final SideJoystickPanel sideJoystickPanel;

	//layout
	private final JPanel editor;
	private JPanel combiningPanel;


	/**
	 * Constructor
	 */
	public MainEditorWindow (TAS parent){

		Logger.log("Initialising window");

		this.parent = parent;
		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //let the WindowListener handle everything

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(parent.closeScript()) {
					Logger.log("exiting program");
					dispose();
				}
			}
		});

		mainJMenuBar = new MainJMenuBar(this, parent);
		setJMenuBar(mainJMenuBar);

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
;
		recreateLayoutPanel();

		pack(); //TODO is still too small, the joystick is too little to use
		setExtendedState(MAXIMIZED_BOTH);
	}

	public void recreateLayoutPanel(){
		if(combiningPanel != null) remove(combiningPanel);
		combiningPanel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;

		c.gridx = 1; //leave 0 and 2 open for the JoystickPanel
		c.weightx = 1;
		combiningPanel.add(editor, c);

		if(parent.getPreferences().getJoystickPanelPosition() == Settings.JoystickPanelPosition.LEFT)
			c.gridx = 0;
		else //RIGHT
			c.gridx = 2;

		c.weightx = 0;
		combiningPanel.add(sideJoystickPanel, c);

		add(combiningPanel);

		revalidate(); //force layout update
	}

	public void setScript(Script script){
		pianoRoll.setScript(script);
		sideJoystickPanel.setScript(script);
	}

	// getter

	public PianoRoll getPianoRoll (){
		return pianoRoll;
	}

	public void onUndoRedo(boolean enableUndo, boolean enableRedo) {
		if (!(pianoRoll.getSelectedRows().length == 0))
			sideJoystickPanel.setEditingRows(pianoRoll.getSelectedRows(), parent.getScript().getLines());
		mainJMenuBar.updateUndoMenu(enableUndo, enableRedo);
	}
}
