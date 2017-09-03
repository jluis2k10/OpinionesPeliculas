DROP TABLE IF EXISTS Models;
CREATE TABLE Models (
  id BIGINT NOT NULL AUTO_INCREMENT ,
  name VARCHAR(50) NOT NULL ,
  adapter_class VARCHAR(255) NOT NULL ,
  language VARCHAR(3) NOT NULL ,
  location VARCHAR(255) NOT NULL ,
  trainable BOOLEAN ,
  description TEXT ,
  PRIMARY KEY (id),
  UNIQUE (name)
)