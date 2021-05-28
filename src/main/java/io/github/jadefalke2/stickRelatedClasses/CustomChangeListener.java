package io.github.jadefalke2.stickRelatedClasses;

import java.util.EventListener;

//custom version of event.ChangeListener to use ChangeObject<StickPosition>
public interface CustomChangeListener extends EventListener {
	void stateChanged(ChangeObject<StickPosition> e);
}
