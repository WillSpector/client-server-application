package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.MusicBand;
import models.MusicGenre;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Управляет коллекцией музыкальных групп.
 * Отвечает за загрузку, сохранения, добавления и фильтрации элементов.
 */
public class CollectionManager {
    private final TreeMap<Integer, MusicBand> collection = new TreeMap<>();
    private final Date initializationDate;
    private String fileName;

    public CollectionManager(String fileName) {
        this.fileName = fileName;
        this.initializationDate = new Date(); // Устанавливаем дату инициализации при создании
        ensureFileExists();
        loadFromFile();
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
        ensureFileExists();  // Проверка существования файла
        loadFromFile();  // Загрузка коллекции из нового файла
    }


    /**
     * Проверяет существование файла и создает его, если он отсутствует.
     */
    private void ensureFileExists() {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Файл " + fileName + " был создан.");
            } catch (IOException e) {
                System.out.println("Ошибка при создании файла: " + e.getMessage());
            }
        }
    }

    /**
     * Добавляет новую музыкальную группу в коллекцию.
     */
    public void addMusicBand(MusicBand band) {
        int newId = collection.keySet().stream().max(Integer::compare).orElse(0) + 1;
        band.setId(newId);
        collection.put(newId, band);
    }

    /**
     * Сохраняет коллекцию в файл.
     */
    public void save() {
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new Gson();
            gson.toJson(collection, writer);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    /**
     * Удаляет элемент из коллекции по ключу.
     */
    public boolean removeByKey(int key) {
        if (collection.containsKey(key)) {
            collection.remove(key);
            return true;
        }
        return false;
    }


    /**
     * Загружает коллекцию музыкальных групп из файла.
     */
    public void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, MusicBand>>() {
            }.getType();
            Map<String, MusicBand> tempMap = gson.fromJson(reader, type);

            if (tempMap != null) {
                for (Map.Entry<String, MusicBand> entry : tempMap.entrySet()) {
                    try {
                        int key = Integer.parseInt(entry.getKey());
                        collection.put(key, entry.getValue());
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка преобразования ключа: " + entry.getKey());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
        }
    }

    /**
     * Фильтрует коллекцию по названию студии.
     */
    public String filterByStudio(String studio) {
        StringBuilder result = new StringBuilder();

        List<MusicBand> filteredBands = collection.values().stream()
                .filter(band -> band.getStudio() != null &&
                        band.getStudio().getStudioName().equalsIgnoreCase(studio.trim()))
                .toList();

        if (filteredBands.isEmpty()) {
            result.append("Нет групп, записанных в этой студии.");
        } else {
            filteredBands.forEach(band -> result.append(formatMusicBand(band)).append("\n"));
        }

        return result.toString().trim();
    }


    /**
     * Информация о музыкальной группе для вывода.
     */
    private String formatMusicBand(MusicBand band) {
        return "ID = " + band.getId() + "\n" +
                "Name = " + band.getName() + "\n" +
                "Coordinates: X = " + band.getCoordinates() +
                "  Y = " + band.getCoordinates() + " ;\n" +
                "Creation Date = " + band.getCreationDate() + "\n" +
                "Number of participants = " + band.getNumberOfParticipants() + "\n" +
                "Music genre = " + band.getGenre() + "\n" +
                "Studio name = " + band.getStudio().getStudioName() +
                "  Studio address = " + band.getStudio().getAddress() + "\n";
    }

    /**
     * Фильтрует коллекцию, выводит жанры, которых больше по количеству
     *
     * @return
     */
    public String filterGreaterThanGenre(String genre) {
        StringBuilder result = new StringBuilder();

        try {
            // Преобразуем строку в объект MusicGenre
            MusicGenre inputGenre = MusicGenre.valueOf(genre.toUpperCase());

            // Фильтруем коллекцию по жанру
            List<MusicBand> filteredBands = collection.values().stream()
                    .filter(band -> band.getGenre() != null)  // Убираем группы без жанра
                    .filter(band -> band.getGenre().compareTo(inputGenre) > 0)  // Сравниваем жанры
                    .toList();

            // Проверяем, есть ли подходящие группы
            if (filteredBands.isEmpty()) {
                result.append("Нет групп с жанром больше, чем \"" + genre + "\".");
            } else {
                filteredBands.forEach(band -> result.append(formatMusicBand(band)).append("\n"));
            }

        } catch (IllegalArgumentException e) {
            result.append("Ошибка: жанр \"" + genre + "\" не найден. Доступные жанры: " + Arrays.toString(MusicGenre.values()));
        }

        return result.toString().trim();
    }


    //Проверяет, содержит ли коллекция элемент с заданным ключом.
    public boolean containsKey(int key) {
        return collection.containsKey(key);
    }

    // Получает музыкальную группу по ключу
    public MusicBand getByKey(int key) {
        return collection.get(key);
    }

    // Очистка коллекции
    public void clear() {
        collection.clear();
    }

    // Получение коллекции
    public TreeMap<Integer, MusicBand> getCollection() {
        return collection;
    }

    // Получение даты создания
    public Date getInitializationDate() {
        return initializationDate;
    }
}
