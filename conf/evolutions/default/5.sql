# --- !Ups
ALTER TABLE contacts ADD COLUMN favorite BOOLEAN NOT NULL;

ALTER TABLE users ADD COLUMN google_info UUID;
ALTER TABLE users ADD COLUMN facebook_info UUID;
ALTER TABLE users ADD COLUMN avatar_url TEXT;

# --- !Downs
ALTER TABLE contacts DROP COLUMN favorite;

ALTER TABLE users DROP COLUMN google_info;
ALTER TABLE users DROP COLUMN facebook_info;
ALTER TABLE users DROP COLUMN avatar_url;