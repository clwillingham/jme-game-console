package com.captiveimagination.game.script;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * 
 * @author <a href="mailto:Daniels.Douglas@gmail.com">Doug Daniels</a>
 * @version $Revision: 1136 $
 * $Id: Script.java 1136 2007-08-18 02:21:47Z mhicks $
 * $HeadURL: svn://captiveimagination.com/public/cigame/trunk/src/com/captiveimagination/game/script/Script.java $
 */
public class Script {
    private static final ScriptEngineManager scriptManager    = new ScriptEngineManager();

    private ScriptEngine                     engine;

    private String                           scriptFileName;

    private String                           suffix;
    
    private StringBuffer scriptContents = new StringBuffer();

    private CompiledScript                   scr              = null;

    private Invocable                        inv              = null;

    private Object                           evalReturnObject = null;

    /**
     * Indicates if the script has already been evaluated.
     */
    private boolean                          evaluated        = false;
    private Bindings n = new SimpleBindings();
    public Script(String scriptFile) {

        Compilable eng = null;
        scriptFileName = scriptFile;
        initEngine();
        try {
            /**
             * If script is compiliable we compile it.
             */
            if (engine instanceof Compilable) {
                // Compile script
                eng = (Compilable) engine;
                BufferedReader fr = new BufferedReader(new FileReader(scriptFileName));
                String line;
                while((line=fr.readLine()) != null) {
                    scriptContents.append(line+"\n");
                }
                scr = eng.compile(new FileReader(scriptFileName));
                engine = scr.getEngine();
                
            }
            //set engine scope namespace            
            engine.setBindings(n, ScriptContext.ENGINE_SCOPE);
            //evaluate();
        } catch (FileNotFoundException fnf) {
        	Logger.getLogger("cigame").log(
                    Level.SEVERE, 
                    "Script file not found: " +scriptFileName, 
                    fnf);
        } catch (ScriptException se) {
        	Logger.getLogger("cigame").log(
                    Level.SEVERE, 
                    "Script exception: " +scriptFileName, 
                    se);
        }
        catch (IOException e) {
        	Logger.getLogger("cigame").log(
                    Level.SEVERE, 
                    "Script read error: " +scriptFileName, 
                    e);
        }
        if(engine instanceof Invocable) {
            inv = (Invocable) engine;
        }

    }

    /**
     * Evaluate the script.
     * 
     * NOTE: This will revaluate the script even if it has already been
     * evaluated.
     * 
     * @return
     */
    public Object eval() throws ScriptException {
               
        try {
            evaluate();
            evaluated = true;
        } catch (ScriptException e) {
            // TODO Auto-generated catch block
        	Logger.getLogger("cigame").log(Level.SEVERE, 
                    "Error evaluating the script "  + this.scriptFileName, e);
            throw e;
        }
        return evalReturnObject;

    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        if (args.length < 2) {
            System.out
                    .println("Invalid parameters, Usage : java "
                            + ScriptEngine.class.getName()
                            + " <scriptFile> <function>");
            System.exit(1);
        }
        Script s = new Script(args[0]);
        String function = args[1];
        
        Object result = s.invoke(function);
        
        System.out.println("Script<" + args[0] + ">." + args[1]
                + "() result: \n" + result);
    }

    public Object invoke(String function) {
        
        return invoke(function, null);
    }
    public <T> T getInterface(Class<T> clasz) throws ScriptException {
        evaluate();
        return inv.getInterface(clasz);
    }
    
    public Object invoke(String function,  Object... args) {
        Object result = null;  
        try {
            evaluate();  
            
            result = inv.invokeFunction(function, args);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void initEngine() {
        String suffix = scriptFileName.substring(scriptFileName
                .lastIndexOf(".") + 1);
        engine = scriptManager.getEngineByExtension(suffix);
    }

    private Object evaluate() throws ScriptException {
        if (!evaluated) {
            // Evaluate compiled script
            if (scr != null) {
                evalReturnObject = scr.eval();
            }
            // evaluate interpreted script
            else {
                evalReturnObject = engine.eval(this.scriptFileName);
            }
            evaluated = true;
        }

        return evalReturnObject;

    }

    /**
     * @param n2
     */
    public void setBindings(Bindings n2) {
        //push more bindings into the script registry
        n.putAll(n2);
    }

    /**
     * @return
     */
    public String getScriptFilePath() {        
        return scriptFileName;
    }

    /**
     * @return
     */
    public String getScriptText() {
        return scriptContents.toString();
    }
}
