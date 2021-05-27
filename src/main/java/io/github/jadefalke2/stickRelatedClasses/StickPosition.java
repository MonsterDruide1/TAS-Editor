package io.github.jadefalke2.stickRelatedClasses;

import java.awt.*;

public class StickPosition {

	// the cartesian coordinates
	// Range of x: -32767;32767
	// Range of y: -32767;32767
	private int x;
	private int y;

	// The max x/y range0
	private final static int MAX_SIZE = 32767;

	/**
	 * Constructor
	 * @param x the scaled x coordinate
	 * @param y the scaled y coordinate
	 */
	public StickPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructor
	 * @param theta the angle of this position (0-2π)
	 * @param radius radius of this position from 0;0
	 */
	public StickPosition(double theta, double radius) {
		x = (int) ((radius * MAX_SIZE) * Math.cos(theta));
		y = (int) ((radius * MAX_SIZE) * Math.sin(theta));
	}
	/**
	 * Constructor
	 * @param angle the angle of this position (0-360)
	 * @param radius radius of this position from 0;0
	 */
	public StickPosition(int angle, double radius) {
		this(Math.toRadians(angle), radius);
	}

	public StickPosition(String component) {
		this(Integer.parseInt(component.split(";")[0]), Integer.parseInt(component.split(";")[1]));
	}

	@Override
	public StickPosition clone(){
		return new StickPosition(x, y);
	}

	// getter

	public Point getPoint () {
		return new Point(x,y);
	}
	/**
	 * @return the x position of the stick
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y position of the stick
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return the angle of the stick (0-2π)
	 */
	public double getTheta() {
		return ((Math.atan2(y,x) + (2*Math.PI)) % (2*Math.PI));
	}

	/**
	 * @return the radius of the stick (distance from middle)
	 */
	public double getRadius() {
		return Math.sqrt(Math.pow(x / (double) MAX_SIZE, 2) + Math.pow(y / (double) MAX_SIZE, 2));
	}

	/**
     * @return true if the stick is at 0;0 (x;y), false otherwise
	 */
	public boolean isZeroZero (){
		return x == 0 && y == 0;
	}

	/**
	 * @return a string in cartesian coordinates
	 */
	public String toCartString() {
		return x + ";" + y;
	}

	/**
	 * @return a string in polar coordinates
	 */
	public String toPolarString (){
		return Math.floor(Math.toDegrees(getTheta())) + "°, " + getRadius();
	}

	@Override
	public String toString (){
		//return "Cartesian: " + toCartString() + "\n Polar: " + toPolarString();
		return toCartString();
	}
}
