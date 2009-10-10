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

import java.util.List;

import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * Utility methods for walking a tree of spatials from a root,
 * and acting on each spatial found using a {@link SpatialAction}
 * @author goki
 */
public class SpatialWalker {


	/**
	 * Walk the tree of nodes/children under this spatial 
	 * (if it is a node - just this spatial otherwise)
	 * and apply the specified action to each one.
	 * The root is at level 0 in the tree.
	 * @param root
	 * 		The root spatial from which to start
	 * @param action
	 * 		The action to apply
	 */
	public static void actOnSpatialTree(Spatial root, SpatialAction action) {
		actOnSpatialTree(root, action, 0);
	}
	
	
	/**
	 * Walk the tree of nodes/children under this spatial 
	 * (if it is a node - just this spatial otherwise)
	 * and apply the specified action to each one
	 * @param spatial
	 * 		The spatial from which to start
	 * @param action
	 * 		The action to apply
	 * @param level
	 * 		The level reached in the tree 
	 */
	public static void actOnSpatialTree(Spatial spatial, SpatialAction action, int level) {
		
		//Do this actual node first
		action.actOnSpatial(spatial, level);
		
		//Do children next (if it is has them)
		if (spatial instanceof Node) {
			List<Spatial> children = ((Node)spatial).getChildren();
			if (children != null) {
				for (Spatial c : children) {
					actOnSpatialTree(c, action, level+1);
				}
			}
		}
	}
	
	/**
	 * Use {@link SpatialWalker.SpatialPrinter} to print the tree of spatials
	 * starting from this spatial
	 * @param spatial
	 * 		The root of the tree
	 */
	public static void printSpatialTree(Spatial spatial) {
		actOnSpatialTree(spatial, new SpatialPrinter());
	}
	
	/**
	 * Simple action, just prints the spatial with tab level from tree level
	 */
	public static class SpatialPrinter implements SpatialAction {
		public void actOnSpatial(Spatial spatial) {
			actOnSpatial(spatial, 0);
		}
		public void actOnSpatial(Spatial spatial, int level) {
			for (int i = 0; i < level; i++) System.out.print("\t");
			System.out.println(spatial);
		}
	}
	
}
