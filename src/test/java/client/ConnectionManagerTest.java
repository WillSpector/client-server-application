package client;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionManagerTest {

    private static final String TEST_PORT_FILE = "server-port.txt";

    @BeforeEach
    public void setUp() throws IOException {
        // Записываем корректный порт в файл перед каждым тестом
        Files.writeString(Paths.get(TEST_PORT_FILE), "5555", StandardCharsets.UTF_8);
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Удаляем файл с портом после каждого теста, чтобы не мешать другим тестам
        Files.deleteIfExists(Paths.get(TEST_PORT_FILE));
    }



    @Test
    public void testReadPortFromFile_MissingFile() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_PORT_FILE));

        IOException ex = assertThrows(IOException.class, () -> {
            ConnectionManager.connectToServer("test.json");
        });

        assertTrue(ex.getMessage().contains("Файл с портом"),
                "Ожидается сообщение об отсутствии файла с портом");
    }

    @Test
    public void testReadPortFromFile_InvalidFormat() throws IOException {
        Files.writeString(Paths.get(TEST_PORT_FILE), "порт 5555", StandardCharsets.UTF_8);

        IOException ex = assertThrows(IOException.class, () -> {
            ConnectionManager.connectToServer("test.json");
        });

        assertTrue(ex.getMessage().toLowerCase().contains("формат порта"),
                "Ожидается сообщение о неверном формате порта");
    }
}
