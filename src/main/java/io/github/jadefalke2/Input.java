package io.github.jadefalke2;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Input {


	private static Map<String, String> encode = new HashMap<>();
	private static Map<String, String> decode = new HashMap<>();

	public Input() {

	}

	public static Map<String, String> getDecodeInputMap() {
		putData();
		decode = invert(encode);
		return decode;
	}

	public static Map<String, String> getEncodeInputMap() {
		putData();
		return encode;
	}

	private static void putData() {
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

		encode.put("DP-L", "KEY_DLEFT");
		encode.put("DP-R", "KEY_DRIGHT");
		encode.put("DP-U", "KEY_DUP");
		encode.put("DP-D", "KEY_DDOWN");

		encode.put("L-stick","KEY_LSTICK");
		encode.put("R-stick", "KEY_RSTICK");

	}


	public static <V, K> Map<V, K> invert(Map<K, V> map) {
		return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
	}


}
