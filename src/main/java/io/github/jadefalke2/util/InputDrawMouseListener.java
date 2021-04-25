package io.github.jadefalke2.util;

import io.github.jadefalke2.Components.PianoRoll;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.CellAction;

import java.awt.*;
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

	// the table
	private final PianoRoll table;

	// mode
	private Mode mode = Mode.IDLE;

	/**
	 * Constructor
	 * @param table the table the data is being read into
	 */
	public InputDrawMouseListener (PianoRoll table){
		this.table = table;
	}

	/**
	 * is called on mouse click. Switches between the columns and executes the corresponding action
	 * @param e the mouseEvent
	 */
	private void reactToMouseClick (MouseEvent e){
		int row = table.rowAtPoint(e.getPoint());
		int col = table.columnAtPoint(e.getPoint());

		rows.add((int)getCell(e).getY());

		switch (col){

			case 0:
				if (e.getButton() == MouseEvent.BUTTON3 && table.isRowSelected(row)) {
					table.openPopUpMenu(table.getSelectedRows(),e.getPoint());
				}
				break;

			case 1: case 2:
				table.openStickWindow(row,col,table.getScript());
				break;

			default:
				mode = table.getValueAt(row, col) != " " ? Mode.ADDING : Mode.REMOVING;
				setCell(new Point(col,row));
		}
	}

	/**
	 * returns the cell at which the mouse click occurs
	 * @param e the mouse event
	 * @return a point with the cell data (col:row)
	 */
	private Point getCell (MouseEvent e){
		int row = table.rowAtPoint(e.getPoint()) < 0 ? 0 : Math.min(table.rowAtPoint(e.getPoint()), table.getRowCount());
		int col = table.columnAtPoint(e.getPoint());

		return new Point(col,row);
	}

	/**
	 * edits the cell at the given point
	 * @param point the corresponding point (cell)
	 */
	private void setCell (Point point){
		TAS.getInstance().executeAction(new CellAction(table.getModel(), table.getScript(),(int)point.getY(),(int)point.getX()));
	}


	// Overwriting Methods

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		if (!rows.contains((int)getCell(e).getY())){
			rows.add((int)getCell(e).getY());
			setCell(getCell(e));
		}
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		reactToMouseClick(e);
	}

	@Override
	public void mouseReleased(MouseEvent e){
		rows.clear();
		mode = Mode.IDLE;
	}

}
