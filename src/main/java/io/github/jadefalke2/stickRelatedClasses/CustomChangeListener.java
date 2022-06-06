package io.github.jadefalke2.stickRelatedClasses;

import java.util.EventListener;

//custom version of event.ChangeListener to use ChangeObject<StickPosition>
public interface CustomChangeListener<E> extends EventListener {
	void stateChanged(ChangeObject<E> e);
	default void silentStateChanged(ChangeObject<E> e) {}
}
