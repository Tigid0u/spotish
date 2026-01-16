SET SEARCH_PATH TO spotish;

-- CI : Un utilisateur ne peut pas suivre une playlist qu'il a créée.

-- Fonction du trigger
CREATE OR REPLACE FUNCTION suivi_propre_playlist()
    RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
DECLARE
    createur VARCHAR(255);
BEGIN
    -- Récupérer le créateur de la playlist
    SELECT nomcreateur
    INTO createur
    FROM spotish.playlist
    WHERE NEW.idPlaylist = idPlaylist;

    -- Vérifier que l'utilisateur qui suit n'est pas le créateur
    IF NEW.nomUtilisateur = createur THEN
        RAISE EXCEPTION 'Un utilisateur ne peut pas suivre une playlist qu''il a créée.';
    END IF;

    RETURN NEW;
END;
$$;

-- Trigger
DROP TRIGGER IF EXISTS before_utilisateur_playlist_insert ON spotish.utilisateur_playlist;

CREATE TRIGGER before_utilisateur_playlist_insert
    BEFORE INSERT
    ON spotish.utilisateur_playlist
    FOR EACH ROW
EXECUTE FUNCTION suivi_propre_playlist();

-- CI: Un album peut avoir uniquement un seul créateur.

-- Fonction du trigger
CREATE OR REPLACE FUNCTION verifie_album_un_seul_createur()
RETURNS TRIGGER
LANGUAGE plpgsql AS
$$
BEGIN
  -- Only enforce the rule for albums
  IF EXISTS (
    SELECT 1
    FROM spotish.album a
    WHERE a.idalbum = NEW.idmedia
  ) THEN

    -- If the album already has a (different) creator, reject
    IF EXISTS (
      SELECT 1
      FROM spotish.createur_media cm
      WHERE cm.idmedia = NEW.idmedia
        AND cm.nomcreateur <> NEW.nomcreateur
    ) THEN
      RAISE EXCEPTION 'Un album ne peut avoir qu''un seul créateur';
    END IF;

  END IF;
  RETURN NEW;
END;
$$;

-- Trigger
DROP TRIGGER IF EXISTS trigger_un_seul_createur_par_album ON spotish.createur_media;

CREATE TRIGGER trigger_un_seul_createur_par_album
BEFORE INSERT OR UPDATE OF idmedia, nomcreateur
ON spotish.createur_media
FOR EACH ROW
EXECUTE FUNCTION verifie_album_un_seul_createur();

-- CI: Un album contient uniquement des chansons dont l'un des créateurs et le créateur de l'album

-- Fonction trigger
CREATE OR REPLACE FUNCTION verif_album_createur_chanson()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    nb_match INT;
BEGIN
    -- Vérifier qu'il existe au moins un créateur commun
    SELECT COUNT(*) INTO nb_match
    FROM spotish.createur_media cm_album
        INNER JOIN spotish.createur_media cm_chanson
            ON cm_album.nomCreateur = cm_chanson.nomCreateur
    WHERE cm_album.idMedia   = NEW.idAlbum
      AND cm_chanson.idMedia = NEW.idChanson;

    IF nb_match = 0 THEN
        RAISE EXCEPTION
            'Erreur : l''album % ne peut pas contenir la chanson %, car aucun de leurs créateurs ne correspond.',
            NEW.idAlbum, NEW.idChanson;
    END IF;

    RETURN NEW;
END;
$$;

-- Trigger
DROP TRIGGER IF EXISTS trig_verif_album_createur_chanson ON spotish.album_chanson;

CREATE CONSTRAINT TRIGGER trig_verif_album_createur_chanson
    AFTER INSERT OR UPDATE ON spotish.album_chanson
    DEFERRABLE INITIALLY DEFERRED
    FOR EACH ROW
EXECUTE FUNCTION verif_album_createur_chanson();

-- Trigger pour la cardinalité entre créateur et média

