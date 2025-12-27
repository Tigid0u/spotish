package ch.heigvd;

import javax.sql.DataSource;

import java.util.Map;

import io.javalin.Javalin;
import ch.heigvd.user.*;

public class App {
  public static final int PORT = 8080;

  public static void main(String[] args) {
    Javalin app = Javalin.create();

    // handle general exceptions
    app.exception(Exception.class, (e, ctx) -> {
      // Expose the internal error message for debugging purposes
      ctx.status(500).json(Map.of("error", e.getMessage()));
      // Uncomment the following line to avoid exposing internal error messages
      // ctx.status(500).json(Map.of("error", "Internal error"));
    });

    // Init a new DataSource pool using HikariCP
    DataSource ds = Db.createDataSource();

    UserRepository userRepository = new UserRepository();
    UserService userService = new UserService(ds, userRepository);
    UserController userController = new UserController(userService);

    // Register routes
    app.get("/utilisateurs", userController::getAll);
    app.get("/utilisateurs/{nomUtilisateur}", userController::getOne);
    app.post("/utilisateurs", userController::insertOne);

    app.start(PORT);
  }
}
