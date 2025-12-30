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
        throw new NotFoundResponse("Music with id  \"" + musicId + "\" not found");
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
}
