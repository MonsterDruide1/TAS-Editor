package io.github.jadefalke2;

import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.Button;
import io.github.jadefalke2.util.CorruptedScriptException;

import java.util.EnumSet;

public class InputLine {

	// the frame, starting at 0
	private int frame;

	// the buttons currently pressed
	public EnumSet<Button> buttons = EnumSet.noneOf(Button.class);

	// the stick positions
	private StickPosition stickL, stickR;


	// Contructors

	public InputLine(int frame) {
		this.frame = frame;
		stickL = new StickPosition(0,0);
		stickR = new StickPosition(0,0);
	}

	public InputLine(String full) throws CorruptedScriptException {
		splitIntoComponents(full);
	}

	/**
	 * Splits the full string of a line into its components. Namely: Frame number, L-stick, R-stick, buttons
	 * @param full the full string of the line
	 * @throws CorruptedScriptException if an unknown button is entered
	 */
	private void splitIntoComponents(String full) throws CorruptedScriptException {

		if (full.equals("")){
			throw new CorruptedScriptException("empty script");
		}

		String[] components = full.split(" ");

		frame = Integer.parseInt(components[0]);
		String buttons = components[1];
		String[] buttonsPressed = buttons.split(";");

		for (String s : buttonsPressed) {
			//TODO better way to handle this?
			try {
				this.buttons.add(Button.valueOf(s));
			} catch(IllegalArgumentException e){
				if(!s.equals("NONE")){
					throw new CorruptedScriptException("Unknown button encountered: " + s);
				}
			}
		}

		stickL = new StickPosition(components[2]);
		stickR = new StickPosition(components[3]);
	}

	/**
	 * Returns a new Inputline with no buttons pressed and both sticks at 0;0
	 * @param frame the frame number of the input line
	 * @return the new input line
	 */
	public static InputLine getEmpty (int frame){
		return new InputLine(frame);
	}


	// getter + setter (special)

	/**
	 * creates and returns a new string that contains all information about this input line
	 * @return the created string
	 */
	public String getFull() {
		StringBuilder tmpString = new StringBuilder();

		tmpString.append(frame).append(" ");

		if (buttons.isEmpty()) {
			tmpString.append("NONE");
		} else {
			boolean first = true;

			for (Button button : buttons) {

				if (!first) {
					tmpString.append(";");
				} else {
					first = false;
				}

				tmpString.append(button.name());
			}
		}

		tmpString
			.append(" ").append(getStickL().toCartString())
			.append(" ").append(getStickR().toCartString());


		return tmpString.toString();
	}

	/**
	 * creates and returns an array with all data of the lines, used to display it in a JTable
	 * @return the Object array
	 */
	public Object[] getArray (){
		Object[] tmp = new Object[3+Button.values().length];
		tmp[0] = frame;
		tmp[1] = stickL.toCartString();
		tmp[2] = stickR.toCartString();

		for (int i=0;i<Button.values().length;i++) {
			tmp[i + 3] = buttons.contains(Button.values()[i]) ? Button.values()[i].toString() : "";
		}
		return tmp;
	}

	/**
	 * returns wether the input line has no buttons pressed and its stick positions are on 0;0
	 * @return if the line is empty
	 */
	public boolean isEmpty (){
		return buttons.isEmpty() && stickR.isZeroZero() && stickL.isZeroZero();
	}

	/**
	 * Increases the frame number by a specified amount
	 * @param amount the amount to increase by
	 */
	public void increaseFrameBy (int amount){
		frame += amount;
	}


	// Getter + setter (normal)

	public int getFrame() {
		return frame;
	}

	public StickPosition getStickL() {
		return stickL;
	}

	public StickPosition getStickR() {
		return stickR;
	}

	public void setStickL(StickPosition stickL) {
		this.stickL = stickL;
	}

	public void setStickR(StickPosition stickR) {
		this.stickR = stickR;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}


	// Overwriting methods

	/**
	 * returns a new Iputline with excatly the same data as the current one
	 * @return the new Input line
	 */
	@Override
	public InputLine clone(){
		InputLine newLine = new InputLine(frame);
		newLine.buttons = buttons.clone();
		newLine.stickL = stickL.clone();
		newLine.stickR = stickR.clone();
		return newLine;
	}
}
