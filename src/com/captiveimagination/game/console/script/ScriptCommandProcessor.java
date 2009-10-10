/**
 * 
 */
package com.captiveimagination.game.console.script;

import java.util.HashMap;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;


import com.captiveimagination.game.console.GameConsole;
import com.captiveimagination.game.console.command.CommandProcessor;
import com.captiveimagination.game.script.Script;
import com.jme.input.KeyInput;

/**
 * @author <a href="mailto:Daniels.Douglas@gmail.com">Doug Daniels</a>
 * @version $Revision: 685 $
 * $Id: ScriptCommandProcessor.java 685 2007-04-03 23:46:57Z dougnukem $
 * $HeadURL: svn://captiveimagination.com/public/cigame/trunk/src/com/captiveimagination/game/console/script/ScriptCommandProcessor.java $
 */
public class ScriptCommandProcessor implements CommandProcessor {
    
    public static final int DEFAULT_ROWS = 5;
    
    public static final String SCRIPT_MODE_CMD = "script";
    
    private GameConsole gameConsole ;
    
    private ScriptEvalCommandProcessor sp;
    
    private HashMap<String, CommandProcessor> highLevelCommands = new HashMap<String, CommandProcessor>();
 
    public ScriptCommandProcessor(GameConsole console) {
        gameConsole = console;
        sp = new ScriptEvalCommandProcessor(console);
        //User types "mode script" to enter scripting mode.
        gameConsole.registerCommandProcessor(SCRIPT_MODE_CMD, this);
        //Use the default high level commands.
        setHighLevelCommands(getDefaultHighLevelCommands(console));
    }
    
    
    /**
     * Creates the default high level commands.
     * 
     * TODO: Populate this using some kind of external data.
     * 
     * @return
     */
    private HashMap<String, CommandProcessor> getDefaultHighLevelCommands(GameConsole console) {
        HashMap<String, CommandProcessor> defaultCommands = new HashMap<String, CommandProcessor>();
        
        SetScriptLanguageCommand setScriptCmd = new SetScriptLanguageCommand(console, this);
        ScriptFileExecuteCommand scriptExecCmd = new ScriptFileExecuteCommand(console, this);
        AttachScriptControllerCommand scriptAttachController = new AttachScriptControllerCommand(console, this);
        
        //COMMAND create setLang <language> command
        defaultCommands.put(setScriptCmd.getCommandText(), setScriptCmd);

        //COMMAND create file <script.ext> command
        defaultCommands.put(scriptExecCmd.getCommandText(), scriptExecCmd);
        
        //COMMAND addController <Spatial> <scriptFile.ext> command
        //Has a list of commands for different functions, like adding and removing script controllers.
        for(String cmd : scriptAttachController.getCommands()) {
            defaultCommands.put(cmd, scriptAttachController);
        }
        
        return defaultCommands;
    }


    private GameConsole getConsole() {
        return gameConsole;
    }

    /**
     * Adds the following CommandProcessors to be handled by this CommandProcessor.
     * @param newHighLevelCommands
     */
    public void setHighLevelCommands(HashMap<String, CommandProcessor> newHighLevelCommands) {
        this.highLevelCommands.putAll(newHighLevelCommands);
    }
    /**
     * Clears the high level commands.
     *
     */
    public void clearHighLevelCommands() {
        this.highLevelCommands.clear();
    }

    /* (non-Javadoc)
     * @see com.captiveimagination.game.console.command.CommandProcessor#execute(java.lang.String, com.captiveimagination.game.console.GameConsole)
     */
    public void execute(String command, GameConsole console) {
        //Pull off the first argument split by the spaces
        String firstArg = command.split("\\s")[0];
        if(isHighLevelCommand(firstArg)) {
            processHighLevelCommand(firstArg, command, console);
        }
        else {
            //Delegate to the script evaluator
            sp.execute(command, console);
        }
        setPrompt(console);
    }
    /**
     * @param console
     */
    private void setPrompt(GameConsole console) {
        String curLang = sp.getLanguage();
        console.setPrompt(curLang+">");
        //sp.getScriptFactory().getEngineName();
        //console.log("<Script Language: "+curLang+">");
    }


    /**
     * Checks if the executing command should be handled
     * by a high level registered CommandProcessor.
     * 
     * @param cmd
     * @return
     */
    private boolean isHighLevelCommand(String cmd) {
        return highLevelCommands.containsKey(cmd);
    }
    /**
     * Handles a high level registered command by delegating to a registered 
     * CommandProcessor.
     * 
     * @param cmd
     */
    private void processHighLevelCommand(String cmd, String params, GameConsole console) {
       highLevelCommands.get(cmd).execute(params, console); 
    }


    /**
     * Allows the Script Command processor to bind objects from your
     * application to the script engine so that scripts can access them.
     * 
     * @param string
     * @param console
     */
    public void register(String string, Object objVal) {
        sp.register(string, objVal);
        
    }


    /**
     * Sets the prompt to display the scripting language on activate.
     * @see com.captiveimagination.game.console.command.CommandProcessor#onModeActivate(com.captiveimagination.game.console.GameConsole)
     */
    public void onModeActivate(GameConsole console) {
        this.setPrompt(console);        
    }


    /**
     * @param language
     */
    public boolean setLanguage(String language) {
        return sp.setLanguage(language);
        
    }


    /**
     * @param file
     * @throws ScriptException 
     */
    public void runScriptFile(String file) throws ScriptException {
        sp.runScriptFile(file);
        
    }


    /**
     * @return
     */
    public String getAvailableLanguagesText() {
        String langs = "";
        Object [] languages = this.sp.getLanguages();
        for(Object language : languages) {
            langs += language.toString() + " ";
        }
        return langs;
    }


    public Object getRegisteredObject(String object) {
        // TODO Auto-generated method stub
        return sp.getRegisteredObject(object);
    }


    public Script getScript(String scriptFile) throws ScriptException {
        // TODO Auto-generated method stub
        return sp.runScriptFile(scriptFile);
    }


    /**
     * @return
     */
    public Bindings getRegisteredObjects() {
        // TODO Auto-generated method stub
        return sp.getRegisteredObjects();
    }
}
