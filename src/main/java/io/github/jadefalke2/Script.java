package io.github.jadefalke2;

import io.github.jadefalke2.components.TxtFileChooser;
import io.github.jadefalke2.stickRelatedClasses.JoystickPanel;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.*;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Script {

	/**
	 * returns an empty string with a specified length of lines
	 * @param amount the number of lines
	 * @return the created script
	 */
	public static Script getEmptyScript (int amount){
		Script tmp = new Script();

		for (int i = 0; i < amount; i++){
			tmp.insertRow(i,InputLine.getEmpty());
		}
		tmp.dirty.set(false);

		return tmp;
	}


	private ObservableProperty<File> file;
	private DefaultTableModel table;
	private final ArrayList<InputLine> inputLines;
	private final ObservableProperty<Boolean> dirty;
	private final List<ObservableProperty.PropertyChangeListener<Integer>> lengthListeners;

	public Script() {
		inputLines = new ArrayList<>();
		file = new ObservableProperty<>(null);
		dirty = new ObservableProperty<>(false);
		lengthListeners = new ArrayList<>();
	}
	public Script(String script) throws CorruptedScriptException {
		this();
		prepareScript(script);
	}
	public Script (File file) throws CorruptedScriptException, IOException {
		this(Util.fileToString(file));
		this.file.set(file);
	}

	/**
	 * prepares the script
	 * @throws CorruptedScriptException if lines are in the wrong order
	 */
	private void prepareScript (String script) throws CorruptedScriptException {
		inputLines.clear();
		String[] lines = script.split("\n");

		int currentFrame = 0;

		for (String line : lines) {

			InputLine currentInputLine = new InputLine(line);
			int frame = Integer.parseInt(line.split(" ")[0]);

			if (frame < currentFrame){
				throw new CorruptedScriptException("Line numbers misordered", currentFrame);
			}

			while(currentFrame < frame){
				inputLines.add(InputLine.getEmpty());
				currentFrame++;
			}

			inputLines.add(currentInputLine);

			currentFrame++;
		}
		updateLength(0);
	}

	public boolean closeScript(){
		if(!dirty.get()){
			return true; //just close without issue if no changes happened
		}

		int result = JOptionPane.showConfirmDialog(null, "Save Project changes?", "Save before closing", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.YES_OPTION){
			//opens a new dialog that asks about saving, then close
			try {
				saveFile();
			} catch(IOException ioe) {
				JOptionPane.showMessageDialog(null, "Failed to save file!\nError: " + ioe.getMessage(), "Saving failed", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			return dirty.get();
		}
		return result == JOptionPane.NO_OPTION; //otherwise return false -> cancel
	}

	/**
	 * Returns the whole script as a String
	 * @return the script as a string
	 */
	public String getFull (){
		return IntStream.range(0, inputLines.size()).mapToObj(i -> inputLines.get(i).getFull(i)+"\n").collect(Collectors.joining());
	}

	public void saveFile() throws IOException {
		saveFile(Settings.INSTANCE.directory.get());
	}

	/**
	 * Saves the script (itself) to that last saved/opened file
	 * @param defaultDir The directory to open the TxtFileChooser in if no file is stored
	 */
	public void saveFile(File defaultDir) throws IOException { //TODO don't have that as a parameter, as it is only passed down to the next layer...
		if(file.get() == null){
			saveFileAs(defaultDir);
			return;
		}

		Logger.log("saving script to " + file.get().getAbsolutePath());

		Util.writeFile(getFull(), file.get());
		dirty.set(false);
	}

	/**
	 * Opens a file selector popup and then saves the script (itself) to that file
	 * @param defaultDir The directory to open the TxtFileChooser in
	 */
	public void saveFileAs(File defaultDir) throws IOException {
		File savedFile = new TxtFileChooser(defaultDir).getFile(false);
		if(savedFile != null){
			Logger.log("saving file as " + savedFile.getAbsolutePath());
			file.set(savedFile);
			saveFile(defaultDir);
		}
	}

	public InputLine getLine(int row){
		return inputLines.get(row);
	}
	public InputLine[] getLines(int[] rows){
		return Arrays.stream(rows).mapToObj(inputLines::get).toArray(InputLine[]::new);
	}
	public InputLine[] getLines(){
		return inputLines.toArray(new InputLine[0]);
	}

	/**
	 * Used to set the table used to display the content of this script.
	 * @param table Displaying table
	 */
	public void setTable(DefaultTableModel table){
		this.table = table;
		fullTableUpdate();
	}

	/**
	 * Force a refresh of all data displayed in the table, removing everything first and then re-adding it.
	 */
	public void fullTableUpdate(){
		table.setRowCount(0);

		for (int i = 0; i < inputLines.size(); i++){
			table.addRow(inputLines.get(i).getArray(i));
		}
	}

	public void replaceRow(int row, InputLine replacement) {
		inputLines.set(row, replacement);
		Object[] tableArray = replacement.getArray(row);
		for(int i=0;i<tableArray.length;i++){
			table.setValueAt(tableArray[i], row, i);
		}
		dirty.set(true);
	}

	public void removeRow(int row){
		inputLines.remove(row);
		table.removeRow(row);
		adjustLines(row);
		dirty.set(true);
		updateLength(inputLines.size()+1);
	}

	public void insertRow(int row, InputLine line) {
		inputLines.add(row, line);
		if(table != null) table.insertRow(row, line.getArray(row));
		adjustLines(row);
		dirty.set(true);
		updateLength(inputLines.size()-1);
	}

	public void appendRow(InputLine line) {
		insertRow(inputLines.size(), line);
	}

	private void adjustLines(int start) {
		if(table == null) return;
		for (int i = start; i < table.getRowCount(); i++){
			table.setValueAt(i,i,0);
		}
	}

	public void toggleButton(int row, Button button) {
		int col = button.ordinal()+3; //+3 for FRAME, LStick, RStick ; TODO find a better way to do this

		if(!inputLines.get(row).buttons.contains(button)){
			inputLines.get(row).buttons.add(button);
			table.setValueAt(table.getColumnName(col), row, col);
		} else {
			inputLines.get(row).buttons.remove(button);
			table.setValueAt("", row, col);
		}
		dirty.set(true);
	}

	public void setStickPos(int row, JoystickPanel.StickType stickType, StickPosition position) {
		if(stickType == JoystickPanel.StickType.L_STICK)
			inputLines.get(row).setStickL(position);
		else
			inputLines.get(row).setStickR(position);
		table.setValueAt(position.toCartString(), row, stickType == JoystickPanel.StickType.L_STICK ? 1 : 2); //TODO find a better way to differentiate sticks?
		dirty.set(true);
	}

	public String getName() {
		return file.get() == null ? "unnamed script" : file.get().getName();
	}

	public boolean isDirty() {
		return dirty.get();
	}

	public void attachDirtyListener(ObservableProperty.PropertyChangeListener<Boolean> listener) {
		dirty.attachListener(listener);
	}
	public void detachDirtyListener(ObservableProperty.PropertyChangeListener<Boolean> listener) {
		dirty.detachListener(listener);
	}
	public void attachLengthListener(ObservableProperty.PropertyChangeListener<Integer> listener) {
		lengthListeners.add(listener);
	}
	public void detachLengthListener(ObservableProperty.PropertyChangeListener<Integer> listener) {
		lengthListeners.remove(listener);
	}
	public void attachFileListener(ObservableProperty.PropertyChangeListener<File> listener) {
		file.attachListener(listener);
	}
	public void detachFileListener(ObservableProperty.PropertyChangeListener<File> listener) {
		file.detachListener(listener);
	}
	public void updateLength(int before) {
		int after = inputLines.size();
		for(ObservableProperty.PropertyChangeListener<Integer> listener : lengthListeners) {
			listener.onChange(after, before);
		}
	}
}
