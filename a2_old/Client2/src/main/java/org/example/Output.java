package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Output implements Runnable {
  private BufferedWriter writer;
  private BlockingQueue<Statistics> queue;
  private CountDownLatch latch;

  public Output(String outputFilePath, BlockingQueue<Statistics> queue, CountDownLatch latch) {
    this.queue = queue;
    this.latch = latch;

    try {
      this.writer = new BufferedWriter(new FileWriter(outputFilePath));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private synchronized void writeToFile(Statistics stats) {
    try {
      this.writer.write(stats.toString());
      this.writer.newLine();
      this.writer.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (this.latch.getCount() > 0) {
      try {
        Statistics stats = this.queue.take();
        writeToFile(stats);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    try {
      this.writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
