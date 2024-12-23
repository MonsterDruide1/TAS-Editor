package io.github.jadefalke2.components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.actions.InsertEmptyLineAction;
import io.github.jadefalke2.actions.LineAction;
import io.github.jadefalke2.script.NXTas;
import io.github.jadefalke2.util.Button;
import io.github.jadefalke2.util.CorruptedScriptException;
import io.github.jadefalke2.util.InputDrawMouseListener;
import io.github.jadefalke2.util.ScriptTableModel;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;

public class PianoRoll extends JTable {

	//script
	private Script script;
	private final ScriptTab scriptTab;

	// table model
	private ScriptTableModel model;


	public PianoRoll (Script script, ScriptTab scriptTab) {
		this.script = script;
		this.scriptTab = scriptTab;
		this.model = new ScriptTableModel(script);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );

		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
		setModel(model);
		setDragEnabled(false);
		setRowHeight(20);
		setShowGrid(true);
		setFillsViewportHeight(true);
		setFont(new Font("Arial", Font.PLAIN, 15));

		getTableHeader().setResizingAllowed(true);
		getTableHeader().setReorderingAllowed(true);
		getTableHeader().setDefaultRenderer(centerRenderer);

		getTableHeader().addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					new ColumnRightClickMenu(PianoRoll.this).openPopUpMenu(e.getPoint(), PianoRoll.this);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});


		//Center all columns
		for (int i = 0; i < getColumnCount(); i++){
			getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		// Mouse listener
		InputDrawMouseListener mouseListener = new InputDrawMouseListener(this, scriptTab);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		setScript(script);

		// disable custom copy/paste bindings, just making them pass through to the window (menu) handler
		setTransferHandler(null);

		adjustColumnWidth();
	}

	@Override
	public void updateUI() {
		super.updateUI();
		setShowGrid(true);
	}

	/**
	 * opens the popup menu with the line actions
	 * @param rows all rows affected by this method
	 * @param point the point at which the menu "spawns"
	 */
	public void openPopUpMenu(int[] rows, Point point){
		new LineRightClickMenu(script, scriptTab).openPopUpMenu(rows, point,this);
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

	public void addEmptyRows(int amount) {
		if(amount == 0) return;

		int selectedIndex = getSelectedRowCount() != 0 ? getSelectedRows()[getSelectedRowCount()-1]+1 : getRowCount();
		executeAction(new InsertEmptyLineAction(script, selectedIndex, amount));
	}

	public void deleteSelectedRows(){
		executeAction(new LineAction(script, getSelectedRows(), LineAction.Type.DELETE));
	}

	public void replaceSelectedRows(InputLine[] rows){
		executeAction(new LineAction(script, getSelectedRows(), rows, LineAction.Type.REPLACE));
	}

	public void insertAfterSelectedRows(InputLine[] rows){
		executeAction(new LineAction(script, getSelectedRows(), rows, LineAction.Type.INSERT));
	}

	public void executeAction(Action action) {
		scriptTab.executeAction(action);
	}

	public InputLine[] getSelectedInputRows(){
		return script.getLines(getSelectedRows());
	}

	/**
	 * returns the table model
	 * @return table model
	 */
	public AbstractTableModel getModel (){
		return model;
	}

	/**
	 * sets the script to a new script
	 * @param script the new script
	 */
	public void setScript (Script script){
		this.script = script;
		model = new ScriptTableModel(script);
	}

	public Script getScript() {
		return script;
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

	public void copy() {
		InputLine[] rows = getSelectedInputRows();
		int[] rowsIndex = getSelectedRows();

		String[] rowStrings = new String[rows.length];
		for(int i=0;i<rows.length;i++){
			rowStrings[i] = rows[i].getFull(rowsIndex[i]);
		}
		String fullString = String.join("\n", rowStrings);

		StringSelection selection = new StringSelection(fullString);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}

	public void paste() throws IOException, UnsupportedFlavorException {
		InputLine[] rows = readClipboard();
		insertAfterSelectedRows(rows);
	}

	public void replace() throws IOException, UnsupportedFlavorException {
		InputLine[] rows = readClipboard();
		replaceSelectedRows(rows);
	}

	public InputLine[] readClipboard() throws IOException, UnsupportedFlavorException {
		String clipContent = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor);
		return Arrays.stream(clipContent.split("[\r\n]+")).map(line -> {
			try {
				return NXTas.readLine(line);
			} catch(CorruptedScriptException e){
				System.out.println("invalid clipboard content: " + line); //TODO proper error handling here
				return null;
			}
		}).toArray(InputLine[]::new);
	}

	public void cut(){
		copy();
		deleteSelectedRows();
	}

    public void setSelectedRows(int[] selectedLines) {
		clearSelection();
		for (int selectedLine : selectedLines) {
			addRowSelectionInterval(selectedLine, selectedLine);
		}

		if(selectedLines.length != 0)
			scrollRectToVisible(getCellRect(selectedLines[0], 0, false));
    }
}
