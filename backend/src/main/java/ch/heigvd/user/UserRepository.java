package ch.heigvd.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ch.heigvd.entities.User;

public class UserRepository {

  /**
   * Get all users from the database.
   * 
   * @param conn The connection to the database.
   * @return A list of all users.
   * @throws SQLException if a database error occurs.
   */
  public List<User> getAll(Connection conn) throws SQLException {
    String sql = """
        select nomUtilisateur, nom, prenom, dateNaissance, email
        from spotish.utilisateur;
        """;
    try (PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      List<User> users = new ArrayList<>();
      while (rs.next()) {
        users.add(new User(
            rs.getString("nomUtilisateur"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getObject("dateNaissance", LocalDate.class),
            rs.getString("email")));
      }

      return users;
    }
  }

  /**
   * Get a user by username.
   * 
   * @param conn     The connection to the database.
   * @param username The username of the user to get.
   * @return The user with the given username, or null if not found.
   * @throws SQLException if a database error occurs.
   */
  public User getOne(Connection conn, String username) throws SQLException {
    String sql = """
        select nomUtilisateur, nom, prenom, dateNaissance, email
        from spotish.utilisateur
        where nomUtilisateur = ?;
        """;
    try (PreparedStatement ps = conn.prepareStatement(sql);) {
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();
      User user = null;
      if (rs.next()) {
        user = new User(
            rs.getString("nomUtilisateur"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getObject("dateNaissance", LocalDate.class),
            rs.getString("email"));
      }
      return user;
    }
  }

  /**
   * Insert a new user into the database.
   * 
   * @param conn The connection to the database.
   * @param user The user to insert.
   * @return The number of rows affected.
   * @throws SQLException if a database error occurs.
   */
  public int insertOne(Connection conn, User user) throws SQLException {
    String sql = """
        insert into spotish.utilisateur (nomutilisateur, nom, prenom, datenaissance, email)
        values (?,?,?,?,?);
        """;
    try (PreparedStatement ps = conn.prepareStatement(sql);) {
      ps.setString(1, user.username());
      ps.setString(2, user.lname());
      ps.setString(3, user.fname());
      ps.setObject(4, user.birthdate());
      ps.setString(5, user.email());

      return ps.executeUpdate();
    }
  }

  /**
   * Check if a user exists in the database.
   * 
   * @param conn     The connection to the database.
   * @param username The username of the user to check.
   * @return true if the user exists, false otherwise.
   * @throws SQLException if a database error occurs.
   */
  public boolean exists(Connection conn, String username) throws SQLException {
    String sql = """
        select nomUtilisateur, nom, prenom, dateNaissance, email
        from spotish.utilisateur
        where nomUtilisateur = ?;
        """;
    try (PreparedStatement ps = conn.prepareStatement(sql);) {
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return true;
      }
      return false;
    }
  }
}
