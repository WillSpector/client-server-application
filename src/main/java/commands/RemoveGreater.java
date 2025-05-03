package commands;

import data.CollectionManager;
import models.MusicBand;
import server.interfaces.UserInputProvider;

import java.util.Iterator;
import java.util.Map;

/**
 * Команда для удаления всех элементов, превышающих элементов.
 */
public class RemoveGreater extends BaseCommand {

    public RemoveGreater(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String execute(UserInputProvider inputProvider) {
        try {
            int id = askInt(inputProvider);
            int removedCount = removeGreaterThan(id);
            if (removedCount == 0) {
                return "Нет элементов с ID больше указанного.";
            }
            return "Удалено элементов: " + removedCount;
        } catch (Exception e) {
            return "Ошибка при удалении: " + e.getMessage();
        }
    }

    private int askInt(UserInputProvider input) {
        while (true) {
            try {
                return Integer.parseInt(input.ask("Введите ID, выше которого следует удалить элементы:"));
            } catch (NumberFormatException e) {
                input.showMessage("Ошибка: введите корректное целое число.");
            }
        }
    }


    private int removeGreaterThan(int id) {
        Map<Integer, MusicBand> collection = collectionManager.getCollection();

        if (collection.isEmpty()) {
            return 0;
        }

        Iterator<Map.Entry<Integer, MusicBand>> iterator = collection.entrySet().iterator();
        int removedCount = 0;

        while (iterator.hasNext()) {
            Map.Entry<Integer, MusicBand> entry = iterator.next();
            if (entry.getKey() > id) {
                iterator.remove();
                removedCount++;
            }
        }
        collectionManager.save();
        return removedCount;
    }


    @Override
    public String getDescription() {
        return "Удаляет из коллекции все элементы, превышающие заданный.";
    }
}

