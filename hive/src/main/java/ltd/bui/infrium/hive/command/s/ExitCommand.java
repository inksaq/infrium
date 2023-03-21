package ltd.bui.infrium.hive.command.s;

import ltd.bui.infrium.hive.Hive;
import ltd.bui.infrium.hive.command.Command;

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
