package ch.heigvd.entities;

import java.util.List;

public record Creator(String creatorName, List<String> artists, List<Album> albums) {
  /**
   * Returns a new Creator instance with the specified albums.
   *
   * @param albums the list of albums to set
   * @return a new Creator instance with the updated albums
   */
  public Creator setAlbums(List<Album> albums) {
    return new Creator(this.creatorName, this.artists, albums);
  }
}
