package models;

import java.io.Serial;
import java.io.Serializable;

/**
 * Представляет собой координаты с двумя значениями: X и Y.
 * Используется для хранения географической или иной информации о позиции объекта.
 */


public class Coordinates implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Long x;
    private final double y;
    private static final long MAX_X = 783;

    public Coordinates(Long x, double y) {
        if (x > MAX_X) {
            throw new IllegalArgumentException("Ошибка: Координата X не может быть больше 783.");
        }
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates: X = " + x + " Y = " + y + ";";
    }
}
