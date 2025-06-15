package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientStateTest {

    @Test
    public void testGetDescription_waitingForFileName() {
        assertEquals("Ожидание имени файла", ClientState.WAITING_FOR_FILE_NAME.getDescription());
    }

    @Test
    public void testGetDescription_idle() {
        assertEquals("Неактивен", ClientState.IDLE.getDescription());
    }

    @Test
    public void testGetDescription_waitingForInput() {
        assertEquals("Ожидание ввода", ClientState.WAITING_FOR_INPUT.getDescription());
    }

    @Test
    public void testGetDescription_executing() {
        assertEquals("Выполнение задачи", ClientState.EXECUTING.getDescription());
    }
}
