package client;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionManagerTest {

    private static final String TEST_PORT_FILE = "server-port.txt";

    // Подготовка файла с корректным портом
    @BeforeEach
    public void setUp() throws IOException {

        Files.write(Paths.get(TEST_PORT_FILE), "5555".getBytes(StandardCharsets.UTF_8));
    }

    // Удаление файла после каждого теста
    @AfterEach
    public void tearDown() throws IOException {

        Files.deleteIfExists(Paths.get(TEST_PORT_FILE));
    }

    @Test
    public void testReadPortFromFile_Valid() {
        assertDoesNotThrow(() -> {
            ConnectionManager.connectToServer("test.json");
        }, "Метод должен корректно считать порт и запустить подключение (если сервер доступен)");
    }

    @Test
    public void testReadPortFromFile_MissingFile() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_PORT_FILE));

        IOException ex = assertThrows(IOException.class, () -> {
            ConnectionManager.connectToServer("test.json");
        });

        assertTrue(ex.getMessage().contains("Файл с портом"), "Cообщение об отсутствии файла");
    }

    @Test
    public void testReadPortFromFile_InvalidFormat() throws IOException {
        Files.write(Paths.get(TEST_PORT_FILE), "порт 5555".getBytes(StandardCharsets.UTF_8));

        IOException ex = assertThrows(IOException.class, () -> {
            ConnectionManager.connectToServer("test.json");
        });

        assertTrue(ex.getMessage().contains("формат порта"), "Ожидаемое сообщение о неверном формате");
    }
}
