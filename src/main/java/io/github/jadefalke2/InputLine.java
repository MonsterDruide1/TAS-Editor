package io.github.jadefalke2;

import java.util.ArrayList;

public class InputLine {

    private String full;

    int line;
    String buttons;
    ArrayList<String> buttonsEncoded = new ArrayList<>();
    StickPosition stickL,stickR;

    public InputLine (String full){
        this.full = full;
        splitIntoComponents();
    }

    private void splitIntoComponents () {

        String[] components = full.split(" ");

        line = Integer.parseInt(components[0]);
        buttons = components[1];
        String[] buttonsPressed = buttons.split(";");

        for (String s: buttonsPressed){
            if (Input.getEncodeInputMap().containsValue(s)) {
                buttonsEncoded.add(Input.getDecodeInputMap().get(s));
            }
        }

        stickL = new StickPosition(Integer.parseInt(components[2].split(";")[0]),Integer.parseInt(components[2].split(";")[1]));
        stickR = new StickPosition(Integer.parseInt(components[3].split(";")[0]),Integer.parseInt(components[3].split(";")[1]));

    }


    public int getLine() {
        return line;
    }

    public ArrayList<String> getButtonsEncoded() {
        return buttonsEncoded;
    }

    public StickPosition getStickL() {
        return stickL;
    }

    public StickPosition getStickR() {
        return stickR;
    }

    private void updateFull (){
        StringBuilder tmpString = new StringBuilder();

        tmpString.append(line + " ");

        boolean first = true;

        if (buttonsEncoded.isEmpty()){
            tmpString.append("NONE");
        }else {
            for (String button : buttonsEncoded) {

                if (!first) {
                    tmpString.append(";");
                } else {
                    first = false;
                }

                tmpString.append(Input.getEncodeInputMap().get(button));
            }
        }

        tmpString.append(" " + getStickL().toString() + " " + getStickR().toString());

        full = tmpString.toString();
    }

    public String getFull (){
        updateFull();
        return full;
    }
}