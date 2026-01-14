package ch.heigvd.playlist;

import ch.heigvd.user.UserRepository;
import ch.heigvd.music.MusicRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import ch.heigvd.entities.Playlist;
import io.javalin.http.ConflictResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;

public class PlaylistService {
  private final PlaylistRepository playlistRepo;
  private final UserRepository userRepo;
  private final MusicRepository musicRepo;
  private final DataSource ds;

  public PlaylistService(DataSource ds, PlaylistRepository playlistRepo, UserRepository userRepo,
      MusicRepository musicRepo) {
    this.ds = ds;
    this.playlistRepo = playlistRepo;
    this.userRepo = userRepo;
    this.musicRepo = musicRepo;
  }

  /**
   * Insert a new playlist into the database.
   * 
   * @param playlist The playlist to insert.
   * @throws ConflictResponse if a playlist with the same ID already exists.
   */
  public Long createPlaylist(Playlist playlist) {
    try (Connection conn = ds.getConnection()) {
      // Check if playlist with the same ID already exists before inserting
      if (playlist.id() != null && playlistRepo.exists(conn, playlist.id())) {
        throw new ConflictResponse("Playlist with ID " + playlist.id() + " already exists");
      }

      return playlistRepo.createPlaylist(conn, playlist);
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

  /**
   * Get all playlists followed by a specific user.
   * 
   * @param username The name of the user.
   * @return A list of playlists followed by the specified user.
   * @throws NotFoundResponse if no followed playlists are found for the given
   *                          user.
   */
  public List<Playlist> getFollowedPlaylists(String username) {
    try (Connection conn = ds.getConnection()) {
      // Check if user exists first (technically shouldn't be necessary if called
      // after authentication but if we scale this to apply to every user it might be
      // useful)
      if (!userRepo.exists(conn, username)) {
        throw new NotFoundResponse("User " + username + " does not exist");
      }

      List<Playlist> playlists = playlistRepo.getAllFollowedPlaylists(conn, username);

      if (playlists.isEmpty()) {
        throw new NotFoundResponse("No followed playlists found for user " + username);
      }

      return playlists;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Make the authenticated user follow a playlist.
   * 
   * @param username   The name of the user.
   * @param playlistId The unique identifier of the playlist.
   * @throws NotFoundResponse if the user or playlist does not exist.
   */
  public void followPlaylist(String username, Long playlistId) {
    try (Connection conn = ds.getConnection()) {
      // No need to check if user exists here since this is done during authentication
      // Check if playlist exists
      if (!playlistRepo.exists(conn, playlistId)) {
        throw new NotFoundResponse("Playlist with ID " + playlistId + " does not exist");
      }

      // Check if user already follows the playlist
      if (playlistRepo.isFollowingPlaylist(conn, username, playlistId)) {
        return; // No action needed if already following
      }

      playlistRepo.followPlaylist(conn, username, playlistId);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Add a music to a playlist.
   * 
   * @param username   The name of the user adding the music.
   * @param playlistId The unique identifier of the playlist.
   * @param musicId    The unique identifier of the music.
   * @throws NotFoundResponse     if the music or playlist does not exist.
   * @throws UnauthorizedResponse if the user is not the creator of the playlist.
   */
  public void addMusicToPlaylist(String username, Long playlistId, Long musicId) {
    try (Connection conn = ds.getConnection()) {
      // Check if music exists
      if (!musicRepo.exists(conn, musicId)) {
        throw new NotFoundResponse("Music with ID " + musicId + " does not exist");
      }

      Playlist playlist = playlistRepo.getPlaylist(conn, playlistId);

      // Check if playlist exists
      if (playlist == null) {
        throw new NotFoundResponse("Playlist with ID " + playlistId + " does not exist");
      }
      // Check if user is the creator of the playlist
      if (!playlist.creatorName().equals(username)) {
        throw new UnauthorizedResponse("User " + username + " is not the creator of playlist " + playlistId);
      }

      // Add music to playlist
      playlistRepo.addMusicToPlaylist(conn, musicId, playlistId);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Remove a music from a playlist.
   * 
   * @param username   The name of the user removing the music.
   * @param playlistId The unique identifier of the playlist.
   * @param musicId    The unique identifier of the music.
   * @throws NotFoundResponse     if the playlist does not exist.
   * @throws UnauthorizedResponse if the user is not the creator of the playlist.
   */
  public void removeMusicFromPlaylist(String username, Long playlistId, Long musicId) {
    try (Connection conn = ds.getConnection()) {
      Playlist playlist = playlistRepo.getPlaylist(conn, playlistId);

      // Check if playlist exists
      if (playlist == null) {
        throw new NotFoundResponse("Playlist with ID " + playlistId + " does not exist");
      }
      // Check if user is the creator of the playlist
      if (!playlist.creatorName().equals(username)) {
        throw new UnauthorizedResponse("User " + username + " is not the creator of playlist " + playlistId);
      }

      // Remove music from playlist
      playlistRepo.deleteMusicFromPlaylist(conn, musicId, playlistId);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
