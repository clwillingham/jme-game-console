/**
 * 
 */
package com.captiveimagination.game.console.script;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.captiveimagination.game.console.GameConsole;
import com.captiveimagination.game.console.command.CommandProcessor;

/**
 * @author <a href="mailto:Daniels.Douglas@gmail.com">Doug Daniels</a>
 * @version $Revision: 1136 $
 * $Id: SetScriptLanguageCommand.java 1136 2007-08-18 02:21:47Z mhicks $
 * $HeadURL: svn://captiveimagination.com/public/cigame/trunk/src/com/captiveimagination/game/console/script/SetScriptLanguageCommand.java $
 *
 */
public class SetScriptLanguageCommand implements CommandProcessor{

    private GameConsole console;
    private ScriptCommandProcessor scrCmdProcessor;
    private static final String CMD = "lang";
    private static final String USAGE ="USAGE: " + CMD + " <language>" ;
    /**
     * @param console
     * @param processor
     */
    public SetScriptLanguageCommand(GameConsole console, ScriptCommandProcessor processor) {
        this.console = console;
        scrCmdProcessor = processor;
        console.registerCommandProcessor(getCommandText(), this);
    }

    /* (non-Javadoc)
     * @see com.captiveimagination.game.console.command.CommandProcessor#execute(java.lang.String, com.captiveimagination.game.console.GameConsole)
     */
    public void execute(String command, GameConsole console) {
        //Split on spaces
        String args[] = command.split("\\s");
        if(args.length!=2) {
            console.log("ERROR:" + USAGE);
            console.log(this.scrCmdProcessor.getAvailableLanguagesText());
            Logger.getLogger("cigame").log(Level.INFO,
                    this.scrCmdProcessor.getAvailableLanguagesText());
        }
        else  {
            String language = args[1];
            boolean success = scrCmdProcessor.setLanguage(language);
            
            if(!success) {
                console.log("ERROR failed to set:" + USAGE);
                console.log(this.scrCmdProcessor.getAvailableLanguagesText());
                Logger.getLogger("cigame").log(Level.INFO,
                        this.scrCmdProcessor.getAvailableLanguagesText());
            }
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
