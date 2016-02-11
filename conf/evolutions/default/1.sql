# --- !Ups

CREATE TABLE account (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    password VARCHAR(128) NOT NULL,
    mail_adress VARCHAR(256) NOT NULL,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (mail_adress)
);

# --- !Downs

DROP TABLE account;
