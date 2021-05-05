package io.github.jadefalke2;

import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.Button;
import io.github.jadefalke2.util.CorruptedScriptException;

import java.util.EnumSet;

public class InputLine {

	private int frame;
	public EnumSet<Button> buttons = EnumSet.noneOf(Button.class);
	private StickPosition stickL, stickR;

	public InputLine(int frame) {
		this.frame = frame;
		stickL = new StickPosition(0,0);
		stickR = new StickPosition(0,0);
	}
	public InputLine(String full) throws CorruptedScriptException {
		splitIntoComponents(full);
	}

	@Override
	public InputLine clone(){
		InputLine newLine = new InputLine(frame);
		newLine.buttons = buttons.clone();
		newLine.stickL = stickL.clone();
		newLine.stickR = stickR.clone();
		return newLine;
	}


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
					throw new CorruptedScriptException("Unknown button encountered: "+s);
				}
			}
		}

		stickL = new StickPosition(components[2]);
		stickR = new StickPosition(components[3]);
	}


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

	public boolean isEmpty (){
		return buttons.isEmpty() && stickR.isZeroZero() && stickL.isZeroZero();
	}

	public static InputLine getEmpty (int frame){
		return new InputLine(frame);
	}

}
