CREATE USER 'sdmsample'@'localhost' IDENTIFIED BY '';
CREATE DATABASE sdmsample;
GRANT ALL PRIVILEGES ON sdmsample.* TO 'sdmsample'@'localhost';
GRANT ALL PRIVILEGES ON sdmsample.* TO 'sdmsample'@'%';