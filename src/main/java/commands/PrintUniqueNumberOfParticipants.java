package commands;

import data.CollectionManager;
import models.MusicBand;

import java.util.*;

/**
 * Команда для вывода списка групп с уникальным количеством участников.
 * Показывает количество участников в музыкальных группах имеют.
 */
public class PrintUniqueNumberOfParticipants extends BaseCommand {
    public PrintUniqueNumberOfParticipants(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public String executeWithOutput() {
        Map<Integer, MusicBand> bands = collectionManager.getCollection();
        if (bands.isEmpty()) {
            return "Коллекция пуста.";
        }

        Set<Integer> uniqueNumbers = new HashSet<>();
        StringBuilder result = new StringBuilder();
        result.append("Уникальное количество участников и их группы:\n");

        for (MusicBand band : bands.values()) {
            Integer participants = band.getNumberOfParticipants();
            if (uniqueNumbers.add(participants)) {
                result.append("Группа: ").append(band.getName()).append(" | Количество участников: ").append(participants).append("\n");
            }
        }

        return result.toString();
    }

    @Override
    public String getDescription() {
        return "Показывает список групп с количеством участников.";
    }
}

