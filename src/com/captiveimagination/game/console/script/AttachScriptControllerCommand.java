package com.captiveimagination.game.console.script;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.captiveimagination.game.console.GameConsole;
import com.captiveimagination.game.console.command.CommandProcessor;
import com.captiveimagination.game.control.script.ScriptController;
import com.jme.scene.Spatial;

/**
 * 
 * @author <a href="mailto:Daniels.Douglas@gmail.com">Doug Daniels</a>
 * @version $Revision: 1133 $
 * $Id: AttachScriptControllerCommand.java 1133 2007-08-13 20:31:12Z mhicks $
 * $HeadURL: svn://captiveimagination.com/public/cigame/trunk/src/com/captiveimagination/game/console/script/AttachScriptControllerCommand.java $
 */
public class AttachScriptControllerCommand implements CommandProcessor {

    private GameConsole            console;

    private ScriptCommandProcessor scrCmdProcessor;

    private static final String    CMD                  = "addController";

    private static final String    CMD_REMOVE           = "removeController";

    // we expect 3 arguments
    private static final int       NUM_ADD_ARGS         = 3;

    private static final int       NUM_REMOVE_ARGS      = 2;

    private static final String    USAGE                = "USAGE: "
                                                                + CMD
                                                                + " <Object> <script.ext>";

    private static final String    USAGE_REMOVE         = "USAGE: "
                                                                + CMD_REMOVE
                                                                + " <Object>";



    /**
     * @param console
     * @param processor
     */
    public AttachScriptControllerCommand(GameConsole console,
            ScriptCommandProcessor processor) {
        this.console = console;
        scrCmdProcessor = processor;
        console.registerCommandProcessor(getCommandText(), this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.captiveimagination.game.console.command.CommandProcessor#execute(java.lang.String,
     *      com.captiveimagination.game.console.GameConsole)
     */
    public void execute(String command, GameConsole console) {
        // Split on spaces
        String args[] = command.split("\\s");
        // Removes all attached ScriptController's
        if (args[0].equals(CMD_REMOVE) && args.length == NUM_REMOVE_ARGS) {
            String object = args[1];
            Object controllee = scrCmdProcessor.getRegisteredObject(object);
            if (!(controllee instanceof Spatial)) {
                console.log("ERROR object not Spatial:" + USAGE_REMOVE);
            }
            else {
                // TODO allow parameter to only remove a certain script
                // controller given it's filename
                Spatial spatialBeingControlled = (Spatial) controllee;
                // Need to clone it so that we don't concurrently remove it
                // while iterating
                for (Object c : (ArrayList) spatialBeingControlled
                        .getControllers().clone()) {
                    // Only remove attached ScriptController's
                    if (c instanceof ScriptController)
                        spatialBeingControlled
                                .removeController((ScriptController) c);
                }
            }

        }
        else if (args[0].equals(CMD) && args.length == NUM_ADD_ARGS) {
            String object = args[1];
            String scriptFileLoc = args[2];
            Object controllee = scrCmdProcessor.getRegisteredObject(object);
            File scriptFile = new File(scriptFileLoc);
            if (!(controllee instanceof Spatial) || !scriptFile.exists()) {
                console
                        .log("ERROR object not Spatial or scriptfile doesn't exist:"
                                + USAGE);
            }
            else {
                try {

                    Spatial spatialBeingControlled = (Spatial) controllee;

                    // The Script Controller will bind the Spatial to ID "controller"
                    // so that scripts have access to Spatial
                    ScriptController sc = new ScriptController(scriptFileLoc,
                            spatialBeingControlled);
                    sc.registerAll(this.scrCmdProcessor.getRegisteredObjects());


                    
                    if (sc != null) {
                        spatialBeingControlled.addController(sc);
                    }
                    else {
                        console
                                .log("ERROR Script was not a valid Controller see documentation"
                                        + USAGE);
                    }
                }
                catch (javax.script.ScriptException e) {
                    console.log("ERROR eval script check LOG:" + USAGE);
                    Logger.getLogger("cigame").log(Level.SEVERE,
                            "ERROR eval script", e);
                }
            }
        }
        else {
            console.log("ERROR:" + USAGE);
        }
    }

    public String[] getCommands() {
        return new String[] { CMD, CMD_REMOVE };
    }

    public String getCommandText() {
        return CMD;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.captiveimagination.game.console.command.CommandProcessor#onModeActivate(com.captiveimagination.game.console.GameConsole)
     */
    public void onModeActivate(GameConsole console) {

    }
}
