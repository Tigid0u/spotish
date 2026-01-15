package ch.heigvd.music;

import java.util.List;

import ch.heigvd.entities.Music;
import io.javalin.http.Context;

public class MusicController {
  private final MusicService musicService;

  private final static int TEN_LAST_LISTENED_CACHE_MAX_AGE_SECONDS = 60;
  private final static int TEN_MOST_LISTENED_CACHE_MAX_AGE_SECONDS = 600; // 10 minutes
  private final static int MUSIC_ID_CACHE_MAX_AGE_SECONDS = 1800; // 30 minutes
  private final static int ALL_MUSICS_CACHE_MAX_AGE_SECONDS = 3600; // 1 hour
  private final static int LIKED_MUSICS_CACHE_MAX_AGE_SECONDS = 300; // 5 minutes

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

    // Cache the response for ALL_MUSICS_CACHE_MAX_AGE_SECONDS seconds
    ctx.header("Cache-Control", "max-age=" + ALL_MUSICS_CACHE_MAX_AGE_SECONDS);

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

    // Cache the response for MUSIC_ID_CACHE_MAX_AGE_SECONDS seconds
    ctx.header("Cache-Control", "max-age=" + MUSIC_ID_CACHE_MAX_AGE_SECONDS);

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

    // Cache the response for TEN_LAST_LISTENED_CACHE_MAX_AGE_SECONDS seconds
    ctx.header("Cache-Control", "max-age=" + TEN_LAST_LISTENED_CACHE_MAX_AGE_SECONDS);

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

    // Cache the response for TEN_MOST_LISTENED_CACHE_MAX_AGE_SECONDS seconds
    ctx.header("Cache-Control", "max-age=" + TEN_MOST_LISTENED_CACHE_MAX_AGE_SECONDS);

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

    // Cache the response for LIKED_MUSICS_CACHE_MAX_AGE_SECONDS seconds
    ctx.header("Cache-Control", "max-age=" + LIKED_MUSICS_CACHE_MAX_AGE_SECONDS);

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
