package io.github.jadefalke2;

import io.github.jadefalke2.components.TxtFileChooser;
import io.github.jadefalke2.util.CorruptedScriptException;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.Util;

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


	private File file = null;
	private final ArrayList<InputLine> inputLines = new ArrayList<>();

	public Script(){}
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

	public InputLine[] getLines(int[] rows){
		return Arrays.stream(rows).mapToObj(inputLines::get).toArray(InputLine[]::new);
	}
	public InputLine[] getLines(){
		return inputLines.toArray(InputLine[]::new);
	}

	//TODO remove this and handle all accesses to that with functions
	public ArrayList<InputLine> getInputLines() {
		return inputLines;
	}
}
