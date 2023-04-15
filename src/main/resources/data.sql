-- -----------------------------------------------------
-- Data inserts
-- -----------------------------------------------------

-- -----------------------------------------------------
-- category
-- -----------------------------------------------------
INSERT INTO `category` (`id`,`title`) VALUES (1,'Fantasy');
INSERT INTO `category` (`id`,`title`) VALUES (2,'Action');
INSERT INTO `category` (`id`,`title`) VALUES (3,'Love novel');
INSERT INTO `category` (`id`,`title`) VALUES (4,'none');

-- -----------------------------------------------------
-- book
-- -----------------------------------------------------
INSERT INTO `book` (`id`,`title`,`isbn`,`price`,`amount`,`category_id`,`cover`,`description`) VALUES (1,'Божественная комедия','ISBN-01234-0123',125,3,1,'NULL','');
INSERT INTO `book` (`id`,`title`,`isbn`,`price`,`amount`,`category_id`,`cover`,`description`) VALUES (2,'Сказки','ISBN-01234-0124',300,5,2,'NULL','NULL');
INSERT INTO `book` (`id`,`title`,`isbn`,`price`,`amount`,`category_id`,`cover`,`description`) VALUES (3,'И пришло разрушение','ISBN-01234-0125',245.5,1,2,'NULL','NULL');
INSERT INTO `book` (`id`,`title`,`isbn`,`price`,`amount`,`category_id`,`cover`,`description`) VALUES (4,'Отец Горио','ISBN-01234-0126',46,3,2,'','');
INSERT INTO `book` (`id`,`title`,`isbn`,`price`,`amount`,`category_id`,`cover`,`description`) VALUES (5,'Вымыслы','ISBN-01234-0127',118.3,4,2,'NULL','');
INSERT INTO `book` (`id`,`title`,`isbn`,`price`,`amount`,`category_id`,`cover`,`description`) VALUES (6,'Декамерон','ISBN-01234-0128',148.7,2,3,'NULL','NULL');
INSERT INTO `book` (`id`,`title`,`isbn`,`price`,`amount`,`category_id`,`cover`,`description`) VALUES (7,'Вопрос о воде и земле','ISBN-01234-0133',125,4,4,'NULL','');

-- -----------------------------------------------------
-- author
-- -----------------------------------------------------
INSERT INTO `author` (`id`,`name`) VALUES (1,'Данте Алигьери');
INSERT INTO `author` (`id`,`name`) VALUES (2,'Ханс Кристиан Андерсен');
INSERT INTO `author` (`id`,`name`) VALUES (3,'Чинуа Ачебе');
INSERT INTO `author` (`id`,`name`) VALUES (4,'Оноре де Бальзак');
INSERT INTO `author` (`id`,`name`) VALUES (5,'Хорхе Луис Борхес');
INSERT INTO `author` (`id`,`name`) VALUES (6,'Джованни Боккаччо');

-- -----------------------------------------------------
-- author_has_book
-- -----------------------------------------------------
INSERT INTO `author_has_book` (`author_id`,`book_id`) VALUES (1,1);
INSERT INTO `author_has_book` (`author_id`,`book_id`) VALUES (1,7);
INSERT INTO `author_has_book` (`author_id`,`book_id`) VALUES (2,2);
INSERT INTO `author_has_book` (`author_id`,`book_id`) VALUES (1,3);
INSERT INTO `author_has_book` (`author_id`,`book_id`) VALUES (3,3);
INSERT INTO `author_has_book` (`author_id`,`book_id`) VALUES (4,4);
INSERT INTO `author_has_book` (`author_id`,`book_id`) VALUES (5,5);
INSERT INTO `author_has_book` (`author_id`,`book_id`) VALUES (6,6);

-- -----------------------------------------------------
-- user
-- -----------------------------------------------------
INSERT INTO `user` (`id`, `login`, `password`, `role`) VALUES (default, 'admin', '8C6976E5B5410415BDE908BD4DEE15DFB167A9C873FC4BB8A81F6F2AB448A918', 'admin');
INSERT INTO `user` (`id`, `login`, `password`, `role`) VALUES (default, 'client', '948FE603F61DC036B5C596DC09FE3CE3F3D30DC90F024C85F3C82DB2CCAB679D', 'client');

INSERT INTO `order` (user_id) VALUES (2);
INSERT INTO `delivery` (name, phone, email, address, description, order_id) VALUES ('name', '111', 'a@a.a', 'addr', ' descr', 1);
INSERT INTO order_book (order_id, book_id, count) VALUES (1, 3, 1);

SELECT o.id, o.user_id, o.operator_id, o.create_date, o.update_date, o.status FROM `order` o WHERE o.id = 1;
SELECT d.id, d.name, d.phone, d.email, d.address, d.description, d.order_id FROM `delivery` d WHERE d.order_id = 1;
SELECT b.id, b.title, b.isbn, b.price, b.amount, b.category_id, b.cover, b.description, 
  ob.order_id, ob.book_id, ob.count, ob.price 
  FROM `book` b, `order_book` ob 
  WHERE ob.book_id = b.id AND ob.order_id = 1;
SELECT * FROM `book`;

