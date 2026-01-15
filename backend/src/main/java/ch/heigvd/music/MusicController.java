package ch.heigvd.music;

import java.util.List;

import ch.heigvd.entities.Music;
import io.javalin.http.Context;

public class MusicController {
  private final MusicService musicService;

  public MusicController(MusicService musicService) {
    this.musicService = musicService;
  }

    /**
     * Handles the HTTP GET request to retrieve all musics.
     *
     * @param ctx the Javalin HTTP context containing request and response data
     */
  public void getAll(Context ctx) {

      List<Music> musics = musicService.getAllMusics();

      ctx.json(musics);
    }

  /**
   * Handles the HTTP GET request to retrieve a music by its ID.
   *
   * @param ctx the Javalin HTTP context containing request and response data
   */
  public void getOne(Context ctx) {
    Long musicId = ctx.pathParamAsClass("idMedia", Long.class).check(id -> id >= 0, "idMedia must be positive").get();

    Music music = musicService.getMusic(musicId);

    ctx.json(music);
  }

  /**
   * Handles the HTTP GET request to retrieve the ten last listened musics for
   * the logged-in user.
   *
   * @param ctx the Javalin HTTP context containing request and response data
   */
  public void getTenLastListened(Context ctx) {
    // We know username is valid because this route is only accessible if the user
    // has the role LOGGED_IN which implies that the username must be valid
    String username = ctx.cookie("userNameCookie");

    List<Music> musics = musicService.getTenLastListenedMusics(username);

    ctx.json(musics);
  }

  /**
   * Handles the HTTP GET request to retrieve the ten most listened musics for
   * the logged-in user.
   *
   * @param ctx the Javalin HTTP context containing request and response data
   */
  public void getTenMostListened(Context ctx) {
    // We know username is valid because this route is only accessible if the user
    // has the role LOGGED_IN which implies that the username must be valid
    String username = ctx.cookie("userNameCookie");

    List<Music> musics = musicService.getTenMostListenedMusics(username);

    ctx.json(musics);
  }

  /**
   * Handles the HTTP GET request to retrieve all liked musics for
   * the logged-in user.
   *
   * @param ctx the Javalin HTTP context containing request and response data
   */
  public void getLiked(Context ctx) {
    // We know username is valid because this route is only accessible if the user
    // has the role LOGGED_IN which implies that the username must be valid
    String username = ctx.cookie("userNameCookie");

    List<Music> musics = musicService.getLikedMusics(username);

    ctx.json(musics);
  }

  /**
   * Handles the HTTP POST request to like a music for the logged-in user.
   *
   * @param ctx the Javalin HTTP context containing request and response data
   */
  public void likeMusic(Context ctx) {
    String username = ctx.cookie("userNameCookie");
    Long musicId = ctx.pathParamAsClass("idMedia", Long.class).check(id -> id >= 0, "idMedia must be positive").get();

    musicService.likeMusic(username, musicId);

    // 201 == Created
    ctx.status(201);
  }
}
