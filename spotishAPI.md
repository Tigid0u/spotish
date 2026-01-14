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

- Get 10 last listened musics (uses cache expiration model)
- Get 3 most listened musics (uses cache expiration model)
- Get a music by its ID
- Get a music by its title (uses cache expiration model)
- Get all musics (uses cache expiration model)
- Get all liked musics (uses cache expiration model)
- Like a music

**Playlist:**

- Create playlist (uses cache validation model)
- Get a playlist by its ID (uses cache validation model)
- Get all playlists of a user (uses cache validation model)
- Get all followed playlists (uses cache validation model)
- Follow a playlist
- Add music to a playlist (uses cache validation model)
- Delete music from a playlist (uses cache validation model)

**Artist:**

- Get info on an artist

**Groupe:**

- Get info on a groupe

**Album:**

- Get info on an album

## Endpoints

### User

#### Create user

- `POST /users`

Create a new user.

##### Request

The request body must contain a JSON object with the following properties:

- `username` - The username of the user
- `lname` - The last name of the user
- `fname` - The first name of the user
- `birthdate` - The birthdate of the user (format: YYYY-MM-DD)
- `email` - The email of the user

##### Response

The response body contains a JSON object with the following properties:

- `username` - The username of the user
- `lname` - The last name of the user
- `fname` - The first name of the user
- `birthdate` - The birthdate of the user (format: YYYY-MM-DD)
- `email` - The email of the user

##### Status codes

- `201` (Created) - The user has been successfully created
- `400` (Bad Request) - The request body is invalid
- `409` (Conflict) - The user already exists

#### Get info on a user

- `GET /users/{username}`

Get info on a user.

##### Request

The request path must contain the following parameter:

- `username` - The username of the user

##### Response

The response body contains a JSON object with the following properties:

- `username` - The username of the user
- `lname` - The last name of the user
- `fname` - The first name of the user
- `birthdate` - The birthdate of the user (format: YYYY-MM-DD)
- `email` - The email of the user

##### Status codes

- `200` (OK) - The user has been found
- `404` (Not Found) - The user does not exist

#### Get all users

- `GET /users`

Get all users.

##### Request

The request body is empty.

##### Response

The response body contains a JSON array with the following properties:

- `username` - The username of the user
- `lname` - The last name of the user
- `fname` - The first name of the user
- `birthdate` - The birthdate of the user (format: YYYY-MM-DD)
- `email` - The email of the user

##### Status codes

- `200` (OK) - The users have been found
- `404` (Not Found) - No users found

---

### Music

#### Get 10 last listened musics

- `GET /musics/last-listened`

Get 10 last listened musics of current user. Uses cache expiration model.

`Cache-Control: max-age=<number of seconds>`

`<number of seconds>` = 1 minutes = 60 seconds, because the last listened musics can change frequently.

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

Get 3 most listened musics of current user. Uses cache expiration model.

`Cache-Control: max-age=<number of seconds>`

`<number of seconds>` = 10 minutes = 600 seconds, because the most listened musics can change frequently.

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

#### Get a music by its title

- `GET /musics/{title}`

Get a music by its title. Uses cache expiration model.

`Cache-Control: max-age=<number of seconds>`

`<number of seconds>` = 30 minutes =  1800 seconds, because the music title data does not change frequently.

##### Request

The request path must contain the following parameter:

- `title` : title of the music

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

#### Get all musics

- `GET /musics`

Get all musics in the database. Uses cache expiration model.

`Cache-Control: max-age=<number of seconds>`

`<number of seconds>` = 1 hour = 3600 seconds, because the music data does not change frequently.

##### Request

The request body is empty.

##### Response

The response body contains a JSON array with the following properties:

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

Get all liked musics of current user. Uses cache expiration model.

`Cache-Control: max-age=<number of seconds>`

`<number of seconds>` = 5 minutes = 300 seconds, because the liked musics can change frequently.

