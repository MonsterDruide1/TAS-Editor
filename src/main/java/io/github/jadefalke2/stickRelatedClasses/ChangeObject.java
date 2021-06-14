package io.github.jadefalke2.stickRelatedClasses;

import javax.swing.event.ChangeEvent;

public class ChangeObject<E> extends ChangeEvent {

	private final E value;

	public ChangeObject(E value, Object source){
		super(source);
		this.value = value;
	}

	public E getValue(){
		return value;
	}

}
