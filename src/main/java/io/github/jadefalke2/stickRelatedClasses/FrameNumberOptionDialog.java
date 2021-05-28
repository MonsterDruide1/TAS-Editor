package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class FrameNumberOptionDialog {

	public static StickPosition[] getSmoothTransitionData (Settings settings, int frames) {
		JDialog dialog = new JDialog();

		//option
		JComboBox<String> dropdownMenu = new JComboBox<>();
		dropdownMenu.addItem("Angular (Closest)");
		dropdownMenu.addItem("Linear");
		dropdownMenu.addItem("Angular (Clockwise)");
		dropdownMenu.addItem("Angular (Counter-Clockwise)");

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
		});

		JoystickPanel startJoystick = new JoystickPanel(settings);
		JoystickPanel endJoystick = new JoystickPanel(settings);

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

		c.gridx = 0;
		c.gridy = 2;
		panel.add(okButton, c);


		dialog.add(panel);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setSize(500,500);
		dialog.setVisible(true);



		StickPosition firstPos = startJoystick.getStickPosition();
		StickPosition endPos = endJoystick.getStickPosition();

		StickPosition[] result = switch(dropdownMenu.getSelectedIndex()){
			case 0 -> transitionAngularClosest(firstPos, endPos, frames);
			case 1 -> transitionLinearClosest(firstPos, endPos, frames);
			case 2 -> transitionAngularClockwise(firstPos, endPos, frames);
			case 3 -> transitionAngularCounterClockwise(firstPos, endPos, frames);
			default -> throw new UnsupportedOperationException("Selected unknown item in interpolation type dropdown: "+dropdownMenu.getSelectedItem()+" ("+dropdownMenu.getSelectedIndex()+")");
		};

		System.out.println(Arrays.toString(result));

		return result;
	}

	public static StickPosition[] transitionAngularClosest(StickPosition firstPos, StickPosition endPos, int frames){
		if(endPos.getTheta()-firstPos.getTheta() > 0)
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

		for(int i=0;i<frames-1;i++){
			result[i] = new StickPosition(firstTheta+((i/((double)frames-1))*diffTheta), firstRadius+((i/((double)frames-1))*diffRadius));
		}
		result[frames-1] = endPos;

		return result;
	}

	public static StickPosition[] transitionAngularClockwise(StickPosition firstPos, StickPosition endPos, int frames){
		StickPosition[] result = new StickPosition[frames];
		double firstTheta = firstPos.getTheta();
		double firstRadius = firstPos.getRadius();
		double diffTheta = endPos.getTheta() - firstTheta;
		double diffRadius = endPos.getRadius() - firstRadius;

		if(diffTheta > 0) diffTheta -= 2*Math.PI;

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
