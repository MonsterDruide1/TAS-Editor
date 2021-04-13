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

	private void prepareScript (){
		inputLines.clear();
		String[] lines = script.split("\n");

		for (String line : lines) {
			inputLines.add(new InputLine(line));
		}
	}

}
