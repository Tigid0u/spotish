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
    User user = ctx.bodyAsClass(User.class);

    // Validation
    if (user.username() == null || user.username().isEmpty()) {
      // 400 == Bad Request
      ctx.status(400).result("username is required");
      return;
    } else if (user.lname() == null || user.lname().isEmpty()) {
      // 400 == Bad Request
      ctx.status(400).result("lname is required");
      return;
    } else if (user.fname() == null || user.fname().isEmpty()) {
      // 400 == Bad Request
      ctx.status(400).result("fname is required");
      return;
    } else if (user.birthdate() == null) {
      // 400 == Bad Request
      ctx.status(400).result("birthdate is required");
      return;
    } else if (user.fname().length() > 255) {
      // 400 == Bad Request
      ctx.status(400).result("fname is too long");
      return;
    } else if (user.lname().length() > 255) {
      // 400 == Bad Request
      ctx.status(400).result("lname is too long");
      return;
    } else if (user.email() != null && user.email().length() > 255) {
      // 400 == Bad Request
      ctx.status(400).result("email is too long");
      return;
    }
    userService.insertUser(user);

    // 201 == Created
    ctx.status(201);
  }
}
