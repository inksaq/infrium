package ltd.bui.infrium.hive.shell;

import ltd.bui.infrium.hive.Hive;
import ltd.bui.infrium.hive.command.Command;
import ltd.bui.infrium.hive.command.s.*;
import ltd.bui.infrium.api.hive.enums.ServerType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hydev.logger.HyLogger;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class CommandHandler implements Runnable {

  private final Terminal terminal;
  private final CommandHistory commandHistory;
  private final LineReader lineReader;
  private final HyLogger logger = new HyLogger("Commands");
  private final List<Command> commandList = new ArrayList<>();

  public CommandHandler() throws IOException {

    this.terminal = TerminalBuilder.builder().system(true).build();
    ServersCompleter serversCompleter = new ServersCompleter();
    List<String> serverTypeList = new ArrayList<>();
    for (ServerType type : ServerType.values()) {
      serverTypeList.add(type.name());
    }

    // Tab Completer
    Completer hostCompleter =
        new ArgumentCompleter(
            new StringsCompleter("host"),
            new StringsCompleter(serverTypeList),
            NullCompleter.INSTANCE);
    Completer stopAllCompleter =
        new ArgumentCompleter(
            new StringsCompleter("stopall"),
            new StringsCompleter(serverTypeList),
            NullCompleter.INSTANCE);
    Completer stopCompleter =
        new ArgumentCompleter(
            new StringsCompleter("stop"), serversCompleter, NullCompleter.INSTANCE);
    Completer pingCompleter =
        new ArgumentCompleter(
            new StringsCompleter("ping"), serversCompleter, NullCompleter.INSTANCE);
    Completer infoCompleter =
        new ArgumentCompleter(
            new StringsCompleter("info"),
            new StringsCompleter(serverTypeList),
            NullCompleter.INSTANCE);

    Completer united =
        new AggregateCompleter(
            new ArgumentCompleter(new StringsCompleter("help"), NullCompleter.INSTANCE),
            new ArgumentCompleter(new StringsCompleter("exit"), NullCompleter.INSTANCE),
            new ArgumentCompleter(new StringsCompleter("sync"), NullCompleter.INSTANCE),
            hostCompleter,
            stopAllCompleter,
            stopCompleter,
            infoCompleter,
            pingCompleter);

    this.commandHistory = new CommandHistory();
    this.lineReader =
        LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(united)
            .history(commandHistory)
            .build();

    // Registering commands.
    commandList.add(new HelpCommand());
    commandList.add(new HostCommand());
    commandList.add(new AddDevCommand());
    commandList.add(new InfoCommand());
    commandList.add(new StopCommand());
    commandList.add(new StopAllCommand());
    commandList.add(new ExitCommand());
    commandList.add(new PingCommand());
    commandList.add(new RequestSyncCommand());
    logger.log("Command Handler started.");
  }

  @Override
  public void run() {
    final String prompt = "InfriumCloud> ";
    while (Hive.getInstance().isRunning()) {
      String line;
      try {
        line = lineReader.readLine(prompt);
        processCommand(line);
      } catch (Exception e) { // exception caused by lineReader - should panic this time
        logger.log("Bye!");
        break;
      }
    }
  }

  private void processCommand(String line) {
    if (!line.isEmpty()) {
      try {
        String[] split = line.split("\\s+");
        if (!split[0].startsWith("#")) {
          boolean executed = false;
          for (Command c : commandList) {
            if (c.getCommandName().equalsIgnoreCase(split[0])) {
              c.onCommand(split(split));
              executed = true;
              break; // Break the loop if the command was executed.
            }
          }
          if (!executed) {
            logger.log("Command not found, type 'help' for command list.");
          }
        }
      } catch (Exception e) { // safe executor - avoid strange crashes from the commands
        e.printStackTrace();
      }
    }
  }

  public String[] split(String[] s) {
    return Arrays.copyOfRange(s, 1, s.length);
  }
}
