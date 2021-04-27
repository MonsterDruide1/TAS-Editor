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
		this(fileToString(file));
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

		int prevLine = 0;

		for (String line : lines) {

			InputLine currentInputLine = new InputLine(line);

			if (currentInputLine.getFrame() <= prevLine){
				throw new CorruptedScriptException("Line numbers misordered");
			}

			if (prevLine + 1 != currentInputLine.getFrame()) {
				for (int i = 0; i < currentInputLine.getFrame() - prevLine - 1; i++){
					inputLines.add(InputLine.getEmpty(prevLine + 1 + i));
				}
			}

			inputLines.add(currentInputLine);

			prevLine = currentInputLine.getFrame();
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

	/**
	 * Takes in a file and converts it to a readble string
	 * @param file the original file
	 * @return the string
	 */
	public static String fileToString (File file) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		return br.lines().map(sCurrentLine -> sCurrentLine + "\n").collect(Collectors.joining());
	}


}
