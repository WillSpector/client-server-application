package commands;

import data.CollectionManager;
import models.MusicBand;
import server.interfaces.UserInputProvider;

/**
 * Команда замены количества участников группы, если новое значение меньше текущего.
 * Пользователь вводит ключ группы и новое количество участников.
 */

public class ReplaceIfLower extends BaseCommand {

    public ReplaceIfLower(CollectionManager collectionManager) {
        super(collectionManager);
    }


    public String execute(String parameters, UserInputProvider inputProvider) {
        try {
            MusicBand band = getBandFromParameters(parameters, inputProvider);
            if (band == null) return "Ошибка: Группа не найдена.";

            int newParticipants = askInt(inputProvider);
            if (newParticipants < band.getNumberOfParticipants()) {
                band.setNumberOfParticipants(newParticipants);
                collectionManager.save();
                return "Группа обновлена: новое количество участников = " + newParticipants;
            } else {
                return "Новое количество участников не меньше текущего. Обновление не выполнено.";
            }
        } catch (NumberFormatException e) {
            return "Ошибка: введите целое число.";
        } catch (Exception e) {
            return "Ошибка при выполнении команды replace_if_lower: " + e.getMessage();
        }
    }

    private MusicBand getBandFromParameters(String parameters, UserInputProvider inputProvider) {
        int id;
        if (parameters.isBlank()) {
            id = askValidKey(inputProvider);
        } else {
            try {
                id = Integer.parseInt(parameters.trim());
            } catch (NumberFormatException e) {
                inputProvider.showMessage("Ошибка: ключ должен быть целым числом.");
                return null;
            }
        }

        MusicBand band = collectionManager.getByKey(id);
        if (band == null) {
            inputProvider.showMessage("Группа с ключом " + id + " не найдена.");
        }
        return band;
    }

    /**
     * Запрашивает у пользователя действительный ключ (ID группы).
     */
    private int askValidKey(UserInputProvider inputProvider) {
        while (true) {
            try {
                int key = Integer.parseInt(inputProvider.ask("Введите ключ (ID группы): "));
                if (collectionManager.containsKey(key)) {
                    return key;
                } else {
                    inputProvider.showMessage("Группа с ключом " + key + " не найдена.");
                }
            } catch (NumberFormatException e) {
                inputProvider.showMessage("Ошибка: ключ должен быть целым числом.");
            }
        }
    }

    /**
     * Запрашивает у пользователя ввод целого числа.
     */
    private int askInt(UserInputProvider inputProvider) {
        while (true) {
            try {
                return Integer.parseInt(inputProvider.ask("Введите новое количество участников: "));
            } catch (NumberFormatException e) {
                inputProvider.showMessage("Ошибка: введите целое число.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Заменяет количество участников, если оно меньше текущего.";
    }
}