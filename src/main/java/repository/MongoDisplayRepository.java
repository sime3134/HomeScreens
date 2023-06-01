package repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Display;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDisplayRepository implements DisplayRepository {

    private static final String COLLECTION_NAME = "DisplayData";

    private final MongoDatabase database;

    public MongoDisplayRepository(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public Display findById(String displayId) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        Document doc = collection.find(new Document("displayId", displayId)).first();
        if (doc == null) {
            return null;
        }
        return new Display(doc.getString("displayId"), doc.getString("name"));
    }

    @Override
    public void deleteById(String displayId) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        collection.deleteOne(new Document("displayId", displayId));
    }

    @Override
    public void insert(Display display) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        Document doc = new Document()
                .append("displayId", display.displayId())
                .append("name", display.name());
        collection.insertOne(doc);
    }

    @Override
    public void deleteAll() {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        collection.deleteMany(new Document());
    }

    @Override
    public List<Display> findAll() {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        return collection.find().map(doc -> new Display(doc.getString("displayId"), doc.getString("name"))).into(new ArrayList<>());
    }
}
