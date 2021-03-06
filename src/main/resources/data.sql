DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS transaction;

CREATE TABLE account (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  activecard BOOLEAN NOT NULL,
  availablelimit INT DEFAULT NULL
);

CREATE TABLE transaction (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  merchant VARCHAR(255) NOT NULL,
  amount INT DEFAULT NULL,
  time TIMESTAMP NOT NULL
);