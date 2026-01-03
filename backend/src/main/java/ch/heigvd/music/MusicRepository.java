package ch.heigvd.music;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ch.heigvd.entities.Music;

public class MusicRepository {
  /**
   * Retrieves a music by its ID from the database.
   *
   * @param conn    the database connection
   * @param musicId the unique identifier of the music
   * @return the Music object corresponding to the given ID, or null if not found
   * @throws SQLException if a database access error occurs
   */
  public Music getOne(Connection conn, Long musicId) throws SQLException {
    // Got the help of ChatGPT for the STRING_AGG part. This part allows to
    // concatenate the creator names in case of a featuring.
    String sql = """
        SELECT c.idchanson as musicId, m.titre as title, m.datedesortie as releaseDate, c.duree as duration, c.genre as genre,
        STRING_AGG(DISTINCT cm.nomcreateur, ', ' ORDER BY cm.nomcreateur) AS creatorNames
        FROM spotish.media m
        INNER JOIN spotish.chanson c ON m.idmedia = c.idchanson
        INNER JOIN spotish.createur_media cm ON m.idmedia = cm.idmedia
        WHERE m.idmedia = ?
        GROUP BY c.idchanson, m.titre, m.datedesortie, c.duree, c.genre;
                        """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, musicId); // Autoboxing
      ResultSet rs = ps.executeQuery();

