package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.LineAction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LineRightClickMenu extends JPopupMenu {

	private final JMenuItem deleteOption;
	private final JMenuItem insertOption;
	private final JMenuItem cloneOption;

	private final JMenuItem copyOption;
	private final JMenuItem pasteOption;
	private final JMenuItem cutOption;

	private final TAS parent;
	private final Script script;
	private final DefaultTableModel model;

	public LineRightClickMenu(TAS parent, Script script, DefaultTableModel model){
		this.parent = parent;
		this.script = script;
		this.model = model;

		copyOption = add("copy");
		pasteOption = add("paste");
		cutOption = add("cut");

		addSeparator();

		deleteOption = add("delete");
		insertOption = add("insert");
		cloneOption = add("clone");

		pack();
	}

	/**
	 * opens the popup menu with the line actions
	 * @param rows all rows affected by this method
	 * @param point the point at which the menu "spawns"
	 */
	public void openPopUpMenu(int[] rows, Point point, Component invoker){

		setListener(deleteOption, rows, LineAction.Type.DELETE);
		setListener(insertOption, rows, LineAction.Type.INSERT);
		setListener(cloneOption, rows, LineAction.Type.CLONE);

		setListener(copyOption, rows, LineAction.Type.COPY);
		setListener(pasteOption, rows, LineAction.Type.PASTE);
		setListener(cutOption, rows, LineAction.Type.CUT);
		show(invoker,(int)point.getX(),(int)point.getY());
	}

	public void setListener(JMenuItem item, int[] rows, LineAction.Type type){
		while (item.getActionListeners().length > 0){
			item.removeActionListener(item.getActionListeners()[0]);
		}

		item.addActionListener(e -> parent.executeAction(new LineAction(parent, model, script, rows, type)));
	}
}
