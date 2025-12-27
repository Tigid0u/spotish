package ch.heigvd.user;

import java.util.List;

import javax.sql.DataSource;

import ch.heigvd.NotFoundException;

import java.sql.Connection;
import java.sql.SQLException;

public class UserService {
  private final UserRepository userRepo;
  private final DataSource ds;

  public UserService(DataSource ds, UserRepository userRepo) {
    this.ds = ds;
    this.userRepo = userRepo;
  }

  public List<User> getAllUsers() {
    try (Connection conn = ds.getConnection()) {
      List<User> users = userRepo.getAll(conn);

      // If the list is empty, this is not a problem. The user can test this out by
      // himself.

      return users;

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public User getUser(String username) {
    try (Connection conn = ds.getConnection()) {
      User user = userRepo.getOne(conn, username);

      if (user == null) {
        throw new NotFoundException("User \"" + username + "\" not found");
      }

      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
