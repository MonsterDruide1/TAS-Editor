package io.github.jadefalke2;

import java.util.ArrayList;

public class Script {

	private final String script;
	private final ArrayList<InputLine> inputLines = new ArrayList<>();

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

		int prevLine = 0;

		for (String line : lines) {
			InputLine currentInputLine = new InputLine(line);


			if (currentInputLine.getLine() <= prevLine){

				try {
					throw new Exception();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}else {

				if (prevLine + 1 != currentInputLine.getLine()) {
					for (int i = 0; i < currentInputLine.getLine() - prevLine - 1; i++){
						inputLines.add(InputLine.getEmpty(prevLine + 1 + i));
					}
				}

				inputLines.add(currentInputLine);

			}

			prevLine = currentInputLine.getLine();
		}
	}

	public String getFull (){
		StringBuilder sb = new StringBuilder();

		for (InputLine inputLine: inputLines){
			sb.append(inputLine.getFull()).append("\n");
		}

		return sb.toString();
	}

	public static Script getEmptyScript (int amount){
		Script tmp = new Script(InputLine.getEmpty(1).getFull());

		for (int i = 2; i < amount; i++){
			tmp.insertLine(i - 1,InputLine.getEmpty(i));
		}

		return tmp;
	}

}
