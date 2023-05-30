package utilities;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseManager {

    private DatabaseManager() {
    }
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final Object lock = new Object();

    public static MongoDatabase getDatabase() {
        synchronized (lock) {
            if (database == null) {
                System.out.println(EnvironmentVars.getValue("MONGO_PATH"));
                mongoClient = MongoClients.create(EnvironmentVars.getValue("MONGO_PATH"));
                database = mongoClient.getDatabase("HomeScreens");
            }
        }
        return database;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

