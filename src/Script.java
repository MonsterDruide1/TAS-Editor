import java.util.ArrayList;
import java.util.Arrays;

public class Script {

    private String script;
    ArrayList<InputLine> inputLines = new ArrayList<>();

    public Script (String script){
        String[] lines = script.split("\n");

        for (String line: lines){
            inputLines.add(new InputLine(line));
        }
    }

    public ArrayList<InputLine> getInputLines (){
        return inputLines;
    }
}
