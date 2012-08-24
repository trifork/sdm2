-- SOR - search optimized
--
CREATE TABLE `SORRelationer` (
   SORRelationerPID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,

  `sor_parent` VARCHAR(100) NOT NULL,
  `sor_child` VARCHAR(100) NOT NULL,

   ModifiedDate DATETIME NOT NULL,
   ValidFrom DATETIME,
   ValidTo DATETIME,

   INDEX (SORRelationerPID, ModifiedDate),
   INDEX USING BTREE (sor_parent, sor_child)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- SOR - search optimized
--
CREATE TABLE `SORYderSHAKRelationer` (
   SORYderSHAKRelationerPID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,

  `shak_yder` VARCHAR(100) NOT NULL,
  `sor` VARCHAR(100) NOT NULL,

   ModifiedDate DATETIME NOT NULL,
   ValidFrom DATETIME,
   ValidTo DATETIME,

   INDEX (SORYderSHAKRelationerPID, ModifiedDate),
   INDEX (shak_yder),
   INDEX (sor)
) ENGINE=InnoDB COLLATE=utf8_bin;
