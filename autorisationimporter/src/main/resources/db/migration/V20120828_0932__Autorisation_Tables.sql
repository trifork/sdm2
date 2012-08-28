CREATE TABLE Autorisation (
    AutorisationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    
    Autorisationsnummer CHAR(5) NOT NULL,
    
    CPR CHAR(10) NOT NULL,
    Fornavn VARCHAR(100) NOT NULL,
    Efternavn VARCHAR(100) NOT NULL,
    UddannelsesKode INT(4) NOT NULL,

    CreatedDate DATETIME NOT NULL,
    ModifiedDate DATETIME NOT NULL,
    ValidFrom DATETIME NOT NULL,
    ValidTo DATETIME NOT NULL,
    
    INDEX (AutorisationPID, ModifiedDate),
    INDEX (Autorisationsnummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- This table is used to hold the set of currently valid
-- autorisations. (Used by the STS)
--
CREATE TABLE autreg (
  id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  
  cpr CHAR(10) NOT NULL,
  given_name VARCHAR(100) NOT NULL,
  surname VARCHAR(100) NOT NULL,
  
  aut_id CHAR(5) NOT NULL,
  edu_id CHAR(4) NOT NULL,
  
  KEY cpr_aut_id (cpr, aut_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
