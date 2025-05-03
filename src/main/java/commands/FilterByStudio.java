package commands;

import data.CollectionManager;
import server.interfaces.UserInputProvider;

/**
 * Команда для фильтрации элементов по названию студии.
 * Пользователь вводит название студии, а программа выводит все данные Music Band.
 */
public class FilterByStudio extends BaseCommand {

    public FilterByStudio(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public String executeWithOutput() {
        return "Команда требует аргумент: название студии. Используйте `filter_by_studio <название>` или введите его вручную.";
    }

    // Основной интерактивный метод, вызываемый через CommandManager с поддержкой ask()
    public String execute(String argument, UserInputProvider inputProvider) {
        if (argument == null || argument.trim().isEmpty()) {
            argument = inputProvider.ask("Введите название студии: ");
        }

        if (argument == null || argument.trim().isEmpty()) {
            return "Ошибка: Название студии не может быть пустым.";
        }
        try {
            return collectionManager.filterByStudio(argument.trim());
        } catch (Exception e) {
            return "Ошибка при фильтрации: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Выводит все элементы, у которых значение поля studio совпадает с заданным.";
    }
}
