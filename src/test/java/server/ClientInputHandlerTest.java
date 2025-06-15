package server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import static org.junit.jupiter.api.Assertions.*;

public class ClientInputHandlerTest {

    @Test
    public void testShowMessageDoesNotThrow() throws IOException {

        SocketChannel dummyChannel = SelectorProvider.provider().openSocketChannel();
        dummyChannel.configureBlocking(false);

        ClientInputHandler handler = new ClientInputHandler(dummyChannel);


        assertDoesNotThrow(() -> handler.showMessage("Ответ от сервера"));
    }

    @Test
    public void testAskReturnsErrorOnIOException() throws IOException {
        SocketChannel dummyChannel = SelectorProvider.provider().openSocketChannel();
        dummyChannel.close();

        ClientInputHandler handler = new ClientInputHandler(dummyChannel);
        String result = handler.ask("Введите что-нибудь");

        assertEquals(">>> Ошибка ввода.", result);
    }
}
