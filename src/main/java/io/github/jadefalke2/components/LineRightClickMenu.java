package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.actions.LineAction;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class LineRightClickMenu extends JPopupMenu {

	private final JMenuItem deleteOption;
	private final JMenuItem insertOption;
	private final JMenuItem cloneOption;

	private final JMenuItem copyOption;
	private final JMenuItem pasteOption;
	private final JMenuItem cutOption;

	private final Script script;
	private final ScriptTab scriptTab;

	public LineRightClickMenu(Script script, ScriptTab scriptTab){
		this.script = script;
		this.scriptTab = scriptTab;

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

		setListener(copyOption, () -> scriptTab.getPianoRoll().copy());
		setListener(pasteOption, () -> {
			try {
				scriptTab.getPianoRoll().paste();
			} catch (IOException | UnsupportedFlavorException e) {
				e.printStackTrace(); //TODO error handling
			}
		});
		setListener(cutOption, () -> scriptTab.getPianoRoll().cut());
		show(invoker,(int)point.getX(),(int)point.getY());
	}

	public void setListener(JMenuItem item, Runnable action){
		while (item.getActionListeners().length > 0){
			item.removeActionListener(item.getActionListeners()[0]);
		}

		item.addActionListener(e -> action.run());
	}

	public void setListener(JMenuItem item, int[] rows, LineAction.Type type){
		setListener(item, () -> scriptTab.executeAction(new LineAction(script, rows, type)));
	}
}
