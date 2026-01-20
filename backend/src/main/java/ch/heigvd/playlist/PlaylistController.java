package ch.heigvd.playlist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ch.heigvd.entities.Playlist;
import io.javalin.http.Context;
import io.javalin.http.NotModifiedResponse;
import io.javalin.http.PreconditionFailedResponse;

public class PlaylistController {
  private final PlaylistService playlistService;

  private final ConcurrentMap<String, LocalDateTime> usersCache;

  // This is a magic String used as key to identify all playlists in the cache
  private static final String ALL_PLAYLISTS_CACHE_KEY = "ALL_PLAYLISTS";

  public PlaylistController(PlaylistService playlistService, ConcurrentMap<String, LocalDateTime> usersCache) {
    this.playlistService = playlistService;
    this.usersCache = usersCache;
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

    Long playlistId = playlistService.createPlaylist(playlist);

    // Store the last modification date of the user
    LocalDateTime now = LocalDateTime.now();
    usersCache.put(playlistCacheKey(playlistId), now);

    // Invalidate the cache for all users
    usersCache.remove(allPlaylistsCacheKey(ctx.cookie("userNameCookie")));

    // Add the last modification date to the response
    ctx.header("Last-Modified", String.valueOf(now));

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

    // Get the last known modification date of the user
    LocalDateTime lastKnownModification =
      ctx.headerAsClass("If-Modified-Since", LocalDateTime.class).getOrDefault(null);

    // Check if the user has been modified since the last known modification date
    if (lastKnownModification != null && usersCache.get(playlistCacheKey(playlistId)).equals(lastKnownModification)) {
      throw new NotModifiedResponse();
    }

    Playlist playlist = playlistService.getPlaylist(playlistId);

    LocalDateTime now;
    if (usersCache.containsKey(playlistCacheKey(playlistId))) {
      // If it is already in the cache, get the last modification date
      now = usersCache.get(playlistCacheKey(playlistId));
    } else {
      // Otherwise, set to the current date
      now = LocalDateTime.now();
      usersCache.put(playlistCacheKey(playlistId), now);
    }

    // Add the last modification date to the response
    ctx.header("Last-Modified", String.valueOf(now));

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

    // Get the last known modification date of all users
    LocalDateTime lastKnownModification =
      ctx.headerAsClass("If-Modified-Since", LocalDateTime.class).getOrDefault(null);

    // Check if all users have been modified since the last known modification date
    if (lastKnownModification != null
        && usersCache.containsKey(allPlaylistsCacheKey(creatorName))
        && usersCache.get(allPlaylistsCacheKey(creatorName)).equals(lastKnownModification)) {
      throw new NotModifiedResponse();
    }

    List<Playlist> playlists = playlistService.getUserPlaylists(creatorName);

    LocalDateTime now;
    if (usersCache.containsKey(allPlaylistsCacheKey(creatorName))) {
      // If it is already in the cache, get the last modification date
      now = usersCache.get(allPlaylistsCacheKey(creatorName));
    } else {
      // Otherwise, set to the current date
      now = LocalDateTime.now();
      usersCache.put(allPlaylistsCacheKey(creatorName), now);
    }

    // Add the last modification date to the response
    ctx.header("Last-Modified", String.valueOf(now));

    ctx.json(playlists);
  }

  /**
   * Get all playlists followed by the logged-in user.
   *
   * @param ctx the Javalin context
   */
  public void getFollowedPlaylists(Context ctx) {
    String username = ctx.cookie("userNameCookie");

    // Get the last known modification date of all users
    LocalDateTime lastKnownModification =
            ctx.headerAsClass("If-Modified-Since", LocalDateTime.class).getOrDefault(null);

    // Check if all users have been modified since the last known modification date
    if (lastKnownModification != null
            && usersCache.containsKey(followedAllPlaylistsCacheKey(username))
            && usersCache.get(followedAllPlaylistsCacheKey(username)).equals(lastKnownModification)) {
      throw new NotModifiedResponse();
    }

    List<Playlist> playlists = playlistService.getFollowedPlaylists(username);

    LocalDateTime now;
    if (usersCache.containsKey(followedAllPlaylistsCacheKey(username))) {
      // If it is already in the cache, get the last modification date
      now = usersCache.get(followedAllPlaylistsCacheKey(username));
    } else {
      // Otherwise, set to the current date
      now = LocalDateTime.now();
      usersCache.put(followedAllPlaylistsCacheKey(username), now);
    }

    // Add the last modification date to the response
    ctx.header("Last-Modified", String.valueOf(now));

    ctx.json(playlists);
  }

