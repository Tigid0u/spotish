package ch.heigvd.album;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ch.heigvd.entities.Album;
import ch.heigvd.entities.Music;

public class AlbumRepository {
  /**
   * Retrieves an album by its ID, including its musics.
   *
   * @param conn the database connection
   * @param id   the ID of the album to retrieve
   * @return the Album object with its musics, or null if not found
   * @throws SQLException if a database access error occurs
   */
  public Album getAlbum(Connection conn, Long id) throws SQLException {
    String sql = """
        SELECT a.idalbum AS albumId, m.titre AS title, m.datedesortie AS releaseDate,
               cm.nomcreateur AS creatorName
        FROM spotish.album a
        JOIN spotish.media m           ON a.idalbum = m.idmedia
        JOIN spotish.createur_media cm ON m.idmedia = cm.idmedia
        WHERE a.idalbum = ?;
            """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, id);
      ResultSet rs = ps.executeQuery();

      Album album = null;

      if (rs.next()) {
        album = new Album(
            rs.getLong("albumId"),
            rs.getString("title"),
            rs.getObject("releaseDate", LocalDate.class),
            rs.getString("creatorName"),
            null);
        // Retrieve musics of the album
        album = album.setMusics(getMusicsForAlbum(conn, album.id()));
      }
      return album;
    }
  }

  /**
   * Retrieves the list of musics for a given album ID.
   *
   * @param conn    the database connection
   * @param albumId the ID of the album
   * @return a list of Music objects associated with the album
   * @throws SQLException if a database access error occurs
   */
  private List<Music> getMusicsForAlbum(Connection conn, Long albumId) throws SQLException {
    String sql = """
        SELECT c.idchanson AS musicId, m.titre AS title, m.datedesortie AS releaseDate, c.duree AS duration, c.genre AS genre,
               STRING_AGG(DISTINCT cm.nomcreateur, ', ' ORDER BY cm.nomcreateur) AS creatorNames
        FROM spotish.album_chanson ac
        JOIN spotish.chanson c          ON ac.idchanson = c.idchanson
        JOIN spotish.media m            ON m.idmedia = c.idchanson
        JOIN spotish.createur_media cm  ON cm.idmedia = m.idmedia
        WHERE ac.idalbum = ?
        GROUP BY c.idchanson, m.titre, m.datedesortie, c.duree, c.genre;
                """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, albumId);
      ResultSet rs = ps.executeQuery();

      List<Music> musics = new ArrayList<>();

      while (rs.next()) {
        Music music = new Music(
            rs.getLong("musicId"),
            rs.getString("title"),
            rs.getObject("releaseDate", LocalDate.class),
            rs.getInt("duration"),
            rs.getString("genre"),
            rs.getString("creatorNames"));

        musics.add(music);
      }
      return musics;
    }
  }
}
