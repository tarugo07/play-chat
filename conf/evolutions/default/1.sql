# --- !Ups

CREATE TABLE IF NOT EXISTS `play_chat`.`account` (
    `id`          BIGINT(20) UNSIGNED AUTO_INCREMENT,
    `name`        VARCHAR(128) NOT NULL,
    `password`    VARCHAR(128) NOT NULL,
    `mail`        VARCHAR(256) NOT NULL,
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `play_chat`.`account_session` (
    `id`          BIGINT(20) UNSIGNED AUTO_INCREMENT,
    `account_id`  BIGINT(20) UNSIGNED NOT NULL,
    `salt`        VARCHAR(256) NOT NULL,
    `expire_time` TIMESTAMP NOT NULL,
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX (`account_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4;


# --- !Downs

DROP TABLE account;
DROP TABLE account_session;
