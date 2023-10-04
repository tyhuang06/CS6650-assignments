public class Album {
  private String artist;
  private String title;
  private int year;

  public Album(String artist, String title, int year) {
    this.artist = artist;
    this.title = title;
    this.year = year;
  }

  public String getArtist() {
    return artist;
  }

  public String getTitle() {
    return title;
  }

  public int getYear() {
    return year;
  }
}
