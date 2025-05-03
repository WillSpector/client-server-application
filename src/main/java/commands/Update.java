package commands;

import data.CollectionManager;
import models.*;
import server.interfaces.UserInputProvider;

/**
 * Команда обновляет элемент коллекции по ID.
 */
public class Update extends BaseCommand {

    public Update(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String execute(UserInputProvider inputProvider) {
        try {
            int id = askId(inputProvider);

            MusicBand oldMusicBand = collectionManager.getByKey(id);
            if (oldMusicBand == null) {
                return "Элемент с id " + id + " не найден.";
            }

            String name = inputProvider.ask("Введите название группы:");

            long x = askLong(inputProvider);
            double y = askDouble(inputProvider);
            Coordinates coordinates = new Coordinates(x, y);
            int numberOfParticipants = askInt(inputProvider);
            MusicGenre genre = askGenre(inputProvider);
            String studioName = inputProvider.ask("Введите название студии:");
            String address = inputProvider.ask("Введите адрес студии:");
            Studio studio = new Studio(studioName, address);

            MusicBand updatedBand = new MusicBand(name, coordinates, numberOfParticipants, genre, studio);
            updatedBand.setId(oldMusicBand.getId());

            collectionManager.getCollection().put(id, updatedBand);
            collectionManager.save();

            return "Элемент с id " + id + " успешно обновлён.";

        } catch (Exception e) {
            return "Ошибка при обновлении элемента: " + e.getMessage();
        }
    }

    private int askId(UserInputProvider input) {
        while (true) {
            try {
                return Integer.parseInt(input.ask("Введите ID элемента, который хотите обновить:"));
            } catch (NumberFormatException e) {
                input.showMessage("Ошибка: id должен быть целым числом. Попробуйте снова.");
            }
        }
    }

    private long askLong(UserInputProvider input) {
        while (true) {
            try {
                return Long.parseLong(input.ask("Введите координату X:"));
            } catch (NumberFormatException e) {
                input.showMessage("Ошибка: введите целое число (long). Попробуйте снова.");
            }
        }
    }

    private double askDouble(UserInputProvider input) {
        while (true) {
            try {
                return Double.parseDouble(input.ask("Введите координату Y:"));
            } catch (NumberFormatException e) {
                input.showMessage("Ошибка: введите число (double). Попробуйте снова.");
            }
        }
    }

    private int askInt(UserInputProvider input) {
        while (true) {
            try {
                return Integer.parseInt(input.ask("Введите количество участников (может быть 0):"));
            } catch (NumberFormatException e) {
                input.showMessage("Ошибка: введите целое число.");
            }
        }
    }

    private MusicGenre askGenre(UserInputProvider input) {
        while (true) {
            try {
                return MusicGenre.valueOf(input.ask("Введите жанр (ROCK, PSYCHEDELIC_ROCK, PSYCHEDELIC_CLOUD_RAP, JAZZ):").trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                input.showMessage("Ошибка: неверный жанр. Возможные значения: ROCK, PSYCHEDELIC_ROCK, PSYCHEDELIC_CLOUD_RAP, JAZZ.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "update: обновить значение элемента коллекции по id (id будет запрошен отдельно).";
    }
}