  /**
   * Follow a playlist (authenticated user).
   *
   * @param ctx the Javalin context
   */
  public void followPlaylist(Context ctx) {
    Long playlistId = ctx.pathParamAsClass("playlistId", Long.class)
        .check(id -> id > 0, "Playlist ID must be a positive number")
        .get();

    String username = ctx.cookie("userNameCookie");

    playlistService.followPlaylist(username, playlistId);

    // Invalidate the cache for all users
    usersCache.remove(followedAllPlaylistsCacheKey(username));

    // 201 == Created
    ctx.status(201);
  }

  /**
   * Add a music to a playlist owned by the logged-in user.
   *
   * @param ctx the Javalin context
   */
  public void addMusicToPlaylist(Context ctx) {
    Long musicId = ctx.pathParamAsClass("idMedia", Long.class)
        .check(id -> id > 0, "Music ID must be a positive number")
        .get();
    Long playlistId = ctx.pathParamAsClass("playlistId", Long.class)
        .check(id -> id > 0, "Playlist ID must be a positive number")
        .get();
    String username = ctx.cookie("userNameCookie");

    // Get the last known modification date of the user
    LocalDateTime lastKnownModification =
      ctx.headerAsClass("If-Unmodified-Since", LocalDateTime.class).getOrDefault(null);

    // Check if the user has been modified since the last known modification date
    if (lastKnownModification != null
            //&& usersCache.get(playlistCacheKey(playlistId)) != null
            && !usersCache.get(playlistCacheKey(playlistId)).equals(lastKnownModification)) {
      throw new PreconditionFailedResponse();
    }

    playlistService.addMusicToPlaylist(username, playlistId, musicId);

    // Store the last modification date of the user
    LocalDateTime now = LocalDateTime.now();
    usersCache.put(playlistCacheKey(playlistId), now);

    // Invalidate the cache for all users
    usersCache.remove(allPlaylistsCacheKey(username));

    // Add the last modification date to the response
    ctx.header("Last-Modified", String.valueOf(now));

    // 201 == Created
    ctx.status(201);
  }

  /**
   * Remove a music from a playlist owned by the logged-in user.
   *
   * @param ctx the Javalin context
   */
  public void removeMusicFromPlaylist(Context ctx) {
    Long musicId = ctx.pathParamAsClass("idMedia", Long.class)
        .check(id -> id > 0, "Music ID must be a positive number")
        .get();
    Long playlistId = ctx.pathParamAsClass("playlistId", Long.class)
        .check(id -> id > 0, "Playlist ID must be a positive number")
        .get();
    String username = ctx.cookie("userNameCookie");

    // Get the last known modification date of the user
    LocalDateTime lastKnownModification =
      ctx.headerAsClass("If-Unmodified-Since", LocalDateTime.class).getOrDefault(null);

    // Check if the user has been modified since the last known modification date
    if (lastKnownModification != null
            //&& usersCache.get(playlistCacheKey(playlistId)) != null
            && !usersCache.get(playlistCacheKey(playlistId)).equals(lastKnownModification)) {
      throw new PreconditionFailedResponse();
    }

    playlistService.removeMusicFromPlaylist(username, playlistId, musicId);

    // Invalidate the cache for the user and update the playlist timestamp
    LocalDateTime now = LocalDateTime.now();
    usersCache.put(playlistCacheKey(playlistId), now);

    // Invalidate the cache for all users
    usersCache.remove(allPlaylistsCacheKey(username));

    // Add the last modification date to the response
    ctx.header("Last-Modified", String.valueOf(now));

    // 204 == No Content
    ctx.status(204);
  }

  private String playlistCacheKey(Long playlistId) {
    return "playlist:" + playlistId;
  }


  /**
   * Cache key for all playlists.
   * @param name the name identifier
   * @return the cache key
   */
  private String allPlaylistsCacheKey(String name) {
    return ALL_PLAYLISTS_CACHE_KEY + ":" + name;
  }

  /**
   * Cache key for all followed playlists.
   * @param name the name identifier
   * @return the cache key
   */
  private String followedAllPlaylistsCacheKey(String name) {
    return "FOLLOWED_" + ALL_PLAYLISTS_CACHE_KEY + ":" + name;
  }
}
