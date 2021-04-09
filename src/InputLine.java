import java.util.ArrayList;

public class InputLine {

    String full;

    int line;
    String buttons;
    ArrayList<String> buttonsEncoded = new ArrayList<>();
    String stickL,stickR;

    public InputLine (String full){
        this.full = full;
        splitIntoComponents();
        System.out.println(buttonsEncoded);
    }

    private void splitIntoComponents () {

        String[] components = full.split(" ");

        line = Integer.parseInt(components[0]);
        buttons = components[1];
        String[] buttonsPressed = buttons.split(";");

        for (String s: buttonsPressed){
            buttonsEncoded.add(Input.getDecodeInputMap().get(s));
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
}
