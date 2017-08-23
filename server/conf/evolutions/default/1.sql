# --- !Ups
create table "week_times" (
"id" UUID NOT NULL PRIMARY KEY,
"monday" TIME,
"tuesday" TIME,
"wednesday" TIME,
"thursday" TIME,
"friday" TIME,
"saturday" TIME,
"sunday" TIME
);

create table "users" (
"id" UUID NOT NULL PRIMARY KEY,
"name" VARCHAR NOT NULL,
"mobile" INTEGER,
"email" VARCHAR,
"breakfast_fk" UUID REFERENCES "week_times",
"lunch_fk" UUID REFERENCES "week_times",
"dinner_fk" UUID REFERENCES "week_times",
"avatar_url" VARCHAR
);

create table "contacts" (
"id" UUID NOT NULL PRIMARY KEY,
"name" TEXT,
"owner_id" UUID NOT NULL REFERENCES "users" ON DELETE CASCADE,
"contact_id" UUID REFERENCES "users",
"contact_phone" INTEGER,
"contact_email" VARCHAR,
"favorite" BOOLEAN NOT NULL
);

create table "login_info" (
"id" UUID NOT NULL PRIMARY KEY,
"provider_id" VARCHAR NOT NULL,
"provider_key" VARCHAR NOT NULL,
"user_id" UUID NOT NULL REFERENCES "users" ON DELETE CASCADE
);

create table "oauth2info" (
"id" UUID NOT NULL PRIMARY KEY,
"login_info" UUID NOT NULL REFERENCES "login_info" ON DELETE CASCADE ON UPDATE CASCADE,
"access_token" VARCHAR NOT NULL,
"token_type" VARCHAR,
"expires_in" INTEGER,
"refresh_token" VARCHAR
);


create table "chains" (
"id" VARCHAR NOT NULL PRIMARY KEY,
"website" VARCHAR,
"menu" VARCHAR
);

create table "establishments" (
"id" UUID NOT NULL PRIMARY KEY,
"chain" VARCHAR NOT NULL REFERENCES "chains" ON DELETE CASCADE,
"address" VARCHAR NOT NULL,
"open_times" UUID NOT NULL REFERENCES "week_times",
"close_times" UUID NOT NULL REFERENCES "week_times"
);

create table "choices" (
"id" UUID NOT NULL PRIMARY KEY,
"usr" UUID NOT NULL REFERENCES "users" ON DELETE CASCADE,
"establishment" UUID NOT NULL REFERENCES "establishments" ON DELETE CASCADE ON UPDATE CASCADE
);

# --- !Downs
drop table "week_times";
drop table "users";
drop table "contacts";
drop table "login_info";
drop table "oauth2info";
drop table "chains";
drop table "establishments";
drop table "choices";
