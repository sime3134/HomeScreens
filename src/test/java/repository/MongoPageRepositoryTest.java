package repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import model.Page;
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
        Page page = new Page("/test");
        page.addData("temperature", 25);
        page.addData("humidity", 40);
        repository.insert(page);
    }

    @AfterEach
    void teardown() {
        repository.deleteAll();
    }

    @Test
    void findByPathInvalid() {
        Page page = repository.findByPath("/notexist234");
        assertNull(page);
    }

    @Test
    void findByPathValid() {
        Page page = repository.findByPath("/test");
        assertNotNull(page);
    }

    @Test
    void insert() {
        Page page = new Page("/test2");
        page.addData("temperature", 25);
        page.addData("humidity", 40);

        repository.insert(page);

        Page fetchedPage = repository.findByPath("/test2");
        assertEquals(page.getData(), fetchedPage.getData().get("data"));
    }

    @Test
    void insertDuplicate() {
        Page page = new Page("/test");
        page.addData("temperature", 25);
        page.addData("humidity", 40);

        assertDoesNotThrow(() -> repository.insert(page));
    }

    @Test
    void deleteByPathInvalid() {
        Page page = repository.findByPath("/notexist234");
        assertNull(page);

        assertDoesNotThrow(() -> repository.deleteByPath("/notexist234"));

        page = repository.findByPath("/notexist234");

        assertNull(page);
    }

    @Test
    void deleteByPathValid() {
        repository.deleteByPath("/test");
        Page page = repository.findByPath("/test");
        assertNull(page);
    }

}