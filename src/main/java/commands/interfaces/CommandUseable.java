package commands.interfaces;

/**
 * Интерфейс, представляющий исполняемую команду.
 * Определяет методы для выполнения команды и получения её описания.
 */
public interface CommandUseable {


    default String executeWithOutput() {
        return "";
    }

    String getDescription();
}

