package com.infrium.api.util;

import lombok.Synchronized;

// easy TaskWaiter, start a task and wait for it to finish
// you can give it a maximum time to wait in case of timeout an exception is thrown
public class TaskWaiter {

  private volatile boolean finished;

  // initial flag if the task is finished
  public TaskWaiter(boolean b) {
    this.finished = b;
  }

  // init the object as task not finished
  public TaskWaiter() {
    this(false);
  }

  // start the task
  @Synchronized
  public final void start() {
    this.finished = false;
  }

  // finish the task
  @Synchronized
  public final void finish() {
    this.finished = true;
  }

  // wait for the task to finish with a timeout
  public final void await(long timeoutMillis) {
    long start = System.currentTimeMillis();
    while (!this.finished) {
      if (System.currentTimeMillis() - start > timeoutMillis) {
        throw new RuntimeException("Task took too long to finish");
      }
    }
  }

  // wait for the task to finish without a timeout - will wait forever
  public final void await() {
    while (!this.finished) {
      Thread.onSpinWait();
    }
  }
}
