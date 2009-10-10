/*
 * Copyright (c) 2007 shingoki
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'shingoki' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.captiveimagination.game.control;

/**
 * A simple spring simulation in one dimension
 * Useful for getting a property (for example a position) to
 * move from one value to another, in a smooth looking way.
 * To use, create a spring with appropriate constants
 * (e.g. new FloatSpring(100) is a reasonable value)
 * Then set spring position to the initial value, and update
 * each frame with target parameter as your desired value.
 * The position parameter will "snap to" the desired value.
 * @author goki
 */
public class FloatSpring {

	float position;
	
	float springK;
	
	float dampingK;
	
	float velocity;
	
	/**
	 * Make a spring with given spring constant and damping constant
	 * @param springK
	 * 		Spring constant, the higher this is the "tighter" the spring,
	 * 		and the more force it will exert for a given extension
	 * @param dampingK
	 * 		Damping constant, the higher this is the stronger the damping,
	 * 		and the more "soggy" the movement.
	 */
	public FloatSpring(float springK, float dampingK) {
		super();
		this.position = 0;
		this.springK = springK;
		this.dampingK = dampingK;
		this.velocity = 0;
	}

	/**
	 * Create a critically damped spring (or near to critically damped)
	 * This spring will quickly move to its target without overshooting
	 * @param springK
	 * 		The spring constant - the higher this is, the more quickly the spring
	 * 		will reach its target. A value of 100 gives a reasonable response in 
	 * 		about a second, a higher value gives a faster response.
	 */
	public FloatSpring(float springK) {
		this (springK, (float)(2 * Math.sqrt(springK)));		
	}
	
	/**
	 * Update the position of the spring. This updates the "position" as if
	 * there were a damped spring stretched between the current position and
	 * the target position. That is, the spring will tend to pull the position towards the target,
	 * and if the spring is damped the position will eventually settle onto the target.
	 * @param target
	 * 		The target towards which the spring is pulling the position
	 * @param time
	 * 		The elapsed time in seconds
	 */
	public void update(float target, float time) {
		
		//Set v to target - position, this is the required movement
		float v = position - target;
		
		//Multiply displacement by spring constant to get spring force,
		//then subtract damping force
		v = v * -springK - velocity * dampingK;
		
		//v is now a force, so assuming unit mass is is also acceleration.
		//multiply by elapsed time to get velocity change
		velocity += v * time;
		
		//If velocity isn't valid, zero it
		if (Float.isNaN(velocity) || Float.isInfinite(velocity)) {
			velocity = 0;
		}
		
		//Change the roll at the new velocity, for elapsed time
		position += velocity * time;
	}

	/**
	 * @return
	 * 		Damping constant, the higher this is the stronger the damping,
	 * 		and the more "soggy" the movement.
	 */
	public float getDampingK() {
		return dampingK;
	}

	/**
	 * @param dampingK
	 * 		Damping constant, the higher this is the stronger the damping,
	 * 		and the more "soggy" the movement.
	 */
	public void setDampingK(float dampingK) {
		this.dampingK = dampingK;
	}

	/**
	 * @return
	 * 		The current position of the simulated spring end point,
	 * 		changes as simulation is updated
	 */
	public float getPosition() {
		return position;
	}

	/**
	 * @param position
	 * 		A new position for simulated spring end point
	 */
	public void setPosition(float position) {
		this.position = position;
	}

	/**
	 * @return
	 * 		The spring constant - the higher this is, the more quickly the spring
	 * 		will reach its target
	 */
	public float getSpringK() {
		return springK;
	}

	/**
	 * @param springK
	 * 		The spring constant - the higher this is, the more quickly the spring
	 * 		will reach its target
	 */
	public void setSpringK(float springK) {
		this.springK = springK;
	}

	/**
	 * @return
	 * 		The current velocity of the position
	 */
	public float getVelocity() {
		return velocity;
	}

	/**
	 * @param velocity
	 * 		A new value for the current velocity of the position
	 */
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}
}
