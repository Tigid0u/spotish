package ch.heigvd.auth;

import ch.heigvd.user.UserService;
import ch.heigvd.user.User;
import io.javalin.http.Context;
import io.javalin.http.Cookie;

public class AuthController {
  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Logs in a user by setting a cookie with the username.
   * Expects a query parameter "nomUtilisateur".
   * 
   * @param ctx Javalin HTTP context
   */
  public void loginUser(Context ctx) {
    String username = ctx.pathParamAsClass("nomUtilisateur", String.class)
        .check(s -> s.length() > 0 && s.length() <= 255, "Username cannot be empty or exceed 255 characters").get();

    User user = userService.getUser(username);

    // Set username cookie (expires after 1 hour = 3600 seconds)
    ctx.cookie("userNameCookie", user.username(), 3600);

    ctx.status(204);
  }

  /**
   * Logs out a user by removing the username cookie.
   * 
   * @param ctx Javalin HTTP context
   */
  public void logoutUser(Context ctx) {
    // Remove the username cookie
    ctx.removeCookie("userNameCookie");

    ctx.status(204);
  }
}
