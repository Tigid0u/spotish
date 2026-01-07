package ch.heigvd.album;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import ch.heigvd.entities.Album;
import io.javalin.http.NotFoundResponse;

public class AlbumService {
  private final DataSource ds;
  private final AlbumRepository albumRepository;

  public AlbumService(DataSource ds, AlbumRepository albumRepository) {
    this.ds = ds;
    this.albumRepository = albumRepository;
  }

  /**
   * Retrieves an album by its ID.
   *
   * @param id the ID of the album to retrieve
   * @return the Album object
   * @throws NotFoundResponse if the album is not found
   * @throws RuntimeException if a database access error occurs
   */
  public Album getAlbum(Long id) {
    try (Connection conn = ds.getConnection()) {
      Album album = albumRepository.getAlbum(conn, id);

      if (album == null) {
        throw new NotFoundResponse("Album with id " + id + " not found");
      }

      return album;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
