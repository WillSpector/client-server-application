package models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Класс, описывающий музыкальную группу.
 * Поля класса:
 * id: не может быть null, должно быть больше 0, уникально и генерируется автоматически.
 * Name: не может быть null или пустой строкой.
 * Coordinates: не может быть null.
 * creationDate: не может быть null, генерируется автоматически при создании объекта.
 * numberOfParticipants: может быть null, но если задано, то должно быть больше 0.
 * genre: не может быть null.
 * Studio: не может быть null.
 */
public class MusicBand implements Comparable<MusicBand>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Статическое поле для автоматической генерации уникального id
    private static int idCounter = 1;

    private Integer id;
    private final String name;
    private final Coordinates coordinates;
    private final Date creationDate;
    private Integer numberOfParticipants;
    private final MusicGenre genre;
    private final Studio studio;

    public MusicBand(String name, Coordinates coordinates, int numberOfParticipants, MusicGenre genre, Studio studio) {
        // Проверка поля name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название не может быть null или пустым.");
        }
        // Проверка поля coordinates
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут быть null.");
        }
        // Проверка поля numberOfParticipants (если указано)
        if (numberOfParticipants < 0) {
            throw new IllegalArgumentException("Количество участников должно быть больше 0.");
        }
        // Проверка поля genre
        if (genre == null) {
            throw new IllegalArgumentException("Жанр не может быть null.");
        }
        // Проверка поля studio
        if (studio == null) {
            throw new IllegalArgumentException("Студия не может быть null.");
        }

        // Инициализация полей
        this.id = idCounter++; // автоматическая генерация уникального id
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = new Date(); // установка текущей даты
        this.numberOfParticipants = numberOfParticipants;
        this.genre = genre;
        this.studio = studio;
    }

    // Геттеры

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public Studio getStudio() {
        return studio;
    }

    // Сеттеры

    public void setNumberOfParticipants(Integer numberOfParticipants) {
        if (numberOfParticipants != null && numberOfParticipants <= 0) {
            throw new IllegalArgumentException("Количество участников должно быть больше 0.");
        }
        this.numberOfParticipants = numberOfParticipants;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MusicBand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", numberOfParticipants=" + numberOfParticipants +
                ", genre=" + genre +
                ", studio=" + studio +
                '}';
    }

    @Override
    public int compareTo(MusicBand o) {
        return this.name.compareTo(o.name);
    }
}
