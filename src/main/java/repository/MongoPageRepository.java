package repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Page;
import org.bson.Document;

public class MongoPageRepository implements PageRepository {

    private static final String COLLECTION_NAME = "PageData";

    private final MongoDatabase database;

    public MongoPageRepository(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public Page findByPath(String path) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        Document doc = collection.find(new Document("path", path)).first();
        if (doc == null) {
            return null;
        }
        return new Page(doc.getString("path"), doc);
    }

    @Override
    public void deleteByPath(String path) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        collection.deleteOne(new Document("path", path));
    }

    @Override
    public void deleteAll() {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        collection.deleteMany(new Document());
    }

    @Override
    public void insert(Page page) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        Document doc = new Document()
                .append("path", page.getPath())
                .append("data", page.getData());
        collection.insertOne(doc);
    }
}
