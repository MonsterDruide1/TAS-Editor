package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.StickAction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.awt.event.*;

public class StickImagePanel extends JPanel {

	// Spinners
    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner radiusSpinner;
    private final JSpinner angleSpinner;

    // Joystick
    private final Joystick joystick;

    // Other stuff
    private final StickPosition stickPosition;
    private final StickType stickType;
	private final InputLine inputLine;

    private final DefaultTableModel table;
    private final int row;
    private final TAS parent; //TODO avoid this


    // Used to have a good way to differentiate sticks

	public enum StickType {
        L_STICK,R_STICK
    }


	/**
	 * Constructor
	 * @param stickPosition the stick position
	 * @param stickType the type of stick
	 * @param script the script
	 * @param table the main table
	 * @param row the current row
	 */
	public StickImagePanel(StickPosition stickPosition, StickType stickType,Script script, DefaultTableModel table, int row, TAS parent) {

		// setting global vars
		this.row = row;
    	this.table = table;
        this.inputLine = script.getInputLines().get(row);
        this.stickType = stickType;
		this.stickPosition = stickPosition;
		this.parent = parent;

		StickPosition[] stickPositions = new StickPosition[Math.min(row,3)];

		// sets the contents of the stickpositions array to be the previous stick positions of the same stick
		for (int i = 0; i < stickPositions.length; i++){
			InputLine currentLine = script.getInputLines().get(row - i);

			stickPositions[i] = stickType == StickType.L_STICK ? currentLine.getStickL() : currentLine.getStickR();
		}

		final int STICK_IMAGE_SIZE = 200;
		joystick = new Joystick(32767, STICK_IMAGE_SIZE,stickPositions );


		joystick.setThumbPos(new Point(stickPosition.getX(),stickPosition.getY()));

        JPanel joyStickPanel = new JPanel();
        JPanel spinnerPanel = new JPanel();

        GridLayout mainLayout = new GridLayout(2,1,0,20);
        GridLayout spinnerLayout = new GridLayout(6,2,30,7);


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

        xSpinner.setValue(stickPosition.getX());
		ySpinner.setValue(stickPosition.getY());
		radiusSpinner.setValue(stickPosition.getRadius());
		angleSpinner.setValue((int)Math.toDegrees(stickPosition.getTheta()));

        radiusSpinner.setPreferredSize(new Dimension(50,20));
        radiusSpinner.setAlignmentX(10);


        xSpinner.addChangeListener(e -> {
            stickPosition.setX((int) xSpinner.getValue());
           	//updatePolarSpinners();
			//uncommenting above leads to recursive method calls and a stackOverFlow error! -> fix
            updateVisual();
            repaint();
        });
        ySpinner.addChangeListener(e -> {
            stickPosition.setY((int) ySpinner.getValue());
            //updatePolarSpinners();
			//uncommenting above leads to recursive method calls and a stackOverFlow error! -> fix
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

		KeyListener keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				//
				//NOT WORKING AS OF NOW
				//

				switch (e.getKeyCode()){

					case KeyEvent.VK_0:
						joystick.setThumbPos(new Point(32767,0));
						break;

					case KeyEvent.VK_KP_LEFT:
						joystick.setThumbPos(new Point(-32767,0));
						break;

					case KeyEvent.VK_KP_UP:
						joystick.setThumbPos(new Point(0,32767));
						break;

					case KeyEvent.VK_KP_DOWN:
						joystick.setThumbPos(new Point(0,-32767));
						break;

				}

				repaint();
			}
		};

		joystick.addMouseListener(mouseListener);
        joystick.addKeyListener(keyListener);




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
		centerButton.addActionListener(e -> {
			joystick.centerThumbPad();
			stickPosition.setPosition((int)joystick.getOutputPos().getX(),(int)joystick.getOutputPos().getY());
			updateCartSpinners();
			updatePolarSpinners();
		});

		spinnerPanel.add(centerButton);


		JButton keepStickPosButton = new JButton("keep stick position for # of frames");
		keepStickPosButton.addActionListener(e -> {
			int frameNumber = FrameNumberOptionDialog.getFrameNumber();
			for (int i = row - 1; i < row + frameNumber; i++){

				if (i >= script.getInputLines().size()){
					script.getInputLines().add(InputLine.getEmpty(i + 1));
					table.addRow(script.getInputLines().get(i).getArray());
				}

				if (stickType == StickType.L_STICK) {
					script.getInputLines().get(i).setStickL(new StickPosition(script.getInputLines().get(row).getStickL()));
					table.setValueAt(script.getInputLines().get(i - 1).getStickL().toCartString(), i,1);

				}else{
					script.getInputLines().get(i).setStickR(new StickPosition(script.getInputLines().get(row).getStickR()));
					table.setValueAt(script.getInputLines().get(i - 1).getStickR().toCartString(), i,2);
				}

			}
		});

		spinnerPanel.add(keepStickPosButton);


    }


	/**
	 * Updates the stick position based on the sliders
	 */
	private void updateStickPosition() {

    	StickPosition oldStickPosition = new StickPosition(stickPosition);

        int x = (int)joystick.getOutputPos().getX();
        int y = (int)joystick.getOutputPos().getY();

        xSpinner.setValue(x);
        ySpinner.setValue(y);
        radiusSpinner.setValue(stickPosition.getRadius());
        angleSpinner.setValue((int) Math.toDegrees(stickPosition.getTheta()));


        stickPosition.setPosition(x,y);

        parent.executeAction(new StickAction(inputLine, stickType, oldStickPosition, stickPosition, table, row));

		repaint();
	}

	/**
	 * Updates the visual stickPosition -> is called on changes
	 */
	private void updateVisual (){
		joystick.setThumbPos(new Point((int)xSpinner.getValue(),(int)ySpinner.getValue()));
		repaint();
	}

	/**
	 * Updates the cartesian spinners
	 */
	private void updateCartSpinners(){
    	xSpinner.setValue(stickPosition.getX());
    	ySpinner.setValue(stickPosition.getY());
	}

	/**
	 * Updates the polar spinners
	 */
	private void updatePolarSpinners(){
		angleSpinner.setValue((int)Math.toDegrees(stickPosition.getTheta()));
		radiusSpinner.setValue(stickPosition.getRadius());
	}

	// getter

	public StickPosition getStickPos (){
    	return stickPosition;
	}
}
