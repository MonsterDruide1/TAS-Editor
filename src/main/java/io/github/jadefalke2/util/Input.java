package io.github.jadefalke2.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Input {


	private static final HashMap<String, String> encode;
	private static final HashMap<String, String> decode;
	public static final String EMPTY_LINE = "1 NONE 0;0 0;0\n";

	static {
		encode = new HashMap<>();
		putData(encode);
		decode = invert(encode);
	}

	public static Map<String, String> getDecodeInputMap() {
		return decode;
	}

	public static Map<String, String> getEncodeInputMap() {
		return encode;
	}

	private static void putData(HashMap<String, String> encode) {
		encode.put("A", "KEY_A");
		encode.put("B", "KEY_B");

		encode.put("X", "KEY_X");
		encode.put("Y", "KEY_Y");

		encode.put("ZR", "KEY_ZR");
		encode.put("ZL", "KEY_ZL");

		encode.put("R", "KEY_R");
		encode.put("L", "KEY_L");

		encode.put("+", "KEY_PLUS");
		encode.put("-", "KEY_MINUS");

		encode.put("DL", "KEY_DLEFT");
		encode.put("DR", "KEY_DRIGHT");
		encode.put("DU", "KEY_DUP");
		encode.put("DD", "KEY_DDOWN");

		encode.put("L-stick","KEY_LSTICK");
		encode.put("R-stick", "KEY_RSTICK");
	}


	public static <V, K> HashMap<V, K> invert(HashMap<K, V> map) {
		return new HashMap<>(map.entrySet().stream().collect(Collectors.toMap(HashMap.Entry::getValue, Map.Entry::getKey)));
	}


}
