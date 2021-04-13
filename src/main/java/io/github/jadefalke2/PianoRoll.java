package io.github.jadefalke2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class PianoRoll extends JTable {

	private boolean stickWindowIsOpen;
	private JPopupMenu popupMenu = new JPopupMenu();


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
		"DP-R",
		"DP-L",
		"DP-U",
		"DP-D"
	};

	DefaultTableModel model = new DefaultTableModel();

	public PianoRoll (Script script){
		preparepopUpMenu();
		setModel(model);
		setDragEnabled(false);

		setSize(500, 700);
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

		getColumnModel().getColumn(0).setPreferredWidth(200);
		getColumnModel().getColumn(1).setPreferredWidth(500);
		getColumnModel().getColumn(2).setPreferredWidth(500);

		for (int i = 3; i < 11; i++) {
			getColumnModel().getColumn(i).setPreferredWidth(60);
		}

		for (int i = 11; i < 15; i++) {
			getColumnModel().getColumn(i).setPreferredWidth(200);
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

			stickWindowIsOpen = true;
			stickWindow.setResizable(false);
			stickWindow.setVisible(true);
			stickWindow.setSize(300, 500);
			stickWindow.setLocation(new Point(200, 200));

			stickWindow.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					stickWindowIsOpen = false;
					e.getWindow().dispose();
				}
			});


			StickImagePanel stickImagePanel;

			if (col == 1) {
				stickImagePanel = new StickImagePanel(script.inputLines.get(row).getStickL(), StickImagePanel.StickType.L_STICK,script.getInputLines().get(row));
			} else {
				stickImagePanel = new StickImagePanel(script.inputLines.get(row).getStickR(), StickImagePanel.StickType.R_STICK,script.getInputLines().get(row));
			}

			stickWindow.add(stickImagePanel);

		}

	}

	private void openPopUpMenu(int row, Point point){
		popupMenu.show(this,(int)point.getX(),(int)point.getY());
	}

	private void preparepopUpMenu (){

		JMenuItem delete = new JMenuItem("delete");
		delete.setActionCommand("delete");
		delete.addActionListener(e -> {
			System.out.println("delete");
		});

		JMenuItem insert = new JMenuItem("insert");
		insert.setActionCommand("insert");
		insert.addActionListener(e -> {
			System.out.println("insert");
		});

		JMenuItem clone = new JMenuItem("clone");
		clone.setActionCommand("clone");
		clone.addActionListener(e -> {
			System.out.println("clone");
		});

		popupMenu.add(delete);
		popupMenu.add(insert);
		popupMenu.add(clone);

		popupMenu.setVisible(true);
		popupMenu.setSize(100,100);
		popupMenu.setVisible(false);
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
