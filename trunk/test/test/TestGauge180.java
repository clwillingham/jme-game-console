package test;

import java.util.ArrayList;
import java.util.List;

import com.captiveimagination.game.console.*;
import com.captiveimagination.game.console.command.*;
import com.captiveimagination.game.hud.Gauge180;
import com.captiveimagination.game.hud.GaugeController;
import com.captiveimagination.game.hud.NamedValuesBean;
import com.captiveimagination.game.hud.SpringyGaugeController;
import com.captiveimagination.game.util.TextureLoader;
import com.jme.bounding.*;
import com.jme.image.*;
import com.jme.input.*;
import com.jme.math.*;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.system.DisplaySystem;
import com.jme.util.*;
import com.jmetest.physics.*;
import com.jmex.game.*;
import com.jmex.game.state.*;

/**
 * @author Matthew D. Hicks
 */
public class TestGauge180 {
	public String exit() {
		return "I would exit, but I don't know how.";
	}
	
	public void quit() {
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		StandardGame game = new StandardGame("Test Gauge180");
		game.getSettings().clear();
		game.start();
		
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
		Node hudNode = new Node("hud");
		
		//Create alpha state to blend using alpha
		BlendState as = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        as.setBlendEnabled(true);
        //as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        //as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as.setSourceFunction(SourceFunction.SourceAlpha);
        as.setSourceFunction(SourceFunction.OneMinusSourceAlpha);
        as.setTestEnabled(false);
        as.setEnabled(true);
       
        //Create disabled light state, hud nodes will be displayed
        //as unlit colour
        LightState ls = display.getRenderer().createLightState();
        ls.setEnabled(false);
        
        hudNode.setRenderState(as);
        hudNode.setRenderState(ls);
        
        //Node uses ortho rendering
        hudNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        
        //Never cull hud items
        hudNode.setCullHint(Spatial.CullHint.Never);

        NamedValuesBean simpleBean = new SuperBean();
        
        //Attach speed gauge
		Texture speedTexture = TextureLoader.loadUncompressedTexture("resources/speedGauge.png");
		Gauge180 speedGauge = new Gauge180(
				"SpeedGauge", 0, FastMath.PI / 6f, FastMath.PI * 2f/3f, 
				true, false,
				128, speedTexture);
		hudNode.attachChild(speedGauge);
		speedGauge.setLocalTranslation(new Vector3f(display.getWidth() / 2,
				display.getHeight() / 2, 0));
		speedGauge.addController(new SpringyGaugeController(speedGauge, simpleBean, "speed", 1f));

		//Attach health gauge
		Texture healthTexture = TextureLoader.loadUncompressedTexture("resources/healthGauge.png");
		Gauge180 healthGauge = new Gauge180(
				"HealthGauge", 0, FastMath.PI / 6f, FastMath.PI * 2f/3f, 
				false, false, 128, healthTexture);
		hudNode.attachChild(healthGauge);
		healthGauge.setLocalTranslation(new Vector3f(display.getWidth() / 2,
				display.getHeight() / 2, 0));
		healthGauge.addController(new SpringyGaugeController(healthGauge, simpleBean, "health", 1f));
        
		
    	BasicGameState debug = new BasicGameState("Basic");
    	debug.getRootNode().attachChild(hudNode);
    	debug.getRootNode().updateRenderState();
    	
    	GameStateManager.getInstance().attachChild(debug);
    	debug.setActive(true);
		
		GameConsole console = new GameConsole(KeyInput.KEY_GRAVE, 5, true);
    	/*int rows = 5;
    	GameConsole console = new GameConsole(KeyInput.KEY_GRAVE, rows, true, 
				new float[]{8, 8, 8, 8}, 
				new Vector2f(100, -16), 
				new Vector2f(100, GameConsole.calculateHeight(rows) + 16), 
				DisplaySystem.getDisplaySystem().getWidth() - 200);*/

		
//		BasicCommandProcessor processor = new BasicCommandProcessor();
//		processor.registerCommand(new TestConsole());
		
		JavaCommandProcessor processor = new JavaCommandProcessor(console);
		processor.register("console", console);
		processor.register("game", game);
		processor.register("state", debug);
		processor.importPackage("com.jme.math.Vector3f");
		processor.register("simpleBean", simpleBean);
		console.registerCommandProcessor("java", processor);
		
		GameStateManager.getInstance().attachChild(console);
		console.setActive(true);
	}
}