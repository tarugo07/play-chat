# --- !Ups

CREATE TABLE IF NOT EXISTS `play_chat`.`account` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(128) NOT NULL,
    `password` VARCHAR(128) NOT NULL,
    `mail_adress` VARCHAR(256) NOT NULL,
    `create_time` TIMESTAMP NOT NULL,
    `update_time` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4;


# --- !Downs

DROP TABLE account;