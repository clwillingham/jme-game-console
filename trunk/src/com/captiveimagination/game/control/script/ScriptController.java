package com.captiveimagination.game.control.script;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;


import com.captiveimagination.game.script.Script;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;

/**
 * A jME Spatial Controller that runs a JSR 223 Script on update.
 * @author Doug Daniels
 * @version $Revision: 1136 $
 * $Id: ScriptController.java 1136 2007-08-18 02:21:47Z mhicks $
 * 
 */
public class ScriptController extends Controller {
    private IScriptController script;
    private Spatial mesh;
    private Bindings bindings  = new SimpleBindings();;
    private static final String UPDATE = "update";
    private Script scriptController;
    /**
     * This is the name of the object that can be accessed in the script.
     */
    private static final String    CONTROLLED_OBJECT_ID = "controlled";
    
    public ScriptController(String scriptFile, Spatial m) throws ScriptException {
        mesh = m;
        scriptController = new Script(scriptFile);
        //Put the Spatial into the script's global variables as variable "controlled"
        bindings.put(CONTROLLED_OBJECT_ID, mesh);
        scriptController.setBindings(bindings);
        // Script can only retrieve interface objects then can be
        // wrapped to jME interface.
        IScriptController controller = scriptController
                .getInterface(IScriptController.class);
        this.script = controller;
        
        Logger.getLogger("cigame").log(
                Level.INFO,
                "Creating ScriptController from script:\n"+
                scriptController.getScriptFilePath() + "\n" +
                "contents:\n" +
                scriptController.getScriptText()
                );
        
    }
    public ScriptController(IScriptController script, Spatial m){
        this.script = script;
        mesh = m;
        
    }

    @Override
    public void update(float time) {
        try {
            //Delegate the update to the script
            script.update(time);
        }
        catch(Exception e) {
        	Logger.getLogger("cigame").log(
                    Level.SEVERE,
                    "ERROR script controller. Removing script controller from Spatial:",
                    e);
            //Remove it 
            mesh.removeController(this);
        }
    }
    public void register(String key, Object val) {
        bindings.put(key, val);
        scriptController.setBindings(bindings);
        
    }
    /**
     * @param registeredObjects
     */
    public void registerAll(Bindings registeredObjects) {
        bindings.putAll(registeredObjects);
        scriptController.setBindings(bindings);
        
    }

}
