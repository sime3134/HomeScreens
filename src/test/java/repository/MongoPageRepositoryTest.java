package repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import model.PageData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilities.EnvironmentVars;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MongoPageRepositoryTest {
    private static PageRepository repository;

    @BeforeAll
    static void beforeAll() throws IOException {
        EnvironmentVars.load();
        MongoClient mongoClient = MongoClients.create(EnvironmentVars.getValue("MONGO_PATH"));
        MongoDatabase database = mongoClient.getDatabase("HomeScreens");
        repository = new MongoPageRepository(database);
    }

    @BeforeEach
    void setup() {
        PageData pageData = new PageData("/test");
        pageData.addData("temperature", 25);
        pageData.addData("humidity", 40);
        repository.insert(pageData);
    }

    @AfterEach
    void teardown() {
        repository.deleteAll();
    }

    @Test
    void findByPathInvalid() {
        PageData pageData = repository.findByPath("/notexist234");
        assertNull(pageData);
    }

    @Test
    void findByPathValid() {
        PageData pageData = repository.findByPath("/test");
        assertNotNull(pageData);
    }

    @Test
    void insert() {
        PageData pageData = new PageData("/test2");
        pageData.addData("temperature", 25);
        pageData.addData("humidity", 40);

        repository.insert(pageData);

        PageData fetchedPageData = repository.findByPath("/test2");
        assertEquals(pageData.getData(), fetchedPageData.getData().get("data"));
    }

    @Test
    void insertDuplicate() {
        PageData pageData = new PageData("/test");
        pageData.addData("temperature", 25);
        pageData.addData("humidity", 40);

        assertDoesNotThrow(() -> repository.insert(pageData));
    }

    @Test
    void deleteByPathInvalid() {
        PageData pageData = repository.findByPath("/notexist234");
        assertNull(pageData);

        assertDoesNotThrow(() -> repository.deleteByPath("/notexist234"));

        pageData = repository.findByPath("/notexist234");

        assertNull(pageData);
    }

    @Test
    void deleteByPathValid() {
        repository.deleteByPath("/test");
        PageData pageData = repository.findByPath("/test");
        assertNull(pageData);
    }

}