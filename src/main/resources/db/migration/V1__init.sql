CREATE EXTENSION IF NOT EXISTS CITEXT;
CREATE TABLE IF NOT EXISTS users (nickname CITEXT UNIQUE NOT NULL PRIMARY KEY, fullname varchar(128) NOT NULL,
about text, email CITEXT UNIQUE NOT NULL);

CREATE INDEX ON users (lower(nickname COLLATE "ucs_basic"));

CREATE TABLE IF NOT EXISTS forums (title VARCHAR(128) NOT NULL, admin CITEXT NOT NULL, slug CITEXT UNIQUE NOT NULL PRIMARY KEY,
posts BIGINT NOT NULL DEFAULT 0, threads BIGINT NOT NULL DEFAULT 0, FOREIGN KEY (admin) REFERENCES users(nickname));

CREATE INDEX ON forums (lower(slug));
CREATE INDEX ON forums (admin);

CREATE TABLE IF NOT EXISTS threads (id SERIAL PRIMARY KEY, title VARCHAR(128) NOT NULL, author CITEXT NOT NULL, forum CITEXT NOT NULL,
message TEXT NOT NULL, votes BIGINT NOT NULL DEFAULT 0, slug CITEXT UNIQUE, created TIMESTAMP NOT NULL DEFAULT current_timestamp,
FOREIGN KEY (author) REFERENCES users(nickname), FOREIGN KEY (forum) REFERENCES forums(slug));

CREATE INDEX ON threads (slug);
CREATE INDEX ON threads (lower(forum));

CREATE TABLE IF NOT EXISTS posts (id SERIAL PRIMARY KEY,parent BIGINT NOT NULL DEFAULT 0,author CITEXT NOT NULL,message TEXT NOT NULL,
isEdited BOOLEAN NOT NULL DEFAULT false,forum CITEXT NOT NULL,thread_id BIGINT NOT NULL,created TIMESTAMP NOT NULL DEFAULT current_timestamp, post_path INTEGER[],
FOREIGN KEY (author) REFERENCES users(nickname),
FOREIGN KEY (forum) REFERENCES forums(slug),
FOREIGN KEY (thread_id) REFERENCES threads(id));

CREATE INDEX ON posts (author,forum);
CREATE INDEX ON posts (id, parent, thread_id);
CREATE INDEX ON posts (thread_id, id);
CREATE INDEX ON posts ((post_path[1]), id);
CREATE INDEX ON posts (thread_id, post_path);
CREATE INDEX ON posts (thread_id, created, id);

CREATE TABLE IF NOT EXISTS votes (author CITEXT NOT NULL, thread_id BIGINT NOT NULL, voice INT NOT NULL,
FOREIGN KEY (author) REFERENCES users(nickname) ON DELETE CASCADE,
FOREIGN KEY (thread_id) REFERENCES threads(id) ON DELETE CASCADE, UNIQUE (author, thread_id));

CREATE INDEX ON votes (author, thread_id);

CREATE TABLE IF NOT EXISTS forum_users (
  author CITEXT NOT NULL,
  forum CITEXT NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_forum_users_user ON forum_users (author);
CREATE INDEX IF NOT EXISTS idx_forum_users_forum ON forum_users (forum);
CREATE INDEX IF NOT EXISTS idx_forum_users_both ON forum_users (lower(forum), author);


CREATE OR REPLACE FUNCTION add_forum_users() RETURNS TRIGGER AS '
  BEGIN
    INSERT INTO forum_users (author, forum) VALUES (NEW.author, NEW.forum);
    RETURN NEW;
  END;
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS add_post_tr ON posts;
CREATE TRIGGER add_post_tr AFTER INSERT ON posts
FOR EACH ROW EXECUTE PROCEDURE add_forum_users();

DROP TRIGGER IF EXISTS add_thread_tr ON threads;
CREATE TRIGGER add_thread_tr AFTER INSERT ON threads
FOR EACH ROW EXECUTE PROCEDURE add_forum_users();