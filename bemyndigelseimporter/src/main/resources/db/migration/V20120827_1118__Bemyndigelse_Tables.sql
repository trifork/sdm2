CREATE TABLE `Bemyndigelse` (
   BemyndigelsePID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    
  `kode` VARCHAR(100) NOT NULL,
  `bemyndigende_cpr` VARCHAR(10) NOT NULL,
  `bemyndigede_cpr` VARCHAR(10) NOT NULL,
  `bemyndigede_cvr` VARCHAR(8) NULL DEFAULT NULL,
  `system` VARCHAR(100) NOT NULL,
  `arbejdsfunktion` VARCHAR(100) NOT NULL,
  `rettighed` VARCHAR(100) NOT NULL,
  `status` VARCHAR(100) NOT NULL,
  `godkendelses_dato` VARCHAR(35) DEFAULT NULL,
  `oprettelses_dato` VARCHAR(35) DEFAULT NULL,
  `modificeret_dato` VARCHAR(35) DEFAULT NULL,
  `gyldig_fra_dato` VARCHAR(35) DEFAULT NULL,
  `gyldig_til_dato` VARCHAR(35) DEFAULT NULL,
  
   ModifiedDate DATETIME NOT NULL,
   ValidFrom DATETIME,
   ValidTo DATETIME,
    
   INDEX (BemyndigelsePID, ModifiedDate),
   INDEX (kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;
