DROP DATABASE IF EXISTS ship_master_java;
CREATE DATABASE IF NOT EXISTS ship_master_java;
USE ship_master;
SET SQL_SAFE_UPDATES=0;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'tyfon';
 SET GLOBAL time_zone = '+00:00';
 SET @@global.time_zone = '+00:00';
