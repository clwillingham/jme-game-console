package test;

import java.net.URL;
import java.util.logging.Level;

import javax.script.ScriptException;

import jmetest.renderer.TestEnvMap;

import com.captiveimagination.game.control.script.ScriptController;
import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class TestScriptController extends SimpleGame {
        private TriMesh mesh;
        //private static String rotateScript = "scripts/groovy/spinController.groovy"; 
        private static String rotateScript = "resources/scripts/js/spinController.js";
        /**
         * Entry point for the test,
         * 
         * @param args
         */
        public static void main(String[] args) {
            TestScriptController app = new TestScriptController();
            //app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
            app.start();
        }

        /**
         * @see com.jme.app.SimpleGame#initGame()
         */
        protected void simpleInitGame() {
            display.setTitle("Testing Script");

            lightState.setEnabled(false);
            
            CullState cull = display.getRenderer().createCullState();
            //cull.setCullHint(CullState.Face.Back);  
            cull.setCullFace(CullState.Face.Back);
            // we set a cull state to hide the back of our batches, "proving" they
            // are camera facing.
            rootNode.setRenderState(cull);
            
            initBoxes();
            initScripts();
            /*
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */
        }
        
        private void initBoxes() {
            mesh = new Box("box",
                   new Vector3f(0,0,0),
                   1,1,1);
            TextureState ts = display.getRenderer().createTextureState();
            Texture t0 = TextureManager.loadTexture(
                    TestEnvMap.class.getClassLoader().getResource(
                    "jmetest/data/images/Monkey.jpg"),
                    Texture.MinificationFilter.Trilinear,
                    Texture.MagnificationFilter.Bilinear);
            t0.setWrap(Texture.WrapMode.Repeat);
            ts.setTexture(t0);
            
            mesh.setRenderState(ts);
            mesh.updateRenderState();
            
            rootNode.attachChild(mesh);
        }
        
        private void initScripts() {
            URL scriptURL = TestScriptController.class.getClassLoader().getResource(rotateScript);
            try {
                ScriptController sc = new ScriptController(scriptURL.getPath(), mesh);
                mesh.addController(sc);
            }
            catch (ScriptException e) {
            }
            //Add the script to the mesh
            //mesh.addController(sc);
        }
        
}
