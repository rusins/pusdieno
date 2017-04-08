# --- !Ups
alter table "contacts" add column "name" TEXT;

# --- !Downs
alter table "contacts" drop column "name";