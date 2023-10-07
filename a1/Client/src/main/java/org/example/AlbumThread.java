package org.example;

import java.io.File;
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

  public AlbumThread(int requests, String serverURL, CountDownLatch latch) {
    this.requests = requests;
    this.serverURL = serverURL;
    this.latch = latch;
    this.api = new DefaultApi();
    this.api.getApiClient().setBasePath(serverURL);
  }

  @Override
  public void run() {
    long startTime;
    long endTime;

    // POST /album
    for (int i = 0; i < this.requests; i++) {
      sendApiRequest("POST");
    }

    // GET /album/{albumID}
    for (int i = 0; i < this.requests; i++) {
      sendApiRequest("GET");
    }

    this.latch.countDown();
  }

  private void sendApiRequest(String type) {
    // try five times before counting request as failed
    final int MAX_ATTEMPTS = 5;
    int attempts = 0;

    while (attempts < MAX_ATTEMPTS) {
      try {
        if (type.equals("GET")) {
          AlbumInfo result = this.api.getAlbumByKey("albumID");
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
