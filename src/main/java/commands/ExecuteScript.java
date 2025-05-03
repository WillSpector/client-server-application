package commands;

import commands.interfaces.CommandUseable;
import data.CommandManager;
import server.interfaces.UserInputProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Команда для выполнения команд из указанного файла.
 */
public class ExecuteScript implements CommandUseable {
    private final CommandManager commandManager;
    private final Set<String> loadedScripts = new HashSet<>();

    public ExecuteScript(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String executeWithOutput() {
        return "Введите имя файла скрипта.";
    }

    /**
     * Основной метод с поддержкой ask().
     */

    public String execute(String parameters, UserInputProvider inputProvider) {

        if (parameters == null || parameters.trim().isEmpty()) {
            parameters = inputProvider.ask("Введите имя файла скрипта: ");
        }

        if (parameters == null || parameters.trim().isEmpty()) {
            return "Ошибка: имя файла не может быть пустым.";
        }

        return executeScriptWithParameters(parameters.trim(), inputProvider);
    }


    /**
     * Выполняет команды из скрипта.
     */
    public String executeScriptWithParameters(String fileName, UserInputProvider inputProvider) {
        if (loadedScripts.contains(fileName)) {
            return "Ошибка: рекурсивный вызов скрипта \"" + fileName + "\"";
        }

        loadedScripts.add(fileName);
        StringBuilder result = new StringBuilder();

        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            while (fileScanner.hasNextLine()) {
                String command = fileScanner.nextLine().trim();
                if (command.isEmpty()) continue;

                result.append("> ").append(command).append("\n");
                String commandResult = commandManager.executeCommand(command, inputProvider);
                if (commandResult != null) {
                    commandResult = commandResult.replace("__END__", "").trim();
                    if (!commandResult.isEmpty()) {
                        result.append(commandResult).append("\n");
                    }
                }

            }
            result.append("Скрипт выполнен успешно.");
        } catch (FileNotFoundException e) {
            return "Ошибка: файл \"" + fileName + "\" не найден.";
        } catch (Exception e) {
            return "Ошибка при выполнении скрипта: " + e.getMessage();
        } finally {
            loadedScripts.remove(fileName);
        }

        return result.toString();
    }

    @Override
    public String getDescription() {
        return "Выполняет команды из указанного файла.";
    }
}
