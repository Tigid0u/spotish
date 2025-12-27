package ch.heigvd.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

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

}
