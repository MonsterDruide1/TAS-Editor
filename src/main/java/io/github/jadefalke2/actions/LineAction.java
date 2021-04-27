package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.CorruptedScriptException;

import javax.swing.table.DefaultTableModel;
import java.util.Arrays;

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
				deleteRows();
				break;

			case DELETE:

				break;
		}
	}

	private void cloneRows(){
		insertRows(Arrays.stream(rows).mapToObj(row -> script.getInputLines().get(row).clone()).toArray(InputLine[]::new));
	}

	private void adjustLines() {
		table.moveRow(table.getRowCount() - rows.length, table.getRowCount() - 1, rows[0] + rows.length);

		for (int i = rows[0] + rows.length; i < table.getRowCount(); i++){
			InputLine cLine = script.getInputLines().get(i);

			cLine.setFrame(cLine.getFrame() + rows.length);
			table.setValueAt(cLine.getFrame(),i,0);
		}
	}

	private void deleteRows(){

		for (int i = rows.length - 1; i >= 0; i--){

			int cRow = rows[i];

			script.getInputLines().remove(cRow);
			table.removeRow(cRow);
		}

		for (int i = rows[0]; i < table.getRowCount(); i++){

			InputLine curLine = script.getInputLines().get(i);
			curLine.setFrame(curLine.getFrame() - rows.length);

			table.setValueAt(script.getInputLines().get(i).getFrame(),i,0);
		}

	}

	private void insertRows(){

		for (int i = 0; i < rows.length; i++) {
			script.getInputLines().add(rows[0] + i + rows.length, InputLine.getEmpty(rows[0] + i + 1));
			table.addRow(script.getInputLines().get(rows[0] + i + rows.length).getArray());
		}

		adjustLines();
	}

	private void insertRows (InputLine[] inputLines){
		for (int i = 0; i < rows.length; i++) {
			script.getInputLines().add(rows[0] + i + rows.length, inputLines[i]);
			table.addRow(script.getInputLines().get(rows[0] + i + rows.length).getArray());
		}

		adjustLines();
	}



}
