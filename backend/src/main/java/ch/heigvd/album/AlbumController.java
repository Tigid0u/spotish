package ch.heigvd.album;

import ch.heigvd.entities.Album;
import io.javalin.http.Context;

public class AlbumController {
  private final AlbumService albumService;

  public AlbumController(AlbumService albumService) {
    this.albumService = albumService;
  }

  /**
   * Get an album by its ID.
   *
   * @param ctx the Javalin context
   */
  public void getAlbum(Context ctx) {
    Long id = ctx.pathParamAsClass("idMedia", Long.class).check(l -> l >= 0, "ID must be positive").get();

    Album album = albumService.getAlbum(id);

    ctx.json(album);
  }
}
