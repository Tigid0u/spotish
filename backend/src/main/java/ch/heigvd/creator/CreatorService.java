package ch.heigvd.creator;

import java.sql.Connection;

import javax.sql.DataSource;

import ch.heigvd.artist.ArtistRepository;
import ch.heigvd.group.GroupRepository;
import io.javalin.http.NotFoundResponse;
import ch.heigvd.entities.Creator;

public class CreatorService {
  private final DataSource ds;
  private final CreatorRepository creatorRepository;
  private final ArtistRepository artistRepository;
  private final GroupRepository groupRepository;

  public CreatorService(DataSource ds, CreatorRepository creatorRepository, ArtistRepository artistRepository,
      GroupRepository groupRepository) {
    this.ds = ds;
    this.creatorRepository = creatorRepository;
    this.artistRepository = artistRepository;
    this.groupRepository = groupRepository;
  }

  /**
   * Retrieves a creator (artist or group) by their name, including their albums.
   *
   * @param creatorName the name of the creator to retrieve
   * @return the Creator object with their albums
   * @throws NotFoundResponse if the creator does not exist
   */
  public Creator getOne(String creatorName) {
    try (Connection conn = ds.getConnection()) {
      // Check if the creator is an artist
      if (artistRepository.exists(conn, creatorName)) {
        Creator artist = artistRepository.getOne(conn, creatorName);
        artist.setAlbums(creatorRepository.getAlbumsOfCreator(conn, creatorName));
        return artist;
      } else if (groupRepository.exists(conn, creatorName)) {
        // Otherwise, check if the creator is a group
        Creator group = groupRepository.getOne(conn, creatorName);
        group.setAlbums(creatorRepository.getAlbumsOfCreator(conn, creatorName));
        return group;
      } else {
        throw new NotFoundResponse("This creator does not exist");
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
