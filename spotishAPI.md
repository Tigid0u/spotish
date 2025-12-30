# Spotish - API

> Authors: Alberto de Sousa Lopes, Maikol Correia Da Silva, Nolan Evard

The spotish API allows to manage musics and users in some sort of spotify like type of application. It uses the HTTP protocol on port `8080` and the JSON format.

The JSON format is used to exchange data. The `Content-Type` header must be set to `application/json` when sending data to the API. The `Accept` header must be set to `application/json` when receiving data from the API.

Users are also able to login and logout. They can also access their profile to
validate their information using a cookie.

The API is based on the CRUD pattern. It has the following operations:

**User:**

- Create user
- Get info on a user
- Get all users

**Music:**

- Get 10 last listened musics
- Get 3 most listened musics
- Get a music by its ID
- Get all liked musics
- Like a music

**Playlist:**

- Create playlist
- Get a playlist by its ID
- Get all playlists of a user
- Get all followed playlists
- Follow a playlist
- Add music to a playlist
- Delete music from a playlist

**Artist:**

- Get info on an artist

**Groupe:**

- Get info on a groupe

**Album:**

- Get info on an album

## Endpoints

### User

#### Create user

- `POST /utilisateurs`

Create a new user.

##### Request

The request body must contain a JSON object with the following properties:

- `nomUtilisateur` - The username of the user
- `nom` - The last name of the user
- `prenom` - The first name of the user
- `dateDeNaissance` - The birth date of the user (format: YYYY-MM-DD)
- `email` - The email of the user

##### Response

The response body contains a JSON object with the following properties:

- `nomUtilisateur` - The username of the user
- `nom` - The last name of the user
- `prenom` - The first name of the user
- `dateDeNaissance` - The birth date of the user (format: YYYY-MM-DD)
- `email` - The email of the user

##### Status codes

- `201` (Created) - The user has been successfully created
- `400` (Bad Request) - The request body is invalid
- `409` (Conflict) - The user already exists

#### Get info on a user

- `GET /utilisateurs/{nomUtilisateur}`

Get info on a user.

##### Request

The request path must contain the following parameter:

- `nomUtilisateur` - The username of the user

##### Response

The response body contains a JSON object with the following properties:

- `nomUtilisateur` - The username of the user
- `nom` - The last name of the user
- `prenom` - The first name of the user
- `dateDeNaissance` - The birth date of the user (format: YYYY-MM-DD)
- `email` - The email of the user

##### Status codes

- `200` (OK) - The user has been found
- `404` (Not Found) - The user does not exist

#### Get all users

- `GET /utilisateurs`

Get all users.

##### Request

The request body is empty.

##### Response

The response body contains a JSON array with the following properties:

- `nomUtilisateur` - The username of the user
- `nom` - The last name of the user
- `prenom` - The first name of the user
- `dateDeNaissance` - The birth date of the user (format: YYYY-MM-DD)
- `email` - The email of the user

##### Status codes

- `200` (OK) - The users have been found
- `404` (Not Found) - No users found

---

### Music

#### Get 10 last listened musics

- `GET /musics/last-listened`

Get 10 last listened musics of current user.

##### Request

The request body is empty. The current user is identified by the `user` cookie.

##### Response

The response body contains a JSON array with the following properties:

- `musicId` - The ID of the music
- `title` - The title of the music
- `releaseDate` - The release date of the music (format: YYYY-MM-DD)
- `duration` - The duration of the music (in seconds)
- `genre` - The genre of the music
- `creatorNames` - The name of the creators (artist or groupe) of the music. If multiple creators, they are separated by commas.

##### Status codes

- `200` (OK) - The musics have been found
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - No musics found for the user

#### Get 3 most listened musics

- `GET /musics/most-listened`

Get 3 most listened musics of current user.

##### Request

The request body is empty. The current user is identified by the `user` cookie.

##### Response

The response body contains a JSON array with the following properties:

- `nomUtilisateur` - The username of the user
- `idMedia` - The ID of the music
- `titre` - The title of the music
- `dateDeSortie` - The release date of the music (format: YYYY-MM-DD)
- `duree` - The duration of the music (in seconds)
- `genre` - The genre of the music
- `nomCreateur` - The name of the creator (artist or groupe) of the music

##### Status codes

- `200` (OK) - The musics have been found
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - No musics found for the user

#### Get a music by its ID

- `GET /musics/{idMedia}`

Get a music by its ID.

##### Request

The request path must contain the following parameter:

- `idMedia` - The ID of the music

##### Response

The response body contains a JSON object with the following properties:

