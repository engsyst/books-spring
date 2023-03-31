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
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `author_title_idx` (`title` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `category`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `category` ;

CREATE TABLE IF NOT EXISTS `category` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `title_UNIQUE` (`title` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `book`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `book` ;
CREATE TABLE IF NOT EXISTS `book` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `isbn` VARCHAR(45) NULL DEFAULT NULL,
  `price` DOUBLE UNSIGNED NOT NULL DEFAULT '0',
  `count` INT(11) UNSIGNED NOT NULL DEFAULT '0',
  `category_id` INT(11) NOT NULL,
  `cover` VARCHAR(1000) NULL,
  `description` MEDIUMTEXT NULL,
  PRIMARY KEY (`id`),
  INDEX `book_title_idx` (`title` ASC),
  INDEX `fk_book_category1_idx` (`category_id` ASC),
  CONSTRAINT `fk_book_category1`
    FOREIGN KEY (`category_id`)
    REFERENCES `category` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `author_has_book`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `author_has_book` ;

CREATE TABLE IF NOT EXISTS `author_has_book` (
  `author_id` INT(11) NOT NULL,
  `book_id` INT(11) NOT NULL,
  PRIMARY KEY (`author_id`, `book_id`),
  INDEX `fk_author_has_book_book1_idx` (`book_id` ASC),
  INDEX `fk_author_has_book_author1_idx` (`author_id` ASC),
  CONSTRAINT `fk_author_has_book_author1`
    FOREIGN KEY (`author_id`)
    REFERENCES `author` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_author_has_book_book1`
    FOREIGN KEY (`book_id`)
    REFERENCES `book` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `user` ;

CREATE TABLE IF NOT EXISTS `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(45) NOT NULL,
  `password` VARCHAR(65) NOT NULL,
  `role` ENUM('client','admin') NOT NULL DEFAULT 'client',
  `email` VARCHAR(45) NULL,
  `phone` VARCHAR(15) NULL,
  `name` VARCHAR(45) NULL,
  `address` VARCHAR(1000) NULL,
  `avatar` VARCHAR(1000) NULL,
  `description` MEDIUMTEXT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_login_idx` (`login` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

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
  `user_id` INT(11) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_delivery_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_delivery_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `order`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `order` ;
CREATE TABLE IF NOT EXISTS `order` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `no` INT(11) NULL,
  `user_id` INT(11) NULL,
  `date` DATETIME NULL DEFAULT CURRENT_TIMESTAMP(),
  `status` ENUM('newed','inprogress','completed','rejected') NULL DEFAULT 'newed',
  `delivery_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_order_user1_idx` (`user_id` ASC),
  INDEX `fk_order_delivery1_idx` (`delivery_id` ASC),
  INDEX `order_date_idx` (`date` DESC),
  INDEX `order_status_idx` (`date` ASC),
  CONSTRAINT `fk_order_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_delivery1`
    FOREIGN KEY (`delivery_id`)
    REFERENCES `delivery` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `book_has_order`
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `book_has_order` ;
CREATE TABLE IF NOT EXISTS `book_has_order` (
  `book_id` INT(11) NOT NULL,
  `order_id` INT(11) NOT NULL,
  `count` INT(11) UNSIGNED NOT NULL,
  `price` DOUBLE UNSIGNED NOT NULL DEFAULT 0.0,
  PRIMARY KEY (`book_id`, `order_id`),
  INDEX `fk_book_has_order_order1_idx` (`order_id` ASC),
  INDEX `fk_book_has_order_book1_idx` (`book_id` ASC),
  CONSTRAINT `fk_book_has_order_book1`
    FOREIGN KEY (`book_id`)
    REFERENCES `book` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_book_has_order_order1`
    FOREIGN KEY (`order_id`)
    REFERENCES `order` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- function concatauthors
-- -----------------------------------------------------
-- DROP function IF EXISTS `concat_authors`;
DELIMITER $$
CREATE FUNCTION `concat_authors` (aid INT(11)) RETURNS VARCHAR(512) READS SQL DATA
BEGIN
	DECLARE done INT DEFAULT 0;
	DECLARE k INT DEFAULT 0;
    DECLARE retv VARCHAR(512);
    DECLARE temp VARCHAR(45);
	DECLARE cur CURSOR FOR
      SELECT `author`.`title` FROM `author_has_book`,`author`
       WHERE  `author_has_book`.`book_id` = `aid` and `author_has_book`.`author_id` = `author`.`id`;
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
  SELECT book.id, book.title, concatauthors(book.id) as `authors`, book.isbn, book.price, book.count, category.title as category
  FROM book, category WHERE category.id = category_id;

-- -----------------------------------------------------
-- View `sum_by_order`
-- -----------------------------------------------------
-- DROP VIEW IF EXISTS `sum_by_order` ;
-- DROP TABLE IF EXISTS `sum_by_order`;
CREATE  OR REPLACE VIEW `sum_by_order` AS
    SELECT `order_id` `oid`, sum(`price` * `count`) `osum`
    FROM `book_has_order` GROUP BY `order_id`;

-- -----------------------------------------------------
-- View `orders`
-- -----------------------------------------------------
-- DROP VIEW IF EXISTS `orders` ;
-- DROP TABLE IF EXISTS `orders`;
CREATE  OR REPLACE VIEW `orders` AS
SELECT
        `order`.`user_id` `user_id`,
        (SELECT `login` FROM `user` WHERE `user`.`id` = `order`.`user_id`) `login`,
        `order`.`id` `order_id`,
        `order`.`status` `status`,
        `book`.`id` `book_id`,
        `book`.`title` `title`,
        `book_has_order`.`count` `count`,
        `book_has_order`.`price` `price`,
        `sum_by_order`.`osum` `osum`,
        `delivery_id`
    FROM
        `order`,
        `book`,
        `book_has_order`,
        `sum_by_order`,
        `delivery`
    WHERE
        `order`.`id` = `book_has_order`.`order_id`
            AND `book_has_order`.`book_id` = `book`.`id`
            AND `order`.`id` = `sum_by_order`.`oid`
    ORDER BY `order`.`user_id` , `order`.`id` , `book`.`id`;


-- -----------------------------------------------------
-- Triggers
-- -----------------------------------------------------
DELIMITER $$
CREATE DEFINER = CURRENT_USER TRIGGER `books`.`book_has_order_BEFORE_INSERT` BEFORE INSERT ON `book_has_order` FOR EACH ROW
BEGIN
	SET @price = (select price from book where id = NEW.`book_id`);
    SET @newcount = (select count from book where id = NEW.`book_id`) - NEW.count;
    SET NEW.price = @price;
    UPDATE book SET book.count = @newcount where id = NEW.`book_id`;
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
		SELECT `book_has_order`.`count` oc, `book_id` bid FROM `book_has_order`
		WHERE  `order_id` = NEW.`id`;
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;
    IF NEW.`status` = 'rejected' AND OLD.`status` != 'rejected' AND OLD.`status` != 'completed' THEN
		OPEN cur;
		FETCH cur INTO ocount, bookid;
		WHILE done = 0 DO
			UPDATE `book` SET `count` = (ocount + `count`) WHERE `id` = bookid;
			FETCH cur INTO ocount, bookid;
		END WHILE;
	END IF;
END;
$$
DELIMITER ;
