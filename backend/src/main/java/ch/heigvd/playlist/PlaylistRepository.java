package ch.heigvd.playlist;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ch.heigvd.entities.Music;
import ch.heigvd.entities.Playlist;

public class PlaylistRepository {
  /**
   * Create a new playlist in the database.
   *
   * @param conn     the database connection
   * @param playlist the playlist to be created
   * @return the number of rows affected
   * @throws SQLException if a database access error occurs
   */
  public int createPlaylist(Connection conn, Playlist playlist) throws SQLException {
    // Source used as reference for unnest function (+ some ChatGPT usage examples):
    // https://stackoverflow.com/questions/68136872/postgresql-join-unnest-output-with-cte-insert-id-insert-many-to-many
    String sql = """
        WITH new_playlist AS (
                INSERT INTO spotish.playlist (nom, description, nomcreateur)
                VALUES (?, ?, ?)
                RETURNING idplaylist
            )
            INSERT INTO spotish.chanson_playlist (idchanson, idplaylist)
            SELECT unnest(?::bigint[]), np.idplaylist
            FROM new_playlist np
            """;

    // Disable auto-commit mode to enable transaction management
    conn.setAutoCommit(false);
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, playlist.name());
      ps.setString(2, playlist.description());
      ps.setString(3, playlist.creatorName());

      // Convert music list (only the ids) to a Long array for the SQL array
      Long[] musicIds = new Long[playlist.musics().size()];
      for (int i = 0; i < playlist.musics().size(); i++) {
        musicIds[i] = playlist.musics().get(i).musicId();
      }
      // Create SQL array from Long array:
      // https://stackoverflow.com/questions/17842211/how-to-use-an-arraylist-as-a-prepared-statement-parameter
      Array musicIdsArray = conn.createArrayOf("bigint", musicIds);
      ps.setArray(4, musicIdsArray);

      int result = ps.executeUpdate();
      conn.commit();

      return result;
    } catch (SQLException e) {
      // if exception, rollback changes
      conn.rollback();
      throw e;
    } finally {
      // Restore default commit behavior
      conn.setAutoCommit(true);
    }
  }

  /**
   * Get a playlist by its ID.
   *
   * @param conn       the database connection
   * @param playlistId the unique identifier of the playlist
   * @return the playlist with the given ID, or null if not found
   * @throws SQLException if a database access error occurs
   */
  public Playlist getPlaylist(Connection conn, Long playlistId) throws SQLException {
    String sql = """
        SELECT p.idplaylist AS playlistId, p.nom AS name, p.description AS description, p.nomcreateur AS creatorName
        FROM spotish.playlist p
        WHERE p.idplaylist = ?;
                """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, playlistId);
      ResultSet rs = ps.executeQuery();

      Playlist playlist = null;

      if (rs.next()) {
        List<Music> musics = getMusicsOfPlaylist(conn, playlistId);

        playlist = new Playlist(
            rs.getLong("playlistId"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("creatorName"),
            musics);
      }
      return playlist;
    }
  }

  /**
   * Check if a playlist exists in the database.
   *
   * @param conn       the database connection
   * @param playlistId the unique identifier of the playlist
   * @return true if the playlist exists, false otherwise
   * @throws SQLException if a database access error occurs
   */
  public boolean exists(Connection conn, Long playlistId) throws SQLException {
    String sql = """
        SELECT COUNT(*) AS count
        FROM spotish.playlist
        WHERE idplaylist = ?;
                """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, playlistId);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        return rs.getInt("count") > 0;
      }
      return false;
    }
  }

  /**
   * Get all playlists created by a specific user.
   *
   * @param conn        the database connection
   * @param creatorName the name of the playlist creator
   * @return a list of playlists created by the specified user
   * @throws SQLException if a database access error occurs
   */
  public List<Playlist> getUserPlaylists(Connection conn, String creatorName) throws SQLException {
    String sql = """
        SELECT p.idplaylist AS playlistId, p.nom AS name, p.description AS description, p.nomcreateur AS creatorName
        FROM spotish.playlist p
        WHERE p.nomcreateur = ?;
                """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, creatorName);
      ResultSet rs = ps.executeQuery();

      List<Playlist> playlists = new ArrayList<>();

      while (rs.next()) {
        Long playlistId = rs.getLong("playlistId");
        List<Music> musics = getMusicsOfPlaylist(conn, playlistId);

        Playlist playlist = new Playlist(
            playlistId,
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("creatorName"),
            musics);

        playlists.add(playlist);
      }
      return playlists;
    }
  }

  /**
   * Get all playlists followed by a specific user.
   *
   * @param conn     the database connection
   * @param username the username of the user
   * @return a list of playlists followed by the specified user
   * @throws SQLException if a database access error occurs
   */
  public List<Playlist> getAllFollowedPlaylists(Connection conn, String username) throws SQLException {
    String sql = """
        SELECT up.idplaylist, p.nom AS playlist_name, p.description, p.nomcreateur AS playlist_creator
        FROM spotish.utilisateur_playlist up
        JOIN spotish.playlist p ON up.idplaylist = p.idplaylist
        WHERE up.nomutilisateur = ?;
            """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();

      List<Playlist> playlists = new ArrayList<>();

      while (rs.next()) {
        Long playlistId = rs.getLong("idplaylist");
        List<Music> musics = getMusicsOfPlaylist(conn, playlistId);

        Playlist playlist = new Playlist(
            playlistId,
            rs.getString("playlist_name"),
            rs.getString("description"),
            rs.getString("playlist_creator"),
            musics);

        playlists.add(playlist);
      }
      return playlists;
    }
  }

  /**
   * Get all musics of a given playlist.
   *
   * @param conn       the database connection
   * @param playlistId the unique identifier of the playlist
   * @return a list of musics in the playlist
   * @throws SQLException if a database access error occurs
   */
  private List<Music> getMusicsOfPlaylist(Connection conn, Long playlistId) throws SQLException {
    String sql = """
        SELECT c.idchanson AS musicId, m.titre AS title, m.datedesortie AS releaseDate, c.duree AS duration, c.genre AS genre,
               STRING_AGG(DISTINCT cm.nomcreateur, ', ' ORDER BY cm.nomcreateur) AS creatorNames
        FROM spotish.chanson_playlist cp
        JOIN spotish.chanson c          ON cp.idchanson = c.idchanson
        JOIN spotish.media m            ON m.idmedia = c.idchanson
        JOIN spotish.createur_media cm  ON cm.idmedia = m.idmedia
        WHERE cp.idplaylist = ?
        GROUP BY c.idchanson, m.titre, m.datedesortie, c.duree, c.genre;
                """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, playlistId);
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
}
