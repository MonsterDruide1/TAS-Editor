package io.github.jadefalke2;

import io.github.jadefalke2.actions.CellAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class InputDrawMouseListener extends MouseAdapter {


	ArrayList<Integer> rows = new ArrayList<>();
	PianoRoll table;


	public InputDrawMouseListener (PianoRoll table){
		this.table = table;
	}

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

	public void mouseReleased(MouseEvent e){
		rows.clear();
	}


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
				setCell(new Point(col,row));
		}
	}

	private Point getCell (MouseEvent e){
		int row = table.rowAtPoint(e.getPoint());
		int col = table.columnAtPoint(e.getPoint());

		return new Point(col,row);
	}

	private void setCell (Point point){
		TAS.getInstance().executeAction(new CellAction(table.getModel(), table.getScript(),(int)point.getY(),(int)point.getX()));
	}
}
