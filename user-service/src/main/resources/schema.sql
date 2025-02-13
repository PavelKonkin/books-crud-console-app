
CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    username VARCHAR(255)                            NOT NULL,
    password VARCHAR(255)                            NOT NULL,
    role     VARCHAR(10)                             NOT NULL,
    CONSTRAINT uq_username UNIQUE (username),
    CONSTRAINT pk_user PRIMARY KEY (user_id)
);