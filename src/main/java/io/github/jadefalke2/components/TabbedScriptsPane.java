package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.ObservableProperty;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class TabbedScriptsPane extends JTabbedPane {

	private final MainEditorWindow mainEditorWindow;
	private final List<ScriptTab> scriptTabs;
	private final ObservableProperty.PropertyChangeListener<Integer> lengthChangeListener;

	public TabbedScriptsPane(MainEditorWindow mainEditorWindow, ObservableProperty.PropertyChangeListener<Integer> listener) {
		this.mainEditorWindow = mainEditorWindow;
		this.lengthChangeListener = listener;
		scriptTabs = new ArrayList<>();

		setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
		addChangeListener((e) -> afterTabChange());
		putClientProperty("JTabbedPane.tabClosable", true);
		putClientProperty("JTabbedPane.tabCloseCallback", (IntConsumer) tabIndex -> {
			AWTEvent e = EventQueue.getCurrentEvent();
			boolean alt = e instanceof MouseEvent && ((MouseEvent) e).isAltDown();
			if(alt)
				closeAllExcept(tabIndex);
			else
				closeTab(tabIndex);
		});
		JComponent label = new JLabel();
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					openScript(Script.getEmptyScript(10));
				}
			}
		});
		putClientProperty("JTabbedPane.trailingComponent", label);
	}

	public void refreshLayouts() {
		for(ScriptTab scriptTab : scriptTabs) {
			scriptTab.refreshLayout();
		}
	}

	public void afterTabChange() {
		ScriptTab activeTab = getActiveScriptTab();
		if(activeTab != null)
			activeTab.updateUndoRedoEnabled();
		else
			mainEditorWindow.enableUndoRedo(false, false);
	}

	public void openScript(Script script) {
		int index = scriptTabs.size();
		ScriptTab scriptTab = new ScriptTab(mainEditorWindow, script, lengthChangeListener);
		scriptTabs.add(scriptTab);
		addTab(script.getName(), scriptTab);
		setSelectedIndex(index);
		scriptTab.setDirtyListener(dirty -> setTitleAt(index, (dirty ? "*" : "")+script.getName()));
		mainEditorWindow.setAllTabsClosed(false);
	}

	public ScriptTab getActiveScriptTab() {
		int selectedIndex = getSelectedIndex();
		if(selectedIndex == -1 || scriptTabs.size() <= selectedIndex) return null;
		return scriptTabs.get(selectedIndex);
	}

	public boolean closeAllScripts() {
		while(getTabCount() > 0) {
			if(!closeTab(0))
				return false;
		}
		return true;
	}

	public void closeAllExcept(int index) {
		int indexToClose = 0;
		while(getTabCount() > 1) {
			if(index == 0){
				indexToClose++;
				index = -1;
				continue;
			}

			if(!closeTab(indexToClose))
				return;
			index--;
		}
	}

	public boolean closeTab(int index) {
		setSelectedIndex(index);
		ScriptTab tab = scriptTabs.get(index);
		if(tab.closeScript()) {
			tab.cleanup();
			scriptTabs.remove(index);
			removeTabAt(index);
			if(scriptTabs.size() == 0)
				mainEditorWindow.setAllTabsClosed(true);
			return true;
		}
		return false;
	}

}
