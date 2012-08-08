CREATE TABLE `samples` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL,
  `sample` VARCHAR(255) NOT NULL,

   PRIMARY KEY (`id`)
);

INSERT INTO `samples` (`name`, `sample`) VALUES ('Foo','Bar');