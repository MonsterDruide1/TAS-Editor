package io.github.jadefalke2;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Input {





    private final String ZR      = "KEY_ZR";
    private final String ZL      = "KEY_ZL";

    private final String R       = "KEY_R";
    private final String L       = "KEY_L";

    private final String PLUS    = "KEY_PLUS";
    private final String MINUS   = "KEY_MINUS";

    private final String D_LEFT  = "KEY_DLEFT";
    private final String D_RIGHT = "KEY_DRIGHT";
    private final String D_UP    = "KEY_DUP";
    private final String D_DOWN  = "KEY_DDOWN";

    private static Map<String, String> encode = new HashMap<>();
    private static Map<String, String> decode = new HashMap<>();

    public Input (){

    }

    public static Map<String, String> getDecodeInputMap(){
        putData();
        decode = invert(encode);
        return decode;
    }

    public static Map<String,String> getEncodeInputMap(){
        putData();
        return encode;
    }

    private static void putData() {
        encode.put("A","KEY_A");
        encode.put("B","KEY_B");

        encode.put("X","KEY_X");
        encode.put("Y","KEY_Y");

        encode.put("ZR","KEY_ZR");
        encode.put("ZL","KEY_ZL");

        encode.put("R","KEY_R");
        encode.put("L","KEY_L");

        encode.put("Plus","KEY_PLUS");
        encode.put("Minus","KEY_MINUS");

        encode.put("DP-L","KEY_DLEFT");
        encode.put("DP-R","KEY_DRIGHT");
        encode.put("DP-U","KEY_DUP");
        encode.put("DP-D","KEY_DDOWN");

        //encode.put("", "NONE");
    }


    public static <V, K> Map<V, K> invert(Map<K, V> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }




}