- `musicId` - The ID of the music
- `title` - The title of the music
- `releaseDate` - The release date of the music (format: YYYY-MM-DD)
- `duration` - The duration of the music (in seconds)
- `genre` - The genre of the music
- `creatorNames` - The name of the creators (artist or groupe) of the music. If multiple creators, they are separated by commas.

##### Status codes

- `200` (OK) - The music has been found
- `404` (Not Found) - The music does not exist
- `400` (Bad Request) - The request is not correctly formatted

#### Get all liked musics

- `GET /musics/liked`

Get all liked musics of current user.

##### Request

The request body is empty. The current user is identified by the `user` cookie.

##### Response

The response body contains a JSON array with the following properties:

- `nomUtilisateur` - The username of the user
- `idMedia` - The ID of the music
- `titre` - The title of the music
- `dateDeSortie` - The release date of the music (format: YYYY-MM-DD)
- `duree` - The duration of the music (in seconds)
- `genre` - The genre of the music
- `nomCreateur` - The name of the creator (artist or groupe) of the music

##### Status codes

- `200` (OK) - The musics have been found
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - No musics found for the user

#### Like a music

- `POST /musics/liked/{idMedia}`

Like a music for current user.

##### Request

The request path must contain the following parameter:

- `idMedia` - The ID of the music

##### Response

The response body contains a JSON object with the following properties:

- `nomUtilisateur` - The username of the user
- `idMedia` - The ID of the music
- `titre` - The title of the music
- `dateDeSortie` - The release date of the music (format: YYYY-MM-DD)
- `duree` - The duration of the music (in seconds)
- `genre` - The genre of the music
- `nomCreateur` - The name of the creator (artist or groupe) of the music

##### Status codes

- `201` (Created) - The music has been successfully liked
- `400` (Bad Request) - The request is invalid
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - The user or music does not exist

---

### Playlist

#### Create playlist

- `POST /playlists`

Create a new playlist.

##### Request

The request body must contain a JSON object with the following properties:

- `idPlaylist` - The ID of the playlist
- `nom` - The name of the playlist
- `description` - The description of the playlist

##### Response

The response body contains a JSON object with the following properties:

- `idPlaylist` - The ID of the playlist
- `nom` - The name of the playlist
- `description` - The description of the playlist

##### Status codes

- `201` (Created) - The playlist has been successfully created
- `400` (Bad Request) - The request body is invalid
- `401` (Unauthorized) - The user is not logged in
- `409` (Conflict) - The playlist already exists

#### Get a playlist by its ID

- `GET /playlists/{idPlaylist}`

Get a playlist by its ID.

##### Request

The request path must contain the following parameter:

- `idPlaylist` - The ID of the playlist

##### Response

The response body contains a JSON object with the following properties:

- `idPlaylist` - The ID of the playlist
- `nom` - The name of the playlist
- `description` - The description of the playlist

##### Status codes

- `200` (OK) - The playlist has been found
- `404` (Not Found) - The playlist does not exist

#### Get all playlists of a user

- `GET /playlists/user/{nomUtilisateur}`

Get all playlists of a user.

##### Request

The request path must contain the following parameter:

- `nomUtilisateur` - The username of the user

##### Response

The response body contains a JSON array with the following properties:

- `idPlaylist` - The ID of the playlist
- `nom` - The name of the playlist
- `description` - The description of the playlist

##### Status codes

- `200` (OK) - The playlists have been found
- `404` (Not Found) - The user does not exist or has no playlists

#### Get all followed playlists

- `GET /playlists/followed`

Get all followed playlists of current user.

##### Request

The request body is empty. The current user is identified by the `user` cookie.

##### Response

The response body contains a JSON array with the following properties:

- `idPlaylist` - The ID of the playlist
- `nom` - The name of the playlist
- `description` - The description of the playlist

##### Status codes

- `200` (OK) - The playlists have been found
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - No followed playlists found for the user

#### Follow a playlist

- `POST /playlists/followed/{idPlaylist}`

Follow a playlist for current user.

##### Request

The request path must contain the following parameter:

- `idPlaylist` - The ID of the playlist

##### Response

The response body contains a JSON object with the following properties:

- `idPlaylist` - The ID of the playlist
- `nom` - The name of the playlist
- `description` - The description of the playlist

##### Status codes

- `201` (Created) - The playlist has been successfully followed
- `400` (Bad Request) - The request is invalid
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - The user or playlist does not exist

#### Add music to a playlist

- `POST /playlists/{idPlaylist}/musics/{idMedia}`

Add music to a playlist for the current user.

