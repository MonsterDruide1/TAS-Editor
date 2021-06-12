package io.github.jadefalke2.stickRelatedClasses;

import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;

public class Joystick extends JPanel {

	private final static int BORDER_THICKNESS = 2;

	// Coordinates + Data
	private final int outputMax;
	private int thumbDiameter;
	private int thumbRadius;
	private int panelWidth;

	private final Settings settings;

	// stick positions
	private StickPosition[] stickPositions = new StickPosition[0];

	private Point currentPos;
	private Point visualOffset;
	protected final SwingPropertyChangeSupport propertySupporter = new SwingPropertyChangeSupport(this);


	/**
	 * @param output_max  The maximum value to scale output to. If this value was
	 *                    5 and the joystick thumb was dragged to the top-left corner, the output
	 *                    would be (-5,5)
	 */

	public Joystick(int output_max, Settings settings) {
		assert output_max > 0;

		this.settings = settings;
		outputMax = output_max;
		panelWidth = Math.min(getWidth(), getHeight());
		thumbDiameter = panelWidth / 15;
		thumbRadius = thumbDiameter / 2;

		MouseAdapter mouseAdapter = new MouseAdapter() {

			private void repaintAndTriggerListeners() {
				SwingUtilities.getRoot(Joystick.this).repaint();
				propertySupporter.firePropertyChange(null, null, 1);
			}

			@Override
			public void mousePressed(final MouseEvent e) {

				requestFocus();

				if (SwingUtilities.isLeftMouseButton(e)) {
					updateThumbPos(e.getX(), e.getY());
					repaintAndTriggerListeners();
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					updateThumbPos(e.getX(), e.getY());
					repaintAndTriggerListeners();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					updateThumbPos(e.getX(), e.getY());
					repaintAndTriggerListeners();
				}
			}
		};

		KeyListener keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_RIGHT -> setThumbPos(new Point(32767, 0));
					case KeyEvent.VK_LEFT  -> setThumbPos(new Point(-32767, 0));
					case KeyEvent.VK_UP    -> setThumbPos(new Point(0, 32767));
					case KeyEvent.VK_DOWN  -> setThumbPos(new Point(0, -32767));
				}

				repaint();

				//TODO UPDATE PARENT PANEL
			}
		};

		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		addKeyListener(keyListener);

		centerThumbPad();
	}



	public void centerThumbPad() {
		currentPos = new Point(0, 0);
	}

	//TODO rework this section, as it does not make sense visually to scaled
	/**
	 * update both thumbPos
	 *
	 * @param mouseX the x position of cursor that has clicked in the joystick panel
	 * @param mouseY the y position of cursor that has clicked in the joystick panel
	 */

	private void updateThumbPos(int mouseX, int mouseY) {

		if (!isEnabled())
			return;

		// if the cursor is clicked out of bounds, we'll modify the position
		// to be the closest point where we can draw the thumb pad completely
		mouseX -= visualOffset.x;
		mouseY -= visualOffset.y;
		if (mouseX < thumbRadius)
			mouseX = thumbRadius;
		else if (mouseX > panelWidth - thumbRadius)
			mouseX = panelWidth - thumbRadius;

		if (mouseY < thumbRadius)
			mouseY = thumbRadius;
		else if (mouseY > panelWidth - thumbRadius)
			mouseY = panelWidth - thumbRadius;

		currentPos = getOutputPos(new Point(mouseX, mouseY));
	}

 	/**
	 * @param thumbPos selected position on the panel (visually)
	 * @return the scaled position of the joystick thumb pad (in normal range)
	 */
	public Point getOutputPos(Point thumbPos) {
		Point result = new Point();
		result.x = outputMax * (thumbPos.x - panelWidth / 2) / (panelWidth / 2 - thumbDiameter / 2);
		result.y = -outputMax * (thumbPos.y - panelWidth / 2) / (panelWidth / 2 - thumbDiameter / 2);
		return result;
	}

	public Point scaledToVisual (Point scaled){
		return new Point((int)((scaled.x/(double)outputMax) * (panelWidth / 2.0 - thumbDiameter / 2.0) + (panelWidth / 2.0)),(int)((scaled.y/(double)-outputMax) * (panelWidth / 2.0 - thumbDiameter / 2.0) + (panelWidth / 2.0)));
	}

	public void setThumbPos (Point scaled){
		currentPos = scaled;
	}

	public StickPosition getStickPosition(){
		return new StickPosition(currentPos.x, currentPos.y);
	}


	// Overwrites


	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		panelWidth = Math.min(getHeight(), getWidth());
		visualOffset = new Point(getWidth()/2-panelWidth/2, getHeight()/2-panelWidth/2);
		g.translate(visualOffset.x, visualOffset.y);
		thumbDiameter = panelWidth / 15;
		thumbRadius = thumbDiameter / 2;

		//joystick background border
		g.setColor(Color.BLACK);
		g.fillOval(thumbRadius, thumbRadius, panelWidth - thumbDiameter, panelWidth - thumbDiameter);

		//joystick background color
		g.setColor(Color.GRAY);
		g.fillOval(thumbRadius + BORDER_THICKNESS, thumbRadius + BORDER_THICKNESS, panelWidth - thumbDiameter - BORDER_THICKNESS * 2, panelWidth - thumbDiameter - BORDER_THICKNESS * 2);


		for (int i = 0; i < stickPositions.length; i++){
			Point tmp = new Point(stickPositions[i].getX(),stickPositions[i].getY());
			Point downscaled = new Point(scaledToVisual(tmp));

			double percentage = (i+1)/(double)(stickPositions.length+1);

			//overdraw positions painted below this one
			g.setColor(Color.GRAY);
			g.fillOval((int)downscaled.getX() - thumbRadius - BORDER_THICKNESS, (int) downscaled.getY() - thumbRadius - BORDER_THICKNESS, thumbRadius * 2 + BORDER_THICKNESS * 2, thumbRadius * 2 + BORDER_THICKNESS * 2);

			//black border
			g.setColor(new Color(0,0,0,(int)(150*percentage)));
			g.fillOval((int)downscaled.getX() - thumbRadius - BORDER_THICKNESS, (int) downscaled.getY() - thumbRadius - BORDER_THICKNESS, thumbRadius * 2 + BORDER_THICKNESS * 2, thumbRadius * 2 + BORDER_THICKNESS * 2);

			//thumb pad color
			g.setColor(new Color(255,0,0,(int)(150*percentage)));
			g.fillOval((int)downscaled.getX() - thumbRadius, (int) downscaled.getY() - thumbRadius, thumbRadius * 2, thumbRadius * 2);
		}

		Point downscaled = new Point(scaledToVisual(currentPos));

		g.setColor(Color.BLACK);
		g.fillOval((int)downscaled.getX() - thumbRadius - BORDER_THICKNESS, (int) downscaled.getY() - thumbRadius - BORDER_THICKNESS, thumbRadius * 2 + BORDER_THICKNESS * 2, thumbRadius * 2 + BORDER_THICKNESS * 2);

		//thumb pad color
		g.setColor(Color.RED);
		g.fillOval((int)downscaled.getX() - thumbRadius, (int) downscaled.getY() - thumbRadius, thumbRadius * 2, thumbRadius * 2);

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
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupporter.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupporter.removePropertyChangeListener(listener);
	}

	public void setStickPositions(StickPosition[] stickPositions) {
		this.stickPositions = stickPositions;
		repaint();
	}

	@Override
	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);

		if(!isEnabled())
			centerThumbPad();
	}
}
