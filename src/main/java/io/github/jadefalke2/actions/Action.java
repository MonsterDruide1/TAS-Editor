package io.github.jadefalke2.actions;

/**
 * Represents a user action that gets added to the undo history and is able to be reverted
 */
public interface Action {

	void execute();

	void revert();
}
