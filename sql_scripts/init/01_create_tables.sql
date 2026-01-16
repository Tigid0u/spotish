DROP SCHEMA IF EXISTS spotish CASCADE;
CREATE SCHEMA spotish;

SET SEARCH_PATH TO spotish;

CREATE TABLE utilisateur (
   nomUtilisateur VARCHAR(255),
   nom VARCHAR(255) NOT NULL,
   prenom VARCHAR(255) NOT NULL,
   dateNaissance DATE NOT NULL,
   email VARCHAR(255) UNIQUE,
   PRIMARY KEY (nomUtilisateur)
);

CREATE TABLE createur (
   nomCreateur VARCHAR(255),
   nomGerant VARCHAR(255) NOT NULL,
   PRIMARY KEY (nomCreateur),
   FOREIGN KEY (nomGerant) REFERENCES utilisateur(nomUtilisateur) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE groupe (
   nomGroupe VARCHAR(255),
   PRIMARY KEY (nomGroupe),
   FOREIGN KEY (nomGroupe) REFERENCES createur(nomCreateur) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE artiste (
   nomArtiste VARCHAR(255),
   nomGroupe VARCHAR(255),
   PRIMARY KEY (nomArtiste),
   FOREIGN KEY (nomArtiste) REFERENCES createur(nomCreateur) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY (nomGroupe) REFERENCES groupe(nomGroupe) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE genre (
   nom VARCHAR(255),
   PRIMARY KEY (nom)
);

CREATE TABLE media (
   idMedia BIGSERIAL,
   titre VARCHAR(255) NOT NULL,
   dateDeSortie DATE NOT NULL,
   PRIMARY KEY (idMedia)
);

CREATE TABLE album (
   idAlbum BIGINT,
   PRIMARY KEY (idAlbum),
   FOREIGN KEY (idAlbum) REFERENCES media(idMedia) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE chanson (
   idChanson BIGINT,
   duree INT NOT NULL,
   genre VARCHAR(255) NOT NULL,
   PRIMARY KEY (idChanson),
   FOREIGN KEY (idChanson) REFERENCES media(idMedia) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY (genre) REFERENCES genre(nom) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE album_chanson (
   idAlbum BIGINT,
   idChanson BIGINT,
   PRIMARY KEY (idAlbum, idChanson),
   FOREIGN KEY (idAlbum) REFERENCES album(idAlbum) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY (idChanson) REFERENCES chanson(idChanson) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE createur_media (
   nomCreateur VARCHAR(255),
   idMedia BIGINT,
   PRIMARY KEY (nomCreateur, idMedia),
   FOREIGN KEY (nomCreateur) REFERENCES createur(nomCreateur) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY (idMedia) REFERENCES media(idMedia) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE playlist (
   idPlaylist BIGSERIAL,
   nom VARCHAR(255) NOT NULL,
   description TEXT,
   nomCreateur VARCHAR(255) NOT NULL,
   PRIMARY KEY (idPlaylist),
   FOREIGN KEY (nomCreateur) REFERENCES utilisateur(nomUtilisateur) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE chanson_playlist (
   idChanson BIGINT,
   idPlaylist BIGINT,
   PRIMARY KEY (idChanson, idPlaylist),
   FOREIGN KEY (idChanson) REFERENCES chanson(idChanson) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY (idPlaylist) REFERENCES playlist(idPlaylist) ON DELETE CASCADE ON UPDATE CASCADE
);


-- Pour l'association aimé
CREATE TABLE utilisateur_aime_chanson (
   nomUtilisateur VARCHAR(255),
   idChanson BIGINT,
   PRIMARY KEY (nomUtilisateur, idChanson),
   FOREIGN KEY (nomUtilisateur) REFERENCES utilisateur(nomUtilisateur) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY (idChanson) REFERENCES chanson(idChanson) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE ecoute (
   nomUtilisateur VARCHAR(255),
   idChanson BIGINT,
   dateHeureEcoute TIMESTAMP,
   PRIMARY KEY (nomUtilisateur, idChanson, dateHeureEcoute),
   FOREIGN KEY (nomUtilisateur) REFERENCES utilisateur(nomUtilisateur) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY (idChanson) REFERENCES chanson(idChanson) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE utilisateur_playlist (
   nomUtilisateur VARCHAR(255),
   idPlaylist BIGINT,
   PRIMARY KEY (nomUtilisateur, idPlaylist),
   FOREIGN KEY (nomUtilisateur) REFERENCES utilisateur(nomUtilisateur) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY (idPlaylist) REFERENCES playlist(idPlaylist) ON DELETE CASCADE ON UPDATE CASCADE
);