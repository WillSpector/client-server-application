package server;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Server {
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final Map<SocketChannel, ClientHandler> clientSessions = new HashMap<>();
    private final Path portFilePath = Paths.get("server-port.txt");
    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        int port = 12345;
        try {
            new Server(port).run();
        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер на порту " + port + ": " + e.getMessage());
        }
    }

    public Server(int port) throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();

        // Ищем порт
        try {
            serverChannel.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            System.out.println(">>> Порт " + port + " занят. Ищу свободный порт...");
            try (ServerSocket tempSocket = new ServerSocket(0)) {
                port = tempSocket.getLocalPort();
            }
            serverChannel.bind(new InetSocketAddress(port));
        }
        // Сохраняем порт в файл
        try (FileWriter writer = new FileWriter(portFilePath.toFile())) {
            writer.write(String.valueOf(port));
        } catch (IOException e) {
            System.err.println(">>> Не удалось сохранить порт в файл: " + e.getMessage());
        }

        // Удаление файла при завершении работы
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.deleteIfExists(portFilePath);
                System.out.println(">>> Файл server-port.txt удалён.");
            } catch (IOException e) {
                System.err.println(">>> Не удалось удалить server-port.txt: " + e.getMessage());
            }
        }));

        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println(">>> Сервер запущен на порту " + port);
    }

    public void run() throws IOException {
        while (true) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (!key.isValid()) continue;

                if (key.isAcceptable()) {
                    acceptConnection();
                } else if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ClientHandler session = clientSessions.get(client);
                    if (session != null) {
                        session.read(key);
                    }
                }
            }
        }
    }

    private void acceptConnection() throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        // регистрируем и сразу получаем ключ
        client.register(selector, SelectionKey.OP_READ);
        System.out.println(">>> Новое подключение: " + client.getRemoteAddress());

        // читаем имя файла первым сообщением
        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
        int bytesRead = client.read(buf);
        String fileName = "collection.json";
        if (bytesRead > 0) {
            buf.flip();
            fileName = StandardCharsets.UTF_8.decode(buf).toString().trim();
        }

        ClientHandler session = new ClientHandler(client, fileName);
        clientSessions.put(client, session);
    }
}