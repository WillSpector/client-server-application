package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class CommandHandler {
    private final SocketChannel socketChannel;
    private final Selector selector;

    public CommandHandler(SocketChannel socketChannel, Selector selector) {
        this.socketChannel = socketChannel;
        this.selector = selector;
    }

    public void handle() {
        try {
            System.out.println("[Клиент] Ожидание запроса от сервера...");
            waitForServerResponse();

            while (true) {
                String commandName = UserInput.readCommand();

                if (commandName.equalsIgnoreCase("exit")) {
                    System.out.println("[Клиент] Завершение клиента.");
                    shutdown();
                    break;
                }

                sendMessage(commandName);
                System.out.println("[Клиент] Ожидание ответа на команду от сервера...");
                waitForServerResponse();
            }
        } catch (IOException e) {
            System.out.println("[Клиент] Ошибка во время работы клиента: " + e.getMessage());
            shutdown();
        }
    }

    private void sendMessage(String message) throws IOException {
        message += "\n";
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(message);
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
//        System.out.print("[Клиент] Сообщение отправлено: " + message); // доп проверка
    }

    private void waitForServerResponse() throws IOException {
        while (true) {
            String serverResponse = readServerResponse();

            if (!serverResponse.isEmpty()) {
                if (serverResponse.trim().endsWith(":")) {
                    String fieldInput = UserInput.readLine(serverResponse + " ");
                    sendMessage(fieldInput);
                    continue;
                }
                System.out.println("[Сервер] " + serverResponse);
                break;
            }

            selector.select();
            processSelectedKeys();
        }
    }

    private String readServerResponse() throws IOException {
        StringBuilder fullMessage = new StringBuilder();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        while (true) {
            buffer.clear();
            int bytesRead = socketChannel.read(buffer);
            if (bytesRead <= 0) break;

            buffer.flip();
            fullMessage.append(StandardCharsets.UTF_8.decode(buffer));

            if (fullMessage.toString().contains("__END__")) {
                break;
            }
        }
        return fullMessage.toString().replace("__END__", "").trim();
    }

    private void processSelectedKeys() throws IOException {
        Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
        while (selectedKeys.hasNext()) {
            SelectionKey key = selectedKeys.next();
            selectedKeys.remove();

            if (key.isReadable()) {
                String response = readServerResponse();
                if (!response.isEmpty()) {
                    System.out.println("[Сервер] " + response);
                }
            }
        }
    }

    private void shutdown() {
        try {
            socketChannel.close();
            selector.close();
            System.out.println("[Клиент] Соединение закрыто.");
        } catch (IOException e) {
            System.out.println("[Клиент] Ошибка при закрытии ресурсов: " + e.getMessage());
        }
    }
}