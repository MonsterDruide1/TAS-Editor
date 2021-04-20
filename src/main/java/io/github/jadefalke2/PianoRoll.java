package io.github.jadefalke2;

import io.github.jadefalke2.actions.CellAction;
import io.github.jadefalke2.actions.LineAction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PianoRoll extends JTable {

	private boolean stickWindowIsOpen;
	private Script script;

	private JPopupMenu popupMenu = new JPopupMenu();
	JMenuItem delete = new JMenuItem("delete");
	JMenuItem insert = new JMenuItem("insert");
	JMenuItem clone = new JMenuItem("clone");


	String[] columnNames = {
		"frame",
		"L-stick",
		"R-Stick",
		"A",
		"B",
		"X",
		"Y",
		"ZR",
		"ZL",
		"R",
		"L",
		"+",
		"-",
		"DP-R",
		"DP-L",
		"DP-U",
		"DP-D",
		"L-stick",
		"R-Stick",
	};

	DefaultTableModel model = new DefaultTableModel();

	public PianoRoll (Script script){

		setAutoResizeMode(AUTO_RESIZE_OFF);

		this.script = script;

		preparepopUpMenu();
		setModel(model);

		setDragEnabled(false);
		getTableHeader().setResizingAllowed(false);
		getTableHeader().setReorderingAllowed(false);

		for (String colName : columnNames) {
			model.addColumn(colName);
		}

		InputDrawMouseListener mouseListener = new InputDrawMouseListener(this);

		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		for (int i = 0; i < script.getInputLines().size(); i++) {
			InputLine currentLine = script.getInputLines().get(i);

			Object[] tmp = new Object[columnNames.length];
			tmp[0] = i + 1;
			addRow(currentLine, tmp, columnNames, model);
		}

		getColumnModel().getColumn(0).setPreferredWidth(45);
		getColumnModel().getColumn(1).setPreferredWidth(90);
		getColumnModel().getColumn(2).setPreferredWidth(90);

		for (int i = 3; i < 13; i++) {
			getColumnModel().getColumn(i).setPreferredWidth(30);
		}

		for (int i = 13; i < columnNames.length - 2; i++) {
			getColumnModel().getColumn(i).setPreferredWidth(40);
		}

		for (int i = columnNames.length - 2; i < columnNames.length; i++){
			getColumnModel().getColumn(i).setPreferredWidth(60);
		}

	}


	public void addEmptyRow(Script script) {
		script.getInputLines().add(new InputLine((script.getInputLines().size() + 1) + " NONE 0;0 0;0"));

		InputLine currentLine = script.getInputLines().get(script.getInputLines().size() - 1);

		Object[] tmp = new Object[columnNames.length];
		tmp[0] = script.getInputLines().size();
		addRow(currentLine, tmp, columnNames, model);
	}

	private void addRow(InputLine currentLine, Object[] tmp, String[] columnNames, DefaultTableModel model) {
		tmp[1] = currentLine.getStickL();
		tmp[2] = currentLine.getStickR();

		for (int j = 3; j < tmp.length; j++) {
			if (currentLine.getButtonsEncoded().contains(columnNames[j])) {
				tmp[j] = columnNames[j];
			} else {
				tmp[j] = " ";
			}
		}

		model.addRow(tmp);
	}

	public void openStickWindow (int row,int col, Script script){
		JFrame stickWindow;
		if (!stickWindowIsOpen) {
			stickWindow = new JFrame();

			StickImagePanel stickImagePanel;

			if (col == 1) {
				stickImagePanel = new StickImagePanel(script.inputLines.get(row).getStickL(), StickImagePanel.StickType.L_STICK,script,model, row);
			} else {
				stickImagePanel = new StickImagePanel(script.inputLines.get(row).getStickR(), StickImagePanel.StickType.R_STICK,script,model, row);
			}

			stickWindowIsOpen = true;
			stickWindow.setResizable(false);
			stickWindow.setVisible(true);
			stickWindow.setSize(300, 500);
			stickWindow.setLocation(new Point(200, 200));

			stickWindow.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					stickWindowIsOpen = false;
					setValueAt(stickImagePanel.getStickPos().toString(),row,col);
					e.getWindow().dispose();
				}
			});

			stickWindow.add(stickImagePanel);

		}

	}

	void openPopUpMenu(int[] rows, Point point){

		if (delete.getActionListeners().length != 0){
			delete.removeActionListener(delete.getActionListeners()[0]);
		}
		delete.addActionListener(e -> {
			TAS.getInstance().executeAction(new LineAction(this.model,script,rows, LineAction.Type.DELETE));
		});

		if (insert.getActionListeners().length != 0){
			insert.removeActionListener(insert.getActionListeners()[0]);
		}
		insert.addActionListener(e -> {
			TAS.getInstance().executeAction(new LineAction(this.model,script,rows, LineAction.Type.INSERT));
		});

		if (clone.getActionListeners().length != 0){
			clone.removeActionListener(clone.getActionListeners()[0]);
		}
		clone.addActionListener(e -> {
			TAS.getInstance().executeAction(new LineAction(this.model,script,rows, LineAction.Type.CLONE));
		});

		popupMenu.show(this,(int)point.getX(),(int)point.getY());
	}

	private void preparepopUpMenu (){

		delete.setActionCommand("delete");
		insert.setActionCommand("insert");
		clone.setActionCommand("clone");


		popupMenu.add(delete);
		popupMenu.add(insert);
		popupMenu.add(clone);

		popupMenu.setVisible(true);
		popupMenu.setSize(100,100);
		popupMenu.setVisible(false);
	}

	public Script getScript (){
		return script;
	}

	public DefaultTableModel getModel (){
		return model;
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
