package io.github.jadefalke2.util;

import java.io.File;

public interface ScriptObserver {

	default void onFileChange(File file) {}
	default void onLengthChange(int length) {}
	default void onDirtyChange(boolean dirty) {}

}
