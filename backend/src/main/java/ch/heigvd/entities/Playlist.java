package ch.heigvd.entities;

import java.util.List;

public record Playlist(Long id, String name, String description, String creatorName, List<Music> musics) {
  /**
   * Returns a new Playlist instance (as java records are meant to be unmutable)
   * with the specified creator name.
   *
   * @param creatorName the name of the creator to set
   * @return a new Playlist instance with the updated creator name
   */
  public Playlist setCreatorName(String creatorName) {
    return new Playlist(this.id, this.name, this.description, creatorName, this.musics);
  }
}