##### Request

The request body is empty. The current user is identified by the `user` cookie.

##### Response

The response body contains a JSON array with the following properties:

- `musicId` - The ID of the music
- `title` - The title of the music
- `releaseDate` - The release date of the music (format: YYYY-MM-DD)
- `duration` - The duration of the music (in seconds)
- `genre` - The genre of the music
- `creatorNames` - The name of the creator (artist or groupe) of the music

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

- `musicId` - The ID of the music
- `title` - The title of the music
- `releaseDate` - The release date of the music (format: YYYY-MM-DD)
- `duration` - The duration of the music (in seconds)
- `genre` - The genre of the music
- `creatorNames` - The name of the creator (artist or groupe) of the music

##### Status codes

- `201` (Created) - The music has been successfully liked
- `400` (Bad Request) - The request is invalid
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - The user or music does not exist

---

### Playlist

#### Create playlist

- `POST /playlists`

Create a new playlist. Uses cache validation model.

##### Request

The request body must contain a JSON object with the following properties:

- `name` - The name of the playlist
- `decription` - The description of the playlist
- `musics` - A JSON array of music IDs to be added to the playlist that must contain at least one music ID and is structured as follows:

  ```json
  [{ "musicId": 1 }, { "musicId": 2 }, { "musicId": 3 }]
  ```

##### Response

The response body is empty.

##### Status codes

- `201` (Created) - The playlist has been successfully created
- `400` (Bad Request) - The request body is invalid
- `401` (Unauthorized) - The user is not logged in
- `409` (Conflict) - The playlist already exists

#### Get a playlist by its ID

- `GET /playlists/{idPlaylist}`

Get a playlist by its ID. Uses cache validation model.

##### Request

The request path must contain the following parameter:

- `idPlaylist` - The ID of the playlist

##### Response

The response body contains a JSON object with the following properties:

- `id` - The ID of the playlist
- `name` - The name of the playlist
- `description` - The description of the playlist
- `musics` - A JSON array of musics in the playlist with the following properties:
  - `musicId` - The ID of the music
  - `title` - The title of the music
  - `releaseDate` - The release date of the music (format: YYYY-MM-DD)
  - `duration` - The duration of the music (in seconds)
  - `genre` - The genre of the music
  - `creatorNames` - The name of the creators (artist or groupe) of the music. If multiple creators, they are separated by commas.

##### Status codes

- `200` (OK) - The playlist has been found
- `304` (Not Modified) - The playlist has not been retrieved because the cache is still valid
- `404` (Not Found) - The playlist does not exist

#### Get all playlists of a user

- `GET /playlists/user/{username}`

Get all playlists of a user. Uses cache validation model.

##### Request

The request path must contain the following parameter:

- `username` - The username of the user

##### Response

The response body contains a JSON array with all playlists of the user with the following properties:

- `id` - The ID of the playlist
- `name` - The name of the playlist
- `description` - The description of the playlist
- `musics` - A JSON array of musics in the playlist with the following properties:
  - `musicId` - The ID of the music
  - `title` - The title of the music
  - `releaseDate` - The release date of the music (format: YYYY-MM-DD)
  - `duration` - The duration of the music (in seconds)
  - `genre` - The genre of the music
  - `creatorNames` - The name of the creators (artist or groupe) of the music. If multiple creators, they are separated by commas.

##### Status codes

- `200` (OK) - The playlists have been found
- `304` (Not Modified) - The playlists have not been retrieved because the cache is still valid
- `404` (Not Found) - The user does not exist or has no playlists

#### Get all followed playlists

- `GET /playlists/followed`

Get all followed playlists of current user. Uses cache validation model.

##### Request

The request body is empty. The current user is identified by the `user` cookie.

##### Response

The response body contains a JSON array of followed playlists with the following properties:

