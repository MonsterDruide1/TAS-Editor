package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import java.awt.*;

import java.awt.event.*;
import java.util.Arrays;

public class JoystickPanel extends JPanel {

	//buttons

	private final JButton centerButton;
	private final JButton smoothTransitionButton;

	// Spinners
    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner radiusSpinner;
    private final JSpinner angleSpinner;

    // Joystick
    private final Joystick joystick;

    // Other stuff
    private StickPosition stickPosition;
    private final StickType stickType;
	private InputLine[] inputLines;

	private final CustomChangeListener onChange;
	private final Settings settings;

	private boolean shouldTriggerUpdate = true;

	// Used to have a good way to differentiate sticks

	public enum StickType {
        L_STICK,R_STICK
    }


	public JoystickPanel(Settings settings, StickType stickType, CustomChangeListener onChange) {

		this.onChange = onChange;
		this.stickType = stickType;
		this.settings = settings;


		// setting global vars
		stickPosition = new StickPosition(0,0);
		joystick = new Joystick(32767, settings);
		joystick.setThumbPos(new Point(stickPosition.getX(),stickPosition.getY()));


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

        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

		ChangeListener spinnerListener = e -> {
			if(shouldTriggerUpdate){
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
				updateAll();
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

        c.gridwidth = 2;
        add(cartesianLabel, c);

        c.gridx = 3;
        add(polarLabel, c);

        c.gridwidth = 1;
        c.gridy = 1;
        c.gridx = 0;
        add(xLabel, c);

        c.gridx = 1;
		add(xSpinner, c);

		c.gridy = 2;
		c.gridx = 0;
		add(yLabel, c);

		c.gridx = 1;
		add(ySpinner, c);

		c.gridx = 2;
		c.gridy = 1;
		add(radiusLabel, c);

		c.gridx = 3;
		add(radiusSpinner, c);

		c.gridx = 2;
		c.gridy = 2;
        add(thetaLabel, c);

        c.gridx = 3;
        add(angleSpinner, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 4;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 3;

		add(joystick, c);
		c.weighty = 0;

		centerButton = new JButton("center");
		centerButton.addActionListener(e -> {
			joystick.centerThumbPad();
			updateStickPosition(true, stickPosition);
		});

		smoothTransitionButton = new JButton("smooth transition");
		smoothTransitionButton.addActionListener(e -> {
			//int frameNumber = FrameNumberOptionDialog.getSmoothTransitionData();
			FrameNumberOptionDialog.getSmoothTransitionData(settings);
		});


		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 0;
		c.ipady = 0;

		c.gridx = 1;
		c.gridy = 4;
		add(smoothTransitionButton, c);

		c.gridy = 5;
		add(centerButton, c);

		setAllEnabled(false);
    }


    private void applyPosition(StickPosition newPos, StickPosition oldPos){
		if(inputLines == null) return;

		onChange.stateChanged(new ChangeObject<>(oldPos, newPos, this));
	}

	/**
	 * Updates the stick position based on the sliders
	 */
	private void updateStickPosition(boolean executeAction, StickPosition oldStickPosition) {
		if(inputLines == null) return;

		StickPosition unCropped = joystick.getStickPosition();
		double radius = Math.min(unCropped.getRadius(), 1);
		stickPosition = new StickPosition(unCropped.getTheta(), radius);

        updateAll();

        if(executeAction)
        	applyPosition(stickPosition, oldStickPosition);
	}

	public void setEditingRows (int[] rows, Script script) {
		shouldTriggerUpdate = false;
		InputLine[] tmp = new InputLine[rows.length];
		Arrays.setAll(tmp, i -> script.getInputLines().get(rows[i]));
		inputLines = tmp;
		stickPosition = stickType == StickType.L_STICK ? tmp[0].getStickL() : tmp[0].getStickR();
		updateAll();
		setAllEnabled(true);

		StickPosition[] stickPositions = new StickPosition[Math.min(rows[0], settings.getLastStickPositionCount())];
		// sets the contents of the stickpositions array to be the previous stick positions of the same stick
		for (int i = 0; i < stickPositions.length; i++){
			InputLine currentLine = script.getInputLines().get(rows[0] - stickPositions.length + i);
			stickPositions[i] = stickType == StickType.L_STICK ? currentLine.getStickL() : currentLine.getStickR();
		}
		joystick.setStickPositions(stickPositions);

		shouldTriggerUpdate = true;
	}

	public void setAllEnabled(boolean enable) {
		angleSpinner.setEnabled(enable);
		radiusSpinner.setEnabled(enable);

		xSpinner.setEnabled(enable);
		ySpinner.setEnabled(enable);

		centerButton.setEnabled(enable);
		smoothTransitionButton.setEnabled(enable);

		joystick.setEnabled(enable);
	}

	private void updateAll(){
		shouldTriggerUpdate = false;
		updateVisual();
		updateCartSpinners();
		updatePolarSpinners();
		shouldTriggerUpdate = true;
	}

	/**
	 * Updates the visual stickPosition -> is called on changes
	 */
	private void updateVisual (){
		joystick.setThumbPos(new Point(stickPosition.getX(), stickPosition.getY()));
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

	public InputLine[] getInputLines() {
		return inputLines;
	}
	public StickType getStickType(){
		return stickType;
	}
}
