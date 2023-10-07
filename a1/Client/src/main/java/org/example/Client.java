package org.example;

import java.util.concurrent.CountDownLatch;

public class Client {
  protected static final String SERVER_URL = "http://localhost:8080/javaServer_war_exploded";

  public Client() {
  }

  public static void main(String[] args) throws InterruptedException {
    // Starting variables
    int threadGroupSize = 10;
    int numThreadGroups = 10;
    long delay = 2;
    CountDownLatch latch = new CountDownLatch(numThreadGroups * threadGroupSize);
    System.out.println("Starting client with " + numThreadGroups + " thread groups of " + threadGroupSize + " threads each");

    // Start timing
    long startTime = System.currentTimeMillis();

    // Run threads
    for (int i = 0; i < numThreadGroups; i++) {
      runThreadGroup(threadGroupSize, latch);
      Thread.sleep(delay * 1000);
    }
    latch.await();

    // End timing
    long endTime = System.currentTimeMillis();

    // Results
    double wallTime = (double) (endTime - startTime) / 1000;
    double throughput = (double) (numThreadGroups * threadGroupSize) / wallTime;
    System.out.println("Wall time: " + wallTime + " seconds");
    System.out.println("Throughput: " + throughput + " requests/second");
  }

  private static void runThreadGroup(int threadGroupSize, CountDownLatch latch) {
    // Create threads
    Thread[] threads = new Thread[threadGroupSize];
    for (int i = 0; i < threadGroupSize; i++) {
      threads[i] = new Thread(new AlbumThread(100, SERVER_URL, latch));
    }

    // Start threads
    for (int i = 0; i < threadGroupSize; i++) {
      threads[i].start();
    }

    // Wait for threads to finish
    for (int i = 0; i < threadGroupSize; i++) {
      try {
        threads[i].join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}