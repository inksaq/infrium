package com.infrium.hive.shell;

import java.time.Instant;
import org.jline.reader.impl.history.DefaultHistory;

public class CommandHistory extends DefaultHistory {

  private static boolean isComment(String line) {
    return line.startsWith("#");
  }

  @Override
  public void add(Instant time, String line) {
    if (isComment(line)) {
      return;
    }
    super.add(time, line);
  }
}
