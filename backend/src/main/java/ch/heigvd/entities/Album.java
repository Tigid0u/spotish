package ch.heigvd.entities;

import java.time.LocalDate;
import java.util.List;

public record Album(Long id, String title, LocalDate releaseDate, String creatorName, List<Music> musics) {
  public Album setMusics(List<Music> musics) {
    return new Album(this.id, this.title, this.releaseDate, this.creatorName, musics);
  }
}
