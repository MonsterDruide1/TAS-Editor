package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MainEditorWindow extends JFrame {

	// frame that can be opened from this one
	private final MainJMenuBar mainJMenuBar;

	//layout
	private final TabbedScriptsPane tabbedPane;
	private final JToolBar toolBar;

	private final JLabel scriptLengthLabel;


	/**
	 * Constructor
	 */
	public MainEditorWindow (TAS parent){

		Logger.log("Initialising window");

		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //let the WindowListener handle everything

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(parent.closeAllScripts()) {
					parent.exit();
				}
			}
		});

		mainJMenuBar = new MainJMenuBar(this, parent);
		setJMenuBar(mainJMenuBar);

		toolBar = new JToolBar();
		scriptLengthLabel = new JLabel();
		setCurrentScriptLength(-1);
		toolBar.add(scriptLengthLabel);
		toolBar.setFloatable(false);
		toolBar.setMargin(new Insets(0, 5, 0, 0));
		add(toolBar, BorderLayout.PAGE_END);

		tabbedPane = new TabbedScriptsPane(this, this::setCurrentScriptLength);
		add(tabbedPane, BorderLayout.CENTER);

		recreateLayoutPanel();

		pack(); //TODO is still too small, the joystick is too little to use
		setExtendedState(MAXIMIZED_BOTH);
	}

	public void recreateLayoutPanel(){
		tabbedPane.refreshLayouts();
		revalidate(); //force layout update
	}

	public void openScript(Script script){
		tabbedPane.openScript(script);
	}
	public boolean closeAllScripts() {
		return tabbedPane.closeAllScripts();
	}

	public void onUndoRedo(boolean enableUndo, boolean enableRedo) {
		getActiveScriptTab().updateSelectedRows();
		enableUndoRedo(enableUndo, enableRedo);
	}
	public void enableUndoRedo(boolean enableUndo, boolean enableRedo) {
		mainJMenuBar.updateUndoMenu(enableUndo, enableRedo);
	}

	public void executeAction(Action action) {
		getActiveScriptTab().executeAction(action);
	}
	public void previewAction(Action action) {
		getActiveScriptTab().previewAction(action);
	}
	public void saveFile() throws IOException {
		getActiveScriptTab().saveFile();
	}
	public void saveFileAs() throws IOException {
		getActiveScriptTab().saveFileAs();
	}
	public void addEmptyRow() {
		getActiveScriptTab().getPianoRoll().addEmptyRow();
	}

	public ScriptTab getActiveScriptTab() {
		return tabbedPane.getActiveScriptTab();
	}

	public void setAllTabsClosed(boolean closed) {
		mainJMenuBar.enableScriptRelatedInputs(!closed);
	}

	public void setCurrentScriptLength(int newLength) {
		scriptLengthLabel.setText("Length: "+newLength);
	}
}
