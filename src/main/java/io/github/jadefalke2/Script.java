package io.github.jadefalke2;

import io.github.jadefalke2.components.TxtFileChooser;
import io.github.jadefalke2.stickRelatedClasses.JoystickPanel;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.Button;
import io.github.jadefalke2.util.CorruptedScriptException;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.Util;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
			tmp.insertLine(i,InputLine.getEmpty());
		}

		return tmp;
	}


	private File file;
	private DefaultTableModel table;
	private final ArrayList<InputLine> inputLines;

	public Script() {
		inputLines = new ArrayList<>();
		file = null;
	}
	public Script(String script) throws CorruptedScriptException {
		this();
		prepareScript(script);
	}
	public Script (File file) throws CorruptedScriptException, IOException {
		this(Util.fileToString(file));
		this.file = file;
	}

	public void insertLine(int row, InputLine inputLine){
		inputLines.add(row,inputLine);
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
	}

	/**
	 * Returns the whole script as a String
	 * @return the script as a string
	 */
	public String getFull (){
		return IntStream.range(0, inputLines.size()).mapToObj(i -> inputLines.get(i).getFull(i)+"\n").collect(Collectors.joining());
	}

	/**
	 * Saves the script (itself) to that last saved/opened file
	 * @param defaultDir The directory to open the TxtFileChooser in if no file is stored
	 */
	public void saveFile(File defaultDir){ //TODO don't have that as a parameter, as it is only passed down to the next layer...
		if(file == null){
			saveFileAs(defaultDir);
			return;
		}

		Logger.log("saving script to " + file.getAbsolutePath());

		try {
			Util.writeFile(getFull(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens a file selector popup and then saves the script (itself) to that file
	 * @param defaultDir The directory to open the TxtFileChooser in
	 */
	public void saveFileAs(File defaultDir){
		try {
			File savedFile = new TxtFileChooser(defaultDir).saveFileAs(this);
			if(savedFile != null){
				Logger.log("saved file as " + savedFile.getAbsolutePath());
				file = savedFile;
			}
		} catch(IOException err){
			err.printStackTrace();
		}
	}

	public InputLine getLine(int row){
		return inputLines.get(row);
	}
	public InputLine[] getLines(int[] rows){
		return Arrays.stream(rows).mapToObj(inputLines::get).toArray(InputLine[]::new);
	}
	public InputLine[] getLines(){
		return inputLines.toArray(InputLine[]::new);
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
	}

	public void removeRow(int row){
		inputLines.remove(row);
		table.removeRow(row);
		adjustLines(row);
	}

	public void insertRow(int row, InputLine line) {
		inputLines.add(row, line);
		table.insertRow(row, line.getArray(row));
		adjustLines(row);
	}

	public void appendRow(InputLine line) {
		insertRow(inputLines.size()-1, line);
	}

	private void adjustLines(int start) {
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
	}

	public void setStickPos(int row, JoystickPanel.StickType stickType, StickPosition position) {
		inputLines.get(row).setStickL(position);
		table.setValueAt(position.toCartString(), row, stickType == JoystickPanel.StickType.L_STICK ? 1 : 2); //TODO find a better way to differentiate sticks?
	}
}
