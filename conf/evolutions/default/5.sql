# --- !Ups
ALTER TABLE contacts ADD COLUMN favorite BOOLEAN NOT NULL;

# --- !Downs
ALTER TABLE contacts DROP COLUMN favorite;
