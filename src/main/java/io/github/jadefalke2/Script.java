package io.github.jadefalke2;

import io.github.jadefalke2.util.CorruptedScriptException;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Script {

	private final ArrayList<InputLine> inputLines = new ArrayList<>();

	public Script(){}

	public Script(String script) throws CorruptedScriptException {
		prepareScript(script);
	}

	public Script (File file) throws CorruptedScriptException, FileNotFoundException {
		this(Util.fileToString(file));
	}

	public ArrayList<InputLine> getInputLines() {
		return inputLines;
	}

	public void insertLine(int row, InputLine inputLine){

		inputLines.add(row,inputLine);

		for (int i = row + 1; i < inputLines.size(); i++){
			inputLines.get(i).setFrame(inputLines.get(i).getFrame() + 1);
		}

	}

	/**
	 * prepares the script
	 * @throws CorruptedScriptException
	 */
	private void prepareScript (String script) throws CorruptedScriptException {
		inputLines.clear();
		String[] lines = script.split("\n");

		int currentFrame = 0;

		for (String line : lines) {

			InputLine currentInputLine = new InputLine(line);

			if (currentInputLine.getFrame() < currentFrame){
				throw new CorruptedScriptException("Line numbers misordered");
			}

			while(currentFrame < currentInputLine.getFrame()){
				inputLines.add(InputLine.getEmpty(currentFrame++));
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
		return inputLines.stream().map(inputLine -> inputLine.getFull() + "\n").collect(Collectors.joining());
	}

	/**
	 * returns an empty string with a specified length of lines
	 * @param amount the number of lines
	 * @return the created script
	 */
	public static Script getEmptyScript (int amount){
		Script tmp = new Script();

		for (int i = 0; i < amount; i++){
			tmp.insertLine(i,InputLine.getEmpty(i+1));
		}

		return tmp;
	}
}
