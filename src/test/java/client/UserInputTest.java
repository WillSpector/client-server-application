package client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserInputTest {

    private final InputStream originalIn = System.in;

    @BeforeEach
    public void setUpStreams() {
        // Ничего не требуется здесь, установка потока происходит в самих тестах
    }

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalIn); // возвращаем стандартный ввод
        UserInput.resetReader();  // сбрасываем reader обратно на System.in
    }

    @Test
    public void testReadFileNameFromUser_EmptyInput() throws IOException {
        String simulatedInput = "\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        UserInput.resetReader();

        String result = UserInput.readFileNameFromUser();
        assertEquals("collection.json", result);
    }

    @Test
    public void testReadFileNameFromUser_NoExtension() throws IOException {
        String simulatedInput = "mydata\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        UserInput.resetReader();

        String result = UserInput.readFileNameFromUser();
        assertEquals("mydata.json", result);
    }

    @Test
    public void testReadLine_ReturnsTrimmed() throws IOException {
        String simulatedInput = "   hello world   \n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        UserInput.resetReader();

        String result = UserInput.readLine("Prompt: ");
        assertEquals("hello world", result);
    }

    @Test
    public void testReadCommand_EmptyThenValid() throws IOException {
        String simulatedInput = "\n   \nshow\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        UserInput.resetReader();

        String result = UserInput.readCommand();
        assertEquals("show", result);
    }

    @Test
    public void testReadCommand_EOF() throws IOException {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        UserInput.resetReader();

        String result = UserInput.readCommand();
        assertEquals("exit", result);
    }
}

