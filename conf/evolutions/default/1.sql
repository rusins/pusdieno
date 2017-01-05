# User schema

# --- !Ups
CREATE TABLE "USER" (
  ID UUID NOT NULL PRIMARY KEY,
  NAME TEXT NOT NULL,
  MOBILE INT,
  MONDAY TIME,
  TUESDAY TIME,
  WEDNESDAY TIME,
  THURSDAY TIME,
  FRIDAY TIME,
  SATURDAY TIME,
  SUNDAY TIME
)

# --- !Downs
DROP TABLE "USER"