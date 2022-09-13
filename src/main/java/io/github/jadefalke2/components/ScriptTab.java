package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.ObservableProperty;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Stack;

public class ScriptTab extends JPanel {

	private final TAS parent;
	private final Script script;
	private final PianoRoll pianoRoll;
	private final SideJoystickPanel sideJoystickPanel;

	private final JPanel editor;

	private final Stack<Action> undoStack;
	private final Stack<Action> redoStack;
	private Action previewAction;

	private ObservableProperty.PropertyChangeListener<Boolean> dirtyChangeListener;

	public ScriptTab(TAS parent, Script script) {
		this.parent = parent;
		this.script = script;

		//initialising stacks
		undoStack = new Stack<>();
		redoStack = new Stack<>();

		pianoRoll = new PianoRoll(parent, script, this);
		sideJoystickPanel = new SideJoystickPanel(parent, pianoRoll, script);

		editor = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Components
		JScrollPane scrollPane = new JScrollPane(pianoRoll);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		editor.add(scrollPane, c);

		refreshLayout();
		parent.getMainEditorWindow().enableUndoRedo(false, false);
	}

	public void refreshLayout() {
		removeAll();
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;

		c.gridx = 1; //leave 0 and 2 open for the JoystickPanel
		c.weightx = 1;
		add(editor, c);

		if(parent.getPreferences().getJoystickPanelPosition() == Settings.JoystickPanelPosition.LEFT)
			c.gridx = 0;
		else //RIGHT
			c.gridx = 2;

		c.weightx = 0;
		add(sideJoystickPanel, c);

		pianoRoll.setShowGrid(true); // necessary after theme change

		revalidate(); //force layout update
	}

	public PianoRoll getPianoRoll (){
		return pianoRoll;
	}

	public void updateSelectedRows() {
		if (!(pianoRoll.getSelectedRows().length == 0))
			sideJoystickPanel.setEditingRows(pianoRoll.getSelectedRows(), script.getLines());
	}

	public void saveFile() throws IOException {
		script.saveFile(parent.getPreferences().getDirectory());
	}
	public void saveFileAs() throws IOException {
		script.saveFileAs(parent.getPreferences().getDirectory());
	}
	public boolean closeScript() {
		return script.closeScript(parent);
	}

	public void executeAction(Action action) {
		if(previewAction != null) {
			previewAction.revert();
			previewAction = null;
		}
		Logger.log("executing action: " + action);

		//adds a new action to the stack to make it possible to undo
		action.execute();
		undoStack.push(action);
		redoStack.clear();
		updateUndoRedoEnabled();
	}

	public void undo() {
		//undoes the last action
		if (undoStack.isEmpty())
			return;

		if(previewAction != null) {
			Logger.log("reverting preview: "+previewAction);
			previewAction.revert();
			previewAction = null;
		}
		Action action = undoStack.pop();
		Logger.log("undoing action: " + action);

		action.revert();
		redoStack.push(action);
		updateUndoRedoEnabled();
	}

	public void redo() {
		//redoes the last action
		if (redoStack.isEmpty())
			return;

		if(previewAction != null) {
			Logger.log("reverting preview: "+previewAction);
			previewAction.revert();
			previewAction = null;
		}
		Action action = redoStack.pop();
		Logger.log("redoing action: " + action);

		action.execute();
		undoStack.push(action);
		updateUndoRedoEnabled();
	}

	public void updateUndoRedoEnabled() {
		parent.getMainEditorWindow().onUndoRedo(!undoStack.isEmpty(), !redoStack.isEmpty());
	}

	public void previewAction(Action action) {
		if(previewAction != null) {
			Logger.log("reverting preview: "+previewAction);
			previewAction.revert();
			previewAction = null;
		}
		Logger.log("previewing action: " + action);

		action.execute();
		previewAction = action;
		redoStack.clear();
		updateUndoRedoEnabled();
	}

	public void setDirtyListener(ObservableProperty.PropertyChangeListener<Boolean> listener) {
		if(dirtyChangeListener != null)
			script.detachDirtyListener(dirtyChangeListener);

		this.dirtyChangeListener = listener;
		script.attachDirtyListener(listener);
	}
	public void cleanup() {
		script.detachDirtyListener(dirtyChangeListener);
		dirtyChangeListener = null;
	}
}
