package io.github.jadefalke2.util;

import io.github.jadefalke2.components.PianoRoll;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.CellAction;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InputDrawMouseListener extends MouseAdapter {


	/**
	 * Enum used to check the mode, so it won't delete when it's adding
	 */
	private enum Mode {
		ADDING, IDLE, REMOVING
	}

	// row where the dragging gesture started
	private int startRow;

	// detect the column
	private int drawingCol;

	// the table
	private final PianoRoll table;

	// mode
	private Mode mode = Mode.IDLE;

	private final TAS parent;

	/**
	 * Constructor
	 * @param table the table the data is being read into
	 */
	public InputDrawMouseListener (PianoRoll table, TAS parent){
		this.table = table;
		this.parent = parent;
	}

	/**
	 * is called on mouse down. Switches between the columns and executes the corresponding action
	 * @param e the mouseEvent
	 */
	private void reactToMousePressed (MouseEvent e){
		int[] cell = getCell(e);
		int row = cell[0];
		int col = cell[1];

		//TODO maybe find a cleaner/better way to do this?
		if(row == -1 || col == -1) { //clicked out of bounds -> deselect all
			table.clearSelection();
			//set it to the last one so if you start dragging from below, it will start selecting
			table.getSelectionModel().setAnchorSelectionIndex(table.getRowCount()-1);
			return;
		}

		startRow = row;
		drawingCol = col;

		switch (col){
			case 0:
			case 1:
			case 2:
				if (e.getButton() == MouseEvent.BUTTON3 && table.isRowSelected(row)) {
					table.openPopUpMenu(table.getSelectedRows(),e.getPoint());
				}
				break;

			default:
				mode = table.getValueAt(row, col) != "" ? Mode.REMOVING : Mode.ADDING;
				setCell(row,col);
		}
	}

	/**
	 * returns the cell at which the mouse click occurs
	 * @param e the mouse event
	 * @return a point with the cell data (col:row)
	 */
	private int[] getCell (MouseEvent e){
		int row = table.rowAtPoint(e.getPoint());
		int col = table.columnAtPoint(e.getPoint());

		return new int[]{row,col};
	}

	/**
	 * edits the cell at the given point
	 * @param col the column
	 * @param row the row
	 */
	private void setCell (int row, int col){
		setCell(row, col, false);
	}
	private void applyPreview (){

		if (row == -1 || col == -1) return;

		if (((table.getValueAt(row, col).equals("") && (mode == Mode.ADDING)) || (!table.getValueAt(row, col).equals("") && (mode == Mode.REMOVING))) != inverse){
			parent.executeAction(new CellAction(parent.getScript(),row,col));
		}

	}


	// Overriding Methods

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		int row = getCell(e)[0];
		if(row == -1) return;

		if(currentEndRow != row) {
			if(Math.abs(currentEndRow-startRow) < Math.abs(row-startRow)) { // additional row selected
				setCell(row, drawingCol);
			} else { // row deselected
				setCell(currentEndRow, drawingCol, true);
			}
			currentEndRow = row;
		}
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		reactToMousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e){
		mode = Mode.IDLE;
		startRow = -1;
		drawingCol = -1;
	}

}
