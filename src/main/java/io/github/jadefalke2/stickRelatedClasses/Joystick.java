package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Joystick extends JPanel {

	private final static int BORDER_THICKNESS = 2;
	private final static int OUTPUT_MAX = StickPosition.MAX_SIZE;

	// Coordinates + Data
	private int thumbDiameter;
	private int thumbRadius;
	private int panelWidth;

	private final Settings settings;

	// stick positions
	private StickPosition[] stickPositions = new StickPosition[0];

	private StickPosition currentPos;
	private Point visualOffset;

	private CustomChangeListener<StickPosition> onChange = null;

	public Joystick(Settings settings) {
		this.settings = settings;
		panelWidth = Math.min(getWidth(), getHeight());
		thumbDiameter = panelWidth / 15;
		thumbRadius = thumbDiameter / 2;

		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				requestFocus();

				if (SwingUtilities.isLeftMouseButton(e)) {
					updateThumbPos(e.getX(), e.getY(), false);
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					updateThumbPos(e.getX(), e.getY(), false);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					updateThumbPos(e.getX(), e.getY(), true);
				}
			}
		};

		KeyListener keyListener = new KeyAdapter() {
			private StickPosition setPos(int angle) {
				if (angle == -1)
					return null;

				if (angle == -2) {
					setThumbPos(new StickPosition(0f, 0), true);
					return null;
				}

				return new StickPosition(angle,1d);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				StickPosition pos = setPos(numPadToAngle(e.getKeyCode()));

				if (pos != null) {
					setThumbPos(pos, true);
				}
			}
		};

		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		addKeyListener(keyListener);

		setThumbPos(new StickPosition(0f, 0), false);
	}

	private static int numPadToAngle(int keyCode){
		if(keyCode == KeyEvent.VK_NUMPAD6) return 0;
		if(keyCode == KeyEvent.VK_NUMPAD9) return 45;
		if(keyCode == KeyEvent.VK_NUMPAD8) return 90;
		if(keyCode == KeyEvent.VK_NUMPAD7) return 135;
		if(keyCode == KeyEvent.VK_NUMPAD4) return 180;
		if(keyCode == KeyEvent.VK_NUMPAD1) return 225;
		if(keyCode == KeyEvent.VK_NUMPAD2) return 270;
		if(keyCode == KeyEvent.VK_NUMPAD3) return 315;
		//special codes
		if(keyCode == KeyEvent.VK_NUMPAD5) return -2; //middle
		//if nothing matched so far -> default
		return -1;
	}

	/**
	 * update both thumbPos
	 *
	 * @param mouseX the x position of cursor that has clicked in the joystick panel
	 * @param mouseY the y position of cursor that has clicked in the joystick panel
	 */

	private void updateThumbPos(int mouseX, int mouseY, boolean triggerAction) {

		if (!isEnabled())
			return;

		// if the cursor is clicked out of bounds, we'll modify the position
		// to be the closest point where we can draw the thumb pad completely
		mouseX -= visualOffset.x;
		mouseY -= visualOffset.y;

		setThumbPos(visualToStick(new Point(mouseX, mouseY)), triggerAction);
	}

 	/**
	 * @param thumbPos selected position on the panel (visually)
	 * @return the scaled position of the joystick thumb pad (in normal range)
	 */
	public StickPosition visualToStick(Point thumbPos) {
		return new StickPosition(
			OUTPUT_MAX * (thumbPos.x - panelWidth / 2f) / (panelWidth / 2f - thumbDiameter / 2f),
			-OUTPUT_MAX * (thumbPos.y - panelWidth / 2f) / (panelWidth / 2f - thumbDiameter / 2f)
		);
	}

	public Point stickToVisual (StickPosition pos){
		return new Point(
			(int)((pos.getX()/(double)OUTPUT_MAX) * ((panelWidth - thumbDiameter) / 2.0) + (panelWidth / 2.0)),
			(int)((pos.getY()/(double)-OUTPUT_MAX) * ((panelWidth - thumbDiameter) / 2.0) + (panelWidth / 2.0))
		);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		panelWidth = Math.min(getHeight(), getWidth());
		visualOffset = new Point((getWidth()-panelWidth)/2, (getHeight()-panelWidth)/2);
		g.translate(visualOffset.x, visualOffset.y);
		thumbDiameter = panelWidth / 15;
		thumbRadius = thumbDiameter / 2;

		//joystick background border
		g.setColor(Color.BLACK);
		// offset to make sure that center of thumb can reach border of circle
		g.fillOval(thumbRadius, thumbRadius, panelWidth - (thumbRadius*2), panelWidth - (thumbRadius*2));

		//joystick background color
		g.setColor(Color.GRAY);
		g.fillOval(thumbRadius + BORDER_THICKNESS, thumbRadius + BORDER_THICKNESS, panelWidth - (thumbRadius*2) - (BORDER_THICKNESS*2), panelWidth - (thumbRadius*2) - (BORDER_THICKNESS*2));


		for (int i = 0; i < stickPositions.length; i++){
			Point downscaled = stickToVisual(stickPositions[i]);

			double percentage = (i+1)/(double)(stickPositions.length+1);

			//overdraw positions painted below this one
			g.setColor(Color.GRAY);
			g.fillOval((int)downscaled.getX() - thumbRadius - BORDER_THICKNESS, (int) downscaled.getY() - thumbRadius - BORDER_THICKNESS, thumbDiameter + BORDER_THICKNESS * 2, thumbDiameter + BORDER_THICKNESS * 2);

			//black border
			g.setColor(new Color(0,0,0,(int)(150*percentage)));
			g.fillOval((int)downscaled.getX() - thumbRadius - BORDER_THICKNESS, (int) downscaled.getY() - thumbRadius - BORDER_THICKNESS, thumbDiameter + BORDER_THICKNESS * 2, thumbDiameter + BORDER_THICKNESS * 2);

			//thumb pad color
			g.setColor(new Color(255,0,0,(int)(150*percentage)));
			g.fillOval((int)downscaled.getX() - thumbRadius, (int) downscaled.getY() - thumbRadius, thumbDiameter, thumbDiameter);
		}

		Point downscaled = new Point(stickToVisual(currentPos));

		//black border
		g.setColor(Color.BLACK);
		g.fillOval((int)downscaled.getX() - thumbRadius - BORDER_THICKNESS, (int) downscaled.getY() - thumbRadius - BORDER_THICKNESS, thumbDiameter + BORDER_THICKNESS * 2, thumbDiameter + BORDER_THICKNESS * 2);

		//thumb pad color
		g.setColor(Color.RED);
		g.fillOval((int)downscaled.getX() - thumbRadius, (int) downscaled.getY() - thumbRadius, thumbDiameter, thumbDiameter);

		//Middle lines
		g.setColor(Color.black);
		g.drawLine(panelWidth / 2, thumbRadius + BORDER_THICKNESS, panelWidth / 2, panelWidth - thumbRadius - BORDER_THICKNESS);
		g.drawLine(thumbRadius + BORDER_THICKNESS, panelWidth / 2, panelWidth - thumbRadius - BORDER_THICKNESS, panelWidth / 2);

		if(!isEnabled()){
			if(settings.isDarkTheme())
				g.setColor(new Color(0, 0, 0, 150));
			else
				g.setColor(new Color(255, 255, 255, 150));
			g.fillOval(thumbRadius, thumbRadius, panelWidth - thumbDiameter, panelWidth - thumbDiameter);
		}
	}

	@Override
	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);

		if(!isEnabled())
			setThumbPos(new StickPosition(0f, 0), false);
		repaint();
	}

	public void setStickPositions(StickPosition[] stickPositions) {
		this.stickPositions = stickPositions;
		repaint();
	}

	public void setThumbPos (StickPosition pos, boolean triggerAction){
		currentPos = pos;
		repaint();

		if(onChange != null) {
			if(triggerAction)
				onChange.stateChanged(new ChangeObject<>(pos, this));
			else
				onChange.silentStateChanged(new ChangeObject<>(pos, this));
		}
	}

	public void setOnChangeListener(CustomChangeListener<StickPosition> onChange){
		this.onChange = onChange;
	}

	public StickPosition getThumbPos(){
		return currentPos;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(250,250);
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(1024, 1024);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(50, 50);
	}
}
