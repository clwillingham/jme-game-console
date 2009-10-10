/**
 * 
 */
package test;



import com.captiveimagination.game.console.*;
import com.captiveimagination.game.console.command.*;
import com.captiveimagination.game.console.script.ScriptCommandProcessor;
import com.jme.bounding.*;
import com.jme.image.*;
import com.jme.input.*;
import com.jme.math.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.util.*;
import com.jmex.game.*;
import com.jmex.game.state.*;

/**
 * @author ddaniels
 *
 */
public class TestScriptConsole {
    public String exit() {
        return "I would exit, but I don't know how.";
    }
    
    public void quit() {
        System.exit(0);
    }
    
    public static void main(String[] args) throws Exception {
        StandardGame game = new StandardGame("Test Script Console");
        game.getSettings().clear();
        game.start();
        
        TextureState ts = game.getDisplay().getRenderer().createTextureState();
        Texture t = TextureManager.loadTexture(TestScriptConsole.class.getClassLoader().getResource("jmetest/data/texture/cloud_land.jpg"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        t.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(t);
        Box box = new Box("Box", new Vector3f(), 10.0f, 10.0f, 10.0f);
        box.setModelBound(new BoundingBox());
        box.updateModelBound();
        box.setRenderState(ts);
        BasicGameState debug = new BasicGameState("Basic");
        debug.getRootNode().attachChild(box);
        debug.getRootNode().updateRenderState();
        
        GameStateManager.getInstance().attachChild(debug);
        debug.setActive(true);
       
        int rows = 5;
        GameConsole console = new GameConsole(KeyInput.KEY_GRAVE, rows, true);
//      GameConsole console = new GameConsole(KeyInput.KEY_GRAVE, rows, true, 
//              new float[]{8, 8, 8, 8}, 
//              new Vector2f(100, -16), 
//              new Vector2f(100, GameConsole.calculateHeight(rows) + 16), 
//              DisplaySystem.getDisplaySystem().getWidth() - 200);

        
        BasicCommandProcessor processor1 = new BasicCommandProcessor();
        processor1.registerCommand(new TestScriptConsole());
        
        //ScriptCommandProcessor automatically registers itself to the "script" mode        
        ScriptCommandProcessor scriptProcessor = new ScriptCommandProcessor(console);
        scriptProcessor.register("console", console);
        scriptProcessor.register("game", game);
        scriptProcessor.register("state", debug);
        //scriptProcessor.importPackage("com.jme.math.Vector3f");
        scriptProcessor.register("box", box);
        
        
        JavaCommandProcessor processor2 = new JavaCommandProcessor(console);
        processor2.register("console", console);
        
        processor2.register("game", game);
        processor2.register("state", debug);
        processor2.importPackage("com.jme.math.Vector3f");
        processor2.register("box", box);
        box.setLocalScale(0.5f);
        console.registerCommandProcessor("command", processor1);
        console.registerCommandProcessor("java", processor2);
        
        GameStateManager.getInstance().attachChild(console);
        console.setActive(true);
    }
}
