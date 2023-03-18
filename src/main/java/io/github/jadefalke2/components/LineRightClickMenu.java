package io.github.jadefalke2.components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.actions.Action;
import io.github.jadefalke2.actions.InsertEmptyLineAction;
import io.github.jadefalke2.actions.LineAction;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class LineRightClickMenu extends JPopupMenu {

	private final JMenuItem deleteOption;
	private final JMenuItem insertOption;
	private final JMenuItem cloneOption;

	private final JMenuItem cutOption;
	private final JMenuItem copyOption;
	private final JMenuItem pasteOption;
	private final JMenuItem replaceOption;

	private final Script script;
	private final ScriptTab scriptTab;

	public LineRightClickMenu(Script script, ScriptTab scriptTab){
		this.script = script;
		this.scriptTab = scriptTab;

		cutOption = add("cut");
		copyOption = add("copy");
		pasteOption = add("paste");
		replaceOption = add("replace");

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
		setListener(insertOption, new InsertEmptyLineAction(script, rows[rows.length-1]+1, rows.length));
		// insert dummy InputLines - unused, but the size is required for undo
		setListener(cloneOption, new LineAction(script, rows, new InputLine[rows.length], LineAction.Type.CLONE));

		setListener(cutOption, () -> scriptTab.getPianoRoll().cut());
		setListener(copyOption, () -> scriptTab.getPianoRoll().copy());
		setListener(pasteOption, () -> {
			try {
				scriptTab.getPianoRoll().paste();
			} catch (IOException | UnsupportedFlavorException e) {
				e.printStackTrace(); //TODO error handling
			}
		});
		setListener(replaceOption, () -> {
			try {
				scriptTab.getPianoRoll().replace();
			} catch (IOException | UnsupportedFlavorException e) {
				e.printStackTrace(); //TODO error handling
			}
		});
		show(invoker,(int)point.getX(),(int)point.getY());
	}

	public void setListener(JMenuItem item, Runnable action){
		while (item.getActionListeners().length > 0){
			item.removeActionListener(item.getActionListeners()[0]);
		}

		item.addActionListener(e -> action.run());
	}

	public void setListener(JMenuItem item, int[] rows, LineAction.Type type){
		setListener(item, new LineAction(script, rows, type));
	}
	public void setListener(JMenuItem item, Action action){
		setListener(item, () -> scriptTab.executeAction(action));
	}
}
