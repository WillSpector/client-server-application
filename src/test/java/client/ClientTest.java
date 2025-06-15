package client;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.mockito.Mockito.*;

class ClientTest {

    @Test
    void testStart_SuccessfulConnection() throws IOException {
        try (
                MockedStatic<UserInput> userInputMock = mockStatic(UserInput.class);
                MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)
        ) {
            // Вывод "test.json" от пользователя
            userInputMock.when(UserInput::readFileNameFromUser).thenReturn("test.json");

            connectionManagerMock.when(() -> ConnectionManager.connectToServer("test.json"))
                    .thenAnswer(invocation -> null);

            Client client = new Client();
            client.start();

            userInputMock.verify(UserInput::readFileNameFromUser);
            connectionManagerMock.verify(() -> ConnectionManager.connectToServer("test.json"));
        }
    }

    @Test
    void testStart_IOException() {
        try (
                MockedStatic<UserInput> userInputMock = mockStatic(UserInput.class);
                MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)
        ) {
            userInputMock.when(UserInput::readFileNameFromUser).thenReturn("test.json");

            connectionManagerMock.when(() -> ConnectionManager.connectToServer("test.json"))
                    .thenThrow(new IOException("Сервер недоступен"));

            Client client = new Client();
            client.start();

            // Проверка вызова
            userInputMock.verify(UserInput::readFileNameFromUser);
            connectionManagerMock.verify(() -> ConnectionManager.connectToServer("test.json"));
        }
    }
}
