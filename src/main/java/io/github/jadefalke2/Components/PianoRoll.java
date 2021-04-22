package io.github.jadefalke2.Components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.LineAction;
import io.github.jadefalke2.stickRelatedClasses.StickImagePanel;
import io.github.jadefalke2.stickRelatedClasses.StickImagePanel.StickType;
import io.github.jadefalke2.util.InputDrawMouseListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import static io.github.jadefalke2.stickRelatedClasses.StickImagePanel.StickType.L_STICK;
import static io.github.jadefalke2.stickRelatedClasses.StickImagePanel.StickType.R_STICK;

public class PianoRoll extends JTable {

	//used to test if a new window can be opened
	private boolean stickWindowIsOpen;

	//script
	private final Script script;

	//Components
	private final JPopupMenu popupMenu = new JPopupMenu();

	private final JMenuItem deleteOption = new JMenuItem("delete");
	private final JMenuItem insertOption = new JMenuItem("insert");
	private final JMenuItem cloneOption = new JMenuItem("clone");

	// table model
	private final DefaultTableModel model = new DefaultTableModel();

	// the names of the columns of the table -> order CAN be changed
	private final String[] columnNames = {
		"frame", "L-stick", "R-Stick", "A", "B", "X", "Y", "ZR", "ZL", "R", "L", "+", "-", "DR", "DL", "DU", "DD", "L-stick", "R-Stick",
	};

	public PianoRoll (Script script){

		this.script = script;

		setAutoResizeMode(AUTO_RESIZE_OFF);
		setPreferredSize(new Dimension(700,800));
		setPreferredScrollableViewportSize(getPreferredSize());
		setFillsViewportHeight(false);
		setModel(model);
		setDragEnabled(false);

		getTableHeader().setResizingAllowed(false);
		getTableHeader().setReorderingAllowed(false);

		// add all the column's corresponding with their names
		for (String colName : columnNames) {
			model.addColumn(colName);
		}

		// adjust the size of all columns

		int[] columnsWidth = {
			45,											   		// frame number
			85,85,										  		// sticks
			18,18,18,18,25,25,18,18,18,18,30,30,30,30,50,50	    // buttons
		};

		for (int i = 0; i < columnsWidth.length && i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		}

		// Mouse listener
		InputDrawMouseListener mouseListener = new InputDrawMouseListener(this);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		// insert all existing rows
		for (int i = 0; i < script.getInputLines().size(); i++) {
			InputLine currentLine = script.getInputLines().get(i);
			addRow(currentLine, i + 1, model);
		}

		preparepopUpMenu();

	}


	/**
	 * adds an empty line to the end of the script
	 * @param script the script which the line is being added to
	 */
	public void addEmptyRow(Script script) {
		script.getInputLines().add(new InputLine((script.getInputLines().size() + 1) + " NONE 0;0 0;0"));
		InputLine currentLine = script.getInputLines().get(script.getInputLines().size() - 1);
		addRow(currentLine, script.getInputLines().size(), model);
	}

	/**
	 * adds a line at a specified place with specified contents
	 * @param line the InputLine that is being inserted
	 * @param lineIndex the place at which the line is being inserted
	 * @param model the table model
	 */
	private void addRow(InputLine line, int lineIndex, DefaultTableModel model) {

		Object[] tmp = new Object[columnNames.length];

		tmp[0] = lineIndex;
		tmp[1] = line.getStickL();
		tmp[2] = line.getStickR();

		for (int j = 3; j < tmp.length; j++) {
			if (line.getButtonsEncoded().contains(columnNames[j])) {
				tmp[j] = columnNames[j];
			} else {
				tmp[j] = " ";
			}
		}

		model.addRow(tmp);
	}

	/**
	 * Opens the Window to control the stick input
	 * @param row the row the window corresponds to
	 * @param col the column the window corresponds to (1 -> LSTICK; 2 -> RSTICK)
	 * @param script
	 */
	public void openStickWindow (int row,int col, Script script){

		StickType stickType = col == 1 ? L_STICK : R_STICK;
		JFrame stickWindow;

		if (!stickWindowIsOpen) {
			stickWindowIsOpen = true;

			stickWindow = new JFrame();

			stickWindow.setResizable(false);
			stickWindow.setVisible(true);
			stickWindow.setSize(300, 500);
			stickWindow.setLocation(new Point(200, 200));

			// Creates StickImagePanel
			InputLine tmpCurrentInputLine = script.getInputLines().get(row);
			StickImagePanel stickImagePanel = new StickImagePanel(stickType == L_STICK ? tmpCurrentInputLine.getStickL() : tmpCurrentInputLine.getStickR(), stickType, script, model, row);


			stickWindow.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					stickWindowIsOpen = false;
					setValueAt(stickImagePanel.getStickPos().toString(),row,col);
					e.getWindow().dispose();
				}
			});

			// adds the panel to the frame
			stickWindow.add(stickImagePanel);
		}

	}

	/**
	 * opens the popup menu with the line actions
	 * @param rows all rows affected by this method
	 * @param point the point at which the menu "spawns"
	 */
	public void openPopUpMenu(int[] rows, Point point){

		if (deleteOption.getActionListeners().length != 0){
			deleteOption.removeActionListener(deleteOption.getActionListeners()[0]);
		}
		deleteOption.addActionListener(e -> {
			TAS.getInstance().executeAction(new LineAction(this.model,script,rows, LineAction.Type.DELETE));
		});

		if (insertOption.getActionListeners().length != 0){
			insertOption.removeActionListener(insertOption.getActionListeners()[0]);
		}
		insertOption.addActionListener(e -> {
			TAS.getInstance().executeAction(new LineAction(this.model,script,rows, LineAction.Type.INSERT));
		});

		if (cloneOption.getActionListeners().length != 0){
			cloneOption.removeActionListener(cloneOption.getActionListeners()[0]);
		}
		cloneOption.addActionListener(e -> {
			TAS.getInstance().executeAction(new LineAction(this.model,script,rows, LineAction.Type.CLONE));
		});

		popupMenu.show(this,(int)point.getX(),(int)point.getY());
	}

	/**
	 * prepares the popup menu for the line actions to be called at a later point
	 */
	private void preparepopUpMenu (){

		deleteOption.setActionCommand("delete");
		insertOption.setActionCommand("insert");
		cloneOption.setActionCommand("clone");


		popupMenu.add(deleteOption);
		popupMenu.add(insertOption);
		popupMenu.add(cloneOption);

		popupMenu.setVisible(true);
		popupMenu.setSize(100,100);
		popupMenu.setVisible(false);
	}

	/**
	 * returns the current script
	 * @return current script
	 */
	public Script getScript (){
		return script;
	}

	/**
	 * returns the table model
	 * @return table model
	 */
	public DefaultTableModel getModel (){
		return model;
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
