package client;

import java.io.IOException;

public class Client {
    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        try {
            // Получаем имя файла коллекции от пользователя
            String fileName = UserInput.readFileNameFromUser();
            // Подключаемся к серверу
            ConnectionManager.connectToServer(fileName);
        } catch (IOException e) {
            System.err.println("Клиент завершил работу с ошибкой: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
