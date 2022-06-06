package io.github.jadefalke2.stickRelatedClasses;

import java.awt.*;
import java.util.Objects;

public class StickPosition {

	// the cartesian coordinates
	// Range of x: -32767;32767
	// Range of y: -32767;32767
	private final int x;
	private final int y;

	// The max x/y range0
	public final static int MAX_SIZE = 32767;

	/**
	 * Constructor
	 * @param x the scaled x coordinate
	 * @param y the scaled y coordinate
	 */
	public StickPosition(int x, int y) {
		double radius = calcRadius(x, y);
		if(radius <= 1) {
			this.x = x;
			this.y = y;
		} else {
			radius = Math.min(radius, 1);
			double theta = calcTheta(x, y);
			this.x = (int) calcX(theta, radius);
			this.y = (int) calcY(theta, radius);
		}
	}

	/**
	 * Constructor
	 * @param theta the angle of this position (0-2π)
	 * @param radius radius of this position from 0;0
	 */
	public StickPosition(double theta, double radius) {
		this(
			(int) calcX(theta, radius),
			(int) calcY(theta, radius)
		);
	}

	private static double calcX(double theta, double radius) {
		return (radius * MAX_SIZE) * Math.cos(theta);
	}
	private static double calcY(double theta, double radius) {
		return (radius * MAX_SIZE) * Math.sin(theta);
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
		return calcTheta(x, y);
	}
	private static double calcTheta(int x, int y) {
		return ((Math.atan2(y,x) + (2*Math.PI)) % (2*Math.PI));
	}

	/**
	 * @return the radius of the stick (distance from middle)
	 */
	public double getRadius() {
		return calcRadius(x, y);
	}
	private static double calcRadius(int x, int y) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StickPosition that = (StickPosition) o;
		return x == that.x && y == that.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
