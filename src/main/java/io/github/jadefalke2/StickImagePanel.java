package io.github.jadefalke2;

import io.github.jadefalke2.actions.StickAction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class StickImagePanel extends JPanel {

    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner radiusSpinner;
    private final JSpinner angleSpinner;

    private final Joystick joystick;

    private StickPosition stickPosition;
    private StickType stickType;
    private InputLine inputLine;

    private DefaultTableModel table;
    private int row;


    private final int STICK_IMAGE_SIZE = 200;


    public enum StickType {
        L_STICK,R_STICK
    }



	public StickImagePanel(StickPosition stickPosition, StickType stickType, InputLine inputLine, DefaultTableModel table, int row) {
		this.row = row;
    	this.table = table;
        this.inputLine = inputLine;
        this.stickType = stickType;
		this.stickPosition = stickPosition;
		joystick = new Joystick(32767,STICK_IMAGE_SIZE);


		joystick.setThumbPos(new Point(stickPosition.getX(),stickPosition.getY()));

        JPanel joyStickPanel = new JPanel();
        JPanel spinnerPanel = new JPanel();
        JPanel centerButtonPanel = new JPanel();


        GridLayout mainLayout = new GridLayout(2,1,0,20);
        GridLayout spinnerLayout = new GridLayout(5,2,80,7);


        spinnerPanel.setLayout(spinnerLayout);
        spinnerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setLayout(mainLayout);
        add(spinnerPanel);
        add(joyStickPanel);
        //add(centerButtonPanel);

        joyStickPanel.add(joystick);



		SpinnerModel xModel = new SpinnerNumberModel(0, -32767, 32767, 100);
        SpinnerModel yModel = new SpinnerNumberModel(0, -32767, 32767, 100);
        SpinnerModel radiusModel = new SpinnerNumberModel(0, 0, 1, 0.1);
        SpinnerModel angleModel = new SpinnerNumberModel(0, 0, 360, 1);


        xSpinner = new JSpinner(xModel);
        ySpinner = new JSpinner(yModel);
        radiusSpinner = new JSpinner(radiusModel);
        angleSpinner = new JSpinner(angleModel);

        xSpinner.setValue(stickPosition.getX());
		ySpinner.setValue(stickPosition.getY());
		radiusSpinner.setValue(stickPosition.getRadius());
		angleSpinner.setValue((int)Math.toDegrees(stickPosition.getTheta()));

        radiusSpinner.setPreferredSize(new Dimension(50,20));
        radiusSpinner.setAlignmentX(10);


        xSpinner.addChangeListener(e -> {
            stickPosition.setX((int) xSpinner.getValue());
           	//updatePolarSpinners();
            updateVisual();
            repaint();
        });
        ySpinner.addChangeListener(e -> {
            stickPosition.setY((int) ySpinner.getValue());
            //updatePolarSpinners();
            updateVisual();
            repaint();
        });
        radiusSpinner.addChangeListener(e -> {
            stickPosition.setRadius((double) radiusSpinner.getValue());
            updateCartSpinners();
            updateVisual();
            repaint();
        });
        angleSpinner.addChangeListener(e -> {
            stickPosition.setTheta((int)angleSpinner.getValue());
            updateCartSpinners();
            updateVisual();
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

		JButton centerButton = new JButton("center");
		centerButton.addActionListener(e -> joystick.centerThumbPad());
		//centerButtonPanel.add(centerButton);

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

        TAS.getInstance().executeAction(new StickAction(inputLine, stickType, oldStickPosition, stickPosition, table, row));

		repaint();
	}

	private void updateVisual (){
		joystick.setThumbPos(new Point((int)xSpinner.getValue(),(int)ySpinner.getValue()));
		repaint();
	}

	private void updateCartSpinners(){
    	xSpinner.setValue(stickPosition.getX());
    	ySpinner.setValue(stickPosition.getY());
	}

	private void updatePolarSpinners(){
		angleSpinner.setValue((int)Math.toDegrees(stickPosition.getTheta()));
		radiusSpinner.setValue(stickPosition.getRadius());
	}

	public StickPosition getStickPos (){
    	return stickPosition;
	}
}
