package io.github.jadefalke2;

import java.util.ArrayList;

public class Script {

	private String script;
	ArrayList<InputLine> inputLines = new ArrayList<>();

	public Script(String script) {
		this.script = script;
		prepareScript();
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

	private void prepareScript (){
		inputLines.clear();
		String[] lines = script.split("\n");

		for (String line : lines) {
			inputLines.add(new InputLine(line));
		}
	}

}
