package commands;

import data.CollectionManager;


/**
 * Команда для вывода информации о коллекции.
 * Показывает тип коллекции, дату инициализации и количество элементов.
 */
public class Info extends BaseCommand {

    public Info(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public String executeWithOutput() {
        StringBuilder result = new StringBuilder();
        result.append("Информация о коллекции:\n");
        result.append("Тип коллекции: ").append(collectionManager.getCollection().getClass().getName()).append("\n");
        result.append("Дата инициализации: ").append(collectionManager.getInitializationDate()).append("\n");
        result.append("Количество элементов: ").append(collectionManager.getCollection().size()).append("\n");
        return result.toString();
    }

    @Override
    public String getDescription() {
        return "Выводит информацию о коллекции (тип, дата инициализации, количество элементов).";
    }
}
