package com.infrium.hive;

import com.infrium.hive.shell.CommandHandler;

public class Main {

  public static void main(String[] args) throws Exception {
    Hive.getInstance();
    new CommandHandler()
        .run(); // start the command handler once everything is loaded
  }
}
