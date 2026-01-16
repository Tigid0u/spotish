/*
===============================================================================
  Projet : Spotish
  Description : Script d’initialisation des données de la base de données

  Remarque :
  Ce jeu de données a été généré avec l’assistance de ChatGPT (OpenAI),
  puis adapté, structuré et validé manuellement afin de respecter
  l’ensemble des contraintes d’intégrité et des triggers définis
  dans le schéma Spotish.

  Objectif :
  - Fournir un dataset cohérent et réaliste
  - Respecter les contraintes fonctionnelles et les triggers
  - Servir de base pour les tests et démonstrations

  Généré avec assistance IA : ChatGPT
  Validation finale : Étudiant (contrôle et ajustements manuels)

===============================================================================
*/


SET SEARCH_PATH TO spotish;

BEGIN;

---------------------------------------------------------------------
-- 1) UTILISATEUR (28 utilisateurs)
-- Certains sont gérants, d’autres non : cela rend la base plus réaliste.
---------------------------------------------------------------------
INSERT INTO utilisateur VALUES
('amelie.paris','Paris','Amélie','1995-03-21','amelie.paris@example.com'),
('lucas.durand','Durand','Lucas','1990-11-12','lucas.durand@example.com'),
('marie.morin','Morin','Marie','1993-06-07','marie.morin@example.com'),
('nathan.girard','Girard','Nathan','1991-02-14','nathan.girard@example.com'),
('lea.moreau','Moreau','Léa','1998-08-30','lea.moreau@example.com'),
('julien.bernard','Bernard','Julien','1994-04-10','julien.bernard@example.com'),
('claire.petit','Petit','Claire','1999-10-05','claire.petit@example.com'),
('adrien.lefevre','Lefevre','Adrien','1988-09-18','adrien.lefevre@example.com'),
('julie.robert','Robert','Julie','1992-12-24','julie.robert@example.com'),
('maxime.dubois','Dubois','Maxime','1997-01-15','maxime.dubois@example.com'),
('antoine.marchal','Marchal','Antoine','1996-02-02','antoine.marchal@example.com'),
('sarah.muller','Muller','Sarah','1993-03-19','sarah.muller@example.com'),
('quentin.roux','Roux','Quentin','1991-05-26','quentin.roux@example.com'),
('emma.perrin','Perrin','Emma','1998-07-11','emma.perrin@example.com'),
('victor.fontaine','Fontaine','Victor','1990-09-09','victor.fontaine@example.com'),
('pauline.giraud','Giraud','Pauline','1997-04-27','pauline.giraud@example.com'),
('louis.blanc','Blanc','Louis','1995-01-08','louis.blanc@example.com'),
('ines.martin','Martin','Inès','1996-06-14','ines.martin@example.com'),
('tom.rey','Rey','Tom','1994-11-05','tom.rey@example.com'),
('manon.fabre','Fabre','Manon','1999-03-01','manon.fabre@example.com'),
('hugo.leroy','Leroy','Hugo','1992-02-20','hugo.leroy@example.com'),
('sophie.renaud','Renaud','Sophie','1993-09-03','sophie.renaud@example.com'),
('alexandre.gautier','Gautier','Alexandre','1989-10-12','alexandre.gautier@example.com'),
('chloe.renard','Renard','Chloé','1997-06-02','chloe.renard@example.com'),
('thomas.dupuis','Dupuis','Thomas','1990-01-30','thomas.dupuis@example.com'),
('lea.marchand','Marchand','Léa','1998-05-09','lea.marchand@example.com'),
('yannis.collet','Collet','Yannis','1995-08-18','yannis.collet@example.com'),
('camille.picard','Picard','Camille','1994-12-07','camille.picard@example.com');

---------------------------------------------------------------------
-- 2) CREATEUR : Groupes + Membres + Artistes solo
-- Règle : un utilisateur peut gérer 0, 1 ou 2 créateurs.
-- S'il en gère 2 → c'est toujours un groupe + un membre du groupe.
---------------------------------------------------------------------

-- ========================
-- GROUPES (6 créateurs)
-- ========================
INSERT INTO createur VALUES
('Imagine Dragons', 'amelie.paris'),     -- Amélie gère le groupe ET son chanteur Dan Reynolds
('Coldplay',        'lea.moreau'),       -- Léa gère le groupe ET Chris Martin
('Muse',            'julie.robert'),     -- Julie gère Muse ET Matthew Bellamy
('Daft Punk',       'pauline.giraud'),   -- Pauline gère Daft Punk ET Thomas Bangalter
('Arctic Monkeys',  'sarah.muller'),     -- Sarah gère Arctic Monkeys ET Alex Turner
('Radiohead',       'ines.martin');      -- Inès gère Radiohead ET Thom Yorke

