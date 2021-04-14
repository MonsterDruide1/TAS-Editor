package io.github.jadefalke2;

import javax.swing.table.DefaultTableModel;

public class LineAction implements Action{

	enum Type {
		DELETE,INSERT,CLONE;
	}

	Type type;
	DefaultTableModel table;
	Script script;
	int row;
	InputLine previousLine;

	public LineAction(DefaultTableModel table, Script script, int row, Type type) {
		previousLine = new InputLine(script.getInputLines().get(row).getFull());
		this.table = table;
		this.script = script;
		this.row = row;
		this.type = type;
	}

	@Override
	public void execute() {
		switch (type){
			case CLONE:
				cloneRow();
				break;

			case DELETE:
				deleteRow();
				break;

			case INSERT:
				insertRow();
				break;
		}
	}

	@Override
	public void revert() {
		switch (type){
			case CLONE:
			case INSERT:
				deleteRow();
				break;

			case DELETE:
				insertRow(previousLine);
				break;
		}
	}

	private void cloneRow(){
		InputLine tmpLine = new InputLine(script.getInputLines().get(row).getFull());
		insertRow(tmpLine);
	}

	private void deleteRow(){
		script.getInputLines().remove(row);
		table.removeRow(row);

		for (int i = row; i < table.getRowCount(); i++){
			InputLine curLine = script.getInputLines().get(i);
			curLine.setLine(curLine.getLine() - 1);

		}

		for (int i = row; i < table.getRowCount(); i++){
			table.setValueAt(script.getInputLines().get(i).getLine(),i,0);
		}
	}

	private void insertRow (){
		insertRow(InputLine.getEmpty(row));
	}

	private void insertRow (InputLine inputLine){
		script.insertLine(row,inputLine);

		table.addRow(inputLine.getArray());
		table.moveRow(table.getRowCount() - 1, table.getRowCount() - 1, row);

		for (int i = row; i < table.getRowCount(); i++){
			table.setValueAt(script.getInputLines().get(i).getLine(),i, 0);
		}
	}

}
