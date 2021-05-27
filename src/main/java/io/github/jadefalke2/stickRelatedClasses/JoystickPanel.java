package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.StickAction;
import io.github.jadefalke2.components.PianoRoll;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.awt.event.*;
import java.util.Arrays;

public class JoystickPanel extends JPanel {

	//buttons

	JButton centerButton;
	JButton smoothTransitionButton;
	JButton keepStickPosButton;

	// Spinners
    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner radiusSpinner;
    private final JSpinner angleSpinner;

    // Joystick
    private final Joystick joystick;

    // Other stuff
    private StickPosition stickPosition;
    private final Script script;
    private StickType stickType;
	private InputLine[] inputLines;
	private StickPosition[] stickPositions;
	private int row;

    private final DefaultTableModel table;
    private final TAS parent; //TODO avoid this

	private boolean shouldTriggerUpdate = true;

	// Used to have a good way to differentiate sticks

	public enum StickType {
        L_STICK,R_STICK
    }


	public JoystickPanel(TAS parent, PianoRoll pianoRoll, Script script, StickType stickType) {

		// setting global vars
		stickPositions = new StickPosition[Math.min(row, parent.getPreferences().getLastStickPositionCount())];
		stickPosition = new StickPosition(0,0);
		joystick = new Joystick(32767, stickPositions);

		this.script = script;
		this.table = pianoRoll.getModel();
		this.parent = parent;
		this.stickType = stickType;



		// spinners
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




		// sets the contents of the stickpositions array to be the previous stick positions of the same stick
		for (int i = 0; i < stickPositions.length; i++){
			InputLine currentLine = script.getInputLines().get(row - stickPositions.length + i);
			stickPositions[i] = stickType == StickType.L_STICK ? currentLine.getStickL() : currentLine.getStickR();
		}

        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

		joystick.setThumbPos(new Point(stickPosition.getX(),stickPosition.getY()));

		ChangeListener spinnerListener = e -> {
			if(shouldTriggerUpdate){
				shouldTriggerUpdate = false;
				StickPosition oldPos = stickPosition;

				if(e.getSource().equals(xSpinner))
					stickPosition = new StickPosition((int) xSpinner.getValue(), stickPosition.getY());
				else if(e.getSource().equals(ySpinner))
					stickPosition = new StickPosition(stickPosition.getX(), (int) ySpinner.getValue());
				else if(e.getSource().equals(radiusSpinner))
					stickPosition = new StickPosition(stickPosition.getTheta(), (double)radiusSpinner.getValue());
				else if(e.getSource().equals(angleSpinner))
					stickPosition = new StickPosition((int)angleSpinner.getValue(), stickPosition.getRadius());
				else
					throw new IllegalArgumentException("Common ChangeListener called on unknown Spinner: "+e.getSource());

				applyPosition(stickPosition, oldPos);
				updateCartSpinners();
				updatePolarSpinners();
				updateVisual();
				repaint();
				shouldTriggerUpdate = true;
			}
		};

		xSpinner.addChangeListener(spinnerListener);
		ySpinner.addChangeListener(spinnerListener);
		radiusSpinner.addChangeListener(spinnerListener);
		angleSpinner.addChangeListener(spinnerListener);

        MouseAdapter mouseListener = new MouseAdapter() {
        	private StickPosition oldStickPos = null;

        	@Override
			public void mousePressed(MouseEvent e){
        		oldStickPos = stickPosition;
				updateStickPosition(false, oldStickPos);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				updateStickPosition(false, oldStickPos);
			}

            @Override
            public void mouseReleased(MouseEvent e){
                updateStickPosition(true, oldStickPos);
                oldStickPos = null;
            }

        };

		KeyListener keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// FIXME
				//NOT WORKING AS OF NOW, as focus always is on one of the spinners...
				//
				switch (e.getKeyCode()) {
					case KeyEvent.VK_0 -> joystick.setThumbPos(new Point(32767, 0));
					case KeyEvent.VK_KP_LEFT -> joystick.setThumbPos(new Point(-32767, 0));
					case KeyEvent.VK_KP_UP -> joystick.setThumbPos(new Point(0, 32767));
					case KeyEvent.VK_KP_DOWN -> joystick.setThumbPos(new Point(0, -32767));
				}
				repaint();
			}
		};

		joystick.addMouseListener(mouseListener);
		joystick.addMouseMotionListener(mouseListener);
        joystick.addKeyListener(keyListener);


        JLabel cartesianLabel = new JLabel("Cartesian");
        JLabel polarLabel = new JLabel("Polar");

        JLabel xLabel = new JLabel("x:");
        JLabel yLabel = new JLabel("y:");

        JLabel radiusLabel = new JLabel("radius:");
        JLabel thetaLabel = new JLabel("angle:");



		c.insets = new Insets(5,3,5,3);

        c.gridwidth = 1;
		c.weightx = 5;
		c.weighty = 5;

        c.gridy = 1;
        c.gridx = 1;
        add(cartesianLabel, c);

        c.gridx = 3;
        add(polarLabel, c);

        c.gridy = 2;
        c.gridx = 0;
        add(xLabel, c);

        c.gridx = 1;
		add(xSpinner, c);

		c.gridy = 3;
		c.gridx = 0;
		add(yLabel, c);

		c.gridx = 1;
		add(ySpinner, c);

		c.gridx = 2;
		c.gridy = 2;
		add(radiusLabel, c);

		c.gridx = 3;
		add(radiusSpinner, c);

		c.gridx = 2;
		c.gridy = 3;
        add(thetaLabel, c);

        c.gridx = 3;
        add(angleSpinner, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 4;
        c.gridheight = 4;
        c.weighty = 0;
        c.ipadx = 250;
        c.ipady = 250;
        c.gridx = 0;
        c.gridy = 4;

		add(joystick, c);

		centerButton = new JButton("center");
		centerButton.addActionListener(e -> {
			joystick.centerThumbPad();
			updateStickPosition(true, stickPosition);
		});

		keepStickPosButton = new JButton("keep stick position for # of frames");
		keepStickPosButton.addActionListener(e -> {
			int frameNumber = FrameNumberOptionDialog.getFrameNumber();
			for (int i = row; i < row + frameNumber; i++){

				if (i >= script.getInputLines().size()){
					script.getInputLines().add(InputLine.getEmpty(i + 1));
					table.addRow(script.getInputLines().get(i).getArray());
				}

				if (stickType == StickType.L_STICK) {
					script.getInputLines().get(i).setStickL(script.getInputLines().get(row).getStickL().clone());
					table.setValueAt(script.getInputLines().get(i).getStickL().toCartString(), i,1);

				} else{
					script.getInputLines().get(i).setStickR(script.getInputLines().get(row).getStickR().clone());
					table.setValueAt(script.getInputLines().get(i).getStickR().toCartString(), i,2);
				}

			}
		});

		smoothTransitionButton = new JButton("smooth transition");
		smoothTransitionButton.addActionListener(e -> {
			//int frameNumber = FrameNumberOptionDialog.getSmoothTransitionData();
			FrameNumberOptionDialog.getSmoothTransitionData();
		});


		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.weighty = 10;
		c.ipadx = 0;
		c.ipady = 0;

		c.gridx = 1;
		c.gridy = 10;
		add(smoothTransitionButton, c);

		c.gridy = 11;
		add(centerButton, c);

		c.gridy = 12;
		add(keepStickPosButton, c);

		setGreyedOut();
    }


    private void applyPosition(StickPosition newPos, StickPosition oldPos){
		if(inputLines == null) return;

		parent.executeAction(new StickAction(inputLines, stickType, oldPos, newPos, table));
	}

	/**
	 * Updates the stick position based on the sliders
	 */
	private void updateStickPosition(boolean executeAction, StickPosition oldStickPosition) {
		if(inputLines == null) return;

        int x = (int)joystick.getThumbPos().getX();
        int y = (int)joystick.getThumbPos().getY();

		StickPosition unCropped = new StickPosition(x, y);
		double radius = Math.min(unCropped.getRadius(), 1);
		stickPosition = new StickPosition(unCropped.getTheta(), radius);

        shouldTriggerUpdate = false;
        xSpinner.setValue(stickPosition.getX());
        ySpinner.setValue(stickPosition.getY());
        radiusSpinner.setValue(stickPosition.getRadius());
        angleSpinner.setValue((int) Math.toDegrees(stickPosition.getTheta()));
        updateVisual();
        shouldTriggerUpdate = true;

        if(executeAction)
        	parent.executeAction(new StickAction(inputLines, stickType, oldStickPosition, stickPosition, table));

		repaint();
	}

	public void setGreyedOut () {
		//TODO color of stick
		joystick.lock();
		setSpinnersAndButtonsEnabled(false);
	}

	public void setEditingRows (int[] rows) {
		shouldTriggerUpdate = false;
		InputLine[] tmp = new InputLine[rows.length];
		Arrays.setAll(tmp, i -> script.getInputLines().get(rows[i]));
		inputLines = tmp;
		joystick.unlock();
		stickPosition = stickType == StickType.L_STICK ? tmp[0].getStickL() : tmp[0].getStickR();
		updateVisual();
		updateCartSpinners();
		updatePolarSpinners();
		setSpinnersAndButtonsEnabled(true);
		shouldTriggerUpdate = true;
	}

	private void setSpinnersAndButtonsEnabled (boolean enable) {
		angleSpinner.setEnabled(enable);
		radiusSpinner.setEnabled(enable);

		xSpinner.setEnabled(enable);
		ySpinner.setEnabled(enable);

		centerButton.setEnabled(enable);
		keepStickPosButton.setEnabled(enable);
		smoothTransitionButton.setEnabled(enable);
	}

	/**
	 * Updates the visual stickPosition -> is called on changes
	 */
	private void updateVisual (){
		joystick.setThumbPos(new Point((int)stickPosition.getX(),(int)stickPosition.getY()));
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

	public StickPosition getStickPosition() {
		return stickPosition;
	}

	public void setStickPosition(StickPosition stickPosition) {
		this.stickPosition = stickPosition;
	}

	public StickType getStickType() {
		return stickType;
	}

	public void setStickType(StickType stickType) {
		this.stickType = stickType;
	}

	public InputLine[] getInputLines() {
		return inputLines;
	}

	public void setInputLines(InputLine[] inputLines) {
		this.inputLines = inputLines;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
}
