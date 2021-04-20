package io.github.jadefalke2;

import java.awt.*;
import java.awt.event.MouseEvent;

public class StickPosition {

	private int x;
	private int y;

	private double theta;
	private double radius;

	private final static int MAX_SIZE = 32767;

	public StickPosition(int x, int y) {
		this.x = x;
		this.y = y;
		updatePolar();
	}

	public StickPosition(StickPosition pos) {
		x = pos.x;
		y = pos.y;
		theta = pos.theta;
		radius = pos.radius;
	}

	private void updatePolar() {
		radius = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) / (double) MAX_SIZE;

		if (radius > 1) {
			radius = 1;
		}

		/*if (x >= 0 && y >= 0){
			theta = Math.atan((double)y/(double)x);
		}

		if (x < 0){
			theta = Math.PI - Math.atan((double)y/(double)x);
		}

		if (x >= 0 && y < 0){
			theta = (Math.PI*2) + Math.atan((double)y/(double)x);
		}

		 */

		theta = Math.atan2((double) y, (double) x);
	}

	private void updateCart() {
		x = (int) ((radius * MAX_SIZE) * Math.cos(theta));
		y = (int) ((radius * MAX_SIZE) * Math.sin(theta));
		updatePolar();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public double getTheta() {
		return theta;
	}

	public double getRadius() {
		return radius;
	}

	public void setX(int x) {
		this.x = x;
		updatePolar();
	}

	public void setY(int y) {
		this.y = y;
		updatePolar();
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		updatePolar();
	}

	public void setTheta(int theta) {
		this.theta = Math.toRadians(theta);
		updateCart();
	}

	public void setRadius(double radius) {
		this.radius = radius;
		updateCart();
	}

	public boolean isZeroZero (){
		return x == 0 && y == 0;
	}

	@Override
	public String toString() {
		return x + ";" + y;
	}

	public String toPolarString (){
		return Math.floor(Math.toDegrees(theta)) + "°, " + radius;
	}
}
