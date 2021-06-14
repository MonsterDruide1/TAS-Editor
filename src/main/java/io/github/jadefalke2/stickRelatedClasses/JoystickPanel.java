package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

import java.awt.event.*;

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

		//Labels
		JLabel stickTypeIndicator = new JLabel(descriptor);
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
		angleSpinner.setValue(Math.toDegrees(stickPosition.getTheta()));

        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setLayout(new GridBagLayout());

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
					stickPosition = new StickPosition((((double)angleSpinner.getValue() + 360) % 360), stickPosition.getRadius());
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


		joystick.addMouseListener(mouseListener);
		joystick.addMouseMotionListener(mouseListener);

        JPanel cartesianPanel = createSpinnerPanel("Cartesian", "x", xSpinner, "y", ySpinner);
        JPanel polarPanel = createSpinnerPanel("Polar", "radius", radiusSpinner, "angle", angleSpinner);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,3,5,3);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1;
		add(stickTypeIndicator, c);

		JPanel coordPanel = new JPanel(new GridLayout(0,2));
		coordPanel.add(cartesianPanel);
		coordPanel.add(polarPanel);

		c.gridy = 1;
		add(coordPanel, c);

        c.weighty = 1;
        c.gridy = 2;
		add(joystick, c);
		c.weighty = 0;

		centerButton = new JButton("center");
		centerButton.addActionListener(e -> {
			joystick.centerThumbPad();
			updateStickPosition(true, stickPosition);
		});

		c.gridy = 3;

		if(smoothTransitionListener != null){
			smoothTransitionButton = new JButton("smooth transition");
			smoothTransitionButton.addActionListener(smoothTransitionListener);
			add(smoothTransitionButton, c);
		} else {
			smoothTransitionButton = null;
		}

		c.gridy = 4;
		add(centerButton, c);
    }

    private static JPanel createSpinnerPanel(String title, String name1, JSpinner spinner1, String name2, JSpinner spinner2){
		JLabel titleLabel = new JLabel(title);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(5,3,5,3);

		c.gridwidth = 2;
		panel.add(titleLabel, c);

		c.gridwidth = 1;
		c.gridy = 1;
		panel.add(new JLabel(name1+":"), c);
		c.gridx = 1;
		panel.add(spinner1, c);

		c.gridy = 2;
		c.gridx = 0;
		panel.add(new JLabel(name2+":"), c);
		c.gridx = 1;
		panel.add(spinner2, c);

		return panel;
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
		angleSpinner.setValue(Math.toDegrees(stickPosition.getTheta()));
		radiusSpinner.setValue(stickPosition.getRadius());
	}
}
