
CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGSERIAL NOT NULL,
    username VARCHAR                            NOT NULL,
    password VARCHAR                            NOT NULL,
    role     VARCHAR                             NOT NULL,
    CONSTRAINT uq_username UNIQUE (username),
    CONSTRAINT pk_user PRIMARY KEY (user_id)
);