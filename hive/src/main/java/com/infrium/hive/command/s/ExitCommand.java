package com.infrium.hive.command.s;

import com.infrium.hive.Hive;
import com.infrium.hive.command.Command;

public class ExitCommand extends Command {

  public ExitCommand() {
    super("exit");
  }

  @Override
  public void onCommand(String[] args) {
    getLogger().log("Good bye!");
    Hive.getInstance().stopCloud();
  }
}
