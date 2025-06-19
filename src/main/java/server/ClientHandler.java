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
import java.util.logging.*;

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

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    static {
        Logger rootLogger = Logger.getLogger("");
        for (Handler h : rootLogger.getHandlers()) {
            h.setFormatter(new SimpleFormatter());
            h.setLevel(Level.FINE);  // показать fine-уровень
        }
        logger.setLevel(Level.FINE);
    }

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
            logger.fine("Состояние клиента: " + state);
            logger.info("Получено от клиента: \"" + commandStr + "\"");

            if (commandStr.equalsIgnoreCase("exit")) {
                collectionManager.save();
                sendResponse(new Response("Сервер завершает соединение."), key);
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
                    logger.fine("Получено во время EXECUTING — игнорируем.");
                    break;
            }

        } catch (IOException e) {
            logger.warning("Ошибка чтения: " + e.getMessage());
            closeConnection(key);
        }
    }

    private void handleFileName(String fileName, SelectionKey key) {
        this.fileName = fileName.trim();
        logger.info("Имя файла получено: " + this.fileName);

        collectionManager.setFileName(this.fileName);
        state = ClientState.IDLE;
        logger.fine("Переход в состояние IDLE (готов к командам)");

        try {
            sendResponse(new Response("Файл задан, можно отправлять команды."), key);
        } catch (IOException e) {
            logger.warning("Не удалось отправить ответ после установки файла: " + e.getMessage());
        }
    }

    private void handleCommand(String commandStr, SelectionKey key) throws IOException {
        logger.info("Обрабатываем команду: " + commandStr);

        Command command = new Command(commandStr, null, false);
        currentCommand = command;

        if (command.requiresInput()) {
            state = ClientState.WAITING_FOR_INPUT;
            logger.fine("Команда требует ввода. Переход в WAITING_FOR_INPUT");
            sendResponse(new Response("Введите данные для команды:"), key);
        } else {
            state = ClientState.EXECUTING;
            logger.fine("Команда не требует ввода. Переход в EXECUTING");
            executeCommand(key);
        }
    }

    private void handleUserInput(String input, SelectionKey key) {
        userInput = input.trim();
        logger.info("Введено пользователем: " + userInput);

        currentCommand.setArgument(userInput);
        state = ClientState.EXECUTING;
        logger.fine("Переход в EXECUTING");
        executeCommand(key);
    }

    private void executeCommand(SelectionKey key) {
        try {
            logger.info("Выполняем команду: " + currentCommand.getName());
            String result = commandManager.executeCommand(currentCommand.asFullInput(), new ClientInputHandler(channel));

            if (result == null || result.isBlank()) result = "Ошибка выполнения команды";
            sendResponse(new Response(result), key);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при выполнении команды", e);
            try {
                sendResponse(new Response("Ошибка выполнения команды: " + e.getMessage()), key);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Ошибка отправки сообщения об ошибке клиенту", ex);
            }
        }

        state = ClientState.IDLE;
        logger.fine("Переход в IDLE (ожидание следующей команды)");
    }

    private void sendResponse(Response response, SelectionKey key) throws IOException {
        byte[] data = (response.getMessage() + "\n").getBytes(StandardCharsets.UTF_8);
        if (data.length > writeBuffer.capacity()) {
            writeBuffer = ByteBuffer.allocate(data.length);
        }
        writeBuffer.clear();
        writeBuffer.put(data);
        writeBuffer.flip();
        channel.write(writeBuffer);
        key.interestOps(SelectionKey.OP_READ);
    }

    private void closeConnection(SelectionKey key) {
        try {
            logger.info("Клиент отключился: " + channel.getRemoteAddress());
            key.cancel();
            channel.close();
        } catch (IOException e) {
            logger.warning("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    public ClientState getState() {
        return this.state;
    }

    public String getFileName() {
        return this.fileName;
    }
}

