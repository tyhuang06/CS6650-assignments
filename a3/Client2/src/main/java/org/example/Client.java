package org.example;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;

public class Client {
  private static final int CALLS_PER_THREAD = 100;
  protected static final String SERVER_URL = "http://localhost:8080/javaServer_war_exploded";
  //protected static final String SERVER_URL = "http://ec2-54-188-53-71.us-west-2.compute.amazonaws.com:8080/javaServer_war";
  private static final String OUTPUT_FILE_PATH = "/Users/tyhuang/Downloads/output.csv";

  public Client() {
  }

  public static void main(String[] args) throws InterruptedException {
    // Starting variables
    int threadGroupSize = 10;
    int numThreadGroups = 30;
    long delay = 2;

    // Threads
    int maxThreads = numThreadGroups * threadGroupSize;
    int totalCalls = CALLS_PER_THREAD * maxThreads * 2;

    // Setup service and latch
    ExecutorService servicePool = Executors.newFixedThreadPool(maxThreads);
    CountDownLatch latch = new CountDownLatch(maxThreads);
    BlockingQueue<Statistics> queue = new LinkedBlockingQueue<>(100);

    System.out.println("Starting client with " + numThreadGroups + " thread groups of " + threadGroupSize + " threads each");

    // Start timing
    long startTime = System.currentTimeMillis();

    // Start output thread
    Thread outputThread = new Thread(new Output(OUTPUT_FILE_PATH, queue, latch));
    outputThread.start();

    // Run threads
    for (int i = 0; i < numThreadGroups; i++) {
      for (int j = 0; j < threadGroupSize; j++) {
        servicePool.execute(new AlbumThread(CALLS_PER_THREAD, SERVER_URL, latch, queue));
      }

      sleep(delay * 1000);
    }

    // Shutdown executor service
    latch.await();
    servicePool.shutdown();

    // End output thread
    outputThread.join();

    // End timing
    long endTime = System.currentTimeMillis();

    // Results
    double wallTime = (double) (endTime - startTime) / 1000;
    double throughput = (double) ((numThreadGroups * threadGroupSize) * 2000) / wallTime;
    System.out.println("Wall time: " + wallTime + " seconds");
    System.out.println("Throughput: " + throughput + " requests/second");

    // Calculate statistics
    ArrayList<Double> postTimes = new ArrayList<>();
    ArrayList<Double> getTimes = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new java.io.FileReader(OUTPUT_FILE_PATH))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        if (values[2].equals("POST")) {
          postTimes.add(Double.parseDouble(values[1]));
        } else if (values[2].equals("GET")) {
          getTimes.add(Double.parseDouble(values[1]));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("POST times:");
    printStats(postTimes);
    // System.out.println("GET times:");
    // printStats(getTimes);
  }

  private static void printStats(ArrayList<Double> times) {
    Collections.sort(times);

    double sum = 0;
    double min = times.get(0);
    double max = times.get(times.size() - 1);
    for (double time : times) {
      sum += time;
    }
    double mean = sum / times.size();
    double median = times.get(times.size() / 2);
    double percentile99 = times.get((int) (times.size() * 0.99));

    System.out.println("Min: " + min);
    System.out.println("Max: " + max);
    System.out.println("Mean: " + mean);
    System.out.println("Median: " + median);
    System.out.println("99th percentile: " + percentile99);
  }
}