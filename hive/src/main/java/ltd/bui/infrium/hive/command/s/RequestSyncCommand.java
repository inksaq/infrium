package ltd.bui.infrium.hive.command.s;

import ltd.bui.infrium.hive.Hive;
import ltd.bui.infrium.hive.command.Command;

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
