package ch.heigvd.group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ch.heigvd.entities.Creator;

public class GroupRepository {
  /**
   * Retrieves a group by its name, including its artists.
   *
   * @param conn      the database connection
   * @param groupName the name of the group to retrieve
   * @return the Creator object representing the group with its artists, or null
   *         if not found
   */
  public Creator getOne(Connection conn, String groupName) {
    String sql = """
              SELECT cr.nomcreateur AS groupName, cr.nomGerant AS managerName
        FROM spotish.createur cr
        INNER JOIN spotish.groupe g ON cr.nomcreateur = g.nomgroupe
        WHERE g.nomgroupe = ?;
                    """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, groupName);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Creator group = new Creator(
              rs.getString("groupName"),
              getArtistsOfGroup(conn, groupName),
              null);
          return group;
        }
        return null;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves the list of artists belonging to a specific group.
   *
   * @param conn      the database connection
   * @param groupName the name of the group
   * @return a list of artist names belonging to the specified group
   */
  public List<String> getArtistsOfGroup(Connection conn, String groupName) {
    String sql = """
            SELECT a.nomartiste AS artistName
        FROM spotish.groupe g
        JOIN spotish.artiste a ON g.nomgroupe = a.nomgroupe
        JOIN spotish.createur cr ON a.nomartiste = cr.nomcreateur
        WHERE g.nomgroupe = ?;
            """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, groupName);
      try (ResultSet rs = ps.executeQuery()) {
        List<String> artists = new ArrayList<>();
        while (rs.next()) {
          String artistName = rs.getString("artistName");
          artists.add(artistName);
        }
        return artists;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Checks if a group with the specified name exists in the database.
   *
   * @param conn      the database connection
   * @param groupName the name of the group to check
   * @return true if the group exists, false otherwise
   */
  public boolean exists(Connection conn, String groupName) {
    String sql = """
            SELECT 1
        FROM spotish.groupe g
        WHERE g.nomgroupe = ?;
            """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, groupName);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
