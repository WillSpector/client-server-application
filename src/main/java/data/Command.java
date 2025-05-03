package data;

import java.io.Serial;
import java.io.Serializable;

public class Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private String argument;
    private final boolean requiresInput; // Флаг для проверки ввода

    public Command(String name, String argument, boolean requiresInput) {
        this.name = name;
        this.argument = argument;
        this.requiresInput = requiresInput;
    }

    public String getName() {
        return name;
    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public boolean requiresInput() {
        return requiresInput;
    }

    public String asFullInput() {
        return argument == null || argument.isBlank()
                ? name
                : name + " " + argument;
    }
}

