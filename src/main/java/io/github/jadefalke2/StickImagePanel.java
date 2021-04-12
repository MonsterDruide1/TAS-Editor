package io.github.jadefalke2;

import javax.swing.*;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StickImagePanel extends JPanel {

    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner radiusSpinner;
    private final JSpinner angleSpinner;

    private final Joystick joystick;

    private StickPosition stickPosition;
    private StickType stickType;
    private InputLine inputLine;


    private final int STICK_IMAGE_SIZE = 200;


    enum StickType {
        L_STICK,R_STICK
    }

	public StickImagePanel(StickPosition stickPosition, StickType stickType,InputLine inputLine) {

        this.inputLine = inputLine;
        this.stickType = stickType;
		this.stickPosition = stickPosition;
		joystick = new Joystick(32767,STICK_IMAGE_SIZE);


        JPanel joyStickPanel = new JPanel();
        JPanel spinnerPanel = new JPanel();


        GridLayout mainLayout = new GridLayout(2,1,0,20);
        GridLayout spinnerLayout = new GridLayout(5,2,80,7);


        spinnerPanel.setLayout(spinnerLayout);
        spinnerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setLayout(mainLayout);
        add(spinnerPanel);
        add(joyStickPanel);

        joyStickPanel.add(joystick);



		SpinnerModel xModel = new SpinnerNumberModel(0, -32767, 32767, 100);
        SpinnerModel yModel = new SpinnerNumberModel(0, -32767, 32767, 100);
        SpinnerModel radiusModel = new SpinnerNumberModel(0, 0, 1, 0.1);
        SpinnerModel angleModel = new SpinnerNumberModel(0, 0, 360, 1);


        xSpinner = new JSpinner(xModel);
        ySpinner = new JSpinner(yModel);
        radiusSpinner = new JSpinner(radiusModel);
        angleSpinner = new JSpinner(angleModel);

        radiusSpinner.setPreferredSize(new Dimension(50,20));
        radiusSpinner.setAlignmentX(10);


        xSpinner.addChangeListener(e -> {
            stickPosition.setX((int) xSpinner.getValue());
            repaint();
        });
        ySpinner.addChangeListener(e -> {
            stickPosition.setY((int) ySpinner.getValue());
            repaint();
        });
        radiusSpinner.addChangeListener(e -> {
            stickPosition.setRadius((double) radiusSpinner.getValue());
            repaint();
        });
        angleSpinner.addChangeListener(e -> {
            stickPosition.setTheta(Math.toRadians((int) angleModel.getValue()));
            repaint();
        });

        MouseListener mouseListener = new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e){
                updateStickPosition();
            }

            @Override
            public void mouseReleased(MouseEvent e){
                updateStickPosition();
            }

        };

        joystick.addMouseListener(mouseListener);




        JLabel cartesianLabel = new JLabel("Cartesian");
        JLabel polarLabel = new JLabel("Polar");

        JLabel xLabel = new JLabel("x:");
        JLabel yLabel = new JLabel("y:");

        JLabel radiusLabel = new JLabel("radius:");
        JLabel thetaLabel = new JLabel("angle:");

        spinnerPanel.add(cartesianLabel);
        spinnerPanel.add(polarLabel);

        spinnerPanel.add(xLabel);
        spinnerPanel.add(thetaLabel);
        spinnerPanel.add(xSpinner);
        spinnerPanel.add(angleSpinner);


        spinnerPanel.add(yLabel);
        spinnerPanel.add(radiusLabel);
        spinnerPanel.add(ySpinner);
        spinnerPanel.add(radiusSpinner);

    }





	private void updateStickPosition() {

    	StickPosition oldStickPosition = new StickPosition(stickPosition);

        int x = (int)joystick.getOutputPos().getX();
        int y = (int)joystick.getOutputPos().getY();

        xSpinner.setValue(x);
        ySpinner.setValue(y);
        radiusSpinner.setValue(stickPosition.getRadius());
        angleSpinner.setValue((int) Math.toDegrees(stickPosition.getTheta()));

        stickPosition.setPosition(x,y);

        TAS.getInstance().executeAction(new StickAction(inputLine, stickType, oldStickPosition, stickPosition));

		repaint();
	}
}
