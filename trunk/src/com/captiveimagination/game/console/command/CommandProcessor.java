package com.captiveimagination.game.console.command;

import com.captiveimagination.game.console.*;

public interface CommandProcessor {
    public void execute(String command, GameConsole console);
    public void onModeActivate(GameConsole console);
}
