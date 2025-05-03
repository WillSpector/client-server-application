package commands;

import commands.interfaces.CommandUseable;
import data.CommandManager;

import java.util.Map;

/**
 * Команда для вывода списка доступных команд.
 * Использует CommandManager для отображения доступных команд и их описаний.
 */
public class Help implements CommandUseable {
    private final CommandManager commandManager;

    public Help(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String executeWithOutput() {
        StringBuilder output = new StringBuilder("Список доступных команд:\n");
        int index = 1;
        for (Map.Entry<String, CommandUseable> entry : commandManager.getCommands().entrySet()) {
            output.append(index++)
                    .append(". ")
                    .append(entry.getKey())
                    .append(" - ")
                    .append(entry.getValue().getDescription())
                    .append("\n");
        }
        return output.toString();
    }

    @Override
    public String getDescription() {
        return "Выводит список доступных команд.";
    }

}
