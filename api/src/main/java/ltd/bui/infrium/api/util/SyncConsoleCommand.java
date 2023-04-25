package ltd.bui.infrium.api.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

// execute a command into the shell synchronously
// easy to use, just pass the command, and the callbacks in case on success and failure
public class SyncConsoleCommand {

  private final String command;
  private final Callback<String> successCallback;
  private final Callback<Exception> errorCallback;

  public SyncConsoleCommand(
      String command, Callback<String> onSuccess, Callback<Exception> onError) {
    this.command = command;
    this.successCallback = onSuccess;
    this.errorCallback = onError;
    this.run();
  }

  private void run() {
    try {
      String s;
      String[] split = this.command.split(" ");
      Process process = Runtime.getRuntime().exec(split);
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder buffer = new StringBuilder();
      while ((s = reader.readLine()) != null) {
        buffer.append(s);
        buffer.append("\n");
      }
      this.successCallback.call(buffer.toString());
      System.out.println(buffer.toString());
    } catch (Exception e) {
      this.errorCallback.call(e);
    }
  }

  public String getCommand() {
    return this.command;
  }
}
