---
theme: gaia
paginate: true
backgroundColor: #fff
backgroundImage: url('https://marp.app/assets/hero-background.svg')
style: |
  .columns {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 1rem;
  }
---

<!-- _class: lead -->

# **Spotish**

---

# **Demo time !**

### Link to spotish at

[https://spotish.freeddns.org/](https://spotish.freeddns.org/)

---

# **API - Overview**

The API endpoints serve the following endpoints:

- user
- creator (artist / group)
- album
- music
- playlist

---

# **API - Structure**

The backend has 3 "layers":

- **Controller**: handles the HTTP request
- **Service**: handles app logic
- **Repository**: handles DB queries

---

# **API - Implementation**

Let's quickly take a look to the implementation of the route `GET /musics/{idMedia}`:

Declaration of the route in the entrypoint (`App.java`):

```java
app.get("/musics/{idMedia}", musicController::getOne, Role.OPEN, Role.LOGGED_IN);
```

---

# **API - Implementation**

Music controller (`musicController.java`):

```java
  public void getOne(Context ctx) {
    Long musicId = ctx.pathParamAsClass("idMedia", Long.class).check(id -> id >= 0, "idMedia must be positive").get();

    Music music = musicService.getMusic(musicId);

    // Cache the response for MUSIC_ID_CACHE_MAX_AGE_SECONDS seconds
    ctx.header("Cache-Control", "max-age=" + MUSIC_ID_CACHE_MAX_AGE_SECONDS);

    ctx.json(music);
  }
```

---

# **API - Implementation**

Music service (`musicService.java`):

```java
  public Music getMusic(Long musicId) {
    try (Connection conn = ds.getConnection()) {
      Music music = musicRepo.getOne(conn, musicId);

      if (music == null) {
        throw new NotFoundResponse("Music with id \"" + musicId + "\" not found");
      }

      return music;
      // [...]
  }
```

---

# **API - Implementation**

Music respository (`musicRepository.java`):

```java
  public Music getOne(Connection conn, Long musicId) throws SQLException {
    String sql = /* SQL Query*/;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, musicId);
      ResultSet rs = ps.executeQuery();
      Music music = null;

      // Retrieve query results
      if (rs.next()) {
        music = new Music(rs.getLong("musicId"), rs.getString("title"), rs.getObject("releaseDate", LocalDate.class),
          rs.getInt("duration"), rs.getString("genre"), rs.getString("creatorNames"));
      }
      return music;
    }
  }
```

---

# **Cache - Expiration model**

```java
private final static int TEN_LAST_LISTENED_CACHE_MAX_AGE_SECONDS = 60;
private final static int TEN_MOST_LISTENED_CACHE_MAX_AGE_SECONDS = 600; // 10 minutes
private final static int MUSIC_ID_CACHE_MAX_AGE_SECONDS = 1800; // 30 minutes
private final static int ALL_MUSICS_CACHE_MAX_AGE_SECONDS = 3600; // 1 hour
private final static int LIKED_MUSICS_CACHE_MAX_AGE_SECONDS = 300; // 5 minutes

// Example usage for the GET /musics route
ctx.header("Cache-Control", "max-age=" + ALL_MUSICS_CACHE_MAX_AGE_SECONDS);
```

---

# **Cache - Validation model**

```java
private final ConcurrentMap<String, LocalDateTime> usersCache;

// Example usage for updating the cache when a playlist is created

// Store the last modification date of the user
LocalDateTime now = LocalDateTime.now();
usersCache.put(playlistCacheKey(playlistId), now);

// Invalidate the cache for all users
usersCache.remove(allPlaylistsCacheKey(ctx.cookie("userNameCookie")));
```

---

# **Cache - Validation model (contd.)**

```java
// Get the last known modification date of the user
LocalDateTime lastKnownModification = ctx.headerAsClass("If-Modified-Since", LocalDateTime.class).getOrDefault(null);

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
```

---

<!-- _class: lead -->

# **Any questions ?**
