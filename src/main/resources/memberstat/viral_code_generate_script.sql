
DROP TABLE IF EXISTS member_stat_snapshot;
CREATE TABLE member_stat_snapshot (
                                      id         BIGINT NOT NULL AUTO_INCREMENT,
                                      created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                                      updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                      viral_code VARCHAR(6) NOT NULL,
                                      PRIMARY KEY (id),
                                      UNIQUE KEY uk_member_stat_snapshot_viral_code (viral_code)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS daily_seq (
                                         name VARCHAR(64) NOT NULL,
                                         ymd  DATE        NOT NULL,
                                         val  BIGINT      NOT NULL,
                                         PRIMARY KEY (name, ymd)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

DROP FUNCTION IF EXISTS nextval;
DELIMITER $$
CREATE FUNCTION nextval(p_name VARCHAR(64))
    RETURNS BIGINT
    NOT DETERMINISTIC
BEGIN
    INSERT INTO daily_seq (name, ymd, val)
    VALUES (p_name, CURDATE(), 1)
    ON DUPLICATE KEY UPDATE val = LAST_INSERT_ID(val + 1);

    RETURN LAST_INSERT_ID();
END$$
DELIMITER ;

DROP FUNCTION IF EXISTS base56_6;
DELIMITER $$
CREATE FUNCTION base56_6(p_x BIGINT UNSIGNED)
    RETURNS CHAR(6)
    NOT DETERMINISTIC
BEGIN
    DECLARE alphabet VARCHAR(56) DEFAULT
        'ABCDEFGHJKMNPQRSTUVWXYZ'
            'abcdefghijkmnpqrstuvwxyz'
            '023456789';
    DECLARE r    VARCHAR(16) DEFAULT '';
    DECLARE n    BIGINT UNSIGNED;
    DECLARE rem  BIGINT UNSIGNED;
    DECLARE BASE INT DEFAULT 56;
    DECLARE MAXN BIGINT UNSIGNED DEFAULT 30840979456; -- 56^6

    SET n = MOD(p_x, MAXN);

    IF n = 0 THEN
        SET r = 'A';
    ELSE
        WHILE n > 0 DO
                SET rem = MOD(n, BASE);
                SET r = CONCAT(SUBSTRING(alphabet, rem + 1, 1), r);
                SET n = FLOOR(n / BASE);
            END WHILE;
    END IF;

    WHILE CHAR_LENGTH(r) < 6 DO
            SET r = CONCAT('A', r);
        END WHILE;

    RETURN SUBSTRING(r, -6);
END$$
DELIMITER ;


DROP FUNCTION IF EXISTS gen_viral_code;
DELIMITER $$
CREATE FUNCTION gen_viral_code(p_seq_name VARCHAR(64), p_daily_cap BIGINT, p_epoch DATE)
    RETURNS CHAR(6)
    NOT DETERMINISTIC
BEGIN
    DECLARE day_index BIGINT UNSIGNED DEFAULT DATEDIFF(CURDATE(), p_epoch);
    DECLARE daily     BIGINT UNSIGNED DEFAULT nextval(p_seq_name);
    DECLARE x         BIGINT UNSIGNED;
    DECLARE secret    VARCHAR(64)     DEFAULT '5A172025';
    DECLARE binhash   BLOB;
    DECLARE hash8     BIGINT UNSIGNED;

    IF daily > p_daily_cap THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Daily cap exceeded';
    END IF;

    SET x = day_index * p_daily_cap + daily;

    SET binhash = UNHEX(SHA2(CONCAT(x, ':', p_seq_name, ':', secret), 256));
    SET hash8   = CAST(CONV(HEX(SUBSTRING(binhash, 1, 8)), 16, 10) AS UNSIGNED);

    RETURN base56_6(hash8);
END$$
DELIMITER ;

DROP TRIGGER IF EXISTS trg_mss_before_insert;
DELIMITER $$
CREATE TRIGGER trg_mss_before_insert
    BEFORE INSERT ON member_stat_snapshot
    FOR EACH ROW
BEGIN
    SET NEW.viral_code = gen_viral_code('viral2025', 1000000, DATE '2025-01-01');
END$$
DELIMITER ;





