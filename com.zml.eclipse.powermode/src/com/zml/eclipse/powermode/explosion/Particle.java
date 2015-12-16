package com.zml.eclipse.powermode.explosion;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class Particle {

	public static final int STATE_ALIVE = 0; // particle is alive
	public static final int STATE_DEAD = 1; // particle is dead

	public static final int DEFAULT_LIFETIME = 20; // play with this
	public static final int MAX_DIMENSION = 5; // the maximum width or height
	public static final int MAX_SPEED = 10; // maximum speed (per update)

	private int state; // particle is alive or dead
	private float widht; // width of the particle
	private float height; // height of the particle
	private float x, y; // horizontal and vertical position
	private double xv, yv; // vertical and horizontal velocity
	private int age; // current age of the particle
	private int lifetime; // particle dies when it reaches this value
	private Color color; // the color of the particle

	private int alpha = 255;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public float getWidht() {
		return widht;
	}

	public void setWidht(float widht) {
		this.widht = widht;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public double getXv() {
		return xv;
	}

	public void setXv(double xv) {
		this.xv = xv;
	}

	public double getYv() {
		return yv;
	}

	public void setYv(double yv) {
		this.yv = yv;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getLifetime() {
		return lifetime;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	// helper methods -------------------------
	public boolean isAlive() {
		return this.state == STATE_ALIVE;
	}

	public boolean isDead() {
		return this.state == STATE_DEAD;
	}

	public Particle(int x, int y) {
		this.x = x;
		this.y = y;
		this.state = Particle.STATE_ALIVE;
		this.widht = rndInt(1, MAX_DIMENSION);
		this.height = this.widht;
		this.lifetime = DEFAULT_LIFETIME;
		this.age = 0;
		this.xv = (rndDbl(0, MAX_SPEED * 2) - MAX_SPEED);
		this.yv = (rndDbl(0, MAX_SPEED * 2) - MAX_SPEED);

		// smoothing out the diagonal speed
		if (xv * xv + yv * yv > MAX_SPEED * MAX_SPEED) {
			xv *= 0.7;
			yv *= 0.7;
		}

		color = new Color(Display.getDefault(), rndInt(0, 255), rndInt(0, 255), rndInt(0, 255));
	}

	/**
	 * Resets the particle
	 * 
	 * @param x
	 * @param y
	 */
	public void reset(float x, float y) {
		this.state = Particle.STATE_ALIVE;
		this.x = x;
		this.y = y;
		this.age = 0;
	}

	// Return an integer that ranges from min inclusive to max inclusive.
	static int rndInt(int min, int max) {
		return (int) (min + Math.random() * (max - min + 1));
	}

	static double rndDbl(double min, double max) {
		return min + (max - min) * Math.random();
	}

	public void update() {
		if (this.state != STATE_DEAD) {
			this.x += this.xv;
			this.y += this.yv;

			alpha -= 30;
			if (alpha <= 0) { // if reached transparency kill the particle
				this.state = STATE_DEAD;
			} else {
				this.age++; // increase the age of the particle
			}
			if (this.age >= this.lifetime) { // reached the end if its life
				this.state = STATE_DEAD;
			}

		}
	}

	public void draw(GC gc) {
		int oldAlpha = gc.getAlpha();
		Color oldColor = gc.getBackground();

		gc.setAlpha(alpha);
		gc.setBackground(color);
		gc.fillOval((int) x, (int) y, (int) this.widht, (int) this.height);

		gc.setBackground(oldColor);
		gc.setAlpha(oldAlpha);
	}

	public void dispose() {
		if (color != null && !color.isDisposed()) {
			color.dispose();
		}
	}

}
