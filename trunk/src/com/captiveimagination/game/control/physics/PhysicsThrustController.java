package com.captiveimagination.game.control.physics;

import com.captiveimagination.game.control.ThrottleController;
import com.jme.input.controls.GameControl;
import com.jme.input.controls.controller.Axis;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;

/**
 * @author Matthew D. Hicks
 */
public class PhysicsThrustController extends ThrottleController {
	private static final long serialVersionUID = 1L;

	private DynamicPhysicsNode node;
	private Axis axis;
	private float stepSpeed;
	private float maxForwardSpeed;
	private float maxReverseSpeed;

	private Vector3f storeCurrentSpeed;
	private Vector3f storeDesiredSpeed;
	private Vector3f storeSpeed;

	public PhysicsThrustController(DynamicPhysicsNode node, Axis axis, GameControl controlForward, GameControl controlReverse, float step, float stepSpeed, float maxForwardSpeed, float maxReverseSpeed) {
		super(controlForward, controlReverse, step);
		this.node = node;
		this.axis = axis;
		this.stepSpeed = stepSpeed;
		this.maxForwardSpeed = maxForwardSpeed;
		this.maxReverseSpeed = maxReverseSpeed;

		storeCurrentSpeed = new Vector3f();
		storeDesiredSpeed = new Vector3f();
		storeSpeed = new Vector3f();
	}

	public float getDesiredSpeed() {
		float throttle = getThrottle();
		float desired;
		if (throttle < 0.0f) {
			desired = throttle * maxReverseSpeed;
		} else {
			desired = throttle * maxForwardSpeed;
		}

		return desired;
	}

	@Override
	public float getSpeed() {
		return node.getLinearVelocity(storeSpeed).dot(node.getLocalRotation().getRotationColumn(axis.getRotationColumn()));
	}

	@Override
	public void update(float time) {
		super.update(time);

		float desiredSpeed = getDesiredSpeed();
		float increment = stepSpeed * time;
		node.getLinearVelocity(storeCurrentSpeed);
		storeDesiredSpeed.set(node.getLocalRotation().getRotationColumn(axis.getRotationColumn()));
		storeDesiredSpeed.multLocal(desiredSpeed);

		if (storeCurrentSpeed.x < storeDesiredSpeed.x) {
			storeCurrentSpeed.x += increment;
			if (storeCurrentSpeed.x > storeDesiredSpeed.x) {
				storeCurrentSpeed.x = storeDesiredSpeed.x;
			}
		} else if (storeCurrentSpeed.x > storeDesiredSpeed.x) {
			storeCurrentSpeed.x -= increment;
			if (storeCurrentSpeed.x < storeDesiredSpeed.x) {
				storeCurrentSpeed.x = storeDesiredSpeed.x;
			}
		}
		if (storeCurrentSpeed.y < storeDesiredSpeed.y) {
			storeCurrentSpeed.y += increment;
			if (storeCurrentSpeed.y > storeDesiredSpeed.y) {
				storeCurrentSpeed.y = storeDesiredSpeed.y;
			}
		} else if (storeCurrentSpeed.y > storeDesiredSpeed.y) {
			storeCurrentSpeed.y -= increment;
			if (storeCurrentSpeed.y < storeDesiredSpeed.y) {
				storeCurrentSpeed.y = storeDesiredSpeed.y;
			}
		}
		if (storeCurrentSpeed.z < storeDesiredSpeed.z) {
			storeCurrentSpeed.z += increment;
			if (storeCurrentSpeed.z > storeDesiredSpeed.z) {
				storeCurrentSpeed.z = storeDesiredSpeed.z;
			}
		} else if (storeCurrentSpeed.z > storeDesiredSpeed.z) {
			storeCurrentSpeed.z -= increment;
			if (storeCurrentSpeed.z < storeDesiredSpeed.z) {
				storeCurrentSpeed.z = storeDesiredSpeed.z;
			}
		}
		node.setLinearVelocity(storeCurrentSpeed);
	}

	public float getMaxForwardSpeed() {

		return maxForwardSpeed;
	}

	public float getMaxReverseSpeed() {

		return maxReverseSpeed;
	}

	public Vector3f storeCurrentSpeed() {

		return storeCurrentSpeed;
	}

	public Vector3f storeDesiredSpeed() {

		return storeDesiredSpeed;
	}
	public Vector3f storeSpeed() {

		return storeSpeed;
	}
}