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
package com.captiveimagination.game.spatial;

import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;

/**
 * Clear all render states (check list, there might be new ones that aren't covered!) on spatials.
 * @author goki
 */
public class SpatialRenderStateClearer implements SpatialAction {

	private static SpatialRenderStateClearer instance = new SpatialRenderStateClearer();
	
	/**
	 * @return
	 * 		A single shared instance of the clearer, since it has no state
	 */
	public static SpatialRenderStateClearer getInstance() {
		return instance;
	}
	
	private SpatialRenderStateClearer() {
	}
	
	@SuppressWarnings("deprecation")
	public void actOnSpatial(Spatial spatial) {
		spatial.clearRenderState(RenderState.RS_BLEND);
		//spatial.clearRenderState(RenderState.RS_ATTRIBUTE);
		spatial.clearRenderState(RenderState.RS_CLIP);
		spatial.clearRenderState(RenderState.RS_COLORMASK_STATE);
		spatial.clearRenderState(RenderState.RS_CULL);
		//spatial.clearRenderState(RenderState.RS_DITHER);
		spatial.clearRenderState(RenderState.RS_FOG);
		spatial.clearRenderState(RenderState.RS_FRAGMENT_PROGRAM);
		spatial.clearRenderState(RenderState.RS_GLSL_SHADER_OBJECTS);
		spatial.clearRenderState(RenderState.RS_LIGHT);
		spatial.clearRenderState(RenderState.RS_MATERIAL);
		spatial.clearRenderState(RenderState.RS_SHADE);
		spatial.clearRenderState(RenderState.RS_STENCIL);
		spatial.clearRenderState(RenderState.RS_TEXTURE);
		spatial.clearRenderState(RenderState.RS_VERTEX_PROGRAM);
		spatial.clearRenderState(RenderState.RS_WIREFRAME);
		spatial.clearRenderState(RenderState.RS_ZBUFFER);
	}

	public void actOnSpatial(Spatial spatial, int level) {
		actOnSpatial(spatial);
	}

}
