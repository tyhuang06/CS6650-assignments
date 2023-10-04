public class ImageData {
  private String albumID;
  private String imageSize;

  public ImageData(String albumID, String imageSize) {
    this.albumID = albumID;
    this.imageSize = imageSize;
  }

  public String getAlbumID() {
    return albumID;
  }

  public String getImageSize() {
    return imageSize;
  }
}
