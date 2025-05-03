package server.interfaces;

public interface UserInputProvider {
    String ask(String prompt);
    void showMessage(String message); // новый метод для сообщений

}

