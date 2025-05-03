package commands;

import data.CollectionManager;
import models.MusicBand;
import server.interfaces.UserInputProvider;

import java.util.Iterator;
import java.util.Map;

/**
 * Команда для удаления всех элементов, ключ которых превышает заданный.
 */
public class RemoveGreaterKey extends BaseCommand {

    public RemoveGreaterKey(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String execute(UserInputProvider inputProvider) {
        try {
            int keyThreshold = askInt(inputProvider);

            Map<Integer, MusicBand> collection = collectionManager.getCollection();
            if (collection.isEmpty()) {
                return "Коллекция пуста. Нечего удалять.";
            }

            Iterator<Map.Entry<Integer, MusicBand>> iterator = collection.entrySet().iterator();
            int removedCount = 0;
            while (iterator.hasNext()) {
                Map.Entry<Integer, MusicBand> entry = iterator.next();
                if (entry.getKey() > keyThreshold) {
                    iterator.remove();
                    removedCount++;
                }
            }

            if (removedCount == 0) {
                return "Нет элементов с ключом больше заданного.";
            }

            collectionManager.save();
            return "Удалено элементов: " + removedCount;

        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    private int askInt(UserInputProvider inputProvider) {
        while (true) {
            try {
                return Integer.parseInt(inputProvider.ask("Введите ключ (ID), выше которого следует удалить элементы:"));
            } catch (NumberFormatException e) {
                inputProvider.showMessage("Ошибка: ключ должен быть целым числом. Попробуйте снова.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Удаляет из коллекции все элементы, ключ которых превышает заданный.";
    }
}
