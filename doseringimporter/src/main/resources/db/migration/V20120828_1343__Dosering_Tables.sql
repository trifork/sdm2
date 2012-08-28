CREATE TABLE DosageStructure (
    DosageStructurePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    
    releaseNumber BIGINT(15) NOT NULL,
    
    code VARCHAR(11) NOT NULL, -- ID
    type VARCHAR(100) NOT NULL,
    simpleString VARCHAR(100), -- OPTIONAL
    supplementaryText VARCHAR(200), -- OPTIONAL
    xml VARCHAR(10000) NOT NULL,
    shortTranslation VARCHAR(70),
    longTranslation VARCHAR(10000), -- OPTIONAL (The specs say it cannot be NULL. See comment in DosageStructure.java)
    ModifiedDate DATETIME NOT NULL,
    ValidFrom DATETIME NOT NULL,
    ValidTo DATETIME NOT NULL,
    CreatedDate DATETIME NOT NULL,
    
    INDEX (DosageStructurePID, ModifiedDate),
    INDEX (code, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE DosageUnit (
    DosageUnitPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    
    code INT(4) NOT NULL, -- ID
    
    releaseNumber BIGINT(15) NOT NULL,
    textSingular VARCHAR(100) NOT NULL,
    textPlural VARCHAR(100) NOT NULL,
    
    ModifiedDate DATETIME NOT NULL,
    ValidFrom DATETIME NOT NULL,
    ValidTo DATETIME NOT NULL,
    CreatedDate DATETIME NOT NULL,
    
    INDEX (DosageUnitPID, ModifiedDate),
    INDEX (code, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE DosageVersion (
    DosageVersionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    
    daDate DATE NOT NULL, -- @ID // TODO: Should the id not be the releaseNumber? :S
    
    lmsDate DATE NOT NULL,
    releaseDate DATE NOT NULL,
    releaseNumber BIGINT(15) NOT NULL,
    
    ModifiedDate DATETIME NOT NULL,
    ValidFrom DATETIME NOT NULL,
    ValidTo DATETIME NOT NULL,
    CreatedDate DATETIME NOT NULL,
    
    INDEX (DosageVersionPID, ModifiedDate),
    INDEX (releaseDate, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE DrugDosageStructureRelation (
    DrugDosageStructureRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    
    id VARCHAR(200) NOT NULL,
    
    drugId BIGINT(11) NOT NULL,
    dosageStructureCode BIGINT(11) NOT NULL,
    releaseNumber BIGINT(15) NOT NULL,
    
    ModifiedDate DATETIME NOT NULL,
    ValidFrom DATETIME NOT NULL,
    ValidTo DATETIME NOT NULL,
    CreatedDate DATETIME NOT NULL,
    
    INDEX (DrugDosageStructureRelationPID, ModifiedDate),
    INDEX (id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE DosageDrug (
    DosageDrugPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    
    releaseNumber BIGINT(15) NOT NULL,
    
    drugId BIGINT(11) NOT NULL,
    dosageUnitCode BIGINT(11) NOT NULL,
    drugName VARCHAR(200) NOT NULL,
    
    ModifiedDate DATETIME NOT NULL,
    ValidFrom DATETIME NOT NULL,
    ValidTo DATETIME NOT NULL,
    CreatedDate DATETIME NOT NULL,
    
    INDEX (DosageDrugPID, ModifiedDate),
    INDEX (drugId, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;