-- ========================
-- MEMBRES DES GROUPES
-- ========================

-- Imagine Dragons (4 membres)
INSERT INTO createur VALUES
('Dan Reynolds', 'amelie.paris'),
('Wayne Sermon','lucas.durand'),
('Ben McKee','marie.morin'),
('Daniel Platzman','nathan.girard');

-- Coldplay (4 membres)
INSERT INTO createur VALUES
('Chris Martin','lea.moreau'),
('Jonny Buckland','julien.bernard'),
('Guy Berryman','claire.petit'),
('Will Champion','adrien.lefevre');

-- Muse (3 membres)
INSERT INTO createur VALUES
('Matthew Bellamy','julie.robert'),
('Chris Wolstenholme','maxime.dubois'),
('Dominic Howard','antoine.marchal');

-- Arctic Monkeys (4 membres)
INSERT INTO createur VALUES
('Alex Turner','sarah.muller'),
('Jamie Cook','quentin.roux'),
('Nick O''Malley','emma.perrin'),
('Matt Helders','victor.fontaine');

-- Daft Punk (2 membres)
INSERT INTO createur VALUES
('Thomas Bangalter','pauline.giraud'),
('Guy-Manuel de Homem-Christo','louis.blanc');

-- Radiohead (5 membres)
INSERT INTO createur VALUES
('Thom Yorke','ines.martin'),
('Jonny Greenwood','lucas.durand'),
('Ed O''Brien','marie.morin'),
('Colin Greenwood','nathan.girard'),
('Phil Selway','julien.bernard');

-- ========================
-- ARTISTES SOLO
-- ========================
INSERT INTO createur VALUES
('Ed Sheeran','tom.rey'),
('Adele','manon.fabre'),
('Billie Eilish','hugo.leroy'),
('The Weeknd','sophie.renaud');

---------------------------------------------------------------------
-- 3) GROUPES → Ils doivent correspondre aux créateurs ci-dessus
---------------------------------------------------------------------
INSERT INTO groupe VALUES
('Imagine Dragons'),('Coldplay'),('Muse'),
('Daft Punk'),('Arctic Monkeys'),('Radiohead');

---------------------------------------------------------------------
-- 4) ARTISTE → Rattache chaque membre à son groupe + les solos en NULL
---------------------------------------------------------------------
INSERT INTO artiste VALUES
-- Imagine Dragons
('Dan Reynolds','Imagine Dragons'),('Wayne Sermon','Imagine Dragons'),
('Ben McKee','Imagine Dragons'),('Daniel Platzman','Imagine Dragons'),

-- Coldplay
('Chris Martin','Coldplay'),('Jonny Buckland','Coldplay'),
('Guy Berryman','Coldplay'),('Will Champion','Coldplay'),

-- Muse
('Matthew Bellamy','Muse'),('Chris Wolstenholme','Muse'),('Dominic Howard','Muse'),

-- Arctic Monkeys
('Alex Turner','Arctic Monkeys'),('Jamie Cook','Arctic Monkeys'),
('Nick O''Malley','Arctic Monkeys'),('Matt Helders','Arctic Monkeys'),

-- Daft Punk
('Thomas Bangalter','Daft Punk'),('Guy-Manuel de Homem-Christo','Daft Punk'),

-- Radiohead
('Thom Yorke','Radiohead'),('Jonny Greenwood','Radiohead'),
('Ed O''Brien','Radiohead'),('Colin Greenwood','Radiohead'),('Phil Selway','Radiohead'),

-- Artistes solo
('Ed Sheeran',NULL),('Adele',NULL),('Billie Eilish',NULL),('The Weeknd',NULL);

---------------------------------------------------------------------
-- 5) GENRE
---------------------------------------------------------------------
INSERT INTO genre VALUES ('Rock'),('Pop'),('Electro'),('Indie'),('Hip-Hop');

---------------------------------------------------------------------
-- 6) MEDIA (ALBUMS + CHANSONS)
-- Les albums 1..20
-- Les chansons 21..60 (les dernières ajoutées permettent 1–5 chansons/album)
---------------------------------------------------------------------

