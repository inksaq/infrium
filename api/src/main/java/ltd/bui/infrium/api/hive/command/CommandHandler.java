package ltd.bui.infrium.api.hive.command;


import io.lettuce.core.pubsub.RedisPubSubAdapter;
import lombok.Getter;
import lombok.NonNull;
import ltd.bui.infrium.api.database.InfriumDB;
import ltd.bui.infrium.api.hive.enums.CommandChannels;
import ltd.bui.infrium.api.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

  private final InfriumDB infriumDB;
  private final Map<String, CommandContainer> commandContainerMap = new HashMap<>();
  private final String serverName;

  public CommandHandler(InfriumDB db, String serverName) {
    this.infriumDB = db;
    this.serverName = serverName;
    infriumDB
        .getPubSubConnectionReceiver()
        .sync()
        .psubscribe(CommandChannels.REDIS_COMMAND_CHANNEL_PATTERN.getChannel());
    infriumDB
        .getPubSubConnectionReceiver()
        .addListener(
            new RedisPubSubAdapter<>() {
              @Override
              public void message(String pattern, String channel, String message) {
                // the pattern is the following
                // (CommandChannels.REDIS_COMMAND_CHANNEL_PATTERN.getChannel() + COMMAND_NAME
                // it will remove the
                // CommandChannels.REDIS_COMMAND_CHANNEL_PATTERN.getChannel().length() and i get che
                // command name
                var commandName =
                    channel.substring(
                        CommandChannels.REDIS_COMMAND_CHANNEL_PATTERN.getChannel().length() - 1);
                // passing every commandhandler, it will handle the command
                CommandHandler.this.receiveCommand(commandName, message);
              }
            });
  }

  private void receiveCommand(String command, String data) {
    var commandContainer = commandContainerMap.get(command);
    if (commandContainer != null) { // is the command registered ?
      var commandObject =
          Constants.get()
              .getGson()
              .fromJson(data, commandContainer.command); // parsing the data into a object
      // the object is a subclass of AbstractCommand
      if (commandObject.isTarget(serverName)) { // is this server the target of the command ?
        commandContainer.callback.run(
            commandObject); // if yes we can call execute the command's callback
      }
    }
  }

  public void publishCommand(@NonNull AbstractCommand command) { // publish a command
    this.infriumDB.publishJson(
        CommandChannels.REDIS_COMMAND_CHANNEL.getChannel() + command.getClass().getSimpleName(),
        command);
  }

  public void publishCommandAsync(@NonNull AbstractCommand command) { // publish a command async
    this.infriumDB.publishJsonAsync(
        CommandChannels.REDIS_COMMAND_CHANNEL.getChannel() + command.getClass().getSimpleName(),
        command);
  }

  /** Register a command */
  public void registerCommand(
      @NonNull Class<? extends AbstractCommand> commandClass, @NonNull ICommandCallback callback) {
    commandContainerMap.put(
        commandClass.getSimpleName(), new CommandContainer(commandClass, callback));
  }

  @Getter
  public static class CommandContainer {

    private final Class<? extends AbstractCommand> command;
    private final ICommandCallback callback;

    public CommandContainer(
        Class<? extends AbstractCommand> commandClass, ICommandCallback callback) {
      this.command = commandClass;
      this.callback = callback;
    }
  }
}
