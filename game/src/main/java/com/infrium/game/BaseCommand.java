package com.infrium.game;


import com.infrium.core.api.command.Command;
import com.infrium.core.api.command.CommandArgs;
import com.infrium.core.api.command.ICommand;

public class BaseCommand implements ICommand {

    public BaseCommand() {
        Settlements.getInstance().getCommandFramework().registerCommands(this);
    }

    @Override
    @Command(name = "")
    public void onCommand(CommandArgs args) throws Exception {

    }
}