##### Request

The request path must contain the following parameters:

- `idPlaylist` - The ID of the playlist
- `idMedia` - The ID of the music

##### Response

The response body contains a JSON object with the following properties:

- `idPlaylist` - The ID of the playlist
- `idMedia` - The ID of the music

##### Status codes

- `201` (Created) - The music has been successfully added to the playlist
- `400` (Bad Request) - The request is invalid
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - The playlist or music does not exist

#### Delete music from a playlist

- `DELETE /playlists/{idPlaylist}/musics/{idMedia}`

Delete music from a playlist for the current user.

##### Request

The request path must contain the following parameters:

- `idPlaylist` - The ID of the playlist
- `idMedia` - The ID of the music

##### Response

The response body is empty.

##### Status codes

- `204` (No Content) - The music has been successfully deleted from the playlist
- `400` (Bad Request) - The request is invalid
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - The playlist or music does not exist

---

### Artist

#### Get info on an artist

- `GET /artists/{nomArtiste}`

Get info on an artist.

##### Request

The request path must contain the following parameter:

- `nomArtiste` - The name of the artist

##### Response

The response body contains a JSON object with the following properties:

- `nomArtiste` - The name of the artist
- `nom` - The last name of the artist
- `prenom` - The first name of the artist
- `dateDeNaissance` - The birth date of the artist (format: YYYY-MM-DD)
- `email` - The email of the artist
- `nomGroupe` - The name of the groupe of the artist (if any)

with a JSON array with the following properties for all his/her musics:

- `idMedia` - The ID of the music
- `titre` - The title of the music
- `dateDeSortie` - The release date of the music (format: YYYY-MM-DD)
- `duree` - The duration of the music (in seconds)
- `genre` - The genre of the music

with a JSON array with the following properties for all his/her albums:

- `idMedia` - The ID of the album
- `titre` - The title of the album
- `dateDeSortie` - The release date of the album (format: YYYY-MM-DD)

##### Status codes

- `200` (OK) - The artist has been found
- `404` (Not Found) - The artist does not exist

---

### Groupe

#### Get info on a groupe

- `GET /groupes/{nomGroupe}`

Get info on a groupe.

##### Request

The request path must contain the following parameter:

- `nomGroupe` - The name of the groupe

##### Response

The response body contains a JSON object with the following properties:

- `nomGroupe` - The name of the groupe
- `dateDeNaissance` - The birth date of the artist (format: YYYY-MM-DD)
- `email` - The email of the artist

with a JSON array with the following properties for all its artists:

- `nomArtiste` - The name of the artist

with a JSON array with the following properties for all the groupe's musics:

- `idMedia` - The ID of the music
- `titre` - The title of the music
- `dateDeSortie` - The release date of the music (format: YYYY-MM-DD)
- `duree` - The duration of the music (in seconds)
- `genre` - The genre of the music

with a JSON array with the following properties for all the groupe's albums:

- `idMedia` - The ID of the album
- `titre` - The title of the album
- `dateDeSortie` - The release date of the album (format: YYYY-MM-DD)

##### Status codes

- `200` (OK) - The groupe has been found
- `404` (Not Found) - The groupe does not exist

---

### Album

#### Get info on an album

- `GET /albums/{idMedia}`

Get info on an album.

##### Request

The request path must contain the following parameter:

- `idMedia` - The ID of the album

##### Response

The response body contains a JSON object with the following properties:

- `idMedia` - The ID of the album
- `titre` - The title of the album
- `dateDeSortie` - The release date of the album (format: YYYY-MM-DD)
- `nomCreateur` - The name of the creator (artist or groupe) of the album

with a JSON array with the following properties for all its musics:

- `idMedia` - The ID of the music
- `titre` - The title of the music
- `dateDeSortie` - The release date of the music (format: YYYY-MM-DD)
- `duree` - The duration of the music (in seconds)
- `genre` - The genre of the music

##### Status codes

---

### Authentication

#### Login

- `POST /login/{nomUtilisateur}`

Login a user.

##### Request

The request path must contain the following parameter:

- `nomUtilisateur` - The username of the user

##### Response

The response body is empty. A `user` cookie is set with the username of the user.

##### Status codes

- `204` (No Content) - The user has been successfully logged in
- `400` (Bad Request) - The request is invalid
- `404` (Not Found) - The user does not exist

#### Logout

- `POST /logout`

Logout a user.

##### Request

The request body is empty.

##### Response

The response body is empty. The `user` cookie is deleted.

##### Status codes

- `204` (No Content) - The user has been successfully logged out
