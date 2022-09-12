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
		putClientProperty("JTabbedPane.hideTabAreaWithOneTab", true);
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
		ScriptTab scriptTab = new ScriptTab(parent, script);
		scriptTabs.add(scriptTab);
		addTab(script.getName(), scriptTab);
		setSelectedIndex(scriptTabs.size()-1);
	}

	// TODO lots of issues with no script active (null return)
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
			scriptTabs.remove(index);
			removeTabAt(index);
			return true;
		}
		return false;
	}

}
