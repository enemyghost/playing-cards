-- -----------------------------------------------------
-- Table `player`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `player` (
  `player_uuid` binary(16) PRIMARY KEY,
  `email_address` varchar(320) NOT NULL UNIQUE,
  `display_name` varchar(20) NOT NULL UNIQUE,
  `password_hash` varbinary(256) NOT NULL
) ENGINE = InnoDB CHARACTER SET utf8mb4;

-- -----------------------------------------------------
-- Table `player_score`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `player_score`(
  `player_uuid` binary(16) PRIMARY KEY,
  `score` integer NOT NULL DEFAULT 0,
  CONSTRAINT `player_score_player_fk`
    FOREIGN KEY  (`player_uuid`)
    REFERENCES `player` (`player_uuid`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET utf8mb4;

-- -----------------------------------------------------
-- Table `game`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `game` (
  `game_uuid` binary(16) PRIMARY KEY,
  `winner_player_uuid` binary(16) NOT NULL,
  `game_completed_epoch_ms` bigint unsigned NOT NULL,
  CONSTRAINT `game_player_fk`
    FOREIGN KEY  (`winner_player_uuid`)
    REFERENCES `player` (`player_uuid`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET utf8mb4;

-- -----------------------------------------------------
-- Table `game_player`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `game_player` (
  `game_uuid` binary(16) NOT NULL,
  `player_uuid` binary(16) NOT NULL,
  `score` integer NOT NULL,
  PRIMARY KEY (`game_uuid`, `player_uuid`),
  CONSTRAINT `game_player_game_fk`
    FOREIGN KEY  (`game_uuid`)
    REFERENCES `game` (`game_uuid`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `game_player_player_fk`
    FOREIGN KEY  (`player_uuid`)
    REFERENCES `player` (`player_uuid`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET utf8mb4;

-- -----------------------------------------------------
-- Table `player_group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `player_group` (
  `player_group_uuid` binary(16) PRIMARY KEY,
  `player_uuid_1` binary(16) NOT NULL,
  `player_uuid_2` binary(16) NOT NULL,
  `player_uuid_3` binary(16),
  `player_uuid_4` binary(16),
  CONSTRAINT `player_group_player1_fk`
    FOREIGN KEY  (`player_uuid_1`)
    REFERENCES `player` (`player_uuid`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `player_group_player2_fk`
    FOREIGN KEY  (`player_uuid_2`)
    REFERENCES `player` (`player_uuid`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `player_group_player3_fk`
    FOREIGN KEY  (`player_uuid_3`)
    REFERENCES `player` (`player_uuid`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `player_group_player4_fk`
    FOREIGN KEY  (`player_uuid_4`)
    REFERENCES `player` (`player_uuid`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET utf8mb4;

-- -----------------------------------------------------
-- Table `game_player_group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `game_player_group` (
  `game_uuid` binary(16) NOT NULL,
  `player_group_uuid` binary(16) NOT NULL,
  PRIMARY KEY (`game_uuid`, `player_group_uuid`),
  CONSTRAINT `game_player_group_game_fk`
    FOREIGN KEY  (`game_uuid`)
    REFERENCES `game` (`game_uuid`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `game_player_group_player_group_fk`
    FOREIGN KEY  (`player_group_uuid`)
    REFERENCES `player_group` (`player_group_uuid`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET utf8mb4;
