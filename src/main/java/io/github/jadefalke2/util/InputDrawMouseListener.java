package io.github.jadefalke2.util;

import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.actions.ButtonAction;
import io.github.jadefalke2.components.PianoRoll;
import io.github.jadefalke2.components.ScriptTab;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InputDrawMouseListener extends MouseAdapter {


	/**
	 * Enum used to check the mode, so it won't delete when it's adding
	 */
	private enum Mode {
		ADDING, IDLE, REMOVING
	}

	// row where the dragging gesture started and ended
	private int startRow, endRow;

	// detect the column
	private int drawingCol;

	// the table
	private final PianoRoll table;

	// mode
	private Mode mode = Mode.IDLE;

	private final ScriptTab scriptTab;

	/**
	 * Constructor
	 * @param table the table the data is being read into
	 */
	public InputDrawMouseListener (PianoRoll table, ScriptTab scriptTab){
		this.table = table;
		this.scriptTab = scriptTab;
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

			drawingCol = -1;
			startRow = -1;

			return;
		}

		// keep current values
		if(e.isShiftDown()) {
			updateEnd(row);
		} else {
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
					mode = table.getModel().getValueAt(row, col) != "" ? Mode.REMOVING : Mode.ADDING;
					updateEnd(row);
			}
		}

	}

	/**
	 * returns the cell at which the mouse click occurs
	 * @param e the mouse event
	 * @return a point with the cell data (col:row)
	 */
	private int[] getCell (MouseEvent e){
		int row = table.rowAtPoint(e.getPoint());
		int col = table.getColumnModel().getColumn(table.columnAtPoint(e.getPoint())).getModelIndex();

		return new int[]{row,col};
	}

	private void updateEnd(int row) {
		if(endRow != row) {
			endRow = row;
			applyPreview();
		}
	}

	private void applyPreview (){
		Action action = getAction();
		if(action != null) scriptTab.previewAction(action);
	}
	private Action getAction() {
		if(drawingCol == -1 || startRow == -1 || endRow == -1 || drawingCol < 3) return null;
		return new ButtonAction(table.getScript(), Button.values()[drawingCol-3], mode == Mode.ADDING, Math.min(startRow, endRow), Math.max(startRow, endRow));
	}


	// Overriding Methods

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		int row = getCell(e)[0];
		if(row == -1) return;

		updateEnd(row);
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		reactToMousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e){
		Action action = getAction();
		if(action != null) scriptTab.executeAction(action);
		startRow = getCell(e)[0]; // for SHIFT-click (also do not reset drawingCol, mode)
	}

}
