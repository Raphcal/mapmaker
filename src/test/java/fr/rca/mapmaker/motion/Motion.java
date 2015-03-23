package fr.rca.mapmaker.motion;

import java.beans.PropertyChangeSupport;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class Motion {
	
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private float maximumSpeed;
	private float maximumFallSpeed;
	private float acceleration;
	private float weight;
	private float jumpForce;
	private float groundInfluence;
	
	private boolean inAir;
	
	private float horizontalSpeed;
	private float verticalSpeed;
	
	private float direction;
	
	private float x;
	private float y;

	public Motion(float maximumSpeed, float maximumFallSpeed, float acceleration, float weight, float jumpForce, float groundInfluence) {
		this.maximumSpeed = maximumSpeed;
		this.maximumFallSpeed = maximumFallSpeed;
		this.acceleration = acceleration;
		this.weight = weight;
		this.jumpForce = jumpForce;
		this.groundInfluence = groundInfluence;
	}

	public static Motion getDefaultMotion() {
		return new Motion(400f, 700f, 200f, 750f, 320f, 100f);
	}
	
	public void reset() {
		x = 0.0f;
		y = 0.0f;
		horizontalSpeed = 0.0f;
		verticalSpeed = 0.0f;
		inAir = false;
	}

	public void jump() {
		final double angle = -Math.PI / 2.0;
		this.inAir = true;
		this.verticalSpeed = -jumpForce;
		double horizontalBonus = Math.cos(angle - Math.PI / 2.0) * groundInfluence;
		if (Math.abs(horizontalBonus) < 0.1) {
			horizontalBonus = 0.0;
		}
		horizontalSpeed += Math.abs(horizontalBonus);
	}

	public void update(float delta) {
		if (inAir) {
			updateInAir(delta);
		} else {
			updateOnGround(delta);
		}
		horizontalSpeed = Math.min(horizontalSpeed, maximumSpeed);
		verticalSpeed = Math.min(verticalSpeed, maximumFallSpeed);
		x += horizontalSpeed * delta;
		y += verticalSpeed * delta;
	}

	private void updateOnGround(float delta) {
		final double angle = 0.0; // -Math.PI / 2.0;
		horizontalSpeed += Math.abs(delta * direction * acceleration * Math.cos(angle));
	}

	private void updateInAir(float delta) {
		final double angle = 0.0;
		horizontalSpeed += Math.abs(delta * direction * acceleration * Math.cos(angle));
		verticalSpeed += weight * delta;
	}

	public boolean isInAir() {
		return inAir;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		final float old = this.x;
		this.x = x;
		propertyChangeSupport.firePropertyChange("x", old, x);
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		final float old = this.y;
		this.y = y;
		propertyChangeSupport.firePropertyChange("y", old, y);
	}

	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		final float old = this.direction;
		this.direction = direction;
		propertyChangeSupport.firePropertyChange("direction", old, direction);
	}

	public float getMaximumSpeed() {
		return maximumSpeed;
	}

	public void setMaximumSpeed(float maximumSpeed) {
		final float old = this.maximumSpeed;
		this.maximumSpeed = maximumSpeed;
		propertyChangeSupport.firePropertyChange("maximumSpeed", old, maximumSpeed);
	}

	public float getMaximumFallSpeed() {
		return maximumFallSpeed;
	}

	public void setMaximumFallSpeed(float maximumFallSpeed) {
		final float old = this.maximumFallSpeed;
		this.maximumFallSpeed = maximumFallSpeed;
		propertyChangeSupport.firePropertyChange("maximumFallSpeed", old, maximumFallSpeed);
	}

	public float getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(float acceleration) {
		final float old = this.acceleration;
		this.acceleration = acceleration;
		propertyChangeSupport.firePropertyChange("acceleration", old, acceleration);
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		final float old = this.weight;
		this.weight = weight;
		propertyChangeSupport.firePropertyChange("weight", old, weight);
	}

	public float getJumpForce() {
		return jumpForce;
	}

	public void setJumpForce(float jumpForce) {
		final float old = this.jumpForce;
		this.jumpForce = jumpForce;
		propertyChangeSupport.firePropertyChange("jumpForce", old, jumpForce);
	}

	public float getGroundInfluence() {
		return groundInfluence;
	}

	public void setGroundInfluence(float groundInfluence) {
		final float old = this.groundInfluence;
		this.groundInfluence = groundInfluence;
		propertyChangeSupport.firePropertyChange("groundInfluence", old, groundInfluence);
	}

}
