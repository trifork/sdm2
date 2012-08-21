CREATE DATABASE sdm_warehouse;
GRANT CREATE, DROP, ALTER, DELETE, INDEX, INSERT, SELECT, UPDATE ON sdm_warehouse.* TO 'sdm4'@'localhost' identified by 'sdm4'; -- implicitly creates user if not exists
