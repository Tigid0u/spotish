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

    userService.insertUser(user);

    // 201 == Created
    ctx.status(201);
  }
}
