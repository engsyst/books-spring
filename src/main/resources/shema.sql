-- -----------------------------------------------------
-- Schema books
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `books` ;

-- -----------------------------------------------------
-- Schema books
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `books` DEFAULT CHARACTER SET utf8 ;
USE `books` ;

-- -----------------------------------------------------
-- Table `author`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `author` ;
CREATE TABLE IF NOT EXISTS `author` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `author_title_idx` (`name` ASC)
)  ENGINE=INNODB;

-- -----------------------------------------------------
-- Table `category`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `category` ;
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `title_UNIQUE` (`title` ASC)
) ENGINE=INNODB;

-- -----------------------------------------------------
-- Table `book`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `book` ;
CREATE TABLE IF NOT EXISTS `book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `isbn` VARCHAR(45) NULL DEFAULT NULL,
  `price` DOUBLE UNSIGNED NOT NULL DEFAULT '0',
  `amount` INT(8) NULL CHECK(`amount` >= 0) DEFAULT '0',
  `category_id` BIGINT NOT NULL,
  `cover` VARCHAR(1000) NULL,
  `description` MEDIUMTEXT NULL,
  PRIMARY KEY (`id`),
  INDEX `book_title_idx` (`title` ASC),
  INDEX `fk_book_category1_idx` (`category_id` ASC),
  CONSTRAINT `fk_book_category1` FOREIGN KEY (`category_id`)
    REFERENCES `category` (`id`)
    ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=INNODB;

-- -----------------------------------------------------
-- Table `author_has_book`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `author_has_book` ;
CREATE TABLE IF NOT EXISTS `author_has_book` (
  `author_id` BIGINT NOT NULL,
  `book_id` BIGINT NOT NULL,
  PRIMARY KEY (`author_id` , `book_id`),
  INDEX `fk_author_has_book_book1_idx` (`book_id` ASC),
  INDEX `fk_author_has_book_author1_idx` (`author_id` ASC),
  CONSTRAINT `fk_author_has_book_author1` FOREIGN KEY (`author_id`)
    REFERENCES `author` (`id`)
    ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_author_has_book_book1` FOREIGN KEY (`book_id`)
    REFERENCES `book` (`id`)
    ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=INNODB;

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `user` ;

CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(45) NOT NULL,
  `password` VARCHAR(65) NOT NULL,
  `role` ENUM('client', 'admin') NOT NULL DEFAULT 'client',
  `email` VARCHAR(45) NULL,
  `phone` VARCHAR(15) NULL,
  `name` VARCHAR(45) NULL,
  `address` VARCHAR(1000) NULL,
  `avatar` VARCHAR(1000) NULL,
  `description` MEDIUMTEXT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_login_idx` (`login` ASC)
)  ENGINE=INNODB;

-- -----------------------------------------------------
-- Table `order`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `order` ;
CREATE TABLE IF NOT EXISTS `order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NULL,
  `operator_id` BIGINT NULL,
  `create_date` DATETIME NULL DEFAULT CURRENT_TIMESTAMP (),
  `update_date` DATETIME NULL DEFAULT CURRENT_TIMESTAMP () ON UPDATE CURRENT_TIMESTAMP (),
  `status` ENUM('newed', 'inprogress', 'completed', 'rejected') NULL DEFAULT 'newed',
  PRIMARY KEY (`id`),
  INDEX `fk_order_user1_idx` (`user_id` ASC),
  INDEX `idx_order_date_idx` (`create_date` DESC),
  CONSTRAINT `fk_order_user1` FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_order_user2` FOREIGN KEY (`operator_id`)
    REFERENCES `user` (`id`)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=INNODB;

-- -----------------------------------------------------
-- Table `delivery`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `delivery` ;
CREATE TABLE IF NOT EXISTS `delivery` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `phone` VARCHAR(15) NULL,
  `email` VARCHAR(45) NULL,
  `address` VARCHAR(1000) NULL,
  `description` VARCHAR(1000) NULL,
  `order_id` BIGINT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_delivery_order1` FOREIGN KEY (`order_id`)
    REFERENCES `order` (`id`)
    ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=INNODB;

-- -----------------------------------------------------
-- Table `order_book`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `order_book` ;

