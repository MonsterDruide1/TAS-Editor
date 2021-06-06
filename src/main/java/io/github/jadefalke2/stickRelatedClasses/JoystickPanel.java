package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

import java.awt.event.*;

public class JoystickPanel extends JPanel {

	//Labels
	private final JLabel stickTypeIndicator;


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

	private CustomChangeListener onChange = null;

	private boolean shouldTriggerUpdate = true;

	// Used to have a good way to differentiate sticks

	public enum StickType {
        L_STICK,R_STICK
    }

	public JoystickPanel(Settings settings, String descriptor) {
		this(settings, null, descriptor);
	}
	public JoystickPanel(Settings settings, ActionListener smoothTransitionListener, String descriptor) {

		// setting global vars
		stickPosition = new StickPosition(0,0);
		joystick = new Joystick(32767, settings);
		joystick.setThumbPos(new Point(stickPosition.getX(),stickPosition.getY()));

		stickTypeIndicator = new JLabel(descriptor);
		stickTypeIndicator.setHorizontalAlignment(JLabel.CENTER);
		// spinners
		SpinnerModel xModel = new SpinnerNumberModel(0, -32767, 32767, 100);
		SpinnerModel yModel = new SpinnerNumberModel(0, -32767, 32767, 100);
		SpinnerModel radiusModel = new SpinnerNumberModel(0, 0, 1, 0.1);
		SpinnerModel angleModel = new SpinnerNumberModel(0, -1, 361, 1);

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
					stickPosition = new StickPosition((((int)angleSpinner.getValue() + 360) % 360), stickPosition.getRadius());
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
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 4;
		add(stickTypeIndicator, c);

		c.weightx = 1;
        c.gridwidth = 2;
        c.gridy = 1;
        add(cartesianLabel, c);

        c.gridx = 3;
        add(polarLabel, c);

        c.gridwidth = 1;
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
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 4;

		add(joystick, c);

		c.weighty = 0;

		centerButton = new JButton("center");
		centerButton.addActionListener(e -> {
			joystick.centerThumbPad();
			updateStickPosition(true, stickPosition);
		});

		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 5;

		if(smoothTransitionListener != null){
			smoothTransitionButton = new JButton("smooth transition");
			smoothTransitionButton.addActionListener(smoothTransitionListener);
			add(smoothTransitionButton, c);
		} else {
			smoothTransitionButton = null;
		}

		c.gridy = 6;
		add(centerButton, c);
    }


    private void applyPosition(StickPosition newPos, StickPosition oldPos){
		if(onChange == null) return;

		onChange.stateChanged(new ChangeObject<>(oldPos, newPos, this));
	}

	/**
	 * Updates the stick position based on the sliders
	 */
	private void updateStickPosition(boolean executeAction, StickPosition oldStickPosition) {
		StickPosition unCropped = joystick.getStickPosition();
		double radius = Math.min(unCropped.getRadius(), 1);
		stickPosition = new StickPosition(unCropped.getTheta(), radius);

        updateAll();

        if(executeAction)
        	applyPosition(stickPosition, oldStickPosition);
	}

	public void setStickPosition(StickPosition stickPosition){
		this.stickPosition = stickPosition;
		updateAll();
	}

	public void setStickPositions(StickPosition[] stickPositions){
		joystick.setStickPositions(stickPositions);
	}

	public StickPosition getStickPosition(){
		return stickPosition;
	}

	public void setOnChangeListener(CustomChangeListener onChange){
		this.onChange = onChange;
	}

	public void setAllEnabled(boolean enable) {
		if(!enable){
			setStickPosition(new StickPosition(0, 0));
			setStickPositions(new StickPosition[0]);
		}

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
		angleSpinner.setValue((int)Math.round(Math.toDegrees(stickPosition.getTheta())));
		radiusSpinner.setValue(stickPosition.getRadius());
	}
}
