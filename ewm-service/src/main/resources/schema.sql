DROP TABLE IF EXISTS users;

CREATE table IF NOT EXISTS users(
    user_id bigint generated by default as identity primary key,
    name varchar NOT NULL,
    email varchar NOT NULL
);

CREATE unique index if not exists email_uindex ON users (email);