-- ========================
-- 20 ALBUMS
-- ========================
INSERT INTO media (titre, dateDeSortie) VALUES
('Night Visions','2012-09-04'),        -- 1 - Imagine Dragons
('Evolve','2017-06-23'),               -- 2
('Parachutes','2000-07-10'),           -- 3 - Coldplay
('A Rush of Blood to the Head','2002-08-26'), -- 4
('Absolution','2003-09-15'),           -- 5 - Muse
('Black Holes and Revelations','2006-07-03'),-- 6
('Homework','1997-01-20'),             -- 7 - Daft Punk
('Discovery','2001-03-12'),            -- 8
('Whatever People Say I Am...','2006-01-23'), -- 9 - Arctic Monkeys
('AM','2013-09-09'),                   -- 10
('OK Computer','1997-05-21'),          -- 11 - Radiohead
('In Rainbows','2007-10-10'),          -- 12
('+','2011-09-09'),                     -- 13 - Ed Sheeran
('÷','2017-03-03'),                     -- 14
('19','2008-01-28'),                    -- 15 - Adele
('21','2011-01-24'),                    -- 16
('When We All Fall Asleep...','2019-03-29'), -- 17 - Billie Eilish
('Happier Than Ever','2021-07-30'),     -- 18
('Beauty Behind the Madness','2015-08-28'), -- 19 - The Weeknd
('After Hours','2020-03-20');           -- 20

-- ========================
-- Chansons (21..60)
-- ========================
INSERT INTO media (titre, dateDeSortie) VALUES
('Believer','2017-02-01'),
('Demons','2013-01-28'),
('Yellow','2000-06-26'),
('Viva la Vida','2008-06-12'),
('Hysteria','2003-12-01'),
('Starlight','2006-09-04'),
('One More Time','2000-11-13'),
('Harder, Better, Faster, Stronger','2001-10-13'),
('Do I Wanna Know?','2013-06-19'),
('R U Mine?','2012-02-27'),
('Karma Police','1997-08-25'),
('No Surprises','1998-01-12'),
('Shape of You','2017-01-06'),
('Perfect','2017-03-03'),
('Rolling in the Deep','2010-11-29'),
('Someone Like You','2011-01-24'),
('Bad Guy','2019-03-29'),
('Therefore I Am','2020-11-12'),
('Blinding Lights','2019-11-29'),
('Save Your Tears','2020-03-20'),
('Radioactive','2012-10-29'),
('On Top of the World','2013-03-18'),
('Thunder','2017-04-27'),
('Trouble','2000-10-23'),
('Clocks','2002-08-26'),
('Time Is Running Out','2003-09-08'),
('Supermassive Black Hole','2006-07-10'),
('Around the World','1997-03-17'),
('Da Funk','1995-05-30'),
('Digital Love','2001-06-11'),
('When the Sun Goes Down','2006-01-16'),
('Mardy Bum','2006-01-23'),
('Why''d You Only Call Me When You''re High?','2013-08-11'),
('Paranoid Android','1997-05-26'),
('Nude','2008-01-31'),
('The A Team','2011-06-12'),
('Lego House','2011-11-13'),
('Castle on the Hill','2017-01-06'),
('Chasing Pavements','2008-01-13'),
('Set Fire to the Rain','2011-07-04');

---------------------------------------------------------------------
-- 7) ALBUM TABLE: idAlbum = 1..20
---------------------------------------------------------------------
INSERT INTO album VALUES
(1),(2),(3),(4),(5),(6),(7),(8),
(9),(10),(11),(12),(13),(14),(15),
(16),(17),(18),(19),(20);

---------------------------------------------------------------------
-- 8) CHANSON TABLE: durations + genre
---------------------------------------------------------------------
INSERT INTO chanson VALUES
(21,204,'Rock'),(22,176,'Rock'),(23,260,'Pop'),(24,242,'Pop'),
(25,230,'Rock'),(26,240,'Rock'),(27,320,'Electro'),(28,224,'Electro'),
(29,272,'Indie'),(30,210,'Indie'),(31,260,'Rock'),(32,228,'Rock'),
(33,234,'Pop'),(34,263,'Pop'),(35,228,'Pop'),(36,285,'Pop'),
(37,194,'Pop'),(38,174,'Pop'),(39,200,'Pop'),(40,215,'Pop'),
(41,186,'Rock'),(42,177,'Rock'),(43,187,'Rock'),
(44,269,'Pop'),(45,304,'Pop'),
(46,238,'Rock'),(47,215,'Rock'),
(48,431,'Electro'),(49,330,'Electro'),(50,300,'Electro'),
(51,199,'Indie'),(52,210,'Indie'),(53,223,'Indie'),
(54,386,'Rock'),(55,254,'Rock'),
(56,225,'Pop'),(57,203,'Pop'),(58,261,'Pop'),
(59,228,'Pop'),(60,240,'Pop');

---------------------------------------------------------------------
-- 9) ALBUM_CHANSON
-- Association album ↔ chansons
-- Maintenant les albums ont entre 1 et 5 chansons
---------------------------------------------------------------------

