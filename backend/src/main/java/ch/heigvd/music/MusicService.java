package ch.heigvd.music;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import ch.heigvd.entities.Music;
import io.javalin.http.NotFoundResponse;

public class MusicService {
  private final MusicRepository musicRepo;
  private final DataSource ds;

  public MusicService(DataSource ds, MusicRepository musicRepo) {
    this.ds = ds;
    this.musicRepo = musicRepo;
  }

    /**
     * Retrieves all musics from the database.
     *
     * @return a list of all Music objects
     * @throws NotFoundResponse if no musics are found in the database
     */
  public List<Music> getAllMusics() {
      try (Connection conn = ds.getConnection()) {
        List<Music> musics = musicRepo.getAll(conn);

        if (musics == null || musics.isEmpty()) {
          throw new NotFoundResponse("No musics found in the database");
        }

        return musics;
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

  /**
   * Retrieves a music by its ID.
   *
   * @param musicId the unique identifier of the music
   * @return the Music object corresponding to the given ID
   * @throws NotFoundResponse if no music with the given ID is found
   */
  public Music getMusic(Long musicId) {
    try (Connection conn = ds.getConnection()) {
      Music music = musicRepo.getOne(conn, musicId);

      if (music == null) {
        throw new NotFoundResponse("Music with id \"" + musicId + "\" not found");
      }

      return music;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves the ten last listened musics for a given user.
   *
   * @param username the username of the user
   * @return a list of the ten last listened Music objects for the user
   * @throws NotFoundResponse if the user has not listened to any musics
   */
  public List<Music> getTenLastListenedMusics(String username) {
    try (Connection conn = ds.getConnection()) {
      List<Music> musics = musicRepo.getTenLastListened(conn, username);

      if (musics == null || musics.isEmpty()) {
        throw new NotFoundResponse("No listened musics found for user \"" + username + "\"");
      }

      return musics;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves the ten most listened musics for a given user.
   *
   * @param username the username of the user
   * @return a list of the ten most listened Music objects for the user
   * @throws NotFoundResponse if the user has not listened to any musics
   */
  public List<Music> getTenMostListenedMusics(String username) {
    try (Connection conn = ds.getConnection()) {
      List<Music> musics = musicRepo.getTenMostListened(conn, username);

      if (musics == null || musics.isEmpty()) {
        throw new NotFoundResponse("No listened musics found for user \"" + username + "\"");
      }

      return musics;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves all liked musics for a given user.
   *
   * @param username the username of the user
   * @return a list of liked Music objects for the user
   * @throws NotFoundResponse if the user has not liked any musics
   */
  public List<Music> getLikedMusics(String username) {
    try (Connection conn = ds.getConnection()) {
      List<Music> musics = musicRepo.getLikedMusics(conn, username);

      if (musics == null || musics.isEmpty()) {
        throw new NotFoundResponse("No liked musics found for user \"" + username + "\"");
      }

      return musics;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Likes a music for a given user.
   *
   * @param username the username of the user
   * @param musicId  the unique identifier of the music to be liked
   * @throws NotFoundResponse if no music with the given ID is found
   */
  public void likeMusic(String username, Long musicId) {
    try (Connection conn = ds.getConnection()) {
      Music music = musicRepo.getOne(conn, musicId);

      // Check if the music exists
      if (music == null) {
        throw new NotFoundResponse("Music with id \"" + musicId + "\" not found");
      }
      // Check if the music is already liked by the user to avoid errors
      if (!musicRepo.isMusicLikedByUser(conn, username, musicId)) {
        musicRepo.likeMusic(conn, username, musicId);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