-- Pour l'insert
CREATE OR REPLACE FUNCTION verifie_cardinalite_createur_media()
RETURNS TRIGGER
LANGUAGE plpgsql AS
   $$
   BEGIN
      -- Cas 1 : le créateur a au moins un média directement
      IF EXISTS (
         SELECT 1
         FROM spotish.createur_media
         WHERE nomCreateur = NEW.nomCreateur
      ) THEN
         RETURN NEW;
      END IF;

      -- Cas 2 : le créateur est un artiste dans un groupe et le groupe a au moins un média
      IF EXISTS (
         SELECT 1
         FROM spotish.artiste a
         JOIN spotish.createur_media cmg
            ON cmg.nomCreateur = a.nomGroupe
         WHERE a.nomArtiste = NEW.nomCreateur
            AND a.nomGroupe IS NOT NULL
      ) THEN
         RETURN NEW;
      END IF;

      -- Sinon : violation
      RAISE EXCEPTION 'Un créateur doit avoir au moins un média (directement ou via son groupe)';
   END;
   $$;

DROP TRIGGER IF EXISTS trigger_cardinalite_createur_media
ON spotish.createur;

-- Source pour les constraint trigger (provient des slides du cours)
-- https://www.cybertec-postgresql.com/en/triggers-to-enforce-constraints/
CREATE CONSTRAINT TRIGGER trigger_cardinalite_createur_media
AFTER INSERT ON spotish.createur
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION verifie_cardinalite_createur_media();


-- Pour le delete
CREATE OR REPLACE FUNCTION verifie_cardinalite_createur_media_delete()
RETURNS TRIGGER
LANGUAGE plpgsql AS
   $$
   BEGIN
      IF NOT EXISTS (
          SELECT 1
          FROM spotish.createur_media
          WHERE nomCreateur = OLD.nomCreateur
      )
      THEN
         RAISE EXCEPTION 'Le créateur doit avoir au moins un média (suppression du dernier lien interdite)';
      END IF;
      RETURN OLD;
   END;
   $$;

DROP TRIGGER IF EXISTS trigger_cardinalite_createur_media_delete
ON spotish.createur_media;

CREATE CONSTRAINT TRIGGER trigger_cardinalite_createur_media_delete
AFTER DELETE ON spotish.createur_media
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION verifie_cardinalite_createur_media_delete();
---------------------------------------------------------------------


-- Trigger pour la cardinalité entre chanson et playlist
CREATE OR REPLACE FUNCTION verifie_cardinalite_chanson_playlist()
RETURNS TRIGGER
LANGUAGE plpgsql AS
   $$
   BEGIN
      IF NOT EXISTS (
            SELECT 1
            FROM spotish.chanson_playlist
            WHERE idPlaylist = NEW.idPlaylist
         )
      THEN
         RAISE EXCEPTION 'Une playlist doit avoir au moins une chanson';
      END IF;

      RETURN NEW;
   END;
   $$;

DROP TRIGGER IF EXISTS trigger_cardinalite_chanson_playlist
ON spotish.playlist;

CREATE CONSTRAINT TRIGGER trigger_cardinalite_chanson_playlist
AFTER INSERT ON spotish.playlist
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION verifie_cardinalite_chanson_playlist();


-- Pour le delete
CREATE OR REPLACE FUNCTION verifie_cardinalite_chanson_playlist_delete()
RETURNS TRIGGER
LANGUAGE plpgsql AS
   $$
   BEGIN
      IF NOT EXISTS ( -- s'il n'y a plus aucune playlist dans chanson_playlist, cela veut dire que la playlist est vide, du coup, on la supprime
         SELECT 1
         FROM spotish.chanson_playlist
         WHERE idPlaylist = OLD.idPlaylist
      )
      THEN
         DELETE FROM spotish.playlist WHERE idPlaylist = OLD.idPlaylist;
      END IF;

      RETURN OLD;
   END;
   $$;

DROP TRIGGER IF EXISTS trigger_cardinalite_chanson_playlist_delete
ON spotish.chanson_playlist;

