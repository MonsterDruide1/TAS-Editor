package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;

import java.util.Arrays;
import java.util.stream.IntStream;

public class LineAction implements Action{

	public enum Type {
		DELETE,
		INSERT,
		CLONE,
		REPLACE
	}

	private final Type type;
	private final Script script;
	private final int[] rows;
	private final InputLine[] replacementLines;
	private InputLine[] previousLines;

	public LineAction(Script script, int[] rows, Type type) {
		this(script, rows, null, type);
	}
	public LineAction(Script script, int[] rows, InputLine[] replacementLines, Type type) {
		this.script = script;
		this.rows = rows;
		this.type = type;
		this.replacementLines = replacementLines;
	}

	@Override
	public void execute() {
		previousLines = script.getLines(rows);

		switch (type) {
			case CLONE: cloneRows(); break;
			case DELETE: deleteRows(false); break;
			case INSERT: insertRows(); break;
			case REPLACE: replaceRows(); break;
		}

	}

	@Override
	public void revert() {
		switch (type){
			case CLONE:                       //fallthrough to INSERT
			case INSERT: deleteRows(true); break;
			case DELETE: insertRows(previousLines, rows); break;
			case REPLACE: revertReplaceRows(); break;
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
			insertEmptyRows(replacement.length - rows.length);
		if(rows.length > replacement.length) //too many frames -> remove redundant lines
			deleteRows(Arrays.copyOfRange(rows, replacement.length, rows.length));

		for(int i=0;i<replacement.length;i++){
			int row = i < rows.length ? rows[i] : rows[rows.length-1] + (i - rows.length) + 1;
			replaceRow(row, replacement[i]);
		}
	}

	private void replaceRow(int row, InputLine replacement){
		script.replaceRow(row, replacement);
	}

	private void cloneRows(){
		InputLine[] tmpLines = new InputLine[rows.length];

		for (int i = 0; i < rows.length; i++){
			tmpLines[i] = script.getLines()[rows[i]].clone();
		}

		insertRows(tmpLines, rows[rows.length-1]+1);
	}

	private void deleteRows(boolean undo){
		int[] toRemove = undo ? IntStream.range(rows[rows.length-1]+1, rows[rows.length-1]+1+replacementLines.length).toArray() : rows;
		deleteRows(toRemove);
	}
	private void deleteRows(int[] rows){
		for (int i = rows.length - 1; i >= 0; i--){
			int row = rows[i];
			script.removeRow(row);
		}
	}

	private void insertRows(){
		insertRows(replacementLines, rows[rows.length-1]+1);
	}
	private void insertEmptyRows(int amount) {
		InputLine[] tmpLines = new InputLine[amount];
		for (int i = 0; i < amount; i++){
			tmpLines[i] = InputLine.getEmpty();
		}
		insertRows(tmpLines, rows[rows.length-1]+1);
	}

	private void insertRows (InputLine[] inputLines, int index) {
		insertRows(inputLines, IntStream.range(index, index+inputLines.length).toArray());
	}
	private void insertRows (InputLine[] inputLines, int[] indices){
		for (int i = 0; i < inputLines.length; i++){
			script.insertRow(indices[i], inputLines[i]);
		}
	}

	@Override
	public String toString() {
		return "Line Action, at frames: " + Arrays.toString(rows) + "; " + type;
	}


}
