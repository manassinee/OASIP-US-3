-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema oasip
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `oasip` ;

-- -----------------------------------------------------
-- Schema oasip
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `oasip` DEFAULT CHARACTER SET utf8 ;
USE `oasip` ;

-- -----------------------------------------------------
-- Table `oasip`.`eventCategory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oasip`.`eventCategory` (
  `eventCategoryId` INT NOT NULL AUTO_INCREMENT,
  `eventCategoryName` VARCHAR(100) NOT NULL,
  `eventCategoryDescription` VARCHAR(500) NULL,
  `eventDuration` INT NOT NULL,
  PRIMARY KEY (`eventCategoryId`),
  UNIQUE INDEX `eventCategoryName_UNIQUE` (`eventCategoryName` ASC) VISIBLE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `oasip`.`event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oasip`.`event` (
  `eventId` INT NOT NULL AUTO_INCREMENT,
  `bookingName` VARCHAR(100) NOT NULL,
  `bookingEmail` VARCHAR(50) NOT NULL,
  `eventStartTime` DATETIME NOT NULL,
  `eventDuration` INT NOT NULL,
  `eventNotes` VARCHAR(500) NULL,
  `eventCategoryId` INT NOT NULL,
  PRIMARY KEY (`eventId`, `eventCategoryId`),
  INDEX `fk_event_eventCategory_idx` (`eventCategoryId` ASC) VISIBLE,
  CONSTRAINT `fk_event_eventCategory`
    FOREIGN KEY (`eventCategoryId`)
    REFERENCES `oasip`.`eventCategory` (`eventCategoryId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


use oasip;

insert into eventCategory (eventCategoryName, eventDuration) values 
    ('Frontend', 30),
    ('Backend', 30),
    ('Database', 30),
    ('DevOps', 30),
    ('Project Management', 30);

insert into event (bookingName, bookingEmail, eventStartTime, eventDuration, eventNotes, eventCategoryId) values 
    ('Manee Meerai', 'maneemeerai@gmail.com', '2022-04-25 11:00:00',45, 'Where am I going? Help.', 2),
    ('Somchai Jaidee', 'somchai007@gmail.com', '2022-04-26 09:00:00',30, null, 1),
    ('Thanawat Naeching', 'thanawat@gmail.com', '2022-04-27 10:00:00',15, 'bing bong', 5),
    ('Tawan Muadmuenwai', 'tawan@gmail.com', '2022-05-27 23:00:00', 30, 'brrrrrr', 2),
    ('Manassinee Vejvithan', 'manassinee@gmail.com', '2022-05-28 11:00:00', 25, 'wat now', 3);


CREATE USER 'us3admin'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON *.* TO 'us3admin'@'%' WITH GRANT OPTION;
