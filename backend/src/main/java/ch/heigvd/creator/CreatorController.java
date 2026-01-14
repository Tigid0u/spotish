package ch.heigvd.creator;

import ch.heigvd.entities.Creator;
import io.javalin.http.Context;

public class CreatorController {
  private CreatorService creatorService;

  public CreatorController(CreatorService creatorService) {
    this.creatorService = creatorService;
  }

  /**
   * Get a creator by their name.
   *
   * @param ctx The Javalin context containing the request and response
   *            information.
   */
  public void getCreator(Context ctx) {
    String creatorName = ctx.pathParamAsClass("creatorName", String.class)
        .check(s -> s != null && !s.isEmpty(), "creatorName must be provided")
        .check(s -> s == null || s.length() <= 255, "creatorName must be at most 255 characters long")
        .get();

    Creator creator = creatorService.getOne(creatorName);

    ctx.json(creator);
  }
}
