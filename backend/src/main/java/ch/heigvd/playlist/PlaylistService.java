package ch.heigvd.playlist;

import ch.heigvd.user.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import ch.heigvd.entities.Playlist;
import io.javalin.http.ConflictResponse;
import io.javalin.http.NotFoundResponse;

public class PlaylistService {
  private final PlaylistRepository playlistRepo;
  private final UserRepository userRepo;
  private final DataSource ds;

  public PlaylistService(DataSource ds, PlaylistRepository playlistRepo, UserRepository userRepo) {
    this.ds = ds;
    this.playlistRepo = playlistRepo;
    this.userRepo = userRepo;
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

  /**
   * Get a playlist by its ID.
   * 
   * @param playlistId The unique identifier of the playlist.
   * @return The playlist with the given ID.
   * @throws NotFoundResponse if the playlist with the given ID does not exist.
   */
  public Playlist getPlaylist(Long playlistId) {
    try (Connection conn = ds.getConnection()) {
      Playlist playlist = playlistRepo.getPlaylist(conn, playlistId);
      if (playlist == null) {
        throw new NotFoundResponse("Playlist with ID " + playlistId + " not found");
      }
      return playlist;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get all playlists created by a specific user.
   * 
   * @param creatorName The name of the creator.
   * @return A list of playlists created by the specified user.
   * @throws NotFoundResponse if no playlists are found for the given user.
   */
  public List<Playlist> getUserPlaylists(String creatorName) {
    try (Connection conn = ds.getConnection()) {
      // Check if user exists first
      if (!userRepo.exists(conn, creatorName)) {
        throw new NotFoundResponse("User " + creatorName + " does not exist");
      }

      List<Playlist> playlists = playlistRepo.getUserPlaylists(conn, creatorName);

      if (playlists.isEmpty()) {
        throw new NotFoundResponse("No playlists found for user " + creatorName);
      }

      return playlists;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
