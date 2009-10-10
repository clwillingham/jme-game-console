package com.captiveimagination.game.control;

import com.jme.input.controls.GameControl;
import com.jme.scene.Controller;

/**
 * A controller to gradually increase or decrease speed to 1.0f/-1.0f based on forward or reverse
 * GameControls.
 *
 * @author Matthew D. Hicks
 */
public class ThrottleController extends Controller {
	private static final long serialVersionUID = 1L;

	private GameControl controlForward;
	private GameControl controlReverse;
	private float step;
	private float throttle;

	private float deadZone;
	private boolean alwaysDegrade;

	private long zeroEncountered;

	public ThrottleController(GameControl controlForward, GameControl controlReverse, float step) {
		this.controlForward = controlForward;
		this.controlReverse = controlReverse;
		this.step = step;

		deadZone = 0.05f;
	}

	public float getDeadZone() {
		return deadZone;
	}

	public void setDeadZone(float deadZone) {
		this.deadZone = deadZone;
	}

	public boolean isAlwaysDegrade() {
		return alwaysDegrade;
	}

	public void setAlwaysDegrade(boolean alwaysDegrade) {
		this.alwaysDegrade = alwaysDegrade;
	}

	public float getDesiredThrottle() {
		float desiredThrottle = controlForward.getValue() - controlReverse.getValue();
		if ((desiredThrottle < deadZone) && (desiredThrottle > -deadZone)) {
			desiredThrottle = 0.0f;
		}
		return desiredThrottle;
	}

	public float getThrottle() {
		return throttle;
	}

	@Override
	public void update(float time) {
		// We want to give the ability to hit 0.0f exactly, so we delay a bit
		if (System.currentTimeMillis() < zeroEncountered + 100) return;

		float desiredThrottle = getDesiredThrottle();

		float increment = step * time;
		float newThrottle = throttle;
		if ((desiredThrottle > 0.0f) && (throttle < desiredThrottle)) {				// Add Positive to throttle
			newThrottle += increment;
			if (newThrottle > desiredThrottle) {
				newThrottle = desiredThrottle;
			}
		} else if ((desiredThrottle < 0.0f) && (throttle > desiredThrottle)) {		// Add Negative to throttle
			newThrottle -= increment;
			if (newThrottle < desiredThrottle) {
				newThrottle = desiredThrottle;
			}
		} else if (desiredThrottle == 0.0f) {										// Degradation of throttle to zero
			if (throttle > 0.0f) {
				if ((alwaysDegrade) || (controlForward.hasTrueAxis())) {
					newThrottle -= increment;
					if (newThrottle < desiredThrottle) {
						newThrottle = desiredThrottle;
					}
				}
			} else if (throttle < 0.0f) {
				if ((alwaysDegrade) || (controlReverse.hasTrueAxis())) {
					newThrottle += increment;
					if (newThrottle > desiredThrottle) {
						newThrottle = desiredThrottle;
					}
				}
			}
		}
		if ((throttle > 0.0f) && (newThrottle < 0.0f)) {			// Changed from positive to negative in this update
			newThrottle = 0.0f;
			zeroEncountered = System.currentTimeMillis();
		} else if ((throttle < 0.0f) && (newThrottle > 0.0f)) {		// Changed from negative to positive in this update
			newThrottle = 0.0f;
			zeroEncountered = System.currentTimeMillis();
		}
		throttle = newThrottle;
	}
}