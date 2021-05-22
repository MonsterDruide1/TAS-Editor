package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.StickAction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.awt.event.*;
import java.util.Arrays;

public class StickImagePanel extends JPanel {

	private final static int STICK_IMAGE_SIZE = 200;

	// frame number label
	private final JLabel frameNumberLabel;

	// Spinners
    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner radiusSpinner;
    private final JSpinner angleSpinner;

    // Joystick
    private final Joystick joystick;

    // Other stuff
    private StickPosition stickPosition;
    private StickType stickType;
	private InputLine[] inputLines;
	private int row;

    private final DefaultTableModel table;
    private final TAS parent; //TODO avoid this

	private boolean shouldTriggerUpdate = true;


    // Used to have a good way to differentiate sticks

	public enum StickType {
        L_STICK,R_STICK
    }


	public StickImagePanel(TAS parent, JTable jTable, Script script) {

		// setting global vars
		stickPosition = new StickPosition(0,0);
    	this.table = (DefaultTableModel) jTable.getModel();
		this.parent = parent;

		StickPosition[] stickPositions = new StickPosition[Math.min(row, parent.getPreferences().getLastStickPositionCount())];

		frameNumberLabel = new JLabel("Currently editing frames: " + " None");

		jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				switch (jTable.getSelectedRows().length) {

					case 0:
						// no rows selected
						setGreyedOut();
						frameNumberLabel.setText("Currently editing frames: " + " None");
						break;

					case 1:
						//one row
						setEditingRows();
						frameNumberLabel.setText("Currently editing frame: " + jTable.getSelectedRows()[0]);
						break;

					default:
						//more than one frame
						setEditingRows();
						frameNumberLabel.setText("Currently editing frames: " + jTable.getSelectedRows()[0] + " - " + jTable.getSelectedRows()[jTable.getSelectedRows().length - 1]);
				}
			}
		});

		// sets the contents of the stickpositions array to be the previous stick positions of the same stick
		for (int i = 0; i < stickPositions.length; i++){
			InputLine currentLine = script.getInputLines().get(row - stickPositions.length + i);

			stickPositions[i] = stickType == StickType.L_STICK ? currentLine.getStickL() : currentLine.getStickR();
		}

		joystick = new Joystick(32767, STICK_IMAGE_SIZE,stickPositions);


		joystick.setThumbPos(new Point(stickPosition.getX(),stickPosition.getY()));


        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();



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

        //TODO simplify these repeating listeners
        xSpinner.addChangeListener(e -> {
            stickPosition.setX((int) xSpinner.getValue());
           	if(shouldTriggerUpdate){
				shouldTriggerUpdate = false;
           		updatePolarSpinners();
				updateVisual();
				repaint();
				shouldTriggerUpdate = true;
			}
        });
        ySpinner.addChangeListener(e -> {
            stickPosition.setY((int) ySpinner.getValue());
            if(shouldTriggerUpdate){
				shouldTriggerUpdate = false;
				updatePolarSpinners();
				updateVisual();
				repaint();
				shouldTriggerUpdate = true;
			}
        });
        radiusSpinner.addChangeListener(e -> {
            stickPosition.setRadius((double) radiusSpinner.getValue());
            if(shouldTriggerUpdate){
				shouldTriggerUpdate = false;
				updateCartSpinners();
				updateVisual();
				repaint();
				shouldTriggerUpdate = true;
			}
        });
        angleSpinner.addChangeListener(e -> {
            stickPosition.setTheta((int)angleSpinner.getValue());
            if(shouldTriggerUpdate){
				shouldTriggerUpdate = false;
				updateCartSpinners();
				updateVisual();
				repaint();
				shouldTriggerUpdate = true;
			}
        });



        MouseAdapter mouseListener = new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				updateStickPosition(false);
			}

            @Override
            public void mouseReleased(MouseEvent e){
                updateStickPosition(true);
            }

        };

		KeyListener keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				//
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

        c.gridwidth = 4;
        c.anchor = GridBagConstraints.PAGE_START;
        add(frameNumberLabel, c);

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


		JButton centerButton = new JButton("center");
		centerButton.addActionListener(e -> {
			joystick.centerThumbPad();
			updateStickPosition(true);
		});

		JButton keepStickPosButton = new JButton("keep stick position for # of frames");
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

				}else{
					script.getInputLines().get(i).setStickR(script.getInputLines().get(row).getStickR().clone());
					table.setValueAt(script.getInputLines().get(i).getStickR().toCartString(), i,2);
				}

			}
		});

		JButton smoothTransitionButton = new JButton("smooth transition");
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
    }


	/**
	 * Updates the stick position based on the sliders
	 */
	private void updateStickPosition(boolean executeAction) {
    	StickPosition oldStickPosition = stickPosition.clone();


        int x = (int)joystick.getThumbPos().getX();
        int y = (int)joystick.getThumbPos().getY();

		stickPosition.setPosition(x,y);

        shouldTriggerUpdate = false;
        xSpinner.setValue(stickPosition.getX());
        ySpinner.setValue(stickPosition.getY());
        radiusSpinner.setValue(stickPosition.getRadius());
        angleSpinner.setValue((int) Math.toDegrees(stickPosition.getTheta()));
        updateVisual();
        shouldTriggerUpdate = true;

        if(executeAction)
        	parent.executeAction(new StickAction(inputLines, stickType, oldStickPosition, stickPosition, table, row));

		repaint();
	}

	private void setGreyedOut () {
		//TODO lock spinners + joystick
	}

	private void setEditingRows () {
		//TODO set InputLines and update them
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
