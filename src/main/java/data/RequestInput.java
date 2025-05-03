package data;

import java.io.Serializable;

public class RequestInput implements Serializable {
    private final String prompt;

    public RequestInput(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
