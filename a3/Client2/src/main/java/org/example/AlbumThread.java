package org.example;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;

public class AlbumThread implements Runnable {
  private int requests;
  private String serverURL;
  private CountDownLatch latch;
  private DefaultApi api;
  private BlockingQueue<Statistics> queue;

  public AlbumThread(int requests, String serverURL, CountDownLatch latch, BlockingQueue queue) {
    this.requests = requests;
    this.serverURL = serverURL;
    this.latch = latch;
    this.queue = queue;
    this.api = new DefaultApi();
    this.api.getApiClient().setBasePath(serverURL);
  }

  @Override
  public void run() {
    // POST /album
    for (int i = 0; i < this.requests; i++) {
      trackTime("POST");
    }

    // GET /album/{albumID}
    for (int i = 0; i < this.requests; i++) {
      trackTime("GET");
    }

    this.latch.countDown();
  }

  private void trackTime(String type) {
    long startTime;
    long endTime;

    startTime = System.currentTimeMillis();
    sendApiRequest(type);
    endTime = System.currentTimeMillis();

    try {
      this.queue.put(new Statistics(startTime, endTime, type, 200));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void sendApiRequest(String type) {
    // try five times before counting request as failed
    final int MAX_ATTEMPTS = 5;
    int attempts = 0;

    while (attempts < MAX_ATTEMPTS) {
      try {
        if (type.equals("GET")) {
          AlbumInfo result = this.api.getAlbumByKey("65440ab77500cd0d7f1b3cba");
        } else if (type.equals("POST")) {
          File image = new File("src/main/java/nmtb.png");
          AlbumsProfile album = new AlbumsProfile().artist("Sex Pistols").title("Never Mind The Bollocks!").year("1977");
          ImageMetaData result = this.api.newAlbum(image, album);
        }
        break;
      } catch (Exception e) {
        attempts++;
      }
    }

    if (attempts == MAX_ATTEMPTS) {
      System.out.println("Request failed");
    }

    return;
  }
}
