package data;

import commands.interfaces.CommandUseable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.interfaces.UserInputProvider;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {

    private CommandManager commandManager;

    @BeforeEach
    void setUp() {
        CollectionManager collectionManager = new CollectionManager("test.json");
        commandManager = new CommandManager("test.json", collectionManager);
    }

    @Test
    void testCommandsAreInitialized() {
        Map<String, CommandUseable> commands = commandManager.getCommands();

        assertFalse(commands.isEmpty(), "Команды должны быть инициализированы");
        assertTrue(commands.containsKey("help"), "Команда 'help' должна быть зарегистрирована");
        assertTrue(commands.containsKey("insert"), "Команда 'insert' должна быть зарегистрирована");
    }

    @Test
    void testExecuteHelpCommand() {
        String result = commandManager.executeCommand("help", new DummyInputProvider());
        assertNotNull(result);
        assertTrue(result.contains("help"), "Результат должен содержать 'help'");
    }


    @Test
    void testExecuteEmptyCommand() {
        String result = commandManager.executeCommand(" ", new DummyInputProvider());
        assertEquals("Ошибка: Пустая команда не может быть выполнена.", result);
    }

    // Заглушка для интерфейса UserInputProvider
    static class DummyInputProvider implements UserInputProvider {

        @Override
        public String ask(String prompt) {
            return "";
        }

        @Override
        public void showMessage(String message) {

        }
    }
}
