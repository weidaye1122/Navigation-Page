CREATE SCHEMA IF NOT EXISTS "team_nav";

CREATE TABLE IF NOT EXISTS "team_nav"."nav_access_stat_log" (
    "id" VARCHAR(32) NOT NULL,
    "type" VARCHAR(20),
    "event_time" TIMESTAMP,
    "ip" VARCHAR(64),
    "success" BOOLEAN,
    PRIMARY KEY ("id")
);
