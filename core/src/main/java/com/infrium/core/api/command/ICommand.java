package com.infrium.core.api.command;

public interface ICommand {

    void onCommand(CommandArgs args) throws Exception;
}