CREATE CONSTRAINT TRIGGER trigger_cardinalite_chanson_playlist_delete
AFTER DELETE ON spotish.chanson_playlist
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION verifie_cardinalite_chanson_playlist_delete();
---------------------------------------------------------------------


-- Trigger pour la cardinalité entre album et chanson
CREATE OR REPLACE FUNCTION verifie_cardinalite_album_chanson()
RETURNS TRIGGER
LANGUAGE plpgsql AS
   $$
   BEGIN
      IF NOT EXISTS (
            SELECT 1
            FROM spotish.album_chanson
            WHERE idAlbum = NEW.idAlbum
         )
      THEN
         RAISE EXCEPTION 'Un album doit avoir au moins une chanson';
      END IF;

      RETURN NEW;
   END;
   $$;

DROP TRIGGER IF EXISTS trigger_cardinalite_album_chanson
ON spotish.album;

CREATE CONSTRAINT TRIGGER trigger_cardinalite_album_chanson
AFTER INSERT ON spotish.album
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION verifie_cardinalite_album_chanson();

-- Pour le delete
CREATE OR REPLACE FUNCTION verifie_cardinalite_album_chanson_delete()
RETURNS TRIGGER
LANGUAGE plpgsql AS
   $$
   BEGIN
      IF NOT EXISTS ( -- s'il n'y a plus aucun album dans album_chanson, cela veut dire que l'album est vide, du coup, on le supprime
         SELECT 1
         FROM spotish.album_chanson
         WHERE idAlbum = OLD.idAlbum
      )
      THEN
         DELETE FROM spotish.album WHERE idAlbum = OLD.idAlbum;
      END IF;

      RETURN OLD;
   END;
   $$;

DROP TRIGGER IF EXISTS trigger_cardinalite_album_chanson_delete
ON spotish.album_chanson;

CREATE CONSTRAINT TRIGGER trigger_cardinalite_album_chanson_delete
AFTER DELETE ON spotish.album_chanson
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION verifie_cardinalite_album_chanson_delete();
---------------------------------------------------------------------

-- Trigger Groupe Artiste

CREATE OR REPLACE FUNCTION verifie_cardinalite_groupe_artiste_on_insert_in_groupe()
    RETURNS TRIGGER
    LANGUAGE plpgsql AS $$
DECLARE
    nb_artiste INT;
BEGIN
    SELECT COUNT(*) INTO nb_artiste
    FROM spotish.artiste
    WHERE artiste.nomgroupe = NEW.nomgroupe;

    IF nb_artiste < 2 THEN
        RAISE EXCEPTION 'Un groupe a besoin de 2 artistes minimum';
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trigger_cardinalite_groupe_artiste_on_insert_in_groupe ON spotish.groupe;

CREATE CONSTRAINT TRIGGER trigger_cardinalite_groupe_artiste_on_insert_in_groupe
AFTER INSERT ON spotish.groupe
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION verifie_cardinalite_groupe_artiste_on_insert_in_groupe();

-- Trigger pour que la suppression d'un artiste d'un groupe où il reste moins de 2 artistes dans le groupe lève une exception
CREATE OR REPLACE FUNCTION verifie_cardinalite_groupe_artiste_on_delete_in_artiste()
    RETURNS TRIGGER
    LANGUAGE plpgsql AS $$
DECLARE
    nb_artiste INT;
BEGIN
    SELECT COUNT(*) INTO nb_artiste
    FROM spotish.artiste
    WHERE spotish.artiste.nomgroupe = OLD.nomgroupe;

    IF nb_artiste < 2 THEN
        RAISE EXCEPTION 'Un groupe a besoin de 2 artistes minimum';
    END IF;

    RETURN OLD;
END;
$$;

-- Trigger
DROP TRIGGER IF EXISTS trigger_cardinalite_groupe_artiste_on_delete_in_artiste ON spotish.artiste;

CREATE CONSTRAINT TRIGGER trigger_cardinalite_groupe_artiste_on_delete_in_artiste
AFTER DELETE ON spotish.artiste
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION verifie_cardinalite_groupe_artiste_on_delete_in_artiste();