package io.github.jadefalke2.util;

import java.util.ArrayList;
import java.util.List;

public class ObservableProperty<T> {

	@FunctionalInterface
	public interface PropertyChangeListener<T> {
		default void onChange(T newValue, T oldValue) { onChange(newValue); }
		void onChange(T newValue);
	}

	private T value;
	private final List<PropertyChangeListener<T>> listeners;

	public ObservableProperty(T value) {
		this.value = value;
		listeners = new ArrayList<>();
	}

	public void set(T value) {
		if(value.equals(this.value)) return;

		for(PropertyChangeListener<T> listener : listeners) {
			listener.onChange(value, this.value);
		}
		this.value = value;
	}
	public T get() {
		return value;
	}

	public void attachListener(PropertyChangeListener<T> listener) {
		listeners.add(listener);
	}
	public void detachListener(PropertyChangeListener<T> listener) {
		listeners.remove(listener);
	}

}
