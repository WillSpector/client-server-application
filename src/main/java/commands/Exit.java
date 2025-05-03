package commands;

import data.CollectionManager;

/**
 * Команда для завершения работы программы.
 */
public class Exit extends BaseCommand {

    private static final String EXIT_MESSAGE = "Завершение работы программы...";

    public Exit(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public String executeWithOutput() {
        return EXIT_MESSAGE;
    }

    @Override
    public String getDescription() {
        return "Завершает выполнение программы";
    }
}

