package io.github.jadefalke2.stickRelatedClasses;

import java.awt.*;

public class StickPosition {

	//TODO is this double-storage necessary?

	// the cartesian coordinates
	// Range of x: -32767;32767
	// Range of y: -32767;32767
	private int x;
	private int y;

	// the polar coordinates
	// Range of theta: 0;2π/0;360
	// Range of radius: 0;1
	private double theta;
	private double radius;

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
		updatePolar();
	}

	public StickPosition(String component) {
		this(Integer.parseInt(component.split(";")[0]), Integer.parseInt(component.split(";")[1]));
		updatePolar();
	}

	@Override
	public StickPosition clone(){
		return new StickPosition(x, y);
	}

	/**
	 * Updates the polar coordinates based on the cartesian coordinates
	 */
	private void updatePolar() {
		radius = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) / (double) MAX_SIZE;

		if (radius > 1) {
			radius = 1;
		}

		theta = ((Math.atan2(y,x) + (2*Math.PI)) % (2*Math.PI));
	}

	/**
	 * Updates the cartesian coordinates based on the polar coordinates
	 */
	private void updateCart() {
		x = (int) ((radius * MAX_SIZE) * Math.cos(theta));
		y = (int) ((radius * MAX_SIZE) * Math.sin(theta));
		updatePolar();
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
	 * @return the angle of the stick
	 */
	public double getTheta() {
		return theta;
	}

	/**
	 * @return the radius of the stick (distance from middle)
	 */
	public double getRadius() {
		return radius;
	}

	/**
     * @return true if the stick is at 0;0 (x;y), false otherwise
	 */
	public boolean isZeroZero (){
		return x == 0 && y == 0;
	}

	// setter

	/**
	 * sets the x coordinate of the stick
	 * @param x the value it is being set to
	 */
	public void setX(int x) {
		this.x = x;
		updatePolar();
	}

	/**
	 * sets the y coordinate of the stick
	 * @param y the value it is being set to
	 */
	public void setY(int y) {
		this.y = y;
		updatePolar();
	}

	/**
	 * sets the x and y position of the stick
	 * @param x the x value that x is being set to
	 * @param y the y value that y is being set to
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		updatePolar();
		updateCart(); //to apply clamped radius
	}

	/**
	 * sets the angle of the stick position
	 * @param theta the new angle
	 */
	public void setTheta(int theta) {
		this.theta = Math.toRadians(theta);
		updateCart();
	}

	/**
	 * sets the radius of the new stick position
	 * @param radius the new radius
	 */
	public void setRadius(double radius) {
		this.radius = radius;
		updateCart();
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
		return Math.floor(Math.toDegrees(theta)) + "°, " + radius;
	}

	@Override
	public String toString (){
		//return "Cartesian: " + toCartString() + "\n Polar: " + toPolarString();
		return toCartString();
	}
}
