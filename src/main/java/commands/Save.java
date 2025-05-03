package commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import data.CollectionManager;
import models.MusicBand;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * Команда для сохранения коллекции в файл.
 */
public class Save extends BaseCommand {
    private final String fileName;

    public Save(String fileName, CollectionManager collectionManager) {
        super(collectionManager);
        this.fileName = fileName;
    }

    public void execute() {
        try {
            File file = new File(fileName);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Map<Integer, MusicBand> existingData = new HashMap<>();

            // Загружаем существующие данные из файла
            if (file.exists() && file.length() > 0) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Type type = new TypeToken<Map<Integer, MusicBand>>() {
                }.getType();
                Map<Integer, MusicBand> oldData = gson.fromJson(reader, type);
                reader.close();

                if (oldData != null) {
                    existingData.putAll(oldData);
                }
            }

            // Добавляем новые элементы, но не изменяем уже существующие ключи
            for (Map.Entry<Integer, MusicBand> entry : collectionManager.getCollection().entrySet()) {
                if (!existingData.containsKey(entry.getKey())) {
                    existingData.put(entry.getKey(), entry.getValue());
                }
            }

            // Перезаписываем файл с обновленными данными
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            gson.toJson(existingData, writer);
            writer.close();

            System.out.println("Данные успешно обновлены и сохранены в файл.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    @Override
    public String executeWithOutput() {
        this.execute();
        return "Коллекция успешно сохранена.";
    }


    @Override
    public String getDescription() {
        return "Сохраняет коллекцию в файл.";
    }
}
