package org.example;

public class Statistics {
  private long startTime;
  private long endTime;
  private String requestType;
  private int httpStatus;

  public Statistics(long startTime, long endTime, String requestType, int httpStatus) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.requestType = requestType;
    this.httpStatus = httpStatus;
  }

  @Override
  public String toString() {
    return String.format("%d,%f,%s,%d", this.startTime, (double) (this.endTime - this.startTime), this.requestType, this.httpStatus);
  }
}
