import com.google.gson.Gson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

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

  public String getAlbumById(String id) {
    Document doc = this.albumsCollection.find(new Document("_id", new ObjectId(id))).first();

    if (doc == null) {
      return null;
    }

    return doc.toJson();
  }

  public String postAlbum(byte[] image, Album album) {
    Document doc = new Document("artist", album.getArtist())
      .append("title", album.getTitle())
      .append("year", album.getYear())
      .append("image", image);

    this.albumsCollection.insertOne(doc);

    String json = gson.toJson(new ImageData(doc.getObjectId("_id").toString(), Integer.toString(image.length)));

    return json;
  }

  public String postLike(String id, int value) {
    Document doc = this.albumsCollection.find(new Document("_id", new ObjectId(id))).first();

    if (doc == null) {
      return null;
    }

    Document likeDoc = new Document("$inc", new Document("likes", value));
    this.albumsCollection.updateOne(doc, likeDoc, new UpdateOptions().upsert(true));

    return doc.toJson();
  }
}
