package ch.heigvd.user;

import java.util.List;

import javax.sql.DataSource;

import io.javalin.http.ConflictResponse;
import io.javalin.http.HttpResponseException;
import io.javalin.http.NotFoundResponse;

import java.sql.Connection;
import java.sql.SQLException;

public class UserService {
  private final UserRepository userRepo;
  private final DataSource ds;

  public UserService(DataSource ds, UserRepository userRepo) {
    this.ds = ds;
    this.userRepo = userRepo;
  }

  /**
   * Get all users from the database.
   * 
   * @return A list of all users.
   */
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

  /**
   * Get a user by username.
   * 
   * @param username The username of the user to get.
   * @return The user with the given username.
   * @throws NotFoundResponse if the user does not exist.
   */
  public User getUser(String username) {
    try (Connection conn = ds.getConnection()) {
      User user = userRepo.getOne(conn, username);

      if (user == null) {
        throw new NotFoundResponse("User \"" + username + "\" not found");
      }

      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Insert a new user into the database.
   * 
   * @param user The user to insert.
   * @throws ConflictResponse if the user already exists.
   * @thows RuntimeException if a database error occurs.
   */
  public void insertUser(User user) {
    try (Connection conn = ds.getConnection()) {
      if (userRepo.exists(conn, user.username())) {
        throw new ConflictResponse("User \"" + user.username() + "\" already exists");
      }

      userRepo.insertOne(conn, user);

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
