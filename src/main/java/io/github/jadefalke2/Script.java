package io.github.jadefalke2;

import io.github.jadefalke2.util.CorruptedScriptException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Script {

	private final String script;
	private final ArrayList<InputLine> inputLines = new ArrayList<>();

	public Script(String script) {

		this.script = script;

		try {
			prepareScript();
		} catch (CorruptedScriptException e) {
			e.printStackTrace();
		}

	}

	public Script (File file){
		this(fileToString(file));
	}

	public ArrayList<InputLine> getInputLines() {
		return inputLines;
	}

	public void insertLine(int row, InputLine inputLine){

		inputLines.add(row,inputLine);

		for (int i = row + 1; i < inputLines.size(); i++){
			inputLines.get(i).setLine(inputLines.get(i).getLine() + 1);
		}

	}

	/**
	 * prepares the script
	 * @throws CorruptedScriptException
	 */
	private void prepareScript () throws CorruptedScriptException {
		inputLines.clear();
		String[] lines = script.split("\n");

		int prevLine = 0;

		for (String line : lines) {

			InputLine currentInputLine = new InputLine(line);

			if (currentInputLine.getLine() <= prevLine){
				throw new CorruptedScriptException("Line numbers misordered");
			}

			if (prevLine + 1 != currentInputLine.getLine()) {
				for (int i = 0; i < currentInputLine.getLine() - prevLine - 1; i++){
					inputLines.add(InputLine.getEmpty(prevLine + 1 + i));
				}
			}

			inputLines.add(currentInputLine);

			prevLine = currentInputLine.getLine();
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
		Script tmp = new Script(InputLine.getEmpty(1).getFull());

		for (int i = 2; i < amount; i++){
			tmp.insertLine(i - 1,InputLine.getEmpty(i));
		}

		return tmp;
	}

	/**
	 * Takes in a file and converts it to a readble string
	 * @param file the original file
	 * @return the string
	 */
	public static String fileToString (File file){

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			return br.lines().map(sCurrentLine -> sCurrentLine + "\n").collect(Collectors.joining());
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// in case file is not being found -> throws an exception as well
		return "";
	}


}
