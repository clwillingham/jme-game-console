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

package com.captiveimagination.game.hud;

import java.nio.FloatBuffer;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * A Quad with a gauge that fills around an arc of a circle not
 * greater than 180 degrees
 * @author shingoki
 */
public class Gauge180 extends Quad implements Gauge {
	private static final long serialVersionUID = 3583905755438577509L;

	protected float maxAngle;
	protected float startAngle;
	protected float value;
	protected boolean clockwise;
	protected boolean spinTexture;
	protected float size;

	/**
	 * Create a gauge.
	 * The center of the gauge (the point it "rotates" about to fill up) 
	 * is at the center of the Spatial 
	 * @param value
	 * 		The initial gauge value
	 * @param startAngle
	 * 		The angle the gauge starts at (measured clockwise from straight down for clockwise
	 * 		gauges, anticlockwise from straight down for anticlockwise gauges)
	 * @param maxAngle
	 * 		The maximum arc angle size (corresponds to value of 1.0f)
	 * 		The absolute value will be used, and capped to PI
	 * 		This is clockwise or anticlockwise according to "clockwise" property
	 * @param clockwise
	 * 		True to have gauge fill up from bottom, clockwise. False to have
	 * 		it fill up from bottom, counterclockwise.
	 * @param spinTexture
	 * 		True to spin the texture to display (the texture moves as value changes, the top of the gauge
	 * 		is the "fixed point" for the texture)
	 * 		False to clip the texture to display (the texture stays in place, but is
	 * 		clipped to a varying angle, the bottom of the texture is the "fixed point")
	 * @param size
	 * 		The size of the gauge (height of quad, width will be half this)
	 * @param t
	 * 		The texture to use for the gauge
	 */
	public Gauge180(
			String name, 
			float value, 
			float startAngle, 
			float maxAngle, 
			boolean clockwise,
			boolean spinTexture,
			float size, Texture t) {		
		super(name, size/2, size);
		this.maxAngle = maxAngle;
		this.startAngle = startAngle;
		this.clockwise = clockwise;
		this.spinTexture = spinTexture;
		this.size = size;
		TextureState textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		textureState.setEnabled(true);
		textureState.setTexture(t);
		setRenderState(textureState);
		maxAngle = FastMath.abs(maxAngle);
		if (maxAngle > FastMath.PI) maxAngle = FastMath.PI;
		if (clockwise) {
			rotateQuad(startAngle, size);
		} else {
			rotateQuad(FastMath.PI - startAngle, size);
		}
		setValue(value);
	}

	protected void rotateTexture(float angle) {
		FloatBuffer texBuf = BufferUtils.createVector2Buffer(4);
		texBuf.clear();
		float xm = FastMath.sin(angle) * 0.5f;
		float ym = FastMath.cos(angle) * 0.5f;
		texBuf.put( 0.5f - xm - ym  ).put( 0.5f -ym + xm );	//0
		texBuf.put( 0.5f + xm - ym ).put( 0.5f + ym + xm );	//1
		texBuf.put( 0.5f + xm ).put( 0.5f + ym );			//2
		texBuf.put( 0.5f - xm ).put( 0.5f - ym );			//3
	}

	protected void rotateQuad(float angle, float size) {
        TriMesh batch = this;
		batch.getVertexBuffer().clear();
		float xm = FastMath.sin(angle) * 0.5f * size;
		float ym = FastMath.cos(angle) * 0.5f * size;
		batch.getVertexBuffer().put( xm - ym  ).put( ym + xm ).put( 0 );	//0
		batch.getVertexBuffer().put( -xm - ym ).put( -ym + xm ).put( 0 );	//1
		batch.getVertexBuffer().put( -xm ).put( -ym ).put( 0 );				//2
		batch.getVertexBuffer().put( xm ).put(  ym ).put( 0 );				//3
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		//Work out rotation
		if (value < 0) value = 0;
		if (value > 1) value = 1;
		if (spinTexture) {
			if (!clockwise) value = -value;
			rotateTexture(FastMath.PI + value * maxAngle );
		} else {
			if (clockwise) {
				rotateQuad(FastMath.PI + value * maxAngle + startAngle, size);
				rotateTexture(FastMath.PI - value * maxAngle);
			} else {
				rotateQuad( - value * maxAngle - startAngle, size);
				rotateTexture(FastMath.PI  + value * maxAngle);
			}
		}
		
		//Remember new value
		this.value = value;
	}

}
