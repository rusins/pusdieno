# User, Contact, Eatery, Cafe, Chain schema

# --- !Ups
ALTER TABLE users ADD COLUMN email TEXT;

CREATE TABLE chains (
id TEXT NOT NULL PRIMARY KEY,
website TEXT,
menu TEXT
);

CREATE TABLE cafes (
id UUID NOT NULL PRIMARY KEY,
chain TEXT NOT NULL REFERENCES chains ON DELETE CASCADE,
address TEXT NOT NULL,
monday_open TIME,
monday_close TIME,
tuesday_open TIME,
tuesday_close TIME,
wednesday_open TIME,
wednesday_close TIME,
thursday_open TIME,
thursday_close TIME,
friday_open TIME,
friday_close TIME,
saturday_open TIME,
saturday_close TIME,
sunday_open TIME,
sunday_close TIME
);

ALTER TABLE eateries DROP COLUMN chain;

ALTER TABLE eateries ADD COLUMN chain TEXT NOT NULL REFERENCES chains ON DELETE CASCADE;

# --- !Downs
