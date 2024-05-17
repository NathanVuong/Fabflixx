DELIMITER //

CREATE PROCEDURE add_movie (
    IN movie_title VARCHAR(100),
    IN movie_year INT,
    IN movie_director VARCHAR(100),
    IN star_name VARCHAR(100),
    IN star_birth_year INT,
    IN genre_name VARCHAR(32),
    OUT movie_id_out VARCHAR(10),
    OUT star_id_out VARCHAR(10),
    OUT genre_id_out INT
)
BEGIN
    DECLARE existing_star_id VARCHAR(10);

    -- Generate movie ID
SELECT CONCAT('tt', LPAD(SUBSTRING(MAX(movies.id), 3) + 1, 7, '0')) INTO movie_id_out FROM movies;

-- Generate star ID
SELECT CONCAT('nm', LPAD(SUBSTRING(MAX(stars.id), 3) + 1, 7, '0')) INTO star_id_out FROM stars;

-- Check if the star exists with the same name and birth year
IF star_name IS NOT NULL THEN
        IF star_birth_year IS NOT NULL THEN
            -- Search for existing star with the same name and birth year
SELECT stars.id INTO existing_star_id FROM stars WHERE stars.name = star_name AND stars.birthYear = star_birth_year;
ELSE
            -- Search for existing star with the same name regardless of birth year
SELECT stars.id INTO existing_star_id FROM stars WHERE stars.name = star_name;
END IF;

        -- If a star with the same name and birth year doesn't exist, insert it
        IF existing_star_id IS NULL THEN
            IF star_birth_year IS NOT NULL THEN
                INSERT INTO stars (id, name, birthYear) VALUES (star_id_out, star_name, star_birth_year);
ELSE
                INSERT INTO stars (id, name) VALUES (star_id_out, star_name);
END IF;
ELSE
            -- Set star_id_out equal to existing_star_id if an existing star is found
            SET star_id_out = existing_star_id;
END IF;
END IF;

    -- Insert the movie
INSERT INTO movies (id, title, year, director, price)
VALUES (movie_id_out, movie_title, movie_year, movie_director, FLOOR(RAND() * 100) + 1); -- Random price between 1 and 100

-- Check if the genre exists, if not, insert it
IF genre_name IS NOT NULL THEN
SELECT genres.id INTO genre_id_out FROM genres WHERE genres.name = genre_name;
IF genre_id_out IS NULL THEN
            INSERT INTO genres (name) VALUES (genre_name);
            SET genre_id_out = LAST_INSERT_ID();
END IF;
END IF;

    -- Insert the associations between movie and star
    IF star_name IS NOT NULL THEN
        INSERT INTO stars_in_movies (starId, movieId) VALUES (star_id_out, movie_id_out);
END IF;

    -- Insert the associations between movie and genre
    IF genre_name IS NOT NULL THEN
        INSERT INTO genres_in_movies (genreId, movieId) VALUES (genre_id_out, movie_id_out);
END IF;

END //

DELIMITER ;