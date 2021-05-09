package io.github.jadefalke2.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {

	public boolean darkTheme = false;
	public int lastStickPositionCount = 3;


	private final Preferences backingPrefs;

	public Settings(Preferences prefs) throws BackingStoreException {
		this.backingPrefs = prefs;
		for(String key : prefs.keys()){
			Field matchingField = Arrays.stream(getClass().getFields())
				.filter(field -> field.getName().equals(key))
				.findFirst().orElse(null);

			try {
				setField(matchingField, key, prefs);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NullPointerException e){
				System.err.println("No such field in Settings: "+key);
			}
		}
	}

	private void setField(Field field, String key, Preferences prefs) throws IllegalAccessException {
		String stringValue = prefs.get(key, field.get(this).toString());
		Object value = switch (field.getType().getTypeName()){
			case "boolean" -> stringValue.equals("true");
			case "int" -> Integer.parseInt(stringValue);
			default -> throw new UnsupportedOperationException("Unexpected settings field type: " + field.getType().getTypeName());
		};
		field.set(this, value);
	}

	public void storeSettings() throws BackingStoreException {
		backingPrefs.clear();
		Arrays.stream(getClass().getFields())
			.forEach(field -> {
				try {
					backingPrefs.put(field.getName(), field.get(this).toString());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
	}

}
