package models;


/**
 * Содержит доступные музыкальные жанры.
 * Используется для классификации музыкальных групп по жанру.
 */
public enum MusicGenre {
    ROCK("Рок"),
    PSYCHEDELIC_ROCK("Психоделический рок"),
    PSYCHEDELIC_CLOUD_RAP("Психоделический облачный рэп"),
    JAZZ("Джаз");

    private final String description;

    MusicGenre(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}


