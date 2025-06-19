package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInput {

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Сбросить reader — использовать текущий System.in (нужно для тестов)
     */
    public static void resetReader() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Чтение имени файла от пользователя.
     */
    public static String readFileNameFromUser() throws IOException {
        System.out.print("[Клиент] Введите имя файла коллекции: ");
        String input = reader.readLine();
        if (input == null || input.trim().isEmpty()) {
            System.out.println("[Клиент] Имя файла не задано. Используется файл по умолчанию 'collection.json'.");
            return "collection.json";
        }
        input = input.trim();
        if (!input.endsWith(".json")) {
            input += ".json";
        }
        return input;
    }

    /**
     * Чтение произвольной строки от пользователя.
     */
    public static String readLine(String prompt) throws IOException {
        System.out.print(prompt);
        String input = reader.readLine();
        return input != null ? input.trim() : "";
    }

    /**
     * Чтение команды от пользователя.
     */
    public static String readCommand() throws IOException {
        while (true) {
            System.out.print("[Клиент] Введите команду: ");
            String input = reader.readLine();
            if (input == null) return "exit"; // Ctrl+D
            input = input.trim();
            if (input.isEmpty()) {
                System.out.println("[Клиент] Команда не может быть пустой.");
                continue;
            }
            return input;
        }
    }
}


