package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.CorruptedScriptException;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class LineAction implements Action{

	public enum Type {
		DELETE,
		INSERT,
		CLONE
	}

	private final Type type;
	private final DefaultTableModel table;
	private final Script script;
	private final int[] rows;
	private final InputLine[] previousLines;

	public LineAction(DefaultTableModel table, Script script, int[] rows, Type type) {

		previousLines = new InputLine[rows.length];

		for (int i = 0; i < rows.length; i++){
			previousLines[i] = script.getInputLines().get(i).clone();
		}

		this.table = table;
		this.script = script;
		this.rows = rows;
		this.type = type;
	}

	//TODO UNDO

	@Override
	public void execute() {
		switch (type){
			case CLONE:
				cloneRows();
				break;

			case DELETE:
				deleteRows();
				break;

			case INSERT:
				insertRows();
				break;
		}

	}

	@Override
	public void revert() {
		switch (type){
			case CLONE:
			case INSERT:
				//deleteRows();
				break;

			case DELETE:
				//
				break;
		}
	}

	private void cloneRows(){
		insertRows(Arrays.stream(rows).mapToObj(row -> script.getInputLines().get(row).clone()).toArray(InputLine[]::new));
	}

	private void adjustLines(int offset, int amount) {

		for (int i = rows[0] + offset; i < table.getRowCount(); i++){

			InputLine cLine = script.getInputLines().get(i);

			cLine.setFrame(cLine.getFrame() + amount);
			table.setValueAt(cLine.getFrame(),i,0);
		}
	}

	private void deleteRows(){

		int start = rows[0];
		int end = rows[rows.length - 1];


		for (int i = start; i <= end; i++){
			table.removeRow(rows[0]);
			script.getInputLines().remove(rows[0]);
		}
		
		adjustLines(0, -rows.length);
	}

	private void insertRows(){
		//intelliJ suggested this, don't ask me how it works.
		InputLine[] inputLines = IntStream.range(0, rows.length).mapToObj(i -> InputLine.getEmpty(rows[0] + i)).toArray(InputLine[]::new);
		insertRows(inputLines);
	}

	private void insertRows (InputLine[] inputLines){

		for (int i = 0; i < rows.length; i++) {
			script.getInputLines().add(rows[0] + i + rows.length, inputLines[i]);
			table.addRow(script.getInputLines().get(rows[0] + i + rows.length).getArray());
		}

		adjustLines(rows.length, rows.length);
	}

}
