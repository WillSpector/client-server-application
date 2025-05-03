package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ConnectionManager {
    private static final String SERVER_ADDRESS = "localhost";
    private static final String PORT_FILE = "server-port.txt";
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_DELAY_MS = 3000;
    private static final int SELECT_TIMEOUT_MS = 100;

    public static void connectToServer(String fileName) throws IOException {
        int SERVER_PORT = readPortFromFile();
        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            try (SocketChannel socketChannel = SocketChannel.open();
                 Selector selector = Selector.open()) {

                socketChannel.configureBlocking(false);
                socketChannel.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));
                socketChannel.register(selector, SelectionKey.OP_CONNECT);

                if (waitForConnect(selector, socketChannel)) {
                    System.out.println("[Клиент] Подключено к " + SERVER_ADDRESS + ":" + SERVER_PORT);
                    sendFileNameToServer(socketChannel, fileName);
                    new CommandHandler(socketChannel, selector).handle();
                    return;
                } else {
                    throw new IOException("[Клиент] Не удалось завершить подключение.");
                }

            } catch (IOException e) {
                attempt++;
                System.err.println("[Клиент] Не удалось подключиться (попытка " + attempt + " из " + MAX_RETRIES + "): " + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        throw new IOException("[Клиент] Ожидание между попытками было прервано", ie);
                    }
                } else {
                    throw new IOException("[Клиент] Не удалось установить соединение после " + MAX_RETRIES + " попыток.");
                }
            }
        }
    }

    // Читаем порт из файла
    private static int readPortFromFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(PORT_FILE))) {
            return Integer.parseInt(reader.readLine().trim());
        } catch (FileNotFoundException e) {
            throw new IOException("[Клиент] Файл с портом " + PORT_FILE + " не найден.");
        } catch (NumberFormatException e) {
            throw new IOException("[Клиент] Некорректный формат порта " + PORT_FILE);
        }
    }

    private static boolean waitForConnect(Selector selector, SocketChannel socketChannel) throws IOException {
        while (true) {
            if (selector.select(SELECT_TIMEOUT_MS) == 0) continue;

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()) {
                    return socketChannel.finishConnect();
                }
            }
        }
    }

    private static void sendFileNameToServer(SocketChannel socketChannel, String message) throws IOException {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(message + "\n");
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
    }
}
