package ch.heigvd.user;

import java.util.List;

import io.javalin.http.Context;

public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Get all users.
   *
   * @param ctx Javalin context
   */
  public void getAll(Context ctx) {
    List<User> users = userService.getAllUsers();

    ctx.json(users);
  }

  /**
   * Get one user by username.
   *
   * @param ctx Javalin context
   */
  public void getOne(Context ctx) {
    String username = ctx.pathParam("nomUtilisateur");

    User user = userService.getUser(username);

    ctx.json(user);
  }

  /**
   * Insert one user.
   *
   * @param ctx Javalin context
   */
  public void insertOne(Context ctx) {
    // Validate the body contents
    User user = ctx.bodyValidator(User.class)
        .check(u -> u.username() != null && !u.username().isEmpty(), "username is required")
        .check(u -> u.lname() != null && !u.lname().isEmpty(), "lname is required")
        .check(u -> u.fname() != null && !u.fname().isEmpty(), "fname is required")
        .check(u -> u.birthdate() != null, "birthdate is required")
        .check(u -> u.username() == null || u.username().length() <= 255, "username is too long")
        .check(u -> u.fname() == null || u.fname().length() <= 255, "fname is too long")
        .check(u -> u.lname() == null || u.lname().length() <= 255, "lname is too long")
        .check(u -> u.email() == null || u.email().length() <= 255, "email is too long")
        .get();

    userService.insertUser(user);

    // 201 == Created
    ctx.status(201);
  }
}
