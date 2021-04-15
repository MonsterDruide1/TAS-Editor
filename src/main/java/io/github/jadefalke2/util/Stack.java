package io.github.jadefalke2.util;

public interface Stack<E> {

	void push(E item);

	E peek();

	E pop();

	boolean isEmpty();

	void clear();
}
