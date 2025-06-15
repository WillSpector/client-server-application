package data;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class CollectionManagerTest {

    private static File tempFile;
    private CollectionManager manager;

    @BeforeAll
    static void setupOnce() throws IOException {
        tempFile = Files.createTempFile("musicband", ".json").toFile();
        tempFile.deleteOnExit();
    }

    @BeforeEach
    void setup() {
        manager = new CollectionManager(tempFile.getAbsolutePath());
    }


    @Test
    void testRemoveByKey_nonExistingKey() {
        boolean removed = manager.removeByKey(999);
        assertFalse(removed);
    }

    @Test
    void testSetFileNameAndReload() {
        String newPath = tempFile.getAbsolutePath();
        manager.setFileName(newPath);
        assertDoesNotThrow(() -> manager.save());
    }
}