CREATE TABLE IF NOT EXISTS `order_book` (
  `book_id` BIGINT NOT NULL,
  `order_id` BIGINT NOT NULL,
  `count` INT(8) NOT NULL CHECK (count > 0) ,
  `price` DECIMAL(8, 2) NULL CHECK (price >= 0) DEFAULT 0.0,
  PRIMARY KEY (`book_id`, `order_id`),
  INDEX `fk_order_book_order1_idx` (`order_id` ASC),
  INDEX `fk_order_book_book1_idx` (`book_id` ASC),
  CONSTRAINT `fk_order_book_book1` FOREIGN KEY (`book_id`)
    REFERENCES `book` (`id`) 
    ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_book_order1`
    FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
    ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- function concat_authors
-- -----------------------------------------------------
DROP function IF EXISTS `concat_authors`;
DELIMITER $$
CREATE FUNCTION `concat_authors` (aid BIGINT) RETURNS VARCHAR(512) READS SQL DATA
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE k INT DEFAULT 0;
  DECLARE retv VARCHAR(512);
  DECLARE temp VARCHAR(45);
  DECLARE cur CURSOR FOR
    SELECT `author`.`title` FROM `author_has_book`,`author`
    WHERE `author_has_book`.`book_id` = `aid` 
      AND `author_has_book`.`author_id` = `author`.`id`;
  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;
  
  OPEN cur;
  SET retv:='';
  
  REPEAT
    FETCH cur INTO temp;
    IF NOT done THEN
      IF k = 0 THEN
        SET retv:=CONCAT(retv, temp);
        SET k := k +1;
      ELSE
        SET retv:=CONCAT(retv, ",", temp);
        END IF;
    END IF;
  UNTIL done END REPEAT;
  RETURN retv;
END;
$$
DELIMITER ;

-- -----------------------------------------------------
-- View `books`
-- -----------------------------------------------------
-- DROP VIEW IF EXISTS `books` ;
-- DROP TABLE IF EXISTS `books`;
CREATE  OR REPLACE VIEW `books` AS
  SELECT book.id, book.title, concat_authors(book.id) as `authors`, book.isbn, book.price, book.amount, category.title as category
  FROM book, category WHERE category.id = category_id;

-- -----------------------------------------------------
-- View `sum_by_order`
-- -----------------------------------------------------
-- DROP VIEW IF EXISTS `sum_by_order` ;
-- DROP TABLE IF EXISTS `sum_by_order`;
CREATE  OR REPLACE VIEW `sum_by_order` AS
  SELECT `order_id` `oid`, sum(`price` * `count`) `osum`
  FROM `order_book` GROUP BY `order_id`;

-- -----------------------------------------------------
-- View `orders`
-- -----------------------------------------------------
-- DROP VIEW IF EXISTS `orders` ;
-- DROP TABLE IF EXISTS `orders`;
CREATE  OR REPLACE VIEW `orders` AS
SELECT `order`.`user_id` `user_id`,
    (SELECT `login` FROM `user` WHERE `user`.`id` = `order`.`user_id`) `login`,
    `order`.`id` `order_id`,
    `order`.`status` `status`,
    `book`.`id` `book_id`,
    `book`.`title` `title`,
    `order_book`.`count` `count`,
    `order_book`.`price` `price`,
    `sum_by_order`.`osum` `osum`
  FROM `order`, `book`, `order_book`, `sum_by_order` 
  WHERE `order`.`id` = `order_book`.`order_id`
      AND `order_book`.`book_id` = `book`.`id`
      AND `order`.`id` = `sum_by_order`.`oid`
  ORDER BY `order`.`user_id` , `order`.`id` , `book`.`id`;


-- -----------------------------------------------------
-- Triggers
-- -----------------------------------------------------
DELIMITER $$
CREATE DEFINER = CURRENT_USER TRIGGER `books`.`order_book_BEFORE_INSERT` BEFORE INSERT ON `order_book` FOR EACH ROW
BEGIN
  SET @price = (select price from book where id = NEW.`book_id`);
  SET @newamount = (select amount from book where id = NEW.`book_id`) - NEW.count;
  SET NEW.price = @price;
  UPDATE book SET book.amount = @newamount where id = NEW.`book_id`;
END;
$$
DELIMITER ;

DELIMITER $$
CREATE DEFINER = CURRENT_USER TRIGGER `books`.`order_AFTER_UPDATE` AFTER UPDATE ON `order` FOR EACH ROW
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE ocount INT DEFAULT 0;
  DECLARE bookid INT DEFAULT 0;
  DECLARE cur CURSOR FOR
    SELECT `order_book`.`count` oc, `book_id` bid FROM `order_book`
    WHERE  `order_id` = NEW.`id`;
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;
    IF NEW.`status` = 'rejected' AND OLD.`status` != 'rejected' AND OLD.`status` != 'completed' THEN
    OPEN cur;
    FETCH cur INTO ocount, bookid;
    WHILE done = 0 DO
      UPDATE `book` SET `amount` = (ocount + `amount`) WHERE `id` = bookid;
      FETCH cur INTO ocount, bookid;
    END WHILE;
  END IF;
END;
$$
DELIMITER ;
