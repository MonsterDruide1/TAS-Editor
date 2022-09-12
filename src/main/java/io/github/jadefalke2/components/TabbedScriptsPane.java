package io.github.jadefalke2.components;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TabbedScriptsPane extends JTabbedPane {

	private final TAS parent;
	private final List<ScriptTab> scriptTabs;

	public TabbedScriptsPane(TAS parent) {
		this.parent = parent;
		scriptTabs = new ArrayList<>();
		addChangeListener((e) -> afterTabChange());
	}

	public void refreshLayouts() {
		for(ScriptTab scriptTab : scriptTabs) {
			scriptTab.refreshLayout();
		}
	}

	public void afterTabChange() {
		getActiveScriptTab().updateUndoRedoEnabled();
	}

	public void openScript(Script script) {
		ScriptTab scriptTab = new ScriptTab(parent, script);
		scriptTabs.add(scriptTab);
		addTab(script.getName(), scriptTab);
		setSelectedIndex(scriptTabs.size()-1);
	}

	public ScriptTab getActiveScriptTab() {
		return scriptTabs.get(getSelectedIndex());
	}

	public boolean closeAllScripts() {
		for(ScriptTab tab : scriptTabs) {
			if(!tab.closeScript()) {
				return false;
			}
		}
		return true;
	}

}
