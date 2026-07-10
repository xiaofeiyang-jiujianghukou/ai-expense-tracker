CREATE TABLE `user` (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    email       VARCHAR(255)    NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    nickname    VARCHAR(100)    DEFAULT NULL,
    status      TINYINT         NOT NULL DEFAULT 1,
    created_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
