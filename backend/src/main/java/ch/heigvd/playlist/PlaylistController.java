package ch.heigvd.playlist;

import java.util.List;

import ch.heigvd.entities.Playlist;
import io.javalin.http.Context;

public class PlaylistController {
  private final PlaylistService playlistService;

  public PlaylistController(PlaylistService playlistService) {
    this.playlistService = playlistService;
  }

  /**
   * Create a new playlist.
   *
   * @param ctx the Javalin context
   */
  public void createPlaylist(Context ctx) {
    Playlist playlist = ctx.bodyValidator(Playlist.class)
        .check(p -> p.id() == null, "ID must be null for new playlists")
        .check(p -> p.name() != null && p.name().length() > 0, "Name must not be empty")
        .check(p -> p.name() == null || p.name().length() <= 250, "Name must be at most 250 characters")
        .check(p -> p.description() == null || p.description().length() <= 250,
            "Description must be at most 250 characters")
        .check(p -> p.creatorName() == null, "Creator name must be null for new playlists")
        .check(p -> p.musics() != null && !p.musics().isEmpty(), "Playlist must contain at least one music")
        .get();

    // Add username of the user as the creater of the playlist
    playlist = playlist.setCreatorName(ctx.cookie("userNameCookie"));

    playlistService.createPlaylist(playlist);

    // 201 == Created
    ctx.status(201);
  }

  /**
   * Get a playlist by its ID.
   *
   * @param ctx the Javalin context
   */
  public void getPlaylist(Context ctx) {
    Long playlistId = ctx.pathParamAsClass("playlistId", Long.class)
        .check(id -> id > 0, "Playlist ID must be a positive number")
        .get();

    Playlist playlist = playlistService.getPlaylist(playlistId);

    ctx.json(playlist);
  }

  /**
   * Get all playlists created by a specific user.
   *
   * @param ctx the Javalin context
   */
  public void getUserPlaylists(Context ctx) {
    String creatorName = ctx.pathParamAsClass("creatorName", String.class)
        .check(name -> name != null && !name.isEmpty(), "Creator name must not be empty")
        .check(name -> name == null || name.length() <= 250, "Creator name must be at most 250 characters")
        .get();

    List<Playlist> playlists = playlistService.getUserPlaylists(creatorName);

    ctx.json(playlists);
  }

  /**
   * Get all playlists followed by the logged-in user.
   *
   * @param ctx the Javalin context
   */
  public void getFollowedPlaylists(Context ctx) {
    String username = ctx.cookie("userNameCookie");

    List<Playlist> playlists = playlistService.getFollowedPlaylists(username);

    ctx.json(playlists);
  }
}
