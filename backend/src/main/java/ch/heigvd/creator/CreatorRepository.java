package ch.heigvd.creator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ch.heigvd.entities.Album;
import ch.heigvd.entities.Music;

public class CreatorRepository {

  /**
   * Retrieves the list of albums for a given creator.
   *
   * @param conn        the database connection
   * @param creatorName the name of the creator
   * @return a list of Album objects associated with the creator
   * @throws SQLException if a database access error occurs
   */
  public List<Album> getAlbumsOfCreator(Connection conn, String creatorName) throws SQLException {
    String sql = """
        SELECT a.idalbum AS albumId, m.titre AS title, cm.nomcreateur as
             creatorName, m.datedesortie AS releaseDate
        FROM spotish.createur_media cm
        JOIN spotish.album a           ON cm.idmedia = a.idalbum
        JOIN spotish.media m           ON a.idalbum = m.idmedia
        WHERE cm.nomcreateur = ?;
                """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, creatorName);
      ResultSet rs = ps.executeQuery();
      List<Album> albums = new ArrayList<>();
      while (rs.next()) {
        Album album = new Album(
            rs.getLong("albumId"),
            rs.getString("title"),
            rs.getObject("releaseDate", LocalDate.class),
            rs.getString("creatorName"),
            null);
        albums.add(album);
      }
      return albums;
    }
  }

  /**
   * Retrieves the list of musics for a given album of a creator.
   *
   * @param conn        the database connection
   * @param creatorName the name of the creator
   * @param albumId     the ID of the album
   * @return a list of Music objects associated with the album of the creator
   * @throws SQLException if a database access error occurs
   */
  public List<Music> getMusicsOfAlbumOfCreator(Connection conn, String creatorName, Long albumId) throws SQLException {
    String sql = """
        SELECT c.idchanson AS musicId, m.titre AS title, m.datedesortie AS releaseDate, c.duree AS duration, c.genre AS genre,
               STRING_AGG(DISTINCT cm.nomcreateur, ', ' ORDER BY cm.nomcreateur) AS creatorNames
        FROM spotish.createur_media cm
        JOIN spotish.album_chanson ac  ON cm.idmedia = ac.idalbum
        JOIN spotish.chanson c         ON ac.idchanson = c.idchanson
        JOIN spotish.media m           ON m.idmedia = c.idchanson
        WHERE cm.nomcreateur = ? AND ac.idalbum = ?
        GROUP BY c.idchanson, m.titre, m.datedesortie, c.duree, c.genre;
                        """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, creatorName);
      ps.setLong(2, albumId);
      ResultSet rs = ps.executeQuery();
      List<Music> musics = new ArrayList<>();
      while (rs.next()) {
        Music music = new Music(
            rs.getLong("musicId"),
            rs.getString("title"),
            rs.getObject("releaseDate", LocalDate.class),
            rs.getInt("duration"),
            rs.getString("genre"),
            rs.getString("creatorName"));
        musics.add(music);
      }
      return musics;
    }
  }
}
