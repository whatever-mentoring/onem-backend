DROP TABLE IF EXISTS shortened_url CASCADE;
DROP TABLE IF EXISTS blocked_domain CASCADE;

CREATE TABLE shortened_url
(
    id            bigint GENERATED ALWAYS AS IDENTITY,
    origin_url    varchar(255)          NOT NULL,
    shortened_url varchar(100)          NOT NULL,
    expired_at    timestamptz,
    disabled      BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE TABLE blocked_domain
(
    id     bigint GENERATED ALWAYS AS IDENTITY,
    domain varchar(255) NOT NULL
);