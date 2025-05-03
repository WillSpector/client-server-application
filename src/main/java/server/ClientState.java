package server;

public enum ClientState {
    WAITING_FOR_FILE_NAME,
    IDLE,
    WAITING_FOR_INPUT,
    EXECUTING;

    public String getDescription() {
        switch (this) {
            case WAITING_FOR_FILE_NAME: return "Ожидание имени файла";
            case IDLE: return "Неактивен";
            case WAITING_FOR_INPUT: return "Ожидание ввода";
            case EXECUTING: return "Выполнение задачи";
            default: return "Неизвестное состояние";
        }
    }
}
