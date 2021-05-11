package io.github.jadefalke2.components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.LineAction;
import io.github.jadefalke2.stickRelatedClasses.StickImagePanel;
import io.github.jadefalke2.stickRelatedClasses.StickImagePanel.StickType;
import io.github.jadefalke2.util.Button;
import io.github.jadefalke2.util.InputDrawMouseListener;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Arrays;

import static io.github.jadefalke2.stickRelatedClasses.StickImagePanel.StickType.L_STICK;
import static io.github.jadefalke2.stickRelatedClasses.StickImagePanel.StickType.R_STICK;

public class PianoRoll extends JTable implements ComponentListener {

	//script
	private Script script;

	//Components
	private final JPopupMenu popupMenu = new JPopupMenu();

	private final JMenuItem deleteOption = new JMenuItem("delete");
	private final JMenuItem insertOption = new JMenuItem("insert");
	private final JMenuItem cloneOption = new JMenuItem("clone");

	// table model
	private final DefaultTableModel model = new DefaultTableModel();
	private final TAS parent;

	private final JFrame stickWindow;


	public PianoRoll (Script script, TAS parent){

		this.script = script;
		this.parent = parent;

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );

		setAutoResizeMode(AUTO_RESIZE_OFF);
		setModel(model);
		setDragEnabled(false);
		setRowHeight(20);
		setShowGrid(true);
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

		adjustColumnWidth();
		addComponentListener(this);

		//Center all columns
		for (int i = 0; i < getColumnCount(); i++){
			getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		// Mouse listener
		InputDrawMouseListener mouseListener = new InputDrawMouseListener(this, parent);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		setScript(script);

		preparePopUpMenu();

		stickWindow = new JFrame();
		stickWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		stickWindow.setLocation(new Point(200, 200));

		//FIXME Table blocks special key-shortcuts like CTRL+C!
	}

	public void adjustColumnWidth(){
		// adjust the size of all columns

		int[] columnsWidth = {
			45,											   		                // frame number
			85, 85,										  	                	// sticks
			18, 18, 18, 18, 25, 25, 18, 18, 18, 18, 30, 30, 30, 30, 50, 50	    // buttons
		};
		float sum = 629;


		for (int i = 0; i < columnsWidth.length && i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setPreferredWidth((int)(getWidth()*(columnsWidth[i]/sum)));
		}
	}


	/**
	 * adds an empty line to the end of the current script
	 */
	public void addEmptyRow() {
		InputLine newEmpty = InputLine.getEmpty(script.getInputLines().size()); //TODO doesn't make sense on a script with missing frames
		script.getInputLines().add(newEmpty);
		model.addRow(newEmpty.getArray());
	}

	/**
	 * Opens the Window to control the stick input
	 * @param row the row the window corresponds to
	 * @param col the column the window corresponds to (1 -> LSTICK; 2 -> RSTICK)
	 * @param script the script
	 */
	public void openStickWindow (int row,int col, Script script){
		if (!stickWindow.isVisible()) {

			stickWindow.getContentPane().removeAll();

			// Creates StickImagePanel
			StickType stickType = col == 1 ? L_STICK : R_STICK;
			InputLine tmpCurrentInputLine = script.getInputLines().get(row);
			//TODO way too many parameters. rework this.
			StickImagePanel stickImagePanel = new StickImagePanel(stickType == L_STICK ? tmpCurrentInputLine.getStickL() : tmpCurrentInputLine.getStickR(), stickType, script, model, row, parent);

			// adds the panel to the frame
			stickWindow.add(stickImagePanel);
			stickWindow.setVisible(true);
			stickWindow.pack();
		}

	}

	/**
	 * opens the popup menu with the line actions
	 * @param rows all rows affected by this method
	 * @param point the point at which the menu "spawns"
	 */
	public void openPopUpMenu(int[] rows, Point point){

		setListener(deleteOption, rows, LineAction.Type.DELETE);
		setListener(insertOption, rows, LineAction.Type.INSERT);
		setListener(cloneOption, rows, LineAction.Type.CLONE);

		popupMenu.show(this,(int)point.getX(),(int)point.getY());
	}

	public void setListener(JMenuItem item, int[] rows, LineAction.Type type){
		while (item.getActionListeners().length > 0){
			item.removeActionListener(item.getActionListeners()[0]);
		}

		item.addActionListener(e -> parent.executeAction(new LineAction(this.model, script, rows, type)));
	}

	/**
	 * prepares the popup menu for the line actions to be called at a later point
	 */
	private void preparePopUpMenu (){

		popupMenu.add(deleteOption);
		popupMenu.add(insertOption);
		popupMenu.add(cloneOption);

		popupMenu.pack();
	}

	public void deleteSelectedRows(){
		parent.executeAction(new LineAction(this.model, script, getSelectedRows(), LineAction.Type.DELETE));
	}

	public void replaceSelectedRows(InputLine[] rows){
		parent.executeAction(new LineAction(model, script, getSelectedRows(), rows, LineAction.Type.REPLACE));
	}

	public InputLine[] getSelectedInputRows(){
		return Arrays.stream(getSelectedRows()).mapToObj(i -> script.getInputLines().get(i)).toArray(InputLine[]::new);
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
	 * sets the script to a new script
	 * @param script the new script
	 */
	public void setScript (Script script){
		this.script = script;

		model.setRowCount(0);

		for (int i = 0; i < script.getInputLines().size(); i++){
			model.addRow(script.getInputLines().get(i).getArray());
		}
	}

	//always fill the available parent container space
	//idea stolen from https://stackoverflow.com/questions/6104916/how-to-make-jtable-both-autoresize-and-horizontall-scrollable/6104955
	//but always making it true as if it were bigger than the preferred size
	@Override
	public boolean getScrollableTracksViewportWidth(){
		return true;
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

	@Override
	public void componentResized(ComponentEvent e) {
		adjustColumnWidth();
	}

	//required to change L&F of PopupMenu as well
	@Override
	public JPopupMenu getComponentPopupMenu() {
		return popupMenu;
	}

	//unused methods from ComponentListener
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentHidden(ComponentEvent e) {}

}
