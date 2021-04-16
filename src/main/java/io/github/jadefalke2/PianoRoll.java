package io.github.jadefalke2;

import io.github.jadefalke2.actions.CellAction;
import io.github.jadefalke2.actions.LineAction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

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



		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_A) {

					script.getInputLines().add(new InputLine((script.getInputLines().size() + 1) + " NONE 0;0 0;0"));

					InputLine currentLine = script.getInputLines().get(script.getInputLines().size() - 1);

					Object[] tmp = new Object[columnNames.length];
					tmp[0] = script.getInputLines().size();
					addRow(currentLine, tmp, columnNames, model);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});

		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				int row = rowAtPoint(evt.getPoint());
				int col = columnAtPoint(evt.getPoint());

				switch (col){

					case 0:

						if (evt.getButton() == MouseEvent.BUTTON3 && isRowSelected(row)) {
							openPopUpMenu(row,evt.getPoint());
						}

						break;

					case 1: case 2:
						openStickWindow(row,col,script);
						break;

					default:
						TAS.getInstance().executeAction(new CellAction(model, script, row, col));
				}

			}
		});

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

	private void openStickWindow (int row,int col, Script script){
		JFrame stickWindow;
		if (!stickWindowIsOpen) {
			stickWindow = new JFrame();

			StickImagePanel stickImagePanel;

			if (col == 1) {
				stickImagePanel = new StickImagePanel(script.inputLines.get(row).getStickL(), StickImagePanel.StickType.L_STICK,script.getInputLines().get(row));
			} else {
				stickImagePanel = new StickImagePanel(script.inputLines.get(row).getStickR(), StickImagePanel.StickType.R_STICK,script.getInputLines().get(row));
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

	private void openPopUpMenu(int row, Point point){

		if (delete.getActionListeners().length != 0){
			delete.removeActionListener(delete.getActionListeners()[0]);
		}
		delete.addActionListener(e -> {
			TAS.getInstance().executeAction(new LineAction(this.model,script,row, LineAction.Type.DELETE));
		});

		if (insert.getActionListeners().length != 0){
			insert.removeActionListener(insert.getActionListeners()[0]);
		}
		insert.addActionListener(e -> {
			TAS.getInstance().executeAction(new LineAction(this.model,script,row, LineAction.Type.INSERT));
			update();
		});

		if (clone.getActionListeners().length != 0){
			clone.removeActionListener(clone.getActionListeners()[0]);
		}
		clone.addActionListener(e -> {
			TAS.getInstance().executeAction(new LineAction(this.model,script,row, LineAction.Type.CLONE));
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

	private void update (){

	}


	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
