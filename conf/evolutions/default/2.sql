# User, Contact, Eatery schema

# --- !Ups
ALTER TABLE users ADD email TEXT;

ADD TABLE contacts (
id UUID NOT NULL PRIMARY KEY,
owner_id UUID NOT NULL REFERENCES users ON DELETE CASCADE,
contact_id UUID REFERENCES users ON DELETE SET NULL,
contact_phone INT,
contact_email TEXT
);

# --- !Downs
ALTER TABLE users DROP email;

DROP TABLE contacts;
