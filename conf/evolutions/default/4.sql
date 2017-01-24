-- User, Contact, Eatery, Cafe, Chain, Choice schema

# --- !Ups
CREATE TABLE eatery_choices (
id UUID NOT NULL PRIMARY KEY,
usr UUID NOT NULL REFERENCES users ON DELETE CASCADE,
eatery UUID NOT NULL REFERENCES eateries ON DELETE CASCADE
);

CREATE TABLE cafe_choices (
id UUID NOT NULL PRIMARY KEY,
usr UUID NOT NULL REFERENCES users ON DELETE CASCADE,
cafe UUID NOT NULL REFERENCES cafes ON DELETE CASCADE
);
# --- !Downs

DROP TABLE eatery_choices;

DROP TABLE cafe_choices;
