package server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    @Test
    public void testServerPortFileIsCreated() throws IOException {
        // Удаляем файл, если он существует
        Path portFilePath = Paths.get("server-port.txt");
        Files.deleteIfExists(portFilePath);

        // Создаём сервер
        Server server = new Server(0);

        // Проверяем, что файл с портом создан
        assertTrue(Files.exists(portFilePath), "Файл server-port.txt должен существовать");

        // Проверяем, что в файле корректный порт
        String content = Files.readString(portFilePath);
        assertDoesNotThrow(() -> Integer.parseInt(content.trim()), "Файл должен содержать число");

    }
}
