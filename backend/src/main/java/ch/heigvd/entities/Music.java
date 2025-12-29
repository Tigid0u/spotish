package ch.heigvd.entities;

import java.time.LocalDate;

/**
 * Music record representing a music entity with its attributes.
 *
 * @param musicId      the unique identifier of the music
 * @param title        the title of the music
 * @param releaseDate  the release date of the music
 * @param duration     the duration of the music in seconds
 * @param genre        the genre of the music
 * @param creatorNames the names of the creators associated with the music. If
 *                     multiple, they are separated by commas.
 */
public record Music(Long musicId, String title, LocalDate releaseDate, Integer duration, String genre,
    String creatorNames) {
}
