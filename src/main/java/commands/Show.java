package commands;

import data.CollectionManager;

/**
 * Команда для вывода всех элементов коллекции.
 */
public class Show extends BaseCommand {

    public Show(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public String executeWithOutput() {
        StringBuilder result = new StringBuilder();

        if (collectionManager.getCollection().isEmpty()) {
            result.append("Коллекция пуста.");
        } else {
            collectionManager.getCollection().forEach((id, band) -> {
                result.append("ID = ").append(band.getId()).append("\n")
                        .append("Name = ").append(band.getName()).append("\n")
                        .append(band.getCoordinates()).append("\n")
                        .append("Creation Date = ").append(band.getCreationDate()).append("\n")
                        .append("Number of participants = ").append(band.getNumberOfParticipants()).append("\n")
                        .append("Music genre = ").append(band.getGenre()).append("\n")
                        .append(band.getStudio()).append("\n\n");
            });
        }

        return result.toString().trim();
    }

    @Override
    public String getDescription() {
        return "Выводит все элементы коллекции.";
    }
}
