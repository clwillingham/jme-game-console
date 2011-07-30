package test;

import java.io.FileNotFoundException;

import javax.script.*;

import com.captiveimagination.game.script.Script;


public class TestPnuts {

    /**
     * @param args
     * @throws ScriptException 
     */
    public static void main(String[] args) {
        try {
/*
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine pnutsEngine = manager.getEngineByName("pnuts");

        Compilable eng = (Compilable)pnutsEngine;
        CompiledScript scr = null;
        try {
            scr = eng.compile(new java.io.FileReader("scripts/pnut/HelloWorld.pnut"));
            scr.eval();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ScriptException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        ScriptEngine e = (ScriptEngine)eng;
        
        //set engine scope namespace
        Bindings n = new SimpleBindings();
        e.setBindings(n, ScriptContext.ENGINE_SCOPE);
        Invocable invk = (Invocable)scr.getEngine();
        //evaluate compiled script
         * 
         */
        Script s = new Script("scripts/pnut/HelloWorld.pnut");
        float time = 0f;
        float mesh = 0f;
        for (int i=0; i<3; i++) {
            time = i;    
            s.invoke("update", time, mesh);           
        }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
