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

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;

/**
 * Only acts on TriMesh spatials, when they are found they are 
 * removed from their parent, and an AreaClodMesh based on the
 * TriMesh is attached back on to the parent. 
 */
public class SpatialClodinator implements SpatialAction {

	float trisPerPixel;
	
	/**
	 * Create a clodinator with default of 0.05f trisPerPixel
	 */
	public SpatialClodinator() {
		this(0.05f);
	}
	
	/**
	 * Create a clodinator
	 * @param trisPerPixel
	 * 		trisPerPixel to set on the created clod meshes
	 */
	public SpatialClodinator(float trisPerPixel) {
		this.trisPerPixel = trisPerPixel;
	}

	/**
	 * If the spatial is a trimesh, remove it from its parent,
	 * and attach an AreaClodMesh based on the mesh back on to the parent.
	 */
	public void actOnSpatial(Spatial spatial) {

		if (spatial instanceof TriMesh) {
			
			Node parent = spatial.getParent();
			
			TriMesh mesh = (TriMesh)spatial;
			
			AreaClodMesh clodMesh = new AreaClodMesh(mesh.getName(), mesh, null);
			clodMesh.setTrisPerPixel(trisPerPixel);
			parent.detachChild(spatial);
			parent.attachChild(clodMesh);
			System.out.println("Clodded TriMesh: " + spatial);
		}
	}

	public void actOnSpatial(Spatial spatial, int level) {
		actOnSpatial(spatial);
	}

}
