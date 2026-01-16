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
# **API**
- `/users`
  - `POST /users` : Create a new user
  - `GET /users/{username}` : Get user info
  - `GET /users` : Get all users
---
# **API**
- `/musics`
  - `GET /musics/last-listened` : Get last listened musics
  - `GET /musics/most-listened` : Get most listened musics
  - `GET /musics/{idMedia}` : Get music info by id
  - `GET /musics/{title}` : Get music info by title
  - `GET /musics` : Get all musics
  - `GET /musics/liked` : Get all liked musics from current user
  - `POST /musics/liked/{idMedia}` : Like a music for current user
---
# **API**
- `/playlists`
  - `POST /playlists` : Create a new playlist
  - `GET /playlists/{idPlaylist}` : Get playlist info by id
  - `GET /playlists/user/{username}` : Get all playlists from a user
  - `GET /playlists/followed` : Get all followed playlists from current user
  - `POST /playlists/followed/{idPlaylist}` : Follow a playlist for current user
  - `POST /playlists/{idPlaylist}/musics/{idMedia}` : Add a music to a playlist
  - `DELETE /playlists/{idPlaylist}/musics/{idMedia}` : Remove a music from a playlist

---
# **API**
- `/creators`
  - `GET /creators/{creatorName}` : Get creator info by name
- `/albums`
  - `GET /albums/{idMedia}` : Get album info by id
- `/login`
  - `POST /login/{username}` : Login a user
  - `POST /logout` : Logout the current user

---
# **Cache - Expiration model**

``` java
Code java ici albertooooooo
```

---
# **Cache - Validation model**

``` java
Code java ici albertooooooo
```

---
<!-- _class: lead -->
# **Any questions ?**
