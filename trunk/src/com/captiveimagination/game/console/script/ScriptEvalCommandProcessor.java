/**
 * 
 */
package com.captiveimagination.game.console.script;

import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.xml.crypto.NoSuchMechanismException;


import com.captiveimagination.game.console.GameConsole;
import com.captiveimagination.game.console.command.CommandProcessor;
import com.captiveimagination.game.script.Script;

/**
 * @author <a href="mailto:Daniels.Douglas@gmail.com">Doug Daniels</a>
 * @version $Revision: 1136 $
 * $Id: ScriptEvalCommandProcessor.java 1136 2007-08-18 02:21:47Z mhicks $
 * $HeadURL: svn://captiveimagination.com/public/cigame/trunk/src/com/captiveimagination/game/console/script/ScriptEvalCommandProcessor.java $
 */
public class ScriptEvalCommandProcessor implements CommandProcessor {
    private static final String                         DEFAULT_LANGUAGE   = "Pnuts";

    private static final ScriptEngineManager            scriptManager      = new ScriptEngineManager();

    private static HashMap<String, ScriptEngineFactory> availableLanguages = new HashMap<String, ScriptEngineFactory>();

    // Initialize the available languages
    static {
        for (ScriptEngineFactory f : scriptManager.getEngineFactories()) {
            availableLanguages.put(f.getLanguageName(), f);
        }

    }

    private ScriptEngineFactory                         curScriptFactory;

    private ScriptEngine                                engine;

    private GameConsole gConsole;

    private Bindings n = new SimpleBindings();
      
    /**
     * Command processor that will execute an arbitrary script. Or evaluate a
     * script on the fly.
     * 
     * @param console
     */
    public ScriptEvalCommandProcessor(GameConsole console) {
        gConsole = console;

        if(availableLanguages.containsKey(DEFAULT_LANGUAGE)) {
            setLanguage(DEFAULT_LANGUAGE);

        }
        else {
            if(availableLanguages.size()>0) {                
                for(String language : availableLanguages.keySet()) {
                    //Just give me the first one that works.                    
                    if(setLanguage(language)) {
                        break;
                    }
                }
            }
            //We've got a problem!
            else {
                throw new NoSuchMechanismException("Script engines cannot be found " +
                        "make sure you have the script engine libraries on your classpath!!");
            }
        }
    }

    public boolean setLanguage(String language) {
        if(availableLanguages.containsKey(language)) {          
            try {
                //Attempt to retrieve the engine factory.
                //Will fail if script implmentation libraries aren't on
                //classpath.
                //See: https://scripting.dev.java.net/
                engine = availableLanguages.get(language).getScriptEngine();                
                    
            }
            catch(Error e) {
                gConsole.log("Attempt to retrieve script implementation factory for: " +
                             language + " has failed. You must have both the script engine "+
                             "and script implementation on classpath see: https://scripting.dev.java.net/");
                return false;
            }
            //Set the factory to the correct value, now that we know it is valid.
            curScriptFactory = availableLanguages.get(language);
            
            //Pass in the key, value pair bindings
            engine.setBindings(n, ScriptContext.ENGINE_SCOPE);
            return true;
        }
        //Language could not be found
        return false;
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.captiveimagination.game.console.command.CommandProcessor#execute(java.lang.String,
     *      com.captiveimagination.game.console.GameConsole)
     */
    public void execute(String command, GameConsole console) {
        
        try {
            engine.eval(command);
        }
        catch (ScriptException e) {
        	Logger.getLogger("cigame").log(Level.SEVERE,
                    "Error while evaluating script command:\n"+command+"\n",
                    e);
            console.log("ERROR: " + e.getMessage());
            
        }
        

    }
    public Script runScriptFile(String file) throws ScriptException {
        Script sc = new Script(file);
        sc.setBindings(n);
        sc.eval(); 
        return sc;
    }
    public void register(String name, Object val) {
        n.put(name, val);
    }

    /**
     * @return
     */
    public String getLanguage() {
        return curScriptFactory.getLanguageName();
    }
    
    public ScriptEngineFactory getScriptFactory() {
        return curScriptFactory;
    }

    /* (non-Javadoc)
     * @see com.captiveimagination.game.console.command.CommandProcessor#onModeActivate(com.captiveimagination.game.console.GameConsole)
     */
    public void onModeActivate(GameConsole console) {
        //Do nothing.
        
    }

    /**
     * @return
     */
    public Object[] getLanguages() {
        Object[] languages = this.availableLanguages.keySet().toArray();
        return languages;
    }

    public Object getRegisteredObject(String object) {
        // TODO Auto-generated method stub
        return n.get(object);
    }

    /**
     * @return
     */
    public Bindings getRegisteredObjects() {
        // TODO Auto-generated method stub
        return n;
    }

}
