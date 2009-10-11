/**
 * 
 */
package com.captiveimagination.game.console.command;

import bsh.*;

import com.captiveimagination.game.console.*;

/**
 * @author Matthew D. Hicks
 *
 */
public class JavaCommandProcessor implements CommandProcessor {
	private Interpreter interpreter;
	
	public JavaCommandProcessor(GameConsole console) {
		interpreter = new Interpreter();
	}
	
	public void register(String namespace, Object object) throws EvalError {
		interpreter.set(namespace, object);
	}
	
	public void importPackage(String packageName) throws EvalError {
		interpreter.eval("import " + packageName + ";");
	}
	
	public void execute(String command, GameConsole console) {
		try {
			interpreter.eval(command);
			
		} catch(EvalError err) {
			console.logErr(err.getMessage());
		}
	}

    /* (non-Javadoc)
     * @see com.captiveimagination.game.console.command.CommandProcessor#onModeActivate(com.captiveimagination.game.console.GameConsole)
     */
    public void onModeActivate(GameConsole console) {
        console.setPrompt("java>");
        
    }
}
