package io.github.jadefalke2.components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.LineAction;
import io.github.jadefalke2.util.Button;
import io.github.jadefalke2.util.InputDrawMouseListener;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class PianoRoll extends JTable {

	//script
	private Script script;

	// table model
	private final DefaultTableModel model = new DefaultTableModel();
	private final TAS parent;


	public PianoRoll (TAS parent, Script script){

		this.parent = parent;
		this.script = script;

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );

		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
		setModel(model);
		setDragEnabled(false);
		setRowHeight(20);
		setShowGrid(true);
		setFillsViewportHeight(true);
		setFont(new Font("Arial", Font.PLAIN, 15));

		getTableHeader().setResizingAllowed(false);
		getTableHeader().setReorderingAllowed(false);
		getTableHeader().setDefaultRenderer(centerRenderer);

		// add all the column's corresponding with their names
		model.addColumn("Frame");
		model.addColumn("L-Stick");
		model.addColumn("R-Stick");

		for (Button button : Button.values()) {
			model.addColumn(button.toString());
		}

		//Center all columns
		for (int i = 0; i < getColumnCount(); i++){
			getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		// Mouse listener
		InputDrawMouseListener mouseListener = new InputDrawMouseListener(this, parent);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		setScript(script);

		getInputMap().put(KeyStroke.getKeyStroke("ctrl c"), "copy");
		getInputMap().put(KeyStroke.getKeyStroke("ctrl v"), "paste");
		getInputMap().put(KeyStroke.getKeyStroke("ctrl x"), "cut");
		getActionMap().put("copy", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.copy();
			}
		});
		getActionMap().put("paste", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					parent.paste();
				} catch (IOException | UnsupportedFlavorException ex) {
					ex.printStackTrace();
				}
			}
		});
		getActionMap().put("cut", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.cut();
			}
		});

		adjustColumnWidth();
	}

	/**
	 * opens the popup menu with the line actions
	 * @param rows all rows affected by this method
	 * @param point the point at which the menu "spawns"
	 */
	public void openPopUpMenu(int[] rows, Point point){
		new LineRightClickMenu(parent, script, model).openPopUpMenu(rows, point,this);
	}

	public void adjustColumnWidth(){
		// adjust the size of all columns

		int[] columnsWidth = {
			45,											   		                // frame number
			85, 85,										  	                	// sticks
			18, 18, 18, 18, 25, 25, 25, 25, 40, 40, 35, 35, 35, 35, 45, 45	    // buttons
		};

		for (int i = 0; i < columnsWidth.length && i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		}
	}


	/**
	 * adds an empty line to the end of the current script
	 */
	public void addEmptyRow() {
		InputLine newEmpty = InputLine.getEmpty();
		script.appendRow(newEmpty);
	}

	public void deleteSelectedRows(){
		parent.executeAction(new LineAction(script, getSelectedRows(), LineAction.Type.DELETE));
	}

	public void replaceSelectedRows(InputLine[] rows){
		parent.executeAction(new LineAction(script, getSelectedRows(), rows, LineAction.Type.REPLACE));
	}

	public InputLine[] getSelectedInputRows(){
		return script.getLines(getSelectedRows());
	}

	/**
	 * returns the table model
	 * @return table model
	 */
	public DefaultTableModel getModel (){
		return model;
	}

	/**
	 * sets the script to a new script
	 * @param script the new script
	 */
	public void setScript (Script script){
		this.script = script;
		script.setTable(model);
	}

	/**
	 * Overwriting the table to make all cells uneditable -> might change in the future due to functions
	 * @param row the row
	 * @param column the column
	 * @return false
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

}
