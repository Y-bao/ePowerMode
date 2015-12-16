package com.zml.eclipse.powermode.explosion;

import org.eclipse.swt.graphics.GC;

public class Explosion {

	public static final int STATE_ALIVE = 0; // at least 1 particle is alive
	public static final int STATE_DEAD = 1; // all particles are dead

	private Particle[] particles; // particles in the explosion
	private int x, y; // the explosion's origin
	private float gravity; // the gravity of the explosion (+ upward, -
							// down)
	private float wind; // speed of wind on horizontal
	private int size; // number of particles
	private int state; // whether it's still active or not

	public Explosion(int particleNr, int x, int y) {
		this.state = STATE_ALIVE;
		this.particles = new Particle[particleNr];
		for (int i = 0; i < this.particles.length; i++) {
			Particle p = new Particle(x, y);
			this.particles[i] = p;
		}
		this.size = particleNr;
	}

	public Particle[] getParticles() {
		return particles;
	}

	public void setParticles(Particle[] particles) {
		this.particles = particles;
	}

	// helper methods -------------------------
	public boolean isAlive() {
		return this.state == STATE_ALIVE;
	}

	public boolean isDead() {
		return this.state == STATE_DEAD;
	}

	public void update() {
		if (this.state != STATE_DEAD) {
			boolean isDead = true;
			for (int i = 0; i < this.particles.length; i++) {
				if (this.particles[i].isAlive()) {
					this.particles[i].update();
					isDead = false;
				}
			}
			if (isDead) {
				this.state = STATE_DEAD;
			}
		}
	}

	public void draw(GC gc) {
		for (int i = 0; i < this.particles.length; i++) {
			if (this.particles[i].isAlive()) {
				this.particles[i].draw(gc);
			}
		}
	}

	public void dispose() {
		for (int i = 0; i < this.particles.length; i++) {
			this.particles[i].setState(Particle.STATE_DEAD);
			this.particles[i].dispose();
		}
	}
}
