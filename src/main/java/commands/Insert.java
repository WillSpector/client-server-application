package commands;

import data.*;
import models.*;
import server.interfaces.UserInputProvider;

/**
 * Команда для добавления нового элемента в коллекцию.
 * Позволяет пользователю добавлять музыкальной группе в коллекцию.
 */
public class Insert extends BaseCommand {

    public Insert(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public void executeInsertWithParameters(String parameters) {
        try {
            // Разделение строки параметров на части
            String[] params = parameters.split(" ");

            // Проверка на достаточность параметров
            if (params.length != 7) {
                System.out.println("Ошибка: Недостаточно параметров для вставки. Требуется 7 параметров.");
                return;
            }
            // Удаляем внешние кавычки из каждого параметра
            for (int i = 0; i < params.length; i++) {
                // Убираем кавычки
                params[i] = params[i].trim().replaceAll("^\"|\"$", "");
            }

            // Извлечение значений из массива параметров
            String name = params[0];
            long x = Long.parseLong(params[1]);  // Координата X
            long y = Long.parseLong(params[2]);  // Координата Y

            // Обработка пустого значения для количества участников
            int numberOfParticipants = 0;  // Значение по умолчанию
            if (!params[3].isEmpty()) {
                try {
                    numberOfParticipants = Integer.parseInt(params[3]);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: Некорректный формат количества участников. Используйте целое число.");
                    return;
                }
            }

            MusicGenre genre = MusicGenre.valueOf(params[4].trim().toUpperCase()); // Жанр
            String studioName = params[5];  // Название студии
            String address = params[6];  // Адрес студии

            // Создание координат и студии
            Coordinates coordinates = new Coordinates(x, y);
            Studio studio = new Studio(studioName, address);

            // Создание музыкальной группы
            MusicBand musicBand = new MusicBand(name, coordinates, numberOfParticipants, genre, studio);

            // Добавление музыкальной группы в коллекцию
            collectionManager.addMusicBand(musicBand);
            collectionManager.save();
            System.out.println("Музыкальная группа успешно добавлена в коллекцию.");

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Некорректный формат числа. Пожалуйста, проверьте координаты и количество участников.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: Некорректный жанр. Пожалуйста, используйте один из следующих жанров: ROCK, PSYCHEDELIC_ROCK, PSYCHEDELIC_CLOUD_RAP, JAZZ.");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Возвращает информацию о процессе добавления нового элемента в коллекцию.
     * Используется для получения результата выполнения команды в виде строки.
     */
    public String executeWithOutput(String parameters) {
        try {
            String[] params = parameters.split(" ");

            // Проверка на достаточность параметров
            if (params.length != 7) {
                return "Ошибка: Недостаточно параметров для вставки. Требуется 7 параметров.";
            }

            for (int i = 0; i < params.length; i++) {
                params[i] = params[i].trim().replaceAll("^\"|\"$", "");
            }

            String name = params[0];
            long x = Long.parseLong(params[1]);
            long y = Long.parseLong(params[2]);
            int numberOfParticipants = 0;
            if (!params[3].isEmpty()) {
                try {
                    numberOfParticipants = Integer.parseInt(params[3]);
                } catch (NumberFormatException e) {
                    return "Ошибка: Некорректный формат количества участников.";
                }
            }

            MusicGenre genre = MusicGenre.valueOf(params[4].trim().toUpperCase());
            String studioName = params[5];
            String address = params[6];

            Coordinates coordinates = new Coordinates(x, y);
            Studio studio = new Studio(studioName, address);
            MusicBand musicBand = new MusicBand(name, coordinates, numberOfParticipants, genre, studio);
            collectionManager.addMusicBand(musicBand);
            collectionManager.save();

            return "Музыкальная группа успешно добавлена в коллекцию.";

        } catch (NumberFormatException e) {
            return "Ошибка: Некорректный формат числа.";
        } catch (IllegalArgumentException e) {
            return "Ошибка: Некорректный жанр.";
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }


    public void execute(UserInputProvider inputProvider) {
        try {
            String name = inputProvider.ask("Введите название группы:");

            long x = askLong(inputProvider);
            double y = askDouble(inputProvider);
            Coordinates coordinates = new Coordinates(x, y);

            int numberOfParticipants = askInt(inputProvider);

            MusicGenre genre = askGenre(inputProvider);

            String studioName = inputProvider.ask("Введите название студии:");
            String address = inputProvider.ask("Введите адрес студии:");
            Studio studio = new Studio(studioName, address);

            MusicBand musicBand = new MusicBand(name, coordinates, numberOfParticipants, genre, studio);
            collectionManager.addMusicBand(musicBand);
            collectionManager.save();

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении элемента: " + e.getMessage());
        }
    }

    private long askLong(UserInputProvider input) {
        while (true) {
            try {
                return Long.parseLong(input.ask("Введите координату X:"));
            } catch (NumberFormatException e) {
                input.showMessage("Ошибка: введите число. ");
            }
        }
    }

    private double askDouble(UserInputProvider input) {
        while (true) {
            try {
                return Double.parseDouble(input.ask("Введите координату Y:"));
            } catch (NumberFormatException e) {
                input.showMessage("Ошибка: введите число. ");
            }
        }
    }

    private int askInt(UserInputProvider input) {
        while (true) {
            try {
                return Integer.parseInt(input.ask("Введите количество участников (может быть 0): "));
            } catch (NumberFormatException e) {
                input.showMessage("Ошибка: введите число. ");
            }
        }
    }

    private MusicGenre askGenre(UserInputProvider input) {
        while (true) {
            try {
                return MusicGenre.valueOf(input.ask("Введите жанр (ROCK, PSYCHEDELIC_ROCK, PSYCHEDELIC_CLOUD_RAP, JAZZ):").trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                input.showMessage("Ошибка: неверный жанр. ");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Добавляет новый элемент в коллекцию.";
    }
}

