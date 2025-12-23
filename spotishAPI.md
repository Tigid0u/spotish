# Spotish - API

The spotish API allows to manage musics and users in some sort of a spotify like type of application. It uses the HTTP protocol on port `8080` and the JSON format.

The JSON format is used to exchange data. The `Content-Type` header must be set to `application/json` when sending data to the API. The `Accept` header must be set to `application/json` when receiving data from the API.

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

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Get all users

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

---

### Music

#### Get 10 last listened musics

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Get 3 most listened musics

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Get a music by its ID

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Get all liked musics

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Like a music

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

---

### Playlist

#### Create playlist

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Get a playlist by its ID

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Get all playlists of a user

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Get all followed playlists

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Follow a playlist

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Add music to a playlist

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

#### Delete music from a playlist

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

--- 

### Artist

#### Get info on an artist

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

---

### Groupe

#### Get info on a groupe

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes

---

### Album

#### Get info on an album

##### Request

The request body must contain a JSON object with the following properties:

##### Response

The response body contains a JSON object with the following properties:

##### Status codes
