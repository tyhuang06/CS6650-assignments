import com.google.gson.Gson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

public class DbService {
  private MongoClient mongoClient;
  private MongoDatabase database;
  private MongoCollection<Document> albumsCollection;
  private Gson gson;

  public DbService() {
    this.mongoClient = MongoClients.create("mongodb://localhost:27017");
    this.database = this.mongoClient.getDatabase("CS6650");
    this.albumsCollection = this.database.getCollection("albums");
    this.gson = new Gson();
  }

  public Album getAlbumById(String id) {
    Document doc = this.albumsCollection.find(new Document("_id", new ObjectId(id))).first();
    Album album = this.gson.fromJson(doc.toJson(), Album.class);

    return album;
  }

  public String postAlbum(Album album) {
    Document doc = new Document("artist", album.getArtist())
      .append("title", album.getTitle())
      .append("year", album.getYear())
      .append("image", album.getImage());

    this.albumsCollection.insertOne(doc);

    return doc.getObjectId("_id").toString();
  }
}
