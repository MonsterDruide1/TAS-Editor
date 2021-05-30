package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;

import javax.swing.table.DefaultTableModel;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;


public class LineAction implements Action{

	public enum Type {
		DELETE,
		INSERT,
		CLONE,
		REPLACE,
		COPY,
		PASTE,
		CUT
	}

	private final TAS parent;

	private final Type type;
	private final DefaultTableModel table;
	private final Script script;
	private final int[] rows;
	private final InputLine[] replacementLines;
	private final InputLine[] previousLines;

	public LineAction(TAS parent, DefaultTableModel table, Script script, int[] rows, Type type) {
		this(parent,table, script, rows, null, type);
	}
	public LineAction(TAS parent, DefaultTableModel table, Script script, int[] rows, InputLine[] replacementLines, Type type) {
		this.parent = parent;
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
			case CUT -> parent.cut();
			case COPY -> parent.copy();
			case PASTE -> {
				try {
					parent.paste();
				} catch (IOException | UnsupportedFlavorException exception) {
					exception.printStackTrace();
				}
			}
		}

	}

	@Override
	public void revert() {
		switch (type){
			case CLONE, INSERT -> deleteRows();
			case DELETE -> insertRows(previousLines, rows[0]);
			case REPLACE -> revertReplaceRows();
		}
	}



	private void replaceRows(){
		replaceRows(rows, replacementLines);
	}
	private void revertReplaceRows(){
		int[] rowsToReplace = new int[replacementLines.length];
		for(int i=0;i< replacementLines.length;i++){
			rowsToReplace[i] = i < rows.length ? rows[i] : rows[rows.length-1] + (i - rows.length);
		}
		replaceRows(rowsToReplace, previousLines);
	}

	private void replaceRows(int[] rows, InputLine[] replacement){
		if(rows.length < replacement.length) //missing frames -> add emptys that there are enough to replace
			insertRows(replacement.length - rows.length);
		if(rows.length > replacement.length) //too many frames -> remove redundant lines
			deleteRows(Arrays.copyOfRange(rows, replacement.length, rows.length));

		for(int i=0;i<replacement.length;i++){
			int row = i < rows.length ? rows[i] : rows[rows.length-1] + (i - rows.length) + 1;
			replaceRow(row, replacement[i]);
		}
	}

	private void replaceRow(int row, InputLine replacement){
		script.getInputLines().set(row, replacement);
		Object[] tableArray = replacement.getArray(row);
		for(int i=0;i<tableArray.length;i++){
			table.setValueAt(tableArray[i], row, i);
		}
	}

	private void adjustLines(int start, int amount) {
		for (int i = start; i < table.getRowCount(); i++){
			table.setValueAt(i,i,0);
		}
	}

	private void cloneRows(){
		InputLine[] tmpLines = new InputLine[rows.length];

		for (int i = 0; i < rows.length; i++){
			tmpLines[i] = script.getInputLines().get(rows[0] + i).clone();
		}

		insertRows(tmpLines, rows[rows.length-1]+1);
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
		insertRows(rows.length);
	}
	private void insertRows(int amount){
		InputLine[] tmpLines = new InputLine[amount];

		for (int i = 0; i < amount; i++){
			tmpLines[i] = InputLine.getEmpty();
		}

		insertRows(tmpLines, rows[rows.length-1]+1);
	}

	private void insertRows (InputLine[] inputLines, int index){
		for (int i = 0; i < inputLines.length; i++){
			int actualIndex = index + i;
			script.getInputLines().add(actualIndex, inputLines[i]);
			table.insertRow(actualIndex, script.getInputLines().get(actualIndex).getArray(actualIndex));
		}

		adjustLines(index + inputLines.length, inputLines.length);
	}


}
