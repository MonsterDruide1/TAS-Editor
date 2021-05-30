package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.LineAction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LineRightClickMenu extends JPopupMenu {

	private final JMenuItem deleteOption = new JMenuItem("delete");
	private final JMenuItem insertOption = new JMenuItem("insert");
	private final JMenuItem cloneOption = new JMenuItem("clone");

	private final JMenuItem copyOption = new JMenuItem("copy");
	private final JMenuItem pasteOption = new JMenuItem("paste");
	private final JMenuItem cutOption = new JMenuItem("cut");

	private final TAS parent;
	private final Script script;
	private final DefaultTableModel model;

	public LineRightClickMenu(TAS parent, Script script, DefaultTableModel model){
		this.parent = parent;
		this.script = script;
		this.model = model;
	}

	/**
	 * opens the popup menu with the line actions
	 * @param rows all rows affected by this method
	 * @param point the point at which the menu "spawns"
	 */
	public void openPopUpMenu(int[] rows, Point point, Component invoker){
		add(deleteOption);
		add(insertOption);
		add(cloneOption);

		setListener(deleteOption, rows, LineAction.Type.DELETE);
		setListener(insertOption, rows, LineAction.Type.INSERT);
		setListener(cloneOption, rows, LineAction.Type.CLONE);

		add(copyOption);
		add(pasteOption);
		add(cutOption);

		setListener(copyOption, rows, LineAction.Type.COPY);
		setListener(pasteOption, rows, LineAction.Type.PASTE);
		setListener(cutOption, rows, LineAction.Type.CUT);

		pack();
		show(invoker,(int)point.getX(),(int)point.getY());
	}

	public void setListener(JMenuItem item, int[] rows, LineAction.Type type){
		while (item.getActionListeners().length > 0){
			item.removeActionListener(item.getActionListeners()[0]);
		}

		item.addActionListener(e -> parent.executeAction(new LineAction(parent,model, script, rows, type)));
	}
}
