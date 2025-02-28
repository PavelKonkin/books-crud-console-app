CREATE TABLE IF NOT EXISTS books
(
    book_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title       VARCHAR(100)                            NOT NULL,
    description VARCHAR(1000)                           NOT NULL,
    image_id    VARCHAR(100),
    CONSTRAINT pk_book PRIMARY KEY (book_id)
);

CREATE TABLE IF NOT EXISTS authors
(
    author_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name      VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_author PRIMARY KEY (author_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title    VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_genre PRIMARY KEY (genre_id)
);

CREATE TABLE IF NOT EXISTS book_author
(
    book_id   BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    CONSTRAINT book_author_pk
        PRIMARY KEY (book_id, author_id),
    CONSTRAINT book_author_book_id_fk
        FOREIGN KEY (book_id) REFERENCES books (book_id)
            ON DELETE CASCADE,
    CONSTRAINT book_author_author_id_fk
        FOREIGN KEY (author_id) REFERENCES authors (author_id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS book_genre
(
    book_id  BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    CONSTRAINT book_genre_pk
        PRIMARY KEY (book_id, genre_id),
    CONSTRAINT book_author_book_id_fk
        FOREIGN KEY (book_id) REFERENCES books (book_id)
            ON DELETE CASCADE,
    CONSTRAINT book_author_genre_id_fk
        FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
            ON DELETE CASCADE
);