- `id` - The ID of the playlist
- `name` - The name of the playlist
- `description` - The description of the playlist
- `musics` - A JSON array of musics in the playlist with the following properties:
  - `musicId` - The ID of the music
  - `title` - The title of the music
  - `releaseDate` - The release date of the music (format: YYYY-MM-DD)
  - `duration` - The duration of the music (in seconds)
  - `genre` - The genre of the music
  - `creatorNames` - The name of the creators (artist or groupe) of the music. If multiple creators, they are separated by commas.

##### Status codes

- `200` (OK) - The playlists have been found
- `304` (Not Modified) - The playlists have not been retrieved because the cache is still valid
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - No followed playlists found for the user

#### Follow a playlist

- `POST /playlists/followed/{idPlaylist}`

Follow a playlist for current user.

##### Request

The request path must contain the following parameter:

- `idPlaylist` - The ID of the playlist

##### Response

The response body is empty.

##### Status codes

- `201` (Created) - The playlist has been successfully followed
- `400` (Bad Request) - The request is invalid
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - The user or playlist does not exist

#### Add music to a playlist

- `POST /playlists/{idPlaylist}/musics/{idMedia}`

Add music to a playlist for the current user. Uses cache validation model.

##### Request

The request path must contain the following parameters:

- `idPlaylist` - The ID of the playlist
- `idMedia` - The ID of the music

##### Response

The response body is empty.

##### Status codes

- `201` (Created) - The music has been successfully added to the playlist
- `400` (Bad Request) - The request is invalid
- `401` (Unauthorized) - The user is not logged in
- `404` (Not Found) - The playlist or music does not exist
- `412` (Precondition Failed) - The cache is not valid

#### Delete music from a playlist

- `DELETE /playlists/{idPlaylist}/musics/{idMedia}`

Delete music from a playlist for the current user. Uses cache validation model.

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
- `412` (Precondition Failed) - The cache is not valid

---

### Creator

#### Get info on a creator (artist or groupe)

- `GET /creators/{creatorName}`

##### Request

The request path must contain the following parameter:

- `creatorName` - The name of the creator (artist or groupe)

##### Response

The response body contains a JSON object with the following properties:

- `creatorName` - The name of the creator
- `artistList` - A JSON array with the artists names of the groupe. If null, the creator is an artist.
- `albums` - A JSON array of albums of the creator with the following properties:
  - `id` - The ID of the album
  - `title` - The title of the album
  - `releaseDate` - The release date of the album (format: YYYY-MM-DD)
  - `musics` - A JSON array of musics in the album with the following properties:
    - `idMedia` - The ID of the music
    - `title` - The title of the music
    - `releaseDate` - The release date of the music (format: YYYY-MM-DD)
    - `duration` - The duration of the music (in seconds)
    - `genre` - The genre of the music
    - `creatorNames` - The name of the creators (artist or groupe) of the music. If multiple creators, they are separated by commas.

##### Status codes

- `200` (OK) - The creator has been found
- `404` (Not Found) - The creator does not exist
- `400` (Bad Request) - The request is not correctly formatted

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

- `id` - The ID of the album
- `title` - The title of the album
- `releaseDate` - The release date of the album (format: YYYY-MM-DD)
- `creatorName` - The name of the creator (artist or groupe) of the album
- `musics` - A JSON array of musics in the album with the following properties:
  - `musicId` - The ID of the music
  - `title` - The title of the music
  - `releaseDate` - The release date of the music (format: YYYY-MM-DD)
  - `duration` - The duration of the music (in seconds)
  - `genre` - The genre of the music
  - `creatorNames` - The name of the creators (artist or groupe) of the music. If multiple creators, they are separated by commas.

##### Status codes

- `200` (OK) - The album has been found
- `404` (Not Found) - The album does not exist
- `400` (Bad Request) - The request is not correctly formatted

---

### Authentication

#### Login

- `POST /login/{username}`

Login a user.

##### Request

The request path must contain the following parameter:

- `username` - The username of the user

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
