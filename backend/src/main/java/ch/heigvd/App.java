package ch.heigvd;

import javax.sql.DataSource;

import java.util.Map;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;

import io.javalin.security.RouteRole;
import ch.heigvd.album.AlbumController;
import ch.heigvd.album.AlbumRepository;
import ch.heigvd.album.AlbumService;
import ch.heigvd.auth.AuthController;
import ch.heigvd.music.MusicController;
import ch.heigvd.music.MusicRepository;
import ch.heigvd.music.MusicService;
import ch.heigvd.playlist.PlaylistController;
import ch.heigvd.playlist.PlaylistRepository;
import ch.heigvd.playlist.PlaylistService;
import ch.heigvd.user.*;

// Acess roles
// OPEN = no username cookie needs to be set to access the ressource, 
// LOGGED_IN = a username cookie is required to access the ressource
enum Role implements RouteRole {
  OPEN, LOGGED_IN
}

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

    // User related ressources
    UserRepository userRepository = new UserRepository();
    UserService userService = new UserService(ds, userRepository);
    UserController userController = new UserController(userService);

    // Auth related ressources
    AuthController authController = new AuthController(userService);

    // Music related ressources
    MusicRepository musicRepository = new MusicRepository();
    MusicService musicService = new MusicService(ds, musicRepository);
    MusicController musicController = new MusicController(musicService);

    // Playlist related ressources
    PlaylistRepository playlistRepository = new PlaylistRepository();
    PlaylistService playlistService = new PlaylistService(ds, playlistRepository, userRepository, musicRepository);
    PlaylistController playlistController = new PlaylistController(playlistService);

    // Album related ressources
    AlbumRepository albumRepository = new AlbumRepository();
    AlbumService albumService = new AlbumService(ds, albumRepository);
    AlbumController albumController = new AlbumController(albumService);

    // Access management
    // We check the required roles before accessing every routes
    app.beforeMatched(ctx -> {
      Role userRole = App.getUserRole(ctx, userService);
      if (!ctx.routeRoles().contains(userRole)) { // routeRoles are provided through the Context interface
        throw new UnauthorizedResponse(
            "You must be logged in (username cookie must be set) to access this ressource !");
      }
    });

    // Register routes

    // User related routes (endpoints)
    app.get("/utilisateurs", userController::getAll, Role.OPEN, Role.LOGGED_IN);
    app.get("/utilisateurs/{nomUtilisateur}", userController::getOne, Role.OPEN, Role.LOGGED_IN);
    app.post("/utilisateurs", userController::insertOne, Role.OPEN, Role.LOGGED_IN);

    // Authentication related routes
    app.post("/login/{nomUtilisateur}", authController::loginUser, Role.OPEN, Role.LOGGED_IN);
    app.post("/logout", authController::logoutUser, Role.LOGGED_IN, Role.OPEN);

    // Music related routes
    app.get("/musics", musicController::getAll, Role.OPEN, Role.LOGGED_IN);
    app.get("/musics/last-listened", musicController::getTenLastListened, Role.LOGGED_IN);
    app.get("/musics/most-listened", musicController::getTenMostListened, Role.LOGGED_IN);
    app.get("/musics/liked", musicController::getLiked, Role.LOGGED_IN);
    // parameter. Otherwise any second part of the url (ex:'last-listened') would be
    // interpreted as a parameter
    app.get("/musics/{idMedia}", musicController::getOne, Role.OPEN, Role.LOGGED_IN);
    app.post("/musics/liked/{idMedia}", musicController::likeMusic, Role.LOGGED_IN);

    // Playlist related routes
    app.get("/playlists/user/{creatorName}", playlistController::getUserPlaylists, Role.OPEN, Role.LOGGED_IN);
    app.get("/playlists/followed", playlistController::getFollowedPlaylists, Role.LOGGED_IN);
    app.get("/playlists/{playlistId}", playlistController::getPlaylist, Role.OPEN, Role.LOGGED_IN);
    app.post("/playlists", playlistController::createPlaylist, Role.LOGGED_IN);
    app.post("/playlists/followed/{playlistId}", playlistController::followPlaylist, Role.LOGGED_IN);
    app.post("/playlists/{playlistId}/musics/{idMedia}", playlistController::addMusicToPlaylist, Role.LOGGED_IN);
    app.delete("/playlists/{playlistId}/musics/{idMedia}", playlistController::removeMusicFromPlaylist,
        Role.LOGGED_IN);

    // Album related routes
    app.get("/albums/{idMedia}", albumController::getAlbum, Role.OPEN, Role.LOGGED_IN);

    app.start(PORT);
  }

  /**
   * Determine the role of the user making the request based on the
   * "userNameCookie" cookie.
   * 
   * @param ctx         The Javalin context of the request.
   * @param userService The UserService to check if the user exists.
   * @return The Role of the user (OPEN or LOGGED_IN).
   */
  private static Role getUserRole(Context ctx, UserService userService) {
    String username = ctx.cookie("userNameCookie");

    if (username == null) {
      return Role.OPEN;
    }

    try {
      userService.getUser(username);
      return Role.LOGGED_IN;
    } catch (NotFoundResponse e) {
      // Means the user doesn't exist => the user is not LOGGED_IN
      return Role.OPEN;
    }
  }
}
