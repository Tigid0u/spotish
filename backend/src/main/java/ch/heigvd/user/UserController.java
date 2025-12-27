package ch.heigvd.user;

import java.util.List;

import io.javalin.http.Context;

public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  public void getAll(Context ctx) {
    List<User> users = userService.getAllUsers();

    ctx.json(users);
  }

  public void getOne(Context ctx) {
    String username = ctx.pathParam("nomUtilisateur");

    User user = userService.getUser(username);

    ctx.json(user);
  }
}
