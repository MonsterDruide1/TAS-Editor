package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.ObservableProperty;
import io.github.jadefalke2.util.ScriptObserver;
import io.github.jadefalke2.util.Settings;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class ScriptTab extends JPanel implements ScriptObserver {

	private final MainEditorWindow mainEditorWindow;
	private final Script script;
	private final JScrollPane scrollPane;
	private final PianoRoll pianoRoll;
	private final SideJoystickPanel sideJoystickPanel;

	private final JPanel editor;

	private final Stack<Action> undoStack;
	private final Stack<Action> redoStack;
	private Action previewAction;

	private ObservableProperty.PropertyChangeListener<Boolean> dirtyChangeListener;
	private ObservableProperty.PropertyChangeListener<Integer> lengthChangeListener;
	private ObservableProperty.PropertyChangeListener<File> fileChangeListener;

	public ScriptTab(MainEditorWindow mainEditorWindow, Script script, ObservableProperty.PropertyChangeListener<Integer> listener) {
		this.mainEditorWindow = mainEditorWindow;
		this.script = script;

		//initialising stacks
		undoStack = new Stack<>();
		redoStack = new Stack<>();

		pianoRoll = new PianoRoll(script, this);
		sideJoystickPanel = new SideJoystickPanel(this, pianoRoll, script);

		editor = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Components
		scrollPane = new JScrollPane(pianoRoll);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		editor.add(scrollPane, c);

		refreshLayout();
		mainEditorWindow.enableUndoRedo(false, false);

		this.lengthChangeListener = listener;
		listener.onChange(script.getLines().length, -1);

		Settings.INSTANCE.joystickPanelPosition.attachListener(ignored -> refreshLayout());

		script.attachObserver(this);
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

		if(Settings.INSTANCE.joystickPanelPosition.get() != Settings.JoystickPanelPosition.HIDDEN) {
			if(Settings.INSTANCE.joystickPanelPosition.get() == Settings.JoystickPanelPosition.LEFT)
				c.gridx = 0;
			else if(Settings.INSTANCE.joystickPanelPosition.get() == Settings.JoystickPanelPosition.RIGHT)
				c.gridx = 2;
			c.weightx = 0;
			add(sideJoystickPanel, c);
		}

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
		script.saveFile();
	}
	public void saveFileAs() throws IOException {
		script.saveFileAs();
	}
	public void saveFileCopy() throws IOException {
		script.saveFileCopy();
	}
	public boolean closeScript() {
		return script.closeScript();
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
		mainEditorWindow.onUndoRedo(!undoStack.isEmpty(), !redoStack.isEmpty());
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
		this.dirtyChangeListener = listener;
	}
	public void setFileListener(ObservableProperty.PropertyChangeListener<File> listener) {
		this.fileChangeListener = listener;
	}
	public void cleanup() {
		script.detachObserver(this);
	}

	@Override
	public void onFileChange(File file) {
		fileChangeListener.onChange(file);
	}
	@Override
	public void onLengthChange(int length) {
		lengthChangeListener.onChange(length);
	}
	@Override
	public void onDirtyChange(boolean dirty) {
		dirtyChangeListener.onChange(dirty);
	}

	public Script getScript() {
		return script;
	}

	public void setSelectedLines(int[] selectedLines) {
		pianoRoll.setSelectedRows(selectedLines);
	}

	public MainEditorWindow getWindow() {
		return mainEditorWindow;
	}
}
