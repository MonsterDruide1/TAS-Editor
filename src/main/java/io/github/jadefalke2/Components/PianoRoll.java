package io.github.jadefalke2.Components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.LineAction;
import io.github.jadefalke2.stickRelatedClasses.StickImagePanel;
import io.github.jadefalke2.util.InputDrawMouseListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class PianoRoll extends JTable {

	private boolean stickWindowIsOpen;
	private final Script script;

	private final JPopupMenu popupMenu = new JPopupMenu();

	private final JMenuItem deleteOption = new JMenuItem("delete");
	private final JMenuItem insertOption = new JMenuItem("insert");
	private final JMenuItem cloneOption = new JMenuItem("clone");

	private final DefaultTableModel model = new DefaultTableModel();


	private final String[] columnNames = {
		"frame", "L-stick", "R-Stick", "A", "B", "X", "Y", "ZR", "ZL", "R", "L", "+", "-", "DR", "DL", "DU", "DD", "L-stick", "R-Stick",
	};

	public PianoRoll (Script script){

		setAutoResizeMode(AUTO_RESIZE_OFF);

		setPreferredSize(new Dimension(700,800));
		setPreferredScrollableViewportSize(getPreferredSize());
		setFillsViewportHeight(false);

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

		int[] columnsWidth = {
			45, //frame number
			85,85, // sticks
			18,18,18,18,25,25,18,18,18,18,30,30,30,30,50,50 //buttons
		};

		for (int i = 0; i < columnsWidth.length && i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
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
				stickImagePanel = new StickImagePanel(script.getInputLines().get(row).getStickL(), StickImagePanel.StickType.L_STICK,script,model, row);
			} else {
				stickImagePanel = new StickImagePanel(script.getInputLines().get(row).getStickR(), StickImagePanel.StickType.R_STICK,script,model, row);
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
