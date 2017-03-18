# --- !Ups
ALTER TABLE contacts ADD COLUMN favorite BOOLEAN NOT NULL;

ALTER TABLE users ADD COLUMN google_id TEXT;
ALTER TABLE users ADD COLUMN google_key TEXT;
ALTER TABLE users ADD COLUMN facebook_id TEXT;
ALTER TABLE users ADD COLUMN facebook_key TEXT;
ALTER TABLE users ADD COLUMN avatar_url TEXT;

# --- !Downs
ALTER TABLE contacts DROP COLUMN favorite;

ALTER TABLE users DROP COLUMN google_id;
ALTER TABLE users DROP COLUMN google_key;
ALTER TABLE users DROP COLUMN facebook_id;
ALTER TABLE users DROP COLUMN facebook_key;
ALTER TABLE users DROP COLUMN avatar_url;