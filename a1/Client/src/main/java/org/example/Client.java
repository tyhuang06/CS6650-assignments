package org.example;

import java.util.concurrent.CountDownLatch;

import io.swagger.client.api.DefaultApi;

public class Client {
  //protected static final String SERVER_URL = "http://localhost:8080/javaServer_war_exploded";
  protected static final String SERVER_URL = "http://ec2-34-222-136-164.us-west-2.compute.amazonaws.com:8080/javaServer_war";
  //protected static final String SERVER_URL = "http://ec2-34-222-136-164.us-west-2.compute.amazonaws.com:8080";
  //protected static final String SERVER_URL = "http://localhost:8080";


  public Client() {
  }

  public static void main(String[] args) throws InterruptedException {
    // Starting variables
    int threadGroupSize = 10;
    int numThreadGroups = 30;
    long delay = 2;
    CountDownLatch latch = new CountDownLatch(numThreadGroups * threadGroupSize);
    DefaultApi api = new DefaultApi();
    api.getApiClient().setBasePath(SERVER_URL);
    System.out.println("Starting client with " + numThreadGroups + " thread groups of " + threadGroupSize + " threads each");

    // Start timing
    long startTime = System.currentTimeMillis();

    // Run threads
    for (int i = 0; i < numThreadGroups; i++) {
      runThreadGroup(threadGroupSize, latch, api);
      Thread.sleep(delay * 1000);
    }
    latch.await();

    // End timing
    long endTime = System.currentTimeMillis();

    // Results
    double wallTime = (double) (endTime - startTime) / 1000;
    double throughput = (double) ((numThreadGroups * threadGroupSize) * 2000) / wallTime;
    System.out.println("Wall time: " + wallTime + " seconds");
    System.out.println("Throughput: " + throughput + " requests/second");
  }

  private static void runThreadGroup(int threadGroupSize, CountDownLatch latch, DefaultApi api) {
    // Create threads
    Thread[] threads = new Thread[threadGroupSize];
    for (int i = 0; i < threadGroupSize; i++) {
      threads[i] = new Thread(new AlbumThread(100, latch, api));
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