      Music music = null;
      if (rs.next()) {
        music = new Music(
            rs.getLong("musicId"),
            rs.getString("title"),
            rs.getObject("releaseDate", LocalDate.class),
            rs.getInt("duration"),
            rs.getString("genre"),
            rs.getString("creatorNames"));
      }
      return music;
    }

  }

  /**
   * Retrieves the ten last listened musics for a given user from the database.
   *
   * @param conn     the database connection
   * @param username the username of the user
   * @return a list of the ten last listened Music objects for the user
   * @throws SQLException if a database access error occurs
   */
  public List<Music> getTenLastListened(Connection conn, String username) throws SQLException {
    // Got the help of ChatGPT for the STRING_AGG part. Without this part, if
    // there's a featuring in the recently listened musics, there would be an entry
    // of the same music for each creator.
    String sql = """
        WITH last_listen AS (
          SELECT e.idchanson, MAX(e.dateheureecoute) AS lastListenedAt
          FROM spotish.ecoute e
          WHERE e.nomutilisateur = ?
          GROUP BY e.idchanson
        )
        SELECT
          c.idchanson AS musicId, m.titre AS title, m.datedesortie AS releaseDate, c.duree AS duration, c.genre AS genre,
          STRING_AGG(DISTINCT cm.nomcreateur, ', ' ORDER BY cm.nomcreateur) AS creatorNames
        FROM last_listen ll
        JOIN spotish.chanson c         ON c.idchanson = ll.idchanson
        JOIN spotish.media m           ON m.idmedia = c.idchanson
        JOIN spotish.createur_media cm ON cm.idmedia = m.idmedia
        GROUP BY c.idchanson, m.titre, m.datedesortie, c.duree, c.genre, ll.lastListenedAt
        ORDER BY ll.lastListenedAt DESC
        LIMIT 10;
            """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();

      List<Music> musics = new ArrayList<>();
      while (rs.next()) {
        musics.add(new Music(
            rs.getLong("musicId"),
            rs.getString("title"),
            rs.getObject("releaseDate", LocalDate.class),
            rs.getInt("duration"),
            rs.getString("genre"),
            rs.getString("creatorNames")));
      }
      return musics;
    }
  }

  /**
   * Retrieves the ten most listened musics for a given user from the database.
   *
   * @param conn     the database connection
   * @param username the username of the user
   * @return a list of the ten most listened Music objects for the user
   * @throws SQLException if a database access error occurs
   */
  public List<Music> getTenMostListened(Connection conn, String username) throws SQLException {
    String sql = """
        WITH most_listened_musics AS (SELECT e.idchanson, count(*) as nblistening
                                      FROM spotish.ecoute e
                                      WHERE e.nomutilisateur = ?
                                      GROUP BY e.idchanson
                                      ORDER BY count(e.idchanson) desc
                                      LIMIT 10)
        SELECT
          c.idchanson AS musicId, m.titre AS title, m.datedesortie AS releaseDate, c.duree AS duration, c.genre AS genre,
          STRING_AGG(DISTINCT cm.nomcreateur, ', ' ORDER BY cm.nomcreateur) AS creatorNames
        FROM most_listened_musics mlm
        JOIN spotish.chanson c         ON c.idchanson = mlm.idchanson
        JOIN spotish.media m           ON m.idmedia = c.idchanson
        JOIN spotish.createur_media cm ON cm.idmedia = m.idmedia
        GROUP BY c.idchanson, m.titre, m.datedesortie, c.duree, c.genre, mlm.nblistening;
                """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();

      List<Music> musics = new ArrayList<>();
      while (rs.next()) {
        musics.add(new Music(
            rs.getLong("musicId"),
            rs.getString("title"),
            rs.getObject("releaseDate", LocalDate.class),
            rs.getInt("duration"),
            rs.getString("genre"),
            rs.getString("creatorNames")));
      }
      return musics;
    }
  }

  /**
   * Retrieves all musics liked by a given user from the database.
   *
   * @param conn     the database connection
   * @param username the username of the user
   * @return a list of Music objects liked by the user
   * @throws SQLException if a database access error occurs
   */
  public List<Music> getLikedMusics(Connection conn, String username) throws SQLException {
    String sql = """
        SELECT  c.idchanson AS musicId, m.titre AS title, m.datedesortie AS releaseDate, c.duree AS duration, c.genre AS genre,
          STRING_AGG(DISTINCT cm.nomcreateur, ', ' ORDER BY cm.nomcreateur) AS creatorNames
        FROM spotish.utilisateur_aime_chanson uac
        JOIN spotish.chanson c         ON c.idchanson = uac.idchanson
        JOIN spotish.media m           ON m.idmedia = c.idchanson
        JOIN spotish.createur_media cm ON cm.idmedia = m.idmedia
        WHERE uac.nomutilisateur = 'amelie.paris'
        GROUP BY c.idchanson, m.titre, m.datedesortie, c.duree, c.genre;
                        """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();

      List<Music> musics = new ArrayList<>();
      while (rs.next()) {
        musics.add(new Music(
            rs.getLong("musicId"),
            rs.getString("title"),
            rs.getObject("releaseDate", LocalDate.class),
            rs.getInt("duration"),
            rs.getString("genre"),
            rs.getString("creatorNames")));
      }
      return musics;
    }
  }

  /**
   * Marks a music as liked by a given user in the database.
   *
   * @param conn     the database connection
   * @param username the username of the user
   * @param musicId  the unique identifier of the music
   * @return the number of rows affected
   * @throws SQLException if a database access error occurs
   */
  public int likeMusic(Connection conn, String username, Long musicId) throws SQLException {
    String sql = """
        INSERT INTO spotish.utilisateur_aime_chanson (nomutilisateur, idchanson)
        VALUES (?, ?);
                """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ps.setLong(2, musicId);
      return ps.executeUpdate();
    }
  }

  /**
   * Checks if a music is liked by a given user in the database.
   *
   * @param conn     the database connection
   * @param username the username of the user
   * @param musicId  the unique identifier of the music
   * @return true if the music is liked by the user, false otherwise
   * @throws SQLException if a database access error occurs
   */
  public boolean isMusicLikedByUser(Connection conn, String username, Long musicId) throws SQLException {
    String sql = """
        SELECT COUNT(*) AS count
        FROM spotish.utilisateur_aime_chanson
        WHERE nomutilisateur = ? AND idchanson = ?;
                """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ps.setLong(2, musicId);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getInt("count") > 0;
      } else {
        return false;
      }
    }
  }
}
