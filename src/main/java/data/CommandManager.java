package data;

import commands.*;
import commands.interfaces.CommandUseable;
import server.interfaces.UserInputProvider;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Отвечает за управление командами, их регистрацию и выполнение.
 */
public class CommandManager {
    private final Map<String, CommandUseable> commands = new TreeMap<>();
    private static final String SPACE = "\\s+";
    private static final int SPLIT_LIMIT = 2;
    private static final int ELEMENT = 0;


    public CommandManager(String fileName, CollectionManager collectionManager) {
        initializeCommands(fileName, collectionManager);
    }

    public Map<String, CommandUseable> getCommands() {
        return commands;
    }

    /**
     * Регистрирует команды с использованием Stream API
     */

    public void initializeCommands(String fileName, CollectionManager collectionManager) {
        Map<String, CommandUseable> initialCommands = Stream.of(
                Map.entry("help", new Help(this)),
                Map.entry("info", new Info(collectionManager)),
                Map.entry("show", new Show(collectionManager)),
                Map.entry("insert", new Insert(collectionManager)),
                Map.entry("update", new Update(collectionManager)),
                Map.entry("remove_key", new RemoveKey(collectionManager)),
                Map.entry("clear", new Clear(collectionManager)),
                Map.entry("execute_script", new ExecuteScript(this)),
                Map.entry("exit", new Exit(collectionManager)),
                Map.entry("remove_greater", new RemoveGreater(collectionManager)),
                Map.entry("replace_if_lower", new ReplaceIfLower(collectionManager)),
                Map.entry("remove_greater_key", new RemoveGreaterKey(collectionManager)),
                Map.entry("filter_by_studio", new FilterByStudio(collectionManager)),
                Map.entry("filter_greater_than_genre", new FilterGreaterThanGenre(collectionManager)),
                Map.entry("print_unique_number_of_participants", new PrintUniqueNumberOfParticipants(collectionManager))
//                ,Map.entry("save", new Save(fileName, collectionManager)) / Оставлено для большего функционала
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        commands.putAll(initialCommands);
    }

    /**
     * Возвращает название команды по ее порядковому номеру
     */
    private String getCommandByNumber(int number) {
        return commands.keySet().stream()
                .skip(number - 1L)
                .findFirst()
                .orElse(null);
    }
    /**
     * Извлекает название команды из введенного пользователем текста.
     */
    private String parseCommand(String input) {
        return input.trim().split(SPACE, SPLIT_LIMIT)[ELEMENT];
    }

    /**
     * Выполняет команду с использованием пользовательского ввода (для интерактивных команд)
     */
    public String executeCommand(String input, UserInputProvider inputProvider) {
        if (input == null || input.trim().isEmpty()) {
            return "Ошибка: Пустая команда не может быть выполнена.";
        }

        try {
            int commandNumber = Integer.parseInt(input.trim());
            String commandName = getCommandByNumber(commandNumber);
            if (commandName != null) {
                return commands.get(commandName).executeWithOutput();
            } else {
                return "Ошибка: Команда " + commandNumber + " не найдена.";
            }
        } catch (NumberFormatException ignored) {}

        String commandName = parseCommand(input);
        CommandUseable commandUseable = commands.get(commandName);

        if (commandUseable == null) {
            return "Неизвестная команда.";
        }

        String parameters = input.substring(commandName.length()).trim();
        return executeCommandByType(commandUseable, parameters, inputProvider);
    }

    private String executeCommandByType(CommandUseable commandUseable, String parameters, UserInputProvider inputProvider) {
        switch (commandUseable) {
            case ExecuteScript executeScript:
                return executeScript.execute(parameters, inputProvider);
            case Insert insert:
                return executeInsertCommand(insert, parameters, inputProvider);
            case Update update:
                return update.execute(inputProvider);
            case ReplaceIfLower replaceIfLower:
                return replaceIfLower.execute(parameters, inputProvider);
            case FilterByStudio filterByStudio:
                return filterByStudio.execute(parameters, inputProvider);
            case FilterGreaterThanGenre filterGenre:
                return filterGenre.execute(parameters, inputProvider);
            case RemoveGreater removeGreater:
                return removeGreater.execute(inputProvider);
            case RemoveGreaterKey removeGreaterKey:
                return removeGreaterKey.execute(inputProvider);
            case RemoveKey removeKey:
                return removeKey.execute(inputProvider);
            default:
                return commandUseable.executeWithOutput();
        }
    }

    private String executeInsertCommand(Insert insert, String parameters, UserInputProvider inputProvider) {
        if (!parameters.isEmpty()) {
            insert.executeInsertWithParameters(parameters);
            return "Коллекция добавлена через 'execute_script'.";
        } else {
            insert.execute(inputProvider);
            return "Музыкальная группа успешно добавлена и сохранена.";
        }
    }

}
