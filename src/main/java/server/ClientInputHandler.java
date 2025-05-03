package server;

import data.Response;
import server.interfaces.UserInputProvider;
import data.RequestInput;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ClientInputHandler implements UserInputProvider {
    private final SocketChannel clientChannel;
    private final Selector selector;
    private static final int TIMEOUT = 5000;
    private static final int BUFFER_SIZE = 4096;

    public ClientInputHandler(SocketChannel clientChannel) throws IOException {
        this.clientChannel = clientChannel;
        this.selector = Selector.open();
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    @Override
    public String ask(String prompt) {
        try {
            sendRequest(prompt);  // Отправка запроса на ввод
            return receiveResponse();  // Ожидание ответа
        } catch (IOException e) {
            System.err.println(">>> Ошибка при запросе данных от клиента: " + e.getMessage());
        }
        return ">>> Ошибка ввода.";
    }

    @Override
    public void showMessage(String message) {
        try {
            sendResponse(message); // Отправка сообщения клиенту
        } catch (IOException e) {
            System.err.println(">>> Ошибка при отправке сообщения клиенту: " + e.getMessage());
        }
    }

    private void sendRequest(String prompt) throws IOException {
        RequestInput requestInput = new RequestInput(prompt);
        writeToChannel(clientChannel, requestInput.getPrompt());
    }

    private void sendResponse(String message) throws IOException {
        Response response = new Response(message);
        writeToChannel(clientChannel, response.getMessage());
    }

    private void writeToChannel(SocketChannel channel, String message) throws IOException {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(message);
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    private String receiveResponse() throws IOException {
        int readyChannels = selector.select(TIMEOUT);  // таймаут 5 сек
        if (readyChannels == 0) {
            System.out.println(">>> Ожидание ответа от клиента истекло.");
            return null;
        }

        // Обрабатываем выбранные каналы
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove(); // Удаляем обработанный ключ

            if (key.isReadable()) {
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                int bytesRead = clientChannel.read(buffer);

                if (bytesRead == -1) {
                    System.out.println(">>> Канал закрыт клиентом.");
                    return null;
                }

                buffer.flip();
                return StandardCharsets.UTF_8.decode(buffer).toString().trim();
            }
        }
        return null;
    }
}
