package commands;

import data.CollectionManager;
import server.interfaces.UserInputProvider;

/**
 * Команда для фильтрации элементов, у которых жанр музыки превышает заданное значение.
 * Пользователь вводит название жанра, а программа выводит жанры, которых больше по количеству.
 */
public class FilterGreaterThanGenre extends BaseCommand {

    public FilterGreaterThanGenre(CollectionManager collectionManager) {
        super(collectionManager);
    }


    @Override
    public String executeWithOutput() {
        return "Команда требует аргумент: название жанра. ";
    }

    public String execute(String genre, UserInputProvider inputProvider) {
        if (genre == null || genre.trim().isEmpty()) {
            genre = inputProvider.ask("Введите жанр для фильтрации: ");
        }

        if (genre == null || genre.trim().isEmpty()) {
            return "Ошибка: Жанр не может быть пустым.";
        }

        try {
            return collectionManager.filterGreaterThanGenre(genre.trim());
        } catch (Exception e) {
            return "Ошибка при фильтрации: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Выводит элементы, у которых значение жанра больше заданного по количеству.";
    }
}