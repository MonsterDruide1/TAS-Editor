package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class TabbedScriptsPane extends JTabbedPane {

	private final TAS parent;
	private final List<ScriptTab> scriptTabs;

	public TabbedScriptsPane(TAS parent) {
		this.parent = parent;
		scriptTabs = new ArrayList<>();

		addChangeListener((e) -> afterTabChange());
		putClientProperty("JTabbedPane.tabClosable", true);
		putClientProperty("JTabbedPane.tabCloseCallback", (IntConsumer) this::closeTab);
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
			parent.getMainEditorWindow().enableUndoRedo(false, false);
	}

	public void openScript(Script script) {
		int index = scriptTabs.size();
		ScriptTab scriptTab = new ScriptTab(parent, script);
		scriptTabs.add(scriptTab);
		addTab(script.getName(), scriptTab);
		setSelectedIndex(index);
		scriptTab.setDirtyListener(dirty -> setTitleAt(index, (dirty ? "*" : "")+script.getName()));
		parent.getMainEditorWindow().setAllTabsClosed(false);
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

	public boolean closeTab(int index) {
		ScriptTab tab = scriptTabs.get(index);
		if(tab.closeScript()) {
			tab.cleanup();
			scriptTabs.remove(index);
			removeTabAt(index);
			if(scriptTabs.size() == 0)
				parent.getMainEditorWindow().setAllTabsClosed(true);
			return true;
		}
		return false;
	}

}
