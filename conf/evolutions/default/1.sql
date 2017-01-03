# User schema

# --- !Ups
CREATE TABLE user (
  id UUID NOT NULL PRIMARY KEY,
  name TEXT NOT NULL,
  mobile INT,
  monday TIME,
  tuesday TIME,
  wednesday TIME,
  thursday TIME,
  friday TIME,
  saturday TIME,
  sunday TIME
)

# --- !Downs
DROP TABLE user