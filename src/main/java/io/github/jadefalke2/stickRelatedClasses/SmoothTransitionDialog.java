package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.util.Settings;
import io.github.jadefalke2.util.TriFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.stream.IntStream;

public class SmoothTransitionDialog extends JDialog {

	public enum SmoothTransitionType {
		ANGULAR_CLOSEST("Angular (Closest)", SmoothTransitionDialog::transitionAngularClosest),
		LINEAR("Linear", SmoothTransitionDialog::transitionLinearClosest),
		ANGULAR_CLOCKWISE("Angular (Clockwise)", SmoothTransitionDialog::transitionAngularClockwise),
		ANGULAR_COUNTERCLOCKWISE("Angular (Counter-Clockwise)", SmoothTransitionDialog::transitionAngularCounterClockwise);

		private final String name;
		private final TriFunction<StickPosition, StickPosition, Integer, StickPosition[]> transitionFunction;
		SmoothTransitionType(String name, TriFunction<StickPosition, StickPosition, Integer, StickPosition[]> transitionFunction) {
			this.name = name;
			this.transitionFunction = transitionFunction;
		}

		public String getName() {
			return name;
		}
		public TriFunction<StickPosition, StickPosition, Integer, StickPosition[]> getTransitionFunction() {
			return transitionFunction;
		}

		public static SmoothTransitionType getByName(String name) {
			for(SmoothTransitionType type : values()) {
				if(type.getName().equals(name))
					return type;
			}
			return null;
		}
	}

	private boolean accepted = false;
	private final int frames;
	private final JComboBox<String> dropdownMenu;
	private final JoystickPanel startJoystick,  endJoystick;

	public SmoothTransitionDialog(Settings settings, StickPosition startPos, StickPosition endPos, int frames){
		super();

		this.frames = frames;
		//option
		dropdownMenu = new JComboBox<>();
		for(SmoothTransitionType option : SmoothTransitionType.values())
			dropdownMenu.addItem(option.getName());

		dropdownMenu.setSelectedIndex(settings.getSmoothTransitionType().ordinal());
		dropdownMenu.addActionListener(e -> {
			updatePositions();
		});

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			accepted = true;
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		});

		startJoystick = new JoystickPanel(settings, "Start");
		startJoystick.setStickPosition(startPos);
		endJoystick = new JoystickPanel(settings, "End");
		endJoystick.setStickPosition(endPos);

		CustomChangeListener<StickPosition> joystickPanelListener = e -> {
			updatePositions();
		};
		startJoystick.setOnChangeListener(joystickPanelListener);
		endJoystick.setOnChangeListener(joystickPanelListener);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		GridBagConstraints c = new GridBagConstraints();


		c.weightx = 1;
		c.weighty = 0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;

		panel.add(dropdownMenu, c);

		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.weighty = 1;

		c.gridx = 0;
		c.gridy = 1;
		panel.add(startJoystick, c);

		c.gridx = 1;
		panel.add(endJoystick, c);

		c.gridwidth = 2;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 2;
		panel.add(okButton, c);

		updatePositions();

		add(panel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
		pack();
	}

	private void updatePositions() {
		if(frames > 1){
			StickPosition[] stickPositionsPreview = getSmoothTransitionData();
			startJoystick.setStickPositions(reverse(stickPositionsPreview));
			endJoystick.setStickPositions(stickPositionsPreview);
		}
	}

	private StickPosition[] reverse (StickPosition[] old) {
		StickPosition[] newArray = new StickPosition[old.length];
		IntStream.range(0, old.length).forEach(i -> newArray[i] = old[old.length - i - 1]);
		return newArray;
	}

	public StickPosition[] getSmoothTransitionData () {
		StickPosition firstPos = startJoystick.getStickPosition();
		StickPosition endPos = endJoystick.getStickPosition();

		SmoothTransitionType type = SmoothTransitionType.getByName((String)dropdownMenu.getSelectedItem());
		return type.getTransitionFunction().apply(firstPos, endPos, frames);
	}

	public boolean isAccepted() {
		return accepted;
	}

	public static StickPosition[] transitionAngularClosest(StickPosition firstPos, StickPosition endPos, int frames){
		double diffThetaCounterClockwise = endPos.getTheta() - firstPos.getTheta();
		double diffThetaClockwise = diffThetaCounterClockwise;
		if(diffThetaCounterClockwise < 0) diffThetaCounterClockwise += 2*Math.PI;
		if(diffThetaClockwise > 0) diffThetaClockwise -= 2*Math.PI;

		if(Math.abs(diffThetaClockwise) < Math.abs(diffThetaCounterClockwise))
			return transitionAngularClockwise(firstPos, endPos, frames);
		else
			return transitionAngularCounterClockwise(firstPos, endPos, frames);
	}

	public static StickPosition[] transitionAngularCounterClockwise(StickPosition firstPos, StickPosition endPos, int frames){
		StickPosition[] result = new StickPosition[frames];
		double firstTheta = firstPos.getTheta();
		double firstRadius = firstPos.getRadius();
		double diffTheta = endPos.getTheta() - firstTheta;
		double diffRadius = endPos.getRadius() - firstRadius;

		if(diffTheta < 0) diffTheta += 2*Math.PI;

		return calculatePositions(endPos, frames, result, firstTheta, firstRadius, diffTheta, diffRadius);
	}

	public static StickPosition[] transitionAngularClockwise(StickPosition firstPos, StickPosition endPos, int frames){
		StickPosition[] result = new StickPosition[frames];
		double firstTheta = firstPos.getTheta();
		double firstRadius = firstPos.getRadius();
		double diffTheta = endPos.getTheta() - firstTheta;
		double diffRadius = endPos.getRadius() - firstRadius;

		if(diffTheta > 0) diffTheta -= 2*Math.PI;

		return calculatePositions(endPos, frames, result, firstTheta, firstRadius, diffTheta, diffRadius);
	}

	private static StickPosition[] calculatePositions(StickPosition endPos, int frames, StickPosition[] result, double firstTheta, double firstRadius, double diffTheta, double diffRadius) {
		for(int i=0;i<frames-1;i++){
			result[i] = new StickPosition(firstTheta+((i/((double)frames-1))*diffTheta), firstRadius+((i/((double)frames-1))*diffRadius));
		}
		result[frames-1] = endPos;

		return result;
	}

	public static StickPosition[] transitionLinearClosest(StickPosition firstPos, StickPosition endPos, int frames){
		StickPosition[] result = new StickPosition[frames];
		int firstX = firstPos.getX();
		int firstY = firstPos.getY();
		int diffX = endPos.getX() - firstX;
		int diffY = endPos.getY() - firstY;

		for(int i=0;i<frames-1;i++){
			result[i] = new StickPosition((int)(firstX+((i/((double)frames-1))*diffX)), (int)(firstY+((i/((double)frames-1))*diffY)));
		}
		result[frames-1] = endPos;

		return result;
	}
}
