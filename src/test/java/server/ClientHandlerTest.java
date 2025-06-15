package server;

import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import static org.junit.jupiter.api.Assertions.*;

public class ClientHandlerTest {

    @Test
    public void testConstructorWithCustomFileName() throws Exception {
        SocketChannel dummyChannel = SelectorProvider.provider().openSocketChannel();

        ClientHandler handler = new ClientHandler(dummyChannel, "test.json");

        assertEquals(ClientState.WAITING_FOR_FILE_NAME, handler.getState(), "Начальное состояние должно быть WAITING_FOR_FILE_NAME");
        assertEquals("test.json", handler.getFileName(), "Имя файла должно устанавливаться корректно");
    }

    @Test
    public void testConstructorWithNullFileNameUsesDefault() throws Exception {
        SocketChannel dummyChannel = SelectorProvider.provider().openSocketChannel();

        ClientHandler handler = new ClientHandler(dummyChannel, null);

        assertEquals("collection.json", handler.getFileName(), "Если имя файла null, должно быть установлено значение по умолчанию");
    }

}
