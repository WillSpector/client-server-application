package commands;

import data.CollectionManager;
import commands.interfaces.CommandUseable;

/**
 * Абстрактный базовый класс для всех команд.
 */
public abstract class BaseCommand implements CommandUseable {

    protected final CollectionManager collectionManager;

    public BaseCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String executeWithOutput() {
        return "";
    }
}

