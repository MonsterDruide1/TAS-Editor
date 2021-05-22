package io.github.jadefalke2.util;

import io.github.jadefalke2.components.PianoRoll;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.CellAction;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class InputDrawMouseListener extends MouseAdapter {


	/**
	 * Enum used to check the mode, so it won't delete when it's adding
	 */
	private enum Mode {
		ADDING, IDLE, REMOVING
	}

	// keeps track of already visited rows
	private final ArrayList<Integer> rows = new ArrayList<>();

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
	 * is called on mouse click. Switches between the columns and executes the corresponding action
	 * @param e the mouseEvent
	 */
	private void reactToMouseClick (MouseEvent e){
		int row = table.rowAtPoint(e.getPoint());
		int col = table.columnAtPoint(e.getPoint());

		rows.add(getCell(e)[0]);

		switch (col){
			case 0:
				if (e.getButton() == MouseEvent.BUTTON3 && table.isRowSelected(row)) {
					table.openPopUpMenu(table.getSelectedRows(),e.getPoint());
				}
				break;

			case 1:
			case 2:
				//TODO set joystick to correct L/R -> what to do when clicked somewhere else?
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

		if (row == -1 || col == -1) return;

		if ((table.getValueAt(row, col).equals("") && (mode == Mode.ADDING)) || (!table.getValueAt(row, col).equals("") && (mode == Mode.REMOVING))){
			parent.executeAction(new CellAction(table.getModel(), table.getScript(),row,col));
		}

	}


	// Overriding Methods

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		if (!rows.contains(getCell(e)[0])){
			rows.add(getCell(e)[0]);
			setCell(getCell(e)[0],drawingCol);
		}
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		drawingCol = getCell(e)[1];
		reactToMouseClick(e);
	}

	@Override
	public void mouseReleased(MouseEvent e){
		rows.clear();
		mode = Mode.IDLE;
	}

}
