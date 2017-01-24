-- User, Contact, Eatery schema

# --- !Ups
ALTER TABLE users ADD COLUMN email TEXT;

CREATE TABLE contacts (
id UUID NOT NULL PRIMARY KEY,
owner_id UUID NOT NULL REFERENCES users ON DELETE CASCADE,
contact_id UUID REFERENCES users ON DELETE SET NULL,
contact_phone INT,
contact_email TEXT
);

CREATE TABLE eateries (
id UUID NOT NULL PRIMARY KEY,
chain TEXT NOT NULL,
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

# --- !Downs
ALTER TABLE users DROP COLUMN email;

DROP TABLE contacts;

DROP TABLE eateries;
