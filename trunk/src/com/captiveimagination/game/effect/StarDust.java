package com.captiveimagination.game.effect;

import java.io.IOException;
import java.util.Random;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Point;
import com.jme.scene.state.*;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * StarDust
 * 
 * @author Zen, Matthew D. Hicks, thzero
 */
public class StarDust extends Node {
	private static final long serialVersionUID = 1L;
	private static final int BLOCKSIZE = 100;
	
	private int blockSize;
	private Point[][][] points;
	private int oldSecX;
	private int oldSecY;
	private int oldSecZ;

	public StarDust(String name, int numStars, int blockSize, boolean randomizeSize) {
		super(name);

		this.blockSize = blockSize;
		points = new Point[3][3][3];

		setIsCollidable(false);

		// A star field
		//
		// in this first edition, just use the standard 'point' class
		// but in future would like to have a custom drawn one - where intensity
		// is related to distance?
		Random r = new Random();

		Vector3f[] vertexes = new Vector3f[numStars];
		for (int x = 0; x < numStars; ++x)
			vertexes[x] = new Vector3f((r.nextFloat()) * blockSize, (r.nextFloat()) * blockSize, (r.nextFloat())
							* blockSize);

		// all dust particles are white
		MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();

		ms.setEmissive(new ColorRGBA(1.0f, 1.0f, 1.0f, 0.5f));
		ms.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 0.5f));
		ms.setEnabled(true);

		BlendState as = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
		as.setBlendEnabled(true);
		//as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		//as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as.setSourceFunction(SourceFunction.SourceAlpha);
		as.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
		as.setTestEnabled(false);
		as.setEnabled(true);

		for (int k = 0; k < 3; ++k) {
			for (int j = 0; j < 3; ++j) {
				for (int i = 0; i < 3; ++i) {
					points[i][j][k] = new Point("stardust " + i + "" + j + "" + k, vertexes, null, null, null);
					if (randomizeSize) {
						points[i][j][k].setPointSize((float)Math.random() * 1.0f);
					}
					points[i][j][k].setLocalTranslation(new Vector3f((i - 1) * blockSize, (j - 1) * blockSize,
									(k - 1) * blockSize));
					points[i][j][k].setModelBound(new BoundingBox());
					points[i][j][k].updateModelBound();
					attachChild(points[i][j][k]);
				}
			}
		}

		// We don't want the light to affect our dust
		updateWorldBound();

		setRenderState(ms);
		setRenderState(as);
	}

	// ensure the viewer is surrounded by stars!
	public void update(Vector3f viewer) {
		// note: funny things happen when scaling things about the origin,
		// so for our purposes we compensate. (we could have used -0.5..0.5)
		// what we want is: -1000..0 -> -1
		//                  0..1000  -> 0
		//                  1000..2000 -> 1
		int secX = (int)((viewer.x / blockSize) + ((viewer.x > 0) ? 0 : -1));
		int secY = (int)((viewer.y / blockSize) + ((viewer.y > 0) ? 0 : -1));
		int secZ = (int)((viewer.z / blockSize) + ((viewer.z > 0) ? 0 : -1));

		// reduce garbage collection...
		if ((secX != oldSecX) || (secY != oldSecY) || (secZ != oldSecZ)) {
			getLocalTranslation().set(secX * blockSize, secY * blockSize, secZ * blockSize);
			oldSecX = secX;
			oldSecY = secY;
			oldSecZ = secZ;
		}
	}

	public void write(JMEExporter e) throws IOException {
		super.write(e);

		OutputCapsule cap = e.getCapsule(this);
		cap.write(blockSize, "blockSize", BLOCKSIZE);
	}

	@SuppressWarnings("unchecked")
	public void read(JMEImporter e) throws IOException {
		super.read(e);

		InputCapsule cap = e.getCapsule(this);
		blockSize = cap.readInt("blockSize", BLOCKSIZE);
	}
}