-- Night Visions (Imagine Dragons)
INSERT INTO album_chanson VALUES
(1,21),(1,41),(1,42);

-- Evolve
INSERT INTO album_chanson VALUES
(2,22),(2,43);

-- Parachutes
INSERT INTO album_chanson VALUES
(3,23),(3,44);

-- A Rush of Blood to the Head
INSERT INTO album_chanson VALUES
(4,24),(4,45);

-- Absolution
INSERT INTO album_chanson VALUES
(5,25),(5,46);

-- Black Holes and Revelations
INSERT INTO album_chanson VALUES
(6,26),(6,47);

-- Homework
INSERT INTO album_chanson VALUES
(7,27),(7,48),(7,49);

-- Discovery
INSERT INTO album_chanson VALUES
(8,28),(8,50);

-- Whatever People Say I Am...
INSERT INTO album_chanson VALUES
(9,29),(9,51),(9,52);

-- AM
INSERT INTO album_chanson VALUES
(10,30),(10,53);

-- OK Computer
INSERT INTO album_chanson VALUES
(11,31),(11,54);

-- In Rainbows
INSERT INTO album_chanson VALUES
(12,32),(12,55);

-- + (Ed Sheeran)
INSERT INTO album_chanson VALUES
(13,33),(13,56),(13,57);

-- ÷
INSERT INTO album_chanson VALUES
(14,34),(14,58);

-- 19 (Adele)
INSERT INTO album_chanson VALUES
(15,35),(15,59);

-- 21 (Adele)
INSERT INTO album_chanson VALUES
(16,36),(16,60);

-- Billie Eilish albums (1 chanson chacun)
INSERT INTO album_chanson VALUES (17,37),(18,38);

-- The Weeknd albums (1 chanson chacun)
INSERT INTO album_chanson VALUES (19,39),(20,40);

---------------------------------------------------------------------
-- 10) CREATEUR_MEDIA : artistes → albums et chansons
-- Dans ce modèle, un groupe est lié à l'ensemble des chansons de l'album.
---------------------------------------------------------------------
INSERT INTO createur_media VALUES
-- Albums
('Imagine Dragons',1),('Imagine Dragons',2),
('Coldplay',3),('Coldplay',4),
('Muse',5),('Muse',6),
('Daft Punk',7),('Daft Punk',8),
('Arctic Monkeys',9),('Arctic Monkeys',10),
('Radiohead',11),('Radiohead',12),
('Ed Sheeran',13),('Ed Sheeran',14),
('Adele',15),('Adele',16),
('Billie Eilish',17),('Billie Eilish',18),
('The Weeknd',19),('The Weeknd',20),

-- chansons originales
('Imagine Dragons',21),('Imagine Dragons',22),
('Coldplay',23),('Coldplay',24),
('Muse',25),('Muse',26),
('Daft Punk',27),('Daft Punk',28),
('Arctic Monkeys',29),('Arctic Monkeys',30),
('Radiohead',31),('Radiohead',32),
('Ed Sheeran',33),('Ed Sheeran',34),
('Adele',35),('Adele',36),
('Billie Eilish',37),('Billie Eilish',38),
('The Weeknd',39),('The Weeknd',40),

-- nouvelles chansons par groupe / artiste
('Imagine Dragons',41),('Imagine Dragons',42),('Imagine Dragons',43),
('Coldplay',44),('Coldplay',45),
('Muse',46),('Muse',47),
('Daft Punk',48),('Daft Punk',49),('Daft Punk',50),
('Arctic Monkeys',51),('Arctic Monkeys',52),('Arctic Monkeys',53),
('Radiohead',54),('Radiohead',55),
('Ed Sheeran',56),('Ed Sheeran',57),('Ed Sheeran',58),
('Adele',59),('Adele',60);

---------------------------------------------------------------------
-- 11) PLAYLIST + 12) CHANSON_PLAYLIST
---------------------------------------------------------------------
INSERT INTO playlist (nom,description,nomCreateur) VALUES
('Rock Legends','Grands classiques rock','lucas.durand'),
('Pop Mix','Sélection pop moderne','amelie.paris'),
('Chill Vibes','Ambiance détente','marie.morin'),
('Electro Break','Best of électro','julie.robert'),
('Indie Essentials','Indie rock incontournable','nathan.girard'),
('Workout Boost','Playlist sport','lea.moreau'),
('Road Trip','Pour la route','maxime.dubois'),
('Best of 2000s','Hits années 2000','adrien.lefevre'),
('Acoustic Mood','Acoustique douce','claire.petit'),
('Golden Hits','Grands succès','julien.bernard'),
('Hip-Hop Flow','Hip-hop','quentin.roux'),
('French Touch','Variété française','emma.perrin'),
('Dream Pop','Pop atmosphérique','antoine.marchal'),
('Hard Rock Power','Rock puissant','victor.fontaine'),
('Classics','Classiques intemporels','pauline.giraud'),
('Indie Pop','Indie pop variée','louis.blanc'),
('Morning Coffee','Pour le matin','ines.martin'),
('Night Drive','Route de nuit','tom.rey'),
('Relaxation','Relax','manon.fabre'),
('Top Hits','Hits du moment','sarah.muller');

