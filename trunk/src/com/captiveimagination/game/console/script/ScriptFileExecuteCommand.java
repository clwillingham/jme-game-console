/**
 * 
 */
package com.captiveimagination.game.console.script;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptException;


import com.captiveimagination.game.console.GameConsole;
import com.captiveimagination.game.console.command.CommandProcessor;
import com.captiveimagination.game.script.Script;

/**
 * @author <a href="mailto:Daniels.Douglas@gmail.com">Doug Daniels</a>
 * @version $Revision: 1136 $
 * $Id: ScriptFileExecuteCommand.java 1136 2007-08-18 02:21:47Z mhicks $
 * $HeadURL: svn://captiveimagination.com/public/cigame/trunk/src/com/captiveimagination/game/console/script/ScriptFileExecuteCommand.java $
 *
 */
public class ScriptFileExecuteCommand implements CommandProcessor{


    private GameConsole console;
    private static final String CMD = "file";
    private static final String USAGE ="USAGE: " + CMD + " <file.ext>" ;
    private ScriptCommandProcessor scrCmdProcessor;
    /**
     * @param console
     * @param processor
     */
    public ScriptFileExecuteCommand(GameConsole console, ScriptCommandProcessor scp) {
        this.console = console;
        scrCmdProcessor = scp;
        console.registerCommandProcessor(getCommandText(), this);
        //scrCmdProcessor = processor;
    }

    /* (non-Javadoc)
     * @see com.captiveimagination.game.console.command.CommandProcessor#execute(java.lang.String, com.captiveimagination.game.console.GameConsole)
     */
    public void execute(String command, GameConsole console) {
        //Split on spaces
        String args[] = command.split("\\s");
        if(args.length!=2) {
            console.log("ERROR:" + USAGE);
        }
        String file = args[1];
        
        try {
            scrCmdProcessor.runScriptFile(file);
        }
        catch (ScriptException e) {
           console.log("ERROR evaluating script " + file);
           Logger.getLogger("cigame").log(Level.SEVERE, 
                   "Console couldn't evaluate the script "  + file,
                   e);
        }
        
    }
    
    public String getCommandText() {
        return CMD;
    }

    /* (non-Javadoc)
     * @see com.captiveimagination.game.console.command.CommandProcessor#onModeActivate(com.captiveimagination.game.console.GameConsole)
     */
    public void onModeActivate(GameConsole console) {
        
    }


}
