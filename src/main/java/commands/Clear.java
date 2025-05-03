package commands;

import data.CollectionManager;

/**
 * Команда для очистки коллекции.
 * Удаляет все элементы коллекции и сохраняет изменения.
 */
public class Clear extends BaseCommand {
    private static final String MESSAGE = "Коллекция успешно очищена!";

    public Clear(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public String executeWithOutput() {
        clearCollection();
        saveCollection();
        return MESSAGE;
    }

    private void clearCollection() {
        collectionManager.clear();
    }

    private void saveCollection() {
        collectionManager.save();
    }

    @Override
    public String getDescription() {
        return "Очищает коллекцию.";
    }
}