INSERT INTO chanson_playlist VALUES
(21,1),(22,1),(23,2),(33,2),(25,3),(26,3),
(27,4),(28,4),(29,5),(30,5),
(31,10),(32,10),(34,16),(35,15),(36,15),
(37,11),(38,11),(39,19),(40,20);

---------------------------------------------------------------------
-- PATCH B) Ajouter au moins 1 chanson aux playlists vides
---------------------------------------------------------------------
INSERT INTO chanson_playlist (idChanson, idPlaylist) VALUES
-- 6) Workout Boost
(33, 6),  -- Shape of You

-- 7) Road Trip
(29, 7),  -- Do I Wanna Know?

-- 8) Best of 2000s
(24, 8),  -- Viva la Vida

-- 9) Acoustic Mood
(56, 9),  -- The A Team

-- 12) French Touch
(28, 12), -- Harder, Better, Faster, Stronger

-- 13) Dream Pop
(44, 13), -- Trouble

-- 14) Hard Rock Power
(46, 14), -- Time Is Running Out

-- 17) Morning Coffee
(36, 17), -- Someone Like You

-- 18) Night Drive
(39, 18); -- Blinding Lights


---------------------------------------------------------------------
-- 13) UTILISATEUR_AIME_CHANSON
---------------------------------------------------------------------
INSERT INTO utilisateur_aime_chanson VALUES
('amelie.paris',21),('lucas.durand',23),('marie.morin',25),('nathan.girard',27),
('lea.moreau',29),('julien.bernard',31),('claire.petit',33),('adrien.lefevre',35),
('julie.robert',37),('maxime.dubois',39),('antoine.marchal',22),('sarah.muller',24),
('quentin.roux',26),('emma.perrin',28),('victor.fontaine',30),('pauline.giraud',32),
('louis.blanc',34),('ines.martin',36),('tom.rey',38),('manon.fabre',40);

---------------------------------------------------------------------
-- 14) ECOUTE
---------------------------------------------------------------------
INSERT INTO ecoute VALUES
('amelie.paris',21,'2024-01-01 10:00'),
('lucas.durand',23,'2024-01-01 10:05'),
('marie.morin',25,'2024-01-01 10:10'),
('nathan.girard',27,'2024-01-01 10:15'),
('lea.moreau',29,'2024-01-01 10:20'),
('julien.bernard',31,'2024-01-01 10:25'),
('claire.petit',33,'2024-01-01 10:30'),
('adrien.lefevre',35,'2024-01-01 10:35'),
('julie.robert',37,'2024-01-01 10:40'),
('maxime.dubois',39,'2024-01-01 10:45'),
('antoine.marchal',22,'2024-01-01 10:50'),
('sarah.muller',24,'2024-01-01 10:55'),
('quentin.roux',26,'2024-01-01 11:00'),
('emma.perrin',28,'2024-01-01 11:05'),
('victor.fontaine',30,'2024-01-01 11:10'),
('pauline.giraud',32,'2024-01-01 11:15'),
('louis.blanc',34,'2024-01-01 11:20'),
('ines.martin',36,'2024-01-01 11:25'),
('tom.rey',38,'2024-01-01 11:30'),
('manon.fabre',40,'2024-01-01 11:35');

---------------------------------------------------------------------
-- 15) UTILISATEUR_PLAYLIST
---------------------------------------------------------------------
INSERT INTO utilisateur_playlist VALUES
('amelie.paris',1),('lucas.durand',2),('nathan.girard',3),('marie.morin',4),
('lea.moreau',5),('julien.bernard',6),('adrien.lefevre',7),('claire.petit',8),
('julie.robert',9),('maxime.dubois',10),('antoine.marchal',11),('sarah.muller',12),
('quentin.roux',13),('emma.perrin',14),('victor.fontaine',15),('pauline.giraud',16),
('louis.blanc',17),('ines.martin',18),('tom.rey',19),('manon.fabre',20);

COMMIT;