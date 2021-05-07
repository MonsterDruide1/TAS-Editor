package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;

import javax.swing.table.DefaultTableModel;
import java.util.Arrays;


public class LineAction implements Action{

	public enum Type {
		DELETE,
		INSERT,
		CLONE,
		REPLACE
	}

	private final Type type;
	private final DefaultTableModel table;
	private final Script script;
	private final int[] rows;
	private final InputLine[] replacementLines;
	private final InputLine[] previousLines;

	public LineAction(DefaultTableModel table, Script script, int[] rows, Type type) {
		this(table, script, rows, null, type);
	}
	public LineAction(DefaultTableModel table, Script script, int[] rows, InputLine[] replacementLines, Type type) {
		this.table = table;
		this.script = script;
		this.rows = rows;
		this.type = type;
		this.replacementLines = replacementLines;

		previousLines = Arrays.stream(rows).mapToObj(i -> script.getInputLines().get(i)).toArray(InputLine[]::new);
	}

	@Override
	public void execute() {
		switch (type) {
			case CLONE -> cloneRows();
			case DELETE -> deleteRows();
			case INSERT -> insertRows();
			case REPLACE -> replaceRows();
		}

	}

	@Override
	public void revert() {
		switch (type){
			case CLONE, INSERT -> deleteRows();
			case DELETE -> insertRows(previousLines);
			case REPLACE -> revertReplaceRows();
		}
	}



	private void replaceRows(){
		deleteRows();
		insertRows(replacementLines);
	}
	private void revertReplaceRows(){
		deleteRows(Arrays.stream(replacementLines).mapToInt(InputLine::getFrame).toArray());
		insertRows(previousLines);
	}

	private void adjustLines(int start, int amount) {
		for (int i = start; i < table.getRowCount(); i++){

			InputLine currentLine = script.getInputLines().get(i);

			currentLine.increaseFrameBy(amount);
			table.setValueAt(currentLine.getFrame(),i,0);
		}
	}


	private void cloneRows(){
		InputLine[] tmpLines = new InputLine[rows.length];

		for (int i = 0; i < rows.length; i++){
			tmpLines[i] = script.getInputLines().get(rows[0] + i).clone();
		}

		insertRows(tmpLines);
	}

	private void deleteRows(){
		deleteRows(rows);
	}
	private void deleteRows(int[] rows){

		for (int i = rows.length - 1; i >= 0; i--){
			int actualIndex = rows[0] + i;

			script.getInputLines().remove(actualIndex);
			table.removeRow(actualIndex);
		}

		adjustLines(rows[0], -rows.length);
	}

	private void insertRows(){
		InputLine[] tmpLines = new InputLine[rows.length];

		for (int i = 0; i < rows.length; i++){
			tmpLines[i] = InputLine.getEmpty(rows[0] + i);
		}

		insertRows(tmpLines);
	}

	private void insertRows (InputLine[] inputLines){

		for (int i = 0; i < inputLines.length; i++){
			int actualIndex = rows[0] + i;
			inputLines[i].setFrame(actualIndex);
			script.getInputLines().add(actualIndex, inputLines[i]);
			table.insertRow(actualIndex, script.getInputLines().get(actualIndex).getArray());
		}

		adjustLines(rows[0] + inputLines.length, inputLines.length);

	}


}
