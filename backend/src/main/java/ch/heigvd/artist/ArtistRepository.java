package ch.heigvd.artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ch.heigvd.entities.Creator;

public class ArtistRepository {
  /**
   * Retrieves an artist by their name, including their albums and musics.
   *
   * @param conn       the database connection
   * @param artistName the name of the artist to retrieve
   * @return the Artist object with their albums and musics, or null if not found
   * @throws SQLException if a database access error occurs
   */
  public Creator getOne(Connection conn, String artistName) throws SQLException {
    String sql = """
        SELECT cr.nomcreateur AS artistName
        FROM spotish.createur cr
        INNER JOIN spotish.artiste a ON cr.nomcreateur = a.nomartiste
        WHERE a.nomartiste = ?;
            """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, artistName);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Creator artist = new Creator(
              rs.getString("artistName"),
              null,
              null);
          return artist;
        }
        return null;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Checks if an artist exists by their name.
   *
   * @param conn       the database connection
   * @param artistName the name of the artist to check
   * @return true if the artist exists, false otherwise
   * @throws SQLException if a database access error occurs
   */
  public boolean exists(Connection conn, String artistName) throws SQLException {
    String sql = """
        SELECT 1
        FROM spotish.artiste a
        WHERE a.nomartiste = ?;
            """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, artistName);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
