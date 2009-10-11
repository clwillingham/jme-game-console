package test;

import bsh.EvalError;

import com.captiveimagination.game.console.GameConsole;
import com.captiveimagination.game.console.command.BasicCommandProcessor;
import com.captiveimagination.game.console.command.JavaCommandProcessor;
import com.jme.app.BaseGame;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;
import com.jmex.game.state.GameStateManager;

public class TestBaseGameConsole extends BaseGame {
	
	Camera cam;
	private int width, height, depth, freq;
	private boolean fullscreen;
	protected Timer timer;
	private InputHandler input;
	public Node scene;
	public static GameConsole console;
	
	public static void main(String[] args)
	{
		TestBaseGameConsole app = new TestBaseGameConsole();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	@Override
	protected void cleanup() {
		// TODO Auto-generated method stub
		GameStateManager.getInstance().cleanup();
	}

	@Override
	protected void initGame() {
		// TODO Auto-generated method stub
		scene = new Node("Scene");
		GameStateManager.create();
		Box b = new Box("Box", new Vector3f(), 5, 5, 5);
		b.setLocalTranslation(0, 0, 10);
		scene.attachChild(b);
		GameStateManager.getInstance().attachChild(console);
		console.setActive(true);
		
	}

	@Override
	protected void initSystem() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		//store the properties information
		width = this.settings.getWidth();
		height = this.settings.getHeight();
		depth = this.settings.getDepth();
		freq = this.settings.getFrequency();
		fullscreen = this.settings.isFullscreen();
		try {
			display = DisplaySystem.getDisplaySystem(this.getNewSettings().getRenderer());
			display.createWindow(width, height, depth, freq, fullscreen);
 
			cam = display.getRenderer().createCamera(width, height);
		} catch (JmeException e) {
			e.printStackTrace();
			System.exit(1);
		}
 
		//set the background to black
		display.getRenderer().setBackgroundColor(ColorRGBA.black);
		
		//initialize the camera
		cam.setFrustumPerspective(45.0f, (float)width / (float)height, 1, 1000);
		Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
		Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
		// Move our camera to a correct place and orientation.
		cam.setFrame(loc, left, up, dir);
		/** Signal that we've changed our camera's location/frustum. */
		cam.update();
		cam.setLocation(new Vector3f(0, 1, -20));
		/** Get a high resolution timer for FPS updates. */
                timer = Timer.getTimer();
         
		display.getRenderer().setCamera(cam);
		display.setTitle("Levitank");
		input = new FirstPersonHandler(cam,30,1);
		
		console = new GameConsole(KeyInput.KEY_GRAVE, 5, true);
		
		//BasicCommandProcessor processor1 = new BasicCommandProcessor();
		//processor1.registerCommand(new TestConsole());
		JavaCommandProcessor processor2 = new JavaCommandProcessor(console);
		try {
			processor2.register("scene", scene);
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			console.log("Error: " + e.getMessage());
			e.printStackTrace();
		}
		
		//console.registerCommandProcessor("command", processor1);
		console.registerCommandProcessor("java", processor2);
		
		KeyBindingManager.getKeyBindingManager().set(
	              "exit",
	              KeyInput.KEY_ESCAPE);
	}

	@Override
	protected void reinit() {
		// TODO Auto-generated method stub
		display.recreateWindow(width, height, depth, freq, fullscreen);
	}

	@Override
	protected void render(float interpolation) {
		// TODO Auto-generated method stub
		timer.update();
		GameStateManager.getInstance().render(timer.getTimePerFrame());
		display.getRenderer().draw(scene);
		
	}

	@Override
	protected void update(float interpolation) {
		// TODO Auto-generated method stub
		timer.update();
		scene.updateGeometricState(interpolation, true);
		GameStateManager.getInstance().update(timer.getTimePerFrame());
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit")) {
		   finished = true;
		}
	}

}
