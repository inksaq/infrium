package com.infrium.hive.command.s;

import com.infrium.hive.Hive;
import com.infrium.hive.command.Command;

public class RequestSyncCommand extends Command {
  public RequestSyncCommand() {
    super("sync");
  }

  @Override
  public void onCommand(String[] args) {
    getLogger().log("Sync requested...");
    Hive.getInstance().requestSync();
  }
}
