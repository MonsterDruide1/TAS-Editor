package io.github.jadefalke2.stickRelatedClasses;

import javax.swing.event.ChangeEvent;

public class ChangeObject<E> extends ChangeEvent {

	private final E oldValue, newValue;

	public ChangeObject(E oldValue, E newValue, Object source){
		super(source);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public E getOldValue(){
		return oldValue;
	}
	public E getNewValue(){
		return newValue;
	}

}
