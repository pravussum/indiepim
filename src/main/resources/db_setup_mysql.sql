create database indiepim character set utf8 collate utf8_general_ci;
create user 'indiepim'@'localhost' identified by 'password';
grant all on indiepim.* to 'indiepim'@'localhost';