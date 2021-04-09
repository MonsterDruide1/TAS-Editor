import java.util.ArrayList;

public class InputLine {

    private String full;

    int line;
    String buttons;
    ArrayList<String> buttonsEncoded = new ArrayList<>();
    String stickL,stickR;

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

            if (Input.getEncodeInputMap().containsKey(s)) {
                buttonsEncoded.add(Input.getDecodeInputMap().get(s));
            }
        }

        stickL = components[2];
        stickR = components[3];
    }


    public int getLine() {
        return line;
    }

    public ArrayList<String> getButtonsEncoded() {
        return buttonsEncoded;
    }

    public String getStickL() {
        return stickL;
    }

    public String getStickR() {
        return stickR;
    }

    private void updateFull (){
        StringBuilder tmpString = new StringBuilder();

        tmpString.append(line + " " + getStickL() + " " + getStickR() + " ");

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

        full = tmpString.toString();
    }

    public String getFull (){
        updateFull();
        return full;
    }
}
