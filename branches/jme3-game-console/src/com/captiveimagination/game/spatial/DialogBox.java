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

import com.captiveimagination.game.spatial.Grid;

import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * A grid with 3x3 quads, textured so as to have nice corners.
 * That is, the uv coords correspond to a 32x32 image which has
 * a border of 1 blank pixel, with the remaining 30x30 image being
 * made up of 9 10x10 sections, which are the corners, edges and
 * middle of the dialog box. The image corners are then always drawn at 
 * the same scale in the corners of the dialog. The top and bottom
 * edges on the image are stretched along the x axis to match the
 * top and bottom of the dialog, similarly for the left and right edges.
 * The center is stretched in both axes.
 * 
 * The image used may also be a multiple of 32x32 pixels (or some other
 * size, but this will not look as neat).
 * 
 * @author shingoki
 */
public class DialogBox extends Grid {
	private static final long serialVersionUID = -2906219587285439289L;
	
	float imageBaseSize;
	float tileSize;
	float borderSize;

	/**
	 * Create a dialog box, assumed to use a 
	 * 32x32 image with 1 pixel border, and 10x10 tiles
	 * @param name
	 * 		The name of the box
	 * @param t
	 * 		The texture
	 */
	public DialogBox(
			String name, 
			Texture t) {
		this(name, 32, 10, 1, t);
	}

	/**
	 * Create a dialog box
	 * @param name
	 * 		The name of the box
	 * @param imageBaseSize
	 * 		The size of the texture used for tiles, the border size
	 * 		is set to imageBaseSize/32, and the tile size to 
	 * 		(imageBaseSize/32) * 10. So it is a scaled version of
	 * 		a 32x32 image with 1 pixel border, and 10x10 tiles
	 * @param t
	 * 		The texture
	 */
	public DialogBox(
			String name, 
			float imageBaseSize,
			Texture t) {
		//Assume a border size of 1, hence tile size is image size - 2, divided into 3 tiles
		this(name, imageBaseSize, (imageBaseSize/32) * 10, imageBaseSize/32, t);
	}
	
	/**
	 * Create a dialog box
	 * @param name
	 * 		The name of the box
	 * @param imageBaseSize
	 * 		The size of the image used for the texture
	 * @param tileSize
	 * 		The size of the tiles in the texture (corner, edge and middle tiles)
	 * @param borderSize
	 * 		The size of the border around the texture
	 * @param t
	 * 		The texture
	 */
	public DialogBox(
			String name, 
			float imageBaseSize,
			float tileSize,
			float borderSize,
			Texture t) {
		//Make a 3x3 grid, which is sized to have 1 unit per tile pixel on each quad
		super(name, tileSize * 3, tileSize * 3, 3, 3, false);
		
		this.imageBaseSize = imageBaseSize;
		this.tileSize = tileSize;
		this.borderSize = borderSize;
		
		//Move the texture coords appropriately
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				//moveUV(x, y, (borderSize + tileSize * x)/imageBaseSize, (borderSize + tileSize * y)/imageBaseSize);
			}
		}
		
		//Set texture
		TextureState textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		textureState.setEnabled(true);
		textureState.setTexture(t);
		setRenderState(textureState);
		
		//set initial dimension to 3x3 tiles
		setDimension(tileSize * 3, tileSize * 3);
	}

	/**
	 * Scale the dialog box
	 * @param width
	 * 		The box width
	 * @param height
	 * 		The box height
	 */
	public void setDimension(float width, float height) {
		
		//Smallest dimension is 0
		if (width < 0) width = 0;
		if (height < 0) height = 0;
		
		//Work out the tile size to use - if either width or height
		//are too small to fit the tile size twice (for corners) then
		//reduce the tile size we use to half that dimension
		float smallestDimension = Math.min(width, height);
		float useTileSize = tileSize;
		if (smallestDimension < tileSize * 2) {
			useTileSize = smallestDimension/2;
		}
		
		//We just need to set the second and third columns and rows
		float widthMinusTile = width - useTileSize;
		float heightMinusTile = height - useTileSize;

		//Move the vertex coords appropriately
		for (int xIndex = 0; xIndex < 4; xIndex++) {
			for (int yIndex = 0; yIndex < 4; yIndex++) {
				
				float x = 0;
				if (xIndex == 0) {
					x = 0;
				} else if (xIndex == 1) {
					x = useTileSize;
				} else if (xIndex == 2) {
					x = widthMinusTile;
				} else {
					x = width;
				}

				float y = 0;
				if (yIndex == 0) {
					y = 0;
				} else if (yIndex == 1) {
					y = useTileSize;
				} else if (yIndex == 2) {
					y = heightMinusTile;
				} else {
					y = height;
				}

				movePoint(xIndex, yIndex, x, y);
			}
		}
		
	}


}
