package commands;

import data.CollectionManager;
import server.interfaces.UserInputProvider;

/**
 * Команда для удаления элемента из коллекции по заданному ключу.
 */
public class RemoveKey extends BaseCommand {

    public RemoveKey(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String execute(UserInputProvider inputProvider) {
        try {
            int key = askKey(inputProvider);

            if (collectionManager.getCollection().isEmpty()) {
                return "Коллекция пуста, нечего удалять.";
            }

            boolean removed = collectionManager.removeByKey(key);
            if (removed) {
                collectionManager.save();
                return "Элемент с ключом " + key + " удалён.";
            } else {
                return "Элемент с таким ключом не найден.";
            }

        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    private int askKey(UserInputProvider inputProvider) {
        while (true) {
            try {
                return Integer.parseInt(inputProvider.ask("Введите ключ (ID) элемента, который хотите удалить:"));
            } catch (NumberFormatException e) {
                inputProvider.showMessage("Ошибка: ключ должен быть целым числом. Попробуйте снова.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Удаляет элемент из коллекции по его ключу.";
    }
}

