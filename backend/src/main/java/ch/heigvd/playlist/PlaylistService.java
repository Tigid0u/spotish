package ch.heigvd.playlist;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import ch.heigvd.entities.Playlist;
import io.javalin.http.ConflictResponse;

public class PlaylistService {
  private final PlaylistRepository playlistRepo;
  private final DataSource ds;

  public PlaylistService(DataSource ds, PlaylistRepository playlistRepo) {
    this.ds = ds;
    this.playlistRepo = playlistRepo;
  }

  /**
   * Insert a new playlist into the database.
   * 
   * @param playlist The playlist to insert.
   * @throws ConflictResponse if a playlist with the same ID already exists.
   */
  public void createPlaylist(Playlist playlist) {
    try (Connection conn = ds.getConnection()) {
      // Check if playlist with the same ID already exists before inserting
      if (playlistRepo.exists(conn, playlist.id())) {
        throw new ConflictResponse("Playlist with ID " + playlist.id() + " already exists");
      }

      playlistRepo.createPlaylist(conn, playlist);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
