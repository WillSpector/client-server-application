package server;

import data.Command;
import data.CommandManager;
import data.CollectionManager;
import data.Response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


public class ClientHandler {
    private final SocketChannel channel;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(8192);
    private String fileName = "collection.json";  // Файл по умолчанию
    private final CommandManager commandManager;
    private final CollectionManager collectionManager;

    private ClientState state = ClientState.WAITING_FOR_FILE_NAME;  // стартуем с ожидания имени файла
    private Command currentCommand;  // Текущая команда
    private String userInput = "";  // Ввод пользователя, если требуется

    public ClientHandler(SocketChannel channel, String fileName) {
        this.channel = channel;
        if (fileName != null && !fileName.isBlank()) {
            this.fileName = fileName;
        }
        this.collectionManager = new CollectionManager(this.fileName);
        this.commandManager = new CommandManager(this.fileName, collectionManager);
    }

    public void read(SelectionKey key) {
        try {
            readBuffer.clear();
            int bytesRead = channel.read(readBuffer);
            if (bytesRead == -1) {
                closeConnection(key);
                return;
            }

            readBuffer.flip();
            String commandStr = StandardCharsets.UTF_8.decode(readBuffer).toString().trim();
            System.out.println("[Сервер] Состояние клиента: " + state);
            System.out.println("[Сервер] Получено от клиента: \"" + commandStr + "\"");

            if (commandStr.equalsIgnoreCase("exit")) {
                collectionManager.save();
                sendResponse(new Response("[Сервер] Сервер завершает соединение."), key);
                closeConnection(key);
                return;
            }

            switch (state) {
                case WAITING_FOR_FILE_NAME:
                    handleFileName(commandStr, key);
                    break;
                case IDLE:
                    handleCommand(commandStr, key);
                    break;
                case WAITING_FOR_INPUT:
                    handleUserInput(commandStr, key);
                    break;
                case EXECUTING:
                    System.out.println("[Сервер] Получено во время EXECUTING — игнорируем.");
                    break;
            }

        } catch (IOException e) {
            System.err.println("[Сервер] Ошибка чтения: " + e.getMessage());
            closeConnection(key);
        }
    }

    private void handleFileName(String fileName, SelectionKey key) {
        this.fileName = fileName.trim();
        System.out.println("[Сервер] Имя файла получено: " + this.fileName);

        collectionManager.setFileName(this.fileName);
        state = ClientState.IDLE;
        System.out.println("[Сервер] Переход в состояние IDLE (готов к командам)");

        try {
            sendResponse(new Response("Файл задан, можно отправлять команды."), key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleCommand(String commandStr, SelectionKey key) throws IOException {
        System.out.println("[Сервер] Обрабатываем команду: " + commandStr);

        Command command = new Command(commandStr, null, false);
        currentCommand = command;

        if (command.requiresInput()) {
            state = ClientState.WAITING_FOR_INPUT;
            System.out.println("[Сервер] Команда требует ввода. Переход в WAITING_FOR_INPUT");
            sendResponse(new Response("[Сервер] Введите данные для команды:"), key);
        } else {
            state = ClientState.EXECUTING;
            System.out.println("[Сервер] Команда не требует ввода. Переход в EXECUTING");
            executeCommand(key);
        }
    }


    private void handleUserInput(String input, SelectionKey key) {
        userInput = input.trim();
        System.out.println("[Сервер] Введено пользователем: " + userInput);

        currentCommand.setArgument(userInput);
        state = ClientState.EXECUTING;
        System.out.println("[Сервер] Переход в EXECUTING");
        executeCommand(key);
    }

    private void executeCommand(SelectionKey key) {
        try {
            System.out.println("[Сервер] Выполняем команду: " + currentCommand.getName());
            String result = commandManager.executeCommand(currentCommand.asFullInput(), new ClientInputHandler(channel));

            if (result == null || result.isBlank()) result = "[Сервер] Ошибка выполнения команды";
            sendResponse(new Response(result), key);
        } catch (Exception e) {
            System.err.println("[Сервер] Ошибка при выполнении команды: " + e.getMessage());
            try {
                sendResponse(new Response("[Сервер] Ошибка выполнения команды: " + e.getMessage()), key);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();

        }

        state = ClientState.IDLE;
        System.out.println("[Сервер] Переход в IDLE (ожидание следующей команды)");
    }

    private void sendResponse(Response response, SelectionKey key) throws IOException {
        byte[] data = (response.getMessage() + "\n").getBytes(StandardCharsets.UTF_8);
        if (data.length > writeBuffer.capacity()) {
            writeBuffer = ByteBuffer.allocate(data.length);  // увеличить буфер один раз
        }
        writeBuffer.clear();
        writeBuffer.put(data);
        writeBuffer.flip();
        channel.write(writeBuffer);
        key.interestOps(SelectionKey.OP_READ);
    }

    private void closeConnection(SelectionKey key) {
        try {
            System.out.println("[Сервер] Клиент отключился: " + channel.getRemoteAddress());
            key.cancel();
            channel.close();
        } catch (IOException e) {
            System.err.println("[Сервер] Ошибка при закрытии: " + e.getMessage());
        }
    }
    public ClientState getState() {
        return this.state;
    }

    public String getFileName() {
        return this.fileName;
    }

}
