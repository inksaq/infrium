package ltd.bui.infrium.hive.command;

import org.hydev.logger.HyLogger;

public abstract class Command {

  private static final HyLogger logger = new HyLogger("Commands");
  private final String commandName;
  private final String[] aliases;

  public Command(String commandName, String... aliases) {
    this.commandName = commandName;
    this.aliases = aliases;
  }

  public abstract void onCommand(String[] args);

  public String getCommandName() {
    return commandName;
  }

  public String[] getAliases() {
    return aliases;
  }

  protected final synchronized HyLogger getLogger() {
    return logger;
  }
}
