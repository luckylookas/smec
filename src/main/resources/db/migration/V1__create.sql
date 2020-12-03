CREATE TABLE `account` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `name` VARCHAR(255)
);

CREATE TABLE `event` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `happened_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `type` VARCHAR (255) NOT NULL,
    `account_id` VARCHAR(36) NOT NULL,
    FOREIGN KEY  (`account_id`) REFERENCES `account`(`id`)
);