package com.captiveimagination.game.console;

import com.captiveimagination.game.console.command.BasicCommandProcessor;
import com.captiveimagination.game.console.command.JavaCommandProcessor;

public class SimpleGameConsole extends GameConsole {
	
	
	public BasicCommandProcessor commandProcessor = new BasicCommandProcessor();
	public JavaCommandProcessor javaProcessor;

	public SimpleGameConsole(int key, int rows, boolean echo) {
		super(key, rows, echo);
		
		
		javaProcessor = new JavaCommandProcessor(this);
		
		
		// TODO Auto-generated constructor stub
	}

}
