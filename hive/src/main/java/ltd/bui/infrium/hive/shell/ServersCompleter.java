package ltd.bui.infrium.hive.shell;

import ltd.bui.infrium.hive.Hive;
import ltd.bui.infrium.api.hive.servers.Server;
import java.util.List;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedString;

public class ServersCompleter implements Completer {

  @Override
  public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
    for (Server server : Hive.getInstance().getRepository().getServers()) {
      // this thing should suggest the servers names - don't know how it works don't ask plz
      candidates.add(
          new Candidate(
              AttributedString.stripAnsi(server.getName()),
              server.getName(),
              null,
              null,
              null,
              null,
              true));
    }
  }
}
