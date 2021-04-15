package io.github.jadefalke2.util;

import java.util.EmptyStackException;

public class CircularStack<E> implements Stack<E> {

	private Object[] array;
	private int index;

	public CircularStack(int size) {
		array = new Object[size];
	}

	@Override
	public void push(E item) {
		array[index] = item;
		index = (index + 1) % array.length;
	}

	@Override
	public E peek() {
		int tempIndex = Math.floorMod(index - 1, array.length);
		if (array[tempIndex] == null)
			throw new EmptyStackException();
		return (E) array[tempIndex];
	}

	@Override
	public E pop() {
		index = Math.floorMod(index - 1, array.length);
		if (array[index] == null)
			throw new EmptyStackException();
		E item = (E) array[index];
		array[index] = null;
		return item;
	}

	@Override
	public boolean isEmpty() {
		return array[Math.floorMod(index - 1, array.length)] == null;
	}

	@Override
	public void clear() {
		array = new Object[array.length];
	}
}
