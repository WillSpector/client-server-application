package client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class CommandHandlerTest {

    private SocketChannel mockChannel;
    private Selector mockSelector;
    private CommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        mockChannel = Mockito.mock(SocketChannel.class);
        mockSelector = Mockito.mock(Selector.class);
        commandHandler = new CommandHandler(mockChannel, mockSelector);
    }

    @Test
    void testSendMessage() throws Exception {
        String message = "test command";
        ByteBuffer sentBuffer = StandardCharsets.UTF_8.encode(message + "\n");

        Mockito.when(mockChannel.write(Mockito.any(ByteBuffer.class)))
                .thenAnswer(invocation -> {
                    ByteBuffer buffer = invocation.getArgument(0);
                    buffer.position(buffer.limit()); // Эмулируем полную запись
                    return sentBuffer.limit();
                });

        Method sendMessage = CommandHandler.class.getDeclaredMethod("sendMessage", String.class);
        sendMessage.setAccessible(true);
        sendMessage.invoke(commandHandler, message);

        Mockito.verify(mockChannel, Mockito.atLeastOnce()).write(Mockito.any(ByteBuffer.class));
    }

    @Test
    void testReadServerResponse() throws Exception {
        String fullResponse = "Server message __END__";
        ByteBuffer buffer = ByteBuffer.wrap(fullResponse.getBytes(StandardCharsets.UTF_8));

        Mockito.when(mockChannel.read(Mockito.any(ByteBuffer.class)))
                .thenAnswer(invocation -> {
                    ByteBuffer target = invocation.getArgument(0);
                    target.put(buffer);
                    return fullResponse.length();
                });

        Method readServerResponse = CommandHandler.class.getDeclaredMethod("readServerResponse");
        readServerResponse.setAccessible(true);
        String result = (String) readServerResponse.invoke(commandHandler);

        assertEquals("Server message", result.trim());
    }
}
